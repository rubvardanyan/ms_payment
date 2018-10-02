package com.sfl.pms.services.payment.method;

import com.sfl.pms.services.payment.method.model.PaymentMethodSettings;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 9/28/18
 * Time: 11:15 PM
 */
public class PaymentMethodSettingsIntegrationService extends AbstractPaymentMethodSettingIntegrationService<PaymentMethodSettings> {

    /* Dependencies */
    @Autowired
    private PaymentMethodSettingsService paymentMethodSettingsService;

    /* Constructor */
    public PaymentMethodSettingsIntegrationService() {
    }

    @Override
    public PaymentMethodSettings getInstance() {
        return getServicesTestHelper().createPaymentMethodSettings();
    }

    @Override
    public AbstractPaymentMethodSettingsService<PaymentMethodSettings> getService() {
        return paymentMethodSettingsService;
    }
}
