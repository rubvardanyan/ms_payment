package com.sfl.pms.queue.producer.payment.common.order.impl;

import com.sfl.pms.queue.amqp.model.payment.common.order.OrderRefundRequestRPCTransferModel;
import com.sfl.pms.queue.amqp.rpc.RPCCallType;
import com.sfl.pms.queue.producer.connector.AmqpConnectorService;
import com.sfl.pms.queue.producer.connector.AmqpResponseHandler;
import com.sfl.pms.queue.producer.payment.common.order.OrderRefundRequestProcessingProducerService;
import com.sfl.pms.services.payment.common.event.order.StartOrderRefundRequestProcessingCommandEvent;
import com.sfl.pms.services.payment.common.event.order.StartOrderRefundRequestProcessingCommandEventListenerAdapter;
import com.sfl.pms.services.system.event.ApplicationEventDistributionService;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Nonnull;

/**
 * User: Ruben Dilanyan
 * Company: SFL LLC
 * Date: 2/4/15
 * Time: 4:23 PM
 */
@Service
public class OrderRefundRequestProcessingProducerServiceImpl implements OrderRefundRequestProcessingProducerService, InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderRefundRequestProcessingProducerServiceImpl.class);

    /* Dependencies */
    @Autowired
    private ApplicationEventDistributionService applicationEventDistributionService;

    @Autowired
    private AmqpConnectorService amqpConnectorService;

    /* Constructors */
    public OrderRefundRequestProcessingProducerServiceImpl() {
        LOGGER.debug("Initializing order refund request processing producer service");
    }

    @Override
    public void afterPropertiesSet() {
        applicationEventDistributionService.subscribe(new CustomerPaymentMethodAuthorizationRequestCreatedEventListener());
    }

    @Override
    public void processOrderRefundRequestProcessingEvent(@Nonnull final Long orderRefundRequestId) {
        Assert.notNull(orderRefundRequestId, "Order refund request ID should not be null");
        LOGGER.debug("Processing order refund request with id - {}", orderRefundRequestId);
        amqpConnectorService.publishMessage(RPCCallType.START_ORDER_REFUND_REQUEST_PROCESSING,
                new OrderRefundRequestRPCTransferModel(orderRefundRequestId),
                OrderRefundRequestRPCTransferModel.class,
                new OrderRefundRequestRPCResponseHandler());
    }

    /* Properties getters and setters */
    public ApplicationEventDistributionService getApplicationEventDistributionService() {
        return applicationEventDistributionService;
    }

    public void setApplicationEventDistributionService(final ApplicationEventDistributionService applicationEventDistributionService) {
        this.applicationEventDistributionService = applicationEventDistributionService;
    }

    public AmqpConnectorService getAmqpConnectorService() {
        return amqpConnectorService;
    }

    public void setAmqpConnectorService(final AmqpConnectorService amqpConnectorService) {
        this.amqpConnectorService = amqpConnectorService;
    }

    /* Inner classes */
    private class CustomerPaymentMethodAuthorizationRequestCreatedEventListener extends StartOrderRefundRequestProcessingCommandEventListenerAdapter {

        /* Constructors */
        public CustomerPaymentMethodAuthorizationRequestCreatedEventListener() {
        }

        @Override
        protected void processOrderRefundRequestCreatedEvent(final StartOrderRefundRequestProcessingCommandEvent event) {
            OrderRefundRequestProcessingProducerServiceImpl.this.processOrderRefundRequestProcessingEvent(event.getOrderRefundRequestId());
        }
    }

    private static class OrderRefundRequestRPCResponseHandler implements AmqpResponseHandler<OrderRefundRequestRPCTransferModel> {

        private final StopWatch stopWatch;

        /* Constructors */
        public OrderRefundRequestRPCResponseHandler() {
            this.stopWatch = new StopWatch();
            stopWatch.start();
        }

        @Override
        public void handleResponse(@Nonnull final OrderRefundRequestRPCTransferModel responseModel) {
            stopWatch.stop();
            LOGGER.debug("Finalized order refund round trip, response model - {}, duration - {}", responseModel, stopWatch.getTime());

        }
    }
}
