package com.sfl.pms.api.internal.client.rest.order.impl;

import com.sfl.pms.api.internal.client.rest.common.AbstractResourceClient;
import com.sfl.pms.api.internal.client.rest.order.OrderPaymentResourceClient;
import com.sfl.pms.core.api.internal.model.common.result.ResultResponseModel;
import com.sfl.pms.core.api.internal.model.order.request.CreateOrderRefundRequest;
import com.sfl.pms.core.api.internal.model.order.request.CreateOrderRequest;
import com.sfl.pms.core.api.internal.model.order.request.GetOrderPaymentRequestStatusRequest;
import com.sfl.pms.core.api.internal.model.order.request.RePayOrderRequest;
import com.sfl.pms.core.api.internal.model.order.response.CreateOrderPaymentResponse;
import com.sfl.pms.core.api.internal.model.order.response.CreateOrderRefundResponse;
import com.sfl.pms.core.api.internal.model.order.response.GetOrderPaymentRequestStatusResponse;
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
 * Time: 7:25 PM
 */
public class OrderPaymentResourceClientImpl extends AbstractResourceClient implements OrderPaymentResourceClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderPaymentResourceClientImpl.class);

    //region Constants
    private static final String RESOURCE_BASE_PATH = "payment/order";

    private static final String CREATE_ORDER_PATH = "create";

    private static final String REFUND_ORDER_PATH = "refund";

    private static final String REPAY_ORDER_PATH = "repay";

    private static final String GET_ORDER_PAYMENT_STATUS_PATH = "status";
    //endregion

    //region Constructors
    public OrderPaymentResourceClientImpl(final Client client, final String apiPath) {
        super(client, apiPath);
        LOGGER.debug("Initializing order payment resource client");
    }
    //endregion

    //region Public methods
    @Nonnull
    @Override
    public ResultResponseModel<CreateOrderPaymentResponse> createOrder(@Nonnull final CreateOrderRequest request) {
        return getClient()
                .target(getApiPath())
                .path(RESOURCE_BASE_PATH)
                .path(CREATE_ORDER_PATH)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(request, MediaType.APPLICATION_JSON_TYPE), new GenericType<ResultResponseModel<CreateOrderPaymentResponse>>() {
                });
    }

    @Nonnull
    @Override
    public ResultResponseModel<CreateOrderPaymentResponse> rePayOrder(@Nonnull final RePayOrderRequest request) {
        return getClient()
                .target(getApiPath())
                .path(RESOURCE_BASE_PATH)
                .path(REPAY_ORDER_PATH)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(request, MediaType.APPLICATION_JSON_TYPE), new GenericType<ResultResponseModel<CreateOrderPaymentResponse>>() {
                });
    }

    @Nonnull
    @Override
    public ResultResponseModel<GetOrderPaymentRequestStatusResponse> getOrderPaymentStatus(@Nonnull final GetOrderPaymentRequestStatusRequest request) {
        return getClient()
                .target(getApiPath())
                .path(RESOURCE_BASE_PATH)
                .path(GET_ORDER_PAYMENT_STATUS_PATH)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(request, MediaType.APPLICATION_JSON_TYPE), new GenericType<ResultResponseModel<GetOrderPaymentRequestStatusResponse>>() {
                });
    }

    @Nonnull
    @Override
    public ResultResponseModel<CreateOrderRefundResponse> createOrder(@Nonnull final CreateOrderRefundRequest request){
        return getClient()
                .target(getApiPath())
                .path(RESOURCE_BASE_PATH)
                .path(REFUND_ORDER_PATH)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(request, MediaType.APPLICATION_JSON_TYPE), new GenericType<ResultResponseModel<CreateOrderRefundResponse>>() {
                });
    }
    //endregion
}
