package com.sfl.pms.externalclients.payment.acapture.model.authentication;

import com.sfl.pms.externalclients.payment.acapture.model.AbstractAcaptureApiCommunicatorModel;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 9/30/18
 * Time: 7:50 PM
 */
public class AcaptureAuthenticationModel extends AbstractAcaptureApiCommunicatorModel {
    private static final long serialVersionUID = 3248300656026935634L;

    /* Properties */
    private String entityId;

    /* Constructor */
    public AcaptureAuthenticationModel() {
    }

    public AcaptureAuthenticationModel(final String entityId) {
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

        final AcaptureAuthenticationModel that = (AcaptureAuthenticationModel) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(entityId, that.entityId)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(entityId)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("entityId", entityId)
                .toString();
    }
}
