package com.sfl.pms.services.payment.notification.dto.acapture;

import com.sfl.pms.services.payment.notification.dto.PaymentProviderNotificationDto;
import com.sfl.pms.services.payment.notification.model.acapture.AcapturePaymentProviderNotification;
import com.sfl.pms.services.payment.provider.model.PaymentProviderType;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 1/23/19
 * Time: 8:41 PM
 */
public class AcapturePaymentProviderNotificationDto extends PaymentProviderNotificationDto<AcapturePaymentProviderNotification> {
    private static final long serialVersionUID = 1391760095321057087L;

    /* Properties */
    private String notificationId;

    private String resultCode;

    private String resultDescription;

    private String ndc;


    /* Constructor */
    public AcapturePaymentProviderNotificationDto() {
        super(PaymentProviderType.ACAPTURE);
    }

    public AcapturePaymentProviderNotificationDto(final String rawContent,final String clientIpAddress,
                                                  final String notificationId, final String resultCode,
                                                  final String resultDescription, final String ndc) {
        super(PaymentProviderType.ACAPTURE, rawContent, clientIpAddress);
        this.notificationId = notificationId;
        this.resultCode = resultCode;
        this.resultDescription = resultDescription;
        this.ndc = ndc;
    }

    /* Public methods */

    @Override
    public void updateDomainEntityProperties(final AcapturePaymentProviderNotification notification) {
        super.updateDomainEntityProperties(notification);
        notification.setNotificationId(getNotificationId());
        notification.setNdc(getNdc());
        notification.setResultCode(getResultCode());
        notification.setResultDescription(getResultDescription());
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

        final AcapturePaymentProviderNotificationDto that = (AcapturePaymentProviderNotificationDto) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(notificationId, that.notificationId)
                .append(resultCode, that.resultCode)
                .append(resultDescription, that.resultDescription)
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
                .append("ndc", ndc)
                .toString();
    }
}
