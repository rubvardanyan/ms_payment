package com.sfl.pms.externalclients.payment.acapture.model.payment;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 9/30/18
 * Time: 8:08 PM
 */
public enum PaymentType {
    PRE_AUTHORIZATION("PA"),
    DEBIT("DB"),
    CREDIT("CD"),
    CAPTURE("CP"),
    REVERSAL("RV"),
    REFUND("RF");

    /* Properties */
    private final String code;

    /* Constructor */
    PaymentType(final String code) {
        this.code = code;
    }

    /* Properties getters and setters */
    public String getCode() {
        return code;
    }
}
