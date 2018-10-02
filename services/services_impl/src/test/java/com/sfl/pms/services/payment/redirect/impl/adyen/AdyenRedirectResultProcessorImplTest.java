package com.sfl.pms.services.payment.redirect.impl.adyen;

import com.sfl.pms.externalclients.payment.adyen.model.datatypes.AdyenPaymentStatus;
import com.sfl.pms.persistence.utility.PersistenceUtilityService;
import com.sfl.pms.services.payment.common.PaymentService;
import com.sfl.pms.services.payment.common.dto.adyen.AdyenPaymentResultDto;
import com.sfl.pms.services.payment.common.exception.PaymentNotFoundForUuIdException;
import com.sfl.pms.services.payment.common.impl.status.PaymentResultStatusMapper;
import com.sfl.pms.services.payment.common.model.Payment;
import com.sfl.pms.services.payment.common.model.PaymentResultStatus;
import com.sfl.pms.services.payment.method.model.PaymentMethodType;
import com.sfl.pms.services.payment.method.model.adyen.AdyenPaymentMethodType;
import com.sfl.pms.services.payment.processing.impl.PaymentResultProcessor;
import com.sfl.pms.services.payment.provider.adyen.AdyenPaymentProviderIntegrationService;
import com.sfl.pms.services.payment.redirect.adyen.AdyenRedirectResultService;
import com.sfl.pms.services.payment.redirect.dto.redirect.adyen.AdyenRedirectResultDto;
import com.sfl.pms.services.payment.redirect.model.PaymentProviderRedirectResultState;
import com.sfl.pms.services.payment.redirect.model.adyen.AdyenRedirectResult;
import com.sfl.pms.services.test.AbstractServicesUnitTest;
import org.easymock.EasyMock;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Test;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * User: Ruben Dilanyan
 * Company: SFL LLC
 * Date: 7/13/15
 * Time: 10:44 AM
 */
public class AdyenRedirectResultProcessorImplTest extends AbstractServicesUnitTest {

    /* Test subject and mocks */
    @TestSubject
    private AdyenRedirectResultProcessorImpl adyenRedirectResultProcessor = new AdyenRedirectResultProcessorImpl();

    @Mock
    private PaymentResultProcessor paymentResultProcessor;

    @Mock
    private AdyenPaymentProviderIntegrationService adyenPaymentProviderIntegrationService;

    @Mock
    private PaymentService paymentService;

    @Mock
    private PaymentResultStatusMapper paymentResultStatusMapper;

    @Mock
    private AdyenRedirectResultService adyenRedirectResultService;

    @Mock
    private PersistenceUtilityService persistenceUtilityService;

    /* Constructors */
    public AdyenRedirectResultProcessorImplTest() {
    }

    /* Test methods */
    @Test
    public void testProcessPaymentProviderRedirectResultWithInvalidArguments() {
        // Reset
        resetAll();
        // Replay
        replayAll();
        // Run test scenario
        try {
            adyenRedirectResultProcessor.processPaymentProviderRedirectResult(null);
            fail("Exception should be thrown");
        } catch (final IllegalArgumentException ex) {
            // Expected
        }
        // Verify
        verifyAll();
    }

    @Test
    public void testProcessPaymentProviderRedirectResultWithInvalidSignature() {
        // Test data
        final AdyenRedirectResultDto adyenRedirectResultDto = getServicesImplTestHelper().createAdyenRedirectResultDto();
        final AdyenRedirectResult adyenRedirectResult = getServicesImplTestHelper().createAdyenRedirectResult(adyenRedirectResultDto);
        // Reset
        resetAll();
        // Expectations
        expect(adyenPaymentProviderIntegrationService.verifySignatureForAdyenRedirectResult(eq(adyenRedirectResultDto))).andReturn(false).once();
        // Replay
        replayAll();
        // Run test scenario
        final PaymentProviderRedirectResultState redirectResultState = adyenRedirectResultProcessor.processPaymentProviderRedirectResult(adyenRedirectResult);
        assertEquals(PaymentProviderRedirectResultState.SIGNATURE_VERIFICATION_FAILED, redirectResultState);
        // Verify
        verifyAll();
    }

    @Test
    public void testProcessPaymentProviderRedirectResultWithInvalidPayment() {
        // Test data
        final AdyenRedirectResultDto adyenRedirectResultDto = getServicesImplTestHelper().createAdyenRedirectResultDto();
        final AdyenRedirectResult adyenRedirectResult = getServicesImplTestHelper().createAdyenRedirectResult(adyenRedirectResultDto);
        // Reset
        resetAll();
        // Expectations
        expect(adyenPaymentProviderIntegrationService.verifySignatureForAdyenRedirectResult(eq(adyenRedirectResultDto))).andReturn(true).once();
        expect(paymentService.getPaymentByUuId(eq(adyenRedirectResult.getMerchantReference()))).andThrow(new PaymentNotFoundForUuIdException(adyenRedirectResult.getMerchantReference(), Payment.class)).once();
        // Replay
        replayAll();
        // Run test scenario
        final PaymentProviderRedirectResultState redirectResultState = adyenRedirectResultProcessor.processPaymentProviderRedirectResult(adyenRedirectResult);
        assertEquals(PaymentProviderRedirectResultState.PAYMENT_LOOKUP_FAILED, redirectResultState);
        // Verify
        verifyAll();
    }

    @Test
    public void testProcessPaymentProviderRedirectResult() {
        // Test data
        final AdyenPaymentStatus adyenPaymentStatus = AdyenPaymentStatus.AUTHORISED;
        final AdyenRedirectResultDto adyenRedirectResultDto = getServicesImplTestHelper().createAdyenRedirectResultDto();
        adyenRedirectResultDto.setAuthResult(adyenPaymentStatus.getResult().toUpperCase());
        final Long adyenRedirectResultId = 2L;
        final AdyenRedirectResult adyenRedirectResult = getServicesImplTestHelper().createAdyenRedirectResult(adyenRedirectResultDto);
        adyenRedirectResult.setId(adyenRedirectResultId);
        final Long paymentId = 1L;
        final Payment payment = getServicesImplTestHelper().createOrderPayment();
        payment.setId(paymentId);
        final PaymentResultStatus paymentResultStatus = PaymentResultStatus.PAID;
        final AdyenPaymentResultDto expectedAdyenPaymentResultDto = new AdyenPaymentResultDto(paymentResultStatus, null, adyenRedirectResultDto.getPspReference(), adyenPaymentStatus.getResult(), null);
        // Reset
        resetAll();
        // Expectations
        expect(adyenPaymentProviderIntegrationService.verifySignatureForAdyenRedirectResult(eq(adyenRedirectResultDto))).andReturn(true).once();
        expect(paymentService.getPaymentByUuId(eq(adyenRedirectResult.getMerchantReference()))).andReturn(payment).once();
        expect(paymentResultStatusMapper.getPaymentResultStatusForAdyenPaymentStatus(eq(adyenPaymentStatus))).andReturn(paymentResultStatus).once();
        expect(paymentResultProcessor.processPaymentResult(eq(paymentId), isNull(), eq(adyenRedirectResult.getId()), eq(expectedAdyenPaymentResultDto))).andReturn(null).once();
        persistenceUtilityService.runInNewTransaction(isA(Runnable.class));
        expectLastCall().andAnswer(() -> {
            final Runnable runnable = (Runnable) getCurrentArguments()[0];
            runnable.run();
            return null;
        }).times(2);
        expect(persistenceUtilityService.initializeAndUnProxy(isA(Object.class))).andAnswer(() -> getCurrentArguments()[0]).once();
        expect(adyenRedirectResultService.updatePaymentForRedirectResult(eq(adyenRedirectResultId), eq(paymentId))).andReturn(adyenRedirectResult).once();
        expect(paymentService.updateConfirmedPaymentMethodType(eq(paymentId), EasyMock.eq(PaymentMethodType.getPaymentMethodTypeForAdyenPaymentMethod(AdyenPaymentMethodType.getAdyenPaymentMethodTypeForCode(adyenRedirectResult.getPaymentMethod()))))).andReturn(payment).once();
        // Replay
        replayAll();
        // Run test scenario
        final PaymentProviderRedirectResultState redirectResultState = adyenRedirectResultProcessor.processPaymentProviderRedirectResult(adyenRedirectResult);
        assertEquals(PaymentProviderRedirectResultState.PROCESSED, redirectResultState);
        // Verify
        verifyAll();
    }

}
