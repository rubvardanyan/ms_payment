package com.sfl.pms.services.payment.processing.order;

import javax.annotation.Nonnull;

/**
 * User: Ruben Dilanyan
 * Company: SFL LLC
 * Date: 1/13/15
 * Time: 3:18 PM
 */
public interface OrderRefundRequestProcessorService {

    /**
     * Creates and processes order refund
     *
     * @param orderRefundRequestId
     */
    @Nonnull
    void processOrderRefundRequest(@Nonnull final Long orderRefundRequestId);
}
