package com.sfl.pms.services.payment.common.impl.removal;

import com.sfl.pms.services.payment.common.removal.CustomerPaymentMethodRemovalProcessingService;
import com.sfl.pms.services.payment.customer.method.CustomerPaymentMethodService;
import com.sfl.pms.services.payment.customer.method.model.CustomerPaymentMethod;
import com.sfl.pms.services.payment.customer.method.model.CustomerPaymentMethodProviderInformation;
import com.sfl.pms.services.payment.processing.impl.PaymentProviderOperationsProcessor;
import com.sfl.pms.services.payment.provider.exception.UnknownPaymentProviderTypeException;
import com.sfl.pms.services.payment.provider.model.PaymentProviderType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Nonnull;

/**
 * User: Mher Sargsyan
 * Company: SFL LLC
 * Date: 4/13/15
 * Time: 2:45 PM
 */
@Service
public class CustomerPaymentMethodRemovalProcessingServiceImpl implements CustomerPaymentMethodRemovalProcessingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomerPaymentMethodRemovalProcessingServiceImpl.class);

    /* Dependencies */
    @Autowired
    @Qualifier("adyenPaymentOperationsProcessor")
    private PaymentProviderOperationsProcessor adyenPaymentProviderOperationsProcessor;

    @Autowired
    private CustomerPaymentMethodService customerPaymentMethodService;

    /* Constructors */
    public CustomerPaymentMethodRemovalProcessingServiceImpl() {
        LOGGER.debug("Initializing customer payment method removal processing service");
    }

    /* Public methods */
    @Override
    public void processCustomerPaymentMethodRemovalRequest(@Nonnull final Long paymentMethodRemovalRequestId) {
        Assert.notNull(paymentMethodRemovalRequestId, "Payment method removal request id should not be null");
        /* Retrieve customer payment method */
        final CustomerPaymentMethod customerPaymentMethod = customerPaymentMethodService.getCustomerPaymentMethodById(paymentMethodRemovalRequestId);
        Assert.isTrue(customerPaymentMethod.getRemoved() != null, "Customer payment method should be removed");
        final CustomerPaymentMethodProviderInformation customerPaymentMethodProviderInformation = customerPaymentMethod.getProviderInformation();
        /* Get appropriate provider operations processor */
        LOGGER.debug("Starting to proceed customer payment method removal for payment method provider information - {}", customerPaymentMethodProviderInformation);
        final PaymentProviderOperationsProcessor paymentProviderOperationsProcessor = getPaymentProviderProcessor(customerPaymentMethodProviderInformation.getType());
        final PaymentProviderOperationsProcessor.CustomerPaymentMethodRemovalData customerPaymentMethodRemovalData = paymentProviderOperationsProcessor.processCustomerPaymentMethodRemoval(customerPaymentMethodProviderInformation.getId());
        LOGGER.debug("Successfully processed customer payment method removal request with status - {}", customerPaymentMethodRemovalData.getStatus());
    }

    /* Utility methods */
    protected PaymentProviderOperationsProcessor getPaymentProviderProcessor(final PaymentProviderType paymentProviderType) {
        switch (paymentProviderType) {
            case ADYEN: {
                return adyenPaymentProviderOperationsProcessor;
            }
            default: {
                LOGGER.error("Unknown payment provider type - {}", paymentProviderType);
                throw new UnknownPaymentProviderTypeException(paymentProviderType);
            }
        }
    }
}
