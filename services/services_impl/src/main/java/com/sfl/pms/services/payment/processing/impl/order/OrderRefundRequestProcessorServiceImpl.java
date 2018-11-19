package com.sfl.pms.services.payment.processing.impl.order;

import com.sfl.pms.persistence.utility.PersistenceUtilityService;
import com.sfl.pms.services.common.exception.ServicesRuntimeException;
import com.sfl.pms.services.payment.common.model.order.request.OrderRefundRequest;
import com.sfl.pms.services.payment.common.model.order.request.OrderRefundRequestState;
import com.sfl.pms.services.payment.common.order.request.OrderRefundRequestService;
import com.sfl.pms.services.payment.processing.order.OrderRefundRequestProcessorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Nonnull;

/**
 * User: Ruben Dilanyan
 * Company: SFL LLC
 * Date: 1/17/15
 * Time: 1:00 PM
 */
@Service
public class OrderRefundRequestProcessorServiceImpl implements OrderRefundRequestProcessorService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderRefundRequestProcessorServiceImpl.class);

    /* Dependencies */
    @Autowired
    private OrderRefundRequestService orderRefundRequestService;

    @Autowired
    private PersistenceUtilityService persistenceUtilityService;

    /* Constructors */
    public OrderRefundRequestProcessorServiceImpl() {
        LOGGER.debug("Initializing order refund processing service");
    }

    @Nonnull
    @Override
    public Long processOrderRefundRequest(@Nonnull final Long orderRefundRequestId) {
        Assert.notNull(orderRefundRequestId, "Order refund request id should not be null");
        LOGGER.debug("Processing order refund request with id - {}", orderRefundRequestId);
        // Update state on request
        updateRefundMethodRequestState(orderRefundRequestId, OrderRefundRequestState.PROCESSING);
        try {
            // Grab data
            return 0l; //Todo: Rub
        } catch (final Exception ex) {
            LOGGER.error("Error occurred during processing order payment request - " + orderRefundRequestId, ex);
            // Update state on request
            updateRefundMethodRequestState(orderRefundRequestId, OrderRefundRequestState.FAILED);
            // Rethrow error
            throw new ServicesRuntimeException(ex);
        }
    }

    /* Utility methods */

    private void updateRefundMethodRequestState(final Long requestId, final OrderRefundRequestState state) {
        getPersistenceUtilityService().runInNewTransaction(() -> {
            final OrderRefundRequest orderRefundRequest = orderRefundRequestService.getById(requestId);
            orderRefundRequestService.updateState(orderRefundRequest.getUuId(), state);
        });
    }

    /* Properties getters and setters */
    public PersistenceUtilityService getPersistenceUtilityService() {
        return persistenceUtilityService;
    }

    public void setPersistenceUtilityService(final PersistenceUtilityService persistenceUtilityService) {
        this.persistenceUtilityService = persistenceUtilityService;
    }
}
