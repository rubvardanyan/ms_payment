package com.sfl.pms.services.payment.notification.impl.processors.acapture.json.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import java.io.Serializable;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 1/23/19
 * Time: 8:19 PM
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AcaptureNotificationAuthenticationJsonModel implements Serializable {
    private static final long serialVersionUID = 5152624989794392869L;

    /* Properties */
    @JsonProperty("entityId")
    private String entityId;

    /* Constructor */
    public AcaptureNotificationAuthenticationJsonModel() {
    }

    public AcaptureNotificationAuthenticationJsonModel(final String entityId) {
        this.entityId = entityId;
    }

    /* Properties getters and setters */
    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(final String entityId) {
        this.entityId = entityId;
    }

    /* Equals, HashCode and ToString */
    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        final AcaptureNotificationAuthenticationJsonModel that = (AcaptureNotificationAuthenticationJsonModel) o;

        return new EqualsBuilder()
                .append(entityId, that.entityId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(entityId)
                .toHashCode();
    }


}
