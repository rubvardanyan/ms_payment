package com.sfl.pms.services.payment.common.model.acapture;

import com.sfl.pms.services.payment.common.model.PaymentResult;
import com.sfl.pms.services.payment.provider.model.PaymentProviderType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 10/1/18
 * Time: 3:50 AM
 */

@Entity
@DiscriminatorValue(value = "ACAPTURE")
@Table(name = "payment_acapture_result")
public class AcapturePaymentResult extends PaymentResult {
    private static final long serialVersionUID = -244746602249759993L;

    /* Properties */
    @Column(name = "result_code", nullable = false)
    private String resultCode;

    @Column(name = "result_description", nullable = false)
    private String resultDescription;

    @Column(name = "payment_reference")
    private String paymentReference;

    /* Constructor */
    public AcapturePaymentResult() {
        setType(PaymentProviderType.ACAPTURE);
    }

    /* Properties getters and setters */
    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(final String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultDescription() {
        return resultDescription;
    }

    public void setResultDescription(final String resultDescription) {
        this.resultDescription = resultDescription;
    }

    public String getPaymentReference() {
        return paymentReference;
    }

    public void setPaymentReference(final String paymentReference) {
        this.paymentReference = paymentReference;
    }

    /* Equals, HashCode and ToString */
    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        final AcapturePaymentResult that = (AcapturePaymentResult) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(resultCode, that.resultCode)
                .append(resultDescription, that.resultDescription)
                .append(paymentReference, that.paymentReference)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(resultCode)
                .append(resultDescription)
                .append(paymentReference)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .appendSuper(super.toString())
                .append("resultCode", resultCode)
                .append("resultDescription", resultDescription)
                .append("paymentReference", paymentReference)
                .toString();
    }
}
