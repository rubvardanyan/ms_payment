package com.sfl.pms.services.payment.common.exception.order;

import com.sfl.pms.services.common.exception.EntityNotFoundForUuIdException;
import com.sfl.pms.services.payment.common.model.order.request.OrderRefundRequest;

/**
 * User: Ruben Dilanyan
 * Company: SFL LLC
 * Date: 2/4/15
 * Time: 1:20 PM
 */
public class OrderRefundRequestNotFoundForUuIdException extends EntityNotFoundForUuIdException {
    private static final long serialVersionUID = 6214135604450195873L;

    /* Constructors */
    public OrderRefundRequestNotFoundForUuIdException(final String uuId) {
        super(uuId, OrderRefundRequest.class);
    }
}
