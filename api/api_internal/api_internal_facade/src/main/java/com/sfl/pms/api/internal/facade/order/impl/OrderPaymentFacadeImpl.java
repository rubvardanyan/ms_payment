package com.sfl.pms.api.internal.facade.order.impl;

import com.sfl.pms.api.internal.facade.order.OrderPaymentFacade;
import com.sfl.pms.core.api.internal.model.common.result.ErrorResponseModel;
import com.sfl.pms.core.api.internal.model.common.result.ErrorType;
import com.sfl.pms.core.api.internal.model.common.result.ResultResponseModel;
import com.sfl.pms.core.api.internal.model.customer.CustomerModel;
import com.sfl.pms.core.api.internal.model.order.*;
import com.sfl.pms.core.api.internal.model.order.request.CreateOrderRefundRequest;
import com.sfl.pms.core.api.internal.model.order.request.CreateOrderRequest;
import com.sfl.pms.core.api.internal.model.order.request.GetOrderPaymentRequestStatusRequest;
import com.sfl.pms.core.api.internal.model.order.request.RePayOrderRequest;
import com.sfl.pms.core.api.internal.model.order.response.CreateOrderPaymentResponse;
import com.sfl.pms.core.api.internal.model.order.response.CreateOrderRefundResponse;
import com.sfl.pms.core.api.internal.model.order.response.GetOrderPaymentRequestStatusResponse;
import com.sfl.pms.services.common.exception.ServicesRuntimeException;
import com.sfl.pms.services.currency.model.Currency;
import com.sfl.pms.services.customer.CustomerService;
import com.sfl.pms.services.customer.dto.CustomerDto;
import com.sfl.pms.services.customer.model.Customer;
import com.sfl.pms.services.order.OrderService;
import com.sfl.pms.services.order.dto.OrderDto;
import com.sfl.pms.services.order.model.Order;
import com.sfl.pms.services.payment.common.dto.order.request.OrderPaymentRequestDto;
import com.sfl.pms.services.payment.common.dto.order.request.OrderRequestPaymentMethodDto;
import com.sfl.pms.services.payment.common.dto.order.request.OrderRequestRedirectPaymentMethodDto;
import com.sfl.pms.services.payment.common.event.order.StartOrderPaymentRequestProcessingCommandEvent;
import com.sfl.pms.services.payment.common.event.order.StartOrderRefundRequestProcessingCommandEvent;
import com.sfl.pms.services.payment.common.model.order.request.OrderPaymentRequest;
import com.sfl.pms.services.payment.common.model.order.request.OrderPaymentRequestState;
import com.sfl.pms.services.payment.common.model.order.request.OrderRefundRequest;
import com.sfl.pms.services.payment.common.model.order.request.OrderRequestPaymentMethod;
import com.sfl.pms.services.payment.common.order.request.OrderPaymentRequestService;
import com.sfl.pms.services.payment.common.order.request.OrderRefundRequestService;
import com.sfl.pms.services.payment.method.model.PaymentMethodType;
import com.sfl.pms.services.payment.provider.model.PaymentProviderType;
import com.sfl.pms.services.system.event.ApplicationEventDistributionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Ruben Dilanyan
 * Company: SFL LLC
 * Date: 1/19/16
 * Time: 1:57 PM
 */
@Component
public class OrderPaymentFacadeImpl implements OrderPaymentFacade {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderPaymentFacadeImpl.class);

    /* Dependencies */
    @Autowired
    private CustomerService customerService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderPaymentRequestService orderPaymentRequestService;

    @Autowired
    private OrderRefundRequestService orderRefundRequestService;

    @Autowired
    private ApplicationEventDistributionService applicationEventDistributionService;

    /* Constructors */
    public OrderPaymentFacadeImpl() {
    }

    @Nonnull
    @Override
    public ResultResponseModel<CreateOrderPaymentResponse> createOrder(@Nonnull final CreateOrderRequest request) {
        Assert.notNull(request, "Request should not be null");
        LOGGER.debug("Processing order creation request  - {}", request);
        final List<ErrorResponseModel> errors = request.validateRequiredFields();
        if (errors.size() != 0) {
            return new ResultResponseModel<>(errors);
        }
        // Create order DTO
        final OrderModel orderModel = request.getOrder();
        final OrderDto orderDto = new OrderDto(orderModel.getUuId(), orderModel.getPaymentTotalWithVat(), Currency.valueOf(orderModel.getCurrency().name()));
        // Create customer DTO
        final CustomerModel customerModel = request.getCustomer();
        final CustomerDto customerDto = new CustomerDto(customerModel.getUuId(), customerModel.getEmail());
        // Grab customer
        final Customer customer = customerService.getOrCreateCustomerForUuId(customerDto);
        // Create order
        final Order order = orderService.createOrder(customer.getId(), orderDto);
        // Create order payment request
        final OrderPaymentRequest orderPaymentRequest = createOrderPaymentRequestAndStartProcessing(order, request.getPaymentMethod(), request.getStorePaymentMethod(), request.getClientIpAddress());
        // Create response
        final CreateOrderPaymentResponse response = new CreateOrderPaymentResponse(orderPaymentRequest.getUuId());
        LOGGER.debug("Successfully created response - {} for order payment request - {}", response, request);
        return new ResultResponseModel<>(response);
    }

    @Nonnull
    @Override
    public ResultResponseModel<CreateOrderPaymentResponse> rePayOrder(@Nonnull final RePayOrderRequest request) {
        Assert.notNull(request, "Request should not be null");
        LOGGER.debug("Processing order rePayment request  - {}", request);
        List<ErrorResponseModel> errors = request.validateRequiredFields();
        if (errors.size() != 0) {
            return new ResultResponseModel<>(errors);
        }
        // Grab the order
        final Order order = orderService.getOrderByUuId(request.getOrderUuId());
        // Validate that order is not paid
        errors = validateOrderIsNotPaid(order);
        if (errors.size() != 0) {
            return new ResultResponseModel<>(errors);
        }
        // Create order payment request
        final OrderPaymentRequest orderPaymentRequest = createOrderPaymentRequestAndStartProcessing(order, request.getPaymentMethod(), request.getStorePaymentMethod(), request.getClientIpAddress());
        // Create response
        final CreateOrderPaymentResponse response = new CreateOrderPaymentResponse(orderPaymentRequest.getUuId());
        LOGGER.debug("Successfully created response - {} for order rePayment request - {}", response, request);
        return new ResultResponseModel<>(response);
    }

    @Nonnull
    @Override
    public ResultResponseModel<GetOrderPaymentRequestStatusResponse> getOrderPaymentRequestStatus(@Nonnull final GetOrderPaymentRequestStatusRequest request) {
        Assert.notNull(request, "Request should not be null");
        LOGGER.debug("Processing order payment status request - {}", request);
        // Perform validation
        final List<ErrorResponseModel> errors = request.validateRequiredFields();
        if (errors.size() != 0) {
            return new ResultResponseModel<>(errors);
        }
        final OrderPaymentRequest orderPaymentRequest = orderPaymentRequestService.getOrderPaymentRequestByUuId(request.getOrderPaymentRequestUuid());
        // Grab data
        final Order order = orderPaymentRequest.getOrder();
        final OrderPaymentRequestState orderPaymentRequestState = orderPaymentRequest.getState();
        // Build result
        final GetOrderPaymentRequestStatusResponse response = new GetOrderPaymentRequestStatusResponse();
        response.setOrderUuId(order.getUuId());
        response.setOrderPaymentRequestUuId(orderPaymentRequest.getUuId());
        response.setPaymentRedirectUrl(orderPaymentRequest.getPaymentRedirectUrl());
        response.setRequestState(OrderPaymentRequestStateClientType.valueOf(orderPaymentRequestState.name()));
        response.setOrderState(OrderStateClientType.valueOf(order.getLastState().name()));
        if (orderPaymentRequest.getOrderPayment() != null) {
            response.setPaymentUuId(orderPaymentRequest.getOrderPayment().getUuId());
        }
        // Build result
        LOGGER.debug("Successfully processed order payment status retrieval request - {}, response - {}", request, response);
        return new ResultResponseModel<>(response);
    }

    @Nonnull
    @Override
    public ResultResponseModel<CreateOrderRefundResponse> refundOrder(@Nonnull final CreateOrderRefundRequest request) {
        Assert.notNull(request, "Request should not be null");
        LOGGER.debug("Processing refund order  request  - {}", request);
        final List<ErrorResponseModel> errors = request.validateRequiredFields();
        if (errors.size() != 0) {
            return new ResultResponseModel<>(errors);
        }
        final OrderPaymentRequest orderPaymentRequest = orderPaymentRequestService.getOrderPaymentRequestByUuId(request.getOrderPaymentRequestUuid());
        final OrderRefundRequest orderRefundRequest = createOrderRefundRequestAndStartProcessing(orderPaymentRequest.getId());
        // Create response
        final CreateOrderRefundResponse response = new CreateOrderRefundResponse(orderRefundRequest.getUuId());
        LOGGER.debug("Successfully created response - {} for refund order request - {}", response, request);
        return new ResultResponseModel<>(response);
    }

    /* Utility methods */
    private OrderPaymentRequest createOrderPaymentRequestAndStartProcessing(final Order order, final OrderRequestPaymentMethodModel paymentMethodModel, final boolean storePaymentMethod, final String clientIpAddress) {
        // Create order request payment method DTO
        final OrderRequestPaymentMethodDto<? extends OrderRequestPaymentMethod> orderRequestPaymentMethodDto = createOrderRequestPaymentMethodDto(paymentMethodModel);
        final OrderPaymentRequestDto orderPaymentRequestDto = new OrderPaymentRequestDto(clientIpAddress, storePaymentMethod);
        // Create order payment request
        final OrderPaymentRequest orderPaymentRequest = orderPaymentRequestService.createOrderPaymentRequest(order.getId(), orderPaymentRequestDto, orderRequestPaymentMethodDto);
        // Publish event
        applicationEventDistributionService.publishAsynchronousEvent(new StartOrderPaymentRequestProcessingCommandEvent(orderPaymentRequest.getId()));
        return orderPaymentRequest;
    }

    private OrderRefundRequest createOrderRefundRequestAndStartProcessing(final Long orderPaymentRequestId) {
        // Create order refund request
        final OrderRefundRequest orderRefundRequest = orderRefundRequestService.create(orderPaymentRequestId);
        // Publish event
        applicationEventDistributionService.publishAsynchronousEvent(new StartOrderRefundRequestProcessingCommandEvent(orderRefundRequest.getId()));
        return orderRefundRequest;
    }

    private final List<ErrorResponseModel> validateOrderIsNotPaid(final Order order) {
        final List<ErrorResponseModel> errors = new ArrayList<>();
        // Check if order is paid
        final boolean orderPaid = orderService.checkIfOrderIsPaid(order.getId());
        if (orderPaid) {
            errors.add(new ErrorResponseModel(ErrorType.ORDER_PAYMENT_ALREADY_PAID));
        }
        return errors;
    }

    private OrderRequestPaymentMethodDto<? extends OrderRequestPaymentMethod> createOrderRequestPaymentMethodDto(final OrderRequestPaymentMethodModel paymentMethodModel) {
        final OrderRequestPaymentMethodDto<? extends OrderRequestPaymentMethod> orderRequestPaymentMethodDto;
        switch (paymentMethodModel.getType()) {
            case REDIRECT_PAYMENT_METHOD: {
                orderRequestPaymentMethodDto = createOrderRequestRedirectPaymentMethodDto((OrderRequestRedirectPaymentMethodModel) paymentMethodModel);
                break;
            }
            default: {
                final String message = "Unsupported order request payment method type - " + paymentMethodModel.getType();
                LOGGER.error(message);
                throw new ServicesRuntimeException(message);
            }
        }
        return orderRequestPaymentMethodDto;
    }

    private OrderRequestRedirectPaymentMethodDto createOrderRequestRedirectPaymentMethodDto(final OrderRequestRedirectPaymentMethodModel paymentMethodModel) {
        final OrderRequestRedirectPaymentMethodDto methodDto = new OrderRequestRedirectPaymentMethodDto();
        if (paymentMethodModel.getPaymentMethodType() != null) {
            methodDto.setPaymentMethodType(PaymentMethodType.valueOf(paymentMethodModel.getPaymentMethodType().name()));
        }
        methodDto.setPaymentProviderType(PaymentProviderType.valueOf(paymentMethodModel.getPaymentProviderType().name()));
        return methodDto;
    }

    /* Properties getters and setters */
    public CustomerService getCustomerService() {
        return customerService;
    }

    public void setCustomerService(final CustomerService customerService) {
        this.customerService = customerService;
    }

    public OrderService getOrderService() {
        return orderService;
    }

    public void setOrderService(final OrderService orderService) {
        this.orderService = orderService;
    }

    public OrderPaymentRequestService getOrderPaymentRequestService() {
        return orderPaymentRequestService;
    }

    public void setOrderPaymentRequestService(final OrderPaymentRequestService orderPaymentRequestService) {
        this.orderPaymentRequestService = orderPaymentRequestService;
    }

    public ApplicationEventDistributionService getApplicationEventDistributionService() {
        return applicationEventDistributionService;
    }

    public void setApplicationEventDistributionService(final ApplicationEventDistributionService applicationEventDistributionService) {
        this.applicationEventDistributionService = applicationEventDistributionService;
    }
}
