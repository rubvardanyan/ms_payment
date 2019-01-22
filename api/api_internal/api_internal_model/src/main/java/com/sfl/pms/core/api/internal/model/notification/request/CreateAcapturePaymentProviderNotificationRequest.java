package com.sfl.pms.core.api.internal.model.notification.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sfl.pms.core.api.internal.model.common.result.ErrorResponseModel;
import com.sfl.pms.core.api.internal.model.common.result.ErrorType;
import com.sfl.pms.core.api.internal.model.provider.PaymentProviderClientType;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 1/21/19
 * Time: 9:09 PM
 */
public class CreateAcapturePaymentProviderNotificationRequest extends AbstractPaymentProviderNotificationRequest {
    private static final long serialVersionUID = 7363048578123178051L;

    /* Properties */
    @JsonProperty("authenticationTag")
    private String authenticationTag;

    @JsonProperty("initializationVector")
    private String initializationVector;

    /* Constructor */
    public CreateAcapturePaymentProviderNotificationRequest() {
        super(PaymentProviderClientType.ACAPTURE);
    }

    public CreateAcapturePaymentProviderNotificationRequest(final String rawContent,
                                                            final String notificationsToken,
                                                            final String clientIpAddress,
                                                            final String authenticationTag,
                                                            final String initializationVector) {
        super(rawContent, notificationsToken, PaymentProviderClientType.ACAPTURE, clientIpAddress);
        this.authenticationTag = authenticationTag;
        this.initializationVector = initializationVector;
    }

    /* Utility methods */
    @Nonnull
    @Override
    public List<ErrorResponseModel> validateRequiredFields() {
        final List<ErrorResponseModel> errors = super.validateRequiredFields();
        if(StringUtils.isBlank(getAuthenticationTag())) {
            errors.add(new ErrorResponseModel(ErrorType.PAYMENT_PROVIDER_NOTIFICATION_MISSING_AUTHENTICATION_TAG));
        }
        if(StringUtils.isBlank(getInitializationVector())) {
            errors.add(new ErrorResponseModel(ErrorType.PAYMENT_PROVIDER_NOTIFICATION_MISSING_INITIALIZATION_VECTOR));
        }
        return errors;
    }

    /* Properties getters and setters */
    public String getAuthenticationTag() {
        return authenticationTag;
    }

    public void setAuthenticationTag(final String authenticationTag) {
        this.authenticationTag = authenticationTag;
    }

    public String getInitializationVector() {
        return initializationVector;
    }

    public void setInitializationVector(final String initializationVector) {
        this.initializationVector = initializationVector;
    }

    /* Equals, HashCode and ToString */
    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        final CreateAcapturePaymentProviderNotificationRequest that = (CreateAcapturePaymentProviderNotificationRequest) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(authenticationTag, that.authenticationTag)
                .append(initializationVector, that.initializationVector)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(authenticationTag)
                .append(initializationVector)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .appendSuper(super.toString())
                .append("authenticationTag", authenticationTag)
                .append("initializationVector", initializationVector)
                .toString();
    }
}
