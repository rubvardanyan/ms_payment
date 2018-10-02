package com.sfl.pms.services.payment.common.model.acapture;

import com.sfl.pms.services.payment.common.model.metadata.PaymentProviderMetadata;
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
 * Date: 9/29/18
 * Time: 12:13 AM
 */
@Entity
@Table(name = "payment_provider_metadata_acapture")
@DiscriminatorValue("ACAPTURE")
public class AcapturePaymentProviderMetadata extends PaymentProviderMetadata {
    private static final long serialVersionUID = -7873580688586998715L;

    /* Properties */
    @Column(name = "checkout_id", nullable = false)
    private String checkoutId;

    /* Constructor */
    public AcapturePaymentProviderMetadata() {
        setProviderType(PaymentProviderType.ACAPTURE);
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

        final AcapturePaymentProviderMetadata that = (AcapturePaymentProviderMetadata) o;

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
                .append("checkoutId", checkoutId)
                .toString();
    }
}
