package com.sfl.pms.api.internal.rest.resources.order;

import com.sfl.pms.api.internal.facade.order.OrderPaymentFacade;
import com.sfl.pms.core.api.internal.model.common.result.ResultResponseModel;
import com.sfl.pms.core.api.internal.model.order.request.CreateOrderRequest;
import com.sfl.pms.core.api.internal.model.order.request.GetOrderPaymentRequestStatusRequest;
import com.sfl.pms.core.api.internal.model.order.request.RePayOrderRequest;
import com.sfl.pms.core.api.internal.model.order.request.CreateOrderRefundRequest;
import com.sfl.pms.core.api.internal.model.order.response.CreateOrderPaymentResponse;
import com.sfl.pms.core.api.internal.model.order.response.GetOrderPaymentRequestStatusResponse;
import com.sfl.pms.core.api.internal.model.order.response.CreateOrderRefundResponse;
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
 * Time: 3:35 PM
 */
@Component
@Path("payment/order")
@Produces("application/json")
public class OrderPaymentResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderPaymentResource.class);

    /* Dependencies */
    @Autowired
    private OrderPaymentFacade orderPaymentFacade;

    /* Constructors */
    public OrderPaymentResource() {
    }

    @POST
    @Path("/create")
    public Response createOrder(final CreateOrderRequest request) {
        LOGGER.debug("Got order payment request - {}", request);
        final ResultResponseModel<CreateOrderPaymentResponse> result = orderPaymentFacade.createOrder(request);
        LOGGER.debug("Successfully retrieved order payment result - {}, for request - {}", result, request);
        return Response.ok(result).build();
    }

    @POST
    @Path("/repay")
    public Response rePayOrder(final RePayOrderRequest request) {
        LOGGER.debug("Got order rePayment request - {}", request);
        final ResultResponseModel<CreateOrderPaymentResponse> result = orderPaymentFacade.rePayOrder(request);
        LOGGER.debug("Successfully retrieved order rePayment result - {}, for request - {}", result, request);
        return Response.ok(result).build();
    }

    @POST
    @Path("/status")
    public Response getOrderPaymentStatus(final GetOrderPaymentRequestStatusRequest request) {
        LOGGER.debug("Got order payment status retrieval request - {}", request);
        final ResultResponseModel<GetOrderPaymentRequestStatusResponse> result = orderPaymentFacade.getOrderPaymentRequestStatus(request);
        LOGGER.debug("Successfully processed order payment status retrieval request - {}", request);
        return Response.ok(result).build();
    }

    @POST
    @Path("/refund")
    public Response refundOrder(final CreateOrderRefundRequest request) {
        LOGGER.debug("Refund order request - {}", request);
        final ResultResponseModel<CreateOrderRefundResponse> result = orderPaymentFacade.refundOrder(request);
        LOGGER.debug("Successfully processed refund order request - {}", request);
        return Response.ok(result).build();
    }
}
