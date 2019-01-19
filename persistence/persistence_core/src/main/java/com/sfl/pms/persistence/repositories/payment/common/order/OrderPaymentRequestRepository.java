package com.sfl.pms.persistence.repositories.payment.common.order;

import com.sfl.pms.services.payment.common.model.order.request.OrderPaymentRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * User: Ruben Dilanyan
 * Company: SFL LLC
 * Date: 2/4/15
 * Time: 1:21 PM
 */
@Repository
public interface OrderPaymentRequestRepository extends JpaRepository<OrderPaymentRequest, Long>, OrderPaymentRequestRepositoryCustom {

    /**
     * Gets order payment request by UUID
     *
     * @param uuId
     * @return orderPaymentRequest
     */
    OrderPaymentRequest findByUuId(final String uuId);

    /**
     * Retrieve order payment request by payment id
     * @param paymentId
     * @return OrderPaymentRequest
     */
    OrderPaymentRequest findByOrderPaymentId(final Long paymentId);
}
