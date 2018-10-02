package com.sfl.pms.services.payment.processing.impl;

import com.sfl.pms.persistence.utility.PersistenceUtilityService;
import com.sfl.pms.services.payment.common.PaymentService;
import com.sfl.pms.services.payment.common.dto.PaymentResultDto;
import com.sfl.pms.services.payment.common.dto.PaymentStateChangeHistoryRecordDto;
import com.sfl.pms.services.payment.common.exception.channel.UnknownPaymentProcessingChannelTypeException;
import com.sfl.pms.services.payment.common.model.Payment;
import com.sfl.pms.services.payment.common.model.PaymentResult;
import com.sfl.pms.services.payment.common.model.PaymentState;
import com.sfl.pms.services.payment.common.model.PaymentType;
import com.sfl.pms.services.payment.common.model.channel.PaymentProcessingChannel;
import com.sfl.pms.services.payment.customer.method.CustomerPaymentMethodsSynchronizationService;
import com.sfl.pms.services.payment.customer.method.dto.CustomerPaymentMethodsSynchronizationResult;
import com.sfl.pms.services.payment.customer.method.model.CustomerPaymentMethod;
import com.sfl.pms.services.payment.provider.exception.UnknownPaymentProviderTypeException;
import com.sfl.pms.services.payment.provider.model.PaymentProviderIntegrationType;
import com.sfl.pms.services.payment.provider.model.PaymentProviderType;
import com.sfl.pms.services.system.concurrency.ScheduledTaskExecutorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.Assert;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * User: Ruben Dilanyan
 * Company: SFL LLC
 * Date: 5/4/15
 * Time: 10:41 AM
 */
public abstract class AbstractPaymentTypeSpecificOperationsProcessorImpl implements PaymentTypeSpecificOperationsProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractPaymentTypeSpecificOperationsProcessorImpl.class);

    /* Constants */
    public static final int PAYMENT_METHOD_SYNCHRONIZATION_WAIT_PERIOD_IN_MILLIS = 60000;

    public static final int PAYMENT_METHOD_SYNCHRONIZATION_MAX_ATTEMPTS = 10;

    /* Dependencies */
    @Autowired
    private PersistenceUtilityService persistenceUtilityService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    @Qualifier("adyenPaymentOperationsProcessor")
    private PaymentProviderOperationsProcessor adyenPaymentProviderOperationsProcessor;

    @Autowired
    @Qualifier("acapturePaymentOperationsProcessor")
    private PaymentProviderOperationsProcessor acapturePaymentProviderOperationsProcessor;

    @Autowired
    private CustomerPaymentMethodsSynchronizationService customerPaymentMethodsSynchronizationService;

    @Autowired
    private ScheduledTaskExecutorService scheduledTaskExecutorService;

    /* Constructors */
    public AbstractPaymentTypeSpecificOperationsProcessorImpl() {
        LOGGER.debug("Initializing abstract payment type specific operations processor");
    }

    @Nonnull
    @Override
    public PaymentResultDto<? extends PaymentResult> processPaymentApiTransaction(@Nonnull final Payment payment) {
        assertPayment(payment);
        LOGGER.debug("Processing payment transaction for payment - {}", payment);
        final PaymentProcessingChannel paymentProcessingChannel = payment.getPaymentProcessingChannel();
        Assert.isTrue(PaymentProviderIntegrationType.API.equals(paymentProcessingChannel.getPaymentProviderIntegrationType()));
        // Process transaction
        final PaymentProviderOperationsProcessor paymentProviderOperationsProcessor = getPaymentProviderProcessor(payment.getPaymentProviderType());
        final PaymentResultDto<? extends PaymentResult> paymentResultDto;
        switch (paymentProcessingChannel.getType()) {
            case ENCRYPTED_PAYMENT_METHOD: {
                paymentResultDto = paymentProviderOperationsProcessor.processPaymentUsingEncryptedPaymentMethodChannel(payment.getId());
                break;
            }
            case CUSTOMER_PAYMENT_METHOD: {
                paymentResultDto = paymentProviderOperationsProcessor.processPaymentUsingCustomerPaymentMethodChannel(payment.getId());
                break;
            }
            default: {
                LOGGER.error("Unsupported payment processing channel type - {}", paymentProcessingChannel.getType());
                throw new UnknownPaymentProcessingChannelTypeException(paymentProcessingChannel.getType());
            }

        }
        LOGGER.debug("Successfully retrieved payment result DTO - {} for payment method authorization payment - {}", paymentResultDto, payment);
        return paymentResultDto;
    }

    @Nonnull
    @Override
    public String generatePaymentRedirectUrl(@Nonnull Payment payment) {
        assertPayment(payment);
        LOGGER.debug("Processing payment URL generation for payment - {}", payment);
        final PaymentProcessingChannel paymentProcessingChannel = payment.getPaymentProcessingChannel();
        Assert.isTrue(PaymentProviderIntegrationType.REDIRECT.equals(paymentProcessingChannel.getPaymentProviderIntegrationType()));
        // Grab payment specific operations processor
        final PaymentProviderOperationsProcessor paymentProviderOperationsProcessor = getPaymentProviderProcessor(payment.getPaymentProviderType());
        final String paymentRedirectUrl = paymentProviderOperationsProcessor.generatePaymentRedirectUrl(payment.getId(), payment.isStorePaymentMethod());
        LOGGER.debug("Successfully generated redirect URL - {} for payment - {}", paymentRedirectUrl, payment);
        return paymentRedirectUrl;
    }

    /* Abstract methods */
    protected abstract PaymentType getPaymentType();

    /* Utility methods */
    private void assertPayment(final Payment payment) {
        Assert.notNull(payment, "Payment should not be null");
        Assert.isTrue(getPaymentType().equals(payment.getType()));
    }

    protected PaymentProviderOperationsProcessor getPaymentProviderProcessor(final PaymentProviderType paymentProviderType) {
        switch (paymentProviderType) {
            case ADYEN: {
                return adyenPaymentProviderOperationsProcessor;
            }
            case ACAPTURE:
                return acapturePaymentProviderOperationsProcessor;
            default: {
                LOGGER.error("Unknown payment provider type - {}", paymentProviderType);
                throw new UnknownPaymentProviderTypeException(paymentProviderType);
            }
        }
    }

    protected void schedulePaymentMethodsSynchronizationTask(final Long customerId, final PaymentProviderType paymentProviderType) {
        // Schedule task to retry synchronization after while
        final CustomerPaymentMethodsSynchronizationTask synchronizationTask = new CustomerPaymentMethodsSynchronizationTask(customerId, paymentProviderType, PAYMENT_METHOD_SYNCHRONIZATION_MAX_ATTEMPTS);
        // Schedule new task
        getScheduledTaskExecutorService().scheduleTaskForExecution(synchronizationTask, PAYMENT_METHOD_SYNCHRONIZATION_WAIT_PERIOD_IN_MILLIS, TimeUnit.MILLISECONDS, true);
    }

    protected void updatePaymentState(final Long paymentId, final PaymentState paymentState, final String information) {
        paymentService.updatePaymentState(paymentId, new PaymentStateChangeHistoryRecordDto(paymentState, information));
    }

    /* Dependencies getters and setters */
    public PaymentProviderOperationsProcessor getAdyenPaymentProviderOperationsProcessor() {
        return adyenPaymentProviderOperationsProcessor;
    }

    public void setAdyenPaymentProviderOperationsProcessor(final PaymentProviderOperationsProcessor adyenPaymentProviderOperationsProcessor) {
        this.adyenPaymentProviderOperationsProcessor = adyenPaymentProviderOperationsProcessor;
    }

    public PersistenceUtilityService getPersistenceUtilityService() {
        return persistenceUtilityService;
    }

    public void setPersistenceUtilityService(final PersistenceUtilityService persistenceUtilityService) {
        this.persistenceUtilityService = persistenceUtilityService;
    }

    public PaymentService getPaymentService() {
        return paymentService;
    }

    public void setPaymentService(final PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public CustomerPaymentMethodsSynchronizationService getCustomerPaymentMethodsSynchronizationService() {
        return customerPaymentMethodsSynchronizationService;
    }

    public void setCustomerPaymentMethodsSynchronizationService(final CustomerPaymentMethodsSynchronizationService customerPaymentMethodsSynchronizationService) {
        this.customerPaymentMethodsSynchronizationService = customerPaymentMethodsSynchronizationService;
    }

    public ScheduledTaskExecutorService getScheduledTaskExecutorService() {
        return scheduledTaskExecutorService;
    }

    public void setScheduledTaskExecutorService(final ScheduledTaskExecutorService scheduledTaskExecutorService) {
        this.scheduledTaskExecutorService = scheduledTaskExecutorService;
    }

    /* Inner classes */
    /* Customer payment methods synchronization task */
    protected class CustomerPaymentMethodsSynchronizationTask implements Runnable {

        /* Properties */
        private final int remainingAttemptsCount;

        private final Long customerId;

        private final PaymentProviderType paymentProviderType;

        /* Constructors */
        public CustomerPaymentMethodsSynchronizationTask(final Long customerId, final PaymentProviderType paymentProviderType, final int remainingAttemptsCount) {
            this.customerId = customerId;
            this.remainingAttemptsCount = remainingAttemptsCount;
            this.paymentProviderType = paymentProviderType;
        }

        @Override
        public void run() {
            List<CustomerPaymentMethod> customerPaymentMethods = new ArrayList<>();
            try {
                final CustomerPaymentMethodsSynchronizationResult synchronizationResult = customerPaymentMethodsSynchronizationService.synchronizeCustomerPaymentMethods(customerId, paymentProviderType);
                customerPaymentMethods = synchronizationResult.getCreatedCustomerPaymentMethods();
            } catch (final Exception ex) {
                // Only log and swallow
                LOGGER.error("Error occurred while synchronizing payment methods for customer - " + customerId + ",  paymentProviderType - " + paymentProviderType + ", remainingAttemptsCount - " + remainingAttemptsCount, ex);
            }
            if (customerPaymentMethods.size() == 0) {
                if (remainingAttemptsCount >= 1) {
                    final CustomerPaymentMethodsSynchronizationTask synchronizationTask = new CustomerPaymentMethodsSynchronizationTask(customerId, paymentProviderType, remainingAttemptsCount - 1);
                    LOGGER.debug("No new payment methods where found after synchronization attempt, rescheduling task - {}", synchronizationTask);
                    // Schedule new task
                    scheduledTaskExecutorService.scheduleTaskForExecution(synchronizationTask, PAYMENT_METHOD_SYNCHRONIZATION_WAIT_PERIOD_IN_MILLIS, TimeUnit.MILLISECONDS, true);
                } else {
                    LOGGER.debug("No new payment methods where found after synchronization attempt, reached attempts count, do not schedule new task");
                }
            }
        }
    }
}
