package com.sfl.pms.services.order.impl.external;

import com.sfl.pms.services.order.external.OrderStateMutationExternalNotifierService;
import com.sfl.pms.services.order.model.OrderState;
import com.sfl.pms.services.payment.common.model.PaymentState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * User: Ruben Dilanyan
 * Company: SFL LLC
 * Date: 1/22/16
 * Time: 5:01 PM
 */
@Service("orderStateMutationExternalDummyNotifierService")
public class OrderStateMutationExternalDummyNotifierServiceImpl implements OrderStateMutationExternalNotifierService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderStateMutationExternalDummyNotifierServiceImpl.class);

    /* Constructors */
    public OrderStateMutationExternalDummyNotifierServiceImpl() {
        LOGGER.debug("Initializing order state mutation external dummy notifier service");
    }

    @Override
    public void notifyOrderStateMutation(@Nonnull final String orderUuId, @Nonnull final OrderState orderState, final PaymentState paymentState, @Nullable final String payentUuid) {
        Assert.notNull(orderUuId, "Order uuid should not be null");
        Assert.notNull(orderState, "Order state should not be null");
        LOGGER.debug("Swallowing order state mutation, order uuid - {}, order state - {}", orderUuId, orderState);
    }
}
