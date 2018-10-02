package com.sfl.pms.services.payment.redirect.impl.adyen;

import com.sfl.pms.externalclients.payment.adyen.model.datatypes.AdyenPaymentStatus;
import com.sfl.pms.persistence.utility.PersistenceUtilityService;
import com.sfl.pms.services.payment.common.PaymentService;
import com.sfl.pms.services.payment.common.dto.adyen.AdyenPaymentResultDto;
import com.sfl.pms.services.payment.common.exception.PaymentNotFoundForUuIdException;
import com.sfl.pms.services.payment.common.impl.status.PaymentResultStatusMapper;
import com.sfl.pms.services.payment.common.model.Payment;
import com.sfl.pms.services.payment.common.model.PaymentResultStatus;
import com.sfl.pms.services.payment.method.model.PaymentMethodType;
import com.sfl.pms.services.payment.method.model.adyen.AdyenPaymentMethodType;
import com.sfl.pms.services.payment.processing.impl.PaymentResultProcessor;
import com.sfl.pms.services.payment.provider.adyen.AdyenPaymentProviderIntegrationService;
import com.sfl.pms.services.payment.redirect.adyen.AdyenRedirectResultService;
import com.sfl.pms.services.payment.redirect.dto.redirect.adyen.AdyenRedirectResultDto;
import com.sfl.pms.services.payment.redirect.model.PaymentProviderRedirectResult;
import com.sfl.pms.services.payment.redirect.model.PaymentProviderRedirectResultState;
import com.sfl.pms.services.payment.redirect.model.adyen.AdyenRedirectResult;
import com.sfl.pms.services.util.mutable.MutableHolder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.Nonnull;

/**
 * User: Ruben Dilanyan
 * Company: SFL LLC
 * Date: 7/13/15
 * Time: 10:40 AM
 */
@Component
public class AdyenRedirectResultProcessorImpl implements AdyenRedirectResultProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdyenRedirectResultProcessorImpl.class);

    /* Dependencies */
    @Autowired
    private PaymentResultProcessor paymentResultProcessor;

    @Autowired
    private AdyenPaymentProviderIntegrationService adyenPaymentProviderIntegrationService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private PaymentResultStatusMapper paymentResultStatusMapper;

    @Autowired
    private AdyenRedirectResultService adyenRedirectResultService;

    @Autowired
    private PersistenceUtilityService persistenceUtilityService;

    /* Constructors */
    public AdyenRedirectResultProcessorImpl() {
        LOGGER.debug("Initializing Adyen redirect result processor");
    }

    @Override
    public PaymentProviderRedirectResultState processPaymentProviderRedirectResult(@Nonnull final PaymentProviderRedirectResult paymentProviderRedirectResult) {
        Assert.notNull(paymentProviderRedirectResult, "Payment provider redirect result should not be null");
        Assert.isInstanceOf(AdyenRedirectResult.class, paymentProviderRedirectResult, "Redirect result should be type of Adyen redirect result");
        AdyenRedirectResult adyenRedirectResult = (AdyenRedirectResult) paymentProviderRedirectResult;
        final AdyenRedirectResultDto adyenRedirectResultDto = new AdyenRedirectResultDto(adyenRedirectResult.getAuthResult(), adyenRedirectResult.getPspReference(), adyenRedirectResult.getMerchantReference(), adyenRedirectResult.getSkinCode(), adyenRedirectResult.getMerchantSig(), adyenRedirectResult.getPaymentMethod(), adyenRedirectResult.getShopperLocale(), adyenRedirectResult.getMerchantReturnData());
        // Verify signature
        final boolean isSignatureValid = adyenPaymentProviderIntegrationService.verifySignatureForAdyenRedirectResult(adyenRedirectResultDto);
        if (!isSignatureValid) {
            LOGGER.warn("Adyen signature verification failed for redirect result DTO - {}", adyenRedirectResultDto);
            return PaymentProviderRedirectResultState.SIGNATURE_VERIFICATION_FAILED;
        }
        // Grab payment for redirect result
        final Payment payment = getPaymentForRedirectResult(adyenRedirectResult);
        if (payment == null) {
            LOGGER.warn("Payment lookup failed for redirect result - {}", adyenRedirectResult);
            return PaymentProviderRedirectResultState.PAYMENT_LOOKUP_FAILED;
        }
        // Check if payment method has to be updated
        if (payment.getConfirmedPaymentMethodType() == null && StringUtils.isNotBlank(adyenRedirectResult.getPaymentMethod())) {
            updateConfirmedPaymentMethod(adyenRedirectResult, payment);
        }
        // Associate payment with redirect result
        adyenRedirectResult = updatePaymentForRedirectResult(adyenRedirectResult.getId(), payment.getId());
        // Create and process payment result DTO
        final AdyenPaymentResultDto paymentResultDto = convertRedirectResultToPaymentResult(adyenRedirectResultDto);
        paymentResultProcessor.processPaymentResult(payment.getId(), null, adyenRedirectResult.getId(), paymentResultDto);
        // Build adyen
        return PaymentProviderRedirectResultState.PROCESSED;
    }

    /* Utility methods */
    private Payment updateConfirmedPaymentMethod(final AdyenRedirectResult adyenRedirectResult, final Payment payment) {
        final MutableHolder<Payment> paymentMutableHolder = new MutableHolder<>(payment);
        persistenceUtilityService.runInNewTransaction(() -> {
            final AdyenPaymentMethodType adyenPaymentMethodType = AdyenPaymentMethodType.getAdyenPaymentMethodTypeForCode(adyenRedirectResult.getPaymentMethod());
            final PaymentMethodType paymentMethodType = PaymentMethodType.getPaymentMethodTypeForAdyenPaymentMethod(adyenPaymentMethodType);
            final Payment updateResult = paymentService.updateConfirmedPaymentMethodType(payment.getId(), paymentMethodType);
            paymentMutableHolder.setValue(persistenceUtilityService.initializeAndUnProxy(updateResult));
        });
        return paymentMutableHolder.getValue();
    }

    private AdyenRedirectResult updatePaymentForRedirectResult(final Long redirectResultId, final Long paymentId) {
        final MutableHolder<AdyenRedirectResult> mutableHolder = new MutableHolder<>(null);
        persistenceUtilityService.runInNewTransaction(() -> {
            mutableHolder.setValue(adyenRedirectResultService.updatePaymentForRedirectResult(redirectResultId, paymentId));
        });
        return mutableHolder.getValue();
    }

    private AdyenPaymentResultDto convertRedirectResultToPaymentResult(final AdyenRedirectResultDto adyenRedirectResultDto) {
        PaymentResultStatus paymentResultStatus = null;
        AdyenPaymentStatus adyenPaymentStatus = null;
        if (StringUtils.isNotBlank(adyenRedirectResultDto.getAuthResult())) {
            adyenPaymentStatus = getAdyenPaymentStatusForRedirectAuthResult(adyenRedirectResultDto.getAuthResult());
            if (adyenPaymentStatus != null) {
                paymentResultStatus = paymentResultStatusMapper.getPaymentResultStatusForAdyenPaymentStatus(adyenPaymentStatus);
            }
        }
        if (paymentResultStatus == null) {
            paymentResultStatus = PaymentResultStatus.FAILED;
        }
        final AdyenPaymentResultDto adyenPaymentResultDto = new AdyenPaymentResultDto(paymentResultStatus, null, adyenRedirectResultDto.getPspReference(), adyenPaymentStatus != null ? adyenPaymentStatus.getResult() : null, null);
        LOGGER.debug("Converted Adyen redirect result - {} into payment result - {}", adyenRedirectResultDto, adyenPaymentResultDto);
        return adyenPaymentResultDto;
    }

    private AdyenPaymentStatus getAdyenPaymentStatusForRedirectAuthResult(final String authResult) {
        AdyenPaymentStatus adyenPaymentStatus = null;
        try {
            adyenPaymentStatus = AdyenPaymentStatus.fromString(authResult);
        } catch (Exception ex) {
            // Only log, do not fail
            LOGGER.error("Error occurred while trying to convert redirect result auth result - " + authResult + " to Adyen payment status.", ex);
        }
        return adyenPaymentStatus;
    }

    private Payment getPaymentForRedirectResult(final AdyenRedirectResult adyenRedirectResult) {
        try {
            return paymentService.getPaymentByUuId(adyenRedirectResult.getMerchantReference());
        } catch (final PaymentNotFoundForUuIdException ex) {
            // Do not fail
            LOGGER.error("Payment lookup failed for redirect result - " + adyenRedirectResult, ex);
            return null;
        }
    }

    /* Properties getters and setters */
    public PaymentResultProcessor getPaymentResultProcessor() {
        return paymentResultProcessor;
    }

    public void setPaymentResultProcessor(final PaymentResultProcessor paymentResultProcessor) {
        this.paymentResultProcessor = paymentResultProcessor;
    }

    public AdyenPaymentProviderIntegrationService getAdyenPaymentProviderIntegrationService() {
        return adyenPaymentProviderIntegrationService;
    }

    public void setAdyenPaymentProviderIntegrationService(final AdyenPaymentProviderIntegrationService adyenPaymentProviderIntegrationService) {
        this.adyenPaymentProviderIntegrationService = adyenPaymentProviderIntegrationService;
    }

    public PaymentService getPaymentService() {
        return paymentService;
    }

    public void setPaymentService(final PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    public PaymentResultStatusMapper getPaymentResultStatusMapper() {
        return paymentResultStatusMapper;
    }

    public void setPaymentResultStatusMapper(final PaymentResultStatusMapper paymentResultStatusMapper) {
        this.paymentResultStatusMapper = paymentResultStatusMapper;
    }

    public AdyenRedirectResultService getAdyenRedirectResultService() {
        return adyenRedirectResultService;
    }

    public void setAdyenRedirectResultService(final AdyenRedirectResultService adyenRedirectResultService) {
        this.adyenRedirectResultService = adyenRedirectResultService;
    }
}
