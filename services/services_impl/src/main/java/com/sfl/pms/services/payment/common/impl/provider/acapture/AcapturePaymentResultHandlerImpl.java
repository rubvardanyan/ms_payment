package com.sfl.pms.services.payment.common.impl.provider.acapture;

import com.sfl.pms.services.payment.common.dto.PaymentResultDto;
import com.sfl.pms.services.payment.common.dto.acapture.AcapturePaymentResultDto;
import com.sfl.pms.services.payment.common.dto.adyen.AdyenPaymentResultDto;
import com.sfl.pms.services.payment.common.impl.provider.PaymentResultHandler;
import com.sfl.pms.services.payment.common.model.PaymentResult;
import com.sfl.pms.services.payment.common.model.acapture.AcapturePaymentResult;
import com.sfl.pms.services.payment.common.model.adyen.AdyenPaymentResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.Nonnull;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 10/1/18
 * Time: 3:59 AM
 */
@Component(value = "acapturePaymentResultHandler")
public class AcapturePaymentResultHandlerImpl implements PaymentResultHandler  {
    private static final Logger LOGGER = LoggerFactory.getLogger(AcapturePaymentResultHandlerImpl.class);

    /* Constructor */
    //TODO: add tests
    public AcapturePaymentResultHandlerImpl() {
    }

    /* Public methods */
    @Override
    public void assertPaymentResultDto(@Nonnull final PaymentResultDto<? extends PaymentResult> paymentResultDto) {
        Assert.notNull(paymentResultDto, "Payment result dto should not be null");
        Assert.isInstanceOf(AcapturePaymentResultDto.class, paymentResultDto, "Payment result dto should be instance of AcapturePaymentResultDto");
        final AcapturePaymentResultDto acapturePaymentResultDto = (AcapturePaymentResultDto) paymentResultDto;
        Assert.notNull(acapturePaymentResultDto.getResultCode(), "Result code should not be null");
        Assert.notNull(acapturePaymentResultDto.getResultDescriptions(), "Result description should not be null");
    }

    @Nonnull
    @Override
    public PaymentResult convertPaymentResultDto(@Nonnull final PaymentResultDto<? extends PaymentResult> paymentResultDto) {
        assertPaymentResultDto(paymentResultDto);
        final AcapturePaymentResultDto acapturePaymentResultDto = (AcapturePaymentResultDto) paymentResultDto;
        // Create payment result
        final AcapturePaymentResult paymentResult = new AcapturePaymentResult();
        acapturePaymentResultDto.updateDomainEntityProperties(paymentResult);
        return paymentResult;
    }
}
