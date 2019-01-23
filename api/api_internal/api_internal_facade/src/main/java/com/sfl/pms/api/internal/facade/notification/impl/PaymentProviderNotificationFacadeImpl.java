package com.sfl.pms.api.internal.facade.notification.impl;

import com.sfl.pms.api.internal.facade.notification.PaymentProviderNotificationFacade;
import com.sfl.pms.api.internal.facade.notification.exception.InvalidPaymentProviderNotificationsTokenException;
import com.sfl.pms.core.api.internal.model.common.result.ErrorResponseModel;
import com.sfl.pms.core.api.internal.model.common.result.ResultResponseModel;
import com.sfl.pms.core.api.internal.model.notification.request.CreateAcapturePaymentProviderNotificationRequest;
import com.sfl.pms.core.api.internal.model.notification.request.CreateAdyenPaymentProviderNotificationRequest;
import com.sfl.pms.core.api.internal.model.notification.response.CreatePaymentProviderNotificationResponse;
import com.sfl.pms.services.payment.notification.PaymentProviderNotificationRequestService;
import com.sfl.pms.services.payment.notification.dto.PaymentProviderNotificationRequestDto;
import com.sfl.pms.services.payment.notification.event.StartPaymentProviderNotificationRequestProcessingEvent;
import com.sfl.pms.services.payment.notification.impl.processors.acapture.decrypt.AcaptureNotificationDecryptor;
import com.sfl.pms.services.payment.notification.impl.processors.acapture.json.AcaptureNotificationJsonDeserializer;
import com.sfl.pms.services.payment.notification.impl.processors.acapture.json.model.AcaptureEncryptedNotificationJsonModel;
import com.sfl.pms.services.payment.notification.model.PaymentProviderNotificationRequest;
import com.sfl.pms.services.payment.provider.model.PaymentProviderType;
import com.sfl.pms.services.payment.settings.PaymentProviderSettingsService;
import com.sfl.pms.services.payment.settings.model.PaymentProviderSettings;
import com.sfl.pms.services.system.event.ApplicationEventDistributionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * User: Ruben Dilanyan
 * Company: SFL LLC
 * Date: 4/27/15
 * Time: 12:19 PM
 */
@Component
public class PaymentProviderNotificationFacadeImpl implements PaymentProviderNotificationFacade {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentProviderNotificationFacadeImpl.class);


    /* Dependencies */
    @Autowired
    private PaymentProviderNotificationRequestService paymentProviderNotificationRequestService;

    @Autowired
    private AcaptureNotificationJsonDeserializer acaptureNotificationJsonDeserializer;

    @Autowired
    private AcaptureNotificationDecryptor acaptureNotificationDecryptor;

    @Autowired
    private PaymentProviderSettingsService paymentProviderSettingsService;

    @Autowired
    private ApplicationEventDistributionService applicationEventDistributionService;

    /* Constructors */
    public PaymentProviderNotificationFacadeImpl() {
        LOGGER.debug("Initializing payment provider notification service facade");
    }


    @Override
    public ResultResponseModel<CreatePaymentProviderNotificationResponse> createAdyenPaymentProviderNotificationRequest(@Nonnull final CreateAdyenPaymentProviderNotificationRequest request) {
        Assert.notNull(request, "Request should not be null");
        // Validate
        final List<ErrorResponseModel> errors = request.validateRequiredFields();
        if (errors.size() > 0) {
            return new ResultResponseModel<>(errors);
        }
        // Grab data
        final String rawContent = request.getRawContent();
        final String notificationsToken = request.getNotificationsToken();
        final PaymentProviderType paymentProviderType = PaymentProviderType.valueOf(request.getPaymentProviderType().name());
        final String clientIpAddress = request.getClientIpAddress();
        LOGGER.debug("Creating new payment provider notification request, raw content - {}, notification token - {} ,payment provider type - {}, client IP address - {}", rawContent, notificationsToken, paymentProviderType, clientIpAddress);
        // Assert payment provider notifications token
        assertNotificationsToken(paymentProviderType, notificationsToken);

        // Create notification request DTO
        final PaymentProviderNotificationRequestDto requestDto = new PaymentProviderNotificationRequestDto(paymentProviderType, rawContent, clientIpAddress);
        // Create payment provider notification request
        final PaymentProviderNotificationRequest notificationRequest = paymentProviderNotificationRequestService.createPaymentProviderNotificationRequest(requestDto);
        // Publish event
        applicationEventDistributionService.publishAsynchronousEvent(new StartPaymentProviderNotificationRequestProcessingEvent(notificationRequest.getId()));
        // Create response
        final CreatePaymentProviderNotificationResponse response = new CreatePaymentProviderNotificationResponse(notificationRequest.getUuId());
        LOGGER.debug("Successfully created payment provider notification request for DTO  - {}, request - {}, response - {}", requestDto, request, response);
        return new ResultResponseModel<>(response);
    }

    @Override
    public ResultResponseModel<CreatePaymentProviderNotificationResponse> createAcapturePaymentProviderNotificationRequest(@Nonnull final CreateAcapturePaymentProviderNotificationRequest request) {
        Assert.notNull(request, "Request should not be null");
        final List<ErrorResponseModel> errors = request.validateRequiredFields();
        if(errors.size() > 0) {
            return new ResultResponseModel<>(errors);
        }
        // Grab data
        final String notificationsToken = request.getNotificationsToken();
        final PaymentProviderType paymentProviderType = PaymentProviderType.valueOf(request.getPaymentProviderType().name());
        final String clientIpAddress = request.getClientIpAddress();
        final String rawContent = request.getRawContent();
        LOGGER.debug("Creating new payment provider notification request, raw content - {}, notification token - {} ,payment provider type - {}, client IP address - {}", rawContent, notificationsToken, paymentProviderType, clientIpAddress);
        // Assert payment provider notifications token
        assertNotificationsToken(paymentProviderType, notificationsToken);
        final AcaptureEncryptedNotificationJsonModel notificationJsonModel = acaptureNotificationJsonDeserializer.deserializeAcaptureEncryptedNotification(rawContent);
        final String payload = notificationJsonModel.getPayload();
        Assert.notNull(payload, "Acapture notification content's payload should not be null");
        final String notificationRawContent = acaptureNotificationDecryptor.decryptNotificationPayload(payload, request.getAuthenticationTag(), request.getInitializationVector());
        final PaymentProviderNotificationRequestDto requestDto = new PaymentProviderNotificationRequestDto(paymentProviderType, notificationRawContent, clientIpAddress);
        // Create payment provider notification request
        final PaymentProviderNotificationRequest notificationRequest = paymentProviderNotificationRequestService.createPaymentProviderNotificationRequest(requestDto);
        // Publish event
        applicationEventDistributionService.publishAsynchronousEvent(new StartPaymentProviderNotificationRequestProcessingEvent(notificationRequest.getId()));
        // Create response
        final CreatePaymentProviderNotificationResponse response = new CreatePaymentProviderNotificationResponse(notificationRequest.getUuId());
        LOGGER.debug("Successfully created payment provider notification request for DTO  - {}, request - {}, response - {}", requestDto, request, response);
        return new ResultResponseModel<>(response);
    }

    /* Utility methods */
    private void assertNotificationsToken(final PaymentProviderType paymentProviderType, final String notificationsToken) {
        final PaymentProviderSettings settings = paymentProviderSettingsService.getActivePaymentProviderSettingsForType(paymentProviderType);
        final String expectedNotificationsToken = settings.getNotificationsToken();
        if (!expectedNotificationsToken.equals(notificationsToken)) {
            LOGGER.debug("Received invalid notifications token for payment provider - {}, received token - {}, expected token - {}", paymentProviderType, notificationsToken, expectedNotificationsToken);
            throw new InvalidPaymentProviderNotificationsTokenException(paymentProviderType, notificationsToken, expectedNotificationsToken);
        }
    }

}
