package com.sfl.pms.services.payment.common.impl.metadata;

import com.sfl.pms.services.payment.common.dto.acapture.AcapturePaymentProviderMetadataDto;
import com.sfl.pms.services.payment.common.dto.metadata.PaymentProviderMetadataDto;
import com.sfl.pms.services.payment.common.model.acapture.AcapturePaymentProviderMetadata;
import com.sfl.pms.services.payment.common.model.metadata.PaymentProviderMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.Nonnull;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 9/29/18
 * Time: 12:35 AM
 */
@Component
public class AcapturePaymentProviderMetadataHandlerImpl implements AcapturePaymentProviderMetadataHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(AcapturePaymentProviderMetadataHandlerImpl.class);

    /* Constructor */
    public AcapturePaymentProviderMetadataHandlerImpl() {
        LOGGER.debug("Initializing acapture payment provider metadata handler");
    }

    /* Public methods */
    @Nonnull
    @Override
    public PaymentProviderMetadata convertPaymentProviderMetadataDto(@Nonnull final PaymentProviderMetadataDto dto) {
        Assert.notNull(dto, "Payment provider metadata dto should not be null");
        final AcapturePaymentProviderMetadata acapturePaymentProviderMetadata = new AcapturePaymentProviderMetadata();
        Assert.isInstanceOf(AcapturePaymentProviderMetadataDto.class, dto, "Payment provider metadata dto should be of type AcapturePaymentProviderMetadataDto");
        final AcapturePaymentProviderMetadataDto acapturePaymentProviderMetadataDto = (AcapturePaymentProviderMetadataDto) dto;
        acapturePaymentProviderMetadataDto.updateDomainEntityProperties(acapturePaymentProviderMetadata);
        return acapturePaymentProviderMetadata;
    }
}
