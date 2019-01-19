package com.sfl.pms.services.payment.common.order.request;

import com.sfl.pms.persistence.utility.PersistenceUtilityService;
import com.sfl.pms.services.customer.model.Customer;
import com.sfl.pms.services.order.model.Order;
import com.sfl.pms.services.payment.common.dto.order.request.OrderPaymentRequestDto;
import com.sfl.pms.services.payment.common.dto.order.request.OrderRequestCustomerPaymentMethodDto;
import com.sfl.pms.services.payment.common.dto.order.request.OrderRequestEncryptedPaymentMethodDto;
import com.sfl.pms.services.payment.common.dto.order.request.OrderRequestRedirectPaymentMethodDto;
import com.sfl.pms.services.payment.common.model.order.OrderPayment;
import com.sfl.pms.services.payment.common.model.order.request.*;
import com.sfl.pms.services.payment.customer.method.model.CustomerPaymentMethod;
import com.sfl.pms.services.test.AbstractServiceIntegrationTest;
import com.sfl.pms.services.util.mutable.MutableHolder;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.*;

/**
 * User: Ruben Dilanyan
 * Company: SFL LLC
 * Date: 2/4/15
 * Time: 1:17 PM
 */
public class OrderPaymentRequestServiceIntegrationTest extends AbstractServiceIntegrationTest {

    /* Dependencies */
    @Autowired
    private OrderPaymentRequestService orderPaymentRequestService;

    @Autowired
    private PersistenceUtilityService persistenceUtilityService;

    /* Constructors */
    public OrderPaymentRequestServiceIntegrationTest() {
    }

    /* Test methods */
    @Test
    public void testUpdateOrderPaymentRequestPayment() {
        // Prepare data
        final Order order = getServicesTestHelper().createOrder();
        final OrderPaymentRequest orderPaymentRequest = getServicesTestHelper().createOrderPaymentRequest(order);
        final OrderPayment orderPayment = getServicesTestHelper().createOrderPayment(order);
        flushAndClear();
        // Update payment for the request
        OrderPaymentRequest result = orderPaymentRequestService.updateOrderPaymentRequestPayment(orderPaymentRequest.getId(), orderPayment.getId());
        assertNotNull(result);
        assertNotNull(result.getOrderPayment());
        assertEquals(orderPayment.getId(), result.getOrderPayment().getId());
        // Flush, clear, reload and assert again
        flushAndClear();
        result = orderPaymentRequestService.getOrderPaymentRequestById(orderPaymentRequest.getId());
        assertNotNull(result);
        assertNotNull(result.getOrderPayment());
        assertEquals(orderPayment.getId(), result.getOrderPayment().getId());
    }

    @Test
    public void testUpdateOrderPaymentRequestRedirectUrl() {
        // Prepare data
        final OrderPaymentRequest orderPaymentRequest = getServicesTestHelper().createOrderPaymentRequest();
        assertNull(orderPaymentRequest.getPaymentRedirectUrl());
        final String redirectUrl = "http://live.adyen.com/pay.shtml";
        flushAndClear();
        // Update redirect URL
        OrderPaymentRequest result = orderPaymentRequestService.updateOrderPaymentRequestRedirectUrl(orderPaymentRequest.getId(), redirectUrl);
        assertNotNull(result);
        assertEquals(redirectUrl, result.getPaymentRedirectUrl());
        // Flush, clear, assert and reload
        flushAndClear();
        result = orderPaymentRequestService.getOrderPaymentRequestById(orderPaymentRequest.getId());
        assertNotNull(result);
        assertEquals(redirectUrl, result.getPaymentRedirectUrl());
    }

    @Test
    public void testUpdateOrderPaymentRequestState() {
        // Grab instance
        OrderPaymentRequest request = getServicesTestHelper().createOrderPaymentRequest();
        final OrderPaymentRequestState initialState = request.getState();
        final MutableHolder<OrderPaymentRequestState> newStateHolder = new MutableHolder<>(null);
        Arrays.asList(OrderPaymentRequestState.values()).forEach(currentState -> {
            if (!currentState.equals(initialState)) {
                newStateHolder.setValue(currentState);
            }
        });
        final OrderPaymentRequestState newState = newStateHolder.getValue();
        flushAndClear();
        request = orderPaymentRequestService.updateOrderPaymentRequestState(request.getId(), newState, new HashSet<>(Arrays.asList(initialState)));
        assertEquals(newState, request.getState());
        // FLush, reload and assert
        flushAndClear();
        request = orderPaymentRequestService.getOrderPaymentRequestById(request.getId());
        assertEquals(newState, request.getState());
    }

    @Test
    public void testCreateOrderPaymentRequestWithCustomerPaymentMethod() {
        // Prepare data
        final OrderPaymentRequestDto orderPaymentRequestDto = getServicesTestHelper().createOrderPaymentRequestDto();
        final Customer customer = getServicesTestHelper().createCustomer();
        final Order order = getServicesTestHelper().createOrder(customer);
        final CustomerPaymentMethod customerPaymentMethod = getServicesTestHelper().createCustomerCardPaymentMethod(customer);
        final OrderRequestCustomerPaymentMethodDto paymentMethodDto = getServicesTestHelper().createOrderRequestCustomerPaymentMethodDto(customerPaymentMethod);
        flushAndClear();
        // Create order payment request
        OrderPaymentRequest orderPaymentRequest = orderPaymentRequestService.createOrderPaymentRequest(order.getId(), orderPaymentRequestDto, paymentMethodDto);
        assertCreatedOrderPaymentRequest(orderPaymentRequest, order, orderPaymentRequestDto);
        assertOrderRequestCustomerPaymentMethod(paymentMethodDto, orderPaymentRequest.getPaymentMethod(), customerPaymentMethod);
        // Flush, clear, reload and assert again
        flushAndClear();
        orderPaymentRequest = orderPaymentRequestService.getOrderPaymentRequestById(orderPaymentRequest.getId());
        assertCreatedOrderPaymentRequest(orderPaymentRequest, order, orderPaymentRequestDto);
        assertOrderRequestCustomerPaymentMethod(paymentMethodDto, orderPaymentRequest.getPaymentMethod(), customerPaymentMethod);
    }

    @Test
    public void testCreateOrderPaymentRequestWithRedirectPaymentMethod() {
        // Prepare data
        final OrderPaymentRequestDto orderPaymentRequestDto = getServicesTestHelper().createOrderPaymentRequestDto();
        final Customer customer = getServicesTestHelper().createCustomer();
        final Order order = getServicesTestHelper().createOrder(customer);
        final OrderRequestRedirectPaymentMethodDto paymentMethodDto = getServicesTestHelper().createOrderRequestRedirectPaymentMethodDto();
        flushAndClear();
        // Create order payment request
        OrderPaymentRequest orderPaymentRequest = orderPaymentRequestService.createOrderPaymentRequest(order.getId(), orderPaymentRequestDto, paymentMethodDto);
        assertCreatedOrderPaymentRequest(orderPaymentRequest, order, orderPaymentRequestDto);
        assertOrderRequestRedirectPaymentMethod(paymentMethodDto, orderPaymentRequest.getPaymentMethod());
        // Flush, clear, reload and assert again
        flushAndClear();
        orderPaymentRequest = orderPaymentRequestService.getOrderPaymentRequestById(orderPaymentRequest.getId());
        assertCreatedOrderPaymentRequest(orderPaymentRequest, order, orderPaymentRequestDto);
        assertOrderRequestRedirectPaymentMethod(paymentMethodDto, orderPaymentRequest.getPaymentMethod());
    }

    @Test
    public void testCreateOrderPaymentRequestWithEncryptedPaymentMethod() {
        // Prepare data
        final OrderPaymentRequestDto orderPaymentRequestDto = getServicesTestHelper().createOrderPaymentRequestDto();
        final Customer customer = getServicesTestHelper().createCustomer();
        final Order order = getServicesTestHelper().createOrder(customer);
        final OrderRequestEncryptedPaymentMethodDto paymentMethodDto = getServicesTestHelper().createOrderRequestEncryptedPaymentMethodDto();
        flushAndClear();
        // Create order payment request
        OrderPaymentRequest orderPaymentRequest = orderPaymentRequestService.createOrderPaymentRequest(order.getId(), orderPaymentRequestDto, paymentMethodDto);
        assertCreatedOrderPaymentRequest(orderPaymentRequest, order, orderPaymentRequestDto);
        assertOrderRequestEncryptedPaymentMethod(paymentMethodDto, orderPaymentRequest.getPaymentMethod());
        // Flush, clear, reload and assert again
        flushAndClear();
        orderPaymentRequest = orderPaymentRequestService.getOrderPaymentRequestById(orderPaymentRequest.getId());
        assertCreatedOrderPaymentRequest(orderPaymentRequest, order, orderPaymentRequestDto);
        assertOrderRequestEncryptedPaymentMethod(paymentMethodDto, orderPaymentRequest.getPaymentMethod());
    }

    @Test
    public void testGetOrderPaymentRequestById() {
        // Prepare data
        final OrderPaymentRequest orderPaymentRequest = getServicesTestHelper().createOrderPaymentRequest();
        flushAndClear();
        // Reload and assert
        final OrderPaymentRequest result = orderPaymentRequestService.getOrderPaymentRequestById(orderPaymentRequest.getId());
        assertEquals(orderPaymentRequest, result);
    }

    @Test
    public void getByPaymentId() {
        // Prepare data
        final OrderPaymentRequest orderPaymentRequest = getServicesTestHelper().createOrderPaymentRequest();
        final OrderPayment orderPayment = getServicesTestHelper().createOrderPayment(orderPaymentRequest.getOrder());
        orderPaymentRequestService.updateOrderPaymentRequestPayment(orderPaymentRequest.getId(), orderPayment.getId());
        flushAndClear();
        // Reload and assert
        final OrderPaymentRequest result = orderPaymentRequestService.getByPaymentId(orderPaymentRequest.getOrderPayment().getId());
        assertEquals(orderPaymentRequest, result);
    }


    @Test
    public void testGetOrderPaymentRequestByUuId() {
        // Prepare data
        final OrderPaymentRequest orderPaymentRequest = getServicesTestHelper().createOrderPaymentRequest();
        flushAndClear();
        // Reload and assert
        final OrderPaymentRequest result = orderPaymentRequestService.getOrderPaymentRequestByUuId(orderPaymentRequest.getUuId());
        assertEquals(orderPaymentRequest, result);
    }

    /* Utility methods */
    private void assertCreatedOrderPaymentRequest(final OrderPaymentRequest paymentRequest, final Order order, final OrderPaymentRequestDto requestDto) {
        getServicesTestHelper().assertOrderPaymentRequest(paymentRequest, requestDto);
        assertEquals(OrderPaymentRequestState.CREATED, paymentRequest.getState());
        assertNotNull(paymentRequest.getOrder());
        assertEquals(order.getId(), paymentRequest.getOrder().getId());
    }

    private void assertOrderRequestCustomerPaymentMethod(final OrderRequestCustomerPaymentMethodDto customerPaymentMethodDto, final OrderRequestPaymentMethod requestPaymentMethod, final CustomerPaymentMethod customerPaymentMethod) {
        assertNotNull(requestPaymentMethod);
        final OrderRequestPaymentMethod unProxiedPaymentMethod = persistenceUtilityService.initializeAndUnProxy(requestPaymentMethod);
        assertTrue(unProxiedPaymentMethod instanceof OrderRequestCustomerPaymentMethod);
        final OrderRequestCustomerPaymentMethod orderRequestCustomerPaymentMethod = (OrderRequestCustomerPaymentMethod) unProxiedPaymentMethod;
        getServicesTestHelper().assertOrderRequestCustomerPaymentMethod(orderRequestCustomerPaymentMethod, customerPaymentMethodDto);
        assertNotNull(orderRequestCustomerPaymentMethod.getCustomerPaymentMethod());
        assertEquals(customerPaymentMethod.getId(), orderRequestCustomerPaymentMethod.getCustomerPaymentMethod().getId());
    }

    private void assertOrderRequestRedirectPaymentMethod(final OrderRequestRedirectPaymentMethodDto redirectPaymentMethodDto, final OrderRequestPaymentMethod requestPaymentMethod) {
        assertNotNull(requestPaymentMethod);
        final OrderRequestPaymentMethod unProxiedPaymentMethod = persistenceUtilityService.initializeAndUnProxy(requestPaymentMethod);
        assertTrue(unProxiedPaymentMethod instanceof OrderRequestRedirectPaymentMethod);
        final OrderRequestRedirectPaymentMethod orderRequestRedirectPaymentMethod = (OrderRequestRedirectPaymentMethod) unProxiedPaymentMethod;
        getServicesTestHelper().assertOrderRequestRedirectPaymentMethod(orderRequestRedirectPaymentMethod, redirectPaymentMethodDto);
    }

    private void assertOrderRequestEncryptedPaymentMethod(final OrderRequestEncryptedPaymentMethodDto paymentMethodDto, final OrderRequestPaymentMethod requestPaymentMethod) {
        assertNotNull(requestPaymentMethod);
        final OrderRequestPaymentMethod unProxiedPaymentMethod = persistenceUtilityService.initializeAndUnProxy(requestPaymentMethod);
        assertTrue(unProxiedPaymentMethod instanceof OrderRequestEncryptedPaymentMethod);
        final OrderRequestEncryptedPaymentMethod requestEncryptedPaymentMethod = (OrderRequestEncryptedPaymentMethod) unProxiedPaymentMethod;
        getServicesTestHelper().assertOrderRequestEncryptedPaymentMethod(requestEncryptedPaymentMethod, paymentMethodDto);
    }
}
