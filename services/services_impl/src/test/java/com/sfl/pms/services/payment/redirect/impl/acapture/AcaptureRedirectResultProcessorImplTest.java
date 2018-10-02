package com.sfl.pms.services.payment.redirect.impl.acapture;

import com.sfl.pms.services.test.AbstractServicesUnitTest;
import org.easymock.TestSubject;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 10/1/18
 * Time: 2:21 AM
 */
public class AcaptureRedirectResultProcessorImplTest extends AbstractServicesUnitTest {

    /* Test subject and mocks */
    @TestSubject
    private AcaptureRedirectResultProcessorImpl acaptureRedirectResultProcessor = new AcaptureRedirectResultProcessorImpl();

    /* Constructor */
    public AcaptureRedirectResultProcessorImplTest() {
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
            acaptureRedirectResultProcessor.processPaymentProviderRedirectResult(null);
            fail("Exception should be thrown");
        } catch (IllegalArgumentException ex) {
            //success
        }
        try {
            acaptureRedirectResultProcessor.processPaymentProviderRedirectResult(getServicesImplTestHelper().createAdyenRedirectResult());
            fail("Exception should be thrown");
        } catch (IllegalArgumentException ex) {
            //success
        }
        // Verify
        verifyAll();
    }
}