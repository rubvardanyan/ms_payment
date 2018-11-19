package com.sfl.pms.services.payment.common.event.order;

import com.sfl.pms.services.system.event.model.ApplicationEvent;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.util.Assert;

/**
 * User: Ruben Dilanyan
 * Company: SFL LLC
 * Date: 2/4/15
 * Time: 3:55 PM
 */
public class StartOrderRefundRequestProcessingCommandEvent implements ApplicationEvent {

    /* Properties */
    private final Long orderRefundRequestId;

    /* Constructors */
    public StartOrderRefundRequestProcessingCommandEvent(final Long orderRefundRequestId) {
        Assert.notNull(orderRefundRequestId, "Order payment request id should not be null");
        this.orderRefundRequestId = orderRefundRequestId;
    }

    /* Properties getters and setters */
    public Long getOrderRefundRequestId() {
        return orderRefundRequestId;
    }

    /* Equals, HashCode and ToString */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof StartOrderRefundRequestProcessingCommandEvent)) {
            return false;
        }
        final StartOrderRefundRequestProcessingCommandEvent that = (StartOrderRefundRequestProcessingCommandEvent) o;
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
