package com.sfl.pms.services.payment.common.impl.metadata;

import com.sfl.pms.services.payment.common.dto.metadata.PaymentProviderMetadataDto;
import com.sfl.pms.services.payment.common.model.metadata.PaymentProviderMetadata;

import javax.annotation.Nonnull;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 9/29/18
 * Time: 12:32 AM
 */
public interface PaymentProviderMetadataHandler {

    /**
     * Converts payment provider metadata dto to payment provider metadata
     * @param dto dto
     * @return PaymentProviderMetadata
     */
    @Nonnull
    PaymentProviderMetadata convertPaymentProviderMetadataDto(@Nonnull PaymentProviderMetadataDto dto);
}
