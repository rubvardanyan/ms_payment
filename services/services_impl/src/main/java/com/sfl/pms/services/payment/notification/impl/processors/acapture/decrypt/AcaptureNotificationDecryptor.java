package com.sfl.pms.services.payment.notification.impl.processors.acapture.decrypt;

import javax.annotation.Nonnull;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 1/23/19
 * Time: 4:47 PM
 */
public interface AcaptureNotificationDecryptor {

    /**
     * Decrypts acapture notification's payload
     * @param payload payload
     * @param authenticationTag authenticationTag
     * @param initializationVector initializationVector
     * @return decrypted payload
     */
    @Nonnull
    String decryptNotificationPayload(@Nonnull final String payload, @Nonnull final String authenticationTag, @Nonnull final String initializationVector);
}
