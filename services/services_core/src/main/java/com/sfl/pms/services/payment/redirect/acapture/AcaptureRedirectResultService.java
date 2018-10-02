package com.sfl.pms.services.payment.redirect.acapture;

import com.sfl.pms.services.payment.redirect.AbstractPaymentProviderRedirectResultService;
import com.sfl.pms.services.payment.redirect.dto.redirect.acapture.AcaptureRedirectResultDto;
import com.sfl.pms.services.payment.redirect.model.acapture.AcaptureRedirectResult;

import javax.annotation.Nonnull;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 9/30/18
 * Time: 11:06 PM
 */
public interface AcaptureRedirectResultService extends AbstractPaymentProviderRedirectResultService<AcaptureRedirectResult> {

    /**
     * Creates payment provider redirect result
     *
     * @param redirectResultDto
     * @return redirectResult
     */
    @Nonnull
    AcaptureRedirectResult createPaymentProviderRedirectResult(@Nonnull final AcaptureRedirectResultDto redirectResultDto);

}
