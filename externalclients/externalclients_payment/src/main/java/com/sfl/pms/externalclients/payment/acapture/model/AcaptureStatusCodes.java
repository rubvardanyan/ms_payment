package com.sfl.pms.externalclients.payment.acapture.model;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 9/30/18
 * Time: 9:08 PM
 */
public enum AcaptureStatusCodes {
    CHECKOUT_SUCCESSFULLY_CREATED("000.200.100"),
    CHECKOUT_SUCCESSFULLY_UPDATE("000.200.101"),
    CHECKOUT_SUCCESSFULLY_DELETED("000.200.102");

    /* Properties */
    private final String code;

    /* Constructor */
    AcaptureStatusCodes(final String code) {
        this.code = code;
    }

    /* Getters */
    public String getCode() {
        return code;
    }
}
