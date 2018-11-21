package com.sfl.pms.services.payment.processing.impl.adyen;

import com.sfl.pms.externalclients.payment.adyen.communicator.AdyenApiCommunicator;
import com.sfl.pms.externalclients.payment.adyen.model.datatypes.AdyenPaymentStatus;
import com.sfl.pms.externalclients.payment.adyen.model.datatypes.ContractType;
import com.sfl.pms.externalclients.payment.adyen.model.payment.AdyenPaymentResultModel;
import com.sfl.pms.externalclients.payment.adyen.model.payment.PaymentAmountModel;
import com.sfl.pms.externalclients.payment.adyen.model.recurring.AbstractRecurringContractDetailsModel;
import com.sfl.pms.externalclients.payment.adyen.model.recurring.RecurringContractBankDetailsModel;
import com.sfl.pms.externalclients.payment.adyen.model.recurring.RecurringContractCardDetailsModel;
import com.sfl.pms.externalclients.payment.adyen.model.request.DisableRecurringContractRequest;
import com.sfl.pms.externalclients.payment.adyen.model.request.EncryptedPaymentRequest;
import com.sfl.pms.externalclients.payment.adyen.model.request.ListRecurringContractDetailsRequest;
import com.sfl.pms.externalclients.payment.adyen.model.request.SubsequentRecurringPaymentRequest;
import com.sfl.pms.externalclients.payment.adyen.model.response.DisableRecurringContractResponse;
import com.sfl.pms.externalclients.payment.adyen.model.response.EncryptedPaymentResponse;
import com.sfl.pms.externalclients.payment.adyen.model.response.ListRecurringContractDetailsResponse;
import com.sfl.pms.externalclients.payment.adyen.model.response.SubsequentRecurringPaymentResponse;
import com.sfl.pms.externalclients.payment.adyen.model.shopper.ShopperDetailsModel;
import com.sfl.pms.persistence.utility.PersistenceUtilityService;
import com.sfl.pms.services.common.exception.ServicesRuntimeException;
import com.sfl.pms.services.country.model.CountryCode;
import com.sfl.pms.services.customer.CustomerService;
import com.sfl.pms.services.customer.model.Customer;
import com.sfl.pms.services.payment.common.PaymentService;
import com.sfl.pms.services.payment.common.auth.CustomerPaymentMethodAuthorizationPaymentService;
import com.sfl.pms.services.payment.common.dto.PaymentResultDto;
import com.sfl.pms.services.payment.common.dto.adyen.AdyenPaymentResultDto;
import com.sfl.pms.services.payment.common.model.Payment;
import com.sfl.pms.services.payment.common.model.PaymentResult;
import com.sfl.pms.services.payment.common.model.PaymentResultStatus;
import com.sfl.pms.services.payment.common.model.adyen.AdyenPaymentResult;
import com.sfl.pms.services.payment.common.model.channel.CustomerPaymentMethodProcessingChannel;
import com.sfl.pms.services.payment.common.model.channel.EncryptedPaymentMethodProcessingChannel;
import com.sfl.pms.services.payment.common.model.channel.PaymentProcessingChannel;
import com.sfl.pms.services.payment.customer.information.adyen.CustomerAdyenInformationService;
import com.sfl.pms.services.payment.customer.information.model.adyen.CustomerAdyenInformation;
import com.sfl.pms.services.payment.customer.method.adyen.CustomerPaymentMethodAdyenInformationService;
import com.sfl.pms.services.payment.customer.method.authorization.CustomerPaymentMethodAuthorizationRequestService;
import com.sfl.pms.services.payment.customer.method.dto.CustomerBankPaymentMethodDto;
import com.sfl.pms.services.payment.customer.method.dto.CustomerCardPaymentMethodDto;
import com.sfl.pms.services.payment.customer.method.dto.CustomerPaymentMethodDto;
import com.sfl.pms.services.payment.customer.method.dto.CustomerPaymentMethodProviderInformationDto;
import com.sfl.pms.services.payment.customer.method.dto.adyen.CustomerPaymentMethodAdyenInformationDto;
import com.sfl.pms.services.payment.customer.method.model.CustomerPaymentMethod;
import com.sfl.pms.services.payment.customer.method.model.CustomerPaymentMethodProviderInformation;
import com.sfl.pms.services.payment.customer.method.model.adyen.CustomerPaymentMethodAdyenInformation;
import com.sfl.pms.services.payment.method.model.PaymentMethodType;
import com.sfl.pms.services.payment.method.model.adyen.AdyenPaymentMethodType;
import com.sfl.pms.services.payment.processing.impl.PaymentProviderOperationsProcessor;
import com.sfl.pms.services.payment.provider.adyen.AdyenPaymentProviderIntegrationService;
import com.sfl.pms.services.payment.provider.dto.AdyenRedirectUrlGenerationDto;
import com.sfl.pms.services.payment.provider.model.adyen.AdyenRecurringContractType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * User: Ruben Dilanyan
 * Company: SFL LLC
 * Date: 1/13/15
 * Time: 6:59 PM
 */
@Component
@Qualifier("adyenPaymentOperationsProcessor")
public class AdyenPaymentOperationsProcessorImpl implements AdyenPaymentOperationsProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdyenPaymentOperationsProcessorImpl.class);

    /* Dependencies */
    @Autowired
    private AdyenApiCommunicator adyenApiCommunicator;

    @Autowired
    private CustomerAdyenInformationService customerAdyenInformationService;

    @Autowired
    private CustomerPaymentMethodAdyenInformationService customerPaymentMethodAdyenInformationService;

    @Autowired
    private CustomerPaymentMethodAuthorizationPaymentService customerPaymentMethodAuthorizationPaymentService;

    @Autowired
    private CustomerPaymentMethodAuthorizationRequestService customerPaymentMethodAuthorizationRequestService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PersistenceUtilityService persistenceUtilityService;

    @Autowired
    private AdyenPaymentProviderIntegrationService adyenPaymentProviderIntegrationService;

    /* Constructors */
    public AdyenPaymentOperationsProcessorImpl() {
        LOGGER.debug("Initializing Adyen payment operations processor");
    }

    @Nonnull
    @Override
    public PaymentResultDto<? extends PaymentResult> processPaymentUsingCustomerPaymentMethodChannel(@Nonnull final Long paymentId) {
        assertPaymentId(paymentId);
        LOGGER.debug("Processing payment with id - {}", paymentId);
        // Load data
        final Payment payment = paymentService.getPaymentById(paymentId);
        final PaymentProcessingChannel paymentProcessingChannel = persistenceUtilityService.initializeAndUnProxy(payment.getPaymentProcessingChannel());
        Assert.isInstanceOf(CustomerPaymentMethodProcessingChannel.class, paymentProcessingChannel, "Payment channel should have customer payment method processing channel");
        final CustomerPaymentMethodProcessingChannel customerPaymentMethodProcessingChannel = (CustomerPaymentMethodProcessingChannel) paymentProcessingChannel;
        final CustomerPaymentMethod customerPaymentMethod = customerPaymentMethodProcessingChannel.getCustomerPaymentMethod();
        final PaymentMethodType paymentMethodType = customerPaymentMethod.getPaymentMethodType();
        final AdyenPaymentMethodType adyenPaymentMethodType = paymentMethodType.getAdyenPaymentMethod();
        CustomerPaymentMethodProviderInformation paymentMethodProviderInformation = customerPaymentMethod.getProviderInformation();
        paymentMethodProviderInformation = persistenceUtilityService.initializeAndUnProxy(paymentMethodProviderInformation);
        Assert.isInstanceOf(CustomerPaymentMethodAdyenInformation.class, paymentMethodProviderInformation, "Customer payment method should be Adyen payment method");
        final CustomerPaymentMethodAdyenInformation paymentMethodAdyenInformation = (CustomerPaymentMethodAdyenInformation) paymentMethodProviderInformation;
        // Prepare request data
        final ShopperDetailsModel shopperDetailsModel = createShopperDetailForCustomer(payment);
        final PaymentAmountModel paymentAmountModel = new PaymentAmountModel(payment.getCurrency().getCode(), payment.getPaymentTotalAmount());
        final SubsequentRecurringPaymentRequest subsequentRecurringPaymentRequest = new SubsequentRecurringPaymentRequest(payment.getUuId(), paymentMethodAdyenInformation.getRecurringDetailReference(), shopperDetailsModel, paymentAmountModel, adyenPaymentMethodType.getRecurringPaymentBrand());
        // Perform request
        final SubsequentRecurringPaymentResponse subsequentRecurringPaymentResponse = adyenApiCommunicator.submitSubsequentPayment(subsequentRecurringPaymentRequest);
        final AdyenPaymentResultModel paymentResultModel = subsequentRecurringPaymentResponse.getPaymentResult();
        // Create payment result DTO
        final AdyenPaymentResultDto adyenPaymentResultDto = createAdyenPaymentResultDto(paymentResultModel);
        LOGGER.debug("Created Adyen payment result DTO when processing order payment with id - {}. Payment result DTO - {}", payment.getId(), adyenPaymentResultDto);
        return adyenPaymentResultDto;
    }

    @Nonnull
    @Override
    public PaymentResultDto<? extends PaymentResult> processPaymentUsingEncryptedPaymentMethodChannel(@Nonnull final Long paymentId) {
        assertPaymentId(paymentId);
        LOGGER.debug("Processing payment with id - {}", paymentId);
        // Load request and payment
        final Payment payment = paymentService.getPaymentById(paymentId);
        final PaymentProcessingChannel paymentProcessingChannel = persistenceUtilityService.initializeAndUnProxy(payment.getPaymentProcessingChannel());
        Assert.isInstanceOf(EncryptedPaymentMethodProcessingChannel.class, paymentProcessingChannel, "Payment processing channel should have type of EncryptedPaymentMethodProcessingChannel");
        final EncryptedPaymentMethodProcessingChannel encryptedPaymentMethodProcessingChannel = (EncryptedPaymentMethodProcessingChannel) paymentProcessingChannel;
        // Prepare request data
        final ShopperDetailsModel shopperDetailsModel = createShopperDetailForCustomer(payment);
        final PaymentAmountModel paymentAmountModel = new PaymentAmountModel(payment.getCurrency().getCode(), payment.getPaymentTotalAmount());
        final EncryptedPaymentRequest encryptedPaymentRequest = new EncryptedPaymentRequest(payment.getUuId(), shopperDetailsModel, paymentAmountModel, encryptedPaymentMethodProcessingChannel.getEncryptedData(), payment.isStorePaymentMethod());
        // Execute call
        final EncryptedPaymentResponse recurringContractResponse = adyenApiCommunicator.submitPaymentUsingEncryptedPaymentMethod(encryptedPaymentRequest);
        final AdyenPaymentResultModel paymentResultModel = recurringContractResponse.getPaymentResult();
        // Create payment result DTO
        final AdyenPaymentResultDto adyenPaymentResultDto = createAdyenPaymentResultDto(paymentResultModel);
        LOGGER.debug("Created Adyen payment result DTO when processing payment method authorization payment with id - {}. Payment result DTO - {}", payment.getId(), adyenPaymentResultDto);
        return adyenPaymentResultDto;
    }

    @Override
    @Nonnull
    public List<PaymentProviderOperationsProcessor.CustomerPaymentMethodProviderData> getStoredRecurringPaymentMethods(@Nonnull final Long customerId) {
        Assert.notNull(customerId, "Customer id should not be null");
        final Customer customer = customerService.getCustomerById(customerId);
        // Prepare request data
        final CustomerAdyenInformation customerAdyenInformation = customerAdyenInformationService.getOrCreateCustomerPaymentProviderInformation(customer.getId());
        final ListRecurringContractDetailsRequest recurringContractDetailsRequest = new ListRecurringContractDetailsRequest(customerAdyenInformation.getShopperReference(), ContractType.RECURRING);
        // This is not correct way of association, but for now we do not have any other option
        final List<AbstractRecurringContractDetailsModel> adyenRecurringContracts = listAdyenRecurringPaymentMethodContracts(recurringContractDetailsRequest);
        // Create list of results
        final List<PaymentProviderOperationsProcessor.CustomerPaymentMethodProviderData> resultList = new ArrayList<>();
        adyenRecurringContracts.forEach(providerData -> {
            final PaymentProviderOperationsProcessor.CustomerPaymentMethodProviderData customerPaymentMethodProviderData = createCustomerPaymentMethodProviderData(providerData);
            LOGGER.debug("Created DTOs without authorization payment and request, payment method DTOs creation result - {}.", new Object[]{customerPaymentMethodProviderData});
            resultList.add(customerPaymentMethodProviderData);
        });
        return resultList;
    }


    @Override
    public PaymentProviderOperationsProcessor.CustomerPaymentMethodRemovalData processCustomerPaymentMethodRemoval(@Nonnull final Long paymentMethodProviderInformationId) {
        Assert.notNull(paymentMethodProviderInformationId, "Payment method provider information id should not be null");
        /* Retrieve customer payment method adyen information */
        final CustomerPaymentMethodAdyenInformation customerPaymentMethodInformation = customerPaymentMethodAdyenInformationService.getCustomerPaymentMethodProviderInformationById(paymentMethodProviderInformationId);
        LOGGER.debug("Successfully retrieved customer payment method adyen information - {}", customerPaymentMethodInformation);
        /* Retrieve adyen shopper details for the customer */
        final Customer customer = customerPaymentMethodInformation.getCustomerPaymentMethod().getCustomer();
        final CustomerAdyenInformation customerAdyenInformation = customerAdyenInformationService.getOrCreateCustomerPaymentProviderInformation(customer.getId());
        /* Create external api communicator request model for disabling recurring contract */
        final DisableRecurringContractRequest disableRecurringContractRequest = new DisableRecurringContractRequest(customerAdyenInformation.getShopperReference(), customerPaymentMethodInformation.getRecurringDetailReference());
        /* Perform request to adyen for disabling recurring contract using adyen api communicator */
        LOGGER.debug("Disabling recurring contract for shopper reference - {} and contract - {}", disableRecurringContractRequest.getShopperReference(), disableRecurringContractRequest.getContractReference());
        final DisableRecurringContractResponse disableRecurringContractResponse = adyenApiCommunicator.disableRecurringContract(disableRecurringContractRequest);
        LOGGER.debug("Successfully processed customer payment method removal request: response - {}", disableRecurringContractResponse);
        return new PaymentProviderOperationsProcessor.CustomerPaymentMethodRemovalData(disableRecurringContractResponse.getDisableContractResult().getStatus());
    }

    @Nonnull
    @Override
    public boolean checkIfPaymentResultAlreadyExists(@Nonnull final Payment payment, @Nonnull final PaymentResultDto<? extends PaymentResult> paymentResultDto) {
        assertPaymentNotNull(payment);
        Assert.notNull(paymentResultDto, "Payment result DTO should not be null");
        Assert.isInstanceOf(AdyenPaymentResultDto.class, paymentResultDto, "Payment result DTO should be type of - AdyenPaymentResultDto");
        final AdyenPaymentResultDto adyenPaymentResultDto = (AdyenPaymentResultDto) paymentResultDto;
        LOGGER.debug("Checking if payment result DTO - {} already exists for payment - {}", paymentResultDto, payment);
        boolean exists = false;
        final Set<PaymentResult> paymentResults = payment.getPaymentResults();
        for (PaymentResult paymentResult : paymentResults) {
            paymentResult = persistenceUtilityService.initializeAndUnProxy(paymentResult);
            if (paymentResult instanceof AdyenPaymentResult) {
                // Check if payment result has same status, authCode, pspReference and result code
                final AdyenPaymentResult adyenPaymentResult = (AdyenPaymentResult) paymentResult;
                // Check PSP reference
                if (!checkIfStringsAreEqual(adyenPaymentResultDto.getPspReference(), adyenPaymentResult.getPspReference())) {
                    continue;
                }
                // Check auth code
                if (!checkIfStringsAreEqual(adyenPaymentResultDto.getAuthCode(), adyenPaymentResult.getAuthCode())) {
                    continue;
                }
                // Check result code
                if (!checkIfStringsAreEqual(adyenPaymentResultDto.getResultCode(), adyenPaymentResult.getResultCode())) {
                    continue;
                }
                // Check status
                if (adyenPaymentResultDto.getStatus() != adyenPaymentResult.getStatus()) {
                    continue;
                }
                exists = true;
                break;
            }
        }
        return exists;
    }

    @Nonnull
    @Override
    public String generatePaymentRedirectUrl(@Nonnull Long paymentId, @Nonnull boolean createRecurringContract) {
        assertPaymentId(paymentId);
        final AdyenRecurringContractType adyenRecurringContractType = createRecurringContract ? AdyenRecurringContractType.RECURRING : AdyenRecurringContractType.NONE;
        final AdyenRedirectUrlGenerationDto urlGenerationDto = adyenPaymentProviderIntegrationService.createAdyenRedirectGenerationDtoForPayment(paymentId, adyenRecurringContractType);
        final String redirectUrl = adyenPaymentProviderIntegrationService.generatePaymentProviderRedirectUrlForDto(urlGenerationDto);
        LOGGER.debug("Generated Adyen redirect URL for payment with id - {} , submitPaymentUsingEncryptedPaymentMethod - {}", paymentId, createRecurringContract);
        return redirectUrl;
    }

    @Nonnull
    @Override
    public void refundPayment(@Nonnull final Long paymentId) {
        LOGGER.error("Payment refund is not supported by adyen payment provider");
        throw new UnsupportedOperationException("Payment refund is not supported by adyen payment provider");
    }

    /* Utility methods */
    private void assertPaymentNotNull(final Payment payment) {
        Assert.notNull(payment, "Payment should not be null");
    }

    private boolean checkIfStringsAreEqual(final String firstValue, final String secondValue) {
        if (firstValue == null && secondValue == null) {
            return true;
        }
        if (firstValue != null) {
            return firstValue.equals(secondValue);
        }
        return false;
    }

    private ShopperDetailsModel createShopperDetailForCustomer(final Payment payment) {
        final Long customerId = payment.getCustomer().getId();
        final CustomerAdyenInformation customerAdyenInformation = customerAdyenInformationService.getOrCreateCustomerPaymentProviderInformation(customerId);
        final ShopperDetailsModel shopperDetailsModel = new ShopperDetailsModel(customerAdyenInformation.getShopperEmail(), customerAdyenInformation.getShopperReference(), payment.getClientIpAddress());
        LOGGER.debug("Creates Adyen shopper detail reference for customer with id - {}, shopper detail - {}", customerId, shopperDetailsModel);
        return shopperDetailsModel;
    }

    private PaymentProviderOperationsProcessor.CustomerPaymentMethodProviderData createCustomerPaymentMethodProviderData(final AbstractRecurringContractDetailsModel contract) {
        // Determine payment method type of contract
        final PaymentMethodType paymentMethodType = determinePaymentMethodType(contract);
        // Customer card payment DTO
        final CustomerPaymentMethodDto<? extends CustomerPaymentMethod> customerPaymentMethodDto = createCustomerPaymentMethodDto(contract, paymentMethodType);
        // Create Adyen payment information
        final CustomerPaymentMethodProviderInformationDto<? extends CustomerPaymentMethodProviderInformation> customerPaymentMethodProviderInformationDto = new CustomerPaymentMethodAdyenInformationDto(contract.getDetailReference());
        // Return result
        return new PaymentProviderOperationsProcessor.CustomerPaymentMethodProviderData(customerPaymentMethodDto, customerPaymentMethodProviderInformationDto);
    }

    private CustomerPaymentMethodDto<? extends CustomerPaymentMethod> createCustomerPaymentMethodDto(final AbstractRecurringContractDetailsModel contract, final PaymentMethodType paymentMethodType) {
        final CustomerPaymentMethodDto<? extends CustomerPaymentMethod> customerPaymentMethodDto;
        // Customer card payment DTO
        if (contract instanceof RecurringContractCardDetailsModel) {
            final RecurringContractCardDetailsModel cardDetailsModel = (RecurringContractCardDetailsModel) contract;
            final CustomerCardPaymentMethodDto customerCardPaymentMethodDto = new CustomerCardPaymentMethodDto();
            customerCardPaymentMethodDto.setExpiryMonth(cardDetailsModel.getExpiryMonth());
            customerCardPaymentMethodDto.setExpiryYear(cardDetailsModel.getExpiryYear());
            customerCardPaymentMethodDto.setHolderName(cardDetailsModel.getCardHolderName());
            customerCardPaymentMethodDto.setNumberTail(cardDetailsModel.getCardNumber());
            customerCardPaymentMethodDto.setPaymentMethodType(paymentMethodType);
            customerPaymentMethodDto = customerCardPaymentMethodDto;
        } else if (contract instanceof RecurringContractBankDetailsModel) {
            final RecurringContractBankDetailsModel bankDetailsModel = (RecurringContractBankDetailsModel) contract;
            final CustomerBankPaymentMethodDto customerBankPaymentMethodDto = new CustomerBankPaymentMethodDto();
            customerBankPaymentMethodDto.setBankName(bankDetailsModel.getBankName());
            customerBankPaymentMethodDto.setCountryCode(CountryCode.valueOf(bankDetailsModel.getCountryCode()));
            customerBankPaymentMethodDto.setIban(bankDetailsModel.getIban());
            customerBankPaymentMethodDto.setBic(bankDetailsModel.getBic());
            customerBankPaymentMethodDto.setOwnerName(bankDetailsModel.getOwnerName());
            customerBankPaymentMethodDto.setBankAccountNumber(bankDetailsModel.getBankAccountNumber());
            customerBankPaymentMethodDto.setPaymentMethodType(paymentMethodType);
            customerPaymentMethodDto = customerBankPaymentMethodDto;
        } else {
            throw new ServicesRuntimeException("Unknown Adyen recurring contract model type - " + contract);
        }
        return customerPaymentMethodDto;
    }

    private AdyenPaymentResultDto createAdyenPaymentResultDto(final AdyenPaymentResultModel paymentResultModel) {
        // Create payment result DTO
        final AdyenPaymentResultDto adyenPaymentResult = new AdyenPaymentResultDto();
        // Set properties
        adyenPaymentResult.setAuthCode(paymentResultModel.getAuthCode());
        adyenPaymentResult.setPspReference(paymentResultModel.getPspReference());
        adyenPaymentResult.setResultCode(paymentResultModel.getResultCode().getResult());
        adyenPaymentResult.setStatus(getPaymentResultStatusForAdyenStatus(paymentResultModel.getResultCode()));
        return adyenPaymentResult;
    }

    private PaymentResultStatus getPaymentResultStatusForAdyenStatus(final AdyenPaymentStatus adyenPaymentStatus) {
        final PaymentResultStatus paymentResultStatus;
        switch (adyenPaymentStatus) {
            case AUTHORISED: {
                paymentResultStatus = PaymentResultStatus.PAID;
                break;
            }
            case CANCELLED: {
                paymentResultStatus = PaymentResultStatus.CANCELLED;
                break;
            }
            case ERROR: {
                paymentResultStatus = PaymentResultStatus.FAILED;
                break;
            }
            case PENDING: {
                paymentResultStatus = PaymentResultStatus.PENDING;
                break;
            }
            case REFUSED: {
                paymentResultStatus = PaymentResultStatus.REFUSED;
                break;
            }
            case RECEIVED: {
                paymentResultStatus = PaymentResultStatus.RECEIVED;
                break;
            }
            default: {
                paymentResultStatus = PaymentResultStatus.FAILED;
                break;
            }
        }
        return paymentResultStatus;
    }

    private void assertPaymentId(final Long paymentId) {
        Assert.notNull(paymentId, "Payment id should not be null");
    }

    private PaymentMethodType determinePaymentMethodType(final AbstractRecurringContractDetailsModel contract) {
        // Grab Adyen payment method for code
        final AdyenPaymentMethodType adyenPaymentMethodType = AdyenPaymentMethodType.getAdyenPaymentMethodTypeForCode(contract.getVariant());
        return PaymentMethodType.getPaymentMethodTypeForAdyenPaymentMethod(adyenPaymentMethodType);
    }

    private List<AbstractRecurringContractDetailsModel> listAdyenRecurringPaymentMethodContracts(final ListRecurringContractDetailsRequest recurringContractDetailsRequest) {
        // This is not correct way of association, but for now we do not have any other option
        final ListRecurringContractDetailsResponse recurringContractDetailsResponse = adyenApiCommunicator.listRecurringDetails(recurringContractDetailsRequest);
        return recurringContractDetailsResponse.getRecurringContracts();
    }

    /* Properties getters and setters */
    public AdyenApiCommunicator getAdyenApiCommunicator() {
        return adyenApiCommunicator;
    }

    public void setAdyenApiCommunicator(final AdyenApiCommunicator adyenApiCommunicator) {
        this.adyenApiCommunicator = adyenApiCommunicator;
    }

    public CustomerAdyenInformationService getCustomerAdyenInformationService() {
        return customerAdyenInformationService;
    }

    public void setCustomerAdyenInformationService(final CustomerAdyenInformationService customerAdyenInformationService) {
        this.customerAdyenInformationService = customerAdyenInformationService;
    }

    public CustomerPaymentMethodAdyenInformationService getCustomerPaymentMethodAdyenInformationService() {
        return customerPaymentMethodAdyenInformationService;
    }

    public void setCustomerPaymentMethodAdyenInformationService(final CustomerPaymentMethodAdyenInformationService customerPaymentMethodAdyenInformationService) {
        this.customerPaymentMethodAdyenInformationService = customerPaymentMethodAdyenInformationService;
    }

    public CustomerPaymentMethodAuthorizationPaymentService getCustomerPaymentMethodAuthorizationPaymentService() {
        return customerPaymentMethodAuthorizationPaymentService;
    }

    public void setCustomerPaymentMethodAuthorizationPaymentService(final CustomerPaymentMethodAuthorizationPaymentService customerPaymentMethodAuthorizationPaymentService) {
        this.customerPaymentMethodAuthorizationPaymentService = customerPaymentMethodAuthorizationPaymentService;
    }

    public CustomerPaymentMethodAuthorizationRequestService getCustomerPaymentMethodAuthorizationRequestService() {
        return customerPaymentMethodAuthorizationRequestService;
    }

    public void setCustomerPaymentMethodAuthorizationRequestService(final CustomerPaymentMethodAuthorizationRequestService customerPaymentMethodAuthorizationRequestService) {
        this.customerPaymentMethodAuthorizationRequestService = customerPaymentMethodAuthorizationRequestService;
    }

    public PaymentService getPaymentService() {
        return paymentService;
    }

    public void setPaymentService(final PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public PersistenceUtilityService getPersistenceUtilityService() {
        return persistenceUtilityService;
    }

    public void setPersistenceUtilityService(final PersistenceUtilityService persistenceUtilityService) {
        this.persistenceUtilityService = persistenceUtilityService;
    }

    public AdyenPaymentProviderIntegrationService getAdyenPaymentProviderIntegrationService() {
        return adyenPaymentProviderIntegrationService;
    }

    public void setAdyenPaymentProviderIntegrationService(final AdyenPaymentProviderIntegrationService adyenPaymentProviderIntegrationService) {
        this.adyenPaymentProviderIntegrationService = adyenPaymentProviderIntegrationService;
    }
}
