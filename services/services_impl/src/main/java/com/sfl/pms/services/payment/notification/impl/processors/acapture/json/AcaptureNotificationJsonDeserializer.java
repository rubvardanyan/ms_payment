package com.sfl.pms.services.payment.notification.impl.processors.acapture.json;

import com.sfl.pms.services.payment.notification.impl.processors.acapture.json.model.AcaptureEncryptedNotificationJsonModel;

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
}
