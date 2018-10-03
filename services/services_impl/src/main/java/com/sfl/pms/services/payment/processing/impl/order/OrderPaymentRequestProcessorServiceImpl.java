package com.sfl.pms.services.payment.processing.impl.order;

import com.sfl.pms.persistence.utility.PersistenceUtilityService;
import com.sfl.pms.services.common.exception.ServicesRuntimeException;
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
import com.sfl.pms.services.payment.customer.method.model.CustomerPaymentMethod;
import com.sfl.pms.services.payment.method.group.GroupPaymentMethodDefinitionService;
import com.sfl.pms.services.payment.method.individual.IndividualPaymentMethodDefinitionService;
import com.sfl.pms.services.payment.method.model.PaymentMethodDefinition;
import com.sfl.pms.services.payment.method.model.PaymentMethodType;
import com.sfl.pms.services.payment.processing.PaymentProcessorService;
import com.sfl.pms.services.payment.processing.dto.PaymentProcessingResultDetailedInformationDto;
import com.sfl.pms.services.payment.processing.dto.redirect.PaymentRedirectProcessingInformationDto;
import com.sfl.pms.services.payment.processing.order.OrderPaymentRequestProcessorService;
import com.sfl.pms.services.payment.provider.model.PaymentProviderIntegrationType;
import com.sfl.pms.services.payment.provider.model.PaymentProviderType;
import com.sfl.pms.services.util.mutable.MutableHolder;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * User: Ruben Dilanyan
 * Company: SFL LLC
 * Date: 1/17/15
 * Time: 1:00 PM
 */
@Service
public class OrderPaymentRequestProcessorServiceImpl implements OrderPaymentRequestProcessorService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderPaymentRequestProcessorServiceImpl.class);

    /* Dependencies */
    @Autowired
    private OrderPaymentService orderPaymentService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private CustomerPaymentMethodService customerPaymentMethodService;

    @Autowired
    private OrderPaymentRequestService orderPaymentRequestService;

    @Autowired
    private PaymentProcessorService paymentProcessorService;

    @Autowired
    private PersistenceUtilityService persistenceUtilityService;

    @Autowired
    private IndividualPaymentMethodDefinitionService individualPaymentMethodDefinitionService;

    @Autowired
    private GroupPaymentMethodDefinitionService groupPaymentMethodDefinitionService;

    /* Constructors */
    public OrderPaymentRequestProcessorServiceImpl() {
        LOGGER.debug("Initializing order payment processing service");
    }

    @Nonnull
    @Override
    public Long processOrderPaymentRequest(@Nonnull final Long orderPaymentRequestId) {
        Assert.notNull(orderPaymentRequestId, "Order payment request id should not be null");
        LOGGER.debug("Processing order payment request with id - {}", orderPaymentRequestId);
        // Update state on request
        updatePaymentMethodRequestState(orderPaymentRequestId, OrderPaymentRequestState.PROCESSING, new LinkedHashSet<>(Arrays.asList(OrderPaymentRequestState.CREATED, OrderPaymentRequestState.FAILED)));
        try {
            // Grab data
            final Pair<OrderPaymentRequest, OrderRequestPaymentMethod> paymentMethodPair = loadOrderPaymentRequest(orderPaymentRequestId);
            final OrderPaymentRequest orderPaymentRequest = paymentMethodPair.getKey();
            final OrderRequestPaymentMethod orderRequestPaymentMethod = paymentMethodPair.getValue();
            final Long orderId = orderPaymentRequest.getOrder().getId();
            // Mark that order payment processing is started
            updateOrderState(orderId, OrderState.PAYMENT_PROCESSING_STARTED);
            // Grab data
            LOGGER.debug("Processing payment for order with id - {} and order request payment method - {}", orderId, orderRequestPaymentMethod);
            // Process order payment request payment
            processOrderPaymentRequest(orderPaymentRequest, orderRequestPaymentMethod, orderId);
            // Update state
            updatePaymentMethodRequestState(orderPaymentRequestId, OrderPaymentRequestState.PROCESSED, new LinkedHashSet<>());
            // Return order
            return orderId;
        } catch (final Exception ex) {
            LOGGER.error("Error occurred during processing order payment request - " + orderPaymentRequestId, ex);
            // Update state on request
            updatePaymentMethodRequestState(orderPaymentRequestId, OrderPaymentRequestState.FAILED, new LinkedHashSet<>());
            // Rethrow error
            throw new ServicesRuntimeException(ex);
        }
    }

    /* Utility methods */
    private void processOrderPaymentRequest(final OrderPaymentRequest orderPaymentRequest, final OrderRequestPaymentMethod orderRequestPaymentMethod, final Long orderId) {
        final Order order = orderPaymentRequest.getOrder();
        final String clientIpAddress = orderPaymentRequest.getClientIpAddress();
        // Create order payment
        final OrderPayment payment = createOrderPayment(order.getId(), orderPaymentRequest.getId(), clientIpAddress);
        // Process payment
        final PaymentProcessingResultDetailedInformationDto paymentProcessingResultDto = paymentProcessorService.processPayment(payment.getId());
        // Check if payment processing result is payment redirect
        if (paymentProcessingResultDto instanceof PaymentRedirectProcessingInformationDto) {
            // Cast and grab redirect URL
            final PaymentRedirectProcessingInformationDto paymentRedirectProcessingInformationDto = (PaymentRedirectProcessingInformationDto) paymentProcessingResultDto;
            orderPaymentRequestService.updateOrderPaymentRequestRedirectUrl(orderPaymentRequest.getId(), paymentRedirectProcessingInformationDto.getRedirectUrl());
        }
    }


    private void updatePaymentMethodRequestState(final Long requestId, final OrderPaymentRequestState state, final Set<OrderPaymentRequestState> allowedStates) {
        getPersistenceUtilityService().runInNewTransaction(() -> {
            orderPaymentRequestService.updateOrderPaymentRequestState(requestId, state, allowedStates);
        });
    }

    private void updateOrderState(final Long orderId, final OrderState state) {
        getPersistenceUtilityService().runInNewTransaction(() -> {
            final OrderStateChangeDto orderStateChangeDto = new OrderStateChangeDto();
            orderStateChangeDto.setOrderId(orderId);
            orderStateChangeDto.setOrderStateChangeHistoryRecordDto(new OrderStateChangeHistoryRecordDto(state));
            orderService.updateOrderState(orderStateChangeDto);
        });
    }

    private Pair<OrderPaymentRequest, OrderRequestPaymentMethod> loadOrderPaymentRequest(final Long orderPaymentRequestId) {
        final MutableHolder<Pair<OrderPaymentRequest, OrderRequestPaymentMethod>> orderPaymentRequestMutableHolder = new MutableHolder<>(null);
        getPersistenceUtilityService().runInNewTransaction(() -> {
            final OrderPaymentRequest orderPaymentRequest = orderPaymentRequestService.getOrderPaymentRequestById(orderPaymentRequestId);
            final OrderRequestPaymentMethod orderRequestPaymentMethod = persistenceUtilityService.initializeAndUnProxy(orderPaymentRequest.getPaymentMethod());
            // Set return value
            orderPaymentRequestMutableHolder.setValue(new ImmutablePair<>(orderPaymentRequest, orderRequestPaymentMethod));
        });
        return orderPaymentRequestMutableHolder.getValue();
    }

    private OrderPayment createOrderPayment(final Long orderId, final Long orderPaymentRequestId, final String clientIpAddress) {
        final MutableHolder<OrderPayment> orderPaymentMutableHolder = new MutableHolder<>(null);
        getPersistenceUtilityService().runInNewTransaction(() -> {
            // Prepare data
            final Order order = orderService.getOrderById(orderId);
            final OrderPaymentRequest orderPaymentRequest = orderPaymentRequestService.getOrderPaymentRequestById(orderPaymentRequestId);
            final OrderRequestPaymentMethod orderRequestPaymentMethod = persistenceUtilityService.initializeAndUnProxy(orderPaymentRequest.getPaymentMethod());
            final PaymentCreationInformation paymentCreationInformation = createOrderPaymentProcessingChannelDto(orderRequestPaymentMethod, order);
            final PaymentMethodDefinition paymentMethodDefinition = paymentCreationInformation.getPaymentMethodDefinition();
            final PaymentProcessingChannelDto<? extends PaymentProcessingChannel> paymentProcessingChannelDto = paymentCreationInformation.getPaymentProcessingChannelDto();
            final PaymentProviderType paymentProviderType = paymentCreationInformation.getPaymentProviderType();
            final BigDecimal paymentSurcharge;
            if (paymentMethodDefinition != null) {
                paymentSurcharge = paymentMethodDefinition.getPaymentSurcharge();
            } else {
                paymentSurcharge = BigDecimal.ZERO;
            }
            // Create order payment and DTO
            final OrderPaymentDto orderPaymentDto = new OrderPaymentDto(paymentProviderType, order.getPaymentTotalWithVat(), paymentSurcharge, order.getCurrency(), clientIpAddress, orderPaymentRequest.isStorePaymentMethod());
            final OrderPayment orderPayment = orderPaymentService.createPayment(order.getId(), orderPaymentDto, paymentProcessingChannelDto);
            // Update payment on request
            orderPaymentRequestService.updateOrderPaymentRequestPayment(orderPaymentRequest.getId(), orderPayment.getId());
            LOGGER.debug("Successfully created order payment for order with id - {}, payment processing channel DTO - {}. Order payment - {}", order.getId(), paymentProcessingChannelDto, orderPayment);
            // Set values to holder
            orderPaymentMutableHolder.setValue(orderPayment);
        });
        return orderPaymentMutableHolder.getValue();
    }

    private PaymentCreationInformation createOrderPaymentProcessingChannelDto(final OrderRequestPaymentMethod orderRequestPaymentMethod, final Order order) {
        final PaymentProcessingChannelDto<? extends PaymentProcessingChannel> paymentProcessingChannelDto;
        final PaymentProviderType paymentProviderType;
        final PaymentMethodDefinition paymentMethodDefinition;
        if (orderRequestPaymentMethod instanceof OrderRequestCustomerPaymentMethod) {
            final OrderRequestCustomerPaymentMethod orderRequestCustomerPaymentMethod = (OrderRequestCustomerPaymentMethod) orderRequestPaymentMethod;
            final CustomerPaymentMethod customerPaymentMethod = orderRequestCustomerPaymentMethod.getCustomerPaymentMethod();
            paymentProcessingChannelDto = new CustomerPaymentMethodProcessingChannelDto(PaymentProviderIntegrationType.API, orderRequestCustomerPaymentMethod.getCustomerPaymentMethod().getId());
            paymentProviderType = customerPaymentMethod.getProviderInformation().getType();
            paymentMethodDefinition = individualPaymentMethodDefinitionService.getPaymentMethodDefinitionForLookupParameters(customerPaymentMethod.getPaymentMethodType(), order.getCurrency(), paymentProviderType);
        } else if (orderRequestPaymentMethod instanceof OrderRequestEncryptedPaymentMethod) {
            final OrderRequestEncryptedPaymentMethod orderRequestEncryptedPaymentMethod = (OrderRequestEncryptedPaymentMethod) orderRequestPaymentMethod;
            paymentProcessingChannelDto = new EncryptedPaymentMethodProcessingChannelDto(PaymentProviderIntegrationType.API, orderRequestEncryptedPaymentMethod.getEncryptedData());
            paymentProviderType = orderRequestEncryptedPaymentMethod.getPaymentProviderType();
            paymentMethodDefinition = groupPaymentMethodDefinitionService.getPaymentMethodDefinitionForLookupParameters(orderRequestEncryptedPaymentMethod.getPaymentMethodGroupType(), order.getCurrency(), paymentProviderType);
        } else if (orderRequestPaymentMethod instanceof OrderRequestRedirectPaymentMethod) {
            final OrderRequestRedirectPaymentMethod orderRequestRedirectPaymentMethod = (OrderRequestRedirectPaymentMethod) orderRequestPaymentMethod;
            final PaymentMethodType paymentMethodType = orderRequestRedirectPaymentMethod.getPaymentMethodType();
            paymentProviderType = orderRequestRedirectPaymentMethod.getPaymentProviderType();
            if (paymentMethodType != null) {
                paymentProcessingChannelDto = new ProvidedPaymentMethodProcessingChannelDto(PaymentProviderIntegrationType.REDIRECT, paymentMethodType);
                if(individualPaymentMethodDefinitionService.checkIfPaymentMethodDefinitionExistsForLookupParameters(paymentMethodType, order.getCurrency(), paymentProviderType)) {
                    paymentMethodDefinition = individualPaymentMethodDefinitionService.getPaymentMethodDefinitionForLookupParameters(paymentMethodType, order.getCurrency(), paymentProviderType);
                } else {
                    paymentMethodDefinition = null;
                }
            } else {
                paymentProcessingChannelDto = new DeferredPaymentMethodProcessingChannelDto(PaymentProviderIntegrationType.REDIRECT);
                paymentMethodDefinition = null;
            }
        } else {
            throw new ServicesRuntimeException("Unsupported order request payment method - " + orderRequestPaymentMethod);
        }
        return new PaymentCreationInformation(paymentProcessingChannelDto, paymentProviderType, paymentMethodDefinition);
    }

    /* Properties getters and setters */
    public OrderPaymentService getOrderPaymentService() {
        return orderPaymentService;
    }

    public void setOrderPaymentService(final OrderPaymentService orderPaymentService) {
        this.orderPaymentService = orderPaymentService;
    }

    public OrderService getOrderService() {
        return orderService;
    }

    public void setOrderService(final OrderService orderService) {
        this.orderService = orderService;
    }

    public CustomerPaymentMethodService getCustomerPaymentMethodService() {
        return customerPaymentMethodService;
    }

    public void setCustomerPaymentMethodService(final CustomerPaymentMethodService customerPaymentMethodService) {
        this.customerPaymentMethodService = customerPaymentMethodService;
    }

    public OrderPaymentRequestService getOrderPaymentRequestService() {
        return orderPaymentRequestService;
    }

    public void setOrderPaymentRequestService(final OrderPaymentRequestService orderPaymentRequestService) {
        this.orderPaymentRequestService = orderPaymentRequestService;
    }

    public PaymentProcessorService getPaymentProcessorService() {
        return paymentProcessorService;
    }

    public void setPaymentProcessorService(final PaymentProcessorService paymentProcessorService) {
        this.paymentProcessorService = paymentProcessorService;
    }

    public PersistenceUtilityService getPersistenceUtilityService() {
        return persistenceUtilityService;
    }

    public void setPersistenceUtilityService(final PersistenceUtilityService persistenceUtilityService) {
        this.persistenceUtilityService = persistenceUtilityService;
    }

    public IndividualPaymentMethodDefinitionService getIndividualPaymentMethodDefinitionService() {
        return individualPaymentMethodDefinitionService;
    }

    public void setIndividualPaymentMethodDefinitionService(final IndividualPaymentMethodDefinitionService individualPaymentMethodDefinitionService) {
        this.individualPaymentMethodDefinitionService = individualPaymentMethodDefinitionService;
    }

    public GroupPaymentMethodDefinitionService getGroupPaymentMethodDefinitionService() {
        return groupPaymentMethodDefinitionService;
    }

    public void setGroupPaymentMethodDefinitionService(final GroupPaymentMethodDefinitionService groupPaymentMethodDefinitionService) {
        this.groupPaymentMethodDefinitionService = groupPaymentMethodDefinitionService;
    }


    /* Inner classes */
    private static class PaymentCreationInformation implements Serializable {

        private static final long serialVersionUID = 4596008983251727246L;

        /* Properties  */
        private final PaymentProcessingChannelDto<? extends PaymentProcessingChannel> paymentProcessingChannelDto;

        private final PaymentProviderType paymentProviderType;

        private final PaymentMethodDefinition paymentMethodDefinition;

        /* Constructors */
        public PaymentCreationInformation(final PaymentProcessingChannelDto<? extends PaymentProcessingChannel> paymentProcessingChannelDto, final PaymentProviderType paymentProviderType, final PaymentMethodDefinition paymentMethodDefinition) {
            this.paymentProcessingChannelDto = paymentProcessingChannelDto;
            this.paymentProviderType = paymentProviderType;
            this.paymentMethodDefinition = paymentMethodDefinition;
        }

        public PaymentProcessingChannelDto<? extends PaymentProcessingChannel> getPaymentProcessingChannelDto() {
            return paymentProcessingChannelDto;
        }

        public PaymentProviderType getPaymentProviderType() {
            return paymentProviderType;
        }

        public PaymentMethodDefinition getPaymentMethodDefinition() {
            return paymentMethodDefinition;
        }
    }
}
