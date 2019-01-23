package com.sfl.pms.services.payment.notification.impl.processors.acapture;

import com.sfl.pms.services.payment.common.dto.acapture.AcapturePaymentResultDto;
import com.sfl.pms.services.payment.common.impl.status.PaymentResultStatusMapper;
import com.sfl.pms.services.payment.common.model.Payment;
import com.sfl.pms.services.payment.common.model.PaymentResultStatus;
import com.sfl.pms.services.payment.common.model.acapture.AcapturePaymentProviderMetadata;
import com.sfl.pms.services.payment.metadata.acapture.AcapturePaymentProviderMetadataService;
import com.sfl.pms.services.payment.notification.acapture.AcapturePaymentProviderNotificationService;
import com.sfl.pms.services.payment.notification.dto.acapture.AcapturePaymentProviderNotificationDto;
import com.sfl.pms.services.payment.notification.impl.processors.acapture.json.AcaptureNotificationJsonDeserializer;
import com.sfl.pms.services.payment.notification.impl.processors.acapture.json.model.AcapturePaymentNotificationPayloadJsonModel;
import com.sfl.pms.services.payment.notification.model.PaymentProviderNotification;
import com.sfl.pms.services.payment.notification.model.PaymentProviderNotificationRequest;
import com.sfl.pms.services.payment.notification.model.acapture.AcapturePaymentProviderNotification;
import com.sfl.pms.services.payment.processing.impl.PaymentResultProcessor;
import com.sfl.pms.services.payment.provider.model.PaymentProviderType;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.Nonnull;
import javax.management.remote.NotificationResult;
import javax.persistence.EntityNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 1/23/19
 * Time: 7:47 PM
 */
@Component("acaptureNotificationProcessor")
public class AcaptureNotificationProcessorImpl implements AcaptureNotificationProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(AcaptureNotificationProcessorImpl.class);

    /* Dependencies */
    @Autowired
    private AcaptureNotificationJsonDeserializer acaptureNotificationJsonDeserializer;

    @Autowired
    private AcapturePaymentProviderNotificationService acapturePaymentProviderNotificationService;

    @Autowired
    private AcapturePaymentProviderMetadataService acapturePaymentProviderMetadataService;

    @Autowired
    private PaymentResultStatusMapper paymentResultStatusMapper;

    @Autowired
    private PaymentResultProcessor paymentResultProcessor;

    /* Constructor */
    public AcaptureNotificationProcessorImpl() {
        LOGGER.debug("Initializing acapture notification processor");
    }

    /* Public methods */
    @Nonnull
    @Override
    public List<PaymentProviderNotification> createPaymentProviderNotificationForRequest(@Nonnull final PaymentProviderNotificationRequest notificationRequest) {
        Assert.notNull(notificationRequest);
        final String rawContent = notificationRequest.getRawContent();
        final AcapturePaymentNotificationPayloadJsonModel notificationPayload = acaptureNotificationJsonDeserializer.deserializePaymentNotificationPayload(rawContent);
        final AcapturePaymentProviderNotificationDto notificationDto = new AcapturePaymentProviderNotificationDto(
                rawContent,
                notificationRequest.getClientIpAddress(),
                notificationPayload.getId(),
                notificationPayload.getResult().getCode(),
                notificationPayload.getResult().getDescription(),
                notificationPayload.getNdc()
        );
        final AcapturePaymentProviderNotification notification = acapturePaymentProviderNotificationService.createPaymentProviderNotification(notificationRequest.getId(), notificationDto);
        LOGGER.debug("Created Acapture payment provider notification, request  - {}, notificationJsonModel - {}, requestContent - {}", notificationRequest, notificationPayload);
        final List<PaymentProviderNotification> notifications = new ArrayList<>();
        notifications.add(notification);
        return notifications;
    }

    @Override
    public void processPaymentProviderNotification(@Nonnull final PaymentProviderNotification notification) {
        Assert.notNull(notification);
        Assert.isTrue(PaymentProviderType.ACAPTURE.equals(notification.getType()), "Notification payment provider should be ACAPTURE");
        Assert.isInstanceOf(AcapturePaymentProviderNotification.class, notification, "Notification type should be AcapturePaymentProviderNotification");
        final AcapturePaymentProviderNotification acaptureNotification = (AcapturePaymentProviderNotification) notification;
        final Payment payment = lookupPaymentForAcaptureNotification(acaptureNotification);
        if(payment == null) {
            return;
        }
        LOGGER.debug("Payment lookup result for notification with id - {} is - {}", acaptureNotification.getId(), payment.getId());
        acapturePaymentProviderNotificationService.updatePaymentForNotification(acaptureNotification.getId(), payment.getId());
        LOGGER.debug("Successfully set payment - {} to notification  - {}", payment, acaptureNotification);
        final PaymentResultStatus paymentStatus = paymentResultStatusMapper.getPaymentResultStatusForAcapturePaymentStatus(acaptureNotification.getResultCode());
        final AcapturePaymentResultDto paymentResultDto = new AcapturePaymentResultDto(
                acaptureNotification.getResultCode(),
                acaptureNotification.getResultDescription(),
                acaptureNotification.getNotificationId()
        );
        paymentResultDto.setStatus(paymentStatus);
        paymentResultProcessor.processPaymentResult(payment.getId(), notification.getId(), null, paymentResultDto);
    }

    /* Utility methods */
    private Payment lookupPaymentForAcaptureNotification(final AcapturePaymentProviderNotification acapturePaymentProviderNotification) {
        final String checkoutId = acapturePaymentProviderNotification.getNdc();
        if(StringUtils.isBlank(checkoutId)) {
            return null;
        }
        try {
            final AcapturePaymentProviderMetadata metadata = acapturePaymentProviderMetadataService.getAcapturePaymentProviderMetadataByCheckoutId(checkoutId);
            return metadata.getPaymentProcessingChannel().getPayment();
        } catch (EntityNotFoundException ex) {
            LOGGER.error("Was not able to find payment for checkout id - {}", checkoutId);
        }
        return null;
    }
}
