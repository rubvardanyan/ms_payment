package com.sfl.pms.services.payment.common.impl.order.request;

import com.sfl.pms.persistence.repositories.payment.common.order.OrderRefundRequestRepository;
import com.sfl.pms.services.payment.common.exception.order.OrderRefundRequestNotFoundForIdException;
import com.sfl.pms.services.payment.common.exception.order.OrderRefundRequestNotFoundForUuIdException;
import com.sfl.pms.services.payment.common.model.order.request.OrderPaymentRequest;
import com.sfl.pms.services.payment.common.model.order.request.OrderRefundRequest;
import com.sfl.pms.services.payment.common.model.order.request.OrderRefundRequestState;
import com.sfl.pms.services.payment.common.order.request.OrderPaymentRequestService;
import com.sfl.pms.services.payment.common.order.request.OrderRefundRequestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Nonnull;
import java.util.Date;

/**
 * Created by Vasil Mamikonyan
 * Company: SFL LLC
 * Date: 11/19/2018
 * Time: 6:44 PM
 */
@Service
public class OrderRefundRequestServiceImpl implements OrderRefundRequestService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderRefundRequestServiceImpl.class);

    /* Dependencies */
    @Autowired
    private OrderRefundRequestRepository orderRefundRequestRepository;

    @Autowired
    private OrderPaymentRequestService orderPaymentRequestService;


    /* Constructors */
    public OrderRefundRequestServiceImpl() {
        LOGGER.debug("Initializing order refund request service");
    }

    @Nonnull
    @Override
    public OrderRefundRequest getById(@Nonnull final Long id) {
        assertOrderRefundRequestId(id);
        LOGGER.debug("Getting order refund request for id - {}", id);
        final OrderRefundRequest orderRefundRequest = orderRefundRequestRepository.findOne(id);
        assertOrderRefundRequestNotNullForId(orderRefundRequest, id);
        LOGGER.debug("Successfully retrieved order refund request for id - {}, order refund request - {}", id, orderRefundRequest);
        return orderRefundRequest;
    }

    @Nonnull
    @Override
    public OrderRefundRequest getByUuId(@Nonnull final String uuid) {
        assertOrderRefundRequestUuId(uuid);
        LOGGER.debug("Getting order refund request for uuId - {}", uuid);
        final OrderRefundRequest orderRefundRequest = orderRefundRequestRepository.findByUuId(uuid);
        assertOrderRefundRequestNotNullForUuId(orderRefundRequest, uuid);
        LOGGER.debug("Successfully retrieved order refund request for uuId - {}, order refund request - {}", uuid, orderRefundRequest);
        return orderRefundRequest;
    }

    @Transactional
    @Nonnull
    @Override
    public OrderRefundRequest create(@Nonnull final Long orderPaymentRequestId) {
        assertOrderPaymentRequestId(orderPaymentRequestId);
        LOGGER.debug("Creating order refund request for order payment request with id - {}", orderPaymentRequestId);
        String s;
        final OrderPaymentRequest orderPaymentRequest = orderPaymentRequestService.getOrderPaymentRequestById(orderPaymentRequestId);
        // Create new order refund request
        OrderRefundRequest orderRefundRequest = new OrderRefundRequest(true);
        // Update domain properties
        orderRefundRequest.setOrderPaymentRequest(orderPaymentRequest);
        // Persist payment request
        orderRefundRequest = orderRefundRequestRepository.save(orderRefundRequest);
        LOGGER.debug("Successfully created order refund request for order payment request with id - {}, refund request - {}", orderPaymentRequestId, orderRefundRequest);
        return orderRefundRequest;
    }

    @Transactional
    @Nonnull
    @Override
    public OrderRefundRequest updateState(@Nonnull final String orderRefundRequestUuid, @Nonnull final OrderRefundRequestState state) {
        assertOrderRefundRequestUuId(orderRefundRequestUuid);
        Assert.notNull(state, "Order refund request state should not be null");
        LOGGER.debug("Updating order refun request state with id - {}, state - {}", orderRefundRequestUuid, state);
        OrderRefundRequest request = orderRefundRequestRepository.findByUuId(orderRefundRequestUuid);
        assertOrderRefundRequestNotNullForUuId(request, orderRefundRequestUuid);
        final OrderRefundRequestState initialState = request.getState();
        // Update state
        request.setState(state);
        request.setUpdated(new Date());
        // Persist request
        request = orderRefundRequestRepository.saveAndFlush(request);
        LOGGER.debug("Successfully updated state for order refund request with uuid - {}, new state - {}", orderRefundRequestUuid, state);
        return request;
    }

    /* Utility methods */
    private void assertOrderRefundRequestId(final Long orderrefundRequestId) {
        Assert.notNull(orderrefundRequestId, "Order refund request id should not be null");
    }

    private void assertOrderRefundRequestNotNullForId(final OrderRefundRequest orderRefundRequest, final Long id) {
        if (orderRefundRequest == null) {
            LOGGER.error("No order refund request was found for id - {}", id);
            throw new OrderRefundRequestNotFoundForIdException(id);
        }
    }

    private void assertOrderRefundRequestUuId(final String orderRefundRequestUuId) {
        Assert.notNull(orderRefundRequestUuId, "Order refund request uuId should not be null");
    }

    private void assertOrderRefundRequestNotNullForUuId(final OrderRefundRequest orderRefundRequest, final String uuId) {
        if (orderRefundRequest == null) {
            LOGGER.error("No order refund request was found for uuId - {}", uuId);
            throw new OrderRefundRequestNotFoundForUuIdException(uuId);
        }
    }

    private void assertOrderPaymentRequestId(final Long orderPaymentRequestId) {
        Assert.notNull(orderPaymentRequestId, "Order payment request id should not be null");
    }
}
