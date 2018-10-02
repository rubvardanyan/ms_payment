package com.sfl.pms.externalclients.payment.acapture.model.result;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sfl.pms.externalclients.payment.acapture.model.AbstractAcaptureApiCommunicatorModel;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 9/30/18
 * Time: 8:28 PM
 */
public class AcaptureResultModel extends AbstractAcaptureApiCommunicatorModel {
    private static final long serialVersionUID = -7626144173383921550L;

    /* Properties */
    @JsonProperty("code")
    private String code;

    @JsonProperty("description")
    private String description;

    /* Constructor */
    public AcaptureResultModel() {
    }

    public AcaptureResultModel(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /* Properties getters and setters */
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /* Equals, HashCode and ToString */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AcaptureResultModel that = (AcaptureResultModel) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(code, that.code)
                .append(description, that.description)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
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
