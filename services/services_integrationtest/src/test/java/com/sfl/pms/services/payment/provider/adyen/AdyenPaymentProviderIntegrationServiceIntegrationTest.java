package com.sfl.pms.services.payment.provider.adyen;

import com.sfl.pms.externalclients.payment.adyen.model.payment.PaymentAmountModel;
import com.sfl.pms.services.customer.CustomerService;
import com.sfl.pms.services.customer.model.Customer;
import com.sfl.pms.services.payment.common.dto.channel.DeferredPaymentMethodProcessingChannelDto;
import com.sfl.pms.services.payment.common.dto.channel.PaymentProcessingChannelDto;
import com.sfl.pms.services.payment.common.dto.channel.ProvidedPaymentMethodProcessingChannelDto;
import com.sfl.pms.services.payment.common.dto.order.OrderPaymentDto;
import com.sfl.pms.services.payment.common.model.channel.PaymentProcessingChannel;
import com.sfl.pms.services.payment.common.model.order.OrderPayment;
import com.sfl.pms.services.payment.customer.information.adyen.CustomerAdyenInformationService;
import com.sfl.pms.services.payment.customer.information.model.adyen.CustomerAdyenInformation;
import com.sfl.pms.services.payment.method.model.PaymentMethodType;
import com.sfl.pms.services.payment.provider.dto.AdyenRedirectUrlGenerationDto;
import com.sfl.pms.services.payment.provider.model.PaymentProviderIntegrationType;
import com.sfl.pms.services.payment.provider.model.adyen.AdyenRecurringContractType;
import com.sfl.pms.services.payment.redirect.dto.redirect.adyen.AdyenRedirectResultDto;
import com.sfl.pms.services.payment.settings.model.adyen.AdyenPaymentSettings;
import com.sfl.pms.services.test.AbstractServiceIntegrationTest;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.HmacUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.UnsupportedEncodingException;

import static org.junit.Assert.*;

/**
 * User: Ruben Dilanyan
 * Company: SFL LLC
 * Date: 7/3/15
 * Time: 2:21 PM
 */
public class AdyenPaymentProviderIntegrationServiceIntegrationTest extends AbstractServiceIntegrationTest {

    /* Dependencies */
    @Autowired
    private AdyenPaymentProviderIntegrationService adyenPaymentProviderIntegrationService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerAdyenInformationService customerAdyenInformationService;

    /* Constructors */
    public AdyenPaymentProviderIntegrationServiceIntegrationTest() {
    }

    /* Test methods */
    @Test
    public void testVerifySignatureForAdyenRedirectResult() {
        // Create Adyen payment settings
        final AdyenPaymentSettings adyenPaymentSettings = getServicesTestHelper().createAdyenPaymentSettings();
        final AdyenRedirectResultDto redirectResultDto = getServicesTestHelper().createAdyenRedirectResultDto();
        try {
            redirectResultDto.setMerchantSig(calculateHmacForAdyenRedirectResult(redirectResultDto, adyenPaymentSettings.getSkinHmacKey()));
        } catch (DecoderException | UnsupportedEncodingException e) {
            fail("Failed to calculate hmac for adyen redirect result");
        }
        // Calculate verify signature
        final boolean isSignatureValid = adyenPaymentProviderIntegrationService.verifySignatureForAdyenRedirectResult(redirectResultDto);
        assertTrue(isSignatureValid);
    }

    @Test
    public void testCalculateSignatureForAdyenRedirectResult() {
        // Create Adyen payment settings
        final AdyenPaymentSettings adyenPaymentSettings = getServicesTestHelper().createAdyenPaymentSettings();
        final AdyenRedirectResultDto redirectResultDto = getServicesTestHelper().createAdyenRedirectResultDto();
        final String expectedSignature;
        try {
            expectedSignature = calculateHmacForAdyenRedirectResult(redirectResultDto, adyenPaymentSettings.getSkinHmacKey());
        } catch (DecoderException | UnsupportedEncodingException e) {
            fail("Failed to calculate hmac for adyen redirect result");
            return;
        }
        // Calculate verify signature
        final String result = adyenPaymentProviderIntegrationService.calculateSignatureForAdyenRedirectResult(redirectResultDto);
        assertEquals(expectedSignature, result);
    }

    @Test
    public void testCreateAdyenRedirectGenerationDtoForPaymentWithSelectedPaymentMethod() {
        // Prepare data
        final ProvidedPaymentMethodProcessingChannelDto processingChannelDto = new ProvidedPaymentMethodProcessingChannelDto();
        processingChannelDto.setPaymentMethodType(PaymentMethodType.IDEAL);
        processingChannelDto.setPaymentProviderIntegrationType(PaymentProviderIntegrationType.REDIRECT);
        // Run test
        testCreateAdyenRedirectGenerationDtoForPayment(processingChannelDto);
    }

    @Test
    public void testCreateAdyenRedirectGenerationDtoForPaymentWithNotSelectedPaymentMethod() {
        // Prepare data
        final DeferredPaymentMethodProcessingChannelDto processingChannelDto = new DeferredPaymentMethodProcessingChannelDto();
        processingChannelDto.setPaymentProviderIntegrationType(PaymentProviderIntegrationType.REDIRECT);
        // Run test
        testCreateAdyenRedirectGenerationDtoForPayment(processingChannelDto);
    }

    @Test
    public void testGeneratePaymentProviderRedirectUrlForDto() throws Exception {
        // Prepare data
        final AdyenPaymentSettings adyenPaymentSettings = getServicesTestHelper().createAdyenPaymentSettings();
        assertNotNull(adyenPaymentSettings);
        final ProvidedPaymentMethodProcessingChannelDto processingChannelDto = new ProvidedPaymentMethodProcessingChannelDto();
        processingChannelDto.setPaymentMethodType(PaymentMethodType.IDEAL);
        processingChannelDto.setPaymentProviderIntegrationType(PaymentProviderIntegrationType.REDIRECT);
        final OrderPaymentDto orderPaymentDto = getServicesTestHelper().createOrderPaymentDto();
        final OrderPayment payment = getServicesTestHelper().createOrderPayment(orderPaymentDto, processingChannelDto);
        final Customer customer = payment.getCustomer();
        final CustomerAdyenInformation customerAdyenInformation = customerAdyenInformationService.getOrCreateCustomerPaymentProviderInformation(customer.getId());
        assertNotNull(customerAdyenInformation);
        flushAndClear();
        // Generate DTO for order payment
        final AdyenRedirectUrlGenerationDto urlGenerationDto = adyenPaymentProviderIntegrationService.createAdyenRedirectGenerationDtoForPayment(payment.getId(), AdyenRecurringContractType.RECURRING);
        // Generate URL
        final String redirectionUrl = adyenPaymentProviderIntegrationService.generatePaymentProviderRedirectUrlForDto(urlGenerationDto);
        assertNotNull(redirectionUrl);
        // Download content and assert
        getServicesTestHelper().assertAdyenIdealRedirectUrlIsCorrect(redirectionUrl);
    }

    /* Utility methods */
    private void testCreateAdyenRedirectGenerationDtoForPayment(final PaymentProcessingChannelDto<? extends PaymentProcessingChannel> processingChannelDto) {
        // Prepare data
        final AdyenPaymentSettings adyenPaymentSettings = getServicesTestHelper().createAdyenPaymentSettings();
        final OrderPayment payment = getServicesTestHelper().createOrderPayment(getServicesTestHelper().createOrderPaymentDto(), processingChannelDto);
        final Customer customer = payment.getCustomer();
        final CustomerAdyenInformation customerAdyenInformation = customerAdyenInformationService.getOrCreateCustomerPaymentProviderInformation(customer.getId());
        final PaymentProcessingChannel processingChannel = payment.getPaymentProcessingChannel();
        // Payment amount model
        final PaymentAmountModel paymentAmountModel = new PaymentAmountModel(payment.getCurrency().getCode(), payment.getPaymentTotalAmount());
        flushAndClear();
        // Generate DTO for order payment
        final AdyenRedirectUrlGenerationDto urlGenerationDto = adyenPaymentProviderIntegrationService.createAdyenRedirectGenerationDtoForPayment(payment.getId(), AdyenRecurringContractType.RECURRING);
        assertNotNull(urlGenerationDto);
        assertEquals(adyenPaymentSettings.getSkinCode(), urlGenerationDto.getSkinCode());
        if (processingChannel.getPaymentMethodTypeIfDefined() != null) {
            assertEquals(adyenPaymentSettings.getHppPaymentUrl(), urlGenerationDto.getAdyenRedirectBaseUrl());
            assertEquals(processingChannel.getPaymentMethodTypeIfDefined().getAdyenPaymentMethod().getCodes().iterator().next(), urlGenerationDto.getBrandCode());
        } else {
            assertNull(urlGenerationDto.getBrandCode());
            assertEquals(adyenPaymentSettings.getHppPaymentMethodSelectionUrl(), urlGenerationDto.getAdyenRedirectBaseUrl());
        }
        assertEquals(adyenPaymentSettings.getDefaultCountry().name(), urlGenerationDto.getCountryCode());
        assertEquals(payment.getCurrency().getCode(), urlGenerationDto.getCurrencyCode());
        assertEquals(adyenPaymentSettings.getMerchantAccount(), urlGenerationDto.getMerchantAccount());
        assertEquals(payment.getUuId(), urlGenerationDto.getMerchantReference());
        assertEquals(AdyenRecurringContractType.RECURRING.getCode(), urlGenerationDto.getRecurringContract());
        assertEquals(customerAdyenInformation.getShopperEmail(), urlGenerationDto.getShopperEmail());
        assertEquals(customerAdyenInformation.getShopperReference(), urlGenerationDto.getShopperReference());
        assertEquals(paymentAmountModel.getPaymentAmountInMinorUnits(), urlGenerationDto.getPaymentAmount().longValue());
        assertEquals(customerAdyenInformation.getShopperReference(), urlGenerationDto.getMerchantReturnData());
        assertEquals(null, urlGenerationDto.getShopperLocale());
        assertEquals(adyenPaymentSettings.getSkinHmacKey(), urlGenerationDto.getSignatureHmacKey());
    }

    private String calculateHmacForAdyenRedirectResult(final AdyenRedirectResultDto adyenRedirectResult, final String hmacKey) throws DecoderException, UnsupportedEncodingException {
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
        final byte[] keyBytes = Hex.decodeHex(hmacKey.toCharArray());
        final byte[] valueBytes = builder.toString().getBytes("UTF-8");
        final byte[] hmac = HmacUtils.hmacSha256(keyBytes, valueBytes);
        return Base64.encodeBase64String(hmac);
    }
}
