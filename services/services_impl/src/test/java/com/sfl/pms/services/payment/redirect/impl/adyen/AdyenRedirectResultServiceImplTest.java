package com.sfl.pms.services.payment.redirect.impl.adyen;

import com.sfl.pms.persistence.repositories.payment.redirect.AbstractPaymentProviderRedirectResultRepository;
import com.sfl.pms.persistence.repositories.payment.redirect.adyen.AdyenRedirectResultRepository;
import com.sfl.pms.services.payment.redirect.AbstractPaymentProviderRedirectResultService;
import com.sfl.pms.services.payment.redirect.dto.redirect.adyen.AdyenRedirectResultDto;
import com.sfl.pms.services.payment.redirect.impl.AbstractPaymentProviderRedirectResultServiceImplTest;
import com.sfl.pms.services.payment.redirect.model.adyen.AdyenRedirectResult;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Test;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.fail;

/**
 * User: Ruben Dilanyan
 * Company: SFL LLC
 * Date: 7/11/15
 * Time: 12:53 PM
 */
public class AdyenRedirectResultServiceImplTest extends AbstractPaymentProviderRedirectResultServiceImplTest<AdyenRedirectResult> {

    /* Test subject and mocks */
    @TestSubject
    private AdyenRedirectResultServiceImpl adyenRedirectResultService = new AdyenRedirectResultServiceImpl();

    @Mock
    private AdyenRedirectResultRepository adyenRedirectResultRepository;

    /* Constructors */
    public AdyenRedirectResultServiceImplTest() {
    }

    /* Test methods */
    @Test
    public void testCreatePaymentProviderRedirectResultWithInvalidArguments() {
        // Test data
        final AdyenRedirectResultDto resultDto = getServicesImplTestHelper().createAdyenRedirectResultDto();
        // Reset
        resetAll();
        // Replay
        replayAll();
        // Run test scenario
        try {
            adyenRedirectResultService.createPaymentProviderRedirectResult(null);
            fail("Exception should be thrown");
        } catch (final IllegalArgumentException ex) {
            // Expected
        }
        try {
            adyenRedirectResultService.createPaymentProviderRedirectResult(new AdyenRedirectResultDto(null, resultDto.getPspReference(), resultDto.getMerchantReference(), resultDto.getSkinCode(), resultDto.getMerchantSig(), resultDto.getPaymentMethod(), resultDto.getShopperLocale(), resultDto.getMerchantReturnData()));
            fail("Exception should be thrown");
        } catch (final IllegalArgumentException ex) {
            // Expected
        }
        try {
            adyenRedirectResultService.createPaymentProviderRedirectResult(new AdyenRedirectResultDto(resultDto.getAuthResult(), resultDto.getPspReference(), null, resultDto.getSkinCode(), resultDto.getMerchantSig(), resultDto.getPaymentMethod(), resultDto.getShopperLocale(), resultDto.getMerchantReturnData()));
            fail("Exception should be thrown");
        } catch (final IllegalArgumentException ex) {
            // Expected
        }
        try {
            adyenRedirectResultService.createPaymentProviderRedirectResult(new AdyenRedirectResultDto(resultDto.getAuthResult(), resultDto.getPspReference(), resultDto.getMerchantReference(), null, resultDto.getMerchantSig(), resultDto.getPaymentMethod(), resultDto.getShopperLocale(), resultDto.getMerchantReturnData()));
            fail("Exception should be thrown");
        } catch (final IllegalArgumentException ex) {
            // Expected
        }
        try {
            adyenRedirectResultService.createPaymentProviderRedirectResult(new AdyenRedirectResultDto(resultDto.getAuthResult(), resultDto.getPspReference(), resultDto.getMerchantReference(), resultDto.getSkinCode(), null, resultDto.getPaymentMethod(), resultDto.getShopperLocale(), resultDto.getMerchantReturnData()));
            fail("Exception should be thrown");
        } catch (final IllegalArgumentException ex) {
            // Expected
        }
        // Verify
        verifyAll();
    }

    @Test
    public void testCreatePaymentProviderRedirectResult() {
        // Test data
        final AdyenRedirectResultDto resultDto = getServicesImplTestHelper().createAdyenRedirectResultDto();
        // Reset
        resetAll();
        // Expectations
        expect(adyenRedirectResultRepository.save(isA(AdyenRedirectResult.class))).andAnswer(() -> (AdyenRedirectResult) getCurrentArguments()[0]).once();
        // Replay
        replayAll();
        // Run test scenario
        final AdyenRedirectResult adyenRedirectResult = adyenRedirectResultService.createPaymentProviderRedirectResult(resultDto);
        getServicesImplTestHelper().assertAdyenRedirectResult(adyenRedirectResult, resultDto);
        // Verify
        verifyAll();
    }

    /* Utility methods */
    @Override
    protected AbstractPaymentProviderRedirectResultRepository<AdyenRedirectResult> getRepository() {
        return adyenRedirectResultRepository;
    }

    @Override
    protected AbstractPaymentProviderRedirectResultService<AdyenRedirectResult> getService() {
        return adyenRedirectResultService;
    }

    @Override
    protected AdyenRedirectResult getInstance() {
        return getServicesImplTestHelper().createAdyenRedirectResult();
    }

    @Override
    protected Class<AdyenRedirectResult> getInstanceClass() {
        return AdyenRedirectResult.class;
    }
}
