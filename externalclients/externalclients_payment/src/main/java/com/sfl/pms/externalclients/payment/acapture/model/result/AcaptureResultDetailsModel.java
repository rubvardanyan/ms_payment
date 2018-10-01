package com.sfl.pms.externalclients.payment.acapture.model.result;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sfl.pms.externalclients.payment.acapture.model.AbstractAcaptureApiCommunicatorModel;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 10/1/18
 * Time: 3:43 AM
 */
public class AcaptureResultDetailsModel extends AbstractAcaptureApiCommunicatorModel {

    /* Properties */
    @JsonProperty("ConnectorTxID1")
    private String connectorTxID1;

    /* Constructor */
    public AcaptureResultDetailsModel() {
    }

    /* Properties getters and setters */
    public String getConnectorTxID1() {
        return connectorTxID1;
    }

    public void setConnectorTxID1(final String connectorTxID1) {
        this.connectorTxID1 = connectorTxID1;
    }

    /* Equals, HashCode and ToString */
    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        final AcaptureResultDetailsModel that = (AcaptureResultDetailsModel) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(connectorTxID1, that.connectorTxID1)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(connectorTxID1)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("connectorTxID1", connectorTxID1)
                .toString();
    }
}
