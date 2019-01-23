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
 * Time: 4:49 PM
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AcaptureEncryptedNotificationJsonModel implements Serializable {
    private static final long serialVersionUID = 2861482769260581235L;

    /* Properties */
    @JsonProperty("payload")
    private String payload;

    /* Constructors */
    public AcaptureEncryptedNotificationJsonModel() {
    }

    public AcaptureEncryptedNotificationJsonModel(final String payload) {
        this.payload = payload;
    }

    /* Properties getters and setters */
    public String getPayload() {
        return payload;
    }

    public void setPayload(final String payload) {
        this.payload = payload;
    }

    /* Equals, HashCode and ToString */
    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        final AcaptureEncryptedNotificationJsonModel that = (AcaptureEncryptedNotificationJsonModel) o;

        return new EqualsBuilder()
                .append(payload, that.payload)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(payload)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("payload", payload)
                .toString();
    }
}
