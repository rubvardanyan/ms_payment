package com.sfl.pms.api.internal.client.rest.redirect.impl;

import com.sfl.pms.api.internal.client.rest.common.AbstractResourceClient;
import com.sfl.pms.api.internal.client.rest.redirect.PaymentProviderRedirectResultResourceClient;
import com.sfl.pms.core.api.internal.model.common.result.ResultResponseModel;
import com.sfl.pms.core.api.internal.model.redirect.request.CreateAcaptureRedirectResultRequest;
import com.sfl.pms.core.api.internal.model.redirect.request.CreateAdyenRedirectResultRequest;
import com.sfl.pms.core.api.internal.model.redirect.request.GetPaymentProviderRedirectResultStatusRequest;
import com.sfl.pms.core.api.internal.model.redirect.response.CreateAcaptureRedirectResultResponse;
import com.sfl.pms.core.api.internal.model.redirect.response.CreateAdyenRedirectResultResponse;
import com.sfl.pms.core.api.internal.model.redirect.response.GetPaymentProviderRedirectResultStatusResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

/**
 * User: Arthur Asatryan
 * Company: SFL LLC
 * Date: 2/21/16
 * Time: 7:32 PM
 */
public class PaymentProviderRedirectResultResourceClientImpl extends AbstractResourceClient implements PaymentProviderRedirectResultResourceClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentProviderRedirectResultResourceClientImpl.class);

    //region Constants
    private static final String RESOURCE_BASE_PATH = "provider/redirect/result";

    private static final String PROCESS_ADYEN_REDIRECT_RESULT_PATH = "adyen/create";

    private static final String PROCESS_ACAPTURE_REDIRECT_RESULT_PATH = "acapture/create";

    private static final String GET_PAYMENT_PROVIDER_REDIRECT_RESULT_STATUS_PATH = "status";
    //endregion

    //region Constructors
    public PaymentProviderRedirectResultResourceClientImpl(final Client client, final String apiPath) {
        super(client, apiPath);
        LOGGER.debug("Initializing payment provider redirect result resource client");
    }
    //endregion

    //region Public methods

    @Nonnull
    @Override
    public ResultResponseModel<CreateAdyenRedirectResultResponse> createAdyenRedirectResult(@Nonnull final CreateAdyenRedirectResultRequest request) {
        return getClient()
                .target(getApiPath())
                .path(RESOURCE_BASE_PATH)
                .path(PROCESS_ADYEN_REDIRECT_RESULT_PATH)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(request, MediaType.APPLICATION_JSON_TYPE), new GenericType<ResultResponseModel<CreateAdyenRedirectResultResponse>>() {
                });
    }

    @Nonnull
    @Override
    public ResultResponseModel<CreateAcaptureRedirectResultResponse> createAcaptureRedirectResult(@Nonnull final CreateAcaptureRedirectResultRequest request) {
        return getClient()
                .target(getApiPath())
                .path(RESOURCE_BASE_PATH)
                .path(PROCESS_ACAPTURE_REDIRECT_RESULT_PATH)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(request, MediaType.APPLICATION_JSON_TYPE), new GenericType<ResultResponseModel<CreateAcaptureRedirectResultResponse>>() {
                });
    }

    @Nonnull
    @Override
    public ResultResponseModel<GetPaymentProviderRedirectResultStatusResponse> getRedirectResultStatus(@Nonnull final GetPaymentProviderRedirectResultStatusRequest request) {
        return getClient()
                .target(getApiPath())
                .path(RESOURCE_BASE_PATH)
                .path(GET_PAYMENT_PROVIDER_REDIRECT_RESULT_STATUS_PATH)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(request, MediaType.APPLICATION_JSON_TYPE), new GenericType<ResultResponseModel<GetPaymentProviderRedirectResultStatusResponse>>() {
                });
    }
    //endregion
}
