package com.sfl.pms.services.payment.metadata.acapture;

import com.sfl.pms.services.payment.common.model.acapture.AcapturePaymentProviderMetadata;

import javax.annotation.Nonnull;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 10/1/18
 * Time: 2:43 AM
 */
public interface AcapturePaymentProviderMetadataService {

    @Nonnull
    AcapturePaymentProviderMetadata getAcapturePaymentProviderMetadataByCheckoutId(@Nonnull final String checkoutId);
}
