package com.sfl.pms.services.payment.method.exception;

import com.sfl.pms.services.common.exception.EntityNotFoundForIdException;
import com.sfl.pms.services.payment.method.model.PaymentMethodSettings;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 9/28/18
 * Time: 9:32 PM
 */
public class PaymentMethodSettingsNotFoundForIdException  extends EntityNotFoundForIdException {
    private static final long serialVersionUID = -2337417208517773571L;

    /* Constructors */
    public PaymentMethodSettingsNotFoundForIdException(final Long id, final Class<? extends PaymentMethodSettings> paymentMethodDefinitionClass) {
        super(id, paymentMethodDefinitionClass);
    }
}