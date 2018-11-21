package com.sfl.pms.services.payment.provider.impl.acapture;

import com.sfl.pms.services.payment.common.dto.acapture.AcapturePaymentResultDto;
import com.sfl.pms.services.payment.redirect.model.acapture.AcaptureRedirectResult;

import javax.annotation.Nonnull;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 9/30/18
 * Time: 7:37 PM
 */
public interface AcapturePaymentProviderIntegrationService {

    /**
     * Creates checkout to open payment session on Acapture
     * @param paymentId
     * @return
     */
    @Nonnull
    String createCheckout(@Nonnull final Long paymentId);

    /**
     * Checks payment status for redirect result using API call to Acapture (IMPORTANT: api call can be made twice in an hour)
     * @param acaptureRedirectResult
     * @return
     */
    @Nonnull
    AcapturePaymentResultDto checkPaymentStatusForRedirectResult(@Nonnull final AcaptureRedirectResult acaptureRedirectResult);

    @Nonnull
    void submitRefund(@Nonnull final Long paymentId);

    @Nonnull
    void submitCapture(@Nonnull final Long paymentId);
}
