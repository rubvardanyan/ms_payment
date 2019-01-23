package com.sfl.pms.services.payment.notification.model.acapture;

import com.sfl.pms.services.payment.notification.model.PaymentProviderNotification;
import com.sfl.pms.services.payment.provider.model.PaymentProviderType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 1/23/19
 * Time: 8:34 PM
 */
@Entity
@Table(name = "payment_provider_notification_acapture")
@DiscriminatorValue("ACAPTURE")
public class AcapturePaymentProviderNotification extends PaymentProviderNotification {
    private static final long serialVersionUID = -8298520519773902038L;

    /* Properties */
    @Column(name = "notification_id", nullable = false)
    private String notificationId;

    @Column(name = "result_code", nullable = false)
    private String resultCode;

    @Column(name = "result_description", nullable = false)
    private String resultDescription;

    @Column(name = "build_number", nullable = false)
    private String buildNumber;

    @Column(name = "ndc", nullable = false)
    private String ndc;

    /* Constructor */
    public AcapturePaymentProviderNotification() {
        setType(PaymentProviderType.ACAPTURE);
    }

    public AcapturePaymentProviderNotification(final boolean generateUuId) {
        super(generateUuId);
        setType(PaymentProviderType.ACAPTURE);
    }

    /* Properties getters and setters */
    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(final String notificationId) {
        this.notificationId = notificationId;
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(final String resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultDescription() {
        return resultDescription;
    }

    public void setResultDescription(final String resultDescription) {
        this.resultDescription = resultDescription;
    }

    public String getBuildNumber() {
        return buildNumber;
    }

    public void setBuildNumber(final String buildNumber) {
        this.buildNumber = buildNumber;
    }

    public String getNdc() {
        return ndc;
    }

    public void setNdc(final String ndc) {
        this.ndc = ndc;
    }

    /* Equals, HashCode and ToString */
    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        final AcapturePaymentProviderNotification that = (AcapturePaymentProviderNotification) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(notificationId, that.notificationId)
                .append(resultCode, that.resultCode)
                .append(resultDescription, that.resultDescription)
                .append(buildNumber, that.buildNumber)
                .append(ndc, that.ndc)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(notificationId)
                .append(resultCode)
                .append(resultDescription)
                .append(buildNumber)
                .append(ndc)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .appendSuper(super.toString())
                .append("notificationId", notificationId)
                .append("resultCode", resultCode)
                .append("resultDescription", resultDescription)
                .append("buildNumber", buildNumber)
                .append("ndc", ndc)
                .toString();
    }
}
