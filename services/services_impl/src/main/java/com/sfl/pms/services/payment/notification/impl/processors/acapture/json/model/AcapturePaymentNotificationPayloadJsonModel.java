package com.sfl.pms.services.payment.notification.impl.processors.acapture.json.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 1/23/19
 * Time: 8:09 PM
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class AcapturePaymentNotificationPayloadJsonModel implements Serializable {
    private static final long serialVersionUID = 3604456832825012689L;

    /* Properties */
    @JsonProperty("id")
    private String id;

    @JsonProperty("paymentType")
    private String paymentType;

    @JsonProperty("paymentBrand")
    private String paymentMethodType;

    @JsonProperty("amount")
    private BigDecimal amount;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("presentationAmount")
    private BigDecimal presentationAmount;

    @JsonProperty("presentationCurrency")
    private String presentationCurrency;

    @JsonProperty("descriptor")
    private String descriptor;

    @JsonProperty("buildNumber")
    private String buildNumber;

    @JsonProperty("timestamp")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss")
    private Date timestamp;

    @JsonProperty("ndc")
    private String ndc;

    @JsonProperty("result")
    private AcaptureNotificationResultJsonModel result;

    @JsonProperty("autentication")
    private AcaptureNotificationAuthenticationJsonModel authentication;

    /* Constructor */
    public AcapturePaymentNotificationPayloadJsonModel() {
    }

    public AcapturePaymentNotificationPayloadJsonModel(final String id, final String paymentType,
                                                       final String paymentMethodType, final BigDecimal amount,
                                                       final String currency, final BigDecimal presentationAmount,
                                                       final String presentationCurrency, final String descriptor,
                                                       final String buildNumber, final Date timestamp,
                                                       final String ndc, final AcaptureNotificationResultJsonModel result,
                                                       final AcaptureNotificationAuthenticationJsonModel authentication) {
        this.id = id;
        this.paymentType = paymentType;
        this.paymentMethodType = paymentMethodType;
        this.amount = amount;
        this.currency = currency;
        this.presentationAmount = presentationAmount;
        this.presentationCurrency = presentationCurrency;
        this.descriptor = descriptor;
        this.buildNumber = buildNumber;
        this.timestamp = timestamp;
        this.ndc = ndc;
        this.result = result;
        this.authentication = authentication;
    }

    /* Properties getters and setters */
    public String getId() {
        return id;
    }

    public void setId(final String id) {
        this.id = id;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(final String paymentType) {
        this.paymentType = paymentType;
    }

    public String getPaymentMethodType() {
        return paymentMethodType;
    }

    public void setPaymentMethodType(final String paymentMethodType) {
        this.paymentMethodType = paymentMethodType;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(final BigDecimal amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(final String currency) {
        this.currency = currency;
    }

    public BigDecimal getPresentationAmount() {
        return presentationAmount;
    }

    public void setPresentationAmount(final BigDecimal presentationAmount) {
        this.presentationAmount = presentationAmount;
    }

    public String getPresentationCurrency() {
        return presentationCurrency;
    }

    public void setPresentationCurrency(final String presentationCurrency) {
        this.presentationCurrency = presentationCurrency;
    }

    public String getDescriptor() {
        return descriptor;
    }

    public void setDescriptor(final String descriptor) {
        this.descriptor = descriptor;
    }

    public String getBuildNumber() {
        return buildNumber;
    }

    public void setBuildNumber(final String buildNumber) {
        this.buildNumber = buildNumber;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(final Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getNdc() {
        return ndc;
    }

    public void setNdc(final String ndc) {
        this.ndc = ndc;
    }

    public AcaptureNotificationResultJsonModel getResult() {
        return result;
    }

    public void setResult(final AcaptureNotificationResultJsonModel result) {
        this.result = result;
    }

    public AcaptureNotificationAuthenticationJsonModel getAuthentication() {
        return authentication;
    }

    public void setAuthentication(final AcaptureNotificationAuthenticationJsonModel authentication) {
        this.authentication = authentication;
    }

    /* Equals, HashCode and ToString */
    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        final AcapturePaymentNotificationPayloadJsonModel that = (AcapturePaymentNotificationPayloadJsonModel) o;

        return new EqualsBuilder()
                .append(id, that.id)
                .append(paymentType, that.paymentType)
                .append(paymentMethodType, that.paymentMethodType)
                .append(amount, that.amount)
                .append(currency, that.currency)
                .append(presentationAmount, that.presentationAmount)
                .append(presentationCurrency, that.presentationCurrency)
                .append(descriptor, that.descriptor)
                .append(buildNumber, that.buildNumber)
                .append(timestamp, that.timestamp)
                .append(ndc, that.ndc)
                .append(result, that.result)
                .append(authentication, that.authentication)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(id)
                .append(paymentType)
                .append(paymentMethodType)
                .append(amount)
                .append(currency)
                .append(presentationAmount)
                .append(presentationCurrency)
                .append(descriptor)
                .append(buildNumber)
                .append(timestamp)
                .append(ndc)
                .append(result)
                .append(authentication)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .appendSuper(super.toString())
                .append("id", id)
                .append("paymentType", paymentType)
                .append("paymentMethodType", paymentMethodType)
                .append("amount", amount)
                .append("currency", currency)
                .append("presentationAmount", presentationAmount)
                .append("presentationCurrency", presentationCurrency)
                .append("descriptor", descriptor)
                .append("buildNumber", buildNumber)
                .append("timestamp", timestamp)
                .append("ndc", ndc)
                .append("result", result)
                .append("authentication", authentication)
                .toString();
    }
}
