package com.sfl.pms.persistence.repositories.payment.notification.acapture;

import com.sfl.pms.persistence.repositories.payment.notification.AbstractPaymentProviderNotificationRepository;
import com.sfl.pms.services.payment.notification.model.acapture.AcapturePaymentProviderNotification;
import org.springframework.stereotype.Repository;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 1/23/19
 * Time: 9:18 PM
 */
@Repository
public interface AcapturePaymentProviderNotificationRepository extends AbstractPaymentProviderNotificationRepository<AcapturePaymentProviderNotification> {
}
