package com.sfl.pms.externalclients.payment.acapture.model.bank;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sfl.pms.externalclients.payment.acapture.model.AbstractAcaptureApiCommunicatorModel;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 10/1/18
 * Time: 3:40 AM
 */
public class AcaptureBankAccountModel extends AbstractAcaptureApiCommunicatorModel {
    private static final long serialVersionUID = -172853769839187666L;

    /* Properties */
    @JsonProperty("holder")
    private String holder;

    @JsonProperty("bankName")
    private String bankName;

    @JsonProperty("number")
    private String number;

    @JsonProperty("iban")
    private String iban;

    @JsonProperty("bankCode")
    private String bankCode;

    @JsonProperty("bic")
    private String bic;

    @JsonProperty("country")
    private String countryCode;

    /* Constructor */
    public AcaptureBankAccountModel() {
    }

    /* Properties getters and setters */
    public String getHolder() {
        return holder;
    }

    public void setHolder(final String holder) {
        this.holder = holder;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(final String bankName) {
        this.bankName = bankName;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(final String number) {
        this.number = number;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(final String iban) {
        this.iban = iban;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(final String bankCode) {
        this.bankCode = bankCode;
    }

    public String getBic() {
        return bic;
    }

    public void setBic(final String bic) {
        this.bic = bic;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(final String countryCode) {
        this.countryCode = countryCode;
    }

    /* Equals, HashCode and ToString */
    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        final AcaptureBankAccountModel that = (AcaptureBankAccountModel) o;

        return new EqualsBuilder()
                .appendSuper(super.equals(o))
                .append(holder, that.holder)
                .append(bankName, that.bankName)
                .append(number, that.number)
                .append(iban, that.iban)
                .append(bankCode, that.bankCode)
                .append(bic, that.bic)
                .append(countryCode, that.countryCode)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .appendSuper(super.hashCode())
                .append(holder)
                .append(bankName)
                .append(number)
                .append(iban)
                .append(bankCode)
                .append(bic)
                .append(countryCode)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .appendSuper(super.toString())
                .append("holder", holder)
                .append("bankName", bankName)
                .append("number", number)
                .append("iban", iban)
                .append("bankCode", bankCode)
                .append("bic", bic)
                .append("countryCode", countryCode)
                .toString();
    }
}
