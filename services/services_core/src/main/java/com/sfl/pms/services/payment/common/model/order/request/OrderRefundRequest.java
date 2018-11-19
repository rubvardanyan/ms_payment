package com.sfl.pms.services.payment.common.model.order.request;

import com.sfl.pms.services.common.model.AbstractDomainUuIdAwareEntityModel;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.*;

/**
 * User: Ruben Dilanyan
 * Company: SFL LLC
 * Date: 2/4/15
 * Time: 12:59 PM
 */
@Entity
@Table(name = "payment_order_refund_request")
public class OrderRefundRequest extends AbstractDomainUuIdAwareEntityModel {
    private static final long serialVersionUID = 8059899129123438524L;

    /* Properties */
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_order_request_id", nullable = false, unique = true)
    private OrderPaymentRequest orderPaymentRequest;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    private OrderRefundRequestState state;

    /* Constructors */
    public OrderRefundRequest() {
        initializeDefaults();
    }

    public OrderRefundRequest(final boolean generateUuId) {
        super(generateUuId);
        initializeDefaults();
    }

    /* Properties getters and setters */
    public OrderPaymentRequest getOrderPaymentRequest() {
        return orderPaymentRequest;
    }

    public void setOrderPaymentRequest(final OrderPaymentRequest orderPaymentRequest) {
        this.orderPaymentRequest = orderPaymentRequest;
    }

    public OrderRefundRequestState getState() {
        return state;
    }

    public void setState(final OrderRefundRequestState state) {
        this.state = state;
    }

    /* Private utility methods */
    private void initializeDefaults() {
        this.state = OrderRefundRequestState.CREATED;
    }

    /* Equals, HashCode and ToString */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof OrderRefundRequest)) {
            return false;
        }
        final OrderRefundRequest that = (OrderRefundRequest) o;
        final EqualsBuilder builder = new EqualsBuilder();
        builder.appendSuper(super.equals(that));
        builder.append(getIdOrNull(this.getOrderPaymentRequest()), getIdOrNull(that.getOrderPaymentRequest()));
        builder.append(this.getState(), that.getState());
        return builder.isEquals();
    }

    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        builder.appendSuper(super.hashCode());
        builder.append(getIdOrNull(this.getOrderPaymentRequest()));
        builder.append(this.getState());
        return builder.build();
    }


    @Override
    public String toString() {
        final ToStringBuilder builder = new ToStringBuilder(this);
        builder.appendSuper(super.toString());
        builder.append("orderPaymentRequest", getIdOrNull(this.getOrderPaymentRequest()));
        builder.append("state", this.getState());
        return builder.build();
    }
}
