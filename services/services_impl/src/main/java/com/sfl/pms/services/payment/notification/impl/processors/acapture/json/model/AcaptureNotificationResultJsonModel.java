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
 * Time: 8:16 PM
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AcaptureNotificationResultJsonModel implements Serializable {
    private static final long serialVersionUID = -7199704605132985488L;

    /* Properties */
    @JsonProperty("code")
    private String code;

    @JsonProperty("description")
    private String description;

    /* Constructor */
    public AcaptureNotificationResultJsonModel() {

    }

    public AcaptureNotificationResultJsonModel(final String code, final String description) {
        this.code = code;
        this.description = description;
    }

    /* Properties getters and setters */
    public String getCode() {
        return code;
    }

    public void setCode(final String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    /* Equals, HashCode and ToString */
    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        final AcaptureNotificationResultJsonModel that = (AcaptureNotificationResultJsonModel) o;

        return new EqualsBuilder()
                .append(code, that.code)
                .append(description, that.description)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(code)
                .append(description)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .appendSuper(super.toString())
                .append("code", code)
                .append("description", description)
                .toString();
    }
}
