package com.sfl.pms.queue.amqp.model.payment.common.order;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sfl.pms.queue.amqp.model.AbstractRPCTransferModel;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * User: Ruben Dilanyan
 * Company: SFL LLC
 * Date: 2/4/15
 * Time: 4:34 PM
 */
public class OrderRefundRequestRPCTransferModel extends AbstractRPCTransferModel {
    private static final long serialVersionUID = 5756339458066263058L;

    /* Properties */
    @JsonProperty(value = "orderRefundRequestId", required = true)
    private Long orderRefundRequestId;

    /* Constructors */
    public OrderRefundRequestRPCTransferModel() {
    }

    public OrderRefundRequestRPCTransferModel(final Long orderRefundRequestId) {
        this.orderRefundRequestId = orderRefundRequestId;
    }

    /* Properties getters and setters */
    public Long getOrderRefundRequestId() {
        return orderRefundRequestId;
    }

    public void setOrderRefundRequestId(final Long orderRefundRequestId) {
        this.orderRefundRequestId = orderRefundRequestId;
    }

    /* Equals, HashCode and ToString */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrderRefundRequestRPCTransferModel)) {
            return false;
        }
        final OrderRefundRequestRPCTransferModel that = (OrderRefundRequestRPCTransferModel) o;
        final EqualsBuilder builder = new EqualsBuilder();
        builder.append(getOrderRefundRequestId(), that.getOrderRefundRequestId());
        return builder.isEquals();
    }

    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        builder.append(getOrderRefundRequestId());
        return builder.build();
    }

    @Override
    public String toString() {
        final ToStringBuilder builder = new ToStringBuilder(this);
        builder.append("orderRefundRequestId", getOrderRefundRequestId());
        return builder.build();
    }
}
