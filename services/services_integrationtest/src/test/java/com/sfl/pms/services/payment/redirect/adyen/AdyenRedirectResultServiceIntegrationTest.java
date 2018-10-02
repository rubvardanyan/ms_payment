package com.sfl.pms.services.payment.redirect.adyen;

import com.sfl.pms.services.payment.redirect.AbstractPaymentProviderRedirectResultService;
import com.sfl.pms.services.payment.redirect.AbstractPaymentProviderRedirectResultServiceIntegrationTest;
import com.sfl.pms.services.payment.redirect.dto.redirect.adyen.AdyenRedirectResultDto;
import com.sfl.pms.services.payment.redirect.model.adyen.AdyenRedirectResult;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * User: Ruben Dilanyan
 * Company: SFL LLC
 * Date: 7/11/15
 * Time: 1:16 PM
 */
public class AdyenRedirectResultServiceIntegrationTest extends AbstractPaymentProviderRedirectResultServiceIntegrationTest<AdyenRedirectResult> {

    /* Dependencies */
    @Autowired
    private AdyenRedirectResultService adyenRedirectResultService;

    /* Constructors */
    public AdyenRedirectResultServiceIntegrationTest() {
    }

    /* Test methods */
    @Test
    public void testCreatePaymentProviderRedirectResult() {
        // Prepare data
        final AdyenRedirectResultDto adyenRedirectResultDto = getServicesTestHelper().createAdyenRedirectResultDto();
        // Create adyen redirect result
        AdyenRedirectResult adyenRedirectResult = adyenRedirectResultService.createPaymentProviderRedirectResult(adyenRedirectResultDto);
        getServicesTestHelper().assertAdyenRedirectResult(adyenRedirectResult, adyenRedirectResultDto);
        // Flush, clear, reload and assert
        flushAndClear();
        adyenRedirectResult = adyenRedirectResultService.getPaymentProviderRedirectResultById(adyenRedirectResult.getId());
        getServicesTestHelper().assertAdyenRedirectResult(adyenRedirectResult, adyenRedirectResultDto);
    }

    /* Utility methods */
    @Override
    protected AbstractPaymentProviderRedirectResultService<AdyenRedirectResult> getService() {
        return adyenRedirectResultService;
    }

    @Override
    protected AdyenRedirectResult getInstance() {
        return getServicesTestHelper().createAdyenRedirectResult();
    }

    @Override
    protected Class<AdyenRedirectResult> getInstanceClass() {
        return AdyenRedirectResult.class;
    }
}
