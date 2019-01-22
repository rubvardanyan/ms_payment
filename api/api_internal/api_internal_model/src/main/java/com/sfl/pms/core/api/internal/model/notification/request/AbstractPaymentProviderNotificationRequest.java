package com.sfl.pms.core.api.internal.model.notification.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sfl.pms.core.api.internal.model.common.AbstractRequestModel;
import com.sfl.pms.core.api.internal.model.common.result.ErrorResponseModel;
import com.sfl.pms.core.api.internal.model.common.result.ErrorType;
import com.sfl.pms.core.api.internal.model.provider.PaymentProviderClientType;
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
 * Date: 1/21/19
 * Time: 9:05 PM
 */
public abstract class AbstractPaymentProviderNotificationRequest extends AbstractRequestModel {
    private static final long serialVersionUID = -6764878481749688039L;

    /* Properties */
    @JsonProperty("rawContent")
    private String rawContent;

    @JsonProperty("notificationsToken")
    private String notificationsToken;

    @JsonProperty("paymentProviderType")
    private PaymentProviderClientType paymentProviderType;

    @JsonProperty("clientIpAddress")
    private String clientIpAddress;

    /* Constructor */
    public AbstractPaymentProviderNotificationRequest(final PaymentProviderClientType paymentProviderType) {
        this.paymentProviderType = paymentProviderType;
    }

    public AbstractPaymentProviderNotificationRequest(final String rawContent,
                                                      final String notificationsToken,
                                                      final PaymentProviderClientType paymentProviderType,
                                                      final String clientIpAddress) {
        this.rawContent = rawContent;
        this.notificationsToken = notificationsToken;
        this.paymentProviderType = paymentProviderType;
        this.clientIpAddress = clientIpAddress;
    }

    /* Utility methods */
    @Nonnull
    @Override
    public List<ErrorResponseModel> validateRequiredFields() {
        final List<ErrorResponseModel> errors = new ArrayList<>();
        if (getPaymentProviderType() == null) {
            errors.add(new ErrorResponseModel(ErrorType.PAYMENT_PROVIDER_NOTIFICATION_MISSING_PROVIDER_TYPE));
        }
        if (StringUtils.isBlank(getRawContent())) {
            errors.add(new ErrorResponseModel(ErrorType.PAYMENT_PROVIDER_NOTIFICATION_MISSING_RAW_CONTENT));
        }
        return errors;
    }

    /* Properties getters and setters */
    public String getRawContent() {
        return rawContent;
    }

    public void setRawContent(final String rawContent) {
        this.rawContent = rawContent;
    }

    public String getNotificationsToken() {
        return notificationsToken;
    }

    public void setNotificationsToken(final String notificationsToken) {
        this.notificationsToken = notificationsToken;
    }

    public PaymentProviderClientType getPaymentProviderType() {
        return paymentProviderType;
    }

    public void setPaymentProviderType(final PaymentProviderClientType paymentProviderType) {
        this.paymentProviderType = paymentProviderType;
    }

    public String getClientIpAddress() {
        return clientIpAddress;
    }

    public void setClientIpAddress(final String clientIpAddress) {
        this.clientIpAddress = clientIpAddress;
    }

    /* Equals, HashCode and ToString */
    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        final AbstractPaymentProviderNotificationRequest that = (AbstractPaymentProviderNotificationRequest) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(rawContent, that.rawContent)
                .append(notificationsToken, that.notificationsToken)
                .append(paymentProviderType, that.paymentProviderType)
                .append(clientIpAddress, that.clientIpAddress)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(rawContent)
                .append(notificationsToken)
                .append(paymentProviderType)
                .append(clientIpAddress)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .appendSuper(super.toString())
                .append("rawContent", rawContent)
                .append("notificationsToken", notificationsToken)
                .append("paymentProviderType", paymentProviderType)
                .append("clientIpAddress", clientIpAddress)
                .toString();
    }
}
