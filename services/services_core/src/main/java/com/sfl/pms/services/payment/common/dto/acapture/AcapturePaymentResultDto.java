package com.sfl.pms.services.payment.common.dto.acapture;

import com.sfl.pms.services.payment.common.dto.PaymentResultDto;
import com.sfl.pms.services.payment.common.model.acapture.AcapturePaymentResult;
import com.sfl.pms.services.payment.provider.model.PaymentProviderType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 10/1/18
 * Time: 3:53 AM
 */
public class AcapturePaymentResultDto extends PaymentResultDto<AcapturePaymentResult> {
    private static final long serialVersionUID = -196496247069612092L;

    /* Properties */
    private String resultCode;

    private String resultDescriptions;

    private String paymentReference;

    /* Constructor */
    public AcapturePaymentResultDto() {
        super(PaymentProviderType.ACAPTURE);
    }

    public AcapturePaymentResultDto(final String resultCode, final String resultDescriptions, final String paymentReference) {
        super(PaymentProviderType.ACAPTURE);
        this.resultCode = resultCode;
        this.resultDescriptions = resultDescriptions;
        this.paymentReference = paymentReference;
    }

    /* Override methods */
    @Override
    public void updateDomainEntityProperties(final AcapturePaymentResult paymentResult) {
        super.updateDomainEntityProperties(paymentResult);
        paymentResult.setResultCode(getResultCode());
        paymentResult.setResultDescription(getResultDescriptions());
        paymentResult.setPaymentReference(getPaymentReference());
    }

    /* Properties getters and setters */
    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(final String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultDescriptions() {
        return resultDescriptions;
    }

    public void setResultDescriptions(final String resultDescriptions) {
        this.resultDescriptions = resultDescriptions;
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

        final AcapturePaymentResultDto that = (AcapturePaymentResultDto) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(resultCode, that.resultCode)
                .append(resultDescriptions, that.resultDescriptions)
                .append(paymentReference, that.paymentReference)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(resultCode)
                .append(resultDescriptions)
                .append(paymentReference)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .appendSuper(super.toString())
                .append("resultCode", resultCode)
                .append("resultDescriptions", resultDescriptions)
                .append("paymentReference", paymentReference)
                .toString();
    }
}
