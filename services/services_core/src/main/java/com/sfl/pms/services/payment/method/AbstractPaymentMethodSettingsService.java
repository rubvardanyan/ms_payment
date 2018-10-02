package com.sfl.pms.services.payment.method;

import com.sfl.pms.services.payment.method.model.PaymentMethodSettings;

import javax.annotation.Nonnull;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 9/28/18
 * Time: 9:13 PM
 */
public interface AbstractPaymentMethodSettingsService<T extends PaymentMethodSettings> {


    /**
     * Gets payment method setting for the given id
     * @param paymentMethodSettingsId payment method settings id
     * @return PaymentMethodSettings
     */
    T getPaymentMethodSettingsById(@Nonnull final Long paymentMethodSettingsId);

}
