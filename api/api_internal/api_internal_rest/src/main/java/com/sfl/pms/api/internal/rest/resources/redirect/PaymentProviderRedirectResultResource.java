package com.sfl.pms.api.internal.rest.resources.redirect;

import com.sfl.pms.api.internal.facade.redirect.PaymentProviderRedirectResultFacade;
import com.sfl.pms.core.api.internal.model.common.result.ResultResponseModel;
import com.sfl.pms.core.api.internal.model.redirect.request.CreateAcaptureRedirectResultRequest;
import com.sfl.pms.core.api.internal.model.redirect.request.CreateAdyenRedirectResultRequest;
import com.sfl.pms.core.api.internal.model.redirect.request.GetPaymentProviderRedirectResultStatusRequest;
import com.sfl.pms.core.api.internal.model.redirect.response.CreateAcaptureRedirectResultResponse;
import com.sfl.pms.core.api.internal.model.redirect.response.CreateAdyenRedirectResultResponse;
import com.sfl.pms.core.api.internal.model.redirect.response.GetPaymentProviderRedirectResultStatusResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * User: Ruben Dilanyan
 * Company: SFL LLC
 * Date: 1/19/16
 * Time: 12:09 PM
 */
@Component
@Path("provider/redirect/result")
@Produces("application/json")
public class PaymentProviderRedirectResultResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentProviderRedirectResultResource.class);

    /* Dependencies */
    @Autowired
    private PaymentProviderRedirectResultFacade paymentProviderRedirectResultFacade;

    /* Constructors */
    public PaymentProviderRedirectResultResource() {
    }

    @POST
    @Path("/adyen/create")
    public Response processAdyenRedirectResult(final CreateAdyenRedirectResultRequest request) {
        LOGGER.debug("Got payment provider redirect result, request - {}", request);
        final ResultResponseModel<CreateAdyenRedirectResultResponse> result = paymentProviderRedirectResultFacade.createAdyenRedirectResult(request);
        LOGGER.debug("Successfully retrieved result - {}, for request - {}", result, request);
        return Response.ok(result).build();
    }

    @POST
    @Path("acapture/create")
    public Response processAcaptureRedirectResult(final CreateAcaptureRedirectResultRequest request) {
        LOGGER.debug("Got payment provider redirect result, request - {}", request);
        final ResultResponseModel<CreateAcaptureRedirectResultResponse> result = paymentProviderRedirectResultFacade.createAcaptureRedirectResult(request);
        LOGGER.debug("Successfully retrieved result - {}, for request - {}", result, request);
        return Response.ok(result).build();
    }

    @POST
    @Path("/status")
    public Response getPaymentProviderRedirectResultStatus(final GetPaymentProviderRedirectResultStatusRequest request) {
        LOGGER.debug("Got payment provider redirect result status, request - {}", request);
        final ResultResponseModel<GetPaymentProviderRedirectResultStatusResponse> result = paymentProviderRedirectResultFacade.getRedirectResultStatus(request);
        LOGGER.debug("Successfully retrieved result - {}, for request - {}", result, request);
        return Response.ok(result).build();
    }
}
