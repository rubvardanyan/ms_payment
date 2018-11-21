package com.sfl.pms.queue.consumer.payment.common.order.rpc;

import com.sfl.pms.persistence.utility.PersistenceUtilityService;
import com.sfl.pms.queue.amqp.model.payment.common.order.OrderPaymentRequestRPCTransferModel;
import com.sfl.pms.queue.amqp.model.payment.common.order.OrderRefundRequestRPCTransferModel;
import com.sfl.pms.queue.amqp.rpc.RPCCallType;
import com.sfl.pms.queue.amqp.rpc.impl.AbstractRPCServiceAdapterImpl;
import com.sfl.pms.queue.amqp.rpc.message.RPCMethodHandler;
import com.sfl.pms.queue.consumer.payment.common.order.OrderRefundRequestProcessingConsumerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 11/21/18
 * Time: 6:12 PM
 */
@Component("orderRefundRequestProcessingConsumerServiceRPCAdapter")
public class OrderRefundRequestProcessingConsumerServiceRPCAdapterImpl extends AbstractRPCServiceAdapterImpl {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderRefundRequestProcessingConsumerServiceRPCAdapterImpl.class);

    /* Dependencies */
    @Autowired
    private OrderRefundRequestProcessingConsumerService orderRefundRequestProcessingConsumerService;

    @Autowired
    private PersistenceUtilityService persistenceUtilityService;

    /* Constructors */
    public OrderRefundRequestProcessingConsumerServiceRPCAdapterImpl() {
        LOGGER.debug("Initializing order payment request processing consumer service RPC adapter");
    }

    @Override
    public void afterPropertiesSet() {
        addMethodHandler(new RPCMethodHandler<OrderRefundRequestRPCTransferModel>(RPCCallType.START_ORDER_REFUND_REQUEST_PROCESSING.getCallIdentifier(), OrderRefundRequestRPCTransferModel.class) {
            @Override
            public Object executeMethod(final Object parameter) {
                final OrderRefundRequestRPCTransferModel rpcHandler = (OrderRefundRequestRPCTransferModel) parameter;
                persistenceUtilityService.runInPersistenceSession(() -> {
                    orderRefundRequestProcessingConsumerService.processOrderRefundRequest(rpcHandler.getOrderRefundRequestId());
                });
                return rpcHandler;
            }
        });
    }
}
