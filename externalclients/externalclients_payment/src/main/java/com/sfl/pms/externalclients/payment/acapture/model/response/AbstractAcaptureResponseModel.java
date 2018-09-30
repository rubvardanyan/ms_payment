package com.sfl.pms.externalclients.payment.acapture.model.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sfl.pms.externalclients.payment.acapture.model.AbstractAcaptureApiCommunicatorModel;
import com.sfl.pms.externalclients.payment.acapture.model.result.AcaptureResultModel;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.Date;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 9/30/18
 * Time: 7:57 PM
 */
public abstract class AbstractAcaptureResponseModel extends AbstractAcaptureApiCommunicatorModel {
    private static final long serialVersionUID = -8426409009845501881L;

    /* Properties */
    @JsonProperty("ndc")
    private String ndc;

    @JsonProperty("timestamp")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private Date timestamp;

    @JsonProperty("buildNumber")
    private String buildNumber;

    @JsonProperty("result")
    private AcaptureResultModel result;

    /* Constructor */
    public AbstractAcaptureResponseModel() {

    }

    /* Properties getters and setters */
    public String getNdc() {
        return ndc;
    }

    public void setNdc(String ndc) {
        this.ndc = ndc;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getBuildNumber() {
        return buildNumber;
    }

    public void setBuildNumber(String buildNumber) {
        this.buildNumber = buildNumber;
    }

    public AcaptureResultModel getResult() {
        return result;
    }

    public void setResult(AcaptureResultModel result) {
        this.result = result;
    }

    /* Equals, HashCode and ToString */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        AbstractAcaptureResponseModel that = (AbstractAcaptureResponseModel) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(ndc, that.ndc)
                .append(timestamp, that.timestamp)
                .append(buildNumber, that.buildNumber)
                .append(result, that.result)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(ndc)
                .append(timestamp)
                .append(buildNumber)
                .append(result)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .appendSuper(super.toString())
                .append("ndc", ndc)
                .append("timestamp", timestamp)
                .append("buildNumber", buildNumber)
                .append("result", result)
                .toString();
    }
}
