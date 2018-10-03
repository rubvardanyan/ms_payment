package com.sfl.pms.services.payment.method.model.acapture;

import com.sfl.pms.services.payment.method.model.PaymentMethodSettings;
import com.sfl.pms.services.payment.settings.model.acapture.AcapturePaymentSettings;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 9/28/18
 * Time: 9:07 PM
 */
@Entity
@Table(name = "payment_method_settings_acapture")
@DiscriminatorValue("ACAPTURE")
public class AcapturePaymentMethodSettings extends PaymentMethodSettings {
    private static final long serialVersionUID = 4449566127293930301L;

    /* Properties */
    @Column(name = "authorization_id")
    private String authorizationId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_settings_id", nullable = false)
    private AcapturePaymentSettings paymentSettings;

    /* Constructor */
    public AcapturePaymentMethodSettings() {
    }

    /* Properties getters and setters */
    public String getAuthorizationId() {
        return authorizationId;
    }

    public void setAuthorizationId(final String authorizationId) {
        this.authorizationId = authorizationId;
    }

    public AcapturePaymentSettings getPaymentSettings() {
        return paymentSettings;
    }

    public void setPaymentSettings(final AcapturePaymentSettings paymentSettings) {
        this.paymentSettings = paymentSettings;
    }

    /* Equals, HashCode and ToString */
    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        final AcapturePaymentMethodSettings that = (AcapturePaymentMethodSettings) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(authorizationId, that.authorizationId)
                .append(getIdOrNull(paymentSettings), getIdOrNull(paymentSettings))
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(authorizationId)
                .append(getIdOrNull(paymentSettings))
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("authorizationId", authorizationId)
                .append("paymentSettings", getIdOrNull(paymentSettings))
                .toString();
    }
}
