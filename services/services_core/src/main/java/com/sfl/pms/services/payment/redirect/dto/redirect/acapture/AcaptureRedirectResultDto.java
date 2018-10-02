package com.sfl.pms.services.payment.redirect.dto.redirect.acapture;

import com.sfl.pms.services.payment.provider.model.PaymentProviderType;
import com.sfl.pms.services.payment.redirect.dto.PaymentProviderRedirectResultDto;
import com.sfl.pms.services.payment.redirect.model.acapture.AcaptureRedirectResult;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 9/30/18
 * Time: 11:07 PM
 */
public class AcaptureRedirectResultDto extends PaymentProviderRedirectResultDto<AcaptureRedirectResult> {
    private static final long serialVersionUID = -3810272663521522597L;

    /* Properties getters and setters */
    private String checkoutId;

    private String resourcePath;

    /* Constructor */
    public AcaptureRedirectResultDto() {
        super(PaymentProviderType.ACAPTURE);
    }
    public AcaptureRedirectResultDto(final String checkoutId, final String resourcePath) {
        super(PaymentProviderType.ACAPTURE);
        this.checkoutId = checkoutId;
        this.resourcePath = resourcePath;
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

    /* Override methods */
    @Override
    public void updateDomainEntityProperties(final AcaptureRedirectResult redirectResult) {
        super.updateDomainEntityProperties(redirectResult);
        redirectResult.setCheckoutId(checkoutId);
        redirectResult.setResourcePath(resourcePath);
    }

    /* Equals, HashCode and ToString */
    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        final AcaptureRedirectResultDto that = (AcaptureRedirectResultDto) o;

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
