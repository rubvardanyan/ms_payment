package com.sfl.pms.services.payment.provider.acapture;

import javax.annotation.Nonnull;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 9/30/18
 * Time: 7:37 PM
 */
public interface AcapturePaymentProviderIntegrationService {

    @Nonnull
    String createCheckout(@Nonnull final Long paymentId);

}
