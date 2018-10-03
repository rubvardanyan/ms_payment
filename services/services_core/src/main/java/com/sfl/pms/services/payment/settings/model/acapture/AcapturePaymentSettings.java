package com.sfl.pms.services.payment.settings.model.acapture;

import com.sfl.pms.services.payment.provider.model.PaymentProviderType;
import com.sfl.pms.services.payment.settings.model.PaymentProviderSettings;
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
 * Date: 9/28/18
 * Time: 8:12 PM
 */
@Entity
@Table(name = "payment_provider_settings_acapture")
@DiscriminatorValue("ACAPTURE")
public class AcapturePaymentSettings extends PaymentProviderSettings {
    private static final long serialVersionUID = 3863445733619379964L;

    /* Properties */
    @Column(name = "hostedPageUrl", nullable = false)
    private String hostPageUrl;

    /* Constructor */
    public AcapturePaymentSettings() {
        setType(PaymentProviderType.ACAPTURE);
    }

    /* Properties getters and setters */
    public String getHostPageUrl() {
        return hostPageUrl;
    }

    public void setHostPageUrl(final String hostPageUrl) {
        this.hostPageUrl = hostPageUrl;
    }

    /* Equals, HashCode and ToString */
    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        final AcapturePaymentSettings that = (AcapturePaymentSettings) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(hostPageUrl, that.hostPageUrl)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(hostPageUrl)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("hostPageUrl", hostPageUrl)
                .toString();
    }
}
