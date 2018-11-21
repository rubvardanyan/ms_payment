package com.sfl.pms.services.payment.method.model.acapture;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 9/28/18
 * Time: 9:58 PM
 */
public enum  AcapturePaymentMethodType {
    MASTER_CARD("MASTER", true),
    VISA("VISA", true),
    IDEAL("IDEAL", false),
    GIROPAY("GIROPAY", false);

    /* Properties */
    private final String code;

    private final boolean captureRequired;

    /* Constructor */
    AcapturePaymentMethodType(final String code, final boolean captureRequired) {
        this.code = code;
        this.captureRequired = captureRequired;
    }

    /* Properties getters */
    public String getCode() {
        return code;
    }

    public boolean isCaptureRequired() {
        return captureRequired;
    }
}
