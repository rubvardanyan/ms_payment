package com.sfl.pms.core.api.internal.model.redirect.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sfl.pms.core.api.internal.model.common.AbstractResponseModel;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 9/30/18
 * Time: 10:54 PM
 */
public class CreateAcaptureRedirectResultResponse extends AbstractResponseModel {
    private static final long serialVersionUID = 8041615210453465848L;

    /* Properties */
    @JsonProperty("paymentProviderRedirectResultUuId")
    private String paymentProviderRedirectResultUuId;

    /* Constructor */
    public CreateAcaptureRedirectResultResponse() {
    }

    public CreateAcaptureRedirectResultResponse(final String paymentProviderRedirectResultUuId) {
        this.paymentProviderRedirectResultUuId = paymentProviderRedirectResultUuId;
    }

    /* Properties getters and setters */
    public String getPaymentProviderRedirectResultUuId() {
        return paymentProviderRedirectResultUuId;
    }

    public void setPaymentProviderRedirectResultUuId(final String paymentProviderRedirectResultUuId) {
        this.paymentProviderRedirectResultUuId = paymentProviderRedirectResultUuId;
    }

    /* Equals, HashCode and ToString */
    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        final CreateAcaptureRedirectResultResponse that = (CreateAcaptureRedirectResultResponse) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(paymentProviderRedirectResultUuId, that.paymentProviderRedirectResultUuId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(paymentProviderRedirectResultUuId)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .appendSuper(super.toString())
                .append("paymentProviderRedirectResultUuId", paymentProviderRedirectResultUuId)
                .toString();
    }
}
