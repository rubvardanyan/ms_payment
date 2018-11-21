package com.sfl.pms.queue.consumer.payment.common.order;

import javax.annotation.Nonnull;

/**
 * User: Ruben Dilanyan
 * Company: SFL LLC
 * Date: 2/4/15
 * Time: 5:02 PM
 */
public interface OrderRefundRequestProcessingConsumerService {

    /**
     * Process order refund request
     *
     * @param orderRefundRequest
     */
    void processOrderRefundRequest(@Nonnull final Long orderRefundRequest);
}
