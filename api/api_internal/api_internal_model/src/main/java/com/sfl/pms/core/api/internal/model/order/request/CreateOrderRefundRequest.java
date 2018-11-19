package com.sfl.pms.core.api.internal.model.order.request;

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
 * User: Ruben Dilanyan
 * Company: SFL LLC
 * Date: 1/19/16
 * Time: 2:08 PM
 */
public class CreateOrderRefundRequest extends AbstractRequestModel {

    private static final long serialVersionUID = -5457703892605553780L;

    /* Properties */
    @JsonProperty("orderPaymentRequestUuid")
    private String orderPaymentRequestUuid;

    @JsonProperty("paymentProviderType")
    private PaymentProviderClientType paymentProviderType;

    /* Constructors */
    public CreateOrderRefundRequest() {
    }

    public CreateOrderRefundRequest(final String orderPaymentRequestUuid, final PaymentProviderClientType paymentProviderType) {
        this.orderPaymentRequestUuid = orderPaymentRequestUuid;
        this.paymentProviderType = paymentProviderType;
    }

    /* Properties getters and setters */
    public String getOrderPaymentRequestUuid() {
        return orderPaymentRequestUuid;
    }

    public void setOrderPaymentRequestUuid(final String orderPaymentRequestUuid) {
        this.orderPaymentRequestUuid = orderPaymentRequestUuid;
    }

    public PaymentProviderClientType getPaymentProviderType() {
        return paymentProviderType;
    }

    public void setPaymentProviderType(final PaymentProviderClientType paymentProviderType) {
        this.paymentProviderType = paymentProviderType;
    }

    /* Validation methods */
    @Nonnull
    @Override
    public List<ErrorResponseModel> validateRequiredFields() {
        final List<ErrorResponseModel> errors = new ArrayList<>();
        if (StringUtils.isBlank(getOrderPaymentRequestUuid())) {
            errors.add(new ErrorResponseModel(ErrorType.ORDER_PAYMENT_REQUEST_MISSING_UUID));
        }
        if (paymentProviderType == null) {
            errors.add(new ErrorResponseModel(ErrorType.ORDER_REFUND_REQUEST__MISSING_PAYMENT_PROVIDER_TYPE));
        }
        return errors;
    }

    /* Equals, HashCode and ToString */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CreateOrderRefundRequest)) {
            return false;
        }
        final CreateOrderRefundRequest that = (CreateOrderRefundRequest) o;
        final EqualsBuilder builder = new EqualsBuilder();
        builder.appendSuper(super.equals(that));
        builder.append(this.getOrderPaymentRequestUuid(), that.getOrderPaymentRequestUuid());
        builder.append(this.getPaymentProviderType(), that.getPaymentProviderType());
        return builder.isEquals();
    }

    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        builder.appendSuper(super.hashCode());
        builder.append(this.getOrderPaymentRequestUuid());
        builder.append(this.getPaymentProviderType());
        return builder.build();
    }

    @Override
    public String toString() {
        final ToStringBuilder builder = new ToStringBuilder(this);
        builder.appendSuper(super.toString());
        builder.append("orderPaymentRequestUuid", this.getOrderPaymentRequestUuid());
        builder.append("paymentProviderType", this.getPaymentProviderType());
        return builder.build();
    }
}
