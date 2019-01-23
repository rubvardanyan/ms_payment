package com.sfl.pms.services.payment.notification.impl.processors.acapture.json;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sfl.pms.externalclients.common.http.exception.ExternalClientRuntimeException;
import com.sfl.pms.services.payment.notification.impl.processors.acapture.json.model.AcaptureEncryptedNotificationJsonModel;
import com.sfl.pms.services.payment.notification.impl.processors.acapture.json.model.AcapturePaymentNotificationPayloadJsonModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.Nonnull;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 1/23/19
 * Time: 4:49 PM
 */
@Component
public class AcaptureNotificationJsonDeserializerImpl implements AcaptureNotificationJsonDeserializer {
    private static final Logger LOGGER  = LoggerFactory.getLogger(AcaptureNotificationJsonDeserializerImpl.class);

    /* Constructor */
    public AcaptureNotificationJsonDeserializerImpl() {
        LOGGER.debug("Initializing acapture notification json deserializer");
    }

    /* Public methods */
    @Nonnull
    @Override
    public AcaptureEncryptedNotificationJsonModel deserializeAcaptureEncryptedNotification(@Nonnull final String notificationJson) {
        Assert.notNull(notificationJson, "Notification json should not be null");
        LOGGER.debug("Deserializing acapture notification encrypted content - {}", notificationJson);
        final AcaptureEncryptedNotificationJsonModel notificationJsonModel = deserializeJson(notificationJson, AcaptureEncryptedNotificationJsonModel.class);
        LOGGER.debug("Acapture encrypted notification deserialization result is - {}, for json - {}", notificationJsonModel, notificationJson);
        return notificationJsonModel;
    }

    @Nonnull
    @Override
    public AcapturePaymentNotificationPayloadJsonModel deserializePaymentNotificationPayload(@Nonnull final String payload) {
        Assert.notNull(payload, "Notification payload should not be null");
        LOGGER.debug("Deserializing acapture notification decrypted payload - {}", payload);
        final AcapturePaymentNotificationPayloadJsonModel payloadJsonModel = deserializeJson(payload, AcapturePaymentNotificationPayloadJsonModel.class);
        LOGGER.debug("Acapture decrypted notification payload deserialization result is - {}, for json - {}", payloadJsonModel, payload);
        return payloadJsonModel;
    }

    /* Utility methods */
    private <T> T deserializeJson(@Nonnull final String jsonString, final Class<T> resultClass) {
        try {
            final ObjectMapper objectMapper = new ObjectMapper();
            final T result = objectMapper.readValue(jsonString, resultClass);
            LOGGER.debug("Successfully deSerialized jsonString - {} to object - {}", jsonString, result);
            return result;
        } catch (final Exception ex) {
            LOGGER.error("Error occurred while deSerializing JSON string - {}", jsonString, ex);
            throw new ExternalClientRuntimeException(ex);
        }
    }
}
