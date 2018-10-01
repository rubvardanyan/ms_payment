package com.sfl.pms.externalclients.payment.acapture.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sfl.pms.externalclients.payment.acapture.model.bank.AcaptureBankAccountModel;
import com.sfl.pms.externalclients.payment.acapture.model.customer.AcaptureCustomerModel;
import com.sfl.pms.externalclients.payment.acapture.model.result.AcaptureResultDetailsModel;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.math.BigDecimal;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 10/1/18
 * Time: 3:16 AM
 */
public class CheckPaymentStatusResponse extends AbstractAcaptureResponseModel {

    /* Properties */
    @JsonProperty("paymentType")
    private String paymentType;

    @JsonProperty("paymentBrand")
    private String paymentBrand;

    @JsonProperty("amount")
    private BigDecimal amount;

    @JsonProperty("currency")
    private String currency;

    @JsonProperty("description")
    private String description;

    @JsonProperty("merchantInvoiceId")
    private String merchantInvoiceId;

    @JsonProperty("customer")
    private AcaptureCustomerModel customerModel;

    @JsonProperty("bankAccount")
    private AcaptureBankAccountModel bankAccountModel;

    @JsonProperty("resultDetails")
    private AcaptureResultDetailsModel reusltDetails;

    /* Constructor */
    public CheckPaymentStatusResponse() {
    }

    /* Properties getters and setters */
    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(final String paymentType) {
        this.paymentType = paymentType;
    }

    public String getPaymentBrand() {
        return paymentBrand;
    }

    public void setPaymentBrand(final String paymentBrand) {
        this.paymentBrand = paymentBrand;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getMerchantInvoiceId() {
        return merchantInvoiceId;
    }

    public void setMerchantInvoiceId(final String merchantInvoiceId) {
        this.merchantInvoiceId = merchantInvoiceId;
    }

    public AcaptureCustomerModel getCustomerModel() {
        return customerModel;
    }

    public void setCustomerModel(final AcaptureCustomerModel customerModel) {
        this.customerModel = customerModel;
    }

    public AcaptureBankAccountModel getBankAccountModel() {
        return bankAccountModel;
    }

    public void setBankAccountModel(final AcaptureBankAccountModel bankAccountModel) {
        this.bankAccountModel = bankAccountModel;
    }

    public AcaptureResultDetailsModel getReusltDetails() {
        return reusltDetails;
    }

    public void setReusltDetails(final AcaptureResultDetailsModel reusltDetails) {
        this.reusltDetails = reusltDetails;
    }

    /* Equals, HashCode and ToString */
    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        final CheckPaymentStatusResponse that = (CheckPaymentStatusResponse) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(paymentType, that.paymentType)
                .append(paymentBrand, that.paymentBrand)
                .append(amount, that.amount)
                .append(currency, that.currency)
                .append(description, that.description)
                .append(merchantInvoiceId, that.merchantInvoiceId)
                .append(customerModel, that.customerModel)
                .append(bankAccountModel, that.bankAccountModel)
                .append(reusltDetails, that.reusltDetails)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(paymentType)
                .append(paymentBrand)
                .append(amount)
                .append(currency)
                .append(description)
                .append(merchantInvoiceId)
                .append(customerModel)
                .append(bankAccountModel)
                .append(reusltDetails)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .appendSuper(super.toString())
                .append("paymentType", paymentType)
                .append("paymentBrand", paymentBrand)
                .append("amount", amount)
                .append("currency", currency)
                .append("description", description)
                .append("merchantInvoiceId", merchantInvoiceId)
                .append("customerModel", customerModel)
                .append("bankAccountModel", bankAccountModel)
                .append("reusltDetails", reusltDetails)
                .toString();
    }
}
