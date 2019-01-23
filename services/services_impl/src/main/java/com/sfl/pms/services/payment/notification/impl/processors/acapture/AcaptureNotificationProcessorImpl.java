package com.sfl.pms.services.payment.notification.impl.processors.acapture;

import com.sfl.pms.services.payment.notification.acapture.AcapturePaymentProviderNotificationService;
import com.sfl.pms.services.payment.notification.dto.acapture.AcapturePaymentProviderNotificationDto;
import com.sfl.pms.services.payment.notification.impl.processors.acapture.json.AcaptureNotificationJsonDeserializer;
import com.sfl.pms.services.payment.notification.impl.processors.acapture.json.model.AcapturePaymentNotificationPayloadJsonModel;
import com.sfl.pms.services.payment.notification.model.PaymentProviderNotification;
import com.sfl.pms.services.payment.notification.model.PaymentProviderNotificationRequest;
import com.sfl.pms.services.payment.notification.model.acapture.AcapturePaymentProviderNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 1/23/19
 * Time: 7:47 PM
 */
@Component("acaptureNotificationProcessor")
public class AcaptureNotificationProcessorImpl implements AcaptureNotificationProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(AcaptureNotificationProcessorImpl.class);

    /* Dependencies */
    @Autowired
    private AcaptureNotificationJsonDeserializer acaptureNotificationJsonDeserializer;

    @Autowired
    private AcapturePaymentProviderNotificationService acapturePaymentProviderNotificationService;

    /* Constructor */
    public AcaptureNotificationProcessorImpl() {
        LOGGER.debug("Initializing acapture notification processor");
    }

    /* Public methods */
    @Nonnull
    @Override
    public List<PaymentProviderNotification> createPaymentProviderNotificationForRequest(@Nonnull final PaymentProviderNotificationRequest notificationRequest) {
        Assert.notNull(notificationRequest);
        final String rawContent = notificationRequest.getRawContent();
        final AcapturePaymentNotificationPayloadJsonModel notificationPayload = acaptureNotificationJsonDeserializer.deserializePaymentNotificationPayload(rawContent);
        final AcapturePaymentProviderNotificationDto notificationDto = new AcapturePaymentProviderNotificationDto(
                rawContent,
                notificationRequest.getClientIpAddress(),
                notificationPayload.getId(),
                notificationPayload.getResult().getCode(),
                notificationPayload.getResult().getDescription(),
                notificationPayload.getBuildNumber(),
                notificationPayload.getNdc()
        );
        final AcapturePaymentProviderNotification notification = acapturePaymentProviderNotificationService.createPaymentProviderNotification(notificationRequest.getId(), notificationDto);
        LOGGER.debug("Created Acapture payment provider notification, request  - {}, notificationJsonModel - {}, requestContent - {}", notificationRequest, notificationPayload);
        final List<PaymentProviderNotification> notifications = new ArrayList<>();
        notifications.add(notification);
        return notifications;
    }

    @Override
    public void processPaymentProviderNotification(@Nonnull final PaymentProviderNotification notification) {

    }
}
