package com.sfl.pms.services.payment.redirect.dto.redirect.adyen;

import com.sfl.pms.services.payment.provider.model.PaymentProviderType;
import com.sfl.pms.services.payment.redirect.dto.PaymentProviderRedirectResultDto;
import com.sfl.pms.services.payment.redirect.model.adyen.AdyenRedirectResult;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * User: Ruben Dilanyan
 * Company: SFL LLC
 * Date: 7/11/15
 * Time: 11:49 AM
 */
public class AdyenRedirectResultDto extends PaymentProviderRedirectResultDto<AdyenRedirectResult> {

    private static final long serialVersionUID = -6250401111063370168L;

    /* Properties */
    private String authResult;

    private String pspReference;

    private String merchantReference;

    private String skinCode;

    private String merchantSig;

    private String paymentMethod;

    private String shopperLocale;

    private String merchantReturnData;

    public AdyenRedirectResultDto() {
        super(PaymentProviderType.ADYEN);
    }

    public AdyenRedirectResultDto(final String authResult, final String pspReference, final String merchantReference, final String skinCode, final String merchantSig, final String paymentMethod, final String shopperLocale, final String merchantReturnData) {
        super(PaymentProviderType.ADYEN);
        this.authResult = authResult;
        this.pspReference = pspReference;
        this.merchantReference = merchantReference;
        this.skinCode = skinCode;
        this.merchantSig = merchantSig;
        this.paymentMethod = paymentMethod;
        this.shopperLocale = shopperLocale;
        this.merchantReturnData = merchantReturnData;
    }

    /* Properties getters and setters */

    public String getAuthResult() {
        return authResult;
    }

    public void setAuthResult(final String authResult) {
        this.authResult = authResult;
    }

    public String getPspReference() {
        return pspReference;
    }

    public void setPspReference(final String pspReference) {
        this.pspReference = pspReference;
    }

    public String getMerchantReference() {
        return merchantReference;
    }

    public void setMerchantReference(final String merchantReference) {
        this.merchantReference = merchantReference;
    }

    public String getSkinCode() {
        return skinCode;
    }

    public void setSkinCode(final String skinCode) {
        this.skinCode = skinCode;
    }

    public String getMerchantSig() {
        return merchantSig;
    }

    public void setMerchantSig(final String merchantSig) {
        this.merchantSig = merchantSig;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(final String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getShopperLocale() {
        return shopperLocale;
    }

    public void setShopperLocale(final String shopperLocale) {
        this.shopperLocale = shopperLocale;
    }

    public String getMerchantReturnData() {
        return merchantReturnData;
    }

    public void setMerchantReturnData(final String merchantReturnData) {
        this.merchantReturnData = merchantReturnData;
    }

    /* Public interface methods */

    @Override
    public void updateDomainEntityProperties(final AdyenRedirectResult redirectResult) {
        super.updateDomainEntityProperties(redirectResult);
        redirectResult.setSkinCode(getSkinCode());
        redirectResult.setShopperLocale(getShopperLocale());
        redirectResult.setPspReference(getPspReference());
        redirectResult.setPaymentMethod(getPaymentMethod());
        redirectResult.setAuthResult(getAuthResult());
        redirectResult.setMerchantReference(getMerchantReference());
        redirectResult.setMerchantReturnData(getMerchantReturnData());
        redirectResult.setMerchantSig(getMerchantSig());
    }

    /* Equals, HashCode and ToString */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof AdyenRedirectResultDto)) {
            return false;
        }
        final AdyenRedirectResultDto that = (AdyenRedirectResultDto) o;
        final EqualsBuilder builder = new EqualsBuilder();
        builder.appendSuper(super.equals(that));
        builder.append(this.getAuthResult(), that.getAuthResult());
        builder.append(this.getPspReference(), that.getPspReference());
        builder.append(this.getMerchantReference(), that.getMerchantReference());
        builder.append(this.getSkinCode(), that.getSkinCode());
        builder.append(this.getMerchantSig(), that.getMerchantSig());
        builder.append(this.getPaymentMethod(), that.getPaymentMethod());
        builder.append(this.getShopperLocale(), that.getShopperLocale());
        builder.append(this.getMerchantReturnData(), that.getMerchantReturnData());
        return builder.isEquals();
    }

    @Override
    public int hashCode() {
        final HashCodeBuilder builder = new HashCodeBuilder();
        builder.appendSuper(super.hashCode());
        builder.append(this.getAuthResult());
        builder.append(this.getPspReference());
        builder.append(this.getMerchantReference());
        builder.append(this.getSkinCode());
        builder.append(this.getMerchantSig());
        builder.append(this.getPaymentMethod());
        builder.append(this.getShopperLocale());
        builder.append(this.getMerchantReturnData());
        return builder.build();
    }


    @Override
    public String toString() {
        final ToStringBuilder builder = new ToStringBuilder(this);
        builder.appendSuper(super.toString());
        builder.append("authResult", this.getAuthResult());
        builder.append("pspReference", this.getPspReference());
        builder.append("merchantReference", this.getMerchantReference());
        builder.append("skinCode", this.getSkinCode());
        builder.append("merchantSig", this.getMerchantSig());
        builder.append("paymentMethod", this.getPaymentMethod());
        builder.append("shopperLocale", this.getShopperLocale());
        builder.append("merchantReturnData", this.getMerchantReturnData());
        return builder.build();
    }
}
