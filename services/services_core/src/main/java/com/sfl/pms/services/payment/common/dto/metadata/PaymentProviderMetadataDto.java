package com.sfl.pms.services.payment.common.dto.metadata;

import com.sfl.pms.services.common.dto.AbstractDomainEntityModelDto;
import com.sfl.pms.services.payment.common.model.metadata.PaymentProviderMetadata;
import com.sfl.pms.services.payment.provider.model.PaymentProviderType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 9/29/18
 * Time: 12:17 AM
 */
public abstract class PaymentProviderMetadataDto<T extends PaymentProviderMetadata> extends AbstractDomainEntityModelDto<T> {
    private static final long serialVersionUID = -7948276035770998681L;

    /* Properties */
    private final PaymentProviderType paymentProviderType;



    /* Constructor */
    public PaymentProviderMetadataDto(final PaymentProviderType paymentProviderType) {
        this.paymentProviderType = paymentProviderType;
    }

    /* Properties getters and setters */
    public PaymentProviderType getPaymentProviderType() {
        return paymentProviderType;
    }

    /* Equals, HashCode and ToString */
    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        final PaymentProviderMetadataDto<?> that = (PaymentProviderMetadataDto<?>) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(paymentProviderType, that.paymentProviderType)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(paymentProviderType)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .appendSuper(super.toString())
                .append("paymentProviderType", paymentProviderType)
                .toString();
    }
}
