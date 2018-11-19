package com.sfl.pms.queue.consumer.payment.common.order.impl;

import com.sfl.pms.queue.consumer.payment.common.order.OrderRefundRequestProcessingConsumerService;
import com.sfl.pms.services.payment.processing.order.OrderRefundRequestProcessorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Nonnull;

/**
 * User: Ruben Dilanyan
 * Company: SFL LLC
 * Date: 2/4/15
 * Time: 5:04 PM
 */
@Service
@Lazy(false)
public class OrderRefundRequestProcessingConsumerServiceImpl implements OrderRefundRequestProcessingConsumerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderRefundRequestProcessingConsumerServiceImpl.class);

    /* Dependencies */
    @Autowired
    private OrderRefundRequestProcessorService orderRefundRequestProcessorService;

    /* Constructors */
    public OrderRefundRequestProcessingConsumerServiceImpl() {
        LOGGER.debug("Initializing order payment request processing consumer service");
    }

    @Override
    public void processOrderRefundRequest(@Nonnull final Long orderRefundRequestId) {
        Assert.notNull(orderRefundRequestId, "Order refund request id should not be null");
        orderRefundRequestProcessorService.processOrderRefundRequest(orderRefundRequestId);
    }

    /* Properties getters and setters */
    public OrderRefundRequestProcessorService getOrderRefundRequestProcessorService() {
        return orderRefundRequestProcessorService;
    }

    public void setOrderRefundRequestProcessorService(final OrderRefundRequestProcessorService orderRefundRequestProcessorService) {
        this.orderRefundRequestProcessorService = orderRefundRequestProcessorService;
    }
}
