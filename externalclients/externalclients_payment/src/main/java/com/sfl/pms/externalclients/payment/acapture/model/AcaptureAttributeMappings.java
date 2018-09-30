package com.sfl.pms.externalclients.payment.acapture.model;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 9/30/18
 * Time: 7:59 PM
 */
public final class AcaptureAttributeMappings {

    /* Authentication */
    private static final String AUTHENTICATION_PREFIX = "authentication.";
    public static final String AUTHENTICATION_USER_ID = AUTHENTICATION_PREFIX + "userId";
    public static final String AUTHENTICATION_PASSWORD = AUTHENTICATION_PREFIX + "password";
    public static final String AUTHENTICATION_ENTITY_ID = AUTHENTICATION_PREFIX + "entityId";

    /* Payment */
    public static final String AMOUNT = "amount";
    public static final String CURRENCY = "currency";
    public static final String PAYMENT_TYPE = "paymentType";


    /* Private constructor to prevent instantiation */
    private AcaptureAttributeMappings() {

    }
}
