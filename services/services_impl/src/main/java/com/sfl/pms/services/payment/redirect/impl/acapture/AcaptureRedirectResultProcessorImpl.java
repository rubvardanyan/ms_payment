package com.sfl.pms.services.payment.redirect.impl.acapture;

import com.sfl.pms.persistence.utility.PersistenceUtilityService;
import com.sfl.pms.services.payment.common.PaymentService;
import com.sfl.pms.services.payment.common.dto.acapture.AcapturePaymentResultDto;
import com.sfl.pms.services.payment.common.impl.status.PaymentResultStatusMapper;
import com.sfl.pms.services.payment.common.model.Payment;
import com.sfl.pms.services.payment.common.model.PaymentResultStatus;
import com.sfl.pms.services.payment.common.model.PaymentState;
import com.sfl.pms.services.payment.common.model.acapture.AcapturePaymentProviderMetadata;
import com.sfl.pms.services.payment.metadata.acapture.AcapturePaymentProviderMetadataService;
import com.sfl.pms.services.payment.processing.impl.PaymentResultProcessor;
import com.sfl.pms.services.payment.provider.impl.acapture.AcapturePaymentProviderIntegrationService;
import com.sfl.pms.services.payment.redirect.acapture.AcaptureRedirectResultService;
import com.sfl.pms.services.payment.redirect.model.PaymentProviderRedirectResult;
import com.sfl.pms.services.payment.redirect.model.PaymentProviderRedirectResultState;
import com.sfl.pms.services.payment.redirect.model.acapture.AcaptureRedirectResult;
import com.sfl.pms.services.util.mutable.MutableHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.Nonnull;
import javax.persistence.EntityNotFoundException;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 10/1/18
 * Time: 2:19 AM
 */
@Component
public class AcaptureRedirectResultProcessorImpl implements AcaptureRedirectResultProcessor {
    private final static Logger LOGGER = LoggerFactory.getLogger(AcaptureRedirectResultProcessorImpl.class);

    /* Dependencies */
    @Autowired
    private AcapturePaymentProviderMetadataService acapturePaymentProviderMetadataService;

    @Autowired
    private AcaptureRedirectResultService acaptureRedirectResultService;

    @Autowired
    private PersistenceUtilityService persistenceUtilityService;

    @Autowired
    private AcapturePaymentProviderIntegrationService acapturePaymentProviderIntegrationService;

    @Autowired
    private PaymentResultStatusMapper paymentResultStatusMapper;

    @Autowired
    private PaymentResultProcessor paymentResultProcessor;

    @Autowired
    private PaymentService paymentService;

    /* Constructor */
    //TODO: add tests
    public AcaptureRedirectResultProcessorImpl() {
        LOGGER.debug("Initializing acapture redirect result processor");
    }

    /* Public methods */
    @Override
    public PaymentProviderRedirectResultState processPaymentProviderRedirectResult(@Nonnull final PaymentProviderRedirectResult paymentProviderRedirectResult) {
        Assert.notNull(paymentProviderRedirectResult, "Payment provider redirect result should not be null");
        Assert.isInstanceOf(AcaptureRedirectResult.class, paymentProviderRedirectResult, "Payment provider redirect result should be intance of AcaptureRedirectResult");
        AcaptureRedirectResult acaptureRedirectResult = (AcaptureRedirectResult)paymentProviderRedirectResult;
        // Grab payment for redirect result
        final Payment payment = getPaymentForRedirectResult(acaptureRedirectResult);
        if(payment == null) {
            LOGGER.warn("Payment lookup failed for redirect result - {}", paymentProviderRedirectResult);
            return PaymentProviderRedirectResultState.PAYMENT_LOOKUP_FAILED;
        }
        // Associate payment with redirect result
        acaptureRedirectResult = updatePaymentForRedirectResult(acaptureRedirectResult.getId(), payment.getId());
        // Create and process payment result DTO
        final AcapturePaymentResultDto acapturePaymentResultDto = acapturePaymentProviderIntegrationService.checkPaymentStatusForRedirectResult(acaptureRedirectResult);
        final PaymentResultStatus paymentStatus = paymentResultStatusMapper.getPaymentResultStatusForAcapturePaymentStatus(acapturePaymentResultDto.getResultCode());
        acapturePaymentResultDto.setStatus(paymentResultStatusMapper.getPaymentResultStatusForAcapturePaymentStatus(acapturePaymentResultDto.getResultCode()));
        paymentResultProcessor.processPaymentResult(payment.getId(), null, acaptureRedirectResult.getId(), acapturePaymentResultDto);
        final Payment updatedPayment = paymentService.getPaymentById(payment.getId());
        if(updatedPayment.getLastState().equals(PaymentState.PAID)) {
            if(updatedPayment.getPaymentProcessingChannel().getPaymentMethodTypeIfDefined().getAcapturePaymentMethodType().isCaptureRequired()){
                acapturePaymentProviderIntegrationService.submitCapture(updatedPayment.getId());
            }
        }
        return PaymentProviderRedirectResultState.PROCESSED;
    }

    /* Utility methods */
    private Payment getPaymentForRedirectResult(final AcaptureRedirectResult redirectResult) {
        final String checkoutId = redirectResult.getCheckoutId();
        try {
            final AcapturePaymentProviderMetadata acapturePaymentProviderMetadata = acapturePaymentProviderMetadataService.getAcapturePaymentProviderMetadataByCheckoutId(checkoutId);
            return acapturePaymentProviderMetadata.getPaymentProcessingChannel().getPayment();
        } catch (EntityNotFoundException ex) {
            LOGGER.error("Payment lookup failed for redirect result - " + redirectResult, ex);
            return null;
        }
    }

    private AcaptureRedirectResult updatePaymentForRedirectResult(final Long redirectResultId, final Long paymentId) {
        final MutableHolder<AcaptureRedirectResult> mutableHolder = new MutableHolder<>(null);
        persistenceUtilityService.runInNewTransaction(() -> {
            mutableHolder.setValue(acaptureRedirectResultService.updatePaymentForRedirectResult(redirectResultId, paymentId));
        });
        return mutableHolder.getValue();
    }
}
