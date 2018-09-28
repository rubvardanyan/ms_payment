package com.sfl.pms.services.helper;

import com.sfl.pms.externalclients.payment.adyen.model.datatypes.AdyenPaymentStatus;
import com.sfl.pms.persistence.repositories.payment.method.acapture.AcapturePaymentMethodSettingsRepository;
import com.sfl.pms.persistence.repositories.payment.settings.acapture.AcapturePaymentSettingsRepository;
import com.sfl.pms.persistence.utility.PersistenceUtilityService;
import com.sfl.pms.services.common.exception.ServicesRuntimeException;
import com.sfl.pms.services.country.model.CountryCode;
import com.sfl.pms.services.currency.model.Currency;
import com.sfl.pms.services.customer.CustomerService;
import com.sfl.pms.services.customer.dto.CustomerDto;
import com.sfl.pms.services.customer.model.Customer;
import com.sfl.pms.services.order.OrderService;
import com.sfl.pms.services.order.dto.OrderDto;
import com.sfl.pms.services.order.dto.OrderStateChangeHistoryRecordDto;
import com.sfl.pms.services.order.dto.payment.provider.OrderProviderPaymentChannelDto;
import com.sfl.pms.services.order.model.Order;
import com.sfl.pms.services.order.model.OrderState;
import com.sfl.pms.services.order.model.OrderStateChangeHistoryRecord;
import com.sfl.pms.services.order.model.payment.OrderPaymentChannel;
import com.sfl.pms.services.order.model.payment.OrderPaymentChannelType;
import com.sfl.pms.services.order.model.payment.provider.OrderProviderPaymentChannel;
import com.sfl.pms.services.payment.common.auth.CustomerPaymentMethodAuthorizationPaymentService;
import com.sfl.pms.services.payment.common.dto.PaymentDto;
import com.sfl.pms.services.payment.common.dto.PaymentStateChangeHistoryRecordDto;
import com.sfl.pms.services.payment.common.dto.adyen.AdyenPaymentResultDto;
import com.sfl.pms.services.payment.common.dto.auth.CustomerPaymentMethodAuthorizationPaymentDto;
import com.sfl.pms.services.payment.common.dto.channel.*;
import com.sfl.pms.services.payment.common.dto.order.OrderPaymentDto;
import com.sfl.pms.services.payment.common.dto.order.request.*;
import com.sfl.pms.services.payment.common.model.*;
import com.sfl.pms.services.payment.common.model.adyen.AdyenPaymentResult;
import com.sfl.pms.services.payment.common.model.auth.CustomerPaymentMethodAuthorizationPayment;
import com.sfl.pms.services.payment.common.model.channel.*;
import com.sfl.pms.services.payment.common.model.order.OrderPayment;
import com.sfl.pms.services.payment.common.model.order.request.*;
import com.sfl.pms.services.payment.common.order.OrderPaymentService;
import com.sfl.pms.services.payment.common.order.request.OrderPaymentRequestService;
import com.sfl.pms.services.payment.customer.information.adyen.CustomerAdyenInformationService;
import com.sfl.pms.services.payment.customer.information.model.adyen.CustomerAdyenInformation;
import com.sfl.pms.services.payment.customer.method.CustomerBankPaymentMethodService;
import com.sfl.pms.services.payment.customer.method.CustomerCardPaymentMethodService;
import com.sfl.pms.services.payment.customer.method.CustomerPaymentMethodService;
import com.sfl.pms.services.payment.customer.method.CustomerPaymentMethodsSynchronizationService;
import com.sfl.pms.services.payment.customer.method.authorization.CustomerEncryptedPaymentMethodAuthorizationRequestService;
import com.sfl.pms.services.payment.customer.method.authorization.CustomerProvidedPaymentMethodAuthorizationRequestService;
import com.sfl.pms.services.payment.customer.method.dto.CustomerBankPaymentMethodDto;
import com.sfl.pms.services.payment.customer.method.dto.CustomerCardPaymentMethodDto;
import com.sfl.pms.services.payment.customer.method.dto.CustomerPaymentMethodProviderInformationDto;
import com.sfl.pms.services.payment.customer.method.dto.adyen.CustomerPaymentMethodAdyenInformationDto;
import com.sfl.pms.services.payment.customer.method.dto.authorization.CustomerEncryptedPaymentMethodAuthorizationRequestDto;
import com.sfl.pms.services.payment.customer.method.dto.authorization.CustomerPaymentMethodAuthorizationRequestDto;
import com.sfl.pms.services.payment.customer.method.dto.authorization.CustomerProvidedPaymentMethodAuthorizationRequestDto;
import com.sfl.pms.services.payment.customer.method.model.CustomerBankPaymentMethod;
import com.sfl.pms.services.payment.customer.method.model.CustomerCardPaymentMethod;
import com.sfl.pms.services.payment.customer.method.model.CustomerPaymentMethod;
import com.sfl.pms.services.payment.customer.method.model.CustomerPaymentMethodType;
import com.sfl.pms.services.payment.customer.method.model.adyen.CustomerPaymentMethodAdyenInformation;
import com.sfl.pms.services.payment.customer.method.model.authorization.CustomerEncryptedPaymentMethodAuthorizationRequest;
import com.sfl.pms.services.payment.customer.method.model.authorization.CustomerPaymentMethodAuthorizationRequest;
import com.sfl.pms.services.payment.customer.method.model.authorization.CustomerPaymentMethodAuthorizationRequestState;
import com.sfl.pms.services.payment.customer.method.model.authorization.CustomerProvidedPaymentMethodAuthorizationRequest;
import com.sfl.pms.services.payment.encryption.adyen.AdyenPaymentCardEncryptionService;
import com.sfl.pms.services.payment.encryption.dto.PaymentCardEncryptionInformationDto;
import com.sfl.pms.services.payment.method.dto.PaymentMethodDefinitionDto;
import com.sfl.pms.services.payment.method.dto.group.GroupPaymentMethodDefinitionDto;
import com.sfl.pms.services.payment.method.dto.individual.IndividualPaymentMethodDefinitionDto;
import com.sfl.pms.services.payment.method.group.GroupPaymentMethodDefinitionService;
import com.sfl.pms.services.payment.method.individual.IndividualPaymentMethodDefinitionService;
import com.sfl.pms.services.payment.method.model.PaymentMethodDefinition;
import com.sfl.pms.services.payment.method.model.PaymentMethodGroupType;
import com.sfl.pms.services.payment.method.model.PaymentMethodSettings;
import com.sfl.pms.services.payment.method.model.PaymentMethodType;
import com.sfl.pms.services.payment.method.model.acapture.AcapturePaymentMethodSettings;
import com.sfl.pms.services.payment.method.model.adyen.AdyenPaymentMethodType;
import com.sfl.pms.services.payment.method.model.group.GroupPaymentMethodDefinition;
import com.sfl.pms.services.payment.method.model.individual.IndividualPaymentMethodDefinition;
import com.sfl.pms.services.payment.notification.PaymentProviderNotificationRequestService;
import com.sfl.pms.services.payment.notification.adyen.AdyenPaymentProviderNotificationService;
import com.sfl.pms.services.payment.notification.dto.PaymentProviderNotificationRequestDto;
import com.sfl.pms.services.payment.notification.dto.adyen.AdyenPaymentProviderNotificationDto;
import com.sfl.pms.services.payment.notification.model.PaymentProviderNotificationRequest;
import com.sfl.pms.services.payment.notification.model.PaymentProviderNotificationRequestState;
import com.sfl.pms.services.payment.notification.model.PaymentProviderNotificationState;
import com.sfl.pms.services.payment.notification.model.adyen.AdyenPaymentProviderNotification;
import com.sfl.pms.services.payment.processing.auth.CustomerPaymentMethodAuthorizationRequestProcessorService;
import com.sfl.pms.services.payment.processing.dto.PaymentProcessingResultDetailedInformationDto;
import com.sfl.pms.services.payment.processing.dto.auth.CustomerPaymentMethodAuthorizationPaymentProcessingResultDetailedInformationDto;
import com.sfl.pms.services.payment.processing.order.OrderPaymentRequestProcessorService;
import com.sfl.pms.services.payment.provider.adyen.AdyenPaymentProviderIntegrationService;
import com.sfl.pms.services.payment.provider.model.PaymentProviderIntegrationType;
import com.sfl.pms.services.payment.provider.model.PaymentProviderType;
import com.sfl.pms.services.payment.redirect.adyen.AdyenRedirectResultService;
import com.sfl.pms.services.payment.redirect.dto.redirect.AdyenRedirectResultDto;
import com.sfl.pms.services.payment.redirect.model.PaymentProviderRedirectResultState;
import com.sfl.pms.services.payment.redirect.model.adyen.AdyenRedirectResult;
import com.sfl.pms.services.payment.settings.adyen.AdyenPaymentSettingsService;
import com.sfl.pms.services.payment.settings.dto.adyen.AdyenPaymentSettingsDto;
import com.sfl.pms.services.payment.settings.model.acapture.AcapturePaymentSettings;
import com.sfl.pms.services.payment.settings.model.adyen.AdyenPaymentSettings;
import com.sfl.pms.services.system.environment.model.EnvironmentType;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableInt;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * User: Ruben Dilanyan
 * Company: SFL LLC
 * Date: 10/01/14
 * Time: 10:18 PM
 */
@Component
public class ServicesTestHelper {


    public static final float COMPARISON_DOUBLE_PRECISION = 0.001f;

    public static final int CARD_EXPIRY_YEAR = 2017;

    public static final int CARD_EXPIRY_MONTH = 11;

    public static final int ADYEN_CARD_CREATION_WAIT_PERIOD = 2000;

    public static final int ADYEN_CARD_CREATION_MAX_ATTEMPTS = 5;

    /* Constants */
    private static final String ADYEN_NOTIFICATION_RAW_CONTENT = "{\"live\":\"false\",\"notificationItems\":[{\"NotificationRequestItem\":{\"additionalData\":{\"expiryDate\":\"8\\/2018\",\"issuerCountry\":\"US\",\"authCode\":\"{authCode}\",\"cardSummary\":\"0008\",\"cardHolderName\":\"Adyen Credit Card Holder\",\"shopperInteraction\":\"ContAuth\",\"shopperEmail\":\"dummy@dummy.com\",\"hmacSignature\":\"v6st1580kZtvNqsDNYUBifxdJVWIS6YG7Tw9TRF4gAs=\",\"shopperReference\":\"1f14a2e5-f51c-481d-a782-0841728af3c9\"},\"amount\":{\"currency\":\"EUR\",\"value\":1300},\"eventCode\":\"AUTHORISATION\",\"eventDate\":\"2015-04-28T10:23:13+02:00\",\"merchantAccountCode\":\"SFLPRO\",\"merchantReference\":\"{paymentUuid}\",\"operations\":[\"CANCEL\",\"CAPTURE\",\"REFUND\"],\"paymentMethod\":\"visa\",\"pspReference\":\"{pspReference}\",\"reason\":\"88531:0008:8\\/2018\",\"success\":\"true\"}}]}";

    private static final String EMAIL = "dummy@dummy.com";

    private static final String IP_ADDRESS = "127.0.0.1";

    private static final String ADYEN_CARD_HOLDER_NAME = "Ruben Dilanyan";

    private static final String ADYEN_CARD_EXPIRY_MONTH = "06";

    private static final String ADYEN_CARD_EXPIRY_YEAR = "2016";

    private static final String ADYEN_CARD_CVC = "737";

    private static final String ADYEN_CARD_AMEX_CVC = "7373";

    private static final String ADYEN_CARD_MASTER_NUMBER = "5100 2900 2900 2909";

    private static final String ADYEN_CARD_VISA_NUMBER = "4111 1111 1111 1111";

    private static final String ADYEN_CARD_AMEX_NUMBER = "3700 0000 0000 002";

    private static final String ADYEN_CARD_DINERS_CLUB_NUMBER = "3600 6666 3333 44";

    private static final String ADYEN_CARD_DISCOVER_NUMBER = "6011 6011 6011 6611";

    /* Configuration properties */
    @Value("#{ appProperties['adyen.environment.type']}")
    private String environmentType;

    /* Dependencies */
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private CustomerEncryptedPaymentMethodAuthorizationRequestService customerEncryptedPaymentMethodAuthorizationRequestService;

    @Autowired
    private CustomerProvidedPaymentMethodAuthorizationRequestService customerProvidedPaymentMethodAuthorizationRequestService;

    @Autowired
    private CustomerCardPaymentMethodService customerCardPaymentMethodService;

    @Autowired
    private AdyenPaymentSettingsService adyenPaymentSettingsService;

    @Autowired
    private CustomerAdyenInformationService customerAdyenInformationService;

    @Autowired
    private OrderPaymentService orderPaymentService;

    @Autowired
    private CustomerPaymentMethodAuthorizationPaymentService customerPaymentMethodAuthorizationPaymentService;

    @Autowired
    private CustomerPaymentMethodAuthorizationRequestProcessorService customerPaymentMethodAuthorizationRequestProcessorService;

    @Autowired
    private CustomerPaymentMethodService customerPaymentMethodService;

    @Autowired
    private PersistenceUtilityService persistenceUtilityService;

    @Autowired
    private AdyenPaymentCardEncryptionService adyenPaymentCardEncryptionService;

    @Autowired
    private OrderPaymentRequestService orderPaymentRequestService;

    @Autowired
    private PaymentProviderNotificationRequestService paymentProviderNotificationRequestService;

    @Autowired
    private AdyenPaymentProviderNotificationService adyenPaymentProviderNotificationService;

    @Autowired
    private OrderPaymentRequestProcessorService orderPaymentRequestProcessorService;

    @Autowired
    private CustomerBankPaymentMethodService customerBankPaymentMethodService;

    @Autowired
    private AdyenRedirectResultService adyenRedirectResultService;

    @Autowired
    private AdyenPaymentProviderIntegrationService adyenPaymentProviderIntegrationService;

    @Autowired
    private CustomerPaymentMethodsSynchronizationService customerPaymentMethodsSynchronizationService;

    @Autowired
    private IndividualPaymentMethodDefinitionService individualPaymentMethodDefinitionService;

    @Autowired
    private GroupPaymentMethodDefinitionService groupPaymentMethodDefinitionService;

    @Autowired
    private AcapturePaymentSettingsRepository acapturePaymentSettingsRepository;

    @Autowired
    private AcapturePaymentMethodSettingsRepository acapturePaymentMethodSettingsRepository;

    /* Constructors */
    public ServicesTestHelper() {
    }

    /* Helper methods */


    /* Customer */
    public CustomerDto createCustomerDto() {
        final CustomerDto userDto = new CustomerDto();
        userDto.setUuId(UUID.randomUUID().toString());
        userDto.setEmail(EMAIL);
        return userDto;
    }

    public Customer createCustomer(final CustomerDto userDto) {
        return customerService.createCustomer(userDto);
    }

    public Customer createCustomer(final String email) {
        final CustomerDto customerDto = createCustomerDto();
        customerDto.setEmail(email);
        return createCustomer(customerDto);
    }

    public Customer createCustomer() {
        return createCustomer(createCustomerDto());
    }

    public void assertCustomer(final Customer user, final CustomerDto userDto) {
        assertNotNull(user);
        assertEquals(userDto.getUuId(), user.getUuId());
        assertEquals(userDto.getEmail(), user.getEmail());
    }


    /* Order */
    public OrderDto createOrderDto() {
        final OrderDto orderDto = new OrderDto();
        orderDto.setCurrency(Currency.EUR);
        orderDto.setPaymentTotalWithVat(BigDecimal.TEN);
        orderDto.setUuId(UUID.randomUUID().toString());
        return orderDto;
    }

    public Order createOrder(final Customer customer, final OrderDto orderDto) {
        return orderService.createOrder(customer.getId(), orderDto);
    }

    public Order createOrder(final Customer customer) {
        return orderService.createOrder(customer.getId(), createOrderDto());
    }

    public Order createOrder() {
        return createOrder(createCustomer(), createOrderDto());
    }

    public void assertOrder(final Order order, final OrderDto orderDto) {
        assertEquals(orderDto.getUuId(), order.getUuId());
        assertEquals(orderDto.getCurrency(), order.getCurrency());
        assertNotNull(order.getPaymentTotalWithVat());
        assertEquals(0, orderDto.getPaymentTotalWithVat().compareTo(order.getPaymentTotalWithVat()));
    }

    public void assertCreatedOrder(final Order order, final Customer customer, final OrderDto orderDto) {
        assertNotNull(order);
        // Assert order fields
        assertOrder(order, orderDto);
        // Assert customer
        assertNotNull(order.getCustomer());
        assertEquals(customer.getId(), order.getCustomer().getId());
        // Assert order state
        assertEquals(1, order.getStateChangeHistoryRecords().size());
        assertOrderLastState(order, null, OrderState.CREATED, 1);
    }

    public void assertOrderLastState(final Order order, final OrderState previousState, final OrderState orderState, final int expectedRecordsCount) {
        assertEquals(orderState, order.getLastState());
        assertEquals(expectedRecordsCount, order.getStateChangeHistoryRecords().size());
        // Check if state was found in list
        boolean found = false;
        for (final OrderStateChangeHistoryRecord historyRecord : order.getStateChangeHistoryRecords()) {
            if (orderState.equals(historyRecord.getUpdatedState()) && previousState == historyRecord.getInitialState()) {
                found = true;
                break;
            }
        }
        assertTrue(found);
    }

    /* Order state change history record DTO */
    public OrderStateChangeHistoryRecordDto createOrderStateChangeHistoryRecordDto() {
        final OrderStateChangeHistoryRecordDto recordDto = new OrderStateChangeHistoryRecordDto();
        recordDto.setUpdatedState(OrderState.PAYMENT_PROCESSING_STARTED);
        return recordDto;
    }

    public void assertOrderStateChangeHistoryRecordDto(final OrderStateChangeHistoryRecord historyRecord, OrderStateChangeHistoryRecordDto historyRecordDto) {
        assertNotNull(historyRecord);
        assertEquals(historyRecordDto.getUpdatedState(), historyRecord.getUpdatedState());
    }

    /* Customer Encrypted payment method authorization request */
    public CustomerEncryptedPaymentMethodAuthorizationRequestDto createCustomerEncryptedPaymentMethodAuthorizationRequestDto() {
        final CustomerEncryptedPaymentMethodAuthorizationRequestDto paymentMethodAuthorizationRequestDto = new CustomerEncryptedPaymentMethodAuthorizationRequestDto();
        paymentMethodAuthorizationRequestDto.setPaymentProviderType(PaymentProviderType.ADYEN);
        paymentMethodAuthorizationRequestDto.setPaymentProviderIntegrationType(PaymentProviderIntegrationType.API);
        paymentMethodAuthorizationRequestDto.setEncryptedData("veerrrryvveeerrrryscaryencrypteddata");
        paymentMethodAuthorizationRequestDto.setPaymentMethodGroup(PaymentMethodGroupType.CARD);
        paymentMethodAuthorizationRequestDto.setClientIpAddress(IP_ADDRESS);
        paymentMethodAuthorizationRequestDto.setCurrency(Currency.EUR);
        return paymentMethodAuthorizationRequestDto;
    }

    public CustomerEncryptedPaymentMethodAuthorizationRequest createCustomerEncryptedPaymentMethodAuthorizationRequest() {
        return createCustomerEncryptedPaymentMethodAuthorizationRequest(createCustomerEncryptedPaymentMethodAuthorizationRequestDto());
    }

    public CustomerEncryptedPaymentMethodAuthorizationRequest createCustomerEncryptedPaymentMethodAuthorizationRequest(final CustomerEncryptedPaymentMethodAuthorizationRequestDto requestDto) {
        return createCustomerEncryptedPaymentMethodAuthorizationRequest(requestDto, createCustomer());
    }

    public CustomerEncryptedPaymentMethodAuthorizationRequest createCustomerEncryptedPaymentMethodAuthorizationRequest(final CustomerEncryptedPaymentMethodAuthorizationRequestDto requestDto, final Customer customer) {
        return createCustomerEncryptedPaymentMethodAuthorizationRequest(customer, requestDto);
    }

    public CustomerEncryptedPaymentMethodAuthorizationRequest createCustomerEncryptedPaymentMethodAuthorizationRequest(final Customer customer, final CustomerEncryptedPaymentMethodAuthorizationRequestDto requestDto) {
        return customerEncryptedPaymentMethodAuthorizationRequestService.createPaymentMethodAuthorizationRequest(customer.getId(), requestDto);
    }

    public void assertCustomerEncryptedPaymentMethodAuthorizationRequest(final CustomerEncryptedPaymentMethodAuthorizationRequest authorizationRequest, final CustomerEncryptedPaymentMethodAuthorizationRequestDto authorizationRequestDto) {
        assertCustomerPaymentMethodAuthorizationRequest(authorizationRequest, authorizationRequestDto);
        assertEquals(authorizationRequestDto.getEncryptedData(), authorizationRequest.getEncryptedData());
        assertEquals(authorizationRequestDto.getPaymentMethodGroup(), authorizationRequest.getPaymentMethodGroup());
    }

    public void assertCustomerPaymentMethodAuthorizationRequest(final CustomerPaymentMethodAuthorizationRequest authorizationRequest, final CustomerPaymentMethodAuthorizationRequestDto<? extends CustomerPaymentMethodAuthorizationRequest> authorizationRequestDto) {
        assertNotNull(authorizationRequest);
        assertEquals(authorizationRequestDto.getType(), authorizationRequest.getType());
        assertEquals(CustomerPaymentMethodAuthorizationRequestState.CREATED, authorizationRequest.getState());
        assertEquals(authorizationRequestDto.getPaymentProviderType(), authorizationRequest.getPaymentProviderType());
        assertEquals(authorizationRequestDto.getPaymentProviderIntegrationType(), authorizationRequest.getPaymentProviderIntegrationType());
        assertEquals(authorizationRequestDto.getClientIpAddress(), authorizationRequest.getClientIpAddress());
        assertEquals(authorizationRequestDto.getCurrency(), authorizationRequest.getCurrency());
    }

    /* Customer Provided payment method authorization request */
    public CustomerProvidedPaymentMethodAuthorizationRequestDto createCustomerProvidedPaymentMethodAuthorizationRequestDto() {
        final CustomerProvidedPaymentMethodAuthorizationRequestDto paymentMethodAuthorizationRequestDto = new CustomerProvidedPaymentMethodAuthorizationRequestDto();
        paymentMethodAuthorizationRequestDto.setPaymentProviderType(PaymentProviderType.ADYEN);
        paymentMethodAuthorizationRequestDto.setPaymentProviderIntegrationType(PaymentProviderIntegrationType.REDIRECT);
        paymentMethodAuthorizationRequestDto.setPaymentMethodType(PaymentMethodType.IDEAL);
        paymentMethodAuthorizationRequestDto.setClientIpAddress("127.0.0.1");
        paymentMethodAuthorizationRequestDto.setCurrency(Currency.EUR);
        return paymentMethodAuthorizationRequestDto;
    }

    public CustomerProvidedPaymentMethodAuthorizationRequest createCustomerProvidedPaymentMethodAuthorizationRequest() {
        return createCustomerProvidedPaymentMethodAuthorizationRequest(createCustomer(), createCustomerProvidedPaymentMethodAuthorizationRequestDto());
    }

    public CustomerProvidedPaymentMethodAuthorizationRequest createCustomerProvidedPaymentMethodAuthorizationRequest(final Customer customer, final CustomerProvidedPaymentMethodAuthorizationRequestDto requestDto) {
        return customerProvidedPaymentMethodAuthorizationRequestService.createPaymentMethodAuthorizationRequest(customer.getId(), requestDto);
    }

    public void assertCustomerProvidedPaymentMethodAuthorizationRequest(final CustomerProvidedPaymentMethodAuthorizationRequest authorizationRequest, final CustomerProvidedPaymentMethodAuthorizationRequestDto authorizationRequestDto) {
        assertCustomerPaymentMethodAuthorizationRequest(authorizationRequest, authorizationRequestDto);
        assertEquals(authorizationRequestDto.getPaymentMethodType(), authorizationRequest.getPaymentMethodType());
    }

    /* Customer card payment method */
    public CustomerCardPaymentMethodDto createCustomerCardPaymentMethodDto() {
        final CustomerCardPaymentMethodDto paymentMethodDto = new CustomerCardPaymentMethodDto();
        paymentMethodDto.setPaymentMethodType(PaymentMethodType.MASTER_CARD);
        paymentMethodDto.setExpiryYear(CARD_EXPIRY_YEAR);
        paymentMethodDto.setExpiryMonth(CARD_EXPIRY_MONTH);
        paymentMethodDto.setHolderName("Mr Ruben Dilanyan");
        paymentMethodDto.setNumberTail("6972");
        return paymentMethodDto;
    }

    public CustomerCardPaymentMethod createCustomerCardPaymentMethod(final CustomerCardPaymentMethodDto paymentMethodDto, final CustomerPaymentMethodProviderInformationDto<? extends CustomerPaymentMethodAdyenInformation> providerInformationDto) {
        final CustomerPaymentMethodAuthorizationRequest authorizationRequest = createCustomerEncryptedPaymentMethodAuthorizationRequest();
        final Customer customer = authorizationRequest.getCustomer();
        // Create payment method
        return customerCardPaymentMethodService.createCustomerPaymentMethod(customer.getId(), authorizationRequest.getId(), paymentMethodDto, providerInformationDto);
    }

    public CustomerCardPaymentMethod createCustomerCardPaymentMethod(final CustomerPaymentMethodAuthorizationRequest authorizationRequest, final CustomerCardPaymentMethodDto paymentMethodDto, final CustomerPaymentMethodProviderInformationDto<? extends CustomerPaymentMethodAdyenInformation> providerInformationDto) {
        final Customer customer = authorizationRequest.getCustomer();
        // Create payment method
        return customerCardPaymentMethodService.createCustomerPaymentMethod(customer.getId(), authorizationRequest.getId(), paymentMethodDto, providerInformationDto);
    }

    public CustomerCardPaymentMethod createCustomerCardPaymentMethod(final Customer customer) {
        final CustomerPaymentMethodAuthorizationRequest request = createCustomerEncryptedPaymentMethodAuthorizationRequest(customer, createCustomerEncryptedPaymentMethodAuthorizationRequestDto());
        return createCustomerCardPaymentMethod(request, createCustomerCardPaymentMethodDto(), createCustomerPaymentMethodAdyenInformationDto());
    }

    public CustomerCardPaymentMethod createCustomerCardPaymentMethod() {
        return createCustomerCardPaymentMethod(createCustomerCardPaymentMethodDto(), createCustomerPaymentMethodAdyenInformationDto());
    }

    public void assertCustomerCardPaymentMethod(final CustomerCardPaymentMethod paymentMethod, final CustomerCardPaymentMethodDto paymentMethodDto) {
        assertEquals(CustomerPaymentMethodType.CARD, paymentMethod.getType());
        assertEquals(paymentMethodDto.getExpiryMonth(), paymentMethod.getExpiryMonth());
        assertEquals(paymentMethodDto.getExpiryYear(), paymentMethod.getExpiryYear());
        assertEquals(paymentMethodDto.getHolderName(), paymentMethod.getHolderName());
        assertEquals(paymentMethodDto.getNumberTail(), paymentMethod.getNumberTail());
        assertEquals(paymentMethodDto.getPaymentMethodType(), paymentMethod.getPaymentMethodType());
    }

    /* Customer bank payment method */
    public CustomerBankPaymentMethodDto createCustomerBankPaymentMethodDto() {
        final CustomerBankPaymentMethodDto paymentMethodDto = new CustomerBankPaymentMethodDto();
        paymentMethodDto.setPaymentMethodType(PaymentMethodType.IDEAL);
        paymentMethodDto.setBankAccountNumber("1234-5678-90123");
        paymentMethodDto.setBankName("HSBC");
        paymentMethodDto.setOwnerName("Ruben Dilanyan");
        paymentMethodDto.setCountryCode(CountryCode.AM);
        paymentMethodDto.setIban("987654321");
        paymentMethodDto.setBic("TESTNL01");
        return paymentMethodDto;
    }

    public CustomerBankPaymentMethod createCustomerBankPaymentMethod(final CustomerBankPaymentMethodDto paymentMethodDto, final CustomerPaymentMethodProviderInformationDto<? extends CustomerPaymentMethodAdyenInformation> providerInformationDto) {
        final CustomerPaymentMethodAuthorizationRequest authorizationRequest = createCustomerEncryptedPaymentMethodAuthorizationRequest();
        final Customer customer = authorizationRequest.getCustomer();
        // Create payment method
        return customerBankPaymentMethodService.createCustomerPaymentMethod(customer.getId(), authorizationRequest.getId(), paymentMethodDto, providerInformationDto);
    }

    public CustomerBankPaymentMethod createCustomerBankPaymentMethod(final CustomerPaymentMethodAuthorizationRequest authorizationRequest, final CustomerBankPaymentMethodDto paymentMethodDto, final CustomerPaymentMethodProviderInformationDto<? extends CustomerPaymentMethodAdyenInformation> providerInformationDto) {
        final Customer customer = authorizationRequest.getCustomer();
        // Create payment method
        return customerBankPaymentMethodService.createCustomerPaymentMethod(customer.getId(), authorizationRequest.getId(), paymentMethodDto, providerInformationDto);
    }

    public CustomerBankPaymentMethod createCustomerBankPaymentMethod(final Customer customer) {
        final CustomerPaymentMethodAuthorizationRequest request = createCustomerEncryptedPaymentMethodAuthorizationRequest(customer, createCustomerEncryptedPaymentMethodAuthorizationRequestDto());
        return createCustomerBankPaymentMethod(request, createCustomerBankPaymentMethodDto(), createCustomerPaymentMethodAdyenInformationDto());
    }

    public CustomerBankPaymentMethod createCustomerBankPaymentMethod() {
        return createCustomerBankPaymentMethod(createCustomerBankPaymentMethodDto(), createCustomerPaymentMethodAdyenInformationDto());
    }

    public void assertCustomerBankPaymentMethod(final CustomerBankPaymentMethod paymentMethod, final CustomerBankPaymentMethodDto paymentMethodDto) {
        assertEquals(CustomerPaymentMethodType.BANK, paymentMethod.getType());
        assertEquals(paymentMethodDto.getBankAccountNumber(), paymentMethod.getBankAccountNumber());
        assertEquals(paymentMethodDto.getBankName(), paymentMethod.getBankName());
        assertEquals(paymentMethodDto.getOwnerName(), paymentMethod.getOwnerName());
        assertEquals(paymentMethodDto.getCountryCode(), paymentMethod.getCountryCode());
        assertEquals(paymentMethodDto.getIban(), paymentMethod.getIban());
        assertEquals(paymentMethodDto.getBic(), paymentMethod.getBic());
        assertEquals(paymentMethodDto.getPaymentMethodType(), paymentMethod.getPaymentMethodType());
    }

    public CustomerPaymentMethodAdyenInformationDto createCustomerPaymentMethodAdyenInformationDto() {
        final CustomerPaymentMethodAdyenInformationDto informationDto = new CustomerPaymentMethodAdyenInformationDto();
        informationDto.setRecurringDetailReference("KHJG-YYTR-UTWX-NBGT" + Math.random());
        return informationDto;
    }

    public void assertCustomerPaymentMethodAdyenInformation(final CustomerPaymentMethodAdyenInformation information, final CustomerPaymentMethodAdyenInformationDto informationDto) {
        assertEquals(informationDto.getRecurringDetailReference(), information.getRecurringDetailReference());
    }

    /* Adyen payment settings */
    public AdyenPaymentSettingsDto createAdyenPaymentSettingsDto() {
        final AdyenPaymentSettingsDto paymentSettingsDto = new AdyenPaymentSettingsDto();
        paymentSettingsDto.setEnvironmentType(EnvironmentType.valueOf(environmentType.toUpperCase()));
        paymentSettingsDto.setUserName("sfl");
        paymentSettingsDto.setPassword("sfl123456");
        paymentSettingsDto.setSkinCode("7eNyxPhj");
        paymentSettingsDto.setSkinHmacKey("F43B5DED28093CA8FE8255340FDAE6BA51CA77E538017DF407D1A8728BFA1E91");
        paymentSettingsDto.setHppPaymentUrl("https://test.adyen.com/hpp/pay.shtml");
        paymentSettingsDto.setHppPaymentMethodSelectionUrl("https://test.adyen.com/hpp/pay.shtml");
        paymentSettingsDto.setDefaultCountry(CountryCode.NL);
        paymentSettingsDto.setMerchantAccount("BlueInktNL");
        paymentSettingsDto.setDefaultLocale("nl-NL");
        paymentSettingsDto.setNotificationsToken("HVFJFJFDRJRTYRETDTIF^FJXDXKTY&TOYGYTFTYDRDTYRRDTYTYLUYGYUFITYFOYFITD^%DTCRXURDDTRXTKYFOTFLYFTDTICTRJ");
        return paymentSettingsDto;
    }

    public AdyenPaymentSettings createAdyenPaymentSettings(final AdyenPaymentSettingsDto paymentSettingsDto) {
        return adyenPaymentSettingsService.createPaymentProviderSettings(paymentSettingsDto);
    }

    public AdyenPaymentSettings createAdyenPaymentSettings() {
        return createAdyenPaymentSettings(createAdyenPaymentSettingsDto());
    }

    public void assertAdyenPaymentSettings(final AdyenPaymentSettings paymentSettings, final AdyenPaymentSettingsDto paymentSettingsDto) {
        assertNotNull(paymentSettings);
        assertEquals(paymentSettingsDto.getEnvironmentType(), paymentSettings.getEnvironmentType());
        assertEquals(paymentSettingsDto.getUserName(), paymentSettings.getUserName());
        assertEquals(paymentSettingsDto.getPassword(), paymentSettings.getPassword());
        assertEquals(paymentSettingsDto.getSkinCode(), paymentSettings.getSkinCode());
        assertEquals(paymentSettingsDto.getSkinHmacKey(), paymentSettings.getSkinHmacKey());
        assertEquals(paymentSettingsDto.getHppPaymentUrl(), paymentSettings.getHppPaymentUrl());
        assertEquals(paymentSettingsDto.getHppPaymentMethodSelectionUrl(), paymentSettings.getHppPaymentMethodSelectionUrl());
        assertEquals(paymentSettingsDto.getDefaultCountry(), paymentSettings.getDefaultCountry());
        assertEquals(paymentSettingsDto.getMerchantAccount(), paymentSettings.getMerchantAccount());
        assertEquals(paymentSettingsDto.getDefaultLocale(), paymentSettings.getDefaultLocale());
        assertEquals(paymentSettingsDto.getNotificationsToken(), paymentSettings.getNotificationsToken());
    }

    /* Customer Adyen payment information */

    public CustomerAdyenInformation createCustomerAdyenInformation(final Customer customer) {
        return customerAdyenInformationService.getOrCreateCustomerPaymentProviderInformation(customer.getId());
    }

    public CustomerAdyenInformation createCustomerAdyenInformation() {
        return createCustomerAdyenInformation(createCustomer());
    }

    public void assertCustomerAdyenInformation(final CustomerAdyenInformation customerAdyenInformation, final Customer customer) {
        assertNotNull(customerAdyenInformation);
        assertEquals(PaymentProviderType.ADYEN, customerAdyenInformation.getType());
        assertEquals(customer.getEmail(), customerAdyenInformation.getShopperEmail());
        assertEquals(customer.getUuId(), customerAdyenInformation.getShopperReference());
        assertEquals(customer.getId(), customerAdyenInformation.getCustomer().getId());
    }

    /* Order payment */
    public OrderPaymentDto createOrderPaymentDto() {
        final OrderPaymentDto paymentDto = new OrderPaymentDto();
        paymentDto.setAmount(BigDecimal.TEN);
        paymentDto.setPaymentMethodSurcharge(BigDecimal.ONE);
        paymentDto.setClientIpAddress(IP_ADDRESS);
        paymentDto.setCurrency(Currency.EUR);
        paymentDto.setPaymentProviderType(PaymentProviderType.ADYEN);
        return paymentDto;
    }

    public OrderPayment createOrderPayment(final Order order, final CustomerPaymentMethod customerPaymentMethod, final OrderPaymentDto paymentDto) {
        return createOrderPayment(order, paymentDto, createCustomerPaymentMethodProcessingChannelDto(customerPaymentMethod));
    }

    public OrderPayment createOrderPayment(final Order order, final OrderPaymentDto paymentDto, final PaymentProcessingChannelDto<? extends PaymentProcessingChannel> paymentProcessingChannel) {
        return orderPaymentService.createPayment(order.getId(), paymentDto, paymentProcessingChannel);
    }

    public OrderPayment createOrderPayment() {
        final Order order = createOrder();
        final CustomerCardPaymentMethod paymentMethod = createCustomerCardPaymentMethod(order.getCustomer());
        return createOrderPayment(order, paymentMethod, createOrderPaymentDto());
    }

    public OrderPayment createOrderPayment(final OrderPaymentDto orderPaymentDto) {
        final Order order = createOrder();
        final CustomerCardPaymentMethod paymentMethod = createCustomerCardPaymentMethod(order.getCustomer());
        return createOrderPayment(order, paymentMethod, orderPaymentDto);
    }

    public OrderPayment createOrderPayment(final OrderPaymentDto orderPaymentDto, final Customer customer) {
        final Order order = createOrder(customer);
        final CustomerCardPaymentMethod paymentMethod = createCustomerCardPaymentMethod(order.getCustomer());
        return createOrderPayment(order, paymentMethod, orderPaymentDto);
    }

    public OrderPayment createOrderPayment(final OrderPaymentDto orderPaymentDto, PaymentProcessingChannelDto<? extends PaymentProcessingChannel> paymentProcessingChannelDto) {
        final Order order = createOrder();
        return createOrderPayment(order, orderPaymentDto, paymentProcessingChannelDto);
    }

    public OrderPayment createOrderPayment(final Order order) {
        final CustomerCardPaymentMethod paymentMethod = createCustomerCardPaymentMethod(order.getCustomer());
        return createOrderPayment(order, paymentMethod, createOrderPaymentDto());
    }

    public void assertOrderPayment(final OrderPayment orderPayment, final OrderPaymentDto orderPaymentDto) {
        assertPayment(orderPayment, orderPaymentDto);
    }

    public void assertPaymentLastState(final Payment payment, final PaymentState lastState, final PaymentState initialState, int expectedStatesCount) {
        assertEquals(expectedStatesCount, payment.getStateChangeHistoryRecords().size());
        assertEquals(lastState, payment.getLastState());
        // Grab last state history record
        PaymentStateChangeHistoryRecord foundHistoryRecord = null;
        for (final PaymentStateChangeHistoryRecord currentEntry : payment.getStateChangeHistoryRecords()) {
            if (initialState == currentEntry.getInitialState() && lastState == currentEntry.getUpdatedState()) {
                foundHistoryRecord = currentEntry;
                break;
            }
        }
        assertNotNull(foundHistoryRecord);
    }

    public PaymentStateChangeHistoryRecordDto createPaymentStateChangeHistoryRecordDto() {
        final PaymentStateChangeHistoryRecordDto recordDto = new PaymentStateChangeHistoryRecordDto();
        recordDto.setUpdatedState(PaymentState.PAID);
        recordDto.setInformation("Updated payment state during payment processing");
        return recordDto;
    }

    public void assertCreatePaymentStateChangeHistoryRecord(final PaymentStateChangeHistoryRecord historyRecord, PaymentStateChangeHistoryRecordDto historyRecordDto) {
        assertNotNull(historyRecord);
        assertEquals(historyRecordDto.getUpdatedState(), historyRecord.getUpdatedState());
        assertEquals(historyRecordDto.getInformation(), historyRecord.getInformation());
    }


    /* Customer payment method authorization payment */
    public CustomerPaymentMethodAuthorizationPaymentDto createCustomerPaymentMethodAuthorizationPaymentDto() {
        final CustomerPaymentMethodAuthorizationPaymentDto paymentDto = new CustomerPaymentMethodAuthorizationPaymentDto();
        paymentDto.setAmount(BigDecimal.TEN);
        paymentDto.setClientIpAddress(IP_ADDRESS);
        paymentDto.setCurrency(Currency.EUR);
        paymentDto.setPaymentProviderType(PaymentProviderType.ADYEN);
        return paymentDto;
    }

    public CustomerPaymentMethodAuthorizationPayment createCustomerPaymentMethodAuthorizationPayment(final CustomerPaymentMethodAuthorizationRequest authorizationRequest, final CustomerPaymentMethodAuthorizationPaymentDto paymentDto, final PaymentProcessingChannelDto<? extends PaymentProcessingChannel> paymentProcessingChannelDto) {
        return customerPaymentMethodAuthorizationPaymentService.createPayment(authorizationRequest.getId(), paymentDto, paymentProcessingChannelDto);
    }

    public CustomerPaymentMethodAuthorizationPayment createCustomerPaymentMethodAuthorizationPayment() {
        final CustomerEncryptedPaymentMethodAuthorizationRequest authorizationRequest = createCustomerEncryptedPaymentMethodAuthorizationRequest();
        final EncryptedPaymentMethodProcessingChannelDto encryptedPaymentMethodProcessingChannelDto = new EncryptedPaymentMethodProcessingChannelDto(PaymentProviderIntegrationType.API, authorizationRequest.getEncryptedData());
        return createCustomerPaymentMethodAuthorizationPayment(authorizationRequest, createCustomerPaymentMethodAuthorizationPaymentDto(), encryptedPaymentMethodProcessingChannelDto);
    }

    public void assertCustomerPaymentMethodAuthorizationPayment(final CustomerPaymentMethodAuthorizationPayment payment, final CustomerPaymentMethodAuthorizationPaymentDto paymentDto) {
        assertPayment(payment, paymentDto);
    }

    public void assertPayment(final Payment payment, final PaymentDto<? extends Payment> paymentDto) {
        assertNotNull(payment);
        assertNotNull(payment.getUuId());
        assertEquals(paymentDto.getType(), payment.getType());
        assertEquals(paymentDto.getPaymentProviderType(), payment.getPaymentProviderType());
        assertEquals(paymentDto.getClientIpAddress(), payment.getClientIpAddress());
        assertEquals(paymentDto.getCurrency(), payment.getCurrency());
        assertEquals(paymentDto.getAmount().doubleValue(), payment.getAmount().doubleValue(), COMPARISON_DOUBLE_PRECISION);
        assertEquals(paymentDto.isStorePaymentMethod(), payment.isStorePaymentMethod());
        assertEquals(paymentDto.getPaymentMethodSurcharge().doubleValue(), payment.getPaymentMethodSurcharge().doubleValue(), COMPARISON_DOUBLE_PRECISION);
        assertEquals(paymentDto.getPaymentMethodSurcharge().add(paymentDto.getAmount()).compareTo(payment.getPaymentTotalAmount()), 0);
    }

    /* Payment result DTO */
    public AdyenPaymentResultDto createAdyenPaymentResultDto() {
        final AdyenPaymentResultDto resultDto = new AdyenPaymentResultDto();
        resultDto.setStatus(PaymentResultStatus.PAID);
        resultDto.setResultCode("result_code_01234");
        resultDto.setPspReference("psp_refernce_56789");
        resultDto.setAuthCode("auth_code_123456789");
        resultDto.setRefusalReason("reason_just_for_fun");
        return resultDto;
    }

    public AdyenPaymentResult createAdyenPaymentResult(final AdyenPaymentResultDto resultDto) {
        final AdyenPaymentResult paymentResult = new AdyenPaymentResult();
        resultDto.updateDomainEntityProperties(paymentResult);
        return paymentResult;
    }

    public void assertAdyenPaymentResult(final AdyenPaymentResult paymentResult, final AdyenPaymentResultDto paymentResultDto) {
        assertEquals(PaymentProviderType.ADYEN, paymentResult.getType());
        assertEquals(paymentResultDto.getAuthCode(), paymentResult.getAuthCode());
        assertEquals(paymentResultDto.getPspReference(), paymentResult.getPspReference());
        assertEquals(paymentResultDto.getResultCode(), paymentResult.getResultCode());
        assertEquals(paymentResultDto.getStatus(), paymentResult.getStatus());
        assertEquals(paymentResultDto.getRefusalReason(), paymentResult.getRefusalReason());
    }

    /* Adyen integration tests data */
    public Pair<CustomerCardPaymentMethod, Long> createCustomerEncryptedCardPaymentMethodWithPaymentProvider() {
        return createCustomerEncryptedCardPaymentMethodWithPaymentProvider(createCustomer(), createMasterCardEncryptedInformation());
    }

    public Pair<CustomerCardPaymentMethod, Long> createCustomerEncryptedCardPaymentMethodWithPaymentProvider(final Customer customer) {
        return createCustomerEncryptedCardPaymentMethodWithPaymentProvider(customer, createMasterCardEncryptedInformation());
    }

    public Pair<CustomerCardPaymentMethod, Long> createCustomerEncryptedCardPaymentMethodWithPaymentProvider(final Customer customer, final String adyenCardEncryptedData) {
        // Prepare data
        final PaymentMethodGroupType paymentMethodGroupType = PaymentMethodGroupType.CARD;
        final CustomerEncryptedPaymentMethodAuthorizationRequestDto authorizationRequestDto = new CustomerEncryptedPaymentMethodAuthorizationRequestDto(PaymentProviderType.ADYEN, PaymentProviderIntegrationType.API, IP_ADDRESS, paymentMethodGroupType, adyenCardEncryptedData, Currency.EUR);
        final CustomerPaymentMethodAuthorizationRequest authorizationRequest = createCustomerEncryptedPaymentMethodAuthorizationRequest(authorizationRequestDto, customer);
        // Create payment method definition
        final GroupPaymentMethodDefinitionDto paymentMethodDefinitionDto = createGroupPaymentMethodDefinitionDto();
        paymentMethodDefinitionDto.setPaymentMethodGroupType(PaymentMethodGroupType.CARD);
        paymentMethodDefinitionDto.setCurrency(authorizationRequestDto.getCurrency());
        paymentMethodDefinitionDto.setPaymentProviderType(PaymentProviderType.ADYEN);
        if (!groupPaymentMethodDefinitionService.checkIfPaymentMethodDefinitionExistsForLookupParameters(paymentMethodDefinitionDto.getPaymentMethodGroupType(), paymentMethodDefinitionDto.getCurrency(), paymentMethodDefinitionDto.getPaymentProviderType())) {
            final GroupPaymentMethodDefinition paymentMethodDefinition = createGroupPaymentMethodDefinition(paymentMethodDefinitionDto);
            assertNotNull(paymentMethodDefinition);
        }
        flushAndClear();
        // Try to create new payment method
        final PaymentProcessingResultDetailedInformationDto detailedInformationDto = customerPaymentMethodAuthorizationRequestProcessorService.processCustomerPaymentMethodAuthorizationRequest(authorizationRequest.getId());
        assertTrue(detailedInformationDto instanceof CustomerPaymentMethodAuthorizationPaymentProcessingResultDetailedInformationDto);
        final CustomerPaymentMethodAuthorizationPaymentProcessingResultDetailedInformationDto paymentMethodsDetailsInformationDto = (CustomerPaymentMethodAuthorizationPaymentProcessingResultDetailedInformationDto) detailedInformationDto;
        List<Long> customerPaymentMethodIds = paymentMethodsDetailsInformationDto.getCustomerPaymentMethodIds();
        final Long paymentId = paymentMethodsDetailsInformationDto.getPaymentId();
        assertNotNull(paymentId);
        assertNotNull(customerPaymentMethodIds);
        // Try for max 10 seconds since sometimes for Adyen it takes longer to create recurring details reference
        final MutableInt counter = new MutableInt(0);
        while (customerPaymentMethodIds.size() == 0 && counter.intValue() < ADYEN_CARD_CREATION_MAX_ATTEMPTS) {
            wait(ADYEN_CARD_CREATION_WAIT_PERIOD);
            final List<CustomerPaymentMethod> customerPaymentMethods = customerPaymentMethodsSynchronizationService.synchronizeCustomerPaymentMethods(customer.getId(), PaymentProviderType.ADYEN).getCreatedCustomerPaymentMethods();
            customerPaymentMethodIds = customerPaymentMethods.stream().map(customerPaymentMethod -> customerPaymentMethod.getId()).collect(Collectors.toCollection(ArrayList::new));
        }
        assertEquals(1, customerPaymentMethodIds.size());
        final Long customerPaymentMethodId = customerPaymentMethodIds.get(0);
        CustomerPaymentMethod customerPaymentMethod = customerPaymentMethodService.getCustomerPaymentMethodById(customerPaymentMethodId);
        assertNotNull(customerPaymentMethod);
        customerPaymentMethod = persistenceUtilityService.initializeAndUnProxy(customerPaymentMethod);
        assertTrue(customerPaymentMethod instanceof CustomerCardPaymentMethod);
        return new MutablePair<>((CustomerCardPaymentMethod) customerPaymentMethod, paymentId);
    }

    public void assertAuthorizedEncryptedPayment(final Payment payment) {
        assertNotNull(payment);
        assertEquals(PaymentState.PAID, payment.getLastState());
        // Grab order payment result
        PaymentResult paymentResult = payment.getPaymentResults().iterator().next();
        paymentResult = persistenceUtilityService.initializeAndUnProxy(paymentResult);
        assertEquals(PaymentResultStatus.PAID, paymentResult.getStatus());
        assertTrue(paymentResult instanceof AdyenPaymentResult);
        final AdyenPaymentResult adyenPaymentResult = (AdyenPaymentResult) paymentResult;
        assertNotNull(adyenPaymentResult.getPspReference());
        assertNotNull(adyenPaymentResult.getAuthCode());
        assertNotNull(adyenPaymentResult.getResultCode());
        assertEquals(AdyenPaymentStatus.AUTHORISED.getResult(), adyenPaymentResult.getResultCode());
        assertNull(adyenPaymentResult.getRefusalReason());
    }

    /* Encrypted cards datas  */
    public String encryptCardData(final PaymentCardEncryptionInformationDto encryptionInformationDto) {
        return adyenPaymentCardEncryptionService.encryptPaymentCardInformation(encryptionInformationDto);
    }

    public PaymentCardEncryptionInformationDto createMasterCardInformation() {
        return new PaymentCardEncryptionInformationDto(ADYEN_CARD_EXPIRY_MONTH, ADYEN_CARD_EXPIRY_YEAR, ADYEN_CARD_MASTER_NUMBER, ADYEN_CARD_HOLDER_NAME, ADYEN_CARD_CVC);
    }

    public String createMasterCardEncryptedInformation() {
        return encryptCardData(createMasterCardInformation());
    }

    public PaymentCardEncryptionInformationDto createVisaCardInformation() {
        return new PaymentCardEncryptionInformationDto(ADYEN_CARD_EXPIRY_MONTH, ADYEN_CARD_EXPIRY_YEAR, ADYEN_CARD_VISA_NUMBER, ADYEN_CARD_HOLDER_NAME, ADYEN_CARD_CVC);
    }

    public String createVisaCardEncryptedInformation() {
        return encryptCardData(createVisaCardInformation());
    }

    public PaymentCardEncryptionInformationDto createAmexCardInformation() {
        return new PaymentCardEncryptionInformationDto(ADYEN_CARD_EXPIRY_MONTH, ADYEN_CARD_EXPIRY_YEAR, ADYEN_CARD_AMEX_NUMBER, ADYEN_CARD_HOLDER_NAME, ADYEN_CARD_AMEX_CVC);
    }

    public String createAmexCardEncryptedInformation() {
        return encryptCardData(createAmexCardInformation());
    }

    public PaymentCardEncryptionInformationDto createDiscoverCardInformation() {
        return new PaymentCardEncryptionInformationDto(ADYEN_CARD_EXPIRY_MONTH, ADYEN_CARD_EXPIRY_YEAR, ADYEN_CARD_DISCOVER_NUMBER, ADYEN_CARD_HOLDER_NAME, ADYEN_CARD_CVC);
    }

    public String createDiscoverCardEncryptedInformation() {
        return encryptCardData(createDiscoverCardInformation());
    }

    public PaymentCardEncryptionInformationDto createDinersCardInformation() {
        return new PaymentCardEncryptionInformationDto(ADYEN_CARD_EXPIRY_MONTH, ADYEN_CARD_EXPIRY_YEAR, ADYEN_CARD_DINERS_CLUB_NUMBER, ADYEN_CARD_HOLDER_NAME, ADYEN_CARD_CVC);
    }

    public String createDinersCardEncryptedInformation() {
        return encryptCardData(createDinersCardInformation());
    }

    public void assertCustomerCardPaymentMethod(final CustomerCardPaymentMethod customerCardPaymentMethod, final PaymentCardEncryptionInformationDto informationDto, final PaymentMethodType paymentMethodType) {
        assertEquals(paymentMethodType, customerCardPaymentMethod.getPaymentMethodType());
        assertEquals(Integer.valueOf(informationDto.getExpiryMonth()).intValue(), customerCardPaymentMethod.getExpiryMonth());
        assertEquals(Integer.valueOf(informationDto.getExpiryYear()).intValue(), customerCardPaymentMethod.getExpiryYear());
        assertEquals(informationDto.getHolderName(), customerCardPaymentMethod.getHolderName());
        assertTrue(informationDto.getNumber().trim().replaceAll(StringUtils.SPACE, StringUtils.EMPTY).endsWith(customerCardPaymentMethod.getNumberTail()));
    }

    public OrderPayment createOrderPaymentAndPayUsingAdyen() {
        // Prepare data
        final String email = "dummy_" + UUID.randomUUID().toString() + "@dummy.com";
        final Customer customer = createCustomer(email);
        Order order = createOrder(customer);
        flushAndClear();
        // Create customer card payment method
        final Pair<CustomerCardPaymentMethod, Long> paymentMethodCreationResult = createCustomerEncryptedCardPaymentMethodWithPaymentProvider(customer);
        final CustomerCardPaymentMethod customerCardPaymentMethod = paymentMethodCreationResult.getKey();
        // Create individual payment method
        final IndividualPaymentMethodDefinitionDto paymentMethodDefinitionDto = createIndividualPaymentMethodDefinitionDto();
        paymentMethodDefinitionDto.setPaymentMethodType(customerCardPaymentMethod.getPaymentMethodType());
        paymentMethodDefinitionDto.setPaymentProviderType(customerCardPaymentMethod.getProviderInformation().getType());
        paymentMethodDefinitionDto.setCurrency(order.getCurrency());
        final IndividualPaymentMethodDefinition paymentMethodDefinition = createIndividualPaymentMethodDefinition(paymentMethodDefinitionDto);
        assertNotNull(paymentMethodDefinition);
        flushAndClear();
        OrderPaymentRequest orderPaymentRequest = createOrderPaymentRequest(order, customerCardPaymentMethod, createOrderPaymentRequestDto());
        flushAndClear();
        // Check that order is not paid
        assertNotEquals(OrderState.PAID, order.getLastState());
        // Create and process order payment
        final Long orderId = orderPaymentRequestProcessorService.processOrderPaymentRequest(orderPaymentRequest.getId());
        assertNotNull(orderId);
        flushAndClear();
        order = orderService.getOrderById(orderId);
        return order.getPayments().iterator().next();
    }

    /* Order provider payment channel */
    public void assertOrderProviderPaymentChannel(final OrderPaymentChannel orderPaymentChannel, final OrderPayment orderPayment, final Order order) {
        assertNotNull(orderPaymentChannel);
        final OrderPaymentChannel unProxiedOrderPaymentChannel = persistenceUtilityService.initializeAndUnProxy(orderPaymentChannel);
        assertTrue(unProxiedOrderPaymentChannel instanceof OrderProviderPaymentChannel);
        final OrderProviderPaymentChannel orderProviderPaymentChannel = (OrderProviderPaymentChannel) unProxiedOrderPaymentChannel;
        assertEquals(OrderPaymentChannelType.PAYMENT_PROVIDER, orderProviderPaymentChannel.getType());
        assertNotNull(orderProviderPaymentChannel.getOrder());
        assertEquals(order.getId(), orderProviderPaymentChannel.getOrder().getId());
        assertNotNull(orderProviderPaymentChannel.getPayment());
        assertEquals(orderPayment.getId(), orderProviderPaymentChannel.getPayment().getId());
    }

    public OrderPaymentChannel createOrderAndMarkAsPaid(final Order order) {
        // Prepare data
        final OrderPayment orderPayment = createOrderPayment(order);
        final OrderProviderPaymentChannelDto paymentChannelDto = new OrderProviderPaymentChannelDto(orderPayment.getId());
        flushAndClear();
        // Update mark order as paid
        OrderPaymentChannel orderPaymentChannel = orderService.markOrderPaid(order.getId(), paymentChannelDto);
        assertNotNull(orderPaymentChannel);
        return orderPaymentChannel;
    }

    /* Order payment request */
    public OrderPaymentRequestDto createOrderPaymentRequestDto() {
        final OrderPaymentRequestDto orderPaymentRequestDto = new OrderPaymentRequestDto();
        orderPaymentRequestDto.setClientIpAddress(IP_ADDRESS);
        orderPaymentRequestDto.setStorePaymentMethod(true);
        return orderPaymentRequestDto;
    }

    public OrderPaymentRequest createOrderPaymentRequest() {
        final Customer customer = createCustomer();
        final Order order = createOrder(customer);
        final CustomerPaymentMethod customerPaymentMethod = createCustomerCardPaymentMethod(customer);
        return createOrderPaymentRequest(order, customerPaymentMethod, createOrderPaymentRequestDto());
    }

    public OrderPaymentRequest createOrderPaymentRequest(final Order order) {
        final CustomerPaymentMethod customerPaymentMethod = createCustomerCardPaymentMethod(order.getCustomer());
        return createOrderPaymentRequest(order, customerPaymentMethod, createOrderPaymentRequestDto());
    }

    public OrderPaymentRequest createOrderPaymentRequest(final Order order, final CustomerPaymentMethod customerPaymentMethod, final OrderPaymentRequestDto requestDto) {
        return createOrderPaymentRequest(order, requestDto, createOrderRequestCustomerPaymentMethodDto(customerPaymentMethod));
    }

    public OrderPaymentRequest createOrderPaymentRequest(final Order order, final OrderPaymentRequestDto requestDto, final OrderRequestPaymentMethodDto<? extends OrderRequestPaymentMethod> orderRequestPaymentMethodDto) {
        return orderPaymentRequestService.createOrderPaymentRequest(order.getId(), requestDto, orderRequestPaymentMethodDto);
    }

    public void assertOrderPaymentRequest(final OrderPaymentRequest orderPaymentRequest, final OrderPaymentRequestDto requestDto) {
        assertNotNull(orderPaymentRequest);
        assertNotNull(orderPaymentRequest.getId());
        assertEquals(requestDto.getClientIpAddress(), orderPaymentRequest.getClientIpAddress());
        assertEquals(requestDto.isStorePaymentMethod(), orderPaymentRequest.isStorePaymentMethod());
    }

    /* Order payment request payment method */
    public OrderRequestCustomerPaymentMethodDto createOrderRequestCustomerPaymentMethodDto(final CustomerPaymentMethod customerPaymentMethod) {
        final OrderRequestCustomerPaymentMethodDto paymentMethodDto = new OrderRequestCustomerPaymentMethodDto();
        paymentMethodDto.setCustomerPaymentMethodId(customerPaymentMethod.getId());
        return paymentMethodDto;
    }

    public void assertOrderRequestCustomerPaymentMethod(final OrderRequestCustomerPaymentMethod paymentMethod, final OrderRequestCustomerPaymentMethodDto paymentMethodDto) {
        assertNotNull(paymentMethod);
        assertEquals(paymentMethodDto.getType(), paymentMethod.getType());
    }

    public OrderRequestEncryptedPaymentMethodDto createOrderRequestEncryptedPaymentMethodDto() {
        final OrderRequestEncryptedPaymentMethodDto paymentMethodDto = new OrderRequestEncryptedPaymentMethodDto();
        paymentMethodDto.setEncryptedData("BlaBlaBla");
        paymentMethodDto.setPaymentProviderType(PaymentProviderType.ADYEN);
        paymentMethodDto.setPaymentMethodGroupType(PaymentMethodGroupType.CARD);
        return paymentMethodDto;
    }

    public void assertOrderRequestEncryptedPaymentMethod(final OrderRequestEncryptedPaymentMethod paymentMethod, final OrderRequestEncryptedPaymentMethodDto paymentMethodDto) {
        assertNotNull(paymentMethod);
        assertEquals(paymentMethodDto.getType(), paymentMethod.getType());
        assertEquals(paymentMethodDto.getEncryptedData(), paymentMethod.getEncryptedData());
        assertEquals(paymentMethodDto.getPaymentProviderType(), paymentMethod.getPaymentProviderType());
        assertEquals(paymentMethodDto.getPaymentMethodGroupType(), paymentMethod.getPaymentMethodGroupType());
    }

    public OrderRequestRedirectPaymentMethodDto createOrderRequestRedirectPaymentMethodDto() {
        final OrderRequestRedirectPaymentMethodDto paymentMethodDto = new OrderRequestRedirectPaymentMethodDto();
        paymentMethodDto.setPaymentMethodType(PaymentMethodType.IDEAL);
        paymentMethodDto.setPaymentProviderType(PaymentProviderType.ADYEN);
        return paymentMethodDto;
    }

    public void assertOrderRequestRedirectPaymentMethod(final OrderRequestRedirectPaymentMethod paymentMethod, final OrderRequestRedirectPaymentMethodDto paymentMethodDto) {
        assertNotNull(paymentMethod);
        assertEquals(paymentMethodDto.getType(), paymentMethod.getType());
        assertEquals(paymentMethodDto.getPaymentMethodType(), paymentMethod.getPaymentMethodType());
        assertEquals(paymentMethodDto.getPaymentProviderType(), paymentMethod.getPaymentProviderType());
    }

    /* Payment provider notification request  */
    public PaymentProviderNotificationRequestDto createPaymentProviderNotificationRequestDto() {
        final PaymentProviderNotificationRequestDto requestDto = new PaymentProviderNotificationRequestDto();
        requestDto.setRawContent(getAdyenNotificationRawContent("HYFTFIOY-JGUFITYIT-UHIYITT-UHTDRTYXCGHB", "5639", "8654534236436708234234"));
        requestDto.setProviderType(PaymentProviderType.ADYEN);
        requestDto.setClientIpAddress(IP_ADDRESS);
        return requestDto;
    }

    public PaymentProviderNotificationRequest createPaymentProviderNotificationRequest(final PaymentProviderNotificationRequestDto requestDto) {
        return paymentProviderNotificationRequestService.createPaymentProviderNotificationRequest(requestDto);
    }

    public PaymentProviderNotificationRequest createPaymentProviderNotificationRequest() {
        return createPaymentProviderNotificationRequest(createPaymentProviderNotificationRequestDto());
    }

    public void assertPaymentProviderNotificationRequest(final PaymentProviderNotificationRequest request, final PaymentProviderNotificationRequestDto requestDto) {
        assertNotNull(request);
        assertEquals(PaymentProviderNotificationRequestState.CREATED, request.getState());
        assertEquals(requestDto.getProviderType(), request.getProviderType());
        assertEquals(requestDto.getRawContent(), request.getRawContent());
        assertEquals(requestDto.getClientIpAddress(), request.getClientIpAddress());
    }

    /* Adyen payment provider notification request  */
    public AdyenPaymentProviderNotificationDto createAdyenPaymentProviderNotificationDto() {
        final AdyenPaymentProviderNotificationDto requestDto = new AdyenPaymentProviderNotificationDto();
        requestDto.setRawContent("Raw content of payment provider notification");
        requestDto.setClientIpAddress(IP_ADDRESS);
        requestDto.setSuccess(Boolean.TRUE);
        requestDto.setAmount(BigDecimal.TEN);
        requestDto.setAuthCode("457896");
        requestDto.setCurrency(Currency.EUR);
        requestDto.setEventCode("AUTHORIZED");
        requestDto.setMerchantReference("JGYFTIITGUGOYFOYGOYGUPGPOTFDIRSDESETULHJN");
        requestDto.setPaymentMethodType(AdyenPaymentMethodType.AMERICAN_EXPRESS);
        requestDto.setPspReference("87876658457679800901315634567");
        return requestDto;
    }

    public AdyenPaymentProviderNotification createAdyenPaymentProviderNotification(final Long notificationRequestId, final AdyenPaymentProviderNotificationDto notificationDto) {
        return adyenPaymentProviderNotificationService.createPaymentProviderNotification(notificationRequestId, notificationDto);
    }

    public AdyenPaymentProviderNotification createAdyenPaymentProviderNotification() {
        return createAdyenPaymentProviderNotification(createPaymentProviderNotificationRequest().getId(), createAdyenPaymentProviderNotificationDto());
    }

    public void assertAdyenPaymentProviderNotification(final AdyenPaymentProviderNotification notification, final AdyenPaymentProviderNotificationDto notificationDto) {
        assertNotNull(notification);
        assertEquals(PaymentProviderType.ADYEN, notification.getType());
        Assert.assertEquals(PaymentProviderNotificationState.CREATED, notification.getState());
        assertEquals(notificationDto.getRawContent(), notification.getRawContent());
        assertEquals(notificationDto.getClientIpAddress(), notification.getClientIpAddress());
        assertEquals(notificationDto.getSuccess(), notification.getSuccess());
        assertEquals(notificationDto.getAmount().doubleValue(), notification.getAmount().doubleValue(), COMPARISON_DOUBLE_PRECISION);
        assertEquals(notificationDto.getAuthCode(), notification.getAuthCode());
        assertEquals(notificationDto.getCurrency(), notification.getCurrency());
        assertEquals(notificationDto.getEventCode(), notification.getEventCode());
        assertEquals(notificationDto.getMerchantReference(), notification.getMerchantReference());
        Assert.assertEquals(notificationDto.getPaymentMethodType(), notification.getPaymentMethodType());
        assertEquals(notificationDto.getPspReference(), notification.getPspReference());
    }

    public String getAdyenNotificationRawContent(final String paymentUuid, final String authCode, final String pspReference) {
        return ADYEN_NOTIFICATION_RAW_CONTENT.replaceAll(Pattern.quote("{paymentUuid}"), paymentUuid).replaceAll(Pattern.quote("{authCode}"), authCode).replaceAll(Pattern.quote("{pspReference}"), pspReference);
    }


    /* Payment processing channel */
    public ProvidedPaymentMethodProcessingChannelDto createProvidedPaymentMethodProcessingChannelDto() {
        final ProvidedPaymentMethodProcessingChannelDto providedPaymentMethodProcessingChannelDto = new ProvidedPaymentMethodProcessingChannelDto();
        providedPaymentMethodProcessingChannelDto.setPaymentMethodType(PaymentMethodType.MASTER_CARD);
        providedPaymentMethodProcessingChannelDto.setPaymentProviderIntegrationType(PaymentProviderIntegrationType.API);
        return providedPaymentMethodProcessingChannelDto;
    }

    public void assertProvidedPaymentMethodProcessingChannel(final ProvidedPaymentMethodProcessingChannel providedPaymentMethodProcessingChannel, final ProvidedPaymentMethodProcessingChannelDto processingChannelDto) {
        assertNotNull(providedPaymentMethodProcessingChannel);
        assertEquals(processingChannelDto.getPaymentProviderIntegrationType(), providedPaymentMethodProcessingChannel.getPaymentProviderIntegrationType());
        assertEquals(processingChannelDto.getPaymentMethodType(), providedPaymentMethodProcessingChannel.getPaymentMethodType());
    }

    public EncryptedPaymentMethodProcessingChannelDto createEncryptedPaymentMethodProcessingChannelDto() {
        final EncryptedPaymentMethodProcessingChannelDto providedPaymentMethodProcessingChannelDto = new EncryptedPaymentMethodProcessingChannelDto();
        providedPaymentMethodProcessingChannelDto.setEncryptedPaymentMethodInformation("GFCHVJH&^%REDCKGVKGCLFD&(*RDUFCVP(*FCJHCDXGLI");
        providedPaymentMethodProcessingChannelDto.setPaymentProviderIntegrationType(PaymentProviderIntegrationType.API);
        return providedPaymentMethodProcessingChannelDto;
    }

    public void assertEncryptedPaymentMethodProcessingChannel(final EncryptedPaymentMethodProcessingChannel processingChannel, final EncryptedPaymentMethodProcessingChannelDto processingChannelDto) {
        assertNotNull(processingChannel);
        assertEquals(processingChannelDto.getPaymentProviderIntegrationType(), processingChannel.getPaymentProviderIntegrationType());
        assertEquals(processingChannelDto.getEncryptedPaymentMethodInformation(), processingChannel.getEncryptedData());
    }

    public CustomerPaymentMethodProcessingChannelDto createCustomerPaymentMethodProcessingChannelDto(final CustomerPaymentMethod customerPaymentMethod) {
        final CustomerPaymentMethodProcessingChannelDto providedPaymentMethodProcessingChannelDto = new CustomerPaymentMethodProcessingChannelDto();
        providedPaymentMethodProcessingChannelDto.setCustomerPaymentMethodId(customerPaymentMethod.getId());
        providedPaymentMethodProcessingChannelDto.setPaymentProviderIntegrationType(PaymentProviderIntegrationType.API);
        return providedPaymentMethodProcessingChannelDto;
    }

    public void assertCustomerPaymentMethodProcessingChannel(final CustomerPaymentMethodProcessingChannel processingChannel, final CustomerPaymentMethodProcessingChannelDto processingChannelDto) {
        assertNotNull(processingChannel);
        assertEquals(processingChannelDto.getPaymentProviderIntegrationType(), processingChannel.getPaymentProviderIntegrationType());
    }

    public DeferredPaymentMethodProcessingChannelDto createDeferredPaymentMethodProcessingChannelDto() {
        return new DeferredPaymentMethodProcessingChannelDto(PaymentProviderIntegrationType.API);
    }

    public void assertDeferredPaymentMethodProcessingChannel(final DeferredPaymentMethodProcessingChannel processingChannel, final DeferredPaymentMethodProcessingChannelDto processingChannelDto) {
        assertNotNull(processingChannel);
        assertEquals(processingChannelDto.getPaymentProviderIntegrationType(), processingChannel.getPaymentProviderIntegrationType());
    }

    /* Adyen integration service */
    public void assertAdyenIdealRedirectUrlIsCorrect(final String url) {
        // Download content
        try {
            final byte[] binaryData = IOUtils.toByteArray(new URI(url));
            final String stringData = new String(binaryData, "utf8");
            assertNotNull(stringData);
            // Assert that page contains in Banks selection
            assertTrue(stringData.contains("/hpp/img/pm/TESTBANK1.png"));
        } catch (final Exception ex) {
            throw new ServicesRuntimeException(ex);
        }
    }

    /* Payment provider redirect result */
    public AdyenRedirectResultDto createAdyenRedirectResultDto() {
        final AdyenRedirectResultDto adyenRedirectResultDto = new AdyenRedirectResultDto();
        adyenRedirectResultDto.setAuthResult("AUTHORISED");
        adyenRedirectResultDto.setMerchantReference("JHHFGGVUHFTCFGLIUGKVGL");
        adyenRedirectResultDto.setMerchantReturnData("HG%*R%&$%UTFRUCUTRRCFXDHFCCTRFCG");
        adyenRedirectResultDto.setMerchantSig("OIUYJHTRD^&Y*OGR&EXDFGUJFGH86tcgvihl");
        adyenRedirectResultDto.setPaymentMethod("mc");
        adyenRedirectResultDto.setPspReference("HVFXCRCTUTTDRGOGITFUYFTGTFURER");
        adyenRedirectResultDto.setShopperLocale("nl-NL");
        adyenRedirectResultDto.setSkinCode("76%TF7u");
        return adyenRedirectResultDto;
    }

    public AdyenRedirectResult createAdyenRedirectResult() {
        return createAdyenRedirectResult(createAdyenRedirectResultDto());
    }

    public AdyenRedirectResult createAdyenRedirectResult(final AdyenRedirectResultDto resultDto) {
        return adyenRedirectResultService.createPaymentProviderRedirectResult(resultDto);
    }

    public void assertAdyenRedirectResult(final AdyenRedirectResult adyenRedirectResult, final AdyenRedirectResultDto resultDto) {
        assertNotNull(adyenRedirectResult);
        assertEquals(resultDto.getType(), adyenRedirectResult.getType());
        assertEquals(PaymentProviderRedirectResultState.CREATED, adyenRedirectResult.getState());
        assertEquals(resultDto.getAuthResult(), adyenRedirectResult.getAuthResult());
        assertEquals(resultDto.getMerchantReference(), adyenRedirectResult.getMerchantReference());
        assertEquals(resultDto.getMerchantReturnData(), adyenRedirectResult.getMerchantReturnData());
        assertEquals(resultDto.getMerchantSig(), adyenRedirectResult.getMerchantSig());
        assertEquals(resultDto.getPaymentMethod(), adyenRedirectResult.getPaymentMethod());
        assertEquals(resultDto.getPspReference(), adyenRedirectResult.getPspReference());
        assertEquals(resultDto.getShopperLocale(), adyenRedirectResult.getShopperLocale());
        assertEquals(resultDto.getSkinCode(), adyenRedirectResult.getSkinCode());
    }

    public AdyenRedirectResultDto createAdyenRedirectResultForRealPaymentDto(final Payment payment) {
        final AdyenPaymentSettings adyenPaymentSettings = createAdyenPaymentSettings();
        // Create DTO
        final AdyenRedirectResultDto adyenRedirectResultDto = new AdyenRedirectResultDto();
        adyenRedirectResultDto.setAuthResult(AdyenPaymentStatus.AUTHORISED.getResult().toUpperCase());
        adyenRedirectResultDto.setSkinCode(adyenPaymentSettings.getSkinCode());
        adyenRedirectResultDto.setMerchantReference(payment.getUuId());
        adyenRedirectResultDto.setMerchantReturnData(payment.getUuId());
        adyenRedirectResultDto.setPaymentMethod(AdyenPaymentMethodType.IDEAL.getCodes().iterator().next());
        // Calculate signature
        final String signature = adyenPaymentProviderIntegrationService.calculateSignatureForAdyenRedirectResult(adyenRedirectResultDto);
        adyenRedirectResultDto.setMerchantSig(signature);
        // Create redirect result
        return adyenRedirectResultDto;
    }

    /* Payment method definition */
    public IndividualPaymentMethodDefinitionDto createIndividualPaymentMethodDefinitionDto() {
        final IndividualPaymentMethodDefinitionDto paymentMethodDefinitionDto = new IndividualPaymentMethodDefinitionDto();
        paymentMethodDefinitionDto.setPaymentMethodType(PaymentMethodType.IDEAL);
        setPaymentMethodDefinitionDtoProperties(paymentMethodDefinitionDto);
        return paymentMethodDefinitionDto;
    }

    public IndividualPaymentMethodDefinition createIndividualPaymentMethodDefinition() {
        return createIndividualPaymentMethodDefinition(createIndividualPaymentMethodDefinitionDto());
    }

    public IndividualPaymentMethodDefinition createIndividualPaymentMethodDefinition(final IndividualPaymentMethodDefinitionDto paymentMethodDefinitionDto) {
        return individualPaymentMethodDefinitionService.createPaymentMethodDefinition(paymentMethodDefinitionDto);
    }

    public void assertIndividualPaymentMethodDefinition(final IndividualPaymentMethodDefinition paymentMethodDefinition, final IndividualPaymentMethodDefinitionDto paymentMethodDefinitionDto) {
        assertNotNull(paymentMethodDefinition);
        Assert.assertEquals(paymentMethodDefinitionDto.getPaymentMethodType(), paymentMethodDefinition.getPaymentMethodType());
        assertPaymentMethodDefinition(paymentMethodDefinition, paymentMethodDefinitionDto);
    }


    public GroupPaymentMethodDefinitionDto createGroupPaymentMethodDefinitionDto() {
        final GroupPaymentMethodDefinitionDto paymentMethodDefinitionDto = new GroupPaymentMethodDefinitionDto();
        paymentMethodDefinitionDto.setPaymentMethodGroupType(PaymentMethodGroupType.CARD);
        setPaymentMethodDefinitionDtoProperties(paymentMethodDefinitionDto);
        return paymentMethodDefinitionDto;
    }

    public GroupPaymentMethodDefinition createGroupPaymentMethodDefinition() {
        return createGroupPaymentMethodDefinition(createGroupPaymentMethodDefinitionDto());
    }

    public GroupPaymentMethodDefinition createGroupPaymentMethodDefinition(final GroupPaymentMethodDefinitionDto paymentMethodDefinitionDto) {
        return groupPaymentMethodDefinitionService.createPaymentMethodDefinition(paymentMethodDefinitionDto);
    }

    public void assertGroupPaymentMethodDefinition(final GroupPaymentMethodDefinition paymentMethodDefinition, final GroupPaymentMethodDefinitionDto paymentMethodDefinitionDto) {
        assertNotNull(paymentMethodDefinition);
        assertEquals(paymentMethodDefinitionDto.getPaymentMethodGroupType(), paymentMethodDefinition.getPaymentMethodGroupType());
        assertPaymentMethodDefinition(paymentMethodDefinition, paymentMethodDefinitionDto);
    }

    private void assertPaymentMethodDefinition(final PaymentMethodDefinition paymentMethodDefinition, final PaymentMethodDefinitionDto<? extends PaymentMethodDefinition> paymentMethodDefinitionDto) {
        assertEquals(paymentMethodDefinitionDto.getType(), paymentMethodDefinition.getType());
        assertEquals(paymentMethodDefinitionDto.getAuthorizationSurcharge().compareTo(paymentMethodDefinition.getAuthorizationSurcharge()), 0);
        assertEquals(paymentMethodDefinitionDto.getPaymentSurcharge().compareTo(paymentMethodDefinition.getPaymentSurcharge()), 0);
        assertEquals(paymentMethodDefinitionDto.getCurrency(), paymentMethodDefinition.getCurrency());
        assertEquals(paymentMethodDefinitionDto.getPaymentProviderType(), paymentMethodDefinition.getPaymentProviderType());
        assertEquals(paymentMethodDefinitionDto.isRecurringPaymentEnabled(), paymentMethodDefinition.isRecurringPaymentEnabled());
    }

    private void setPaymentMethodDefinitionDtoProperties(final PaymentMethodDefinitionDto<? extends PaymentMethodDefinition> paymentMethodDefinitionDto) {
        paymentMethodDefinitionDto.setPaymentProviderType(PaymentProviderType.ADYEN);
        paymentMethodDefinitionDto.setCurrency(Currency.EUR);
        paymentMethodDefinitionDto.setAuthorizationSurcharge(BigDecimal.ONE);
        paymentMethodDefinitionDto.setPaymentSurcharge(BigDecimal.TEN);
        paymentMethodDefinitionDto.setRecurringPaymentEnabled(true);
    }

    /* Acapture payment settings */
    public AcapturePaymentSettings createAcapturePaymentSettings() {
        return createAcapturePaymentSettings(EnvironmentType.TEST);
    }

    public AcapturePaymentSettings createAcapturePaymentSettings(final EnvironmentType environmentType) {
        final AcapturePaymentSettings paymentSettings = new AcapturePaymentSettings();
        paymentSettings.setNotificationsToken(UUID.randomUUID().toString());
        paymentSettings.setPassword(UUID.randomUUID().toString());
        paymentSettings.setUserName(UUID.randomUUID().toString());
        paymentSettings.setEnvironmentType(environmentType);
        paymentSettings.setHostPageUrl("http://mysite.com/payment/");
        return acapturePaymentSettingsRepository.save(paymentSettings);
    }

    public PaymentMethodSettings createPaymentMethodSettings() {
        return createAcapturePaymentMethodSettings();
    }

    public AcapturePaymentMethodSettings createAcapturePaymentMethodSettings() {
        final AcapturePaymentMethodSettings paymentMethodSettings = new AcapturePaymentMethodSettings();
        paymentMethodSettings.setProviderType(PaymentProviderType.ACAPTURE);
        paymentMethodSettings.setPaymentMethodType(PaymentMethodType.VISA);
        paymentMethodSettings.setPaymentSettings(createAcapturePaymentSettings());
        paymentMethodSettings.setAuthorizationId(UUID.randomUUID().toString());
        return acapturePaymentMethodSettingsRepository.save(paymentMethodSettings);
    }

    /* Utility methods */
    public void wait(final int millis) {
        try {
            Thread.currentThread().sleep(millis);
        } catch (final InterruptedException ex) {
            // Swallow
        }
    }

    private void flush() {
        entityManager.flush();
    }

    private void clear() {
        entityManager.clear();
    }

    private void flushAndClear() {
        flush();
        clear();
    }
}
