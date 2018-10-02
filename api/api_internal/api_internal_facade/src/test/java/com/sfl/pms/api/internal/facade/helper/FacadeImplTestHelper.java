package com.sfl.pms.api.internal.facade.helper;

import com.sfl.pms.core.api.internal.model.currency.CurrencyClientType;
import com.sfl.pms.core.api.internal.model.customer.CustomerModel;
import com.sfl.pms.core.api.internal.model.method.PaymentMethodClientType;
import com.sfl.pms.core.api.internal.model.notification.request.CreatePaymentProviderNotificationRequest;
import com.sfl.pms.core.api.internal.model.order.OrderModel;
import com.sfl.pms.core.api.internal.model.order.OrderRequestRedirectPaymentMethodModel;
import com.sfl.pms.core.api.internal.model.order.request.CreateOrderRequest;
import com.sfl.pms.core.api.internal.model.order.request.GetOrderPaymentRequestStatusRequest;
import com.sfl.pms.core.api.internal.model.order.request.RePayOrderRequest;
import com.sfl.pms.core.api.internal.model.provider.PaymentProviderClientType;
import com.sfl.pms.core.api.internal.model.redirect.request.CreateAdyenRedirectResultRequest;
import com.sfl.pms.core.api.internal.model.redirect.request.GetPaymentProviderRedirectResultStatusRequest;
import com.sfl.pms.services.country.model.CountryCode;
import com.sfl.pms.services.currency.model.Currency;
import com.sfl.pms.services.customer.dto.CustomerDto;
import com.sfl.pms.services.customer.model.Customer;
import com.sfl.pms.services.order.dto.OrderDto;
import com.sfl.pms.services.order.model.Order;
import com.sfl.pms.services.payment.common.dto.order.OrderPaymentDto;
import com.sfl.pms.services.payment.common.dto.order.request.OrderPaymentRequestDto;
import com.sfl.pms.services.payment.common.model.order.OrderPayment;
import com.sfl.pms.services.payment.common.model.order.request.OrderPaymentRequest;
import com.sfl.pms.services.payment.notification.dto.PaymentProviderNotificationRequestDto;
import com.sfl.pms.services.payment.notification.model.PaymentProviderNotificationRequest;
import com.sfl.pms.services.payment.notification.model.PaymentProviderNotificationRequestState;
import com.sfl.pms.services.payment.provider.model.PaymentProviderType;
import com.sfl.pms.services.payment.redirect.dto.redirect.adyen.AdyenRedirectResultDto;
import com.sfl.pms.services.payment.redirect.model.adyen.AdyenRedirectResult;
import com.sfl.pms.services.payment.settings.dto.adyen.AdyenPaymentSettingsDto;
import com.sfl.pms.services.payment.settings.model.adyen.AdyenPaymentSettings;
import com.sfl.pms.services.system.environment.model.EnvironmentType;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class FacadeImplTestHelper {

    /* Constructors */
    public FacadeImplTestHelper() {
    }

    /* Redirect result status */
    public GetPaymentProviderRedirectResultStatusRequest createGetRedirectResultStatusRequest() {
        final GetPaymentProviderRedirectResultStatusRequest request = new GetPaymentProviderRedirectResultStatusRequest();
        request.setRedirectResultUuId(UUID.randomUUID().toString());
        return request;
    }

    /* Get order payment request status */
    public GetOrderPaymentRequestStatusRequest createGetOrderPaymentRequestStatusRequest() {
        final GetOrderPaymentRequestStatusRequest request = new GetOrderPaymentRequestStatusRequest();
        request.setOrderPaymentRequestUuid(UUID.randomUUID().toString());
        return request;
    }

    /* Create order payment request */
    public CreateOrderRequest createCreateOrderPaymentRequest() {
        final CreateOrderRequest request = new CreateOrderRequest();
        request.setStorePaymentMethod(Boolean.FALSE);
        request.setClientIpAddress("127.0.0.1");
        request.setCustomer(createCustomerModel());
        request.setOrder(createOrderModel());
        return request;
    }

    /* RePay order request */
    public RePayOrderRequest createRePayPaymentRequest() {
        final RePayOrderRequest request = new RePayOrderRequest();
        request.setStorePaymentMethod(Boolean.FALSE);
        request.setClientIpAddress("127.0.0.1");
        request.setOrderUuId(UUID.randomUUID().toString());
        return request;
    }

    /* Order request payment method */
    public OrderRequestRedirectPaymentMethodModel createRequestRedirectPaymentMethodModel() {
        final OrderRequestRedirectPaymentMethodModel orderRequestRedirectPaymentMethodModel = new OrderRequestRedirectPaymentMethodModel();
        orderRequestRedirectPaymentMethodModel.setPaymentMethodType(PaymentMethodClientType.IDEAL);
        orderRequestRedirectPaymentMethodModel.setPaymentProviderType(PaymentProviderClientType.ADYEN);
        return orderRequestRedirectPaymentMethodModel;
    }

    /* Order model */
    public OrderModel createOrderModel() {
        final OrderModel orderModel = new OrderModel();
        orderModel.setUuId(UUID.randomUUID().toString());
        orderModel.setCurrency(CurrencyClientType.EUR);
        orderModel.setPaymentTotalWithVat(BigDecimal.TEN);
        return orderModel;
    }

    /* Customer model */
    public CustomerModel createCustomerModel() {
        final CustomerModel customerModel = new CustomerModel();
        customerModel.setEmail("dummy@dummy.com");
        customerModel.setUuId(UUID.randomUUID().toString());
        return customerModel;
    }

    /* Create payment provider notification request */
    public CreatePaymentProviderNotificationRequest createCreatePaymentProviderNotificationRequest() {
        final CreatePaymentProviderNotificationRequest request = new CreatePaymentProviderNotificationRequest();
        request.setClientIpAddress("127.0.0.1");
        request.setNotificationsToken("GTFYTFTYTUYFTYFUYTFTTUITUFITFITFOFYGO");
        request.setPaymentProviderType(PaymentProviderClientType.ADYEN);
        request.setRawContent("BUUYFITYELFUYGEOUYFGOYGEYOGYWGLIYGLIWGV:IUWGVIGIUD:UV:");
        return request;
    }

    /* Create payment provider redirect result request */
    public CreateAdyenRedirectResultRequest createCreateAdyenRedirectResultRequest() {
        final CreateAdyenRedirectResultRequest request = new CreateAdyenRedirectResultRequest();
        request.setAuthResult("AUTHRESULT");
        request.setMerchantReference("JKHGTKTKTIYYVLY");
        request.setMerchantReturnData("JGH:G:UG:UG:FOTCCF");
        request.setMerchantSignature("LKHUILGOYUTDURDRDTYU*&)&TOUGVLGVL");
        request.setPaymentMethod("IDEAL");
        request.setPspReference("KHYOFITDIRTDIRT&PGOYGF&*O)TCK");
        request.setShopperLocale("en_EN");
        request.setSkinCode("HLIGOYGOY");
        return request;
    }

    /* Payment provider notification request  */
    public PaymentProviderNotificationRequestDto createPaymentProviderNotificationRequestDto() {
        final PaymentProviderNotificationRequestDto requestDto = new PaymentProviderNotificationRequestDto();
        requestDto.setRawContent("Raw content of payment provider notification");
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

    /* Adyen payment settings */
    public AdyenPaymentSettingsDto createAdyenPaymentSettingsDto() {
        final AdyenPaymentSettingsDto paymentSettingsDto = new AdyenPaymentSettingsDto();
        paymentSettingsDto.setEnvironmentType(EnvironmentType.PRODUCTION);
        paymentSettingsDto.setUserName("sfl");
        paymentSettingsDto.setPassword("sfl123456");
        paymentSettingsDto.setSkinCode("TF76GFF");
        paymentSettingsDto.setSkinHmacKey("HHLVGCGFCF767VGV&^^(6r45GCJ$$^)");
        paymentSettingsDto.setHppPaymentUrl("http://test.adyen.com/select.html");
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

}
