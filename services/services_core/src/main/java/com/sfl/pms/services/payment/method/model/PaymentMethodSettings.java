package com.sfl.pms.services.payment.method.model;

import com.sfl.pms.services.common.model.AbstractDomainEntityModel;
import com.sfl.pms.services.payment.provider.model.PaymentProviderType;
import com.sfl.pms.services.payment.settings.model.PaymentProviderSettings;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 9/28/18
 * Time: 9:04 PM
 */
@Entity
@Table(name = "payment_method_settings")
@DiscriminatorColumn(name = "provider_type", discriminatorType = DiscriminatorType.STRING)
public abstract class PaymentMethodSettings extends AbstractDomainEntityModel {
    private static final long serialVersionUID = 4306034234100949934L;

    /* Properties */
    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method_type")
    private PaymentMethodType paymentMethodType;

    @Enumerated(EnumType.STRING)
    @Column(name = "provider_type", insertable = false, updatable = false)
    private PaymentProviderType providerType;

    /* Constructor */
    public PaymentMethodSettings() {
    }

    /* Properties getters and setters */
    public PaymentMethodType getPaymentMethodType() {
        return paymentMethodType;
    }

    public void setPaymentMethodType(final PaymentMethodType paymentMethodType) {
        this.paymentMethodType = paymentMethodType;
    }

    public PaymentProviderType getProviderType() {
        return providerType;
    }

    public void setProviderType(final PaymentProviderType providerType) {
        this.providerType = providerType;
    }

    /* Equals, HashCode and ToString */
    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        final PaymentMethodSettings that = (PaymentMethodSettings) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(paymentMethodType, that.paymentMethodType)
                .append(providerType, that.providerType)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(paymentMethodType)
                .append(providerType)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("type", paymentMethodType)
                .append("providerType", providerType)
                .toString();
    }
}
