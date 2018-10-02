package com.sfl.pms.services.payment.settings.acapture;

import com.sfl.pms.services.payment.settings.AbstractPaymentProviderSettingsService;
import com.sfl.pms.services.payment.settings.model.acapture.AcapturePaymentSettings;

import javax.annotation.Nonnull;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 9/28/18
 * Time: 8:17 PM
 */
public interface AcapturePaymentSettingsService extends AbstractPaymentProviderSettingsService<AcapturePaymentSettings> {

    /**
     * Gets Acapture payment settings for current environment
     * @return AcapturePaymentSettings
     */
    @Nonnull
    AcapturePaymentSettings getActivePaymentSettings();
}
