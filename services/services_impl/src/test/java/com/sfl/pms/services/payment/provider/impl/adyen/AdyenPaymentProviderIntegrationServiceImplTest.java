package com.sfl.pms.services.payment.provider.impl.adyen;

import com.sfl.pms.externalclients.payment.adyen.model.payment.PaymentAmountModel;
import com.sfl.pms.persistence.utility.PersistenceUtilityService;
import com.sfl.pms.services.common.exception.ServicesRuntimeException;
import com.sfl.pms.services.customer.CustomerService;
import com.sfl.pms.services.customer.model.Customer;
import com.sfl.pms.services.encryption.EncryptionUtilityService;
import com.sfl.pms.services.payment.common.PaymentService;
import com.sfl.pms.services.payment.common.model.Payment;
import com.sfl.pms.services.payment.common.model.channel.DeferredPaymentMethodProcessingChannel;
import com.sfl.pms.services.payment.common.model.channel.PaymentProcessingChannel;
import com.sfl.pms.services.payment.common.model.channel.ProvidedPaymentMethodProcessingChannel;
import com.sfl.pms.services.payment.common.model.order.OrderPayment;
import com.sfl.pms.services.payment.customer.information.adyen.CustomerAdyenInformationService;
import com.sfl.pms.services.payment.customer.information.model.adyen.CustomerAdyenInformation;
import com.sfl.pms.services.payment.method.model.PaymentMethodType;
import com.sfl.pms.services.payment.method.model.adyen.AdyenPaymentMethodType;
import com.sfl.pms.services.payment.provider.dto.AdyenRedirectUrlGenerationDto;
import com.sfl.pms.services.payment.provider.model.PaymentProviderIntegrationType;
import com.sfl.pms.services.payment.provider.model.PaymentProviderType;
import com.sfl.pms.services.payment.provider.model.adyen.AdyenRecurringContractType;
import com.sfl.pms.services.payment.redirect.dto.redirect.adyen.AdyenRedirectResultDto;
import com.sfl.pms.services.payment.settings.adyen.AdyenPaymentSettingsService;
import com.sfl.pms.services.payment.settings.model.adyen.AdyenPaymentSettings;
import com.sfl.pms.services.test.AbstractServicesUnitTest;
import org.apache.commons.lang3.StringUtils;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Test;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

/**
 * User: Ruben Dilanyan
 * Company: SFL LLC
 * Date: 6/30/15
 * Time: 2:53 PM
 */
public class AdyenPaymentProviderIntegrationServiceImplTest extends AbstractServicesUnitTest {

    /* Test subject and mocks */
    @TestSubject
    private AdyenPaymentProviderIntegrationServiceImpl adyenPaymentProviderIntegrationService = new AdyenPaymentProviderIntegrationServiceImpl();

    @Mock
    private PaymentService paymentService;

    @Mock
    private EncryptionUtilityService encryptionUtilityService;

    @Mock
    private AdyenPaymentSettingsService adyenPaymentSettingsService;

    @Mock
    private CustomerAdyenInformationService customerAdyenInformationService;

    @Mock
    private CustomerService customerService;

    @Mock
    private PersistenceUtilityService persistenceUtilityService;

    /* Constructors */
    public AdyenPaymentProviderIntegrationServiceImplTest() {
    }

    /* Test methods */
    @Test
    public void testCalculateSignatureForAdyenRedirectResultWithInvalidArguments() {
        // Reset
        resetAll();
        // Replay
        replayAll();
        // Run test scenario
        try {
            adyenPaymentProviderIntegrationService.calculateSignatureForAdyenRedirectResult(null);
            fail("Exception should be thrown");
        } catch (final IllegalArgumentException ex) {
            // Expected
        }
        // Verify
        verifyAll();
    }

    @Test
    public void testCalculateSignatureForAdyenRedirectResult() {
        final AdyenRedirectResultDto adyenRedirectResultDto = getServicesImplTestHelper().createAdyenRedirectResultDto();
        final AdyenPaymentSettings adyenPaymentSettings = getServicesImplTestHelper().createAdyenPaymentSettings();
        final String hmacSource = calculateHmacSourceForAdyenRedirectResult(adyenRedirectResultDto);
        // Reset
        resetAll();
        // Expectations
        expect(adyenPaymentSettingsService.getActivePaymentSettings()).andReturn(adyenPaymentSettings).once();
        expect(encryptionUtilityService.calculateBase64EncodedHmacSha256(eq(hmacSource), eq(adyenPaymentSettings.getSkinHmacKey()))).andReturn(adyenRedirectResultDto.getMerchantSig()).once();
        // Replay
        replayAll();
        // Run test scenario
        final String result = adyenPaymentProviderIntegrationService.calculateSignatureForAdyenRedirectResult(adyenRedirectResultDto);
        assertEquals(adyenRedirectResultDto.getMerchantSig(), result);
        // Verify
        verifyAll();
    }

    @Test
    public void testVerifySignatureForAdyenRedirectResultWithInvalidArguments() {
        // Reset
        resetAll();
        // Replay
        replayAll();
        // Run test scenario
        try {
            adyenPaymentProviderIntegrationService.verifySignatureForAdyenRedirectResult(null);
            fail("Exception should be thrown");
        } catch (final IllegalArgumentException ex) {
            // Expected
        }
        try {
            final AdyenRedirectResultDto adyenRedirectResultDto = getServicesImplTestHelper().createAdyenRedirectResultDto();
            adyenRedirectResultDto.setMerchantSig(null);
            adyenPaymentProviderIntegrationService.verifySignatureForAdyenRedirectResult(adyenRedirectResultDto);
            fail("Exception should be thrown");
        } catch (final IllegalArgumentException ex) {
            // Expected
        }
        // Verify
        verifyAll();
    }

    @Test
    public void testVerifySignatureForAdyenRedirectResultWithPositiveProbe() {
        final AdyenRedirectResultDto adyenRedirectResultDto = getServicesImplTestHelper().createAdyenRedirectResultDto();
        final AdyenPaymentSettings adyenPaymentSettings = getServicesImplTestHelper().createAdyenPaymentSettings();
        final String hmacSource = calculateHmacSourceForAdyenRedirectResult(adyenRedirectResultDto);
        // Reset
        resetAll();
        // Expectations
        expect(adyenPaymentSettingsService.getActivePaymentSettings()).andReturn(adyenPaymentSettings).once();
        expect(encryptionUtilityService.calculateBase64EncodedHmacSha256(eq(hmacSource), eq(adyenPaymentSettings.getSkinHmacKey()))).andReturn(adyenRedirectResultDto.getMerchantSig()).once();
        // Replay
        replayAll();
        // Run test scenario
        final boolean result = adyenPaymentProviderIntegrationService.verifySignatureForAdyenRedirectResult(adyenRedirectResultDto);
        assertTrue(result);
        // Verify
        verifyAll();
    }

    @Test
    public void testVerifySignatureForAdyenRedirectResultWithNegativeProbe() {
        final AdyenRedirectResultDto adyenRedirectResultDto = getServicesImplTestHelper().createAdyenRedirectResultDto();
        final AdyenPaymentSettings adyenPaymentSettings = getServicesImplTestHelper().createAdyenPaymentSettings();
        final String hmacSource = calculateHmacSourceForAdyenRedirectResult(adyenRedirectResultDto);
        // Reset
        resetAll();
        // Expectations
        expect(adyenPaymentSettingsService.getActivePaymentSettings()).andReturn(adyenPaymentSettings).once();
        expect(encryptionUtilityService.calculateBase64EncodedHmacSha256(eq(hmacSource), eq(adyenPaymentSettings.getSkinHmacKey()))).andReturn(adyenRedirectResultDto.getMerchantSig() + "_invalid").once();
        // Replay
        replayAll();
        // Run test scenario
        final boolean result = adyenPaymentProviderIntegrationService.verifySignatureForAdyenRedirectResult(adyenRedirectResultDto);
        assertFalse(result);
        // Verify
        verifyAll();
    }

    @Test
    public void testCreateAdyenRedirectGenerationDtoForPaymentWithInvalidArguments() {
        // Test data
        final Long paymentId = 1L;
        final AdyenRecurringContractType recurringContractType = AdyenRecurringContractType.RECURRING;
        // Reset
        resetAll();
        // Replay
        replayAll();
        // Run test scenario
        try {
            adyenPaymentProviderIntegrationService.createAdyenRedirectGenerationDtoForPayment(null, recurringContractType);
            fail("Exception should be thrown");
        } catch (final IllegalArgumentException ex) {
            // Expected
        }
        try {
            adyenPaymentProviderIntegrationService.createAdyenRedirectGenerationDtoForPayment(paymentId, null);
            fail("Exception should be thrown");
        } catch (final IllegalArgumentException ex) {
            // Expected
        }
        // Verify
        verifyAll();
    }

    @Test
    public void testCreateAdyenRedirectGenerationDtoForPaymentWithInvalidPaymentProviderType() {
        // Test data
        final Long paymentId = 1L;
        final Payment payment = getServicesImplTestHelper().createOrderPayment();
        payment.setPaymentProviderType(PaymentProviderType.BRAINTREE);
        payment.setId(paymentId);
        final AdyenRecurringContractType recurringContractType = AdyenRecurringContractType.RECURRING;
        // Reset
        resetAll();
        // Expectations
        expect(paymentService.getPaymentById(eq(paymentId))).andReturn(payment).once();
        // Replay
        replayAll();
        // Run test scenario
        try {
            adyenPaymentProviderIntegrationService.createAdyenRedirectGenerationDtoForPayment(paymentId, recurringContractType);
            fail("Exception should be thrown");
        } catch (final IllegalArgumentException ex) {
            // Expected
        }
        // Verify
        verifyAll();
    }

    @Test
    public void testCreateAdyenRedirectGenerationDtoForPaymentWithInvalidPaymentProcessingChannelIntegrationType() {
        // Test data
        final Long paymentId = 1L;
        final Payment payment = getServicesImplTestHelper().createOrderPayment();
        payment.setPaymentProviderType(PaymentProviderType.ADYEN);
        payment.setId(paymentId);
        PaymentProcessingChannel paymentProcessingChannel = getServicesImplTestHelper().createProvidedPaymentMethodProcessingChannel();
        paymentProcessingChannel.setPaymentProviderIntegrationType(PaymentProviderIntegrationType.API);
        payment.setPaymentProcessingChannel(paymentProcessingChannel);
        final AdyenRecurringContractType recurringContractType = AdyenRecurringContractType.RECURRING;
        // Reset
        resetAll();
        // Expectations
        expect(paymentService.getPaymentById(eq(paymentId))).andReturn(payment).once();
        expect(persistenceUtilityService.initializeAndUnProxy(paymentProcessingChannel)).andReturn(paymentProcessingChannel).once();
        // Replay
        replayAll();
        // Run test scenario
        try {
            adyenPaymentProviderIntegrationService.createAdyenRedirectGenerationDtoForPayment(paymentId, recurringContractType);
            fail("Exception should be thrown");
        } catch (final IllegalArgumentException ex) {
            // Expected
        }
        // Verify
        verifyAll();
    }

    @Test
    public void testCreateAdyenRedirectGenerationDtoForPaymentWithSelectedPaymentMethod() {
        // Create payment channel
        final ProvidedPaymentMethodProcessingChannel methodProcessingChannel = getServicesImplTestHelper().createProvidedPaymentMethodProcessingChannel();
        methodProcessingChannel.setPaymentMethodType(PaymentMethodType.IDEAL);
        methodProcessingChannel.setPaymentProviderIntegrationType(PaymentProviderIntegrationType.REDIRECT);
        // Execute test
        testCreateAdyenRedirectGenerationDtoForPayment(methodProcessingChannel);
    }

    @Test
    public void testCreateAdyenRedirectGenerationDtoForPaymentWithNotSelectedPaymentMethod() {
        // Create payment channel
        final DeferredPaymentMethodProcessingChannel methodProcessingChannel = getServicesImplTestHelper().createDeferredPaymentMethodProcessingChannel();
        methodProcessingChannel.setPaymentProviderIntegrationType(PaymentProviderIntegrationType.REDIRECT);
        // Execute test
        testCreateAdyenRedirectGenerationDtoForPayment(methodProcessingChannel);
    }

    @Test
    public void testGeneratePaymentProviderRedirectUrlForDtoWithInvalidArguments() {
        // Test data
        AdyenRedirectUrlGenerationDto urlGenerationDto = null;
        // Reset
        resetAll();
        // Replay
        replayAll();
        // Run test scenario
        try {
            adyenPaymentProviderIntegrationService.generatePaymentProviderRedirectUrlForDto(null);
            fail("Exception should be thrown");
        } catch (final IllegalArgumentException ex) {
            // Expected
        }
        try {
            urlGenerationDto = getServicesImplTestHelper().createAdyenRedirectUrlGenerationDto();
            urlGenerationDto.setMerchantReference(null);
            adyenPaymentProviderIntegrationService.generatePaymentProviderRedirectUrlForDto(urlGenerationDto);
            fail("Exception should be thrown");
        } catch (final IllegalArgumentException ex) {
            // Expected
        }
        try {
            urlGenerationDto = getServicesImplTestHelper().createAdyenRedirectUrlGenerationDto();
            urlGenerationDto.setPaymentAmount(null);
            adyenPaymentProviderIntegrationService.generatePaymentProviderRedirectUrlForDto(urlGenerationDto);
            fail("Exception should be thrown");
        } catch (final IllegalArgumentException ex) {
            // Expected
        }
        try {
            urlGenerationDto = getServicesImplTestHelper().createAdyenRedirectUrlGenerationDto();
            urlGenerationDto.setCurrencyCode(null);
            adyenPaymentProviderIntegrationService.generatePaymentProviderRedirectUrlForDto(urlGenerationDto);
            fail("Exception should be thrown");
        } catch (final IllegalArgumentException ex) {
            // Expected
        }
        try {
            urlGenerationDto = getServicesImplTestHelper().createAdyenRedirectUrlGenerationDto();
            urlGenerationDto.setShipBeforeDate(null);
            adyenPaymentProviderIntegrationService.generatePaymentProviderRedirectUrlForDto(urlGenerationDto);
            fail("Exception should be thrown");
        } catch (final IllegalArgumentException ex) {
            // Expected
        }
        try {
            urlGenerationDto = getServicesImplTestHelper().createAdyenRedirectUrlGenerationDto();
            urlGenerationDto.setSkinCode(null);
            adyenPaymentProviderIntegrationService.generatePaymentProviderRedirectUrlForDto(urlGenerationDto);
            fail("Exception should be thrown");
        } catch (final IllegalArgumentException ex) {
            // Expected
        }
        try {
            urlGenerationDto = getServicesImplTestHelper().createAdyenRedirectUrlGenerationDto();
            urlGenerationDto.setMerchantAccount(null);
            adyenPaymentProviderIntegrationService.generatePaymentProviderRedirectUrlForDto(urlGenerationDto);
            fail("Exception should be thrown");
        } catch (final IllegalArgumentException ex) {
            // Expected
        }
        try {
            urlGenerationDto = getServicesImplTestHelper().createAdyenRedirectUrlGenerationDto();
            urlGenerationDto.setSessionValidity(null);
            adyenPaymentProviderIntegrationService.generatePaymentProviderRedirectUrlForDto(urlGenerationDto);
            fail("Exception should be thrown");
        } catch (final IllegalArgumentException ex) {
            // Expected
        }
        try {
            urlGenerationDto = getServicesImplTestHelper().createAdyenRedirectUrlGenerationDto();
            urlGenerationDto.setMerchantReturnData(null);
            adyenPaymentProviderIntegrationService.generatePaymentProviderRedirectUrlForDto(urlGenerationDto);
            fail("Exception should be thrown");
        } catch (final IllegalArgumentException ex) {
            // Expected
        }
        try {
            urlGenerationDto = getServicesImplTestHelper().createAdyenRedirectUrlGenerationDto();
            urlGenerationDto.setSignatureHmacKey(null);
            adyenPaymentProviderIntegrationService.generatePaymentProviderRedirectUrlForDto(urlGenerationDto);
            fail("Exception should be thrown");
        } catch (final IllegalArgumentException ex) {
            // Expected
        }
        try {
            urlGenerationDto = getServicesImplTestHelper().createAdyenRedirectUrlGenerationDto();
            urlGenerationDto.setAdyenRedirectBaseUrl(null);
            adyenPaymentProviderIntegrationService.generatePaymentProviderRedirectUrlForDto(urlGenerationDto);
            fail("Exception should be thrown");
        } catch (final IllegalArgumentException ex) {
            // Expected
        }
        // Verify
        verifyAll();
    }

    @Test
    public void testGeneratePaymentProviderRedirectUrlForDto() {
        // Test data
        final AdyenRedirectUrlGenerationDto adyenRedirectUrlGenerationDto = getServicesImplTestHelper().createAdyenRedirectUrlGenerationDto();
        final String signedRequestValue = "signedrequest";
        // Reset
        resetAll();
        // Expectations
        expect(encryptionUtilityService.calculateBase64EncodedHmacSha256(isA(String.class), eq(adyenRedirectUrlGenerationDto.getSignatureHmacKey()))).andReturn(signedRequestValue).once();
        // Replay
        replayAll();
        // Run test scenario
        final String redirectionUrl = adyenPaymentProviderIntegrationService.generatePaymentProviderRedirectUrlForDto(adyenRedirectUrlGenerationDto);
        assertNotNull(redirectionUrl);
        // Assert that parameters are found in URL
        assertTrue(redirectionUrl.contains(encodeURLParameter(adyenRedirectUrlGenerationDto.getMerchantReturnData())));
        assertTrue(redirectionUrl.contains(encodeURLParameter(adyenRedirectUrlGenerationDto.getSessionValidity())));
        assertTrue(redirectionUrl.contains(encodeURLParameter(adyenRedirectUrlGenerationDto.getShipBeforeDate())));
        assertTrue(redirectionUrl.contains(encodeURLParameter(adyenRedirectUrlGenerationDto.getShopperLocale())));
        assertTrue(redirectionUrl.contains(encodeURLParameter(adyenRedirectUrlGenerationDto.getCountryCode())));
        assertTrue(redirectionUrl.contains(encodeURLParameter(adyenRedirectUrlGenerationDto.getCurrencyCode())));
        assertTrue(redirectionUrl.contains(encodeURLParameter(adyenRedirectUrlGenerationDto.getMerchantAccount())));
        assertTrue(redirectionUrl.contains(encodeURLParameter(adyenRedirectUrlGenerationDto.getMerchantReference())));
        assertTrue(redirectionUrl.contains(encodeURLParameter(adyenRedirectUrlGenerationDto.getRecurringContract())));
        assertTrue(redirectionUrl.contains(encodeURLParameter(adyenRedirectUrlGenerationDto.getShopperEmail())));
        assertTrue(redirectionUrl.contains(encodeURLParameter(adyenRedirectUrlGenerationDto.getShopperReference())));
        assertTrue(redirectionUrl.contains(encodeURLParameter(adyenRedirectUrlGenerationDto.getSkinCode())));
        assertTrue(redirectionUrl.contains(encodeURLParameter(adyenRedirectUrlGenerationDto.getPaymentAmount().toString())));
        assertTrue(redirectionUrl.contains(encodeURLParameter(adyenRedirectUrlGenerationDto.getBrandCode())));
        assertTrue(redirectionUrl.contains(encodeURLParameter(signedRequestValue)));
        // Verify
        verifyAll();
    }

    /* Utility methods */
    private void testCreateAdyenRedirectGenerationDtoForPayment(final PaymentProcessingChannel methodProcessingChannel) {
        // Test data
        final Long customerId = 2L;
        final Customer customer = getServicesImplTestHelper().createCustomer();
        customer.setId(customerId);
        final Long paymentId = 1L;
        final OrderPayment payment = getServicesImplTestHelper().createOrderPayment();
        payment.setPaymentProviderType(PaymentProviderType.ADYEN);
        payment.setId(paymentId);
        payment.setCustomer(customer);
        payment.setPaymentProcessingChannel(methodProcessingChannel);
        final AdyenRecurringContractType recurringContractType = AdyenRecurringContractType.RECURRING;
        // Create payment settings
        final AdyenPaymentSettings adyenPaymentSettings = getServicesImplTestHelper().createAdyenPaymentSettings();
        // Session validity date
        final DateTime sessionValidityDate = new DateTime().plusDays(5).withTimeAtStartOfDay();
        final String formattedSessionValidity = ISODateTimeFormat.dateTimeNoMillis().print(sessionValidityDate);
        final DateTime shipBeforeDate = new DateTime().plusDays(1).withTimeAtStartOfDay();
        final String formattedShipBeforeDate = shipBeforeDate.toString(DateTimeFormat.forPattern("YYYY-MM-dd"));
        // Customer Adyen information
        final CustomerAdyenInformation customerAdyenInformation = getServicesImplTestHelper().createCustomerAdyenInformation();
        // Payment amount model
        final PaymentAmountModel paymentAmountModel = new PaymentAmountModel(payment.getCurrency().getCode(), payment.getPaymentTotalAmount());
        // Determine payment method and redirect base URL
        final AdyenPaymentMethodType adyenPaymentMethodType;
        final String redirectBaseUrl;
        if (methodProcessingChannel.getPaymentMethodTypeIfDefined() != null) {
            adyenPaymentMethodType = methodProcessingChannel.getPaymentMethodTypeIfDefined().getAdyenPaymentMethod();
            redirectBaseUrl = adyenPaymentSettings.getHppPaymentUrl();
        } else {
            adyenPaymentMethodType = null;
            redirectBaseUrl = adyenPaymentSettings.getHppPaymentMethodSelectionUrl();
        }
        // Reset
        resetAll();
        // Expectations
        expect(paymentService.getPaymentById(eq(paymentId))).andReturn(payment).once();
        expect(adyenPaymentSettingsService.getActivePaymentSettings()).andReturn(adyenPaymentSettings).once();
        expect(customerAdyenInformationService.getOrCreateCustomerPaymentProviderInformation(eq(customerId))).andReturn(customerAdyenInformation).once();
        expect(persistenceUtilityService.initializeAndUnProxy(eq(methodProcessingChannel))).andReturn(methodProcessingChannel).once();
        // Replay
        replayAll();
        // Run test scenario
        final AdyenRedirectUrlGenerationDto urlGenerationDto = adyenPaymentProviderIntegrationService.createAdyenRedirectGenerationDtoForPayment(paymentId, recurringContractType);
        assertNotNull(urlGenerationDto);
        assertEquals(adyenPaymentSettings.getSkinCode(), urlGenerationDto.getSkinCode());
        assertEquals(redirectBaseUrl, urlGenerationDto.getAdyenRedirectBaseUrl());
        if (adyenPaymentMethodType != null) {
            assertEquals(adyenPaymentMethodType.getCodes().iterator().next(), urlGenerationDto.getBrandCode());
        } else {
            assertNull(urlGenerationDto.getBrandCode());
        }
        assertEquals(adyenPaymentSettings.getDefaultCountry().name(), urlGenerationDto.getCountryCode());
        assertEquals(payment.getCurrency().getCode(), urlGenerationDto.getCurrencyCode());
        assertEquals(adyenPaymentSettings.getMerchantAccount(), urlGenerationDto.getMerchantAccount());
        assertEquals(payment.getUuId(), urlGenerationDto.getMerchantReference());
        assertEquals(recurringContractType.getCode(), urlGenerationDto.getRecurringContract());
        assertEquals(formattedSessionValidity, urlGenerationDto.getSessionValidity());
        assertEquals(customerAdyenInformation.getShopperEmail(), urlGenerationDto.getShopperEmail());
        assertEquals(customerAdyenInformation.getShopperReference(), urlGenerationDto.getShopperReference());
        assertEquals(paymentAmountModel.getPaymentAmountInMinorUnits(), urlGenerationDto.getPaymentAmount().longValue());
        assertEquals(formattedShipBeforeDate, urlGenerationDto.getShipBeforeDate());
        assertEquals(customerAdyenInformation.getShopperReference(), urlGenerationDto.getMerchantReturnData());
        assertNull(urlGenerationDto.getShopperLocale());
        assertEquals(adyenPaymentSettings.getSkinHmacKey(), urlGenerationDto.getSignatureHmacKey());
        // Verify
        verifyAll();
    }

    private String encodeURLParameter(final String value) {
        try {
            return URLEncoder.encode(value, "utf8");
        } catch (final UnsupportedEncodingException ex) {
            throw new ServicesRuntimeException(ex);
        }
    }

    private String calculateHmacSourceForAdyenRedirectResult(final AdyenRedirectResultDto adyenRedirectResult) {
        final StringBuilder builder = new StringBuilder();
        final StringBuilder paramValueBuilder = new StringBuilder();
        if (StringUtils.isNoneBlank(adyenRedirectResult.getAuthResult())) {
            builder.append("authResult");
            builder.append(":");
            paramValueBuilder.append(adyenRedirectResult.getAuthResult());
            paramValueBuilder.append(":");
        }
        if (StringUtils.isNoneBlank(adyenRedirectResult.getMerchantReference())) {
            builder.append("merchantReference");
            builder.append(":");
            paramValueBuilder.append(adyenRedirectResult.getMerchantReference());
            paramValueBuilder.append(":");
        }
        if (StringUtils.isNoneBlank(adyenRedirectResult.getMerchantReturnData())) {
            builder.append("merchantReturnData");
            builder.append(":");
            paramValueBuilder.append(adyenRedirectResult.getMerchantReturnData());
            paramValueBuilder.append(":");
        }
        if (StringUtils.isNoneBlank(adyenRedirectResult.getPaymentMethod())) {
            builder.append("paymentMethod");
            builder.append(":");
            paramValueBuilder.append(adyenRedirectResult.getPaymentMethod());
            paramValueBuilder.append(":");
        }
        if (StringUtils.isNoneBlank(adyenRedirectResult.getPspReference())) {
            builder.append("pspReference");
            builder.append(":");
            paramValueBuilder.append(adyenRedirectResult.getPspReference());
            paramValueBuilder.append(":");
        }
        if (StringUtils.isNoneBlank(adyenRedirectResult.getShopperLocale())) {
            builder.append("shopperLocale");
            builder.append(":");
            paramValueBuilder.append(adyenRedirectResult.getShopperLocale());
            paramValueBuilder.append(":");
        }
        if (StringUtils.isNoneBlank(adyenRedirectResult.getSkinCode())) {
            builder.append("skinCode");
            builder.append(":");
            paramValueBuilder.append(adyenRedirectResult.getSkinCode());
            paramValueBuilder.append(":");
        }
        paramValueBuilder.deleteCharAt(paramValueBuilder.length() - 1);
        builder.append(paramValueBuilder);
        return builder.toString();
    }

}
