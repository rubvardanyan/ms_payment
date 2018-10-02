package com.sfl.pms.services.payment.common.model.channel;

import com.sfl.pms.services.common.model.AbstractDomainEntityModel;
import com.sfl.pms.services.payment.common.model.Payment;
import com.sfl.pms.services.payment.common.model.metadata.PaymentProviderMetadata;
import com.sfl.pms.services.payment.method.model.PaymentMethodType;
import com.sfl.pms.services.payment.provider.model.PaymentProviderIntegrationType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;

/**
 * User: Ruben Dilanyan
 * Company: SFL LLC
 * Date: 7/7/15
 * Time: 10:51 AM
 */
@Entity
@Table(name = "payment_processing_channel")
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "type", discriminatorType = DiscriminatorType.STRING)
public abstract class PaymentProcessingChannel extends AbstractDomainEntityModel {

    private static final long serialVersionUID = -3976813088523455220L;

    /* Properties */
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, insertable = false, updatable = false)
    private PaymentProcessingChannelType type;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id", nullable = false, unique = false)
    private Payment payment;

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_provider_integration_type", nullable = false)
    private PaymentProviderIntegrationType paymentProviderIntegrationType;

    @OneToOne(optional = true, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "payment_provider_metadata_id", nullable = true, unique = false)
    private PaymentProviderMetadata paymentProviderMetadata;

    /* Constructors */
    public PaymentProcessingChannel() {
    }

    /* Properties getters and setters */
    public PaymentProcessingChannelType getType() {
        return type;
    }

    public void setType(final PaymentProcessingChannelType type) {
        this.type = type;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(final Payment payment) {
        this.payment = payment;
    }

    public PaymentProviderIntegrationType getPaymentProviderIntegrationType() {
        return paymentProviderIntegrationType;
    }

    public void setPaymentProviderIntegrationType(final PaymentProviderIntegrationType paymentProviderIntegrationType) {
        this.paymentProviderIntegrationType = paymentProviderIntegrationType;
    }

    public PaymentProviderMetadata getPaymentProviderMetadata() {
        return paymentProviderMetadata;
    }

    public void setPaymentProviderMetadata(final PaymentProviderMetadata paymentProviderMetadata) {
        this.paymentProviderMetadata = paymentProviderMetadata;
    }

    /* Abstract methods */
    public abstract PaymentMethodType getPaymentMethodTypeIfDefined();

    /* Equals, HashCode and ToString */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PaymentProcessingChannel)) {
            return false;
        }
        final PaymentProcessingChannel that = (PaymentProcessingChannel) o;
        final EqualsBuilder builder = new EqualsBuilder();
        builder.appendSuper(super.equals(that));
        builder.append(this.getType(), that.getType());
        builder.append(this.getPaymentProviderIntegrationType(), that.getPaymentProviderIntegrationType());
        builder.append(getIdOrNull(this.getPayment()), getIdOrNull(that.getPayment()));
        builder.append(getIdOrNull(this.getPaymentProviderMetadata()), getIdOrNull(that.getPaymentProviderMetadata()));
        return builder.isEquals();
    }

    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        builder.appendSuper(super.hashCode());
        builder.append(this.getType());
        builder.append(this.getPaymentProviderIntegrationType());
        builder.append(getIdOrNull(this.getPayment()));
        builder.append(getIdOrNull(this.getPaymentProviderMetadata()));
        return builder.build();
    }


    @Override
    public String toString() {
        final ToStringBuilder builder = new ToStringBuilder(this);
        builder.appendSuper(super.toString());
        builder.append("type", this.getType());
        builder.append("paymentProviderIntegrationType", this.getPaymentProviderIntegrationType());
        builder.append("payment", getIdOrNull(this.getPayment()));
        builder.append("paymentProviderMetadata", getIdOrNull(this.getPaymentProviderMetadata()));
        return builder.build();
    }
}
