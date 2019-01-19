package com.sfl.pms.services.payment.common.impl.order.request;

import com.sfl.pms.persistence.repositories.payment.common.order.OrderPaymentRequestRepository;
import com.sfl.pms.services.common.exception.ServicesRuntimeException;
import com.sfl.pms.services.order.OrderService;
import com.sfl.pms.services.order.model.Order;
import com.sfl.pms.services.payment.common.dto.order.request.OrderPaymentRequestDto;
import com.sfl.pms.services.payment.common.dto.order.request.OrderRequestPaymentMethodDto;
import com.sfl.pms.services.payment.common.exception.order.*;
import com.sfl.pms.services.payment.common.model.order.OrderPayment;
import com.sfl.pms.services.payment.common.model.order.request.OrderPaymentRequest;
import com.sfl.pms.services.payment.common.model.order.request.OrderPaymentRequestState;
import com.sfl.pms.services.payment.common.model.order.request.OrderRequestPaymentMethod;
import com.sfl.pms.services.payment.common.model.order.request.OrderRequestPaymentMethodType;
import com.sfl.pms.services.payment.common.order.OrderPaymentService;
import com.sfl.pms.services.payment.common.order.request.OrderPaymentRequestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Nonnull;
import java.util.Date;
import java.util.Set;

/**
 * User: Ruben Dilanyan
 * Company: SFL LLC
 * Date: 2/4/15
 * Time: 1:10 PM
 */
@Service
public class OrderPaymentRequestServiceImpl implements OrderPaymentRequestService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderPaymentRequestServiceImpl.class);

    /* Dependencies */
    @Autowired
    private OrderPaymentRequestRepository orderPaymentRequestRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderPaymentService orderPaymentService;

    @Autowired
    private OrderRequestCustomerPaymentMethodDtoHandler orderRequestCustomerPaymentMethodDtoHandler;

    @Autowired
    private OrderRequestEncryptedPaymentMethodDtoHandler orderRequestEncryptedPaymentMethodDtoHandler;

    @Autowired
    private OrderRequestRedirectPaymentMethodDtoHandler orderRequestRedirectPaymentMethodDtoHandler;

    /* Constructors */
    public OrderPaymentRequestServiceImpl() {
        LOGGER.debug("Initializing order payment request service");
    }

    @Nonnull
    @Override
    public OrderPaymentRequest getOrderPaymentRequestById(@Nonnull final Long orderPaymentRequestId) {
        assertOrderPaymentRequestId(orderPaymentRequestId);
        LOGGER.debug("Getting order payment request for id - {}", orderPaymentRequestId);
        final OrderPaymentRequest orderPaymentRequest = orderPaymentRequestRepository.findOne(orderPaymentRequestId);
        assertOrderPaymentRequestNotNullForId(orderPaymentRequest, orderPaymentRequestId);
        LOGGER.debug("Successfully retrieved order payment request for id - {}, order payment request - {}", orderPaymentRequestId, orderPaymentRequest);
        return orderPaymentRequest;
    }

    @Nonnull
    @Override
    public OrderPaymentRequest getOrderPaymentRequestByUuId(@Nonnull final String orderPaymentRequestUuId) {
        assertOrderPaymentRequestUuId(orderPaymentRequestUuId);
        LOGGER.debug("Getting order payment request for uuId - {}", orderPaymentRequestUuId);
        final OrderPaymentRequest orderPaymentRequest = orderPaymentRequestRepository.findByUuId(orderPaymentRequestUuId);
        assertOrderPaymentRequestNotNullForUuId(orderPaymentRequest, orderPaymentRequestUuId);
        LOGGER.debug("Successfully retrieved order payment request for uuId - {}, order payment request - {}", orderPaymentRequestUuId, orderPaymentRequest);
        return orderPaymentRequest;
    }

    @Transactional
    @Nonnull
    @Override
    public OrderPaymentRequest createOrderPaymentRequest(@Nonnull final Long orderId, @Nonnull final OrderPaymentRequestDto orderPaymentRequestDto, @Nonnull final OrderRequestPaymentMethodDto<? extends OrderRequestPaymentMethod> orderRequestPaymentMethodDto) {
        assertOrderPaymentRequestId(orderId);
        Assert.notNull(orderRequestPaymentMethodDto, "Order request payment method should not be null");
        assertOrderPaymentRequestDto(orderPaymentRequestDto);
        LOGGER.debug("Creating order payment request for order with id - {}, request DTO - {}", orderId, orderPaymentRequestDto);
        final Order order = orderService.getOrderById(orderId);
        // Grab payment method DTO handler
        final OrderRequestPaymentMethodDtoHandler paymentMethodDtoHandler = getOrderRequestPaymentMethodDtoHandler(orderRequestPaymentMethodDto.getType());
        paymentMethodDtoHandler.assertOrderRequestPaymentMethodDto(orderRequestPaymentMethodDto, order.getCustomer());
        // Create new order payment request
        OrderPaymentRequest orderPaymentRequest = new OrderPaymentRequest(true);
        // Update domain properties
        orderPaymentRequestDto.updateDomainEntityProperties(orderPaymentRequest);
        orderPaymentRequest.setOrder(order);
        // Create order request payment method
        final OrderRequestPaymentMethod orderRequestPaymentMethod = paymentMethodDtoHandler.convertOrderRequestPaymentMethodDto(orderRequestPaymentMethodDto, order.getCustomer());
        orderRequestPaymentMethod.setOrderPaymentRequest(orderPaymentRequest);
        orderPaymentRequest.setPaymentMethod(orderRequestPaymentMethod);
        // Persist payment request
        orderPaymentRequest = orderPaymentRequestRepository.save(orderPaymentRequest);
        LOGGER.debug("Successfully created order payment request for order with id - {}, payment request - {}", orderId, orderPaymentRequest);
        return orderPaymentRequest;
    }

    @Transactional
    @Nonnull
    @Override
    public OrderPaymentRequest updateOrderPaymentRequestState(@Nonnull final Long requestId, @Nonnull final OrderPaymentRequestState state, @Nonnull final Set<OrderPaymentRequestState> allowedInitialStates) {
        assertOrderPaymentRequestId(requestId);
        Assert.notNull(state, "Order payment request state should not be null");
        Assert.notNull(allowedInitialStates, "Order payment request allowed initial states should not be null");
        LOGGER.debug("Updating order payment request state with id - {}, state - {}, allowed initial states - {}", requestId, state, allowedInitialStates);
        OrderPaymentRequest request = orderPaymentRequestRepository.findByIdWithWriteLockFlushedAndFreshData(requestId);
        assertOrderPaymentRequestNotNullForId(request, requestId);
        final OrderPaymentRequestState initialState = request.getState();
        if (allowedInitialStates.size() != 0) {
            assertPaymentRequestState(state, allowedInitialStates, initialState);
        }
        // Update state
        request.setState(state);
        request.setUpdated(new Date());
        // Persist request
        request = orderPaymentRequestRepository.saveAndFlush(request);
        LOGGER.debug("Successfully updated state for order payment request with id - {}, new state - {}, initial state - {}", requestId, state, initialState);
        return request;
    }

    @Transactional
    @Nonnull
    @Override
    public OrderPaymentRequest updateOrderPaymentRequestRedirectUrl(@Nonnull final Long requestId, @Nonnull final String redirectUrl) {
        assertOrderPaymentRequestId(requestId);
        Assert.notNull(redirectUrl, "Redirect URL should not be null");
        LOGGER.debug("Updating redirect URL for order payment request with id - {}, redirect URL - {}", requestId, redirectUrl);
        OrderPaymentRequest orderPaymentRequest = orderPaymentRequestRepository.findOne(requestId);
        assertOrderPaymentRequestNotNullForId(orderPaymentRequest, requestId);
        // Update URL and persist
        orderPaymentRequest.setPaymentRedirectUrl(redirectUrl);
        orderPaymentRequest = orderPaymentRequestRepository.save(orderPaymentRequest);
        LOGGER.debug("Successfully update redirect URL for order payment request with id - {}, order payment request - {}", orderPaymentRequest.getId(), orderPaymentRequest);
        return orderPaymentRequest;
    }

    @Transactional
    @Nonnull
    @Override
    public OrderPaymentRequest updateOrderPaymentRequestPayment(@Nonnull final Long requestId, @Nonnull final Long orderPaymentId) {
        assertOrderPaymentRequestId(requestId);
        Assert.notNull(orderPaymentId, "Order payment id should not be null");
        LOGGER.debug("Update payment for order payment request with id - {}, payment id - {}", requestId, orderPaymentId);
        // Grab order payment request
        OrderPaymentRequest orderPaymentRequest = orderPaymentRequestRepository.findOne(requestId);
        assertOrderPaymentRequestNotNullForId(orderPaymentRequest, requestId);
        // Grab order payment
        final OrderPayment orderPayment = orderPaymentService.getPaymentById(orderPaymentId);
        assertPaymentRequestAndPaymentOrders(orderPaymentRequest, orderPayment);
        // Set payment and update request
        orderPaymentRequest.setOrderPayment(orderPayment);
        orderPaymentRequest = orderPaymentRequestRepository.save(orderPaymentRequest);
        LOGGER.debug("Successfully updated payment for order payment request with id - {}, payment id - {}", orderPaymentRequest.getId(), orderPayment.getId());
        return orderPaymentRequest;
    }

    @Transactional
    @Nonnull
    @Override
    public OrderPaymentRequest getByPaymentId(@Nonnull final Long paymentId) {
        LOGGER.debug("Get order payment request by payment id - {}", paymentId);
        final OrderPaymentRequest orderPaymentRequest = orderPaymentRequestRepository.findByOrderPaymentId(paymentId);
        assertOrderPaymentRequestNotNullForPaymentId(orderPaymentRequest, paymentId);
        LOGGER.debug("Successfully retrieved order payment request by payment id - {}", paymentId);
        return orderPaymentRequest;
    }

    /* Utility methods */
    private void assertPaymentRequestAndPaymentOrders(final OrderPaymentRequest orderPaymentRequest, final OrderPayment orderPayment) {
        // Grab order IDs
        final Long requestOrderId = orderPaymentRequest.getOrder().getId();
        final Long paymentOrderId = orderPayment.getOrder().getId();
        if (!requestOrderId.equals(paymentOrderId)) {
            LOGGER.error("Order payment request with id - {} is created for order - {} whereas payment with id - {} was created for order - {}", orderPaymentRequest.getId(), requestOrderId, orderPayment.getId(), paymentOrderId);
            throw new InvalidOrderPaymentRequestPaymentException(orderPaymentRequest.getId(), orderPayment.getId());
        }
    }

    private void assertPaymentRequestState(final OrderPaymentRequestState state, final Set<OrderPaymentRequestState> allowedInitialStates, final OrderPaymentRequestState initialState) {
        if (!allowedInitialStates.contains(initialState)) {
            LOGGER.error("{} state is not allowed since request has state - {} but expected request states are - {}", state, initialState, allowedInitialStates);
            throw new OrderPaymentRequestStateNotAllowedException(state, initialState, allowedInitialStates);
        }
    }

    private void assertOrderPaymentRequestDto(final OrderPaymentRequestDto orderPaymentRequestDto) {
        Assert.notNull(orderPaymentRequestDto, "Order payment request DTO should not be null");
    }

    private void assertOrderPaymentRequestUuId(final String orderPaymentRequestUuId) {
        Assert.notNull(orderPaymentRequestUuId, "Order payment request uuId should not be null");
    }

    private void assertOrderPaymentRequestId(final Long orderPaymentRequestId) {
        Assert.notNull(orderPaymentRequestId, "Order payment request id should not be null");
    }

    private void assertOrderPaymentRequestNotNullForId(final OrderPaymentRequest orderPaymentRequest, final Long id) {
        if (orderPaymentRequest == null) {
            LOGGER.error("No order payment request was found for id - {}", id);
            throw new OrderPaymentRequestNotFoundForIdException(id);
        }
    }

    private void assertOrderPaymentRequestNotNullForPaymentId(final OrderPaymentRequest orderPaymentRequest, final Long paymentId) {
        if (orderPaymentRequest == null) {
            LOGGER.error("No order payment request was found for id - {}", paymentId);
            throw new OrderPaymentRequestNotFoundForPaymentIdException(paymentId);
        }
    }

    private void assertOrderPaymentRequestNotNullForUuId(final OrderPaymentRequest orderPaymentRequest, final String uuId) {
        if (orderPaymentRequest == null) {
            LOGGER.error("No order payment request was found for uuId - {}", uuId);
            throw new OrderPaymentRequestNotFoundForUuIdException(uuId);
        }
    }

    private OrderRequestPaymentMethodDtoHandler getOrderRequestPaymentMethodDtoHandler(final OrderRequestPaymentMethodType type) {
        switch (type) {
            case CUSTOMER_PAYMENT_METHOD:
                return orderRequestCustomerPaymentMethodDtoHandler;
            case ENCRYPTED_PAYMENT_METHOD:
                return orderRequestEncryptedPaymentMethodDtoHandler;
            case REDIRECT_PAYMENT_METHOD:
                return orderRequestRedirectPaymentMethodDtoHandler;
            default: {
                throw new ServicesRuntimeException("Unknown order request payment method type - " + type);
            }
        }
    }

    /* Properties getters and setters */
    public OrderPaymentRequestRepository getOrderPaymentRequestRepository() {
        return orderPaymentRequestRepository;
    }

    public void setOrderPaymentRequestRepository(final OrderPaymentRequestRepository orderPaymentRequestRepository) {
        this.orderPaymentRequestRepository = orderPaymentRequestRepository;
    }

    public OrderService getOrderService() {
        return orderService;
    }

    public void setOrderService(final OrderService orderService) {
        this.orderService = orderService;
    }

    public OrderRequestCustomerPaymentMethodDtoHandler getOrderRequestCustomerPaymentMethodDtoHandler() {
        return orderRequestCustomerPaymentMethodDtoHandler;
    }

    public void setOrderRequestCustomerPaymentMethodDtoHandler(final OrderRequestCustomerPaymentMethodDtoHandler orderRequestCustomerPaymentMethodDtoHandler) {
        this.orderRequestCustomerPaymentMethodDtoHandler = orderRequestCustomerPaymentMethodDtoHandler;
    }

    public OrderRequestEncryptedPaymentMethodDtoHandler getOrderRequestEncryptedPaymentMethodDtoHandler() {
        return orderRequestEncryptedPaymentMethodDtoHandler;
    }

    public void setOrderRequestEncryptedPaymentMethodDtoHandler(final OrderRequestEncryptedPaymentMethodDtoHandler orderRequestEncryptedPaymentMethodDtoHandler) {
        this.orderRequestEncryptedPaymentMethodDtoHandler = orderRequestEncryptedPaymentMethodDtoHandler;
    }

    public OrderRequestRedirectPaymentMethodDtoHandler getOrderRequestRedirectPaymentMethodDtoHandler() {
        return orderRequestRedirectPaymentMethodDtoHandler;
    }

    public void setOrderRequestRedirectPaymentMethodDtoHandler(final OrderRequestRedirectPaymentMethodDtoHandler orderRequestRedirectPaymentMethodDtoHandler) {
        this.orderRequestRedirectPaymentMethodDtoHandler = orderRequestRedirectPaymentMethodDtoHandler;
    }

    public OrderPaymentService getOrderPaymentService() {
        return orderPaymentService;
    }

    public void setOrderPaymentService(final OrderPaymentService orderPaymentService) {
        this.orderPaymentService = orderPaymentService;
    }
}
