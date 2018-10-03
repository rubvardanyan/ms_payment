package com.sfl.pms.api.internal.client.rest.redirect;

import com.sfl.pms.core.api.internal.model.common.result.ResultResponseModel;
import com.sfl.pms.core.api.internal.model.redirect.request.CreateAcaptureRedirectResultRequest;
import com.sfl.pms.core.api.internal.model.redirect.request.CreateAdyenRedirectResultRequest;
import com.sfl.pms.core.api.internal.model.redirect.request.GetPaymentProviderRedirectResultStatusRequest;
import com.sfl.pms.core.api.internal.model.redirect.response.CreateAcaptureRedirectResultResponse;
import com.sfl.pms.core.api.internal.model.redirect.response.CreateAdyenRedirectResultResponse;
import com.sfl.pms.core.api.internal.model.redirect.response.GetPaymentProviderRedirectResultStatusResponse;

import javax.annotation.Nonnull;

/**
 * User: Arthur Asatryan
 * Company: SFL LLC
 * Date: 2/21/16
 * Time: 7:19 PM
 */
public interface PaymentProviderRedirectResultResourceClient {

    /**
     * Creates new Adyen redirect result
     *
     * @param request
     * @return response
     */
    @Nonnull
    ResultResponseModel<CreateAdyenRedirectResultResponse> createAdyenRedirectResult(@Nonnull final CreateAdyenRedirectResultRequest request);

    /**
     * Creates new Adyen redirect result
     *
     * @param request
     * @return response
     */
    @Nonnull
    ResultResponseModel<CreateAcaptureRedirectResultResponse> createAcaptureRedirectResult(@Nonnull final CreateAcaptureRedirectResultRequest request);

    /**
     * Gets redirect result status
     *
     * @param request
     * @return response
     */
    @Nonnull
    ResultResponseModel<GetPaymentProviderRedirectResultStatusResponse> getRedirectResultStatus(@Nonnull final GetPaymentProviderRedirectResultStatusRequest request);
}
