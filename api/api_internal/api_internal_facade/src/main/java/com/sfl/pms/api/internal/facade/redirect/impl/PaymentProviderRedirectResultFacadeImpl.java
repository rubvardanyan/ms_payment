package com.sfl.pms.api.internal.facade.redirect.impl;

import com.sfl.pms.api.internal.facade.redirect.PaymentProviderRedirectResultFacade;
import com.sfl.pms.core.api.internal.model.common.result.ErrorResponseModel;
import com.sfl.pms.core.api.internal.model.common.result.ResultResponseModel;
import com.sfl.pms.core.api.internal.model.redirect.PaymentProviderRedirectResultStateClientType;
import com.sfl.pms.core.api.internal.model.redirect.request.CreateAcaptureRedirectResultRequest;
import com.sfl.pms.core.api.internal.model.redirect.request.CreateAdyenRedirectResultRequest;
import com.sfl.pms.core.api.internal.model.redirect.request.GetPaymentProviderRedirectResultStatusRequest;
import com.sfl.pms.core.api.internal.model.redirect.response.CreateAcaptureRedirectResultResponse;
import com.sfl.pms.core.api.internal.model.redirect.response.CreateAdyenRedirectResultResponse;
import com.sfl.pms.core.api.internal.model.redirect.response.GetPaymentProviderRedirectResultStatusResponse;
import com.sfl.pms.services.payment.common.model.Payment;
import com.sfl.pms.services.payment.redirect.PaymentProviderRedirectResultService;
import com.sfl.pms.services.payment.redirect.acapture.AcaptureRedirectResultService;
import com.sfl.pms.services.payment.redirect.adyen.AdyenRedirectResultService;
import com.sfl.pms.services.payment.redirect.dto.redirect.acapture.AcaptureRedirectResultDto;
import com.sfl.pms.services.payment.redirect.dto.redirect.adyen.AdyenRedirectResultDto;
import com.sfl.pms.services.payment.redirect.event.StartPaymentProviderRedirectResultProcessingEvent;
import com.sfl.pms.services.payment.redirect.model.PaymentProviderRedirectResult;
import com.sfl.pms.services.payment.redirect.model.PaymentProviderRedirectResultState;
import com.sfl.pms.services.payment.redirect.model.acapture.AcaptureRedirectResult;
import com.sfl.pms.services.payment.redirect.model.adyen.AdyenRedirectResult;
import com.sfl.pms.services.system.event.ApplicationEventDistributionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * User: Ruben Dilanyan
 * Company: SFL LLC
 * Date: 1/19/16
 * Time: 11:26 AM
 */
@Component
public class PaymentProviderRedirectResultFacadeImpl implements PaymentProviderRedirectResultFacade {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentProviderRedirectResultFacadeImpl.class);

    /* Dependencies */
    @Autowired
    private AdyenRedirectResultService adyenRedirectResultService;

    @Autowired
    private AcaptureRedirectResultService acaptureRedirectResultService;

    @Autowired
    private ApplicationEventDistributionService applicationEventDistributionService;

    @Autowired
    private PaymentProviderRedirectResultService paymentProviderRedirectResultService;

    /* Constructors */
    public PaymentProviderRedirectResultFacadeImpl() {
        LOGGER.debug("Initializing payment provider redirect result service facade");
    }

    @Nonnull
    @Override
    public ResultResponseModel<CreateAdyenRedirectResultResponse> createAdyenRedirectResult(@Nonnull final CreateAdyenRedirectResultRequest request) {
        Assert.notNull(request, "Request should not be null");
        LOGGER.debug("Processing Adyen redirect result creation for request - {}", request);
        final List<ErrorResponseModel> errors = request.validateRequiredFields();
        if (errors.size() != 0) {
            return new ResultResponseModel<>(errors);
        }
        // Create Adyen redirect result
        final AdyenRedirectResultDto adyenRedirectResultDto = new AdyenRedirectResultDto(request.getAuthResult(), request.getPspReference(), request.getMerchantReference(), request.getSkinCode(), request.getMerchantSignature(), request.getPaymentMethod(), request.getShopperLocale(), request.getMerchantReturnData());
        final AdyenRedirectResult adyenRedirectResult = adyenRedirectResultService.createPaymentProviderRedirectResult(adyenRedirectResultDto);
        // Publish event
        applicationEventDistributionService.publishAsynchronousEvent(new StartPaymentProviderRedirectResultProcessingEvent(adyenRedirectResult.getId()));
        // Create response
        final CreateAdyenRedirectResultResponse response = new CreateAdyenRedirectResultResponse(adyenRedirectResult.getUuId());
        LOGGER.debug("Successfully processed Adyen redirect result creation request - {}, response - {}", request, response);
        return new ResultResponseModel<>(response);
    }

    @Nonnull
    @Override
    public ResultResponseModel<CreateAcaptureRedirectResultResponse> createAcaptureRedirectResult(@Nonnull final CreateAcaptureRedirectResultRequest request) {
        Assert.notNull(request);
        LOGGER.debug("Processing Acapture redirect result creation request - {}", request);
        final List<ErrorResponseModel> errors = request.validateRequiredFields();
        if (errors.size() != 0) {
            return new ResultResponseModel<>(errors);
        }
        // Create acapture redirect result
        final AcaptureRedirectResultDto acaptureRedirectResultDto = new AcaptureRedirectResultDto(request.getCheckoutId(), request.getResourcePath());
        final AcaptureRedirectResult acaptureRedirectResult = acaptureRedirectResultService.createPaymentProviderRedirectResult(acaptureRedirectResultDto);
        // Public event
        applicationEventDistributionService.publishAsynchronousEvent(new StartPaymentProviderRedirectResultProcessingEvent(acaptureRedirectResult.getId()));
        // Create response
        return new ResultResponseModel<>(new CreateAcaptureRedirectResultResponse(acaptureRedirectResult.getUuId()));
    }

    @Nonnull
    @Override
    public ResultResponseModel<GetPaymentProviderRedirectResultStatusResponse> getRedirectResultStatus(@Nonnull final GetPaymentProviderRedirectResultStatusRequest request) {
        Assert.notNull(request, "Request should not be null");
        LOGGER.debug("Processing get redirect result request - {}", request);
        final List<ErrorResponseModel> errors = request.validateRequiredFields();
        if (errors.size() != 0) {
            return new ResultResponseModel<>(errors);
        }
        final PaymentProviderRedirectResult paymentProviderRedirectResult = paymentProviderRedirectResultService.getPaymentProviderRedirectResultByUuId(request.getRedirectResultUuId());
        final PaymentProviderRedirectResultState state = paymentProviderRedirectResult.getState();
        final Payment payment;
        if (PaymentProviderRedirectResultState.PROCESSED.equals(state)) {
            payment = paymentProviderRedirectResult.getPayment();
        } else {
            payment = null;
        }
        // Create response
        final GetPaymentProviderRedirectResultStatusResponse response = new GetPaymentProviderRedirectResultStatusResponse();
        response.setRedirectResultUuId(paymentProviderRedirectResult.getUuId());
        response.setState(PaymentProviderRedirectResultStateClientType.valueOf(paymentProviderRedirectResult.getState().name()));
        if (payment != null) {
            response.setPaymentUuId(payment.getUuId());
        }
        // Build result
        LOGGER.debug("Successfully processed request - {}, response - {}", request, response);
        return new ResultResponseModel<>(response);
    }

    /* Properties getters and setters */
    public AdyenRedirectResultService getAdyenRedirectResultService() {
        return adyenRedirectResultService;
    }

    public void setAdyenRedirectResultService(final AdyenRedirectResultService adyenRedirectResultService) {
        this.adyenRedirectResultService = adyenRedirectResultService;
    }

    public ApplicationEventDistributionService getApplicationEventDistributionService() {
        return applicationEventDistributionService;
    }

    public void setApplicationEventDistributionService(final ApplicationEventDistributionService applicationEventDistributionService) {
        this.applicationEventDistributionService = applicationEventDistributionService;
    }

    public PaymentProviderRedirectResultService getPaymentProviderRedirectResultService() {
        return paymentProviderRedirectResultService;
    }

    public void setPaymentProviderRedirectResultService(final PaymentProviderRedirectResultService paymentProviderRedirectResultService) {
        this.paymentProviderRedirectResultService = paymentProviderRedirectResultService;
    }
}
