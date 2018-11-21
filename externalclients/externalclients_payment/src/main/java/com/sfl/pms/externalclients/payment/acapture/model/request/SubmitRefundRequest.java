package com.sfl.pms.externalclients.payment.acapture.model.request;

import com.sfl.pms.externalclients.payment.acapture.model.AbstractAcaptureApiCommunicatorModel;
import com.sfl.pms.externalclients.payment.acapture.model.authentication.AcaptureAuthenticationModel;
import com.sfl.pms.externalclients.payment.acapture.model.payment.AcaptureAmountModel;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 11/19/18
 * Time: 10:12 PM
 */
public class SubmitRefundRequest extends AbstractAcaptureApiCommunicatorModel {
    private static final long serialVersionUID = -7406633859497601275L;

    /* Properties */
    private String checkoutId;

    private AcaptureAmountModel amountModel;

    private AcaptureAuthenticationModel authenticationModel;

    /* Constructors */
    public SubmitRefundRequest() {
    }

    public SubmitRefundRequest(final String checkoutId, final AcaptureAmountModel amountModel, final AcaptureAuthenticationModel authenticationModel) {
        this.checkoutId = checkoutId;
        this.amountModel = amountModel;
        this.authenticationModel = authenticationModel;
    }

    /* Properties getters and setters */
    public String getCheckoutId() {
        return checkoutId;
    }

    public void setCheckoutId(final String checkoutId) {
        this.checkoutId = checkoutId;
    }

    public AcaptureAmountModel getAmountModel() {
        return amountModel;
    }

    public void setAmountModel(final AcaptureAmountModel amountModel) {
        this.amountModel = amountModel;
    }

    public AcaptureAuthenticationModel getAuthenticationModel() {
        return authenticationModel;
    }

    public void setAuthenticationModel(final AcaptureAuthenticationModel authenticationModel) {
        this.authenticationModel = authenticationModel;
    }

    /* Equals, HashCode and ToString */
    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        final SubmitRefundRequest that = (SubmitRefundRequest) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(checkoutId, that.checkoutId)
                .append(amountModel, that.amountModel)
                .append(authenticationModel, that.authenticationModel)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(checkoutId)
                .append(amountModel)
                .append(authenticationModel)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .appendSuper(super.toString())
                .append("checkoutId", checkoutId)
                .append("amountModel", amountModel)
                .append("authenticationModel", authenticationModel)
                .toString();
    }
}
