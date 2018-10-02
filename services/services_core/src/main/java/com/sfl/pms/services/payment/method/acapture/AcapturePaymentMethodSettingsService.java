package com.sfl.pms.services.payment.method.acapture;

import com.sfl.pms.services.payment.method.AbstractPaymentMethodSettingsService;
import com.sfl.pms.services.payment.method.model.acapture.AcapturePaymentMethodSettings;
import com.sfl.pms.services.payment.method.model.acapture.AcapturePaymentMethodType;

import javax.annotation.Nonnull;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 9/28/18
 * Time: 9:53 PM
 */
public interface AcapturePaymentMethodSettingsService extends AbstractPaymentMethodSettingsService<AcapturePaymentMethodSettings> {

    /**
     * Gets Acapture payment method settings for the given payment method type and acapture payment settings
     * @param acapturePaymentMethodType payment method type
     * @param paymentSettingsId payment settings id
     * @return AcapturePaymentMethodSettings
     */
    @Nonnull
    AcapturePaymentMethodSettings getAcapturePaymentMethodSettingsByPaymentMethodTypeAndPaymentSettingsId(@Nonnull final AcapturePaymentMethodType acapturePaymentMethodType, @Nonnull final Long paymentSettingsId);

}
