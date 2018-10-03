package com.sfl.pms.persistence.repositories.payment.settings.acapture;

import com.sfl.pms.persistence.repositories.payment.settings.AbstractPaymentProviderSettingsRepository;
import com.sfl.pms.services.payment.settings.model.acapture.AcapturePaymentSettings;
import org.springframework.stereotype.Repository;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 9/28/18
 * Time: 8:28 PM
 */
@Repository
public interface AcapturePaymentSettingsRepository extends AbstractPaymentProviderSettingsRepository<AcapturePaymentSettings> {
}
