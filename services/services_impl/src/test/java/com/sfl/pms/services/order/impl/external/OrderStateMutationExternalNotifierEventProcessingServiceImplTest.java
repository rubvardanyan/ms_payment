package com.sfl.pms.services.order.impl.external;

import com.sfl.pms.persistence.utility.PersistenceUtilityService;
import com.sfl.pms.services.order.OrderService;
import com.sfl.pms.services.order.external.OrderStateMutationExternalNotifierService;
import com.sfl.pms.services.order.model.Order;
import com.sfl.pms.services.order.model.OrderState;
import com.sfl.pms.services.order.model.payment.provider.OrderProviderPaymentChannel;
import com.sfl.pms.services.payment.common.model.PaymentState;
import com.sfl.pms.services.payment.common.model.order.OrderPayment;
import com.sfl.pms.services.test.AbstractServicesUnitTest;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Test;

import java.util.UUID;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.fail;

/**
 * User: Ruben Dilanyan
 * Company: SFL LLC
 * Date: 1/22/16
 * Time: 7:05 PM
 */
public class OrderStateMutationExternalNotifierEventProcessingServiceImplTest extends AbstractServicesUnitTest {

    /* Test subject and mocks */
    @TestSubject
    private OrderStateMutationExternalNotifierEventProcessingServiceImpl orderStateMutationExternalNotifierEventProcessingService = new OrderStateMutationExternalNotifierEventProcessingServiceImpl();

    @Mock
    private OrderService orderService;

    @Mock
    private OrderStateMutationExternalNotifierService orderStateMutationExternalNotifierService;

    @Mock
    private PersistenceUtilityService persistenceUtilityService;

    /* Constructors */
    public OrderStateMutationExternalNotifierEventProcessingServiceImplTest() {
    }

    /* Test methods */
    @Test
    public void testProcessOrderStateUpdatedEventWithInvalidArguments() {
        // Test data
        final Long orderId = 1L;
        final OrderState orderState = OrderState.PAID;
        // Reset
        resetAll();
        // Replay
        replayAll();
        // Run test scenario
        try {
            orderStateMutationExternalNotifierEventProcessingService.processOrderStateUpdatedEvent(null, orderState, UUID.randomUUID().toString());
            fail("Exception should be thrown");
        } catch (final IllegalArgumentException ex) {
            // Expected
        }
        try {
            orderStateMutationExternalNotifierEventProcessingService.processOrderStateUpdatedEvent(orderId, null, UUID.randomUUID().toString());
            fail("Exception should be thrown");
        } catch (final IllegalArgumentException ex) {
            // Expected
        }
        // Verify
    }

    @Test
    public void testProcessOrderStateUpdatedEvent() {
        // Test data
        final Long orderId = 1L;
        final OrderState orderState = OrderState.PAID;
        final Order order = getServicesImplTestHelper().createOrder();
        final OrderProviderPaymentChannel orderProviderPaymentChannel = getServicesImplTestHelper().createOrderProviderPaymentChannel();
        final String paymentUuid = UUID.randomUUID().toString();
        final OrderPayment payment = getServicesImplTestHelper().createOrderPayment();
        payment.setUuId(paymentUuid);
        payment.setLastState(PaymentState.PAID);
        orderProviderPaymentChannel.setPayment(payment);
        order.setPaymentChannel(orderProviderPaymentChannel);
        order.getPayments().add(payment);
        // Reset
        resetAll();
        // Expectations
        expect(orderService.getOrderById(eq(orderId))).andReturn(order).once();
        orderStateMutationExternalNotifierService.notifyOrderStateMutation(eq(order.getUuId()), eq(orderState), eq(payment.getLastState()), eq(paymentUuid));
        expectLastCall().once();
        // Replay
        replayAll();
        // Run test scenario
        orderStateMutationExternalNotifierEventProcessingService.processOrderStateUpdatedEvent(orderId, orderState, paymentUuid);
        // Verify
        verifyAll();
    }
}
