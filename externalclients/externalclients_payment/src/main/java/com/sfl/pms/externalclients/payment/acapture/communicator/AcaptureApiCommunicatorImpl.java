package com.sfl.pms.externalclients.payment.acapture.communicator;

import com.sfl.pms.externalclients.common.http.rest.RestClient;
import com.sfl.pms.externalclients.payment.acapture.model.AcaptureApiPaths;
import com.sfl.pms.externalclients.payment.acapture.model.AcaptureAttributeMappings;
import com.sfl.pms.externalclients.payment.acapture.model.authentication.AcaptureAuthenticationModel;
import com.sfl.pms.externalclients.payment.acapture.model.payment.AcaptureAmountModel;
import com.sfl.pms.externalclients.payment.acapture.model.request.CheckPaymentStatusRequest;
import com.sfl.pms.externalclients.payment.acapture.model.request.CreateCheckoutRequest;
import com.sfl.pms.externalclients.payment.acapture.model.response.CheckPaymentStatusResponse;
import com.sfl.pms.externalclients.payment.acapture.model.response.CreateCheckoutResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import javax.annotation.Nonnull;
import java.text.DecimalFormat;
import java.util.Collections;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 9/30/18
 * Time: 7:45 PM
 */
@Component
public class AcaptureApiCommunicatorImpl implements AcaptureApiCommunicator {
    private static final Logger LOGGER = LoggerFactory.getLogger(AcaptureApiCommunicatorImpl.class);

    /* Constants */
    private static final DecimalFormat AMOUNT_FORMATTER = new DecimalFormat("0.00");

    /* Dependencies */
    @Autowired
    private RestClient restClient;

    @Value("#{ appProperties['acapture.ws.url']}")
    private String url;

    @Value("#{ appProperties['acapture.ws.authentication.userId']}")
    private String userId;

    @Value("#{ appProperties['acapture.ws.authentication.password']}")
    private String password;

    /* Constructor */
    public AcaptureApiCommunicatorImpl() {
        LOGGER.debug("Initializing acapture api communicator");
    }

    /* Public methods */
    @Nonnull
    @Override
    public CreateCheckoutResponse createCheckout(@Nonnull final CreateCheckoutRequest request) {
        assertCreateCheckoutRequest(request);
        LOGGER.debug("Submitting checkout request with model - {}", request);
        final MultiValueMap<String, String> valueMap = getValueMapWithAuthorizationValues(request.getAuthenticationModel());
        valueMap.add(AcaptureAttributeMappings.AMOUNT, AMOUNT_FORMATTER.format(request.getAmountModel().getAmount()));
        valueMap.add(AcaptureAttributeMappings.CURRENCY, request.getAmountModel().getCurrency());
        valueMap.add(AcaptureAttributeMappings.PAYMENT_TYPE, request.getPaymentType().getCode());
        valueMap.add(AcaptureAttributeMappings.MERCHANT_INVOICE_ID, request.getPaymentUuid());
        final HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(valueMap, getDefaultHeaders());
        final ResponseEntity<CreateCheckoutResponse> responseEntity = restClient.postForEntity(url + AcaptureApiPaths.CHECKOUTS, entity, CreateCheckoutResponse.class);
        if(responseEntity.getStatusCode() != HttpStatus.OK) {
            LOGGER.error("Invalid response status returned - {}", responseEntity);
            throw new IllegalStateException("Invalid response status returned - " + responseEntity);
        }
        LOGGER.debug("Successfully processed checkout request, retrieved response - {}", responseEntity);
        return responseEntity.getBody();
    }

    @Nonnull
    @Override
    public CheckPaymentStatusResponse checkPaymentStatus(@Nonnull final CheckPaymentStatusRequest request) {
        assertCheckPaymentStatusRequest(request);
        LOGGER.debug("Submitting check payment status request with model - {}", request);
        final MultiValueMap<String, String> valueMap = getValueMapWithAuthorizationValues(request.getAuthenticationModel());
        final HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(valueMap, getDefaultHeaders());
        final String requestUrl = url +
                request.getResourcePath() +
                "?" +
                AcaptureAttributeMappings.AUTHENTICATION_USER_ID +
                "=" +
                userId +
                "&" +
                AcaptureAttributeMappings.AUTHENTICATION_PASSWORD +
                "=" +
                password +
                "&" +
                AcaptureAttributeMappings.AUTHENTICATION_ENTITY_ID +
                "=" +
                request.getAuthenticationModel().getEntityId();
        final ResponseEntity<CheckPaymentStatusResponse> responseEntity = restClient.getForEntity(requestUrl, CheckPaymentStatusResponse.class);
        if(responseEntity.getStatusCode() != HttpStatus.OK) {
            LOGGER.error("Invalid response status returned - {}", responseEntity);
            throw new IllegalStateException("Invalid response status returned - " + responseEntity);
        }
        return responseEntity.getBody();
    }

    /* Utility methods */
    private void assertCreateCheckoutRequest(final CreateCheckoutRequest request) {
        Assert.notNull(request, "Checkout request should not be null");
        Assert.notNull(request.getPaymentType(), "Payment type should not be null in checkout request");
        assertAuthenticationModel(request.getAuthenticationModel());
        assertAmountModel(request.getAmountModel());
    }

    private void assertCheckPaymentStatusRequest(final CheckPaymentStatusRequest request) {
        Assert.notNull(request, "Check payment status request should not be null");
        Assert.notNull(request.getResourcePath(), "Resource path in check payment status request should not be null ");
        assertAuthenticationModel(request.getAuthenticationModel());
    }

    private void assertAuthenticationModel(final AcaptureAuthenticationModel authenticationModel) {
        Assert.notNull(authenticationModel, "Authentication model should not be null");
        Assert.notNull(authenticationModel.getEntityId(), "Entity id in authentication model should not be null");
    }

    private void assertAmountModel(final AcaptureAmountModel amountModel) {
        Assert.notNull(amountModel, "Amount model should not be null");
        Assert.notNull(amountModel.getCurrency(), "Currency in amount model should not be null");
        Assert.notNull(amountModel.getAmount(), "Amount in amount model should not be null");
    }

    private MultiValueMap<String, String> getValueMapWithAuthorizationValues(final AcaptureAuthenticationModel authenticationModel) {
        final MultiValueMap<String, String> valueMap = new LinkedMultiValueMap<>();
        valueMap.add(AcaptureAttributeMappings.AUTHENTICATION_USER_ID, userId);
        valueMap.add(AcaptureAttributeMappings.AUTHENTICATION_PASSWORD, password);
        valueMap.add(AcaptureAttributeMappings.AUTHENTICATION_ENTITY_ID, authenticationModel.getEntityId());
        return valueMap;
    }

    private static HttpHeaders getDefaultHeaders() {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        return headers;
    }
}
