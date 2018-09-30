package com.sfl.pms.core.api.internal.model.redirect.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sfl.pms.core.api.internal.model.common.AbstractRequestModel;
import com.sfl.pms.core.api.internal.model.common.result.ErrorResponseModel;
import com.sfl.pms.core.api.internal.model.common.result.ErrorType;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 9/30/18
 * Time: 10:54 PM
 */
public class CreateAcaptureRedirectResultRequest extends AbstractRequestModel {
    private static final long serialVersionUID = 4399819330604768571L;

    /* Properties */
    @JsonProperty("checkoutId")
    private String checkoutId;

    @JsonProperty("resourcePath")
    private String resourcePath;

    /* Constructor */
    public CreateAcaptureRedirectResultRequest() {
    }

    public CreateAcaptureRedirectResultRequest(final String checkoutId, final String resourcePath) {
        this.checkoutId = checkoutId;
        this.resourcePath = resourcePath;
    }

    /* Public methods */
    @Nonnull
    @Override
    public List<ErrorResponseModel> validateRequiredFields() {
        final List<ErrorResponseModel> errors = new ArrayList<>();
        if(StringUtils.isBlank(this.getCheckoutId())) {
            errors.add(new ErrorResponseModel(ErrorType.PAYMENT_PROVIDER_REDIRECT_RESULT_MISSING_CHECKOUT_ID));
        }
        if(StringUtils.isBlank(this.getResourcePath())) {
            errors.add(new ErrorResponseModel(ErrorType.PAYMENT_PROVIDER_REDIRECT_RESULT_MISSING_RESOURCE_PATH));
        }
        return errors;
    }

    /* Properties getters and setters */
    public String getCheckoutId() {
        return checkoutId;
    }

    public void setCheckoutId(final String checkoutId) {
        this.checkoutId = checkoutId;
    }

    public String getResourcePath() {
        return resourcePath;
    }

    public void setResourcePath(final String resourcePath) {
        this.resourcePath = resourcePath;
    }

    /* Equals, HashCode and ToString */
    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        final CreateAcaptureRedirectResultRequest that = (CreateAcaptureRedirectResultRequest) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(checkoutId, that.checkoutId)
                .append(resourcePath, that.resourcePath)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(checkoutId)
                .append(resourcePath)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .appendSuper(super.toString())
                .append("checkoutId", checkoutId)
                .append("resourcePath", resourcePath)
                .toString();
    }
}
