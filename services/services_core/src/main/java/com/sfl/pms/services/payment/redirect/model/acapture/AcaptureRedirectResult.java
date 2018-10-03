package com.sfl.pms.services.payment.redirect.model.acapture;

import com.sfl.pms.services.payment.provider.model.PaymentProviderType;
import com.sfl.pms.services.payment.redirect.model.PaymentProviderRedirectResult;
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
 * Date: 9/30/18
 * Time: 11:02 PM
 */
@Entity
@DiscriminatorValue(value = "ACAPTURE")
@Table(name = "payment_redirect_result_acapture")
public class AcaptureRedirectResult extends PaymentProviderRedirectResult {

    /* Properties */
    @Column(name = "checkout_id", nullable = false)
    private String checkoutId;

    @Column(name = "resource_path", nullable = false)
    private String resourcePath;

    /* Constructor */
    public AcaptureRedirectResult() {
        setType(PaymentProviderType.ACAPTURE);
    }

    public AcaptureRedirectResult(final boolean generateUuId) {
        super(generateUuId);
        setType(PaymentProviderType.ACAPTURE);
    }

    /* Properties getters and setters */
    public String getCheckoutId() {
        return checkoutId;
    }

    public void setCheckoutId(final String checkoutId) {
        this.checkoutId = checkoutId;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public void setResourcePath(final String resourcePath) {
        this.resourcePath = resourcePath;
    }

    /* Equals, HashCode and ToString */
    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        final AcaptureRedirectResult that = (AcaptureRedirectResult) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(checkoutId, that.checkoutId)
                .append(resourcePath, that.resourcePath)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(checkoutId)
                .append(resourcePath)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .appendSuper(super.toString())
                .append("checkoutId", checkoutId)
                .append("resourcePath", resourcePath)
                .toString();
    }
}
