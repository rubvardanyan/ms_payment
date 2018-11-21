package com.sfl.pms.api.internal.client.rest.order;

import com.sfl.pms.core.api.internal.model.common.result.ResultResponseModel;
import com.sfl.pms.core.api.internal.model.order.request.CreateOrderRefundRequest;
import com.sfl.pms.core.api.internal.model.order.request.CreateOrderRequest;
import com.sfl.pms.core.api.internal.model.order.request.GetOrderPaymentRequestStatusRequest;
import com.sfl.pms.core.api.internal.model.order.request.RePayOrderRequest;
import com.sfl.pms.core.api.internal.model.order.response.CreateOrderPaymentResponse;
import com.sfl.pms.core.api.internal.model.order.response.CreateOrderRefundResponse;
import com.sfl.pms.core.api.internal.model.order.response.GetOrderPaymentRequestStatusResponse;

import javax.annotation.Nonnull;

/**
 * User: Arthur Asatryan
 * Company: SFL LLC
 * Date: 2/21/16
 * Time: 7:18 PM
 */
public interface OrderPaymentResourceClient {

    /**
     * Places new order
     *
     * @param request
     * @return response
     */
    @Nonnull
    ResultResponseModel<CreateOrderPaymentResponse> createOrder(@Nonnull final CreateOrderRequest request);

    /**
     * RePays existing order
     *
     * @param request
     * @return response
     */
    @Nonnull
    ResultResponseModel<CreateOrderPaymentResponse> rePayOrder(@Nonnull final RePayOrderRequest request);

    /**
     * Retrieves order payment status
     *
     * @param request
     * @return response
     */
    @Nonnull
    ResultResponseModel<GetOrderPaymentRequestStatusResponse> getOrderPaymentStatus(@Nonnull final GetOrderPaymentRequestStatusRequest request);

    /**
     * refund order
     *
     * @param request
     * @return response
     */
    @Nonnull
    ResultResponseModel<CreateOrderRefundResponse> createOrder(@Nonnull final CreateOrderRefundRequest request);
}
