package com.sfl.pms.services.order.external;

import com.sfl.pms.services.order.model.Order;
import com.sfl.pms.services.order.model.OrderState;
import com.sfl.pms.services.test.AbstractServiceIntegrationTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * User: Ruben Dilanyan
 * Company: SFL LLC
 * Date: 1/22/16
 * Time: 6:58 PM
 */
@Component
public class OrderStateMutationExternalNotifierEventProcessingServiceIntegrationTest extends AbstractServiceIntegrationTest {

    /* Dependencies */
    @Autowired
    private OrderStateMutationExternalNotifierEventProcessingService orderStateMutationExternalNotifierEventProcessingService;

    /* Constructors */
    public OrderStateMutationExternalNotifierEventProcessingServiceIntegrationTest() {
    }

    /* Test methods */
    @Test
    public void testProcessOrderStateUpdatedEvent() {
        // Test data
        final Order order = getServicesTestHelper().createOrder();
        flushAndClear();
        // Process event
        orderStateMutationExternalNotifierEventProcessingService.processOrderStateUpdatedEvent(order.getId(), OrderState.PAID, UUID.randomUUID().toString());
    }
}
