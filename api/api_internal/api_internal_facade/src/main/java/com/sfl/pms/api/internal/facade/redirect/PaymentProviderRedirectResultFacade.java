package com.sfl.pms.api.internal.facade.redirect;

import com.sfl.pms.core.api.internal.model.common.result.ResultResponseModel;
import com.sfl.pms.core.api.internal.model.redirect.request.CreateAcaptureRedirectResultRequest;
import com.sfl.pms.core.api.internal.model.redirect.request.CreateAdyenRedirectResultRequest;
import com.sfl.pms.core.api.internal.model.redirect.request.GetPaymentProviderRedirectResultStatusRequest;
import com.sfl.pms.core.api.internal.model.redirect.response.CreateAcaptureRedirectResultResponse;
import com.sfl.pms.core.api.internal.model.redirect.response.CreateAdyenRedirectResultResponse;
import com.sfl.pms.core.api.internal.model.redirect.response.GetPaymentProviderRedirectResultStatusResponse;

import javax.annotation.Nonnull;

/**
 * User: Ruben Dilanyan
 * Company: SFL LLC
 * Date: 1/19/16
 * Time: 11:26 AM
 */
public interface PaymentProviderRedirectResultFacade {

    /**
     * Creates Adyen redirect result
     *
     * @param request
     * @return response
     */
    @Nonnull
    ResultResponseModel<CreateAdyenRedirectResultResponse> createAdyenRedirectResult(@Nonnull final CreateAdyenRedirectResultRequest request);

    /**
     * Creates Acapture redirect result
     * @param request
     * @return
     */
    @Nonnull
    ResultResponseModel<CreateAcaptureRedirectResultResponse> createAcaptureRedirectResult(@Nonnull final CreateAcaptureRedirectResultRequest request);


    /**
     * Gets redirect response status
     *
     * @param request
     * @return response
     */
    @Nonnull
    ResultResponseModel<GetPaymentProviderRedirectResultStatusResponse> getRedirectResultStatus(@Nonnull final GetPaymentProviderRedirectResultStatusRequest request);
}
