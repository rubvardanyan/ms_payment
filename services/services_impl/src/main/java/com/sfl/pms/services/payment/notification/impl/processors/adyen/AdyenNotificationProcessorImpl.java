package com.sfl.pms.services.payment.notification.impl.processors.adyen;

import com.sfl.pms.externalclients.payment.adyen.model.datatypes.AdyenPaymentNotificationEventType;
import com.sfl.pms.externalclients.payment.adyen.model.datatypes.AdyenPaymentStatus;
import com.sfl.pms.services.currency.model.Currency;
import com.sfl.pms.services.encryption.EncryptionUtilityService;
import com.sfl.pms.services.payment.common.PaymentService;
import com.sfl.pms.services.payment.common.dto.adyen.AdyenPaymentResultDto;
import com.sfl.pms.services.payment.common.exception.PaymentNotFoundForUuIdException;
import com.sfl.pms.services.payment.common.model.Payment;
import com.sfl.pms.services.payment.common.model.PaymentResultStatus;
import com.sfl.pms.services.payment.method.model.PaymentMethodType;
import com.sfl.pms.services.payment.method.model.adyen.AdyenPaymentMethodType;
import com.sfl.pms.services.payment.notification.adyen.AdyenPaymentProviderNotificationService;
import com.sfl.pms.services.payment.notification.dto.adyen.AdyenPaymentProviderNotificationDto;
import com.sfl.pms.services.payment.notification.impl.processors.adyen.json.AdyenNotificationJsonDeserializer;
import com.sfl.pms.services.payment.notification.impl.processors.adyen.json.model.*;
import com.sfl.pms.services.payment.notification.model.PaymentProviderNotification;
import com.sfl.pms.services.payment.notification.model.PaymentProviderNotificationRequest;
import com.sfl.pms.services.payment.notification.model.adyen.AdyenPaymentProviderNotification;
import com.sfl.pms.services.payment.processing.impl.PaymentResultProcessor;
import com.sfl.pms.services.payment.provider.model.PaymentProviderType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.Nonnull;
import java.math.BigDecimal;
import java.util.*;

/**
 * User: Ruben Dilanyan
 * Company: SFL LLC
 * Date: 4/28/15
 * Time: 11:27 AM
 */
@Component("adyenNotificationProcessor")
public class AdyenNotificationProcessorImpl implements AdyenNotificationProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdyenNotificationProcessorImpl.class);

    private static final Set<AdyenPaymentNotificationEventType> NOTIFICATION_TYPES_REQUIRING_POST_PROCESSING;

    static {
        final Set<AdyenPaymentNotificationEventType> tempNotificationTypes = new HashSet<>();
        tempNotificationTypes.add(AdyenPaymentNotificationEventType.AUTHORIZATION);
        // Publish set
        NOTIFICATION_TYPES_REQUIRING_POST_PROCESSING = Collections.unmodifiableSet(tempNotificationTypes);
    }

    /* Dependencies */
    @Autowired
    private AdyenPaymentProviderNotificationService adyenPaymentProviderNotificationService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private AdyenNotificationJsonDeserializer adyenNotificationJsonDeserializer;

    @Autowired
    private EncryptionUtilityService encryptionUtilityService;

    @Autowired
    private PaymentResultProcessor paymentResultProcessor;

    /* Constructors */
    public AdyenNotificationProcessorImpl() {
        LOGGER.debug("Initializing Adyen notification processor");
    }


    @Nonnull
    @Override
    public List<PaymentProviderNotification> createPaymentProviderNotificationForRequest(@Nonnull final PaymentProviderNotificationRequest notificationRequest) {
        Assert.notNull(notificationRequest, "Notification request should not be null");
        // Create notification DTO
        final List<PaymentProviderNotification> notifications = new ArrayList<>();
        final AdyenNotificationJsonModel notificationJsonModel = adyenNotificationJsonDeserializer.deserializeAdyenNotification(notificationRequest.getRawContent());
        // Go through extracted notification items
        for (final AdyenNotificationArrayItemJsonModel arrayItemJsonModel : notificationJsonModel.getNotificationItems()) {
            // Create notification
            final AdyenNotificationRequestItemJsonModel itemJsonModel = arrayItemJsonModel.getNotificationRequestItem();
            final AdyenPaymentProviderNotification notification = createAdyenPaymentProviderNotification(notificationRequest, notificationJsonModel, itemJsonModel);
            LOGGER.debug("Created new notification for request - {}, notification item JSON model - {}, notification - {}", notificationRequest, itemJsonModel, notification);
            notifications.add(notification);
        }
        LOGGER.debug("Successfully created Adyen payment provider notification - {}", notifications);
        return notifications;
    }

    @Override
    public void processPaymentProviderNotification(@Nonnull final PaymentProviderNotification notification) {
        Assert.notNull(notification, "Notification should not be null");
        Assert.isTrue(PaymentProviderType.ADYEN.equals(notification.getType()), "Notification should have payment provider type ADYEN");
        AdyenPaymentProviderNotification adyenNotification = (AdyenPaymentProviderNotification) notification;
        // Grab merchant reference and check if it exists
        final Payment payment = lookupPaymentForAdyenNotification(adyenNotification);
        // Check if payment was found, if yes associate payment with notification
        if (payment != null) {
            LOGGER.debug("Payment lookup result for notification with id - {} is - {}", notification.getId(), payment.getId());
            adyenNotification = adyenPaymentProviderNotificationService.updatePaymentForNotification(notification.getId(), payment.getId());
            LOGGER.debug("Successfully set payment - {} to notification  - {}", payment, adyenNotification);
            // Update confirmed payment method on payment
            if (adyenNotification.getPaymentMethodType() != null && payment.getConfirmedPaymentMethodType() == null) {
                final PaymentMethodType paymentMethodType = PaymentMethodType.getPaymentMethodTypeForAdyenPaymentMethod(adyenNotification.getPaymentMethodType());
                paymentService.updateConfirmedPaymentMethodType(payment.getId(), paymentMethodType);
            }
            final AdyenPaymentNotificationEventType eventType = AdyenPaymentNotificationEventType.getEventTypeForCode(adyenNotification.getEventCode());
            if (eventType != null) {
                if (NOTIFICATION_TYPES_REQUIRING_POST_PROCESSING.contains(eventType)) {
                    LOGGER.debug("Post processing notification with id - {}, its event type - {}", notification.getId(), eventType);
                    final AdyenPaymentResultDto paymentResultDto = createPaymentResultDtoForNotification(adyenNotification, eventType);
                    LOGGER.debug("Created following payment result DTO - {} for Adyen notification - {}", paymentResultDto, adyenNotification);
                    paymentResultProcessor.processPaymentResult(payment.getId(), notification.getId(), null, paymentResultDto);
                } else {
                    LOGGER.debug("Do not post process notification with id - {}, its event type - {}", notification.getId(), eventType);
                }
            } else {
                LOGGER.error("Unrecognized Adyen notification event type - {}, storing notification and skipping processing. Notification id - {}", adyenNotification.getEventCode(), adyenNotification.getId());
            }
        }
    }

    /* Utility methods */
    private AdyenPaymentResultDto createPaymentResultDtoForNotification(final AdyenPaymentProviderNotification notification, final AdyenPaymentNotificationEventType eventType) {
        // Create result DTO
        final AdyenPaymentResultDto paymentResultDto = new AdyenPaymentResultDto();
        paymentResultDto.setAuthCode(notification.getAuthCode());
        paymentResultDto.setPspReference(notification.getPspReference());
        if (AdyenPaymentNotificationEventType.AUTHORIZATION.equals(eventType) && notification.getSuccess() != null && notification.getSuccess().booleanValue()) {
            paymentResultDto.setResultCode(AdyenPaymentStatus.AUTHORISED.getResult());
            paymentResultDto.setStatus(PaymentResultStatus.PAID);
        } else {
            paymentResultDto.setResultCode(AdyenPaymentStatus.REFUSED.getResult());
            paymentResultDto.setStatus(PaymentResultStatus.REFUSED);
        }
        return paymentResultDto;
    }

    private Payment lookupPaymentForAdyenNotification(final AdyenPaymentProviderNotification adyenNotification) {
        // Grab merchant reference and check if it exists
        final String merchantReference = adyenNotification.getMerchantReference();
        final Payment payment;
        if (StringUtils.isNoneBlank(merchantReference)) {
            payment = lookupPaymentForMerchantReference(merchantReference);
        } else {
            payment = null;
        }
        return payment;
    }

    private Payment lookupPaymentForMerchantReference(final String merchantReference) {
        try {
            final Payment payment = paymentService.getPaymentByUuId(merchantReference);
            LOGGER.debug("Successfully performed lookup of payment for merchant reference - {}, payment - {}", merchantReference, payment);
            return payment;
        } catch (final PaymentNotFoundForUuIdException ex) {
            // Only log
            LOGGER.error("Was not able to find payment for merchant reference - {}", merchantReference, ex);
        }
        return null;
    }

    private AdyenPaymentProviderNotification createAdyenPaymentProviderNotification(@Nonnull final PaymentProviderNotificationRequest notificationRequest, final AdyenNotificationJsonModel notificationJsonModel, final AdyenNotificationRequestItemJsonModel requestItemJsonModel) {
        // Create notification DTO
        final AdyenPaymentProviderNotificationDto notificationDto = createAdyenPaymentProviderNotificationDto(notificationRequest, requestItemJsonModel);
        // Create notification
        final AdyenPaymentProviderNotification notification = adyenPaymentProviderNotificationService.createPaymentProviderNotification(notificationRequest.getId(), notificationDto);
        LOGGER.debug("Created Adyen payment provider notification, request  - {}, notificationJsonModel - {}, requestItemJsonModel - {}", notificationRequest, notificationJsonModel, requestItemJsonModel);
        return notification;
    }

    private AdyenPaymentProviderNotificationDto createAdyenPaymentProviderNotificationDto(final PaymentProviderNotificationRequest notificationRequest, final AdyenNotificationRequestItemJsonModel itemJsonModel) {
        // Create notification DTO
        final AdyenPaymentProviderNotificationDto notificationDto = new AdyenPaymentProviderNotificationDto();
        // Set properties
        notificationDto.setRawContent(notificationRequest.getRawContent());
        notificationDto.setClientIpAddress(notificationRequest.getClientIpAddress());
        // Set JSON properties
        if (itemJsonModel != null) {
            notificationDto.setPspReference(itemJsonModel.getPspReference());
            if (itemJsonModel.getPaymentMethod() != null) {
                notificationDto.setPaymentMethodType(AdyenPaymentMethodType.getAdyenPaymentMethodTypeForCode(itemJsonModel.getPaymentMethod()));
            }
            notificationDto.setMerchantReference(itemJsonModel.getMerchantReference());
            notificationDto.setEventCode(itemJsonModel.getEventCode());
            if (itemJsonModel.getAmount() != null) {
                final AdyenNotificationAmountJsonModel amountJsonModel = itemJsonModel.getAmount();
                notificationDto.setCurrency(Currency.valueOf(amountJsonModel.getCurrency()));
                notificationDto.setAmount(BigDecimal.valueOf(amountJsonModel.getValue()));
            }
            notificationDto.setSuccess(itemJsonModel.getSuccess());
            if (itemJsonModel.getAdditionalData() != null) {
                final AdyenNotificationAdditionalDataJsonModel additionalDataJsonModel = itemJsonModel.getAdditionalData();
                notificationDto.setAuthCode(additionalDataJsonModel.getAuthCode());
            }
        }
        return notificationDto;
    }

    /* Properties getters and setters */
    public AdyenPaymentProviderNotificationService getAdyenPaymentProviderNotificationService() {
        return adyenPaymentProviderNotificationService;
    }

    public void setAdyenPaymentProviderNotificationService(final AdyenPaymentProviderNotificationService adyenPaymentProviderNotificationService) {
        this.adyenPaymentProviderNotificationService = adyenPaymentProviderNotificationService;
    }

    public AdyenNotificationJsonDeserializer getAdyenNotificationJsonDeserializer() {
        return adyenNotificationJsonDeserializer;
    }

    public void setAdyenNotificationJsonDeserializer(final AdyenNotificationJsonDeserializer adyenNotificationJsonDeserializer) {
        this.adyenNotificationJsonDeserializer = adyenNotificationJsonDeserializer;
    }

    public EncryptionUtilityService getEncryptionUtilityService() {
        return encryptionUtilityService;
    }

    public void setEncryptionUtilityService(final EncryptionUtilityService encryptionUtilityService) {
        this.encryptionUtilityService = encryptionUtilityService;
    }

    public PaymentService getPaymentService() {
        return paymentService;
    }

    public void setPaymentService(final PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public PaymentResultProcessor getPaymentResultProcessor() {
        return paymentResultProcessor;
    }

    public void setPaymentResultProcessor(final PaymentResultProcessor paymentResultProcessor) {
        this.paymentResultProcessor = paymentResultProcessor;
    }
}
