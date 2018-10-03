package com.sfl.pms.services.payment.redirect.impl;

import com.sfl.pms.persistence.repositories.payment.redirect.AbstractPaymentProviderRedirectResultRepository;
import com.sfl.pms.persistence.repositories.payment.redirect.PaymentProviderRedirectResultRepository;
import com.sfl.pms.services.payment.redirect.AbstractPaymentProviderRedirectResultService;
import com.sfl.pms.services.payment.redirect.exception.PaymentProviderRedirectResultNotFoundForIdException;
import com.sfl.pms.services.payment.redirect.exception.PaymentProviderRedirectResultStateNotAllowedException;
import com.sfl.pms.services.payment.redirect.model.PaymentProviderRedirectResult;
import com.sfl.pms.services.payment.redirect.model.PaymentProviderRedirectResultState;
import com.sfl.pms.services.payment.redirect.model.acapture.AcaptureRedirectResult;
import com.sfl.pms.services.payment.redirect.model.adyen.AdyenRedirectResult;
import com.sfl.pms.services.util.mutable.MutableHolder;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

/**
 * User: Ruben Dilanyan
 * Company: SFL LLC
 * Date: 7/11/15
 * Time: 12:53 PM
 */
public class PaymentProviderRedirectResultServiceImplTest extends AbstractPaymentProviderRedirectResultServiceImplTest<PaymentProviderRedirectResult> {

    /* Test subject and mocks */
    @TestSubject
    private PaymentProviderRedirectResultServiceImpl paymentProviderRedirectResultService = new PaymentProviderRedirectResultServiceImpl();

    @Mock
    private PaymentProviderRedirectResultRepository paymentProviderRedirectResultRepository;

    /* Constructors */
    public PaymentProviderRedirectResultServiceImplTest() {
    }

    /* Test methods */
    @Test
    public void testUpdatePaymentProviderRedirectResultStateWithInvalidArguments() {
        // Test data
        final Long redirectResultId = 1l;
        final PaymentProviderRedirectResultState state = PaymentProviderRedirectResultState.PROCESSING;
        final Set<PaymentProviderRedirectResultState> allowedStates = new LinkedHashSet<>(Arrays.asList(PaymentProviderRedirectResultState.CREATED));
        // Reset
        resetAll();
        // Replay
        replayAll();
        // Run test scenario
        try {
            paymentProviderRedirectResultService.updatePaymentProviderRedirectResultState(null, state, allowedStates);
            fail("Exception should be thrown");
        } catch (final IllegalArgumentException ex) {
            // Expected
        }
        try {
            paymentProviderRedirectResultService.updatePaymentProviderRedirectResultState(redirectResultId, null, allowedStates);
            fail("Exception should be thrown");
        } catch (final IllegalArgumentException ex) {
            // Expected
        }
        try {
            paymentProviderRedirectResultService.updatePaymentProviderRedirectResultState(redirectResultId, state, null);
            fail("Exception should be thrown");
        } catch (final IllegalArgumentException ex) {
            // Expected
        }
        // Verify
        verifyAll();
    }

    @Test
    public void testUpdatePaymentProviderRedirectResultWithNotExistingRequestId() {
        // Test data
        final Long redirectResultId = 1l;
        final PaymentProviderRedirectResultState state = PaymentProviderRedirectResultState.PROCESSING;
        final Set<PaymentProviderRedirectResultState> allowedStates = new LinkedHashSet<>(Arrays.asList(PaymentProviderRedirectResultState.CREATED));
        // Reset
        resetAll();
        // Expectations
        expect(paymentProviderRedirectResultRepository.findByIdWithPessimisticWriteLock(eq(redirectResultId), AdyenRedirectResult.class)).andReturn(null).once();
        // Replay
        replayAll();
        // Run test scenario
        try {
            paymentProviderRedirectResultService.updatePaymentProviderRedirectResultState(redirectResultId, state, allowedStates);
            fail("Exception should be thrown");
        } catch (final PaymentProviderRedirectResultNotFoundForIdException ex) {
            // Expected
            assertPaymentProviderRedirectResultNotFoundForIdException(ex, redirectResultId);
        }
        // Verify
        verifyAll();
    }

    @Test
    public void testUpdatePaymentProviderRedirectResultNotAllowedState() {
        // Test data
        final Long redirectResultId = 1l;
        final PaymentProviderRedirectResultState state = PaymentProviderRedirectResultState.PROCESSING;
        final PaymentProviderRedirectResult redirectResult = getInstance();
        final PaymentProviderRedirectResultState initialState = redirectResult.getState();
        final MutableHolder<PaymentProviderRedirectResultState> notAllowedStateHolder = new MutableHolder<>(null);
        Arrays.asList(PaymentProviderRedirectResultState.values()).forEach(currentState -> {
            if (!currentState.equals(initialState)) {
                notAllowedStateHolder.setValue(currentState);
            }
        });
        final Set<PaymentProviderRedirectResultState> allowedStates = new LinkedHashSet<>(Arrays.asList(notAllowedStateHolder.getValue()));
        // Reset
        resetAll();
        // Expectations
        expect(paymentProviderRedirectResultRepository.findByIdWithPessimisticWriteLock(eq(redirectResultId), PaymentProviderRedirectResult.class)).andReturn(redirectResult).once();
        // Replay
        replayAll();
        // Run test scenario
        try {
            paymentProviderRedirectResultService.updatePaymentProviderRedirectResultState(redirectResultId, state, allowedStates);
            fail("Exception should be thrown");
        } catch (final PaymentProviderRedirectResultStateNotAllowedException ex) {
            // Expected
            assertPaymentProviderRedirectResultStateNotAllowedException(ex, state, initialState, allowedStates);
        }
        // Verify
        verifyAll();
    }

    @Test
    public void testUpdatePaymentProviderRedirectResultState() {
        // Test data
        final Long redirectResultId = 1l;
        final PaymentProviderRedirectResultState state = PaymentProviderRedirectResultState.PROCESSING;
        final PaymentProviderRedirectResult redirectResult = getInstance();
        final PaymentProviderRedirectResultState initialState = redirectResult.getState();
        final Set<PaymentProviderRedirectResultState> allowedStates = new LinkedHashSet<>(Arrays.asList(initialState));
        final Date requestUpdated = redirectResult.getUpdated();
        // Reset
        resetAll();
        // Expectations
        expect(paymentProviderRedirectResultRepository.findByIdWithPessimisticWriteLock(eq(redirectResultId), PaymentProviderRedirectResult.class)).andReturn(redirectResult).once();
        expect(paymentProviderRedirectResultRepository.save(isA(PaymentProviderRedirectResult.class))).andAnswer(() -> (PaymentProviderRedirectResult) getCurrentArguments()[0]).once();
        // Replay
        replayAll();
        // Run test scenario
        final PaymentProviderRedirectResult result = paymentProviderRedirectResultService.updatePaymentProviderRedirectResultState(redirectResultId, state, allowedStates);
        assertNotNull(result);
        assertEquals(state, result.getState());
        assertTrue(result.getUpdated().compareTo(requestUpdated) >= 0 && requestUpdated != result.getUpdated());
        // Verify
        verifyAll();
    }

    /* Utility methods */
    @Override
    protected AbstractPaymentProviderRedirectResultRepository<PaymentProviderRedirectResult> getRepository() {
        return paymentProviderRedirectResultRepository;
    }

    @Override
    protected AbstractPaymentProviderRedirectResultService<PaymentProviderRedirectResult> getService() {
        return paymentProviderRedirectResultService;
    }

    @Override
    protected PaymentProviderRedirectResult getInstance() {
        return getServicesImplTestHelper().createAdyenRedirectResult();
    }

    @Override
    protected Class<PaymentProviderRedirectResult> getInstanceClass() {
        return PaymentProviderRedirectResult.class;
    }

    private void assertPaymentProviderRedirectResultStateNotAllowedException(final PaymentProviderRedirectResultStateNotAllowedException ex, final PaymentProviderRedirectResultState requiredState, final PaymentProviderRedirectResultState requestState, final Set<PaymentProviderRedirectResultState> allowedStates) {
        assertEquals(requestState, ex.getRequestState());
        assertEquals(requiredState, ex.getRequiredState());
        assertEquals(allowedStates, ex.getAllowedStates());
    }
}
