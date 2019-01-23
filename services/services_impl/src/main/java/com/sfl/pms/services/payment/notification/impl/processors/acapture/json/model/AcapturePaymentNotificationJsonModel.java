package com.sfl.pms.services.payment.notification.impl.processors.acapture.json.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 1/23/19
 * Time: 8:07 PM
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AcapturePaymentNotificationJsonModel implements Serializable {
    private static final long serialVersionUID = -3818024994784029900L;

    /* Properties */
    @JsonProperty("type")
    private AcaptureNotificationType type;

    @JsonProperty("payload")
    private AcapturePaymentNotificationPayloadJsonModel payload;

    /* Constructor */
    public AcapturePaymentNotificationJsonModel() {
    }

    public AcapturePaymentNotificationJsonModel(final AcaptureNotificationType type, final AcapturePaymentNotificationPayloadJsonModel payload) {
        this.type = type;
        this.payload = payload;
    }

    /* Properties getters and setters */
    public AcaptureNotificationType getType() {
        return type;
    }

    public void setType(final AcaptureNotificationType type) {
        this.type = type;
    }

    public AcapturePaymentNotificationPayloadJsonModel getPayload() {
        return payload;
    }

    public void setPayload(final AcapturePaymentNotificationPayloadJsonModel payload) {
        this.payload = payload;
    }

    /* Equals, HashCode and ToString */
    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        final AcapturePaymentNotificationJsonModel that = (AcapturePaymentNotificationJsonModel) o;

        return new EqualsBuilder()
                .append(type, that.type)
                .append(payload, that.payload)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(type)
                .append(payload)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("type", type)
                .append("payload", payload)
                .toString();
    }
}
