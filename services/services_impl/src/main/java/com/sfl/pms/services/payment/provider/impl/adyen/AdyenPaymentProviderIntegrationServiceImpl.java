package com.sfl.pms.services.payment.provider.impl.adyen;

import com.sfl.pms.externalclients.payment.adyen.model.payment.PaymentAmountModel;
import com.sfl.pms.persistence.utility.PersistenceUtilityService;
import com.sfl.pms.services.common.exception.ServicesRuntimeException;
import com.sfl.pms.services.customer.CustomerService;
import com.sfl.pms.services.encryption.EncryptionUtilityService;
import com.sfl.pms.services.payment.common.PaymentService;
import com.sfl.pms.services.payment.common.model.Payment;
import com.sfl.pms.services.payment.common.model.channel.PaymentProcessingChannel;
import com.sfl.pms.services.payment.customer.information.adyen.CustomerAdyenInformationService;
import com.sfl.pms.services.payment.customer.information.model.adyen.CustomerAdyenInformation;
import com.sfl.pms.services.payment.method.model.PaymentMethodType;
import com.sfl.pms.services.payment.method.model.adyen.AdyenPaymentMethodType;
import com.sfl.pms.services.payment.provider.adyen.AdyenPaymentProviderIntegrationService;
import com.sfl.pms.services.payment.provider.dto.AdyenRedirectUrlGenerationDto;
import com.sfl.pms.services.payment.provider.model.PaymentProviderIntegrationType;
import com.sfl.pms.services.payment.provider.model.PaymentProviderType;
import com.sfl.pms.services.payment.provider.model.adyen.AdyenRecurringContractType;
import com.sfl.pms.services.payment.redirect.dto.redirect.adyen.AdyenRedirectResultDto;
import com.sfl.pms.services.payment.settings.adyen.AdyenPaymentSettingsService;
import com.sfl.pms.services.payment.settings.model.adyen.AdyenPaymentSettings;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * User: Ruben Dilanyan
 * Company: SFL LLC
 * Date: 6/30/15
 * Time: 2:50 PM
 */
@Service
public class AdyenPaymentProviderIntegrationServiceImpl implements AdyenPaymentProviderIntegrationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdyenPaymentProviderIntegrationServiceImpl.class);

    /* Constants */
    private static final String PARAMETER_PAYMENT_AMOUNT = "paymentAmount";

    private static final String PARAMETER_CURRENCY_CODE = "currencyCode";

    private static final String PARAMETER_MERCHANT_REFERENCE = "merchantReference";

    private static final String PARAMETER_SKIN_CODE = "skinCode";

    private static final String PARAMETER_MERCHANT_ACCOUNT = "merchantAccount";

    private static final String PARAMETER_SHOPPER_EMAIL = "shopperEmail";

    private static final String PARAMETER_SHOPPER_REFERENCE = "shopperReference";

    private static final String PARAMETER_RECURRING_CONTRACT = "recurringContract";

    private static final String PARAMETER_SIGNATURE = "merchantSig";

    private static final String PARAMETER_COUNTRY_CODE = "countryCode";

    private static final String PARAMETER_SHOPPER_LOCALE = "shopperLocale";

    private static final String PARAMETER_SHIP_BEFORE_DATE = "shipBeforeDate";

    private static final String PARAMETER_SESSION_VALIDITY = "sessionValidity";

    private static final String PARAMETER_MERCHANT_RETURN_DATA = "merchantReturnData";

    private static final String PARAMETER_BRAND_CODE = "brandCode";

    private static final String PARAMETER_AUTH_RESULT = "authResult";

    private static final String PARAMETER_PSP_REFERENCE = "pspReference";

    private static final String PARAMETER_PAYMENT_METHOD = "paymentMethod";

    private static final String URL_PARAMETERS_DELIMITER = "&";

    private static final String URL_BASE_URL_DELIMITER = "?";

    private static final String URL_PARAMETER_NAME_VALUE_DELIMITER = "=";

    private static final String HMAC_DELIMITER = ":";

    private static final int FIFE = 5;

    private static final int ONE = 1;

    private static final String SIGNATURE_IGNORE_FIELDS_PREFIX = "ignore.";

    /* Dependencies */
    @Autowired
    private PaymentService paymentService;

    @Autowired
    private EncryptionUtilityService encryptionUtilityService;

    @Autowired
    private AdyenPaymentSettingsService adyenPaymentSettingsService;

    @Autowired
    private CustomerAdyenInformationService customerAdyenInformationService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private PersistenceUtilityService persistenceUtilityService;

    /* Constructors */
    public AdyenPaymentProviderIntegrationServiceImpl() {
        LOGGER.debug("Initializing Adyen payment provider integration service");
    }

    @Nonnull
    @Override
    public String generatePaymentProviderRedirectUrlForDto(@Nonnull final AdyenRedirectUrlGenerationDto redirectUrlGenerationDto) {
        assertAdyenRedirectUrlGenerationDto(redirectUrlGenerationDto);
        LOGGER.debug("Generating payment provider URL for DTO - {}", redirectUrlGenerationDto);
        // Start building redirection URL
        final String queryString = generateQueryString(redirectUrlGenerationDto);
        final StringBuilder builder = new StringBuilder();
        builder.append(redirectUrlGenerationDto.getAdyenRedirectBaseUrl());
        builder.append(URL_BASE_URL_DELIMITER);
        builder.append(queryString);
        LOGGER.debug("Successfully generated Adyen redirect URL - {} for DTO - {}", queryString, redirectUrlGenerationDto);
        return builder.toString();
    }

    @Nonnull
    @Override
    public AdyenRedirectUrlGenerationDto createAdyenRedirectGenerationDtoForPayment(@Nonnull final Long paymentId, @Nonnull final AdyenRecurringContractType recurringContractType) {
        Assert.notNull(paymentId, "Payment id should not be null");
        Assert.notNull(recurringContractType, "Recurring contract type should not be null");
        LOGGER.debug("Creating Adyen payment redirection DTO for payment with id - {}", paymentId);
        final Payment payment = paymentService.getPaymentById(paymentId);
        Assert.isTrue(PaymentProviderType.ADYEN.equals(payment.getPaymentProviderType()), "Payment should have Adyen as selected payment provider type");
        final PaymentProcessingChannel paymentProcessingChannel = persistenceUtilityService.initializeAndUnProxy(payment.getPaymentProcessingChannel());
        Assert.isTrue(PaymentProviderIntegrationType.REDIRECT.equals(paymentProcessingChannel.getPaymentProviderIntegrationType()));
        final PaymentMethodType paymentMethodType = paymentProcessingChannel.getPaymentMethodTypeIfDefined();
        final AdyenPaymentMethodType adyenPaymentMethodType;
        if (paymentMethodType != null) {
            adyenPaymentMethodType = paymentMethodType.getAdyenPaymentMethod();
        } else {
            adyenPaymentMethodType = null;
        }
        // Calculate and set payment amount
        final PaymentAmountModel paymentAmountModel = new PaymentAmountModel(payment.getCurrency().getCode(), payment.getPaymentTotalAmount());
        // Calculate and set session validity
        final DateTime sessionValidityDate = new DateTime().plusDays(FIFE).withTimeAtStartOfDay();
        final String formattedSessionValidity = ISODateTimeFormat.dateTimeNoMillis().print(sessionValidityDate);
        // Calculate and set ship before date
        final DateTime shipBeforeDate = new DateTime().plusDays(ONE).withTimeAtStartOfDay();
        final String formattedShipBeforeDate = shipBeforeDate.toString(DateTimeFormat.forPattern("YYYY-MM-dd"));
        // Load Adyen payment settings
        final AdyenPaymentSettings adyenPaymentSettings = adyenPaymentSettingsService.getActivePaymentSettings();
        // Grab customer Adyen information
        final CustomerAdyenInformation customerAdyenInformation = customerAdyenInformationService.getOrCreateCustomerPaymentProviderInformation(payment.getCustomer().getId());
        // Create redirection DTO
        final AdyenRedirectUrlGenerationDto urlGenerationDto = new AdyenRedirectUrlGenerationDto();
        if (adyenPaymentMethodType != null) {
            urlGenerationDto.setBrandCode(adyenPaymentMethodType.getCodes().iterator().next());
            urlGenerationDto.setAdyenRedirectBaseUrl(adyenPaymentSettings.getHppPaymentUrl());
        } else {
            urlGenerationDto.setAdyenRedirectBaseUrl(adyenPaymentSettings.getHppPaymentMethodSelectionUrl());
        }
        urlGenerationDto.setSkinCode(adyenPaymentSettings.getSkinCode());
        urlGenerationDto.setCountryCode(adyenPaymentSettings.getDefaultCountry().name());
        urlGenerationDto.setCurrencyCode(payment.getCurrency().name());
        urlGenerationDto.setMerchantAccount(adyenPaymentSettings.getMerchantAccount());
        urlGenerationDto.setMerchantReference(payment.getUuId());
        if (!AdyenRecurringContractType.NONE.equals(recurringContractType)) {
            urlGenerationDto.setRecurringContract(recurringContractType.getCode());
        }
        urlGenerationDto.setSessionValidity(formattedSessionValidity);
        urlGenerationDto.setShopperEmail(customerAdyenInformation.getShopperEmail());
        urlGenerationDto.setShopperReference(customerAdyenInformation.getShopperReference());
        urlGenerationDto.setPaymentAmount(paymentAmountModel.getPaymentAmountInMinorUnits());
        urlGenerationDto.setShipBeforeDate(formattedShipBeforeDate);
        urlGenerationDto.setMerchantReturnData(customerAdyenInformation.getShopperReference());
        urlGenerationDto.setSignatureHmacKey(adyenPaymentSettings.getSkinHmacKey());
        LOGGER.debug("Successfully created Adyen URL generation DTO - {}", urlGenerationDto);
        return urlGenerationDto;
    }

    @Override
    public boolean verifySignatureForAdyenRedirectResult(@Nonnull final AdyenRedirectResultDto resultDto) {
        assertAdyenRedirectResultDto(resultDto);
        Assert.notNull(resultDto.getMerchantSig(), "Merchant signature should not be null");
        LOGGER.debug("Checking hmac signature for Adyen result DTO - {}", resultDto);
        // Calculate hmac for redirect result
        final String calculatedHmac = calculateSignatureForAdyenRedirectResult(resultDto);
        final boolean isSignatureValid = calculatedHmac.equals(resultDto.getMerchantSig());
        LOGGER.debug("Signature verification result for redirect result DTO - {} is - {}", resultDto, isSignatureValid);
        return isSignatureValid;
    }

    @Nonnull
    @Override
    public String calculateSignatureForAdyenRedirectResult(@Nonnull final AdyenRedirectResultDto resultDto) {
        assertAdyenRedirectResultDto(resultDto);
        LOGGER.debug("Calculating hmac signature for Adyen result DTO - {}", resultDto);
        // Grab payment settings
        final AdyenPaymentSettings adyenPaymentSettings = adyenPaymentSettingsService.getActivePaymentSettings();
        final String hmacKey = adyenPaymentSettings.getSkinHmacKey();
        // Calculate hmac for redirect result
        final String calculatedHmac = calculateHmacForAdyenRedirectResult(resultDto, hmacKey);
        LOGGER.debug("Successfully calculated signature for Adyen redirect result DTO - {}, signature - {}", resultDto, calculatedHmac);
        return calculatedHmac;
    }

    /* Utility methods */
    private void assertAdyenRedirectResultDto(final AdyenRedirectResultDto resultDto) {
        Assert.notNull(resultDto, "Result DTO should not be null");
    }

    private String calculateHmacForAdyenRedirectResult(final AdyenRedirectResultDto adyenRedirectResult, final String hmacKey) {
        final StringBuilder builder = new StringBuilder();
        final StringBuilder paramValueBuilder = new StringBuilder();
        if (StringUtils.isNoneBlank(adyenRedirectResult.getAuthResult())) {
            builder.append(PARAMETER_AUTH_RESULT);
            builder.append(HMAC_DELIMITER);
            paramValueBuilder.append(adyenRedirectResult.getAuthResult());
            paramValueBuilder.append(HMAC_DELIMITER);
        }
        if (StringUtils.isNoneBlank(adyenRedirectResult.getMerchantReference())) {
            builder.append(PARAMETER_MERCHANT_REFERENCE);
            builder.append(HMAC_DELIMITER);
            paramValueBuilder.append(adyenRedirectResult.getMerchantReference());
            paramValueBuilder.append(HMAC_DELIMITER);
        }
        if (StringUtils.isNoneBlank(adyenRedirectResult.getMerchantReturnData())) {
            builder.append(PARAMETER_MERCHANT_RETURN_DATA);
            builder.append(HMAC_DELIMITER);
            paramValueBuilder.append(adyenRedirectResult.getMerchantReturnData());
            paramValueBuilder.append(HMAC_DELIMITER);
        }
        if (StringUtils.isNoneBlank(adyenRedirectResult.getPaymentMethod())) {
            builder.append(PARAMETER_PAYMENT_METHOD);
            builder.append(HMAC_DELIMITER);
            paramValueBuilder.append(adyenRedirectResult.getPaymentMethod());
            paramValueBuilder.append(HMAC_DELIMITER);
        }
        if (StringUtils.isNoneBlank(adyenRedirectResult.getPspReference())) {
            builder.append(PARAMETER_PSP_REFERENCE);
            builder.append(HMAC_DELIMITER);
            paramValueBuilder.append(adyenRedirectResult.getPspReference());
            paramValueBuilder.append(HMAC_DELIMITER);
        }
        if (StringUtils.isNoneBlank(adyenRedirectResult.getShopperLocale())) {
            builder.append(PARAMETER_SHOPPER_LOCALE);
            builder.append(HMAC_DELIMITER);
            paramValueBuilder.append(adyenRedirectResult.getShopperLocale());
            paramValueBuilder.append(HMAC_DELIMITER);
        }
        if (StringUtils.isNoneBlank(adyenRedirectResult.getSkinCode())) {
            builder.append(PARAMETER_SKIN_CODE);
            builder.append(HMAC_DELIMITER);
            paramValueBuilder.append(adyenRedirectResult.getSkinCode());
            paramValueBuilder.append(HMAC_DELIMITER);
        }
        paramValueBuilder.deleteCharAt(paramValueBuilder.length() - 1);
        builder.append(paramValueBuilder);
        return encryptionUtilityService.calculateBase64EncodedHmacSha256(builder.toString(), hmacKey);
    }

    private void assertAdyenRedirectUrlGenerationDto(final AdyenRedirectUrlGenerationDto redirectUrlGenerationDto) {
        Assert.notNull(redirectUrlGenerationDto, "URL generation DTO should not be null");
        Assert.notNull(redirectUrlGenerationDto.getMerchantReference(), "Merchant reference in URL generation DTO should not be null");
        Assert.notNull(redirectUrlGenerationDto.getPaymentAmount(), "Payment amount in URL generation DTO should not be null");
        Assert.notNull(redirectUrlGenerationDto.getCurrencyCode(), "Currency code in URL generation DTO should not be null");
        Assert.notNull(redirectUrlGenerationDto.getShipBeforeDate(), "Ship before date in URL generation DTO should not be null");
        Assert.notNull(redirectUrlGenerationDto.getSkinCode(), "Skin code in URL generation DTO should not be null");
        Assert.notNull(redirectUrlGenerationDto.getMerchantAccount(), "Merchant account in URL generation DTO should not be null");
        Assert.notNull(redirectUrlGenerationDto.getSessionValidity(), "Session validity in URL generation DTO should not be null");
        Assert.notNull(redirectUrlGenerationDto.getMerchantReturnData(), "Merchant return data in URL generation DTO should not be null");
        Assert.notNull(redirectUrlGenerationDto.getSignatureHmacKey(), "Signature HMAC key in URL generation DTO should not be null");
        Assert.notNull(redirectUrlGenerationDto.getAdyenRedirectBaseUrl(), "Adyen redirection key in URL generation DTO should not be null");
    }

    private String generateQueryString(final AdyenRedirectUrlGenerationDto redirectUrlGenerationDto) {
        final Map<String, UrlParameter> urlParameterMap = generateUrlQueryParametersIncludingSignature(redirectUrlGenerationDto);
        // Generate query string
        final StringBuilder queryString = new StringBuilder();
        urlParameterMap.entrySet().forEach(entry -> {
            if (queryString.length() != 0) {
                queryString.append(URL_PARAMETERS_DELIMITER);
            }
            queryString.append(entry.getKey() + URL_PARAMETER_NAME_VALUE_DELIMITER + entry.getValue().getEncodedValue());
        });
        return queryString.toString();
    }

    private String calculateHmacForRedirectParameters(final String hmacKey, final SortedMap<String, UrlParameter> parameterMap) {
        final StringBuilder hmacBuilder = new StringBuilder();
        final StringBuilder valueBuilder = new StringBuilder();
        parameterMap.forEach((key, urlParameter) -> {
            if(!key.startsWith(SIGNATURE_IGNORE_FIELDS_PREFIX)) {
                hmacBuilder.append(key);
                hmacBuilder.append(HMAC_DELIMITER);
                valueBuilder.append(escapeMerchantSignatureValues(urlParameter.getValue()));
                valueBuilder.append(HMAC_DELIMITER);
            }
        });
        valueBuilder.deleteCharAt(valueBuilder.length() - 1);
        hmacBuilder.append(valueBuilder.toString());
        return encryptionUtilityService.calculateBase64EncodedHmacSha256(hmacBuilder.toString(), hmacKey);
    }

    private Map<String, UrlParameter> generateUrlQueryParametersIncludingSignature(final AdyenRedirectUrlGenerationDto redirectUrlGenerationDto) {
        final SortedMap<String, UrlParameter> urlParameterMap = generateUrlQueryParameters(redirectUrlGenerationDto);
        // Calculate hmac
        final String hmacSignature = calculateHmacForRedirectParameters(redirectUrlGenerationDto.getSignatureHmacKey(), urlParameterMap);
        urlParameterMap.put(PARAMETER_SIGNATURE, new UrlParameter(PARAMETER_SIGNATURE, hmacSignature));
        return urlParameterMap;
    }

    private SortedMap<String, UrlParameter> generateUrlQueryParameters(final AdyenRedirectUrlGenerationDto redirectUrlGenerationDto) {
        final SortedMap<String, UrlParameter> urlParameterMap = new TreeMap<>();
        // Add parameters
        if (redirectUrlGenerationDto.getPaymentAmount() != null) {
            urlParameterMap.put(PARAMETER_PAYMENT_AMOUNT, new UrlParameter(PARAMETER_PAYMENT_AMOUNT, redirectUrlGenerationDto.getPaymentAmount().toString()));
        }
        if (redirectUrlGenerationDto.getCurrencyCode() != null) {
            urlParameterMap.put(PARAMETER_CURRENCY_CODE, new UrlParameter(PARAMETER_CURRENCY_CODE, redirectUrlGenerationDto.getCurrencyCode()));
        }
        if (redirectUrlGenerationDto.getMerchantReference() != null) {
            urlParameterMap.put(PARAMETER_MERCHANT_REFERENCE, new UrlParameter(PARAMETER_MERCHANT_REFERENCE, redirectUrlGenerationDto.getMerchantReference()));
        }
        if (redirectUrlGenerationDto.getSkinCode() != null) {
            urlParameterMap.put(PARAMETER_SKIN_CODE, new UrlParameter(PARAMETER_SKIN_CODE, redirectUrlGenerationDto.getSkinCode()));
        }
        if (redirectUrlGenerationDto.getMerchantAccount() != null) {
            urlParameterMap.put(PARAMETER_MERCHANT_ACCOUNT, new UrlParameter(PARAMETER_MERCHANT_ACCOUNT, redirectUrlGenerationDto.getMerchantAccount()));
        }
        if (redirectUrlGenerationDto.getShopperEmail() != null) {
            urlParameterMap.put(PARAMETER_SHOPPER_EMAIL, new UrlParameter(PARAMETER_SHOPPER_EMAIL, redirectUrlGenerationDto.getShopperEmail()));
        }
        if (redirectUrlGenerationDto.getShopperReference() != null) {
            urlParameterMap.put(PARAMETER_SHOPPER_REFERENCE, new UrlParameter(PARAMETER_SHOPPER_REFERENCE, redirectUrlGenerationDto.getShopperReference()));
        }
        if (redirectUrlGenerationDto.getRecurringContract() != null) {
            urlParameterMap.put(PARAMETER_RECURRING_CONTRACT, new UrlParameter(PARAMETER_RECURRING_CONTRACT, redirectUrlGenerationDto.getRecurringContract()));
        }
        if (redirectUrlGenerationDto.getCountryCode() != null) {
            urlParameterMap.put(PARAMETER_COUNTRY_CODE, new UrlParameter(PARAMETER_COUNTRY_CODE, redirectUrlGenerationDto.getCountryCode()));
        }
        if (redirectUrlGenerationDto.getShopperLocale() != null) {
            urlParameterMap.put(PARAMETER_SHOPPER_LOCALE, new UrlParameter(PARAMETER_SHOPPER_LOCALE, redirectUrlGenerationDto.getShopperLocale()));
        }
        if (redirectUrlGenerationDto.getShipBeforeDate() != null) {
            urlParameterMap.put(PARAMETER_SHIP_BEFORE_DATE, new UrlParameter(PARAMETER_SHIP_BEFORE_DATE, redirectUrlGenerationDto.getShipBeforeDate()));
        }
        if (redirectUrlGenerationDto.getSessionValidity() != null) {
            urlParameterMap.put(PARAMETER_SESSION_VALIDITY, new UrlParameter(PARAMETER_SESSION_VALIDITY, redirectUrlGenerationDto.getSessionValidity()));
        }
        if (redirectUrlGenerationDto.getMerchantReturnData() != null) {
            urlParameterMap.put(PARAMETER_MERCHANT_RETURN_DATA, new UrlParameter(PARAMETER_MERCHANT_RETURN_DATA, redirectUrlGenerationDto.getMerchantReturnData()));
        }
        if (redirectUrlGenerationDto.getBrandCode() != null) {
            urlParameterMap.put(PARAMETER_BRAND_CODE, new UrlParameter(PARAMETER_BRAND_CODE, redirectUrlGenerationDto.getBrandCode()));
        }
        return urlParameterMap;
    }

    private static String escapeMerchantSignatureValues(String val) {
        if (val == null) {
            return "";
        }
        return val.replace("\\", "\\\\").replace(":", "\\:");
    }
    /* Properties getters and setters */
    public PaymentService getPaymentService() {
        return paymentService;
    }

    public void setPaymentService(final PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public EncryptionUtilityService getEncryptionUtilityService() {
        return encryptionUtilityService;
    }

    public void setEncryptionUtilityService(final EncryptionUtilityService encryptionUtilityService) {
        this.encryptionUtilityService = encryptionUtilityService;
    }

    public AdyenPaymentSettingsService getAdyenPaymentSettingsService() {
        return adyenPaymentSettingsService;
    }

    public void setAdyenPaymentSettingsService(final AdyenPaymentSettingsService adyenPaymentSettingsService) {
        this.adyenPaymentSettingsService = adyenPaymentSettingsService;
    }

    public CustomerAdyenInformationService getCustomerAdyenInformationService() {
        return customerAdyenInformationService;
    }

    public void setCustomerAdyenInformationService(final CustomerAdyenInformationService customerAdyenInformationService) {
        this.customerAdyenInformationService = customerAdyenInformationService;
    }

    public CustomerService getCustomerService() {
        return customerService;
    }

    public void setCustomerService(final CustomerService customerService) {
        this.customerService = customerService;
    }

    /* Inner classes */
    private static class UrlParameter implements Serializable {

        private static final long serialVersionUID = -5735955746087368818L;

        /* Properties */
        private final String name;

        private final String value;

        /* Constructors */
        public UrlParameter(final String name, final String value) {
            this.name = name;
            this.value = value;
        }

        /* Properties getters and setters */
        public String getName() {
            return name;
        }

        public String getValue() {
            return value;
        }

        private String getEncodedValue() {
            try {
                return URLEncoder.encode(getValue(), "utf8");
            } catch (final UnsupportedEncodingException ex) {
                throw new ServicesRuntimeException(ex);
            }
        }

        /* Equals, HashCode and ToString */
        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof UrlParameter)) {
                return false;
            }
            final UrlParameter that = (UrlParameter) o;
            final EqualsBuilder builder = new EqualsBuilder();
            builder.appendSuper(super.equals(that));
            builder.append(this.getName(), that.getName());
            builder.append(this.getValue(), that.getValue());
            return builder.isEquals();
        }

        @Override
        public int hashCode() {
            final HashCodeBuilder builder = new HashCodeBuilder();
            builder.appendSuper(super.hashCode());
            builder.append(this.getName());
            builder.append(this.getValue());
            return builder.build();
        }


        @Override
        public String toString() {
            final ToStringBuilder builder = new ToStringBuilder(this);
            builder.appendSuper(super.toString());
            builder.append("name", this.getName());
            builder.append("value", this.getValue());
            return builder.build();
        }
    }
}
