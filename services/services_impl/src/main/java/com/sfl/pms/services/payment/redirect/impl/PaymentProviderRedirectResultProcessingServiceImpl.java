package com.sfl.pms.services.payment.redirect.impl;

import com.sfl.pms.persistence.utility.PersistenceUtilityService;
import com.sfl.pms.services.common.exception.ServicesRuntimeException;
import com.sfl.pms.services.payment.provider.exception.UnknownPaymentProviderTypeException;
import com.sfl.pms.services.payment.provider.model.PaymentProviderType;
import com.sfl.pms.services.payment.redirect.PaymentProviderRedirectResultProcessingService;
import com.sfl.pms.services.payment.redirect.PaymentProviderRedirectResultService;
import com.sfl.pms.services.payment.redirect.impl.acapture.AcaptureRedirectResultProcessor;
import com.sfl.pms.services.payment.redirect.impl.adyen.AdyenRedirectResultProcessor;
import com.sfl.pms.services.payment.redirect.model.PaymentProviderRedirectResult;
import com.sfl.pms.services.payment.redirect.model.PaymentProviderRedirectResultState;
import com.sfl.pms.services.util.mutable.MutableHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * User: Ruben Dilanyan
 * Company: SFL LLC
 * Date: 7/12/15
 * Time: 3:50 PM
 */
@Service
public class PaymentProviderRedirectResultProcessingServiceImpl implements PaymentProviderRedirectResultProcessingService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentProviderRedirectResultProcessingServiceImpl.class);

    /* Dependencies */
    @Autowired
    private PaymentProviderRedirectResultService paymentProviderRedirectResultService;

    @Autowired
    private PersistenceUtilityService persistenceUtilityService;

    @Autowired
    private AdyenRedirectResultProcessor adyenRedirectResultProcessor;

    @Autowired
    private AcaptureRedirectResultProcessor acaptureRedirectResultProcessor;

    /* Constructors */
    public PaymentProviderRedirectResultProcessingServiceImpl() {
        LOGGER.debug("Initializing payment provider redirect result processing service");
    }

    @Override
    public void processPaymentProviderRedirectResult(@Nonnull final Long paymentProviderRedirectResultId) {
        Assert.notNull(paymentProviderRedirectResultId, "Payment provider redirect result id should not be null");
        LOGGER.debug("Processing payment provider redirect result with id - {}", paymentProviderRedirectResultId);
        updateRedirectResultState(paymentProviderRedirectResultId, PaymentProviderRedirectResultState.PROCESSING, new LinkedHashSet<>(Arrays.asList(PaymentProviderRedirectResultState.CREATED, PaymentProviderRedirectResultState.FAILED)));
        try {
            // Load payment provider redirect result
            final PaymentProviderRedirectResult redirectResult = getPaymentProviderRedirectResult(paymentProviderRedirectResultId);
            final PaymentProviderType paymentProviderType = redirectResult.getType();
            final PaymentProviderRedirectResultProcessor redirectResultProcessor = getPaymentProviderRedirectResultProcessor(paymentProviderType);
            // Process payment provider redirect result
            final PaymentProviderRedirectResultState redirectResultState = redirectResultProcessor.processPaymentProviderRedirectResult(redirectResult);
            // Mark redirect result as processed
            updateRedirectResultState(paymentProviderRedirectResultId, redirectResultState, new LinkedHashSet<>());
        } catch (final Exception ex) {
            final String errorMessage = "Error occurred while processing payment provider redirect result for id - " + paymentProviderRedirectResultId;
            LOGGER.error(errorMessage, ex);
            updateRedirectResultState(paymentProviderRedirectResultId, PaymentProviderRedirectResultState.FAILED, new LinkedHashSet<>());
            throw new ServicesRuntimeException(errorMessage, ex);
        }
    }

    /* Utility methods */
    private PaymentProviderRedirectResult getPaymentProviderRedirectResult(final Long id) {
        final MutableHolder<PaymentProviderRedirectResult> mutableHolder = new MutableHolder<>(null);
        persistenceUtilityService.runInNewTransaction(() -> {
            PaymentProviderRedirectResult redirectResult = paymentProviderRedirectResultService.getPaymentProviderRedirectResultById(id);
            redirectResult = persistenceUtilityService.initializeAndUnProxy(redirectResult);
            mutableHolder.setValue(redirectResult);
        });
        return mutableHolder.getValue();
    }

    private void updateRedirectResultState(final Long redirectResultId, final PaymentProviderRedirectResultState state, final Set<PaymentProviderRedirectResultState> allowedStates) {
        persistenceUtilityService.runInNewTransaction(() -> {
            paymentProviderRedirectResultService.updatePaymentProviderRedirectResultState(redirectResultId, state, allowedStates);
        });
    }

    private PaymentProviderRedirectResultProcessor getPaymentProviderRedirectResultProcessor(final PaymentProviderType paymentProviderType) {
        switch (paymentProviderType) {
            case ADYEN:
                return adyenRedirectResultProcessor;
            case ACAPTURE:
                return acaptureRedirectResultProcessor;
            default: {
                throw new UnknownPaymentProviderTypeException(paymentProviderType);
            }
        }
    }

    /* Properties getters and setters */
    public PaymentProviderRedirectResultService getPaymentProviderRedirectResultService() {
        return paymentProviderRedirectResultService;
    }

    public void setPaymentProviderRedirectResultService(final PaymentProviderRedirectResultService paymentProviderRedirectResultService) {
        this.paymentProviderRedirectResultService = paymentProviderRedirectResultService;
    }

    public PersistenceUtilityService getPersistenceUtilityService() {
        return persistenceUtilityService;
    }

    public void setPersistenceUtilityService(final PersistenceUtilityService persistenceUtilityService) {
        this.persistenceUtilityService = persistenceUtilityService;
    }

    public AdyenRedirectResultProcessor getAdyenRedirectResultProcessor() {
        return adyenRedirectResultProcessor;
    }

    public void setAdyenRedirectResultProcessor(final AdyenRedirectResultProcessor adyenRedirectResultProcessor) {
        this.adyenRedirectResultProcessor = adyenRedirectResultProcessor;
    }
}
