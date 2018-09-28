package com.sfl.pms.persistence.repositories.payment.method.acapture;

import com.sfl.pms.persistence.repositories.payment.method.AbstractPaymentMethodSettingsRepository;
import com.sfl.pms.services.payment.method.model.PaymentMethodType;
import com.sfl.pms.services.payment.method.model.acapture.AcapturePaymentMethodSettings;

import javax.annotation.Nonnull;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 9/28/18
 * Time: 9:24 PM
 */
public interface AcapturePaymentMethodSettingsRepository extends AbstractPaymentMethodSettingsRepository<AcapturePaymentMethodSettings> {

    AcapturePaymentMethodSettings findByPaymentMethodTypeAndPaymentSettingsId(@Nonnull final PaymentMethodType paymentMethodType, @Nonnull Long paymentSettingsId);
}
