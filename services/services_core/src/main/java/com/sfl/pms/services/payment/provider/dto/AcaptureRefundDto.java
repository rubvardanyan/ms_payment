package com.sfl.pms.services.payment.provider.dto;

import com.sfl.pms.services.currency.model.Currency;
import com.sfl.pms.services.payment.method.model.acapture.AcapturePaymentMethodType;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 11/19/18
 * Time: 10:04 PM
 */
public class AcaptureRefundDto implements Serializable {

    /* Properties */
    private String checkoutId;

    private BigDecimal amount;

    private Currency currency;

    private AcapturePaymentMethodType paymentMethodType;
}
