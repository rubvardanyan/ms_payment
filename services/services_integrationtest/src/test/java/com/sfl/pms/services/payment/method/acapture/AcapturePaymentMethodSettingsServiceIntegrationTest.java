package com.sfl.pms.services.payment.method.acapture;

import com.sfl.pms.services.payment.method.AbstractPaymentMethodSettingIntegrationService;
import com.sfl.pms.services.payment.method.AbstractPaymentMethodSettingsService;
import com.sfl.pms.services.payment.method.model.acapture.AcapturePaymentMethodSettings;
import com.sfl.pms.services.payment.method.model.acapture.AcapturePaymentMethodType;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 9/28/18
 * Time: 11:20 PM
 */
public class AcapturePaymentMethodSettingsServiceIntegrationTest extends AbstractPaymentMethodSettingIntegrationService<AcapturePaymentMethodSettings> {

    /* Dependencies */
    @Autowired
    private AcapturePaymentMethodSettingsService acapturePaymentMethodSettingsService;

    /* Constructor */
    public AcapturePaymentMethodSettingsServiceIntegrationTest() {
    }

    /* Test methods */
    @Test
    public void testGetAcapturePaymentMethodSettingsByPaymentMethodTypeAndPaymentSettingsId() {
        //Prepare data
        final AcapturePaymentMethodSettings paymentMethodSettings = getServicesTestHelper().createAcapturePaymentMethodSettings();
        final AcapturePaymentMethodType acapturePaymentMethodType = paymentMethodSettings.getPaymentMethodType().getAcapturePaymentMethodType();
        final Long paymentSettingsId = paymentMethodSettings.getPaymentSettings().getId();
        final AcapturePaymentMethodSettings result = acapturePaymentMethodSettingsService.getAcapturePaymentMethodSettingsByPaymentMethodTypeAndPaymentSettingsId(acapturePaymentMethodType, paymentSettingsId);
        assertEquals(paymentMethodSettings, result);
        flushAndClear();
        // Load payment method settings
        final AcapturePaymentMethodSettings resultAfterFlush = acapturePaymentMethodSettingsService.getAcapturePaymentMethodSettingsByPaymentMethodTypeAndPaymentSettingsId(acapturePaymentMethodType, paymentSettingsId);
        assertEquals(paymentMethodSettings, resultAfterFlush);
    }

    /* Override methods */
    @Override
    public AcapturePaymentMethodSettings getInstance() {
        return getServicesTestHelper().createAcapturePaymentMethodSettings();
    }

    @Override
    public AbstractPaymentMethodSettingsService<AcapturePaymentMethodSettings> getService() {
        return acapturePaymentMethodSettingsService;
    }
}
