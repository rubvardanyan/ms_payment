package com.sfl.pms.services.payment.metadata.acapture;

import com.sfl.pms.services.payment.common.model.acapture.AcapturePaymentProviderMetadata;
import com.sfl.pms.services.test.AbstractServiceIntegrationTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 10/1/18
 * Time: 2:57 AM
 */
public class AcapturePaymentProviderMetadataServiceIntegrationTest extends AbstractServiceIntegrationTest {

    /* Dependencies */
    @Autowired
    private AcapturePaymentProviderMetadataService acapturePaymentProviderMetadataService;

    /* Constructor */
    public AcapturePaymentProviderMetadataServiceIntegrationTest() {
    }

    /* Test methods */
    @Test
    public void testGetAcapturePaymentProviderMetadataByCheckoutId() {
        // Prepare
        final AcapturePaymentProviderMetadata acapturePaymentProviderMetadata = getServicesTestHelper().createAcapturePaymentProviderMetadata();
        AcapturePaymentProviderMetadata result = acapturePaymentProviderMetadataService.getAcapturePaymentProviderMetadataByCheckoutId(acapturePaymentProviderMetadata.getCheckoutId());
        assertEquals(acapturePaymentProviderMetadata, result);
        flushAndClear();
        // Reload metadata
        result = acapturePaymentProviderMetadataService.getAcapturePaymentProviderMetadataByCheckoutId(acapturePaymentProviderMetadata.getCheckoutId());
        assertEquals(acapturePaymentProviderMetadata, result);
    }
}
