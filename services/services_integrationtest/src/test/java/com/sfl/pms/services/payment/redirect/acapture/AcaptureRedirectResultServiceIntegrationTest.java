package com.sfl.pms.services.payment.redirect.acapture;

import com.sfl.pms.services.payment.redirect.AbstractPaymentProviderRedirectResultService;
import com.sfl.pms.services.payment.redirect.AbstractPaymentProviderRedirectResultServiceIntegrationTest;
import com.sfl.pms.services.payment.redirect.dto.redirect.acapture.AcaptureRedirectResultDto;
import com.sfl.pms.services.payment.redirect.model.acapture.AcaptureRedirectResult;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 9/30/18
 * Time: 11:29 PM
 */
public class AcaptureRedirectResultServiceIntegrationTest extends AbstractPaymentProviderRedirectResultServiceIntegrationTest<AcaptureRedirectResult> {

    /* Dependencies */
    @Autowired
    private AcaptureRedirectResultService acaptureRedirectResultService;

    /* Test methods */
    @Test
    public void testCreatePaymentProviderRedirectResult() {
        // Prepare data
        final AcaptureRedirectResultDto acaptureRedirectResultDto = getServicesTestHelper().createAcaptureRedirectResultDto();
        AcaptureRedirectResult redirectResult = acaptureRedirectResultService.createPaymentProviderRedirectResult(acaptureRedirectResultDto);
        getServicesTestHelper().assertAcaptureRedirectResult(redirectResult, acaptureRedirectResultDto);
        flushAndClear();
        // Load redirect result
        redirectResult = acaptureRedirectResultService.getPaymentProviderRedirectResultById(redirectResult.getId());
        getServicesTestHelper().assertAcaptureRedirectResult(redirectResult, acaptureRedirectResultDto);
    }

    /* Override methods */
    @Override
    protected AbstractPaymentProviderRedirectResultService<AcaptureRedirectResult> getService() {
        return acaptureRedirectResultService;
    }

    @Override
    protected AcaptureRedirectResult getInstance() {
        return getServicesTestHelper().createAcaptureRedirectResult();
    }

    @Override
    protected Class<AcaptureRedirectResult> getInstanceClass() {
        return AcaptureRedirectResult.class;
    }
}
