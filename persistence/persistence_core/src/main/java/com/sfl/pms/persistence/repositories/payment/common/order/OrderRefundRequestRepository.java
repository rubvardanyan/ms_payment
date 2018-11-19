package com.sfl.pms.persistence.repositories.payment.common.order;

import com.sfl.pms.services.payment.common.model.order.request.OrderRefundRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * User: Ruben Dilanyan
 * Company: SFL LLC
 * Date: 2/4/15
 * Time: 1:21 PM
 */
@Repository
public interface OrderRefundRequestRepository extends JpaRepository<OrderRefundRequest, Long> {

    /**
     * Gets order refund request by UUID
     *
     * @param uuid
     * @return OrderRefundRequest
     */
    OrderRefundRequest findByUuId(final String uuid);
}
