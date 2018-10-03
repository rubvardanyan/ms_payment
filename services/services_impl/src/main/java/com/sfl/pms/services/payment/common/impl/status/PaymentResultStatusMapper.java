package com.sfl.pms.services.payment.common.impl.status;

import com.sfl.pms.externalclients.payment.adyen.model.datatypes.AdyenPaymentStatus;
import com.sfl.pms.services.payment.common.model.PaymentResultStatus;

/**
 * User: Ruben Dilanyan
 * Company: SFL LLC
 * Date: 7/13/15
 * Time: 2:06 PM
 */
public interface PaymentResultStatusMapper {

    /**
     * Maps Adyen payment status to payment result status
     *
     * @param adyenPaymentStatus
     * @return paymentResultStatus
     */
    PaymentResultStatus getPaymentResultStatusForAdyenPaymentStatus(final AdyenPaymentStatus adyenPaymentStatus);

    /**
     * Maps Acapture payment status check response result code to payment result status
     * @param acapturePaymentStatus
     * @return
     */
    PaymentResultStatus getPaymentResultStatusForAcapturePaymentStatus(final String acapturePaymentStatus);
}
