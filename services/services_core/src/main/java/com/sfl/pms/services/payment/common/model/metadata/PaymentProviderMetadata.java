package com.sfl.pms.services.payment.common.model.metadata;

import com.sfl.pms.services.common.model.AbstractDomainEntityModel;
import com.sfl.pms.services.payment.common.model.channel.PaymentProcessingChannel;
import com.sfl.pms.services.payment.provider.model.PaymentProviderType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 9/29/18
 * Time: 12:09 AM
 */
@Entity
@Table(name = "payment_provider_metadata")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "provider_type", discriminatorType = DiscriminatorType.STRING)
public abstract class PaymentProviderMetadata extends AbstractDomainEntityModel {
    private static final long serialVersionUID = -3168336803019025274L;

    /* Properties */
    @Enumerated(EnumType.STRING)
    @Column(name = "provider_type", insertable = false, updatable = false)
    private PaymentProviderType providerType;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_processing_channel_id", nullable = false, unique = false)
    private PaymentProcessingChannel paymentProcessingChannel;

    /* Constructor */
    public PaymentProviderMetadata() {
    }

    /* Properties getters and setters */
    public PaymentProviderType getProviderType() {
        return providerType;
    }

    public void setProviderType(final PaymentProviderType providerType) {
        this.providerType = providerType;
    }

    public PaymentProcessingChannel getPaymentProcessingChannel() {
        return paymentProcessingChannel;
    }

    public void setPaymentProcessingChannel(final PaymentProcessingChannel paymentProcessingChannel) {
        this.paymentProcessingChannel = paymentProcessingChannel;
    }

    /* Equals, HashCode and ToString */
    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        final PaymentProviderMetadata that = (PaymentProviderMetadata) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(providerType, that.providerType)
                .append(getIdOrNull(paymentProcessingChannel), getIdOrNull(that.paymentProcessingChannel))
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(providerType)
                .append(getIdOrNull(paymentProcessingChannel))
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("providerType", providerType)
                .append("paymentProcessingChannel", getIdOrNull(paymentProcessingChannel))
                .toString();
    }
}
