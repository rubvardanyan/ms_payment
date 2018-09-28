package com.sfl.pms.services.payment.method;

import com.sfl.pms.services.payment.method.model.PaymentMethodSettings;
import com.sfl.pms.services.test.AbstractServiceIntegrationTest;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 9/28/18
 * Time: 11:02 PM
 */
public abstract class AbstractPaymentMethodSettingIntegrationService<T extends PaymentMethodSettings> extends AbstractServiceIntegrationTest {

    /* Constructor */
    public AbstractPaymentMethodSettingIntegrationService() {
    }

    /* Test methods */
    @Test
    public void testGetPaymentMethodSettingsById() {
        // Prepare data
        final T paymentMethodSettings = getInstance();
        final T result = getService().getPaymentMethodSettingsById(paymentMethodSettings.getId());
        assertEquals(paymentMethodSettings, result);
        flushAndClear();
        // Load payment method setting
        final T resultAfterFlush = getService().getPaymentMethodSettingsById(paymentMethodSettings.getId());
        assertEquals(paymentMethodSettings, resultAfterFlush);
    }

    /* Abstract methods */
    public abstract T getInstance();
    public abstract AbstractPaymentMethodSettingsService<T> getService();

}
