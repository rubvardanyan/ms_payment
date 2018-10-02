package com.sfl.pms.externalclients.payment.acapture.model.payment;

import com.sfl.pms.externalclients.payment.acapture.model.AbstractAcaptureApiCommunicatorModel;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.math.BigDecimal;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 9/30/18
 * Time: 7:54 PM
 */
public class AcaptureAmountModel extends AbstractAcaptureApiCommunicatorModel {
    private static final long serialVersionUID = 296047280940593053L;

    /* Properties */
    private String currency;

    private BigDecimal amount;

    /* Constructor */
    public AcaptureAmountModel() {
    }

    public AcaptureAmountModel(final String currency, final BigDecimal amount) {
        this.currency = currency;
        this.amount = amount;
    }

    /* Properties getters and setters */
    public String getCurrency() {
        return currency;
    }

    public void setCurrency(final String currency) {
        this.currency = currency;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(final BigDecimal amount) {
        this.amount = amount;
    }

    /* Equals, HashCode and ToString */
    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        final AcaptureAmountModel that = (AcaptureAmountModel) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(currency, that.currency)
                .append(amount, that.amount)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(currency)
                .append(amount)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("currency", currency)
                .append("amount", amount)
                .toString();
    }
}
