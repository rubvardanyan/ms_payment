package com.sfl.pms.services.payment.method.impl;

import com.sfl.pms.persistence.repositories.payment.method.AbstractPaymentMethodSettingsRepository;
import com.sfl.pms.persistence.repositories.payment.method.PaymentMethodSettingsRepository;
import com.sfl.pms.services.payment.method.PaymentMethodSettingsService;
import com.sfl.pms.services.payment.method.model.PaymentMethodSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 9/28/18
 * Time: 9:37 PM
 */
@Service
public class PaymentMethodSettingsServiceImpl extends AbstractPaymentMethodSettingsServiceImpl<PaymentMethodSettings> implements PaymentMethodSettingsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentMethodSettingsServiceImpl.class);

    /* Dependencies */
    @Autowired
    private PaymentMethodSettingsRepository paymentMethodSettingsRepository;

    /* Constructor */
    public PaymentMethodSettingsServiceImpl() {
        LOGGER.debug("Initializing payment method settings service");
    }

    /* Override methods */
    @Override
    protected AbstractPaymentMethodSettingsRepository<PaymentMethodSettings> getRepository() {
        return paymentMethodSettingsRepository;
    }

    @Override
    protected Class<PaymentMethodSettings> getInstanceClass() {
        return PaymentMethodSettings.class;
    }
}
