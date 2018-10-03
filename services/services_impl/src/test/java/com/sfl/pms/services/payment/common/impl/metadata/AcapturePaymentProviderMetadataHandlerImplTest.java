package com.sfl.pms.services.payment.common.impl.metadata;

import com.sfl.pms.services.payment.common.dto.acapture.AcapturePaymentProviderMetadataDto;
import com.sfl.pms.services.payment.common.dto.metadata.PaymentProviderMetadataDto;
import com.sfl.pms.services.payment.common.model.acapture.AcapturePaymentProviderMetadata;
import com.sfl.pms.services.payment.common.model.metadata.PaymentProviderMetadata;
import com.sfl.pms.services.test.AbstractServicesUnitTest;
import org.easymock.TestSubject;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 9/29/18
 * Time: 3:01 AM
 */
public class AcapturePaymentProviderMetadataHandlerImplTest extends AbstractServicesUnitTest {

    /* Test subject and mocks */
    @TestSubject
    private AcapturePaymentProviderMetadataHandlerImpl acapturePaymentProviderMetadataHandler = new AcapturePaymentProviderMetadataHandlerImpl();

    /* Constructor */
    public AcapturePaymentProviderMetadataHandlerImplTest() {
    }

    /* Test methods */
    @Test
    public void testConvertPaymentProviderMetadataDtoWithInvalidArguments() {
        // Reset
        resetAll();
        // Replay
        replayAll();
        // Run test scenario
        try {
            acapturePaymentProviderMetadataHandler.convertPaymentProviderMetadataDto(null);
            fail("Exception should be thrown");
        } catch (IllegalArgumentException ex) {
            //success
        }
        // Verify
        verifyAll();
    }

    @Test
    public void testConvertPaymentProviderMetadataDto() {
        // Test data
        final AcapturePaymentProviderMetadataDto dto = getServicesImplTestHelper().createAcapturePaymentProviderMetadataDto();
        // Reset
        resetAll();
        // Replay
        replayAll();
        // Run test scenario
        final PaymentProviderMetadata acapturePaymentProviderMetadata = acapturePaymentProviderMetadataHandler.convertPaymentProviderMetadataDto(dto);
        assertTrue(acapturePaymentProviderMetadata instanceof AcapturePaymentProviderMetadata);
        final AcapturePaymentProviderMetadata result = (AcapturePaymentProviderMetadata) acapturePaymentProviderMetadata;
        getServicesImplTestHelper().assertAcapturePaymentProviderMetadataDto(result, dto);
        // Verify
        verifyAll();
    }
}