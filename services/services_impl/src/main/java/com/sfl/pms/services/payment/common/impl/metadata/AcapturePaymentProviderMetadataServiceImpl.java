package com.sfl.pms.services.payment.common.impl.metadata;

import com.sfl.pms.persistence.repositories.payment.common.metadata.acapture.AcapturePaymentProviderMetadataRepository;
import com.sfl.pms.services.payment.common.model.acapture.AcapturePaymentProviderMetadata;
import com.sfl.pms.services.payment.metadata.acapture.AcapturePaymentProviderMetadataService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Nonnull;
import javax.persistence.EntityNotFoundException;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 10/1/18
 * Time: 2:45 AM
 */
@Service
public class AcapturePaymentProviderMetadataServiceImpl implements AcapturePaymentProviderMetadataService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AcapturePaymentProviderMetadataServiceImpl.class);

    /* Dependencies */
    @Autowired
    private AcapturePaymentProviderMetadataRepository acapturePaymentProviderMetadataRepository;

    /* Constructor */
    public AcapturePaymentProviderMetadataServiceImpl() {
        LOGGER.debug("Initializing acapture payment provider metadata service");
    }

    /* Public methods */
    @Nonnull
    @Override
    public AcapturePaymentProviderMetadata getAcapturePaymentProviderMetadataByCheckoutId(@Nonnull final String checkoutId) {
        Assert.notNull(checkoutId, "Checkout id should not be null");
        final AcapturePaymentProviderMetadata acapturePaymentProviderMetadata = acapturePaymentProviderMetadataRepository.findByCheckoutId(checkoutId);
        assertAcapturePaymentProviderMetadataFoundForCheckoutId(acapturePaymentProviderMetadata, checkoutId);
        return acapturePaymentProviderMetadata;
    }

    /* Utility methods */
    private void assertAcapturePaymentProviderMetadataFoundForCheckoutId(final AcapturePaymentProviderMetadata acapturePaymentProviderMetadata, final String checkoutId) {
        if(acapturePaymentProviderMetadata == null) {
            LOGGER.debug("Could not find AcapturePaymentProviderMetadata for checkout id - {}", checkoutId);
            throw new EntityNotFoundException("Could not find AcapturePaymentProviderMetadata for checkout id - "+ checkoutId);
        }
    }
}
