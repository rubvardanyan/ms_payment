package com.sfl.pms.services.payment.common.order.request;

import com.sfl.pms.services.payment.common.model.order.request.OrderRefundRequest;
import com.sfl.pms.services.payment.common.model.order.request.OrderRefundRequestState;

import javax.annotation.Nonnull;

/**
 * User: Ruben Dilanyan
 * Company: SFL LLC
 * Date: 2/4/15
 * Time: 1:10 PM
 */
public interface OrderRefundRequestService {

    /**
     * Gets order refund request by id
     *
     * @param id
     * @return OrderRefundRequest
     */
    @Nonnull
    OrderRefundRequest getById(@Nonnull final Long id);

    /**
     * Gets order refund request by uuId
     *
     * @param uuid
     * @return OrderRefundRequest
     */
    @Nonnull
    OrderRefundRequest getByUuId(@Nonnull final String uuid);


    /**
     * Creates new order refund request
     *
     * @param orderPaymentRequestId
     * @return OrderRefundRequest
     */
    @Nonnull
    OrderRefundRequest create(@Nonnull final Long orderPaymentRequestId);


    /**
     * Updates order refund request state by provided uuid
     *
     * @param orderRefundRequestUuid
     * @param state
     * @return OrderRefundRequest
     */
    @Nonnull
    OrderRefundRequest updateState(@Nonnull final String orderRefundRequestUuid, @Nonnull final OrderRefundRequestState state);
}
