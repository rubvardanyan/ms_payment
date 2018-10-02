package com.sfl.pms.externalclients.payment.acapture.model.request;

import com.sfl.pms.externalclients.payment.acapture.model.AbstractAcaptureApiCommunicatorModel;
import com.sfl.pms.externalclients.payment.acapture.model.authentication.AcaptureAuthenticationModel;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 10/1/18
 * Time: 3:15 AM
 */
public class CheckPaymentStatusRequest extends AbstractAcaptureApiCommunicatorModel {

    /* Properties */
    private String resourcePath;

    private AcaptureAuthenticationModel authenticationModel;

    /* Constructor */
    public CheckPaymentStatusRequest() {
    }

    public CheckPaymentStatusRequest(final String resourcePath, final AcaptureAuthenticationModel authenticationModel) {
        this.resourcePath = resourcePath;
        this.authenticationModel = authenticationModel;
    }

    /* Properties getters and setters */
    public String getResourcePath() {
        return resourcePath;
    }

    public void setResourcePath(final String resourcePath) {
        this.resourcePath = resourcePath;
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

        final CheckPaymentStatusRequest that = (CheckPaymentStatusRequest) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(resourcePath, that.resourcePath)
                .append(authenticationModel, that.authenticationModel)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(resourcePath)
                .append(authenticationModel)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .appendSuper(super.toString())
                .append("resourcePath", resourcePath)
                .append("authenticationModel", authenticationModel)
                .toString();
    }
}
