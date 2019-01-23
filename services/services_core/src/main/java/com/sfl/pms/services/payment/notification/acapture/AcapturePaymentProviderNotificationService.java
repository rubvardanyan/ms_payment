package com.sfl.pms.services.payment.notification.acapture;

import com.sfl.pms.services.payment.notification.AbstractPaymentProviderNotificationService;
import com.sfl.pms.services.payment.notification.dto.acapture.AcapturePaymentProviderNotificationDto;
import com.sfl.pms.services.payment.notification.model.acapture.AcapturePaymentProviderNotification;

import javax.annotation.Nonnull;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 1/23/19
 * Time: 8:49 PM
 */
public interface AcapturePaymentProviderNotificationService extends AbstractPaymentProviderNotificationService<AcapturePaymentProviderNotification> {

    /**
     * Creates new Acapture payment provider notification for provided DTO
     *
     * @param notificationRequestId
     * @param notificationDto
     * @return acapturePaymentProviderNotification
     */
    @Nonnull
    AcapturePaymentProviderNotification createPaymentProviderNotification(@Nonnull final Long notificationRequestId, @Nonnull final AcapturePaymentProviderNotificationDto notificationDto);
}
