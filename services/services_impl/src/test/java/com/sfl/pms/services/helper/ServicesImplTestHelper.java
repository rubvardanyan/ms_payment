package com.sfl.pms.services.helper;

import com.sfl.pms.externalclients.payment.adyen.model.datatypes.AdyenPaymentNotificationEventType;
import com.sfl.pms.services.country.model.CountryCode;
import com.sfl.pms.services.currency.model.Currency;
import com.sfl.pms.services.customer.dto.CustomerDto;
import com.sfl.pms.services.customer.model.Customer;
import com.sfl.pms.services.order.dto.OrderDto;
import com.sfl.pms.services.order.dto.OrderStateChangeHistoryRecordDto;
import com.sfl.pms.services.order.dto.payment.provider.OrderProviderPaymentChannelDto;
import com.sfl.pms.services.order.model.Order;
import com.sfl.pms.services.order.model.OrderState;
import com.sfl.pms.services.order.model.OrderStateChangeHistoryRecord;
import com.sfl.pms.services.order.model.payment.provider.OrderProviderPaymentChannel;
import com.sfl.pms.services.payment.common.dto.PaymentDto;
import com.sfl.pms.services.payment.common.dto.PaymentStateChangeHistoryRecordDto;
import com.sfl.pms.services.payment.common.dto.acapture.AcapturePaymentProviderMetadataDto;
import com.sfl.pms.services.payment.common.dto.adyen.AdyenPaymentResultDto;
import com.sfl.pms.services.payment.common.dto.auth.CustomerPaymentMethodAuthorizationPaymentDto;
import com.sfl.pms.services.payment.common.dto.channel.CustomerPaymentMethodProcessingChannelDto;
import com.sfl.pms.services.payment.common.dto.channel.DeferredPaymentMethodProcessingChannelDto;
import com.sfl.pms.services.payment.common.dto.channel.EncryptedPaymentMethodProcessingChannelDto;
import com.sfl.pms.services.payment.common.dto.channel.ProvidedPaymentMethodProcessingChannelDto;
import com.sfl.pms.services.payment.common.dto.order.OrderPaymentDto;
import com.sfl.pms.services.payment.common.dto.order.request.OrderPaymentRequestDto;
import com.sfl.pms.services.payment.common.dto.order.request.OrderRequestCustomerPaymentMethodDto;
import com.sfl.pms.services.payment.common.dto.order.request.OrderRequestEncryptedPaymentMethodDto;
import com.sfl.pms.services.payment.common.dto.order.request.OrderRequestRedirectPaymentMethodDto;
import com.sfl.pms.services.payment.common.model.Payment;
import com.sfl.pms.services.payment.common.model.PaymentResultStatus;
import com.sfl.pms.services.payment.common.model.PaymentState;
import com.sfl.pms.services.payment.common.model.PaymentStateChangeHistoryRecord;
import com.sfl.pms.services.payment.common.model.acapture.AcapturePaymentProviderMetadata;
import com.sfl.pms.services.payment.common.model.adyen.AdyenPaymentResult;
import com.sfl.pms.services.payment.common.model.auth.CustomerPaymentMethodAuthorizationPayment;
import com.sfl.pms.services.payment.common.model.channel.CustomerPaymentMethodProcessingChannel;
import com.sfl.pms.services.payment.common.model.channel.DeferredPaymentMethodProcessingChannel;
import com.sfl.pms.services.payment.common.model.channel.EncryptedPaymentMethodProcessingChannel;
import com.sfl.pms.services.payment.common.model.channel.ProvidedPaymentMethodProcessingChannel;
import com.sfl.pms.services.payment.common.model.order.OrderPayment;
import com.sfl.pms.services.payment.common.model.order.request.OrderPaymentRequest;
import com.sfl.pms.services.payment.common.model.order.request.OrderRequestCustomerPaymentMethod;
import com.sfl.pms.services.payment.common.model.order.request.OrderRequestEncryptedPaymentMethod;
import com.sfl.pms.services.payment.common.model.order.request.OrderRequestRedirectPaymentMethod;
import com.sfl.pms.services.payment.customer.information.dto.adyen.CustomerAdyenInformationDto;
import com.sfl.pms.services.payment.customer.information.model.adyen.CustomerAdyenInformation;
import com.sfl.pms.services.payment.customer.method.dto.CustomerBankPaymentMethodDto;
import com.sfl.pms.services.payment.customer.method.dto.CustomerCardPaymentMethodDto;
import com.sfl.pms.services.payment.customer.method.dto.adyen.CustomerPaymentMethodAdyenInformationDto;
import com.sfl.pms.services.payment.customer.method.dto.authorization.CustomerEncryptedPaymentMethodAuthorizationRequestDto;
import com.sfl.pms.services.payment.customer.method.dto.authorization.CustomerPaymentMethodAuthorizationRequestDto;
import com.sfl.pms.services.payment.customer.method.dto.authorization.CustomerProvidedPaymentMethodAuthorizationRequestDto;
import com.sfl.pms.services.payment.customer.method.model.CustomerBankPaymentMethod;
import com.sfl.pms.services.payment.customer.method.model.CustomerCardPaymentMethod;
import com.sfl.pms.services.payment.customer.method.model.CustomerPaymentMethodType;
import com.sfl.pms.services.payment.customer.method.model.adyen.CustomerPaymentMethodAdyenInformation;
import com.sfl.pms.services.payment.customer.method.model.authorization.CustomerEncryptedPaymentMethodAuthorizationRequest;
import com.sfl.pms.services.payment.customer.method.model.authorization.CustomerPaymentMethodAuthorizationRequest;
import com.sfl.pms.services.payment.customer.method.model.authorization.CustomerPaymentMethodAuthorizationRequestState;
import com.sfl.pms.services.payment.customer.method.model.authorization.CustomerProvidedPaymentMethodAuthorizationRequest;
import com.sfl.pms.services.payment.method.dto.PaymentMethodDefinitionDto;
import com.sfl.pms.services.payment.method.dto.group.GroupPaymentMethodDefinitionDto;
import com.sfl.pms.services.payment.method.dto.individual.IndividualPaymentMethodDefinitionDto;
import com.sfl.pms.services.payment.method.model.PaymentMethodDefinition;
import com.sfl.pms.services.payment.method.model.PaymentMethodGroupType;
import com.sfl.pms.services.payment.method.model.PaymentMethodSettings;
import com.sfl.pms.services.payment.method.model.PaymentMethodType;
import com.sfl.pms.services.payment.method.model.acapture.AcapturePaymentMethodSettings;
import com.sfl.pms.services.payment.method.model.adyen.AdyenPaymentMethodType;
import com.sfl.pms.services.payment.method.model.group.GroupPaymentMethodDefinition;
import com.sfl.pms.services.payment.method.model.individual.IndividualPaymentMethodDefinition;
import com.sfl.pms.services.payment.notification.dto.PaymentProviderNotificationRequestDto;
import com.sfl.pms.services.payment.notification.dto.adyen.AdyenPaymentProviderNotificationDto;
import com.sfl.pms.services.payment.notification.impl.processors.adyen.json.model.*;
import com.sfl.pms.services.payment.notification.model.PaymentProviderNotificationRequest;
import com.sfl.pms.services.payment.notification.model.PaymentProviderNotificationRequestState;
import com.sfl.pms.services.payment.notification.model.PaymentProviderNotificationState;
import com.sfl.pms.services.payment.notification.model.adyen.AdyenPaymentProviderNotification;
import com.sfl.pms.services.payment.provider.dto.AdyenRedirectUrlGenerationDto;
import com.sfl.pms.services.payment.provider.model.PaymentProviderIntegrationType;
import com.sfl.pms.services.payment.provider.model.PaymentProviderType;
import com.sfl.pms.services.payment.redirect.dto.redirect.AdyenRedirectResultDto;
import com.sfl.pms.services.payment.redirect.model.PaymentProviderRedirectResultState;
import com.sfl.pms.services.payment.redirect.model.adyen.AdyenRedirectResult;
import com.sfl.pms.services.payment.settings.dto.adyen.AdyenPaymentSettingsDto;
import com.sfl.pms.services.payment.settings.model.acapture.AcapturePaymentSettings;
import com.sfl.pms.services.payment.settings.model.adyen.AdyenPaymentSettings;
import com.sfl.pms.services.system.environment.model.EnvironmentType;
import com.sfl.pms.services.util.mutable.MutableHolder;
import org.junit.Assert;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.*;

/**
 * User: Ruben Dilanyan
 * Company: SFL LLC
 * Date: 10/01/14
 * Time: 10:18 PM
 */
public class ServicesImplTestHelper {

    /* Constants */
    private static final String ADYEN_NOTIFICATION_RAW_CONTENT = "{\"live\":\"false\",\"notificationItems\":[{\"NotificationRequestItem\":{\"additionalData\":{\"expiryDate\":\"8\\/2018\",\"issuerCountry\":\"US\",\"authCode\":\"88531\",\"cardSummary\":\"0008\",\"cardHolderName\":\"Adyen Credit Card Holder\",\"shopperInteraction\":\"ContAuth\",\"shopperEmail\":\"dummy@dummy.com\",\"hmacSignature\":\"v6st1580kZtvNqsDNYUBifxdJVWIS6YG7Tw9TRF4gAs=\",\"shopperReference\":\"1f14a2e5-f51c-481d-a782-0841728af3c9\"},\"amount\":{\"currency\":\"EUR\",\"value\":1300},\"eventCode\":\"AUTHORISATION\",\"eventDate\":\"2015-04-28T10:23:13+02:00\",\"merchantAccountCode\":\"SFLPRO\",\"merchantReference\":\"8ce9243f-68cc-47c6-9ab2-7469d8dfc9d2\",\"operations\":[\"CANCEL\",\"CAPTURE\",\"REFUND\"],\"paymentMethod\":\"visa\",\"pspReference\":\"8814302093932614\",\"reason\":\"88531:0008:8\\/2018\",\"success\":\"true\"}}]}";

    public ServicesImplTestHelper() {
    }


    /* Customer */
    public CustomerDto createCustomerDto() {
        final CustomerDto userDto = new CustomerDto();
        userDto.setUuId(UUID.randomUUID().toString());
        userDto.setEmail("dummy@dummy.com");
        return userDto;
    }

    public Customer createCustomer(final CustomerDto userDto) {
        final Customer user = new Customer();
        userDto.updateDomainEntityProperties(user);
        return user;
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
        // Create order
        final OrderDto orderDto = new OrderDto();
        orderDto.setUuId(UUID.randomUUID().toString());
        orderDto.setPaymentTotalWithVat(BigDecimal.TEN);
        orderDto.setCurrency(Currency.EUR);
        return orderDto;
    }

    public Order createOrder() {
        // Create order
        return createOrder(createOrderDto());
    }

    public Order createOrder(final OrderDto orderDto) {
        // Create order
        final Order order = new Order();
        orderDto.updateDomainEntityProperties(order);
        return order;
    }

    public void assertOrder(final Order order, final OrderDto orderDto) {
        assertNotNull(order);
        assertEquals(orderDto.getCurrency(), order.getCurrency());
        assertEquals(orderDto.getPaymentTotalWithVat(), order.getPaymentTotalWithVat());
        assertEquals(orderDto.getUuId(), order.getUuId());
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
        paymentMethodAuthorizationRequestDto.setClientIpAddress("127.0.0.1");
        paymentMethodAuthorizationRequestDto.setCurrency(Currency.EUR);
        return paymentMethodAuthorizationRequestDto;
    }

    public CustomerEncryptedPaymentMethodAuthorizationRequest createCustomerEncryptedPaymentMethodAuthorizationRequest() {
        return createCustomerEncryptedPaymentMethodAuthorizationRequest(createCustomerEncryptedPaymentMethodAuthorizationRequestDto());
    }

    public CustomerEncryptedPaymentMethodAuthorizationRequest createCustomerEncryptedPaymentMethodAuthorizationRequest(final CustomerEncryptedPaymentMethodAuthorizationRequestDto requestDto) {
        final CustomerEncryptedPaymentMethodAuthorizationRequest paymentMethodAuthorizationRequest = new CustomerEncryptedPaymentMethodAuthorizationRequest(true);
        requestDto.updateDomainEntityProperties(paymentMethodAuthorizationRequest);
        return paymentMethodAuthorizationRequest;
    }

    public void assertCustomerEncryptedPaymentMethodAuthorizationRequest(final CustomerEncryptedPaymentMethodAuthorizationRequest authorizationRequest, final CustomerEncryptedPaymentMethodAuthorizationRequestDto authorizationRequestDto) {
        assertCustomerPaymentMethodAuthorizationRequest(authorizationRequest, authorizationRequestDto);
        assertEquals(authorizationRequestDto.getEncryptedData(), authorizationRequest.getEncryptedData());
        assertEquals(authorizationRequestDto.getPaymentMethodGroup(), authorizationRequest.getPaymentMethodGroup());
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
        return createCustomerProvidedPaymentMethodAuthorizationRequest(createCustomerProvidedPaymentMethodAuthorizationRequestDto());
    }

    public CustomerProvidedPaymentMethodAuthorizationRequest createCustomerProvidedPaymentMethodAuthorizationRequest(final CustomerProvidedPaymentMethodAuthorizationRequestDto requestDto) {
        final CustomerProvidedPaymentMethodAuthorizationRequest paymentMethodAuthorizationRequest = new CustomerProvidedPaymentMethodAuthorizationRequest(true);
        requestDto.updateDomainEntityProperties(paymentMethodAuthorizationRequest);
        return paymentMethodAuthorizationRequest;
    }

    public void assertCustomerProvidedPaymentMethodAuthorizationRequest(final CustomerProvidedPaymentMethodAuthorizationRequest authorizationRequest, final CustomerProvidedPaymentMethodAuthorizationRequestDto authorizationRequestDto) {
        assertCustomerPaymentMethodAuthorizationRequest(authorizationRequest, authorizationRequestDto);
        assertEquals(authorizationRequestDto.getPaymentMethodType(), authorizationRequest.getPaymentMethodType());
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

    /* Customer card payment method */
    public CustomerCardPaymentMethodDto createCustomerCardPaymentMethodDto() {
        final CustomerCardPaymentMethodDto paymentMethodDto = new CustomerCardPaymentMethodDto();
        paymentMethodDto.setPaymentMethodType(PaymentMethodType.MASTER_CARD);
        paymentMethodDto.setExpiryYear(2017);
        paymentMethodDto.setExpiryMonth(11);
        paymentMethodDto.setHolderName("Mr John Appleased");
        paymentMethodDto.setNumberTail("6972");
        return paymentMethodDto;
    }

    public CustomerCardPaymentMethod createCustomerCardPaymentMethod(final CustomerCardPaymentMethodDto paymentMethodDto) {
        final CustomerCardPaymentMethod paymentMethod = new CustomerCardPaymentMethod(true);
        paymentMethodDto.updateDomainEntityProperties(paymentMethod);
        return paymentMethod;
    }

    public CustomerCardPaymentMethod createCustomerCardPaymentMethod() {
        return createCustomerCardPaymentMethod(createCustomerCardPaymentMethodDto());
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
        paymentMethodDto.setOwnerName("John Appleased");
        paymentMethodDto.setCountryCode(CountryCode.AM);
        paymentMethodDto.setIban("987654321");
        paymentMethodDto.setBic("TESTNL01");
        return paymentMethodDto;
    }

    public CustomerBankPaymentMethod createCustomerBankPaymentMethod(final CustomerBankPaymentMethodDto paymentMethodDto) {
        final CustomerBankPaymentMethod paymentMethod = new CustomerBankPaymentMethod(true);
        paymentMethodDto.updateDomainEntityProperties(paymentMethod);
        return paymentMethod;
    }

    public CustomerBankPaymentMethod createCustomerBankPaymentMethod() {
        return createCustomerBankPaymentMethod(createCustomerBankPaymentMethodDto());
    }

    public void assertCustomerBankPaymentMethod(final CustomerBankPaymentMethod paymentMethod, final CustomerBankPaymentMethodDto paymentMethodDto) {
        assertEquals(CustomerPaymentMethodType.BANK, paymentMethod.getType());
        assertEquals(paymentMethodDto.getBankAccountNumber(), paymentMethod.getBankAccountNumber());
        assertEquals(paymentMethodDto.getBankName(), paymentMethod.getBankName());
        assertEquals(paymentMethodDto.getOwnerName(), paymentMethod.getOwnerName());
        Assert.assertEquals(paymentMethodDto.getCountryCode(), paymentMethod.getCountryCode());
        assertEquals(paymentMethodDto.getIban(), paymentMethod.getIban());
        assertEquals(paymentMethodDto.getBic(), paymentMethod.getBic());
        assertEquals(paymentMethodDto.getPaymentMethodType(), paymentMethod.getPaymentMethodType());
    }

    public CustomerPaymentMethodAdyenInformationDto createCustomerPaymentMethodAdyenInformationDto() {
        final CustomerPaymentMethodAdyenInformationDto informationDto = new CustomerPaymentMethodAdyenInformationDto();
        informationDto.setRecurringDetailReference("KHJG-YYTR-UTWX-NBGT");
        return informationDto;
    }

    public void assertCustomerPaymentMethodAdyenInformation(final CustomerPaymentMethodAdyenInformation information, final CustomerPaymentMethodAdyenInformationDto informationDto) {
        assertEquals(informationDto.getRecurringDetailReference(), information.getRecurringDetailReference());
    }

    public CustomerPaymentMethodAdyenInformation createCustomerPaymentMethodAdyenInformation() {
        return createCustomerPaymentMethodAdyenInformation(createCustomerPaymentMethodAdyenInformationDto());
    }

    public CustomerPaymentMethodAdyenInformation createCustomerPaymentMethodAdyenInformation(final CustomerPaymentMethodAdyenInformationDto informationDto) {
        final CustomerPaymentMethodAdyenInformation paymentMethodAdyenInformation = new CustomerPaymentMethodAdyenInformation();
        informationDto.updateDomainEntityProperties(paymentMethodAdyenInformation);
        return paymentMethodAdyenInformation;
    }

    /* Adyen payment settings */
    public AdyenPaymentSettingsDto createAdyenPaymentSettingsDto() {
        final AdyenPaymentSettingsDto paymentSettingsDto = new AdyenPaymentSettingsDto();
        paymentSettingsDto.setEnvironmentType(EnvironmentType.PRODUCTION);
        paymentSettingsDto.setUserName("sfl");
        paymentSettingsDto.setPassword("sfl123456");
        paymentSettingsDto.setSkinCode("TF76GFF");
        paymentSettingsDto.setSkinHmacKey("HHLVGCGFCF767VGV&^^(6r45GCJ$$^)");
        paymentSettingsDto.setHppPaymentUrl("http://test.adyen.com/details.html");
        paymentSettingsDto.setHppPaymentMethodSelectionUrl("http://test.adyen.com/select.html");
        paymentSettingsDto.setDefaultCountry(CountryCode.NL);
        paymentSettingsDto.setMerchantAccount("Test merchant account");
        paymentSettingsDto.setDefaultLocale("nl-NL");
        paymentSettingsDto.setNotificationsToken("HVFJFJFDRJRTYRETDTIF^FJXDXKTY&TOYGYTFTYDRDTYRRDTYTYLUYGYUFITYFOYFITD^%DTCRXURDDTRXTKYFOTFLYFTDTICTRJ");
        return paymentSettingsDto;
    }

    public AdyenPaymentSettings createAdyenPaymentSettings(final AdyenPaymentSettingsDto paymentSettingsDto) {
        final AdyenPaymentSettings paymentMethod = new AdyenPaymentSettings();
        paymentSettingsDto.updateDomainEntityProperties(paymentMethod);
        return paymentMethod;
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
    public CustomerAdyenInformationDto createCustomerAdyenInformationDto() {
        final CustomerAdyenInformationDto informationDto = new CustomerAdyenInformationDto();
        informationDto.setShopperEmail("dummy@dummy.com");
        informationDto.setShopperReference("YTRTY-IUYTR-POIUN-NBVCF");
        return informationDto;
    }

    public CustomerAdyenInformation createCustomerAdyenInformation(final CustomerAdyenInformationDto informationDto) {
        final CustomerAdyenInformation information = new CustomerAdyenInformation();
        informationDto.updateDomainEntityProperties(information);
        return information;
    }

    public CustomerAdyenInformation createCustomerAdyenInformation() {
        return createCustomerAdyenInformation(createCustomerAdyenInformationDto());
    }

    public void assertCustomerAdyenInformation(final CustomerAdyenInformation customerAdyenInformation, final CustomerAdyenInformationDto customerAdyenInformationDto) {
        assertNotNull(customerAdyenInformation);
        assertEquals(customerAdyenInformationDto.getType(), customerAdyenInformation.getType());
        assertEquals(customerAdyenInformationDto.getShopperEmail(), customerAdyenInformation.getShopperEmail());
        assertEquals(customerAdyenInformationDto.getShopperReference(), customerAdyenInformation.getShopperReference());
    }

    /* Order payment */
    public OrderPaymentDto createOrderPaymentDto() {
        final OrderPaymentDto paymentDto = new OrderPaymentDto();
        paymentDto.setAmount(BigDecimal.TEN);
        paymentDto.setClientIpAddress("127.0.0.1");
        paymentDto.setCurrency(Currency.EUR);
        paymentDto.setPaymentProviderType(PaymentProviderType.ADYEN);
        paymentDto.setStorePaymentMethod(false);
        paymentDto.setPaymentMethodSurcharge(BigDecimal.ONE);
        return paymentDto;
    }

    public OrderPayment createOrderPayment(final OrderPaymentDto paymentDto) {
        final OrderPayment payment = new OrderPayment(true);
        paymentDto.updateDomainEntityProperties(payment);
        return payment;
    }

    public OrderPayment createOrderPayment() {
        return createOrderPayment(createOrderPaymentDto());
    }

    public void assertOrderPayment(final OrderPayment orderPayment, final OrderPaymentDto orderPaymentDto) {
        assertPayment(orderPayment, orderPaymentDto);
    }

    public void assertPaymentLastState(final Payment payment, final PaymentState lastState, final PaymentState initialState, int expectedStatesCount) {
        assertEquals(expectedStatesCount, payment.getStateChangeHistoryRecords().size());
        // Grab last state history record
        final MutableHolder<PaymentStateChangeHistoryRecord> mutableHolder = new MutableHolder<>(null);
        payment.getStateChangeHistoryRecords().stream().forEach(historyRecord -> {
            mutableHolder.setValue(historyRecord);
        });
        final PaymentStateChangeHistoryRecord historyRecord = mutableHolder.getValue();
        assertEquals(lastState, payment.getLastState());
        assertEquals(initialState, historyRecord.getInitialState());
        assertEquals(lastState, historyRecord.getUpdatedState());
    }

    public PaymentStateChangeHistoryRecordDto createPaymentStateChangeHistoryRecordDto() {
        final PaymentStateChangeHistoryRecordDto recordDto = new PaymentStateChangeHistoryRecordDto();
        recordDto.setUpdatedState(PaymentState.PAID);
        recordDto.setInformation("Updated payment state during payment processing");
        return recordDto;
    }

    public void assertCreatePaymentStateChangeHistoryRecord(final PaymentStateChangeHistoryRecord historyRecord, PaymentStateChangeHistoryRecordDto historyRecordDto) {
        assertNotNull(historyRecord);
        Assert.assertEquals(historyRecordDto.getUpdatedState(), historyRecord.getUpdatedState());
        assertEquals(historyRecordDto.getInformation(), historyRecord.getInformation());
    }

    /* Customer payment method authorization payment */
    public CustomerPaymentMethodAuthorizationPaymentDto createCustomerPaymentMethodAuthorizationPaymentDto() {
        final CustomerPaymentMethodAuthorizationPaymentDto paymentDto = new CustomerPaymentMethodAuthorizationPaymentDto();
        paymentDto.setAmount(BigDecimal.TEN);
        paymentDto.setClientIpAddress("127.0.0.1");
        paymentDto.setCurrency(Currency.EUR);
        paymentDto.setPaymentProviderType(PaymentProviderType.ADYEN);
        return paymentDto;
    }

    public CustomerPaymentMethodAuthorizationPayment createCustomerPaymentMethodAuthorizationPayment(final CustomerPaymentMethodAuthorizationPaymentDto paymentDto) {
        final CustomerPaymentMethodAuthorizationPayment payment = new CustomerPaymentMethodAuthorizationPayment(true);
        paymentDto.updateDomainEntityProperties(payment);
        return payment;
    }

    public CustomerPaymentMethodAuthorizationPayment createCustomerPaymentMethodAuthorizationPayment() {
        return createCustomerPaymentMethodAuthorizationPayment(createCustomerPaymentMethodAuthorizationPaymentDto());
    }

    public void assertCustomerPaymentMethodAuthorizationPayment(final CustomerPaymentMethodAuthorizationPayment payment, final CustomerPaymentMethodAuthorizationPaymentDto paymentDto) {
        assertPayment(payment, paymentDto);
    }

    public void assertPayment(final Payment payment, final PaymentDto<? extends Payment> paymentDto) {
        assertNotNull(payment);
        assertNotNull(payment.getUuId());
        Assert.assertEquals(paymentDto.getType(), payment.getType());
        assertEquals(paymentDto.getPaymentProviderType(), payment.getPaymentProviderType());
        assertEquals(paymentDto.getClientIpAddress(), payment.getClientIpAddress());
        assertEquals(paymentDto.isStorePaymentMethod(), payment.isStorePaymentMethod());
        assertEquals(paymentDto.getCurrency(), payment.getCurrency());
        assertEquals(paymentDto.getAmount().doubleValue(), payment.getAmount().doubleValue(), 0.001);
        assertEquals(paymentDto.getPaymentMethodSurcharge().doubleValue(), payment.getPaymentMethodSurcharge().doubleValue(), 0.001);
        assertEquals(paymentDto.getPaymentMethodSurcharge().add(paymentDto.getAmount()).compareTo(payment.getPaymentTotalAmount()), 0);
    }

    /* Payment result DTO */
    public AdyenPaymentResultDto createAdyenPaymentResultDto() {
        final AdyenPaymentResultDto resultDto = new AdyenPaymentResultDto();
        resultDto.setStatus(PaymentResultStatus.PAID);
        resultDto.setResultCode("result_code_01234");
        resultDto.setPspReference("psp_refernce_56789");
        resultDto.setAuthCode("auth_code_123456789");
        resultDto.setRefusalReason("reason_for_fun");
        return resultDto;
    }

    public AdyenPaymentResult createAdyenPaymentResult(final AdyenPaymentResultDto resultDto) {
        final AdyenPaymentResult paymentResult = new AdyenPaymentResult();
        resultDto.updateDomainEntityProperties(paymentResult);
        return paymentResult;
    }

    public AdyenPaymentResult createAdyenPaymentResult() {
        final AdyenPaymentResultDto paymentResultDto = createAdyenPaymentResultDto();
        return createAdyenPaymentResult(paymentResultDto);
    }

    public void assertAdyenPaymentResult(final AdyenPaymentResult paymentResult, final AdyenPaymentResultDto paymentResultDto) {
        assertEquals(PaymentProviderType.ADYEN, paymentResult.getType());
        assertEquals(paymentResultDto.getAuthCode(), paymentResult.getAuthCode());
        assertEquals(paymentResultDto.getPspReference(), paymentResult.getPspReference());
        assertEquals(paymentResultDto.getResultCode(), paymentResult.getResultCode());
        assertEquals(paymentResultDto.getStatus(), paymentResult.getStatus());
        assertEquals(paymentResultDto.getRefusalReason(), paymentResult.getRefusalReason());
    }


    /* Payment provider notification request  */
    public PaymentProviderNotificationRequestDto createPaymentProviderNotificationRequestDto() {
        final PaymentProviderNotificationRequestDto requestDto = new PaymentProviderNotificationRequestDto();
        requestDto.setRawContent(ADYEN_NOTIFICATION_RAW_CONTENT);
        requestDto.setProviderType(PaymentProviderType.ADYEN);
        requestDto.setClientIpAddress("127.0.0.1");
        return requestDto;
    }

    public PaymentProviderNotificationRequest createPaymentProviderNotificationRequest(final PaymentProviderNotificationRequestDto requestDto) {
        final PaymentProviderNotificationRequest request = new PaymentProviderNotificationRequest(true);
        requestDto.updateDomainEntityProperties(request);
        return request;
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

    public String getAdyenNotificationRawContent() {
        return ADYEN_NOTIFICATION_RAW_CONTENT;
    }

    public AdyenNotificationJsonModel createAdyenNotificationJsonModel() {
        // Create notification JSON model
        final AdyenNotificationJsonModel notificationJsonModel = new AdyenNotificationJsonModel();
        notificationJsonModel.setLive(Boolean.FALSE);
        // Build request item
        final AdyenNotificationRequestItemJsonModel itemJsonModel = new AdyenNotificationRequestItemJsonModel();
        notificationJsonModel.getNotificationItems().add(new AdyenNotificationArrayItemJsonModel(itemJsonModel));
        // Set properties
        itemJsonModel.setEventCode("AUTHORISATION");
        itemJsonModel.setEventDate("2015-04-28T10:23:13+02:00");
        itemJsonModel.setMerchantAccountCode("SFLPRO");
        itemJsonModel.setMerchantReference("8ce9243f-68cc-47c6-9ab2-7469d8dfc9d2");
        itemJsonModel.getOperations().addAll(Arrays.asList("CANCEL", "CAPTURE", "REFUND"));
        itemJsonModel.setPaymentMethod("visa");
        itemJsonModel.setPspReference("8814302093932614");
        itemJsonModel.setReason("88531:0008:8/2018");
        itemJsonModel.setSuccess(true);
        // Create amount model
        final AdyenNotificationAmountJsonModel amountJsonModel = new AdyenNotificationAmountJsonModel();
        itemJsonModel.setAmount(amountJsonModel);
        amountJsonModel.setCurrency("EUR");
        amountJsonModel.setValue(1300);
        // Create additional data model
        final AdyenNotificationAdditionalDataJsonModel additionalDataJsonModel = new AdyenNotificationAdditionalDataJsonModel();
        itemJsonModel.setAdditionalData(additionalDataJsonModel);
        additionalDataJsonModel.setExpiryDate("8/2018");
        additionalDataJsonModel.setIssuerCountry("US");
        additionalDataJsonModel.setAuthCode("88531");
        additionalDataJsonModel.setCardSummary("0008");
        additionalDataJsonModel.setCardHolderName("Adyen Credit Card Holder");
        additionalDataJsonModel.setShopperInteraction("ContAuth");
        additionalDataJsonModel.setShopperEmail("dummy@dummy.com");
        additionalDataJsonModel.setHmacSignature("v6st1580kZtvNqsDNYUBifxdJVWIS6YG7Tw9TRF4gAs=");
        additionalDataJsonModel.setShopperReference("1f14a2e5-f51c-481d-a782-0841728af3c9");
        return notificationJsonModel;
    }

    /* Adyen payment provider notification request  */
    public AdyenPaymentProviderNotificationDto createAdyenPaymentProviderNotificationDto() {
        final AdyenPaymentProviderNotificationDto requestDto = new AdyenPaymentProviderNotificationDto();
        requestDto.setRawContent("Raw content of payment provider notification");
        requestDto.setClientIpAddress("127.0.0.1");
        requestDto.setSuccess(Boolean.TRUE);
        requestDto.setAmount(BigDecimal.TEN);
        requestDto.setAuthCode("457896");
        requestDto.setCurrency(Currency.EUR);
        requestDto.setEventCode(AdyenPaymentNotificationEventType.AUTHORIZATION.getEventCode());
        requestDto.setMerchantReference("JGYFTIITGUGOYFOYGOYGUPGPOTFDIRSDESETULHJN");
        requestDto.setPaymentMethodType(AdyenPaymentMethodType.AMERICAN_EXPRESS);
        requestDto.setPspReference("87876658457679800901315634567");
        return requestDto;
    }

    public AdyenPaymentProviderNotification createAdyenPaymentProviderNotification(final AdyenPaymentProviderNotificationDto notificationDto) {
        final AdyenPaymentProviderNotification notification = new AdyenPaymentProviderNotification(true);
        notificationDto.updateDomainEntityProperties(notification);
        return notification;
    }

    public AdyenPaymentProviderNotification createAdyenPaymentProviderNotification() {
        return createAdyenPaymentProviderNotification(createAdyenPaymentProviderNotificationDto());
    }

    public void assertAdyenPaymentProviderNotification(final AdyenPaymentProviderNotification notification, final AdyenPaymentProviderNotificationDto notificationDto) {
        assertNotNull(notification);
        assertEquals(PaymentProviderType.ADYEN, notification.getType());
        Assert.assertEquals(PaymentProviderNotificationState.CREATED, notification.getState());
        assertEquals(notificationDto.getRawContent(), notification.getRawContent());
        assertEquals(notificationDto.getClientIpAddress(), notification.getClientIpAddress());
        assertEquals(notificationDto.getSuccess(), notification.getSuccess());
        assertEquals(notificationDto.getAmount(), notification.getAmount());
        assertEquals(notificationDto.getAuthCode(), notification.getAuthCode());
        assertEquals(notificationDto.getCurrency(), notification.getCurrency());
        assertEquals(notificationDto.getEventCode(), notification.getEventCode());
        assertEquals(notificationDto.getMerchantReference(), notification.getMerchantReference());
        Assert.assertEquals(notificationDto.getPaymentMethodType(), notification.getPaymentMethodType());
        assertEquals(notificationDto.getPspReference(), notification.getPspReference());
    }


    /* Adyen redirect URL generation DTO */
    public AdyenRedirectUrlGenerationDto createAdyenRedirectUrlGenerationDto() {
        final AdyenRedirectUrlGenerationDto urlGenerationDto = new AdyenRedirectUrlGenerationDto();
        urlGenerationDto.setPaymentAmount(10000L);
        urlGenerationDto.setCurrencyCode("GBP");
        urlGenerationDto.setShipBeforeDate("2016-10-20");
        urlGenerationDto.setMerchantReference("InternetOrder");
        urlGenerationDto.setSkinCode("123454aD37dJA");
        urlGenerationDto.setMerchantAccount("TestMerchant");
        urlGenerationDto.setShopperEmail("dummy@dummy.com");
        urlGenerationDto.setShopperReference("dummy");
        urlGenerationDto.setRecurringContract("ContAuth");
        urlGenerationDto.setCountryCode("NL");
        urlGenerationDto.setShopperLocale("nl-NL");
        urlGenerationDto.setSignatureHmacKey("testSignatureKey1kjbvhlsvn");
        urlGenerationDto.setAdyenRedirectBaseUrl("https://adyen.com/details.html");
        urlGenerationDto.setSessionValidity("2007-10-11T11:00:00Z");
        urlGenerationDto.setMerchantReturnData("MerchantReturnData");
        urlGenerationDto.setBrandCode("ideal");
        return urlGenerationDto;
    }

    /* Payment processing channel */
    public ProvidedPaymentMethodProcessingChannelDto createProvidedPaymentMethodProcessingChannelDto() {
        final ProvidedPaymentMethodProcessingChannelDto providedPaymentMethodProcessingChannelDto = new ProvidedPaymentMethodProcessingChannelDto();
        providedPaymentMethodProcessingChannelDto.setPaymentMethodType(PaymentMethodType.MASTER_CARD);
        providedPaymentMethodProcessingChannelDto.setPaymentProviderIntegrationType(PaymentProviderIntegrationType.API);
        return providedPaymentMethodProcessingChannelDto;
    }

    public ProvidedPaymentMethodProcessingChannel createProvidedPaymentMethodProcessingChannel() {
        return createProvidedPaymentMethodProcessingChannel(createProvidedPaymentMethodProcessingChannelDto());
    }

    public ProvidedPaymentMethodProcessingChannel createProvidedPaymentMethodProcessingChannel(final ProvidedPaymentMethodProcessingChannelDto processingChannelDto) {
        final ProvidedPaymentMethodProcessingChannel paymentMethodProcessingChannel = new ProvidedPaymentMethodProcessingChannel();
        processingChannelDto.updateDomainEntityProperties(paymentMethodProcessingChannel);
        return paymentMethodProcessingChannel;
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

    public EncryptedPaymentMethodProcessingChannel createEncryptedPaymentMethodProcessingChannel() {
        return createEncryptedPaymentMethodProcessingChannel(createEncryptedPaymentMethodProcessingChannelDto());
    }

    public EncryptedPaymentMethodProcessingChannel createEncryptedPaymentMethodProcessingChannel(final EncryptedPaymentMethodProcessingChannelDto processingChannelDto) {
        final EncryptedPaymentMethodProcessingChannel encryptedPaymentMethodProcessingChannel = new EncryptedPaymentMethodProcessingChannel();
        processingChannelDto.updateDomainEntityProperties(encryptedPaymentMethodProcessingChannel);
        return encryptedPaymentMethodProcessingChannel;
    }

    public void assertEncryptedPaymentMethodProcessingChannel(final EncryptedPaymentMethodProcessingChannel processingChannel, final EncryptedPaymentMethodProcessingChannelDto processingChannelDto) {
        assertNotNull(processingChannel);
        assertEquals(processingChannelDto.getPaymentProviderIntegrationType(), processingChannel.getPaymentProviderIntegrationType());
        assertEquals(processingChannelDto.getEncryptedPaymentMethodInformation(), processingChannel.getEncryptedData());
    }

    public CustomerPaymentMethodProcessingChannelDto createCustomerPaymentMethodProcessingChannelDto() {
        final CustomerPaymentMethodProcessingChannelDto providedPaymentMethodProcessingChannelDto = new CustomerPaymentMethodProcessingChannelDto();
        providedPaymentMethodProcessingChannelDto.setCustomerPaymentMethodId(1L);
        providedPaymentMethodProcessingChannelDto.setPaymentProviderIntegrationType(PaymentProviderIntegrationType.API);
        return providedPaymentMethodProcessingChannelDto;
    }

    public CustomerPaymentMethodProcessingChannel createCustomerPaymentMethodProcessingChannel() {
        return createCustomerPaymentMethodProcessingChannel(createCustomerPaymentMethodProcessingChannelDto());
    }

    public CustomerPaymentMethodProcessingChannel createCustomerPaymentMethodProcessingChannel(final CustomerPaymentMethodProcessingChannelDto methodProcessingChannelDto) {
        final CustomerPaymentMethodProcessingChannel methodProcessingChannel = new CustomerPaymentMethodProcessingChannel();
        methodProcessingChannelDto.updateDomainEntityProperties(methodProcessingChannel);
        return methodProcessingChannel;
    }

    public void assertCustomerPaymentMethodProcessingChannel(final CustomerPaymentMethodProcessingChannel processingChannel, final CustomerPaymentMethodProcessingChannelDto processingChannelDto) {
        assertNotNull(processingChannel);
        assertEquals(processingChannelDto.getPaymentProviderIntegrationType(), processingChannel.getPaymentProviderIntegrationType());
    }

    public DeferredPaymentMethodProcessingChannelDto createDeferredPaymentMethodProcessingChannelDto() {
        return new DeferredPaymentMethodProcessingChannelDto(PaymentProviderIntegrationType.API);
    }

    public DeferredPaymentMethodProcessingChannel createDeferredPaymentMethodProcessingChannel(final DeferredPaymentMethodProcessingChannelDto processingChannelDto) {
        final DeferredPaymentMethodProcessingChannel methodProcessingChannel = new DeferredPaymentMethodProcessingChannel();
        processingChannelDto.updateDomainEntityProperties(methodProcessingChannel);
        return methodProcessingChannel;
    }

    public DeferredPaymentMethodProcessingChannel createDeferredPaymentMethodProcessingChannel() {
        return createDeferredPaymentMethodProcessingChannel(createDeferredPaymentMethodProcessingChannelDto());
    }

    public void assertDeferredPaymentMethodProcessingChannel(final DeferredPaymentMethodProcessingChannel processingChannel, final DeferredPaymentMethodProcessingChannelDto processingChannelDto) {
        assertNotNull(processingChannel);
        assertEquals(processingChannelDto.getPaymentProviderIntegrationType(), processingChannel.getPaymentProviderIntegrationType());
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
        final AdyenRedirectResult adyenRedirectResult = new AdyenRedirectResult(true);
        resultDto.updateDomainEntityProperties(adyenRedirectResult);
        return adyenRedirectResult;
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
        final IndividualPaymentMethodDefinition paymentMethodDefinition = new IndividualPaymentMethodDefinition();
        paymentMethodDefinitionDto.updateDomainEntityProperties(paymentMethodDefinition);
        return paymentMethodDefinition;
    }

    public void assertIndividualPaymentMethodDefinition(final IndividualPaymentMethodDefinition paymentMethodDefinition, final IndividualPaymentMethodDefinitionDto paymentMethodDefinitionDto) {
        assertNotNull(paymentMethodDefinition);
        assertEquals(paymentMethodDefinitionDto.getPaymentMethodType(), paymentMethodDefinition.getPaymentMethodType());
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
        final GroupPaymentMethodDefinition paymentMethodDefinition = new GroupPaymentMethodDefinition();
        paymentMethodDefinitionDto.updateDomainEntityProperties(paymentMethodDefinition);
        return paymentMethodDefinition;
    }

    public void assertGroupPaymentMethodDefinition(final GroupPaymentMethodDefinition paymentMethodDefinition, final GroupPaymentMethodDefinitionDto paymentMethodDefinitionDto) {
        assertNotNull(paymentMethodDefinition);
        assertEquals(paymentMethodDefinitionDto.getPaymentMethodGroupType(), paymentMethodDefinition.getPaymentMethodGroupType());
        assertPaymentMethodDefinition(paymentMethodDefinition, paymentMethodDefinitionDto);
    }

    private void assertPaymentMethodDefinition(final PaymentMethodDefinition paymentMethodDefinition, final PaymentMethodDefinitionDto<? extends PaymentMethodDefinition> paymentMethodDefinitionDto) {
        assertEquals(paymentMethodDefinitionDto.getType(), paymentMethodDefinition.getType());
        assertEquals(paymentMethodDefinitionDto.getAuthorizationSurcharge(), paymentMethodDefinition.getAuthorizationSurcharge());
        assertEquals(paymentMethodDefinitionDto.getPaymentSurcharge(), paymentMethodDefinition.getPaymentSurcharge());
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

    /* Order payment request payment method */
    public OrderRequestCustomerPaymentMethodDto createOrderRequestCustomerPaymentMethodDto() {
        final OrderRequestCustomerPaymentMethodDto paymentMethodDto = new OrderRequestCustomerPaymentMethodDto();
        paymentMethodDto.setCustomerPaymentMethodId(1L);
        return paymentMethodDto;
    }

    public OrderRequestCustomerPaymentMethod createOrderRequestCustomerPaymentMethod(final OrderRequestCustomerPaymentMethodDto paymentMethodDto) {
        final OrderRequestCustomerPaymentMethod paymentMethod = new OrderRequestCustomerPaymentMethod();
        paymentMethodDto.updateDomainEntityProperties(paymentMethod);
        return paymentMethod;
    }

    public OrderRequestCustomerPaymentMethod createOrderRequestCustomerPaymentMethod() {
        return createOrderRequestCustomerPaymentMethod(createOrderRequestCustomerPaymentMethodDto());
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

    public OrderRequestEncryptedPaymentMethod createOrderRequestEncryptedPaymentMethod(final OrderRequestEncryptedPaymentMethodDto paymentMethodDto) {
        final OrderRequestEncryptedPaymentMethod paymentMethod = new OrderRequestEncryptedPaymentMethod();
        paymentMethodDto.updateDomainEntityProperties(paymentMethod);
        return paymentMethod;
    }

    public OrderRequestEncryptedPaymentMethod createOrderRequestEncryptedPaymentMethod() {
        return createOrderRequestEncryptedPaymentMethod(createOrderRequestEncryptedPaymentMethodDto());
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

    public OrderRequestRedirectPaymentMethod createOrderRequestRedirectPaymentMethod(final OrderRequestRedirectPaymentMethodDto paymentMethodDto) {
        final OrderRequestRedirectPaymentMethod paymentMethod = new OrderRequestRedirectPaymentMethod();
        paymentMethodDto.updateDomainEntityProperties(paymentMethod);
        return paymentMethod;
    }

    public OrderRequestRedirectPaymentMethod createOrderRequestRedirectPaymentMethod() {
        return createOrderRequestRedirectPaymentMethod(createOrderRequestRedirectPaymentMethodDto());
    }

    public void assertOrderRequestRedirectPaymentMethod(final OrderRequestRedirectPaymentMethod paymentMethod, final OrderRequestRedirectPaymentMethodDto paymentMethodDto) {
        assertNotNull(paymentMethod);
        assertEquals(paymentMethodDto.getType(), paymentMethod.getType());
        assertEquals(paymentMethodDto.getPaymentMethodType(), paymentMethod.getPaymentMethodType());
        assertEquals(paymentMethodDto.getPaymentProviderType(), paymentMethod.getPaymentProviderType());
    }

    /* Order payment request */
    public OrderPaymentRequestDto createOrderPaymentRequestDto() {
        final OrderPaymentRequestDto orderPaymentRequestDto = new OrderPaymentRequestDto();
        orderPaymentRequestDto.setClientIpAddress("127.0.0.1");
        orderPaymentRequestDto.setStorePaymentMethod(true);
        return orderPaymentRequestDto;
    }

    public OrderPaymentRequest createOrderPaymentRequest() {
        return createOrderPaymentRequest(createOrderPaymentRequestDto());
    }

    public OrderPaymentRequest createOrderPaymentRequest(final OrderPaymentRequestDto requestDto) {
        final OrderPaymentRequest orderPaymentRequest = new OrderPaymentRequest(true);
        requestDto.updateDomainEntityProperties(orderPaymentRequest);
        return orderPaymentRequest;
    }

    public void assertOrderPaymentRequest(final OrderPaymentRequest orderPaymentRequest, final OrderPaymentRequestDto requestDto) {
        assertNotNull(orderPaymentRequest);
        assertEquals(requestDto.getClientIpAddress(), orderPaymentRequest.getClientIpAddress());
        assertEquals(requestDto.isStorePaymentMethod(), orderPaymentRequest.isStorePaymentMethod());
    }

    /* Order payment channel */
    public OrderProviderPaymentChannelDto createOrderProviderPaymentChannelDto() {
        final OrderProviderPaymentChannelDto orderProviderPaymentChannelDto = new OrderProviderPaymentChannelDto();
        orderProviderPaymentChannelDto.setPaymentId(1L);
        return orderProviderPaymentChannelDto;
    }

    public OrderProviderPaymentChannel createOrderProviderPaymentChannel(final OrderProviderPaymentChannelDto orderProviderPaymentChannelDto) {
        final OrderProviderPaymentChannel orderProviderPaymentChannel = new OrderProviderPaymentChannel();
        orderProviderPaymentChannelDto.updateDomainEntityProperties(orderProviderPaymentChannel);
        return orderProviderPaymentChannel;
    }

    public OrderProviderPaymentChannel createOrderProviderPaymentChannel() {
        return createOrderProviderPaymentChannel(createOrderProviderPaymentChannelDto());
    }

    /* Acapture payment settings */
    public AcapturePaymentSettings createAcapturePaymentSettings() {
        final AcapturePaymentSettings paymentSettings = new AcapturePaymentSettings();
        paymentSettings.setNotificationsToken(UUID.randomUUID().toString());
        paymentSettings.setPassword(UUID.randomUUID().toString());
        paymentSettings.setUserName(UUID.randomUUID().toString());
        paymentSettings.setEnvironmentType(EnvironmentType.STAGING);
        paymentSettings.setHostPageUrl("http://mysite.com/payment/");
        return paymentSettings;
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
        return paymentMethodSettings;
    }

    public AcapturePaymentProviderMetadataDto createAcapturePaymentProviderMetadataDto() {
        final AcapturePaymentProviderMetadataDto dto = new AcapturePaymentProviderMetadataDto();
        dto.setCheckoutId(UUID.randomUUID().toString());
        return dto;
    }

    public void assertAcapturePaymentProviderMetadataDto(final AcapturePaymentProviderMetadata metadata, final AcapturePaymentProviderMetadataDto dto) {
        assertNotNull(metadata);
        assertEquals(PaymentProviderType.ACAPTURE, metadata.getProviderType());
        assertEquals(metadata.getCheckoutId(), dto.getCheckoutId());
    }


    /* Future */
    public Future createFuture() {
        return new Future<Object>() {
            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                return false;
            }

            @Override
            public boolean isCancelled() {
                return false;
            }

            @Override
            public boolean isDone() {
                return true;
            }

            @Override
            public Object get() throws InterruptedException, ExecutionException {
                return null;
            }

            @Override
            public Object get(final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                return this;
            }
        };
    }
}
