package com.sfl.pms.externalclients.payment.acapture.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 9/30/18
 * Time: 7:57 PM
 */
public class CreateCheckoutResponse extends AbstractAcaptureResponseModel {
    private static final long serialVersionUID = -7873657862621744973L;

    /* Properties */
    @JsonProperty("id")
    private String checkoutId;

    /* Constructor */
    public CreateCheckoutResponse() {
    }

    /* Properties getters and setters */
    public String getCheckoutId() {
        return checkoutId;
    }

    public void setCheckoutId(final String checkoutId) {
        this.checkoutId = checkoutId;
    }

    /* Equals, HashCode and ToString */
    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        final CreateCheckoutResponse that = (CreateCheckoutResponse) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(checkoutId, that.checkoutId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(checkoutId)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .appendSuper(super.toString())
                .append("checkoutId", checkoutId)
                .toString();
    }
}
