package com.sfl.pms.externalclients.payment.acapture.model.request;

import com.sfl.pms.externalclients.payment.acapture.model.AbstractAcaptureApiCommunicatorModel;
import com.sfl.pms.externalclients.payment.acapture.model.authentication.AcaptureAuthenticationModel;
import com.sfl.pms.externalclients.payment.acapture.model.payment.AcaptureAmountModel;
import com.sfl.pms.externalclients.payment.acapture.model.payment.PaymentType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 9/30/18
 * Time: 7:56 PM
 */
public class CreateCheckoutRequest extends AbstractAcaptureApiCommunicatorModel {
    private static final long serialVersionUID = -7331565026457604034L;

    /* Properties */
    private AcaptureAuthenticationModel authenticationModel;

    private AcaptureAmountModel amountModel;

    private PaymentType paymentType;

    private String paymentUuid;

    /* Constructor */
    public CreateCheckoutRequest() {
    }

    /* Properties getters and setters */
    public AcaptureAuthenticationModel getAuthenticationModel() {
        return authenticationModel;
    }

    public void setAuthenticationModel(final AcaptureAuthenticationModel authenticationModel) {
        this.authenticationModel = authenticationModel;
    }

    public AcaptureAmountModel getAmountModel() {
        return amountModel;
    }

    public void setAmountModel(final AcaptureAmountModel amountModel) {
        this.amountModel = amountModel;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(final PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    public String getPaymentUuid() {
        return paymentUuid;
    }

    public void setPaymentUuid(final String paymentUuid) {
        this.paymentUuid = paymentUuid;
    }

    /* Equals, HashCode and ToString */
    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        final CreateCheckoutRequest that = (CreateCheckoutRequest) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(authenticationModel, that.authenticationModel)
                .append(amountModel, that.amountModel)
                .append(paymentType, that.paymentType)
                .append(paymentUuid, that.paymentUuid)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(authenticationModel)
                .append(amountModel)
                .append(paymentType)
                .append(paymentUuid)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("authenticationModel", authenticationModel)
                .append("amountModel", amountModel)
                .append("paymentType", paymentType)
                .append("paymentUuid", paymentUuid)
                .toString();
    }
}
