package com.sfl.pms.externalclients.payment.acapture.model.customer;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sfl.pms.externalclients.payment.acapture.model.AbstractAcaptureApiCommunicatorModel;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 10/1/18
 * Time: 3:38 AM
 */
public class AcaptureCustomerModel extends AbstractAcaptureApiCommunicatorModel {
    private static final long serialVersionUID = 8631905272023382796L;

    /* Properties */
    @JsonProperty("ip")
    private String ip;

    @JsonProperty("ipCountry")
    private String ipCountry;

    /* Constructor */
    public AcaptureCustomerModel() {
    }

    /* Properties getters and setters */
    public String getIp() {
        return ip;
    }

    public void setIp(final String ip) {
        this.ip = ip;
    }

    public String getIpCountry() {
        return ipCountry;
    }

    public void setIpCountry(final String ipCountry) {
        this.ipCountry = ipCountry;
    }

    /* Equals, HashCode and ToString */
    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        final AcaptureCustomerModel that = (AcaptureCustomerModel) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(ip, that.ip)
                .append(ipCountry, that.ipCountry)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(ip)
                .append(ipCountry)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .appendSuper(super.toString())
                .append("ip", ip)
                .append("ipCountry", ipCountry)
                .toString();
    }
}
