package com.sfl.pms.services.payment.processing.impl.order;

import com.sfl.pms.persistence.utility.PersistenceUtilityService;
import com.sfl.pms.services.common.exception.ServicesRuntimeException;
import com.sfl.pms.services.customer.model.Customer;
import com.sfl.pms.services.order.OrderService;
import com.sfl.pms.services.order.dto.OrderStateChangeDto;
import com.sfl.pms.services.order.dto.OrderStateChangeHistoryRecordDto;
import com.sfl.pms.services.order.model.Order;
import com.sfl.pms.services.order.model.OrderState;
import com.sfl.pms.services.payment.common.dto.channel.*;
import com.sfl.pms.services.payment.common.dto.order.OrderPaymentDto;
import com.sfl.pms.services.payment.common.model.channel.PaymentProcessingChannel;
import com.sfl.pms.services.payment.common.model.order.OrderPayment;
import com.sfl.pms.services.payment.common.model.order.request.*;
import com.sfl.pms.services.payment.common.order.OrderPaymentService;
import com.sfl.pms.services.payment.common.order.request.OrderPaymentRequestService;
import com.sfl.pms.services.payment.customer.method.CustomerPaymentMethodService;
import com.sfl.pms.services.payment.customer.method.model.CustomerCardPaymentMethod;
import com.sfl.pms.services.payment.customer.method.model.adyen.CustomerPaymentMethodAdyenInformation;
import com.sfl.pms.services.payment.method.group.GroupPaymentMethodDefinitionService;
import com.sfl.pms.services.payment.method.individual.IndividualPaymentMethodDefinitionService;
import com.sfl.pms.services.payment.method.model.PaymentMethodDefinition;
import com.sfl.pms.services.payment.method.model.PaymentMethodDefinitionType;
import com.sfl.pms.services.payment.method.model.PaymentMethodGroupType;
import com.sfl.pms.services.payment.method.model.PaymentMethodType;
import com.sfl.pms.services.payment.method.model.group.GroupPaymentMethodDefinition;
import com.sfl.pms.services.payment.method.model.individual.IndividualPaymentMethodDefinition;
import com.sfl.pms.services.payment.processing.PaymentProcessorService;
import com.sfl.pms.services.payment.processing.dto.order.OrderPaymentProcessingResultDetailedInformationDto;
import com.sfl.pms.services.payment.processing.dto.redirect.PaymentRedirectProcessingInformationDto;
import com.sfl.pms.services.payment.provider.model.PaymentProviderIntegrationType;
import com.sfl.pms.services.payment.provider.model.PaymentProviderType;
import com.sfl.pms.services.test.AbstractServicesUnitTest;
import org.easymock.EasyMock;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.LinkedHashSet;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.fail;

/**
 * User: Ruben Dilanyan
 * Company: SFL LLC
 * Date: 5/5/15
 * Time: 2:55 PM
 */
public class OrderPaymentRequestProcessorServiceImplTest extends AbstractServicesUnitTest {

    /* Test subject and mocks */
    @TestSubject
    private OrderPaymentRequestProcessorServiceImpl orderPaymentRequestProcessorService = new OrderPaymentRequestProcessorServiceImpl();

    @Mock
    private OrderPaymentService orderPaymentService;

    @Mock
    private OrderService orderService;

    @Mock
    private CustomerPaymentMethodService customerPaymentMethodService;

    @Mock
    private OrderPaymentRequestService orderPaymentRequestService;

    @Mock
    private PaymentProcessorService paymentProcessorService;

    @Mock
    private PersistenceUtilityService persistenceUtilityService;

    @Mock
    private IndividualPaymentMethodDefinitionService individualPaymentMethodDefinitionService;

    @Mock
    private GroupPaymentMethodDefinitionService groupPaymentMethodDefinitionService;

    /* Constructors */
    public OrderPaymentRequestProcessorServiceImplTest() {
    }

    /* Test methods */
    @Test
    public void testProcessOrderPaymentRequestWithInvalidArguments() {
        // Reset
        resetAll();
        // Replay
        replayAll();
        // Run test scenario
        try {
            orderPaymentRequestProcessorService.processOrderPaymentRequest(null);
            fail("Exception should be thrown");
        } catch (final IllegalArgumentException ex) {
            // Expected
        }
        // Verify
        verifyAll();
    }

    @Test
    public void testProcessOrderPaymentRequestWithCustomerPaymentMethod() {
        /* Test data */
        final Long customerCardPaymentMethodId = 2L;
        // Customer payment method
        final CustomerCardPaymentMethod customerCardPaymentMethod = getServicesImplTestHelper().createCustomerCardPaymentMethod();
        customerCardPaymentMethod.setId(customerCardPaymentMethodId);
        // Create order request payment method
        final OrderRequestCustomerPaymentMethod orderRequestCustomerPaymentMethod = getServicesImplTestHelper().createOrderRequestCustomerPaymentMethod();
        orderRequestCustomerPaymentMethod.setCustomerPaymentMethod(customerCardPaymentMethod);
        final Long customerPaymentMethodAdditionalInformationId = 5L;
        // Payment method provider information
        final CustomerPaymentMethodAdyenInformation customerPaymentMethodAdyenInformation = getServicesImplTestHelper().createCustomerPaymentMethodAdyenInformation();
        customerPaymentMethodAdyenInformation.setId(customerPaymentMethodAdditionalInformationId);
        customerCardPaymentMethod.setProviderInformation(customerPaymentMethodAdyenInformation);
        // Create expected processing channel DTO
        final CustomerPaymentMethodProcessingChannelDto paymentMethodProcessingChannelDto = getServicesImplTestHelper().createCustomerPaymentMethodProcessingChannelDto();
        paymentMethodProcessingChannelDto.setCustomerPaymentMethodId(customerCardPaymentMethod.getId());
        paymentMethodProcessingChannelDto.setPaymentProviderIntegrationType(PaymentProviderIntegrationType.API);
        // Create payment method definition
        final Long paymentMethodDefinitionId = 6L;
        final PaymentMethodDefinition paymentMethodDefinition = getServicesImplTestHelper().createIndividualPaymentMethodDefinition();
        paymentMethodDefinition.setId(paymentMethodDefinitionId);
        // Execute actual processing
        testProcessOrderPaymentRequest(orderRequestCustomerPaymentMethod, paymentMethodProcessingChannelDto, customerPaymentMethodAdyenInformation.getType(), paymentMethodDefinition, customerCardPaymentMethod.getPaymentMethodType(), PaymentMethodGroupType.CARD);
    }

    @Test
    public void testProcessOrderPaymentRequestWithEncryptedPaymentMethod() {
        /* Test data */
        // Create order request payment method
        final OrderRequestEncryptedPaymentMethod orderRequestEncryptedPaymentMethod = getServicesImplTestHelper().createOrderRequestEncryptedPaymentMethod();
        // Create expected processing channel DTO
        final EncryptedPaymentMethodProcessingChannelDto paymentMethodProcessingChannelDto = getServicesImplTestHelper().createEncryptedPaymentMethodProcessingChannelDto();
        paymentMethodProcessingChannelDto.setEncryptedPaymentMethodInformation(orderRequestEncryptedPaymentMethod.getEncryptedData());
        paymentMethodProcessingChannelDto.setPaymentProviderIntegrationType(PaymentProviderIntegrationType.API);
        // Create payment method definition
        final Long paymentMethodDefinitionId = 6L;
        final PaymentMethodDefinition paymentMethodDefinition = getServicesImplTestHelper().createGroupPaymentMethodDefinition();
        paymentMethodDefinition.setId(paymentMethodDefinitionId);
        // Execute actual processing
        testProcessOrderPaymentRequest(orderRequestEncryptedPaymentMethod, paymentMethodProcessingChannelDto, orderRequestEncryptedPaymentMethod.getPaymentProviderType(), paymentMethodDefinition, null, orderRequestEncryptedPaymentMethod.getPaymentMethodGroupType());
    }

    @Test
    public void testProcessOrderPaymentRequestWithRedirectPaymentMethodAndSelectedPaymentMethod() {
        /* Test data */
        // Create order request payment method
        final OrderRequestRedirectPaymentMethod orderRequestRedirectPaymentMethod = getServicesImplTestHelper().createOrderRequestRedirectPaymentMethod();
        // Create expected processing channel DTO
        final ProvidedPaymentMethodProcessingChannelDto paymentMethodProcessingChannelDto = getServicesImplTestHelper().createProvidedPaymentMethodProcessingChannelDto();
        paymentMethodProcessingChannelDto.setPaymentMethodType(orderRequestRedirectPaymentMethod.getPaymentMethodType());
        paymentMethodProcessingChannelDto.setPaymentProviderIntegrationType(PaymentProviderIntegrationType.REDIRECT);
        // Payment method definition
        final Long paymentMethodDefinitionId = 7L;
        final IndividualPaymentMethodDefinition paymentMethodDefinition = getServicesImplTestHelper().createIndividualPaymentMethodDefinition();
        paymentMethodDefinition.setId(paymentMethodDefinitionId);
        // Customer
        final Long customerId = 6L;
        final Customer customer = getServicesImplTestHelper().createCustomer();
        customer.setId(customerId);
        // Order payment request
        final Long orderPaymentRequestId = 1L;
        final OrderPaymentRequest orderPaymentRequest = getServicesImplTestHelper().createOrderPaymentRequest();
        orderPaymentRequest.setId(orderPaymentRequestId);
        orderPaymentRequest.setPaymentMethod(orderRequestRedirectPaymentMethod);
        // Order
        final Long orderId = 3L;
        final Order order = getServicesImplTestHelper().createOrder();
        order.setId(orderId);
        order.setCustomer(customer);
        orderPaymentRequest.setOrder(order);
        // Order payment
        final OrderPaymentDto expectedOrderPaymentDto = new OrderPaymentDto(orderRequestRedirectPaymentMethod.getPaymentProviderType(), order.getPaymentTotalWithVat(), paymentMethodDefinition.getPaymentSurcharge(), order.getCurrency(), orderPaymentRequest.getClientIpAddress(), true);
        final Long orderPaymentId = 4L;
        final OrderPayment orderPayment = getServicesImplTestHelper().createOrderPayment();
        orderPayment.setId(orderPaymentId);
        // Create payment
        final Long paymentId = 1L;
        final String redirectUrl = "https://live.adyen.com/pay.shtml";
        // Create payment processing result
        final PaymentRedirectProcessingInformationDto paymentRedirectProcessingInformationDto = new PaymentRedirectProcessingInformationDto(paymentId, redirectUrl);
        final OrderStateChangeDto orderStateChangeDto = new OrderStateChangeDto();
        orderStateChangeDto.setOrderId(orderId);
        orderStateChangeDto.setOrderStateChangeHistoryRecordDto(new OrderStateChangeHistoryRecordDto(OrderState.PAYMENT_PROCESSING_STARTED));
        // Reset
        resetAll();
        // Expectations
        persistenceUtilityService.runInNewTransaction(isA(Runnable.class));
        expectLastCall().andAnswer(() -> {
            final Runnable runnable = (Runnable) getCurrentArguments()[0];
            runnable.run();
            return null;
        }).anyTimes();
        expect(persistenceUtilityService.initializeAndUnProxy(isA(Object.class))).andAnswer(() -> getCurrentArguments()[0]).anyTimes();
        expect(orderPaymentRequestService.getOrderPaymentRequestById(eq(orderPaymentRequestId))).andReturn(orderPaymentRequest).times(2);
        expect(orderService.updateOrderState(eq(orderStateChangeDto))).andReturn(null).once();
        expect(orderPaymentRequestService.updateOrderPaymentRequestState(eq(orderPaymentRequestId), EasyMock.eq(OrderPaymentRequestState.PROCESSING), eq(new LinkedHashSet<>(Arrays.asList(OrderPaymentRequestState.CREATED, OrderPaymentRequestState.FAILED))))).andReturn(orderPaymentRequest).once();
        expect(orderService.getOrderById(eq(orderId))).andReturn(order).once();
        expect(individualPaymentMethodDefinitionService.checkIfPaymentMethodDefinitionExistsForLookupParameters(EasyMock.eq(orderRequestRedirectPaymentMethod.getPaymentMethodType()), eq(order.getCurrency()), EasyMock.eq(orderRequestRedirectPaymentMethod.getPaymentProviderType()))).andReturn(true).once();
        expect(individualPaymentMethodDefinitionService.getPaymentMethodDefinitionForLookupParameters(EasyMock.eq(orderRequestRedirectPaymentMethod.getPaymentMethodType()), eq(order.getCurrency()), EasyMock.eq(orderRequestRedirectPaymentMethod.getPaymentProviderType()))).andReturn(paymentMethodDefinition).once();
        expect(orderPaymentService.createPayment(eq(orderId), eq(expectedOrderPaymentDto), eq(paymentMethodProcessingChannelDto))).andReturn(orderPayment).once();
        expect(paymentProcessorService.processPayment(eq(orderPaymentId))).andReturn(paymentRedirectProcessingInformationDto).once();
        expect(orderPaymentRequestService.updateOrderPaymentRequestRedirectUrl(eq(orderPaymentRequestId), eq(paymentRedirectProcessingInformationDto.getRedirectUrl()))).andReturn(orderPaymentRequest).once();
        expect(orderPaymentRequestService.updateOrderPaymentRequestState(eq(orderPaymentRequestId), eq(OrderPaymentRequestState.PROCESSED), eq(new LinkedHashSet<>()))).andReturn(orderPaymentRequest).once();
        expect(orderPaymentRequestService.updateOrderPaymentRequestPayment(eq(orderPaymentRequestId), eq(orderPaymentId))).andReturn(orderPaymentRequest).once();
        // Replay
        replayAll();
        // Run test scenario
        orderPaymentRequestProcessorService.processOrderPaymentRequest(orderPaymentRequestId);
        // Verify
        verifyAll();
    }

    @Test
    public void testProcessOrderPaymentRequestWithRedirectPaymentMethodAndNotSelectedPaymentMethod() {
        /* Test data */
        // Create order request payment method
        final OrderRequestRedirectPaymentMethod orderRequestRedirectPaymentMethod = getServicesImplTestHelper().createOrderRequestRedirectPaymentMethod();
        orderRequestRedirectPaymentMethod.setPaymentMethodType(null);
        // Create expected processing channel DTO
        final DeferredPaymentMethodProcessingChannelDto paymentMethodProcessingChannelDto = getServicesImplTestHelper().createDeferredPaymentMethodProcessingChannelDto();
        paymentMethodProcessingChannelDto.setPaymentProviderIntegrationType(PaymentProviderIntegrationType.REDIRECT);
        // Customer
        final Long customerId = 6L;
        final Customer customer = getServicesImplTestHelper().createCustomer();
        customer.setId(customerId);
        // Order payment request
        final Long orderPaymentRequestId = 1L;
        final OrderPaymentRequest orderPaymentRequest = getServicesImplTestHelper().createOrderPaymentRequest();
        orderPaymentRequest.setId(orderPaymentRequestId);
        orderPaymentRequest.setPaymentMethod(orderRequestRedirectPaymentMethod);
        // Order
        final Long orderId = 3L;
        final Order order = getServicesImplTestHelper().createOrder();
        order.setId(orderId);
        order.setCustomer(customer);
        orderPaymentRequest.setOrder(order);
        // Order payment
        final OrderPaymentDto expectedOrderPaymentDto = new OrderPaymentDto(orderRequestRedirectPaymentMethod.getPaymentProviderType(), order.getPaymentTotalWithVat(), BigDecimal.ZERO, order.getCurrency(), orderPaymentRequest.getClientIpAddress(), true);
        final Long orderPaymentId = 4L;
        final OrderPayment orderPayment = getServicesImplTestHelper().createOrderPayment();
        orderPayment.setId(orderPaymentId);
        // Create payment
        final Long paymentId = 1L;
        final String redirectUrl = "https://live.adyen.com/pay.shtml";
        // Create payment processing result
        final PaymentRedirectProcessingInformationDto paymentRedirectProcessingInformationDto = new PaymentRedirectProcessingInformationDto(paymentId, redirectUrl);
        final OrderStateChangeDto orderStateChangeDto = new OrderStateChangeDto();
        orderStateChangeDto.setOrderId(orderId);
        orderStateChangeDto.setOrderStateChangeHistoryRecordDto(new OrderStateChangeHistoryRecordDto(OrderState.PAYMENT_PROCESSING_STARTED));
        // Reset
        resetAll();
        // Expectations
        persistenceUtilityService.runInNewTransaction(isA(Runnable.class));
        expectLastCall().andAnswer(() -> {
            final Runnable runnable = (Runnable) getCurrentArguments()[0];
            runnable.run();
            return null;
        }).anyTimes();
        expect(persistenceUtilityService.initializeAndUnProxy(isA(Object.class))).andAnswer(() -> getCurrentArguments()[0]).anyTimes();
        expect(orderPaymentRequestService.getOrderPaymentRequestById(eq(orderPaymentRequestId))).andReturn(orderPaymentRequest).times(2);
        expect(orderService.updateOrderState(eq(orderStateChangeDto))).andReturn(null).once();
        expect(orderPaymentRequestService.updateOrderPaymentRequestState(eq(orderPaymentRequestId), eq(OrderPaymentRequestState.PROCESSING), eq(new LinkedHashSet<>(Arrays.asList(OrderPaymentRequestState.CREATED, OrderPaymentRequestState.FAILED))))).andReturn(orderPaymentRequest).once();
        expect(orderService.getOrderById(eq(orderId))).andReturn(order).once();
        expect(orderPaymentService.createPayment(eq(orderId), eq(expectedOrderPaymentDto), eq(paymentMethodProcessingChannelDto))).andReturn(orderPayment).once();
        expect(paymentProcessorService.processPayment(eq(orderPaymentId))).andReturn(paymentRedirectProcessingInformationDto).once();
        expect(orderPaymentRequestService.updateOrderPaymentRequestRedirectUrl(eq(orderPaymentRequestId), eq(paymentRedirectProcessingInformationDto.getRedirectUrl()))).andReturn(orderPaymentRequest).once();
        expect(orderPaymentRequestService.updateOrderPaymentRequestState(eq(orderPaymentRequestId), eq(OrderPaymentRequestState.PROCESSED), eq(new LinkedHashSet<>()))).andReturn(orderPaymentRequest).once();
        expect(orderPaymentRequestService.updateOrderPaymentRequestPayment(eq(orderPaymentRequestId), eq(orderPaymentId))).andReturn(orderPaymentRequest).once();
        // Replay
        replayAll();
        // Run test scenario
        orderPaymentRequestProcessorService.processOrderPaymentRequest(orderPaymentRequestId);
        // Verify
        verifyAll();
    }

    @Test
    public void testProcessOrderPaymentRequestWhenExceptionOccursDuringProcessing() {
        /* Test data */
        // Customer
        final Long customerId = 6L;
        final Customer customer = getServicesImplTestHelper().createCustomer();
        customer.setId(customerId);
        // Payment method definition
        final Long paymentMethodDefinitionId = 7L;
        final IndividualPaymentMethodDefinition paymentMethodDefinition = getServicesImplTestHelper().createIndividualPaymentMethodDefinition();
        paymentMethodDefinition.setId(paymentMethodDefinitionId);
        // Order payment request
        final Long orderPaymentRequestId = 1L;
        final OrderPaymentRequest orderPaymentRequest = getServicesImplTestHelper().createOrderPaymentRequest();
        orderPaymentRequest.setId(orderPaymentRequestId);
        final Long customerCardPaymentMethodId = 2L;
        // Customer payment method
        final CustomerCardPaymentMethod customerCardPaymentMethod = getServicesImplTestHelper().createCustomerCardPaymentMethod();
        customerCardPaymentMethod.setId(customerCardPaymentMethodId);
        customerCardPaymentMethod.setCustomer(customer);
        final OrderRequestCustomerPaymentMethod orderRequestCustomerPaymentMethod = getServicesImplTestHelper().createOrderRequestCustomerPaymentMethod();
        orderRequestCustomerPaymentMethod.setCustomerPaymentMethod(customerCardPaymentMethod);
        orderPaymentRequest.setPaymentMethod(orderRequestCustomerPaymentMethod);
        final Long customerPaymentMethodAdditionalInformationId = 5L;
        // Payment method provider information
        final CustomerPaymentMethodAdyenInformation customerPaymentMethodAdyenInformation = getServicesImplTestHelper().createCustomerPaymentMethodAdyenInformation();
        customerPaymentMethodAdyenInformation.setId(customerPaymentMethodAdditionalInformationId);
        customerCardPaymentMethod.setProviderInformation(customerPaymentMethodAdyenInformation);
        // Order
        final Long orderId = 3L;
        final Order order = getServicesImplTestHelper().createOrder();
        order.setId(orderId);
        order.setCustomer(customer);
        orderPaymentRequest.setOrder(order);
        // Order payment
        final OrderPaymentDto expectedOrderPaymentDto = new OrderPaymentDto(customerPaymentMethodAdyenInformation.getType(), order.getPaymentTotalWithVat(), paymentMethodDefinition.getPaymentSurcharge(), order.getCurrency(), orderPaymentRequest.getClientIpAddress(), true);
        final Long orderPaymentId = 4L;
        final OrderPayment orderPayment = getServicesImplTestHelper().createOrderPayment();
        orderPayment.setId(orderPaymentId);
        // Payment processing channel
        final CustomerPaymentMethodProcessingChannelDto paymentMethodProcessingChannelDto = getServicesImplTestHelper().createCustomerPaymentMethodProcessingChannelDto();
        paymentMethodProcessingChannelDto.setCustomerPaymentMethodId(customerCardPaymentMethod.getId());
        paymentMethodProcessingChannelDto.setPaymentProviderIntegrationType(PaymentProviderIntegrationType.API);
        final OrderStateChangeDto orderStateChangeDto = new OrderStateChangeDto();
        orderStateChangeDto.setOrderId(orderId);
        orderStateChangeDto.setOrderStateChangeHistoryRecordDto(new OrderStateChangeHistoryRecordDto(OrderState.PAYMENT_PROCESSING_STARTED));
        // Reset
        resetAll();
        // Expectations
        persistenceUtilityService.runInNewTransaction(isA(Runnable.class));
        expectLastCall().andAnswer(() -> {
            final Runnable runnable = (Runnable) getCurrentArguments()[0];
            runnable.run();
            return null;
        }).anyTimes();
        expect(persistenceUtilityService.initializeAndUnProxy(isA(Object.class))).andAnswer(() -> getCurrentArguments()[0]).anyTimes();
        expect(orderPaymentRequestService.getOrderPaymentRequestById(eq(orderPaymentRequestId))).andReturn(orderPaymentRequest).times(2);
        expect(orderPaymentRequestService.updateOrderPaymentRequestState(eq(orderPaymentRequestId), eq(OrderPaymentRequestState.PROCESSING), eq(new LinkedHashSet<>(Arrays.asList(OrderPaymentRequestState.CREATED, OrderPaymentRequestState.FAILED))))).andReturn(orderPaymentRequest).once();
        expect(orderService.getOrderById(eq(orderId))).andReturn(order).once();
        expect(orderService.updateOrderState(eq(orderStateChangeDto))).andReturn(null).once();
        expect(orderPaymentService.createPayment(eq(orderId), eq(expectedOrderPaymentDto), eq(paymentMethodProcessingChannelDto))).andReturn(orderPayment).once();
        expect(paymentProcessorService.processPayment(eq(orderPaymentId))).andThrow(new ServicesRuntimeException("Exception to check status handling")).once();
        expect(orderPaymentRequestService.updateOrderPaymentRequestState(eq(orderPaymentRequestId), eq(OrderPaymentRequestState.FAILED), eq(new LinkedHashSet<>()))).andReturn(orderPaymentRequest).once();
        expect(orderPaymentRequestService.updateOrderPaymentRequestPayment(eq(orderPaymentRequestId), eq(orderPaymentId))).andReturn(orderPaymentRequest).once();
        expect(individualPaymentMethodDefinitionService.getPaymentMethodDefinitionForLookupParameters(EasyMock.eq(customerCardPaymentMethod.getPaymentMethodType()), eq(order.getCurrency()), EasyMock.eq(customerCardPaymentMethod.getProviderInformation().getType()))).andReturn(paymentMethodDefinition).once();
        // Replay
        replayAll();
        // Run test scenario
        try {
            orderPaymentRequestProcessorService.processOrderPaymentRequest(orderPaymentRequestId);
            fail("Exception should ne thrown");
        } catch (final ServicesRuntimeException ex) {
            // Expected
        }
        // Verify
        verifyAll();
    }

    /* Utility methods */
    private void testProcessOrderPaymentRequest(final OrderRequestPaymentMethod orderRequestPaymentMethod, final PaymentProcessingChannelDto<? extends PaymentProcessingChannel> expectedPaymentProcessingChannelDto, final PaymentProviderType paymentProviderType, final PaymentMethodDefinition paymentMethodDefinition, final PaymentMethodType paymentMethodType, final PaymentMethodGroupType paymentMethodGroupType) {
        /* Test data */
        // Customer
        final Long customerId = 6L;
        final Customer customer = getServicesImplTestHelper().createCustomer();
        customer.setId(customerId);
        // Order payment request
        final Long orderPaymentRequestId = 1L;
        final OrderPaymentRequest orderPaymentRequest = getServicesImplTestHelper().createOrderPaymentRequest();
        orderPaymentRequest.setId(orderPaymentRequestId);
        orderPaymentRequest.setPaymentMethod(orderRequestPaymentMethod);
        // Order
        final Long orderId = 3L;
        final Order order = getServicesImplTestHelper().createOrder();
        order.setId(orderId);
        order.setCustomer(customer);
        orderPaymentRequest.setOrder(order);
        // Order payment
        final OrderPaymentDto expectedOrderPaymentDto = new OrderPaymentDto(paymentProviderType, order.getPaymentTotalWithVat(), paymentMethodDefinition.getPaymentSurcharge(), order.getCurrency(), orderPaymentRequest.getClientIpAddress(), orderPaymentRequest.isStorePaymentMethod());
        final Long orderPaymentId = 4L;
        final OrderPayment orderPayment = getServicesImplTestHelper().createOrderPayment();
        orderPayment.setId(orderPaymentId);
        final OrderStateChangeDto orderStateChangeDto = new OrderStateChangeDto();
        orderStateChangeDto.setOrderId(orderId);
        orderStateChangeDto.setOrderStateChangeHistoryRecordDto(new OrderStateChangeHistoryRecordDto(OrderState.PAYMENT_PROCESSING_STARTED));
        // Reset
        resetAll();
        // Expectations
        persistenceUtilityService.runInNewTransaction(isA(Runnable.class));
        expectLastCall().andAnswer(() -> {
            final Runnable runnable = (Runnable) getCurrentArguments()[0];
            runnable.run();
            return null;
        }).anyTimes();
        expect(persistenceUtilityService.initializeAndUnProxy(isA(Object.class))).andAnswer(() -> getCurrentArguments()[0]).anyTimes();
        expect(orderPaymentRequestService.getOrderPaymentRequestById(eq(orderPaymentRequestId))).andReturn(orderPaymentRequest).times(2);
        expect(orderPaymentRequestService.updateOrderPaymentRequestState(eq(orderPaymentRequestId), eq(OrderPaymentRequestState.PROCESSING), eq(new LinkedHashSet<>(Arrays.asList(OrderPaymentRequestState.CREATED, OrderPaymentRequestState.FAILED))))).andReturn(orderPaymentRequest).once();
        expect(orderService.getOrderById(eq(orderId))).andReturn(order).once();
        expect(orderService.updateOrderState(eq(orderStateChangeDto))).andReturn(null).once();
        expect(orderPaymentService.createPayment(eq(orderId), eq(expectedOrderPaymentDto), eq(expectedPaymentProcessingChannelDto))).andReturn(orderPayment).once();
        expect(paymentProcessorService.processPayment(eq(orderPaymentId))).andReturn(new OrderPaymentProcessingResultDetailedInformationDto(orderPaymentId)).once();
        expect(orderPaymentRequestService.updateOrderPaymentRequestState(eq(orderPaymentRequestId), eq(OrderPaymentRequestState.PROCESSED), eq(new LinkedHashSet<>()))).andReturn(orderPaymentRequest).once();
        expect(orderPaymentRequestService.updateOrderPaymentRequestPayment(eq(orderPaymentRequestId), eq(orderPaymentId))).andReturn(orderPaymentRequest).once();
        if (PaymentMethodDefinitionType.INDIVIDUAL.equals(paymentMethodDefinition.getType())) {
            final IndividualPaymentMethodDefinition individualPaymentMethodDefinition = (IndividualPaymentMethodDefinition) paymentMethodDefinition;
            expect(individualPaymentMethodDefinitionService.getPaymentMethodDefinitionForLookupParameters(eq(paymentMethodType), eq(order.getCurrency()), eq(paymentProviderType))).andReturn(individualPaymentMethodDefinition).once();
        } else if (PaymentMethodDefinitionType.GROUP.equals(paymentMethodDefinition.getType())) {
            final GroupPaymentMethodDefinition groupPaymentMethodDefinition = (GroupPaymentMethodDefinition) paymentMethodDefinition;
            expect(groupPaymentMethodDefinitionService.getPaymentMethodDefinitionForLookupParameters(eq(paymentMethodGroupType), eq(order.getCurrency()), eq(paymentProviderType))).andReturn(groupPaymentMethodDefinition).once();
        }
        // Replay
        replayAll();
        // Run test scenario
        orderPaymentRequestProcessorService.processOrderPaymentRequest(orderPaymentRequestId);
        // Verify
        verifyAll();
    }
}
