package com.sfl.pms.services.order.model;

/**
 * User: Ruben Dilanyan
 * Company: SFL LLC
 * Date: 11/12/14
 * Time: 7:10 PM
 */
public enum OrderState {
    CREATED,
    PAYMENT_PROCESSING_STARTED,
    PAYMENT_FAILED,
    PAID,
    REFUNDED,
    REFUND_FAILED
}
