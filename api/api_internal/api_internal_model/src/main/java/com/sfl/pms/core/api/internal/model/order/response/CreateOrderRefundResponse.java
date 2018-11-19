package com.sfl.pms.core.api.internal.model.order.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sfl.pms.core.api.internal.model.common.AbstractResponseModel;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * User: Ruben Dilanyan
 * Company: SFL LLC
 * Date: 1/19/16
 * Time: 2:12 PM
 */
public class CreateOrderRefundResponse extends AbstractResponseModel {

    private static final long serialVersionUID = -5964988045208009613L;

    /* Properties */
    @JsonProperty("orderRefundRequestUuId")
    private String orderRefundRequestUuId;

    /* Constructors */
    public CreateOrderRefundResponse() {
    }

    public CreateOrderRefundResponse(final String orderRefundRequestUuId) {
        this.orderRefundRequestUuId = orderRefundRequestUuId;
    }

    /* Properties getters and setters */
    public String getOrderRefundRequestUuId() {
        return orderRefundRequestUuId;
    }

    public void setOrderRefundRequestUuId(final String orderRefundRequestUuId) {
        this.orderRefundRequestUuId = orderRefundRequestUuId;
    }

    /* Equals, HashCode and ToString */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CreateOrderRefundResponse)) {
            return false;
        }
        final CreateOrderRefundResponse that = (CreateOrderRefundResponse) o;
        final EqualsBuilder builder = new EqualsBuilder();
        builder.appendSuper(super.equals(that));
        builder.append(this.getOrderRefundRequestUuId(), that.getOrderRefundRequestUuId());
        return builder.isEquals();
    }

    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        builder.appendSuper(super.hashCode());
        builder.append(this.getOrderRefundRequestUuId());
        return builder.build();
    }

    @Override
    public String toString() {
        final ToStringBuilder builder = new ToStringBuilder(this);
        builder.appendSuper(super.toString());
        builder.append("orderRefundRequestUuId", this.getOrderRefundRequestUuId());
        return builder.build();
    }
}
