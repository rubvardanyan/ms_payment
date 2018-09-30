package com.sfl.pms.services.payment.processing.impl;

import com.sfl.pms.persistence.utility.PersistenceUtilityService;
import com.sfl.pms.services.payment.common.PaymentService;
import com.sfl.pms.services.payment.common.dto.PaymentStateChangeHistoryRecordDto;
import com.sfl.pms.services.payment.common.exception.UnknownPaymentTypeException;
import com.sfl.pms.services.payment.common.model.Payment;
import com.sfl.pms.services.payment.common.model.PaymentState;
import com.sfl.pms.services.payment.common.model.PaymentType;
import com.sfl.pms.services.payment.processing.impl.auth.CustomerPaymentMethodAuthorizationPaymentSpecificOperationsProcessor;
import com.sfl.pms.services.payment.processing.impl.order.OrderPaymentSpecificOperationsProcessor;
import com.sfl.pms.services.payment.provider.exception.UnknownPaymentProviderTypeException;
import com.sfl.pms.services.payment.provider.model.PaymentProviderType;
import com.sfl.pms.services.util.mutable.MutableHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.annotation.Nonnull;

/**
 * User: Ruben Dilanyan
 * Company: SFL LLC
 * Date: 1/13/15
 * Time: 7:08 PM
 */
public abstract class AbstractPaymentProcessorImpl {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractPaymentProcessorImpl.class);

    /* Dependencies */
    @Autowired
    @Qualifier("adyenPaymentOperationsProcessor")
    private PaymentProviderOperationsProcessor adyenPaymentProviderOperationsProcessor;

    @Autowired
    @Qualifier("acapturePaymentOperationsProcessor")
    private PaymentProviderOperationsProcessor acapturePaymentProviderOperationsProcessor;

    @Autowired
    private PersistenceUtilityService persistenceUtilityService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    @Qualifier("customerPaymentMethodAuthorizationPaymentSpecificOperationsProcessor")
    private CustomerPaymentMethodAuthorizationPaymentSpecificOperationsProcessor customerPaymentMethodAuthorizationPaymentSpecificOperationsProcessor;

    @Autowired
    @Qualifier("orderPaymentSpecificOperationsProcessor")
    private OrderPaymentSpecificOperationsProcessor orderPaymentSpecificOperationsProcessor;

    /* Constructors */
    public AbstractPaymentProcessorImpl() {
        LOGGER.debug("Initializing abstract payment processor service");
    }

    /* Utility methods */
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

    protected void updatePaymentState(final Long paymentId, final PaymentState paymentState, final String information) {
        persistenceUtilityService.runInNewTransaction(() -> {
            paymentService.updatePaymentState(paymentId, new PaymentStateChangeHistoryRecordDto(paymentState, information));
        });
    }

    protected Payment loadPayment(@Nonnull final Long paymentId) {
        final MutableHolder<Payment> paymentMutableHolder = new MutableHolder<>(null);
        persistenceUtilityService.runInNewTransaction(() -> {
            final Payment payment = paymentService.getPaymentById(paymentId);
            paymentMutableHolder.setValue(payment);
        });
        return paymentMutableHolder.getValue();
    }

    protected PaymentTypeSpecificOperationsProcessor getPaymentTypeSpecificOperationsProcessor(final PaymentType paymentType) {
        switch (paymentType) {
            case ORDER:
                return orderPaymentSpecificOperationsProcessor;
            case PAYMENT_METHOD_AUTHORIZATION:
                return customerPaymentMethodAuthorizationPaymentSpecificOperationsProcessor;
            default: {
                LOGGER.error("Unknown payment type - {}", paymentType);
                throw new UnknownPaymentTypeException(paymentType);
            }
        }
    }

    /* Properties getters and setters */
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

    public CustomerPaymentMethodAuthorizationPaymentSpecificOperationsProcessor getCustomerPaymentMethodAuthorizationPaymentSpecificOperationsProcessor() {
        return customerPaymentMethodAuthorizationPaymentSpecificOperationsProcessor;
    }

    public void setCustomerPaymentMethodAuthorizationPaymentSpecificOperationsProcessor(final CustomerPaymentMethodAuthorizationPaymentSpecificOperationsProcessor customerPaymentMethodAuthorizationPaymentSpecificOperationsProcessor) {
        this.customerPaymentMethodAuthorizationPaymentSpecificOperationsProcessor = customerPaymentMethodAuthorizationPaymentSpecificOperationsProcessor;
    }

    public OrderPaymentSpecificOperationsProcessor getOrderPaymentSpecificOperationsProcessor() {
        return orderPaymentSpecificOperationsProcessor;
    }

    public void setOrderPaymentSpecificOperationsProcessor(final OrderPaymentSpecificOperationsProcessor orderPaymentSpecificOperationsProcessor) {
        this.orderPaymentSpecificOperationsProcessor = orderPaymentSpecificOperationsProcessor;
    }
}
