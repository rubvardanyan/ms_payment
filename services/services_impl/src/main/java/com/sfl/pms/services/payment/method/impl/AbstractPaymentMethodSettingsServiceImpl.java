package com.sfl.pms.services.payment.method.impl;

import com.sfl.pms.persistence.repositories.payment.method.AbstractPaymentMethodSettingsRepository;
import com.sfl.pms.services.payment.method.AbstractPaymentMethodSettingsService;
import com.sfl.pms.services.payment.method.exception.PaymentMethodSettingsNotFoundForIdException;
import com.sfl.pms.services.payment.method.model.PaymentMethodSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import javax.annotation.Nonnull;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 9/28/18
 * Time: 9:17 PM
 */
public abstract class AbstractPaymentMethodSettingsServiceImpl<T extends PaymentMethodSettings> implements AbstractPaymentMethodSettingsService<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractPaymentMethodSettingsServiceImpl.class);

    /* Constructor */
    public AbstractPaymentMethodSettingsServiceImpl() {
        LOGGER.debug("Initializing abstract payment method settings service");
    }

    /* Public methods */
    @Override
    public T getPaymentMethodSettingsById(@Nonnull final Long paymentMethodSettingsId) {
        assertPaymentMethodSettingsId(paymentMethodSettingsId);
        LOGGER.debug("Getting payment method settings for id - {}", paymentMethodSettingsId);
        final T paymentMethodSettings = getRepository().findOne(paymentMethodSettingsId);
        assertPaymentMethodSettingsForId(paymentMethodSettings, paymentMethodSettingsId);
        LOGGER.debug("Found payment method settings -{}, for id - {}", paymentMethodSettings, paymentMethodSettingsId);
        return paymentMethodSettings;
    }

    /* Abstract methods */
    protected abstract AbstractPaymentMethodSettingsRepository<T> getRepository();

    protected abstract Class<T> getInstanceClass();

    /* Utility methods */
    private void assertPaymentMethodSettingsId(final Long paymentMethodSettingsId) {
        Assert.notNull(paymentMethodSettingsId, "Payment method id should not be null");
    }

    private void assertPaymentMethodSettingsForId(final T paymentMethodSettings, final Long paymentMethodSettingsId) {
        if(paymentMethodSettings == null) {
            LOGGER.error("Payment method settings not found for id - {}", paymentMethodSettingsId);
            throw new PaymentMethodSettingsNotFoundForIdException(paymentMethodSettingsId, getInstanceClass());
        }
    }
}
