package com.sfl.pms.services.payment.provider.impl.acapture;

import com.sfl.pms.externalclients.payment.acapture.communicator.AcaptureApiCommunicator;
import com.sfl.pms.externalclients.payment.acapture.model.AcaptureStatusCodes;
import com.sfl.pms.externalclients.payment.acapture.model.authentication.AcaptureAuthenticationModel;
import com.sfl.pms.externalclients.payment.acapture.model.payment.AcaptureAmountModel;
import com.sfl.pms.externalclients.payment.acapture.model.payment.PaymentType;
import com.sfl.pms.externalclients.payment.acapture.model.request.CheckPaymentStatusRequest;
import com.sfl.pms.externalclients.payment.acapture.model.request.CreateCheckoutRequest;
import com.sfl.pms.externalclients.payment.acapture.model.response.CheckPaymentStatusResponse;
import com.sfl.pms.externalclients.payment.acapture.model.response.CreateCheckoutResponse;
import com.sfl.pms.externalclients.payment.acapture.model.result.AcaptureResultModel;
import com.sfl.pms.services.common.exception.ServicesRuntimeException;
import com.sfl.pms.services.payment.common.PaymentService;
import com.sfl.pms.services.payment.common.dto.acapture.AcapturePaymentResultDto;
import com.sfl.pms.services.payment.common.model.Payment;
import com.sfl.pms.services.payment.common.model.acapture.AcapturePaymentProviderMetadata;
import com.sfl.pms.services.payment.common.model.channel.PaymentProcessingChannel;
import com.sfl.pms.services.payment.common.model.channel.ProvidedPaymentMethodProcessingChannel;
import com.sfl.pms.services.payment.common.model.metadata.PaymentProviderMetadata;
import com.sfl.pms.services.payment.method.acapture.AcapturePaymentMethodSettingsService;
import com.sfl.pms.services.payment.method.model.PaymentMethodType;
import com.sfl.pms.services.payment.method.model.acapture.AcapturePaymentMethodSettings;
import com.sfl.pms.services.payment.method.model.acapture.AcapturePaymentMethodType;
import com.sfl.pms.services.payment.redirect.model.acapture.AcaptureRedirectResult;
import com.sfl.pms.services.payment.settings.acapture.AcapturePaymentSettingsService;
import com.sfl.pms.services.payment.settings.model.acapture.AcapturePaymentSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Nonnull;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 9/30/18
 * Time: 7:38 PM
 */
@Service
public class AcapturePaymentProviderIntegrationServiceImpl implements AcapturePaymentProviderIntegrationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AcapturePaymentProviderIntegrationServiceImpl.class);

    /* Dependencies */
    @Autowired
    private PaymentService paymentService;

    @Autowired
    private AcapturePaymentMethodSettingsService paymentMethodSettingsService;

    @Autowired
    private AcapturePaymentSettingsService acapturePaymentSettingsService;

    @Autowired
    private AcaptureApiCommunicator acaptureApiCommunicator;


    /* Constructor */
    public AcapturePaymentProviderIntegrationServiceImpl() {
        LOGGER.debug("Initializing acapture payment provider integration service");
    }

    /* Public methods */
    @Nonnull
    @Override
    public String createCheckout(@Nonnull final Long paymentId) {
        Assert.notNull(paymentId, "Payment id should not be null");
        final Payment payment = paymentService.getPaymentById(paymentId);
        final PaymentProcessingChannel paymentProcessingChannel = payment.getPaymentProcessingChannel();
        Assert.isInstanceOf(ProvidedPaymentMethodProcessingChannel.class, paymentProcessingChannel, "Payment processing channel should be of type ProvidedPaymentMethodProcessingChannel");
        final ProvidedPaymentMethodProcessingChannel providedPaymentMethodProcessingChannel = (ProvidedPaymentMethodProcessingChannel) paymentProcessingChannel;
        final AcapturePaymentMethodType acapturePaymentMethodType = providedPaymentMethodProcessingChannel.getPaymentMethodType().getAcapturePaymentMethodType();
        Assert.notNull(acapturePaymentMethodType, "Acapture payment method should not be null");
        final AcapturePaymentSettings paymentSettings = acapturePaymentSettingsService.getActivePaymentSettings();
        final AcapturePaymentMethodSettings paymentMethodSettings = paymentMethodSettingsService.getAcapturePaymentMethodSettingsByPaymentMethodTypeAndPaymentSettingsId(acapturePaymentMethodType, paymentSettings.getId());
        final CreateCheckoutRequest createCheckoutRequest = new CreateCheckoutRequest();
        createCheckoutRequest.setPaymentType(PaymentType.PRE_AUTHORIZATION);
        createCheckoutRequest.setAmountModel(new AcaptureAmountModel(payment.getCurrency().getCode(), payment.getAmount()));
        createCheckoutRequest.setAuthenticationModel(new AcaptureAuthenticationModel(paymentMethodSettings.getAuthorizationId()));
        createCheckoutRequest.setPaymentUuid(payment.getUuId());
        final CreateCheckoutResponse checkoutResponse = acaptureApiCommunicator.createCheckout(createCheckoutRequest);
        if(!AcaptureStatusCodes.CHECKOUT_SUCCESSFULLY_CREATED.getCode().equals(checkoutResponse.getResult().getCode())) {
            LOGGER.error("Not success status code received during checkout creation with request - {}, response - {}", createCheckoutRequest, checkoutResponse);
            throw new ServicesRuntimeException("Not success status code received during checkout creation with request - " + createCheckoutRequest + " response - " + checkoutResponse);
        }
        return checkoutResponse.getCheckoutId();
    }

    @Nonnull
    @Override
    public AcapturePaymentResultDto checkPaymentStatusForRedirectResult(@Nonnull final AcaptureRedirectResult acaptureRedirectResult) {
        Assert.notNull(acaptureRedirectResult, "Acapture redirect result should not be null");
        final PaymentMethodType paymentMethod = acaptureRedirectResult.getPayment().getPaymentProcessingChannel().getPaymentMethodTypeIfDefined();
        Assert.notNull(paymentMethod, "Payment method should not be null");
        final AcapturePaymentMethodType acapturePaymentMethod = paymentMethod.getAcapturePaymentMethodType();
        Assert.notNull(acapturePaymentMethod);
        final AcapturePaymentSettings paymentSettings = acapturePaymentSettingsService.getActivePaymentSettings();
        final AcapturePaymentMethodSettings paymentMethodSettings = paymentMethodSettingsService.getAcapturePaymentMethodSettingsByPaymentMethodTypeAndPaymentSettingsId(acapturePaymentMethod, paymentSettings.getId());
        final CheckPaymentStatusRequest request = new CheckPaymentStatusRequest(acaptureRedirectResult.getResourcePath(), new AcaptureAuthenticationModel(paymentMethodSettings.getAuthorizationId()));
        final CheckPaymentStatusResponse checkPaymentStatusResponse = acaptureApiCommunicator.checkPaymentStatus(request);
        assertPaymentUuidForStatusCheckResponse(acaptureRedirectResult, checkPaymentStatusResponse);
        return new AcapturePaymentResultDto(checkPaymentStatusResponse.getResult().getCode(), checkPaymentStatusResponse.getResult().getDescription());
    }

    /* Utility methods */
    private void assertPaymentUuidForStatusCheckResponse(final AcaptureRedirectResult redirectResult, final CheckPaymentStatusResponse response) {
        if(!redirectResult.getPayment().getUuId().equals(response.getMerchantInvoiceId())) {
            LOGGER.error("Wrong payment uuid retrieved from status check response - {}", response);
            throw new ServicesRuntimeException("Wrong payment uuid retrieved from status check response - " + response);
        }
    }
}
