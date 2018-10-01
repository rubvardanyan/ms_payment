package com.sfl.pms.services.payment.common.impl.status;

import com.sfl.pms.externalclients.payment.adyen.model.datatypes.AdyenPaymentStatus;
import com.sfl.pms.services.payment.common.model.PaymentResultStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * User: Ruben Dilanyan
 * Company: SFL LLC
 * Date: 7/13/15
 * Time: 2:06 PM
 */
@Component
public class PaymentResultStatusMapperImpl implements PaymentResultStatusMapper {

    private static final Map<AdyenPaymentStatus, PaymentResultStatus> ADYEN_TO_PAYMENT_STATUS_MAPPING;

    static {
        final Map<AdyenPaymentStatus, PaymentResultStatus> tempStatusMap = new HashMap<>();
        tempStatusMap.put(AdyenPaymentStatus.AUTHORISED, PaymentResultStatus.PAID);
        tempStatusMap.put(AdyenPaymentStatus.CANCELLED, PaymentResultStatus.CANCELLED);
        tempStatusMap.put(AdyenPaymentStatus.ERROR, PaymentResultStatus.ERROR);
        tempStatusMap.put(AdyenPaymentStatus.PENDING, PaymentResultStatus.PENDING);
        tempStatusMap.put(AdyenPaymentStatus.REFUSED, PaymentResultStatus.REFUSED);
        tempStatusMap.put(AdyenPaymentStatus.RECEIVED, PaymentResultStatus.RECEIVED);
        // Publish map
        ADYEN_TO_PAYMENT_STATUS_MAPPING = Collections.unmodifiableMap(tempStatusMap);
    }

    public PaymentResultStatusMapperImpl() {
    }

    @Override
    public PaymentResultStatus getPaymentResultStatusForAdyenPaymentStatus(@Nonnull final AdyenPaymentStatus adyenPaymentStatus) {
        Assert.notNull(adyenPaymentStatus, "Adyen payment status should not be null");
        return ADYEN_TO_PAYMENT_STATUS_MAPPING.get(adyenPaymentStatus);
    }

    @Override
    public PaymentResultStatus getPaymentResultStatusForAcapturePaymentStatus(final String acapturePaymentStatus) {
        //TODO: map status
        return PaymentResultStatus.PAID;
    }
}
