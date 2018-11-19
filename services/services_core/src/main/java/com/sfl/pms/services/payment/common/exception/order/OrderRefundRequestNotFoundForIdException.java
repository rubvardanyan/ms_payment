package com.sfl.pms.services.payment.common.exception.order;

import com.sfl.pms.services.common.exception.EntityNotFoundForIdException;
import com.sfl.pms.services.payment.common.model.order.request.OrderRefundRequest;

/**
 * User: Ruben Dilanyan
 * Company: SFL LLC
 * Date: 2/4/15
 * Time: 1:19 PM
 */
public class OrderRefundRequestNotFoundForIdException extends EntityNotFoundForIdException {
    private static final long serialVersionUID = 9187084672631637644L;

    /* Constructors */
    public OrderRefundRequestNotFoundForIdException(final Long id) {
        super(id, OrderRefundRequest.class);
    }
}
