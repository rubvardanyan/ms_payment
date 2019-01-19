package com.sfl.pms.services.payment.common.order.request;

import com.sfl.pms.services.payment.common.dto.order.request.OrderPaymentRequestDto;
import com.sfl.pms.services.payment.common.dto.order.request.OrderRequestPaymentMethodDto;
import com.sfl.pms.services.payment.common.model.order.request.OrderPaymentRequest;
import com.sfl.pms.services.payment.common.model.order.request.OrderPaymentRequestState;
import com.sfl.pms.services.payment.common.model.order.request.OrderRequestPaymentMethod;

import javax.annotation.Nonnull;
import java.util.Set;

/**
 * User: Ruben Dilanyan
 * Company: SFL LLC
 * Date: 2/4/15
 * Time: 1:10 PM
 */
public interface OrderPaymentRequestService {

    /**
     * Gets order payment request by id
     *
     * @param orderPaymentRequestId
     * @return orderPaymentRequest
     */
    @Nonnull
    OrderPaymentRequest getOrderPaymentRequestById(@Nonnull final Long orderPaymentRequestId);

    /**
     * Gets order payment request by uuId
     *
     * @param orderPaymentRequestUuId
     * @return orderPaymentRequest
     */
    @Nonnull
    OrderPaymentRequest getOrderPaymentRequestByUuId(@Nonnull final String orderPaymentRequestUuId);


    /**
     * Creates new order payment request
     *
     * @param orderId
     * @param orderPaymentRequestDto
     * @param orderRequestPaymentMethodDto
     * @return orderPaymentRequest
     */
    @Nonnull
    OrderPaymentRequest createOrderPaymentRequest(@Nonnull final Long orderId, @Nonnull final OrderPaymentRequestDto orderPaymentRequestDto, @Nonnull final OrderRequestPaymentMethodDto<? extends OrderRequestPaymentMethod> orderRequestPaymentMethodDto);


    /**
     * Updates order payment request state
     *
     * @param requestId
     * @param state
     * @param allowedInitialStates
     * @return orderPaymentRequest
     */
    @Nonnull
    OrderPaymentRequest updateOrderPaymentRequestState(@Nonnull final Long requestId, @Nonnull final OrderPaymentRequestState state, @Nonnull final Set<OrderPaymentRequestState> allowedInitialStates);

    /**
     * Updates order payment request redirect URL
     *
     * @param requestId
     * @param redirectUrl
     * @return orderPaymentRequest
     */
    @Nonnull
    OrderPaymentRequest updateOrderPaymentRequestRedirectUrl(@Nonnull final Long requestId, @Nonnull final String redirectUrl);


    /**
     * Updates payment on order payment request
     *
     * @param requestId
     * @param orderPaymentId
     * @return orderPaymentRequest
     */
    @Nonnull
    OrderPaymentRequest updateOrderPaymentRequestPayment(@Nonnull final Long requestId, @Nonnull final Long orderPaymentId);

    /**
     * Get order payment request by payment id
     * @param paymentId
     * @return OrderPaymentRequest
     */
    @Nonnull
    OrderPaymentRequest getByPaymentId(@Nonnull final Long paymentId);
}
