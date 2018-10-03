package com.sfl.pms.services.payment.method.model;

import com.sfl.pms.services.payment.method.model.acapture.AcapturePaymentMethodType;
import com.sfl.pms.services.payment.method.model.adyen.AdyenPaymentMethodType;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * User: Ruben Dilanyan
 * Company: SFL LLC
 * Date: 12/23/14
 * Time: 10:48 AM
 */
public enum PaymentMethodType {
    AMERICAN_EXPRESS(PaymentMethodGroupType.CARD, AdyenPaymentMethodType.AMERICAN_EXPRESS, null),
    MAESTRO(PaymentMethodGroupType.CARD, AdyenPaymentMethodType.MAESTRO, null),
    MASTER_CARD(PaymentMethodGroupType.CARD, AdyenPaymentMethodType.MASTER_CARD, AcapturePaymentMethodType.MASTER_CARD),
    VISA(PaymentMethodGroupType.CARD, AdyenPaymentMethodType.VISA, AcapturePaymentMethodType.VISA),
    MISTER_CASH(PaymentMethodGroupType.CARD, AdyenPaymentMethodType.MISTER_CASH, null),
    DINERS_CLUB(PaymentMethodGroupType.CARD, AdyenPaymentMethodType.DINERS_CLUB, null),
    DISCOVER(PaymentMethodGroupType.CARD, AdyenPaymentMethodType.DISCOVER, null),
    IDEAL(PaymentMethodGroupType.BANK_TRANSFER, AdyenPaymentMethodType.IDEAL, AcapturePaymentMethodType.IDEAL),
    PAYPAL(PaymentMethodGroupType.BANK_TRANSFER, null, AcapturePaymentMethodType.PAYPAL),
    SEPA_DIRECT_DEBIT(PaymentMethodGroupType.BANK_TRANSFER, AdyenPaymentMethodType.SEPA_DIRECT_DEBIT, null);

    /* Static mapping of Adyen payment method type with payment method */
    private static final Map<AdyenPaymentMethodType, PaymentMethodType> ADYEN_TO_ENUM_MAPPING;
    private static final Map<AcapturePaymentMethodType, PaymentMethodType> ACAPTURE_TO_ENUM_MAPPING;

    static {
        final Map<AdyenPaymentMethodType, PaymentMethodType> adyenTempMapping = new HashMap<>();
        final Map<AcapturePaymentMethodType, PaymentMethodType> acaptureTempMapping = new HashMap<>();
        // Loop through values and create mapping
        for (final PaymentMethodType paymentMethodType : PaymentMethodType.values()) {
            if (paymentMethodType.getAdyenPaymentMethod() != null) {
                adyenTempMapping.put(paymentMethodType.getAdyenPaymentMethod(), paymentMethodType);
            }
            if(paymentMethodType.getAcapturePaymentMethodType() != null) {
                acaptureTempMapping.put(paymentMethodType.getAcapturePaymentMethodType(), paymentMethodType);
            }
        }
        // Publish map
        ADYEN_TO_ENUM_MAPPING = Collections.unmodifiableMap(adyenTempMapping);
        ACAPTURE_TO_ENUM_MAPPING = Collections.unmodifiableMap(acaptureTempMapping);
    }

    /* Properties */
    private final PaymentMethodGroupType group;

    private final AdyenPaymentMethodType adyenPaymentMethod;

    private final AcapturePaymentMethodType acapturePaymentMethodType;

    /* Constructor */
    PaymentMethodType(final PaymentMethodGroupType group, final AdyenPaymentMethodType adyenPaymentMethod, final AcapturePaymentMethodType acapturePaymentMethodType) {
        this.group = group;
        this.adyenPaymentMethod = adyenPaymentMethod;
        this.acapturePaymentMethodType = acapturePaymentMethodType;
    }

    /* Properties getters and setters */
    public PaymentMethodGroupType getGroup() {
        return group;
    }

    public AdyenPaymentMethodType getAdyenPaymentMethod() {
        return adyenPaymentMethod;
    }

    public AcapturePaymentMethodType getAcapturePaymentMethodType() {
        return acapturePaymentMethodType;
    }

    /* Utility methods */
    public static PaymentMethodType getPaymentMethodTypeForAdyenPaymentMethod(final AdyenPaymentMethodType adyenPaymentMethodType) {
        Assert.notNull(adyenPaymentMethodType, "Adyen payment method type should not be null");
        final PaymentMethodType paymentMethodType = ADYEN_TO_ENUM_MAPPING.get(adyenPaymentMethodType);
        if (paymentMethodType == null) {
            throw new IllegalArgumentException("Unknown Adyen payment method type - " + adyenPaymentMethodType);
        }
        return paymentMethodType;
    }

    public static PaymentMethodType getPaymentMethodTypeForAcapturePaymentMethod(final AcapturePaymentMethodType acapturePaymentMethodType) {
        Assert.notNull(acapturePaymentMethodType, "Acapture payment method type should not be null");
        final PaymentMethodType paymentMethodType = ACAPTURE_TO_ENUM_MAPPING.get(acapturePaymentMethodType);
        if (paymentMethodType == null) {
            throw new IllegalArgumentException("Unknown Acapture payment method type - " + acapturePaymentMethodType);
        }
        return paymentMethodType;
    }

}
