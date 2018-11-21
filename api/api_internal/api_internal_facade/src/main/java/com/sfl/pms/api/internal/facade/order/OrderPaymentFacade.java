package com.sfl.pms.api.internal.facade.order;

import com.sfl.pms.core.api.internal.model.common.result.ResultResponseModel;
import com.sfl.pms.core.api.internal.model.order.request.CreateOrderRequest;
import com.sfl.pms.core.api.internal.model.order.request.GetOrderPaymentRequestStatusRequest;
import com.sfl.pms.core.api.internal.model.order.request.RePayOrderRequest;
import com.sfl.pms.core.api.internal.model.order.request.CreateOrderRefundRequest;
import com.sfl.pms.core.api.internal.model.order.response.CreateOrderPaymentResponse;
import com.sfl.pms.core.api.internal.model.order.response.GetOrderPaymentRequestStatusResponse;
import com.sfl.pms.core.api.internal.model.order.response.CreateOrderRefundResponse;

import javax.annotation.Nonnull;

/**
 * User: Ruben Dilanyan
 * Company: SFL LLC
 * Date: 1/19/16
 * Time: 1:57 PM
 */
public interface OrderPaymentFacade {

    /**
     * Creates order and payment request
     *
     * @param request
     * @return response
     */
    @Nonnull
    ResultResponseModel<CreateOrderPaymentResponse> createOrder(@Nonnull final CreateOrderRequest request);


    /**
     * Repays existing order
     *
     * @param request
     * @return response
     */
    @Nonnull
    ResultResponseModel<CreateOrderPaymentResponse> rePayOrder(@Nonnull final RePayOrderRequest request);

    /**
     * Gets order payment request status
     *
     * @param request
     * @return response
     */
    @Nonnull
    ResultResponseModel<GetOrderPaymentRequestStatusResponse> getOrderPaymentRequestStatus(@Nonnull final GetOrderPaymentRequestStatusRequest request);

    /**
     * Refund order for provided refund order requets
     *
     * @param request
     * @return response
     */
    ResultResponseModel<CreateOrderRefundResponse> refundOrder(@Nonnull final CreateOrderRefundRequest request);
}
