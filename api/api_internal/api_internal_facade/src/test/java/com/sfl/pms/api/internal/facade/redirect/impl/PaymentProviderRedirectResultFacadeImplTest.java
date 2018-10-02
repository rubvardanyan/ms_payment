package com.sfl.pms.api.internal.facade.redirect.impl;

import com.sfl.pms.api.internal.facade.test.AbstractFacadeUnitTest;
import com.sfl.pms.core.api.internal.model.common.result.ErrorType;
import com.sfl.pms.core.api.internal.model.common.result.ResultResponseModel;
import com.sfl.pms.core.api.internal.model.redirect.PaymentProviderRedirectResultStateClientType;
import com.sfl.pms.core.api.internal.model.redirect.request.CreateAcaptureRedirectResultRequest;
import com.sfl.pms.core.api.internal.model.redirect.request.CreateAdyenRedirectResultRequest;
import com.sfl.pms.core.api.internal.model.redirect.request.GetPaymentProviderRedirectResultStatusRequest;
import com.sfl.pms.core.api.internal.model.redirect.response.CreateAcaptureRedirectResultResponse;
import com.sfl.pms.core.api.internal.model.redirect.response.CreateAdyenRedirectResultResponse;
import com.sfl.pms.core.api.internal.model.redirect.response.GetPaymentProviderRedirectResultStatusResponse;
import com.sfl.pms.services.payment.common.model.Payment;
import com.sfl.pms.services.payment.redirect.PaymentProviderRedirectResultService;
import com.sfl.pms.services.payment.redirect.acapture.AcaptureRedirectResultService;
import com.sfl.pms.services.payment.redirect.adyen.AdyenRedirectResultService;
import com.sfl.pms.services.payment.redirect.dto.redirect.acapture.AcaptureRedirectResultDto;
import com.sfl.pms.services.payment.redirect.dto.redirect.adyen.AdyenRedirectResultDto;
import com.sfl.pms.services.payment.redirect.event.StartPaymentProviderRedirectResultProcessingEvent;
import com.sfl.pms.services.payment.redirect.model.PaymentProviderRedirectResult;
import com.sfl.pms.services.payment.redirect.model.PaymentProviderRedirectResultState;
import com.sfl.pms.services.payment.redirect.model.acapture.AcaptureRedirectResult;
import com.sfl.pms.services.payment.redirect.model.adyen.AdyenRedirectResult;
import com.sfl.pms.services.system.event.ApplicationEventDistributionService;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.UUID;

import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.*;

/**
 * User: Ruben Dilanyan
 * Company: SFL LLC
 * Date: 1/19/16
 * Time: 11:54 AM
 */
public class PaymentProviderRedirectResultFacadeImplTest extends AbstractFacadeUnitTest {

    /* Test subject and mocks */
    @TestSubject
    private PaymentProviderRedirectResultFacadeImpl paymentProviderRedirectFacade = new PaymentProviderRedirectResultFacadeImpl();

    @Mock
    private AdyenRedirectResultService adyenRedirectResultService;

    @Mock
    private AcaptureRedirectResultService acaptureRedirectResultService;

    @Mock
    private ApplicationEventDistributionService applicationEventDistributionService;

    @Mock
    private PaymentProviderRedirectResultService paymentProviderRedirectResultService;

    /* Constructors */
    public PaymentProviderRedirectResultFacadeImplTest() {
    }

    /* Test methods */
    @Test
    public void testGetGetRedirectResultStatusWithInvalidArguments() {
        // Reset
        resetAll();
        // Replay
        replayAll();
        // Run test scenario
        try {
            paymentProviderRedirectFacade.getRedirectResultStatus(null);
            fail("Exception should be thrown");
        } catch (final IllegalArgumentException ex) {
            // Expected
        }
        // Verify
        verifyAll();
    }

    @Test
    public void testGetGetRedirectResultStatusWithValidationErrors() {
        // Test data
        final GetPaymentProviderRedirectResultStatusRequest request = new GetPaymentProviderRedirectResultStatusRequest();
        // Reset
        resetAll();
        // Replay
        replayAll();
        // Run test scenario
        final ResultResponseModel<GetPaymentProviderRedirectResultStatusResponse> result = paymentProviderRedirectFacade.getRedirectResultStatus(request);
        assertNotNull(result);
        assertNull(result.getResponse());
        assertEquals(1, result.getErrors().size());
        assertValidationErrors(result.getErrors(), Collections.singleton(ErrorType.PAYMENT_PROVIDER_REDIRECT_RESULT_MISSING_UUID));
        // Verify
        verifyAll();
    }

    @Test
    public void testGetGetRedirectResultStatus() {
        // Test data
        final GetPaymentProviderRedirectResultStatusRequest request = getFacadeImplTestHelper().createGetRedirectResultStatusRequest();
        final Long redirectResultId = 1L;
        final PaymentProviderRedirectResult providerRedirectResult = getFacadeImplTestHelper().createAdyenRedirectResult();
        providerRedirectResult.setId(redirectResultId);
        providerRedirectResult.setState(PaymentProviderRedirectResultState.PROCESSED);
        // Payment
        final Long paymentId = 2L;
        final Payment payment = getFacadeImplTestHelper().createOrderPayment();
        payment.setId(paymentId);
        providerRedirectResult.setPayment(payment);
        // Reset
        resetAll();
        // Expectations
        expect(paymentProviderRedirectResultService.getPaymentProviderRedirectResultByUuId(eq(request.getRedirectResultUuId()))).andReturn(providerRedirectResult).once();
        // Replay
        replayAll();
        // Run test scenario
        final ResultResponseModel<GetPaymentProviderRedirectResultStatusResponse> result = paymentProviderRedirectFacade.getRedirectResultStatus(request);
        assertNotNull(result);
        assertNotNull(result.getResponse());
        assertEquals(0, result.getErrors().size());
        final GetPaymentProviderRedirectResultStatusResponse response = result.getResponse();
        assertEquals(providerRedirectResult.getUuId(), response.getRedirectResultUuId());
        assertEquals(PaymentProviderRedirectResultStateClientType.valueOf(providerRedirectResult.getState().name()), response.getState());
        assertEquals(payment.getUuId(), response.getPaymentUuId());
        // Verify
        verifyAll();
    }

    @Test
    public void testCreateAdyenRedirectResultWithInvalidArguments() {
        // Reset
        resetAll();
        // Replay
        replayAll();
        // Run test scenario
        try {
            paymentProviderRedirectFacade.createAdyenRedirectResult(null);
            fail("Exception should be thrown");
        } catch (final IllegalArgumentException ex) {
            // Expected
        }
        // Verify
        verifyAll();
    }

    @Test
    public void testCreateAdyenRedirectResultWithValidationErrors() {
        // Test data
        final CreateAdyenRedirectResultRequest request = new CreateAdyenRedirectResultRequest();
        // Reset
        resetAll();
        // Replay
        replayAll();
        // Run test scenario
        final ResultResponseModel<CreateAdyenRedirectResultResponse> result = paymentProviderRedirectFacade.createAdyenRedirectResult(request);
        assertNotNull(result);
        assertNull(result.getResponse());
        assertEquals(4, result.getErrors().size());
        assertValidationErrors(result.getErrors(), new HashSet<>(Arrays.asList(
                ErrorType.PAYMENT_PROVIDER_REDIRECT_RESULT_MISSING_AUTH_RESULT,
                ErrorType.PAYMENT_PROVIDER_REDIRECT_RESULT_MISSING_MERCHANT_SIGNATURE,
                ErrorType.PAYMENT_PROVIDER_REDIRECT_RESULT_MISSING_SKIN_CODE,
                ErrorType.PAYMENT_PROVIDER_REDIRECT_RESULT_MISSING_MERCHANT_REFERENCE)
        ));
        // Verify
        verifyAll();
    }

    @Test
    public void testCreateAdyenRedirectResult() {
        // Test data
        final CreateAdyenRedirectResultRequest request = getFacadeImplTestHelper().createCreateAdyenRedirectResultRequest();
        // Redirect result
        final AdyenRedirectResultDto adyenRedirectResultDto = new AdyenRedirectResultDto(request.getAuthResult(), request.getPspReference(), request.getMerchantReference(), request.getSkinCode(), request.getMerchantSignature(), request.getPaymentMethod(), request.getShopperLocale(), request.getMerchantReturnData());
        final Long adyenRedirectResultId = 1L;
        final AdyenRedirectResult adyenRedirectResult = getFacadeImplTestHelper().createAdyenRedirectResult();
        adyenRedirectResult.setId(adyenRedirectResultId);
        // Reset
        resetAll();
        // Expectations
        expect(adyenRedirectResultService.createPaymentProviderRedirectResult(eq(adyenRedirectResultDto))).andReturn(adyenRedirectResult).once();
        applicationEventDistributionService.publishAsynchronousEvent(eq(new StartPaymentProviderRedirectResultProcessingEvent(adyenRedirectResultId)));
        // Replay
        replayAll();
        // Run test scenario
        final ResultResponseModel<CreateAdyenRedirectResultResponse> result = paymentProviderRedirectFacade.createAdyenRedirectResult(request);
        assertNotNull(result);
        assertEquals(0, result.getErrors().size());
        assertNotNull(result.getResponse());
        final CreateAdyenRedirectResultResponse response = result.getResponse();
        assertEquals(adyenRedirectResult.getUuId(), response.getPaymentProviderRedirectResultUuId());
        // Verify
        verifyAll();
    }

    @Test
    public void testCreateAcaptureRedirectResultWithInvalidArguments() {
        // Reset
        resetAll();
        // Replay
        replayAll();
        // Run test scenario
        try {
            paymentProviderRedirectFacade.createAcaptureRedirectResult(null);
            fail("Exception should be thrown");
        } catch (IllegalArgumentException ex) {
            //success
        }
        // Verify
        verifyAll();
    }

    @Test
    public void testCreateAcaptureRedirectResultWithValidationErrors() {
        // Test data
        final CreateAcaptureRedirectResultRequest request = new CreateAcaptureRedirectResultRequest(null, "");
        // Reset
        resetAll();
        // Replay
        replayAll();
        // Run test scenario
        final ResultResponseModel<CreateAcaptureRedirectResultResponse> response = paymentProviderRedirectFacade.createAcaptureRedirectResult(request);
        assertEquals(2, response.getErrors().size());
        assertValidationErrors(response.getErrors(), new HashSet<>(Arrays.asList(
                ErrorType.PAYMENT_PROVIDER_REDIRECT_RESULT_MISSING_CHECKOUT_ID,
                ErrorType.PAYMENT_PROVIDER_REDIRECT_RESULT_MISSING_RESOURCE_PATH
        )));
        // Verify
        verifyAll();
    }

    @Test
    public void testCreateAcaptureRedirectResult() {
        // Test data
        final CreateAcaptureRedirectResultRequest request = new CreateAcaptureRedirectResultRequest(UUID.randomUUID().toString(), UUID.randomUUID().toString());
        final AcaptureRedirectResultDto acaptureRedirectResultDto = new AcaptureRedirectResultDto(request.getCheckoutId(), request.getResourcePath());
        final AcaptureRedirectResult acaptureRedirectResult = new AcaptureRedirectResult();
        acaptureRedirectResult.setCheckoutId(acaptureRedirectResultDto.getCheckoutId());
        acaptureRedirectResult.setResourcePath(acaptureRedirectResultDto.getResourcePath());
        acaptureRedirectResult.setId(665L);
        acaptureRedirectResult.setUuId(UUID.randomUUID().toString());
        // Reset
        resetAll();
        // Expectations
        expect(acaptureRedirectResultService.createPaymentProviderRedirectResult(acaptureRedirectResultDto)).andReturn(acaptureRedirectResult);
        applicationEventDistributionService.publishAsynchronousEvent(new StartPaymentProviderRedirectResultProcessingEvent(acaptureRedirectResult.getId()));
        // Replay
        replayAll();
        // Run test scenario
        final ResultResponseModel<CreateAcaptureRedirectResultResponse> result = paymentProviderRedirectFacade.createAcaptureRedirectResult(request);
        assertNotNull(result);
        assertEquals(0, result.getErrors().size());
        assertNotNull(result.getResponse());
        assertEquals(acaptureRedirectResult.getUuId(), result.getResponse().getPaymentProviderRedirectResultUuId());
        // Verify
        verifyAll();
    }
}
