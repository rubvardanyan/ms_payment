package com.sfl.pms.services.payment.notification.impl.processors.acapture.json;

import com.sfl.pms.services.payment.notification.impl.processors.acapture.json.model.AcaptureEncryptedNotificationJsonModel;
import com.sfl.pms.services.payment.notification.impl.processors.acapture.json.model.AcapturePaymentNotificationPayloadJsonModel;

import javax.annotation.Nonnull;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 1/23/19
 * Time: 4:48 PM
 */
public interface AcaptureNotificationJsonDeserializer {

    /**
     * Deserialize acapture encrypted notification into JSON model
     * @param notificationJson notification json
     * @return AcaptureEncryptedNotificationJsonModel
     */
    @Nonnull
    AcaptureEncryptedNotificationJsonModel deserializeAcaptureEncryptedNotification(@Nonnull final String notificationJson);

    /**
     * Deserialize acapture decrypted notification content payload into JSON model
     * @param payload payload
     * @return AcapturePaymentNotificationPayloadJsonModel
     */
    @Nonnull
    AcapturePaymentNotificationPayloadJsonModel deserializePaymentNotificationPayload(@Nonnull final String payload);
}
