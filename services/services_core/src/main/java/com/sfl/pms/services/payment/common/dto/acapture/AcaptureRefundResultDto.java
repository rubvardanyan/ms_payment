package com.sfl.pms.services.payment.common.dto.acapture;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 10/1/18
 * Time: 3:53 AM
 */
public class AcaptureRefundResultDto implements Serializable {
    private static final long serialVersionUID = -196496247069612092L;

    /* Properties */
    private String resultCode;

    private String resultDescriptions;

    /* Constructor */
    public AcaptureRefundResultDto() {
        super();
    }

    public AcaptureRefundResultDto(final String resultCode, final String resultDescriptions) {
        this.resultCode = resultCode;
        this.resultDescriptions = resultDescriptions;
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

    /* Equals, HashCode and ToString */
    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        final AcaptureRefundResultDto that = (AcaptureRefundResultDto) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(resultCode, that.resultCode)
                .append(resultDescriptions, that.resultDescriptions)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(resultCode)
                .append(resultDescriptions)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .appendSuper(super.toString())
                .append("resultCode", resultCode)
                .append("resultDescriptions", resultDescriptions)
                .toString();
    }
}
