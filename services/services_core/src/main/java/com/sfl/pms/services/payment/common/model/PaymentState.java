package com.sfl.pms.services.payment.common.model;

/**
 * User: Ruben Dilanyan
 * Company: SFL LLC
 * Date: 1/11/15
 * Time: 11:17 AM
 */
public enum PaymentState {
    CREATED(false, true),
    STARTED_PROCESSING(false, true),
    GENERATED_REDIRECT_URL(false, true),
    FAILED(false, false),
    REFUSED(false, false),
    PAID(true, false),
    PENDING(true, true),
    CANCELLED(false, false),
    RECEIVED(true, true),
    REFUNDED(false,false),
    REFUND_FAILED(false,false);

    private final boolean interpretAsPaid;

    private final boolean isTemporaryState;

    /* Constructors */
    private PaymentState(final boolean interpretAsPaid, final boolean isTemporaryState) {
        this.interpretAsPaid = interpretAsPaid;
        this.isTemporaryState = isTemporaryState;
    }

    /* Properties getters and setters */
    public boolean isInterpretAsPaid() {
        return interpretAsPaid;
    }

    public boolean isTemporaryState() {
        return isTemporaryState;
    }
}
