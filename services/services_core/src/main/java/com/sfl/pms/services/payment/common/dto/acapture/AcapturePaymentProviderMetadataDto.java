package com.sfl.pms.services.payment.common.dto.acapture;

import com.sfl.pms.services.payment.common.dto.metadata.PaymentProviderMetadataDto;
import com.sfl.pms.services.payment.common.model.acapture.AcapturePaymentProviderMetadata;
import com.sfl.pms.services.payment.provider.model.PaymentProviderType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 9/29/18
 * Time: 12:16 AM
 */
public class AcapturePaymentProviderMetadataDto extends PaymentProviderMetadataDto<AcapturePaymentProviderMetadata> {
    private static final long serialVersionUID = -8920575276169957499L;

    /* Properties */
    private String checkoutId;

    /* Constructor */
    public AcapturePaymentProviderMetadataDto() {
        super(PaymentProviderType.ACAPTURE);
    }

    public AcapturePaymentProviderMetadataDto(final String checkoutId) {
        this();
        this.checkoutId = checkoutId;
    }

    /* Public methods */
    @Override
    public void updateDomainEntityProperties(final AcapturePaymentProviderMetadata domainEntity) {
        domainEntity.setCheckoutId(this.checkoutId);
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

        final AcapturePaymentProviderMetadataDto that = (AcapturePaymentProviderMetadataDto) o;

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
