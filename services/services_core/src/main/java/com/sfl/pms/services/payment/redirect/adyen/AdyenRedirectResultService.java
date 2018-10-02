package com.sfl.pms.services.payment.redirect.adyen;

import com.sfl.pms.services.payment.redirect.AbstractPaymentProviderRedirectResultService;
import com.sfl.pms.services.payment.redirect.dto.redirect.adyen.AdyenRedirectResultDto;
import com.sfl.pms.services.payment.redirect.model.adyen.AdyenRedirectResult;

import javax.annotation.Nonnull;

/**
 * User: Ruben Dilanyan
 * Company: SFL LLC
 * Date: 7/11/15
 * Time: 12:35 PM
 */
public interface AdyenRedirectResultService extends AbstractPaymentProviderRedirectResultService<AdyenRedirectResult> {

    /**
     * Creates payment provider redirect result
     *
     * @param redirectResultDto
     * @return redirectResult
     */
    @Nonnull
    AdyenRedirectResult createPaymentProviderRedirectResult(@Nonnull final AdyenRedirectResultDto redirectResultDto);
}
