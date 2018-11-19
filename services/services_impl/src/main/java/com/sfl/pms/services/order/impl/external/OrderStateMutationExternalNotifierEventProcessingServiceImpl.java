package com.sfl.pms.services.order.impl.external;

import com.sfl.pms.persistence.utility.PersistenceUtilityService;
import com.sfl.pms.services.order.OrderService;
import com.sfl.pms.services.order.event.OrderPaidEvent;
import com.sfl.pms.services.order.event.OrderPaidEventListenerAdapter;
import com.sfl.pms.services.order.event.OrderStateChangedEvent;
import com.sfl.pms.services.order.event.OrderStateChangedEventListenerAdapter;
import com.sfl.pms.services.order.external.OrderStateMutationExternalNotifierEventProcessingService;
import com.sfl.pms.services.order.external.OrderStateMutationExternalNotifierService;
import com.sfl.pms.services.order.model.Order;
import com.sfl.pms.services.order.model.OrderState;
import com.sfl.pms.services.order.model.payment.OrderPaymentChannel;
import com.sfl.pms.services.order.model.payment.provider.OrderProviderPaymentChannel;
import com.sfl.pms.services.payment.common.model.PaymentState;
import com.sfl.pms.services.payment.common.model.order.OrderPayment;
import com.sfl.pms.services.system.concurrency.TaskExecutorService;
import com.sfl.pms.services.system.event.ApplicationEventDistributionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * User: Ruben Dilanyan
 * Company: SFL LLC
 * Date: 1/22/16
 * Time: 6:00 PM
 */
public class OrderStateMutationExternalNotifierEventProcessingServiceImpl implements OrderStateMutationExternalNotifierEventProcessingService, InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderStateMutationExternalNotifierEventProcessingServiceImpl.class);

    /* Dependencies */
    private OrderStateMutationExternalNotifierService orderStateMutationExternalNotifierService;

    @Autowired
    private ApplicationEventDistributionService applicationEventDistributionService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private TaskExecutorService taskExecutorService;

    @Autowired
    private PersistenceUtilityService persistenceUtilityService;

    /* Constructors */
    public OrderStateMutationExternalNotifierEventProcessingServiceImpl() {
    }

    @Override
    public void afterPropertiesSet() {
        // Subscribe to events
        applicationEventDistributionService.subscribe(new OrderPaidEventListener());
        applicationEventDistributionService.subscribe(new OrderStateChangedEventListener());
    }

    @Override
    public void processOrderStateUpdatedEvent(@Nonnull final Long orderId, @Nonnull final OrderState orderState, @Nullable final String paymentUuid) {
        Assert.notNull(orderId, "Order id should not be null");
        Assert.notNull(orderState, "Order state should not be null");
        LOGGER.debug("Processing order state updated event, order id - {}, order state - {}", orderId, orderState);
        final Order order = orderService.getOrderById(orderId);
        final PaymentState paymentState = StringUtils.isEmpty(paymentUuid) ? null : order.getPayments().stream().
                filter(payment -> payment.getUuId().equals(paymentUuid))
                .findFirst()
                .map(OrderPayment::getLastState)
                .orElseThrow(() -> new IllegalStateException(String.format(
                        "Payment with uuid ='%s' should be associated with order with uuid='%s'", paymentUuid, order.getUuId())
                ));
        orderStateMutationExternalNotifierService.notifyOrderStateMutation(order.getUuId(), orderState, paymentState, paymentUuid);
        LOGGER.debug("Successfully processed order state updated event, order id - {}, order state - {}", orderId, orderState);
    }

    /* Inner classes */
    private class OrderPaidEventListener extends OrderPaidEventListenerAdapter {

        @Override
        protected void processOrderPaidEvent(final OrderPaidEvent orderPaidEvent) {
            taskExecutorService.executeTaskAsynchronously(() -> processOrderStateUpdatedEvent(orderPaidEvent.getOrderId(), OrderState.PAID, getPaymentUuidOrNull(orderPaidEvent.getOrderId())), true);
        }
    }

    private class OrderStateChangedEventListener extends OrderStateChangedEventListenerAdapter {

        @Override
        protected void processOrderStateChangedEvent(final OrderStateChangedEvent orderStateChangedEvent) {
            taskExecutorService.executeTaskAsynchronously(() -> processOrderStateUpdatedEvent(orderStateChangedEvent.getOrderId(), orderStateChangedEvent.getOrderState(), orderStateChangedEvent.getPaymentUuid()), true);
        }
    }

    /* Properties getters and setters */
    public ApplicationEventDistributionService getApplicationEventDistributionService() {
        return applicationEventDistributionService;
    }

    public void setApplicationEventDistributionService(final ApplicationEventDistributionService applicationEventDistributionService) {
        this.applicationEventDistributionService = applicationEventDistributionService;
    }

    public OrderStateMutationExternalNotifierService getOrderStateMutationExternalNotifierService() {
        return orderStateMutationExternalNotifierService;
    }

    public void setOrderStateMutationExternalNotifierService(final OrderStateMutationExternalNotifierService orderStateMutationExternalNotifierService) {
        this.orderStateMutationExternalNotifierService = orderStateMutationExternalNotifierService;
    }

    public OrderService getOrderService() {
        return orderService;
    }

    public void setOrderService(final OrderService orderService) {
        this.orderService = orderService;
    }

    public TaskExecutorService getTaskExecutorService() {
        return taskExecutorService;
    }

    public void setTaskExecutorService(final TaskExecutorService taskExecutorService) {
        this.taskExecutorService = taskExecutorService;
    }

    /* Utility methods */
    @Nullable
    private String getPaymentUuidOrNull(@Nonnull final Long orderId) {
        final Order order = orderService.getOrderById(orderId);
        if (order.getPaymentChannel() != null) {
            final OrderPaymentChannel orderPaymentChannel = persistenceUtilityService.initializeAndUnProxy(order.getPaymentChannel());
            if (orderPaymentChannel instanceof OrderProviderPaymentChannel) {
                final OrderProviderPaymentChannel orderProviderPaymentChannel = (OrderProviderPaymentChannel) orderPaymentChannel;
                return orderProviderPaymentChannel.getPayment().getUuId();
            }
        }
        return null;
    }
}
