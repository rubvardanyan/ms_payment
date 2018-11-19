package com.sfl.pms.queue.producer.payment.common.order;

import javax.annotation.Nonnull;

/**
 * User: Ruben Dilanyan
 * Company: SFL LLC
 * Date: 2/4/15
 * Time: 4:22 PM
 */
public interface OrderRefundRequestProcessingProducerService {

    /**
     * Process order refund request
     *
     * @param orderRefundRequestId
     */
    void processOrderRefundRequestProcessingEvent(@Nonnull final Long orderRefundRequestId);
}
