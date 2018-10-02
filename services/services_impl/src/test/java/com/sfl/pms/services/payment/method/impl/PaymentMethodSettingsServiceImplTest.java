package com.sfl.pms.services.payment.method.impl;

import com.sfl.pms.persistence.repositories.payment.method.AbstractPaymentMethodSettingsRepository;
import com.sfl.pms.persistence.repositories.payment.method.PaymentMethodSettingsRepository;
import com.sfl.pms.services.payment.method.AbstractPaymentMethodSettingsService;
import com.sfl.pms.services.payment.method.model.PaymentMethodSettings;
import org.easymock.Mock;
import org.easymock.TestSubject;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 9/28/18
 * Time: 9:35 PM
 */
public class PaymentMethodSettingsServiceImplTest extends AbstractPaymentMethodSettingsServiceImplTest<PaymentMethodSettings> {

    /* Test subject and mocks */
    @TestSubject
    private PaymentMethodSettingsServiceImpl paymentMethodSettingsService = new PaymentMethodSettingsServiceImpl();

    @Mock
    private PaymentMethodSettingsRepository paymentMethodSettingsRepository;


    @Override
    protected Class<PaymentMethodSettings> getInstanceClass() {
        return PaymentMethodSettings.class;
    }

    @Override
    protected PaymentMethodSettings getInstance() {
        return getServicesImplTestHelper().createPaymentMethodSettings();
    }

    @Override
    protected AbstractPaymentMethodSettingsService<PaymentMethodSettings> getService() {
        return paymentMethodSettingsService;
    }

    @Override
    protected AbstractPaymentMethodSettingsRepository<PaymentMethodSettings> getRepository() {
        return paymentMethodSettingsRepository;
    }
}
