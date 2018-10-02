package com.sfl.pms.services.payment.common.impl;

import com.sfl.pms.persistence.repositories.payment.common.AbstractPaymentRepository;
import com.sfl.pms.persistence.repositories.payment.common.PaymentResultRepository;
import com.sfl.pms.services.common.exception.ServicesRuntimeException;
import com.sfl.pms.services.payment.common.AbstractPaymentService;
import com.sfl.pms.services.payment.common.dto.PaymentDto;
import com.sfl.pms.services.payment.common.dto.PaymentResultDto;
import com.sfl.pms.services.payment.common.exception.*;
import com.sfl.pms.services.payment.common.impl.channel.*;
import com.sfl.pms.services.payment.common.impl.provider.PaymentResultHandler;
import com.sfl.pms.services.payment.common.model.Payment;
import com.sfl.pms.services.payment.common.model.PaymentResult;
import com.sfl.pms.services.payment.common.model.channel.PaymentProcessingChannelType;
import com.sfl.pms.services.payment.notification.PaymentProviderNotificationService;
import com.sfl.pms.services.payment.notification.model.PaymentProviderNotification;
import com.sfl.pms.services.payment.provider.exception.UnknownPaymentProviderTypeException;
import com.sfl.pms.services.payment.provider.model.PaymentProviderType;
import com.sfl.pms.services.payment.redirect.PaymentProviderRedirectResultService;
import com.sfl.pms.services.payment.redirect.model.PaymentProviderRedirectResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Nonnull;
import java.util.Date;

/**
 * User: Ruben Dilanyan
 * Company: SFL LLC
 * Date: 1/11/15
 * Time: 12:05 PM
 */
public abstract class AbstractPaymentServiceImpl<T extends Payment> implements AbstractPaymentService<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractPaymentServiceImpl.class);

    /* Dependencies */
    @Autowired
    @Qualifier("adyenPaymentResultHandler")
    private PaymentResultHandler adyenPaymentResultHandler;

    @Autowired
    @Qualifier("acapturePaymentResultHandler")
    private PaymentResultHandler acapturePaymentResultHandler;

    @Autowired
    private PaymentResultRepository paymentResultRepository;

    @Autowired
    private PaymentProviderNotificationService paymentProviderNotificationService;

    @Autowired
    private PaymentProviderRedirectResultService paymentProviderRedirectResultService;

    @Autowired
    private CustomerPaymentMethodProcessingChannelHandler customerPaymentMethodProcessingChannelHandler;

    @Autowired
    private DeferredPaymentMethodProcessingChannelHandler deferredPaymentMethodProcessingChannelHandler;

    @Autowired
    private EncryptedPaymentMethodProcessingChannelHandler encryptedPaymentMethodProcessingChannelHandler;

    @Autowired
    private ProvidedPaymentMethodProcessingChannelHandler providedPaymentMethodProcessingChannelHandler;

    /* Constructors */
    public AbstractPaymentServiceImpl() {
        LOGGER.debug("Initializing abstract payment service");
    }


    @Nonnull
    @Override
    public T getPaymentById(@Nonnull final Long paymentId) {
        assertPaymentIdNotNull(paymentId);
        LOGGER.debug("Loading payment for id - {}, payment class - {}", paymentId, getInstanceClass());
        final T payment = getRepository().findOne(paymentId);
        assertPaymentNotNullForId(payment, paymentId);
        LOGGER.debug("Successfully retrieved payment for id - {}, payment class - {}", paymentId, getInstanceClass());
        return payment;
    }

    @Nonnull
    @Override
    public T getPaymentByUuId(@Nonnull final String paymentUuId) {
        Assert.notNull(paymentUuId, "Payment UUID should not be null");
        LOGGER.debug("Loading payment for UUID - {}, payment class - {}", paymentUuId, getInstanceClass());
        final T payment = getRepository().findByUuId(paymentUuId);
        assertPaymentNotNullForUuId(payment, paymentUuId);
        LOGGER.debug("Successfully retrieved payment for UUID - {}, payment class - {}, payment - {}", paymentUuId, getInstanceClass(), payment);
        return payment;
    }

    @Transactional
    @Nonnull
    @Override
    public PaymentResult createPaymentResultForPayment(@Nonnull final Long paymentId, final Long paymentProviderNotificationId, final Long paymentProviderRedirectResultId, @Nonnull final PaymentResultDto<? extends PaymentResult> paymentResultDto) {
        assertPaymentIdNotNull(paymentId);
        assertPaymentResultDto(paymentResultDto);
        LOGGER.debug("Updating payment result for payment with id - {}, payment result DTO - {}", paymentId, paymentResultDto);
        T payment = getRepository().findOne(paymentId);
        assertPaymentNotNullForId(payment, paymentId);
        // Load and assert payment provider notification
        final PaymentProviderNotification notification = loadAndAssertPaymentProviderNotification(paymentProviderNotificationId, paymentId);
        // Load and assert payment provider redirect result
        final PaymentProviderRedirectResult redirectResult = loadAndAssertPaymentProviderRedirectResult(paymentProviderRedirectResultId, paymentId);
        // Convert to DTO to payment result entity
        final PaymentResult paymentResult = getPaymentResultHandler(paymentResultDto.getType()).convertPaymentResultDto(paymentResultDto);
        paymentResult.setPayment(payment);
        paymentResult.setNotification(notification);
        paymentResult.setRedirectResult(redirectResult);
        payment.getPaymentResults().add(paymentResult);
        payment.setUpdated(new Date());
        // Persist payment
        payment = getRepository().save(payment);
        LOGGER.debug("Successfully update payment result for payment with id - {}. Payment result - {}", payment.getId(), paymentResult);
        return paymentResult;
    }

    /* Utility methods */
    private PaymentProviderNotification loadAndAssertPaymentProviderNotification(final Long notificationId, final Long paymentId) {
        final PaymentProviderNotification notification;
        if (notificationId != null) {
            notification = paymentProviderNotificationService.getPaymentProviderNotificationById(notificationId);
            final Payment notificationPayment = notification.getPayment();
            if (notificationPayment == null) {
                LOGGER.error("Notification with id - {} must be associated with payment", notificationId);
                throw new InvalidNotificationForPaymentException(notificationId, paymentId);
            }
            if (!paymentId.equals(notificationPayment.getId())) {
                LOGGER.error("Notification with id - {} must be associated with payment id - {}", notificationId, paymentId);
                throw new InvalidNotificationForPaymentException(notificationId, paymentId);
            }
            // Check that no payment result exists for notification
            final PaymentResult existingPaymentResult = paymentResultRepository.findByNotification(notification);
            if (existingPaymentResult != null) {
                LOGGER.error("Payment result with id - {} already exists for notification with id - {}", existingPaymentResult.getId(), notification.getId());
                throw new PaymentResultAlreadyExistsForNotificationException(notificationId, existingPaymentResult.getId());
            }
        } else {
            notification = null;
        }
        return notification;
    }

    private PaymentProviderRedirectResult loadAndAssertPaymentProviderRedirectResult(final Long redirectResultId, final Long paymentId) {
        final PaymentProviderRedirectResult redirectResult;
        if (redirectResultId != null) {
            redirectResult = paymentProviderRedirectResultService.getPaymentProviderRedirectResultById(redirectResultId);
            final Payment redirectResultPayment = redirectResult.getPayment();
            if (redirectResultPayment == null) {
                LOGGER.error("Redirect result with id - {} must be associated with payment", redirectResultId);
                throw new InvalidRedirectResultForPaymentException(redirectResultId, paymentId);
            }
            if (!paymentId.equals(redirectResultPayment.getId())) {
                LOGGER.error("Redirect result with id - {} must be associated with payment id - {}", redirectResultId, paymentId);
                throw new InvalidRedirectResultForPaymentException(redirectResultId, paymentId);
            }
            // Check that no payment result exists for notification
            final PaymentResult existingPaymentResult = paymentResultRepository.findByRedirectResult(redirectResult);
            if (existingPaymentResult != null) {
                LOGGER.error("Payment result with id - {} already exists for redirect result with id - {}", existingPaymentResult.getId(), redirectResult.getId());
                throw new PaymentResultAlreadyExistsForRedirectResultException(redirectResultId, existingPaymentResult.getId());
            }
        } else {
            redirectResult = null;
        }
        return redirectResult;
    }

    private PaymentResultHandler getPaymentResultHandler(final PaymentProviderType paymentProviderType) {
        switch (paymentProviderType) {
            case ADYEN: {
                return adyenPaymentResultHandler;
            }
            case ACAPTURE:
                return acapturePaymentResultHandler;
            default: {
                LOGGER.error("Unknown payment provider type - {}", paymentProviderType);
                throw new UnknownPaymentProviderTypeException(paymentProviderType);
            }
        }
    }

    private void assertPaymentResultDto(final PaymentResultDto<? extends PaymentResult> paymentResultDto) {
        Assert.notNull(paymentResultDto, "Payment result DTO should not be null");
        getPaymentResultHandler(paymentResultDto.getType()).assertPaymentResultDto(paymentResultDto);
    }

    protected void assertPaymentNotNullForId(final T payment, final Long paymentId) {
        if (payment == null) {
            LOGGER.error("No payment was found for id - {}, payment class - {}", paymentId, getInstanceClass());
            throw new PaymentNotFoundForIdException(paymentId, getInstanceClass());
        }
    }

    protected void assertPaymentNotNullForUuId(final T payment, final String paymentUuId) {
        if (payment == null) {
            LOGGER.error("No payment was found for uuId - {}, payment class - {}", paymentUuId, getInstanceClass());
            throw new PaymentNotFoundForUuIdException(paymentUuId, getInstanceClass());
        }
    }

    protected void assertPaymentIdNotNull(final Long paymentId) {
        Assert.notNull(paymentId, "Payment id should not be null");
    }

    protected PaymentProcessingChannelHandler getPaymentProcessingChannelHandler(final PaymentProcessingChannelType paymentProcessingChannelType) {
        switch (paymentProcessingChannelType) {
            case CUSTOMER_PAYMENT_METHOD:
                return customerPaymentMethodProcessingChannelHandler;
            case DEFERRED_PAYMENT_METHOD:
                return deferredPaymentMethodProcessingChannelHandler;
            case ENCRYPTED_PAYMENT_METHOD:
                return encryptedPaymentMethodProcessingChannelHandler;
            case PROVIDED_PAYMENT_METHOD:
                return providedPaymentMethodProcessingChannelHandler;
            default:
                throw new ServicesRuntimeException("Unknown payment processing channel type - " + paymentProcessingChannelType);
        }
    }

    protected void assertPaymentDto(final PaymentDto<? extends Payment> paymentDto) {
        Assert.notNull(paymentDto, "Payment DTO should not be null");
        Assert.notNull(paymentDto.getAmount(), "Amount in payment DTO should not be null");
        Assert.notNull(paymentDto.getPaymentMethodSurcharge(), "Payment method surcharge in order payment DTO should not be null");
        Assert.notNull(paymentDto.getCurrency(), "Currency in payment DTO should not be null");
        Assert.notNull(paymentDto.getPaymentProviderType(), "Payment provider type in payment DTO should not be null");
    }

    /* Abstract methods */
    protected abstract AbstractPaymentRepository<T> getRepository();

    protected abstract Class<T> getInstanceClass();

    /* Properties getters and setters */
    public PaymentResultHandler getAdyenPaymentResultHandler() {
        return adyenPaymentResultHandler;
    }

    public void setAdyenPaymentResultHandler(final PaymentResultHandler adyenPaymentResultHandler) {
        this.adyenPaymentResultHandler = adyenPaymentResultHandler;
    }

    public PaymentResultRepository getPaymentResultRepository() {
        return paymentResultRepository;
    }

    public void setPaymentResultRepository(final PaymentResultRepository paymentResultRepository) {
        this.paymentResultRepository = paymentResultRepository;
    }

    public PaymentProviderNotificationService getPaymentProviderNotificationService() {
        return paymentProviderNotificationService;
    }

    public void setPaymentProviderNotificationService(final PaymentProviderNotificationService paymentProviderNotificationService) {
        this.paymentProviderNotificationService = paymentProviderNotificationService;
    }

    public CustomerPaymentMethodProcessingChannelHandler getCustomerPaymentMethodProcessingChannelHandler() {
        return customerPaymentMethodProcessingChannelHandler;
    }

    public void setCustomerPaymentMethodProcessingChannelHandler(CustomerPaymentMethodProcessingChannelHandler customerPaymentMethodProcessingChannelHandler) {
        this.customerPaymentMethodProcessingChannelHandler = customerPaymentMethodProcessingChannelHandler;
    }

    public DeferredPaymentMethodProcessingChannelHandler getDeferredPaymentMethodProcessingChannelHandler() {
        return deferredPaymentMethodProcessingChannelHandler;
    }

    public void setDeferredPaymentMethodProcessingChannelHandler(DeferredPaymentMethodProcessingChannelHandler deferredPaymentMethodProcessingChannelHandler) {
        this.deferredPaymentMethodProcessingChannelHandler = deferredPaymentMethodProcessingChannelHandler;
    }

    public EncryptedPaymentMethodProcessingChannelHandler getEncryptedPaymentMethodProcessingChannelHandler() {
        return encryptedPaymentMethodProcessingChannelHandler;
    }

    public void setEncryptedPaymentMethodProcessingChannelHandler(EncryptedPaymentMethodProcessingChannelHandler encryptedPaymentMethodProcessingChannelHandler) {
        this.encryptedPaymentMethodProcessingChannelHandler = encryptedPaymentMethodProcessingChannelHandler;
    }

    public ProvidedPaymentMethodProcessingChannelHandler getProvidedPaymentMethodProcessingChannelHandler() {
        return providedPaymentMethodProcessingChannelHandler;
    }

    public void setProvidedPaymentMethodProcessingChannelHandler(ProvidedPaymentMethodProcessingChannelHandler providedPaymentMethodProcessingChannelHandler) {
        this.providedPaymentMethodProcessingChannelHandler = providedPaymentMethodProcessingChannelHandler;
    }

    public PaymentProviderRedirectResultService getPaymentProviderRedirectResultService() {
        return paymentProviderRedirectResultService;
    }

    public void setPaymentProviderRedirectResultService(final PaymentProviderRedirectResultService paymentProviderRedirectResultService) {
        this.paymentProviderRedirectResultService = paymentProviderRedirectResultService;
    }
}
