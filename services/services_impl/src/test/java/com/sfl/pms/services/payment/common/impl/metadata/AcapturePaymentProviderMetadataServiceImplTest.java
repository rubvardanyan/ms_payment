package com.sfl.pms.services.payment.common.impl.metadata;

import com.sfl.pms.persistence.repositories.payment.common.metadata.acapture.AcapturePaymentProviderMetadataRepository;
import com.sfl.pms.services.payment.common.model.acapture.AcapturePaymentProviderMetadata;
import com.sfl.pms.services.test.AbstractServicesUnitTest;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Test;

import javax.persistence.EntityNotFoundException;
import java.util.UUID;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.*;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 10/1/18
 * Time: 2:50 AM
 */
public class AcapturePaymentProviderMetadataServiceImplTest extends AbstractServicesUnitTest {

    /* Test subject and mocks */
    @TestSubject
    private AcapturePaymentProviderMetadataServiceImpl acapturePaymentProviderMetadataService = new AcapturePaymentProviderMetadataServiceImpl();

    @Mock
    private AcapturePaymentProviderMetadataRepository acapturePaymentProviderMetadataRepository;

    /* Constructor */
    public AcapturePaymentProviderMetadataServiceImplTest() {
    }

    /* Test methods */

    @Test
    public void testGetAcapturePaymentProviderMetadataByCheckoutIdWithInvalidArguments() {
        // Reset
        resetAll();
        // Replay
        replayAll();
        // Run test scenario
        try {
            acapturePaymentProviderMetadataService.getAcapturePaymentProviderMetadataByCheckoutId(null);
            fail("Exception should be thrown");
        } catch (IllegalArgumentException ex) {
            //success
        }
        // Verify
        verifyAll();
    }

    @Test
    public void testGetAcapturePaymentProviderMetadataByCheckoutIdWhenDoesNotExist() {
        // Test data
        final String checkoutId = UUID.randomUUID().toString();
        // Reset
        resetAll();
        // Expectations
        expect(acapturePaymentProviderMetadataRepository.findByCheckoutId(checkoutId)).andReturn(null);
        // Replay
        replayAll();
        // Run test scenario
        try {
            acapturePaymentProviderMetadataService.getAcapturePaymentProviderMetadataByCheckoutId(checkoutId);
            fail("Exception should be thrown");
        } catch (EntityNotFoundException ex) {
            //success
        }
        // Verify
        verifyAll();
    }

    @Test
    public void testGetAcapturePaymentProviderMetadataByCheckoutId() {
        // Test data
        final String checkoutId = UUID.randomUUID().toString();
        final AcapturePaymentProviderMetadata metadata = new AcapturePaymentProviderMetadata();
        metadata.setCheckoutId(checkoutId);
        // Reset
        resetAll();
        // Expectations
        expect(acapturePaymentProviderMetadataRepository.findByCheckoutId(checkoutId)).andReturn(metadata);
        // Replay
        replayAll();
        // Run test scenario
        final AcapturePaymentProviderMetadata result = acapturePaymentProviderMetadataService.getAcapturePaymentProviderMetadataByCheckoutId(checkoutId);
        assertNotNull(result);
        assertEquals(metadata, result);
        // Verify
        verifyAll();
    }
}