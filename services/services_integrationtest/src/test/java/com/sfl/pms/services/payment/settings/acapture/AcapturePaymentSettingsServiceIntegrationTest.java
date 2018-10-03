package com.sfl.pms.services.payment.settings.acapture;

import com.sfl.pms.services.payment.settings.AbstractPaymentProviderSettingsService;
import com.sfl.pms.services.payment.settings.AbstractPaymentProviderSettingsServiceIntegrationTest;
import com.sfl.pms.services.payment.settings.model.acapture.AcapturePaymentSettings;
import com.sfl.pms.services.system.environment.model.EnvironmentType;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import static org.junit.Assert.assertEquals;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 9/28/18
 * Time: 8:54 PM
 */
public class AcapturePaymentSettingsServiceIntegrationTest extends AbstractPaymentProviderSettingsServiceIntegrationTest<AcapturePaymentSettings> {

    /* Dependencies */
    @Autowired
    private AcapturePaymentSettingsService acapturePaymentSettingsService;

    @Value("#{ appProperties['acapture.environment.type']}")
    private String environmentType;

    /* Test methods */
    @Test
    public void testGetActivePaymentSettings() {
        // Prepare data
        final EnvironmentType environmentType = EnvironmentType.valueOf(this.environmentType);
        final AcapturePaymentSettings paymentSettings = getServicesTestHelper().createAcapturePaymentSettings(environmentType);
        flushAndClear();
        // Load current settings
        final AcapturePaymentSettings result = acapturePaymentSettingsService.getActivePaymentSettings();
        assertEquals(paymentSettings, result);
    }

    /* Override methods */
    @Override
    protected AbstractPaymentProviderSettingsService<AcapturePaymentSettings> getService() {
        return acapturePaymentSettingsService;
    }

    @Override
    protected AcapturePaymentSettings getInstance() {
        return getServicesTestHelper().createAcapturePaymentSettings();
    }
}
