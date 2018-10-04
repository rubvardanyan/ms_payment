package com.sfl.pms.services.payment.method.model.acapture;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 9/28/18
 * Time: 9:58 PM
 */
public enum  AcapturePaymentMethodType {
    MASTER_CARD("MASTER"),
    VISA("VISA"),
    IDEAL("IDEAL"),
    GIROPAY("GIROPAY");

    /* Properties */
    private final String code;

    /* Constructor */
    AcapturePaymentMethodType(String code) {
        this.code = code;
    }

    /* Properties getters */
    public String getCode() {
        return code;
    }
}
