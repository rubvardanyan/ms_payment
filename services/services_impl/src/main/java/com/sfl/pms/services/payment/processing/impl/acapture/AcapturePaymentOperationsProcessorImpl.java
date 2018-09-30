package com.sfl.pms.services.payment.processing.impl.acapture;

import com.sfl.pms.services.payment.common.PaymentService;
import com.sfl.pms.services.payment.common.dto.PaymentResultDto;
import com.sfl.pms.services.payment.common.dto.acapture.AcapturePaymentProviderMetadataDto;
import com.sfl.pms.services.payment.common.model.Payment;
import com.sfl.pms.services.payment.common.model.PaymentResult;
import com.sfl.pms.services.payment.common.model.channel.ProvidedPaymentMethodProcessingChannel;
import com.sfl.pms.services.payment.provider.acapture.AcapturePaymentProviderIntegrationService;
import com.sfl.pms.services.payment.settings.acapture.AcapturePaymentSettingsService;
import com.sfl.pms.services.payment.settings.model.acapture.AcapturePaymentSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 9/28/18
 * Time: 11:50 PM
 */
@Component
@Qualifier("acapturePaymentOperationsProcessor")
public class AcapturePaymentOperationsProcessorImpl implements AcapturePaymentOperationsProcessor {
    private static final Logger LOGGER = LoggerFactory.getLogger(AcapturePaymentOperationsProcessorImpl.class);

    /* Dependencies */
    @Autowired
    private AcapturePaymentProviderIntegrationService acapturePaymentProviderIntegrationService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private AcapturePaymentSettingsService acapturePaymentSettingsService;


    /* Constructor */
    public AcapturePaymentOperationsProcessorImpl() {
        LOGGER.debug("Initializing acapture payment operations processor");
    }

    /* Public methods */
    @Nonnull
    @Override
    public PaymentResultDto<? extends PaymentResult> processPaymentUsingCustomerPaymentMethodChannel(@Nonnull final Long paymentId) {
        LOGGER.error("Customer payment method channel not supported by acapture payment operations processor");
        throw new UnsupportedOperationException("Customer payment method channel not supported by acapture payment operations processor");
    }

    @Nonnull
    @Override
    public PaymentResultDto<? extends PaymentResult> processPaymentUsingEncryptedPaymentMethodChannel(@Nonnull final Long paymentId) {
        LOGGER.error("Encrypted payment method channel not supported by acapture payment operations processor");
        throw new UnsupportedOperationException("Encrypted payment method channel not supported by acapture payment operations processor");
    }

    @Nonnull
    @Override
    public List<CustomerPaymentMethodProviderData> getStoredRecurringPaymentMethods(@Nonnull final Long customerId) {
        LOGGER.error("Recurring payment methods not supported by acapture payment operations processor");
        throw new UnsupportedOperationException("Recurring payment methods not supported by acapture payment operations processor");
    }

    @Override
    public CustomerPaymentMethodRemovalData processCustomerPaymentMethodRemoval(@Nonnull final Long paymentMethodProviderInformationId) {
        LOGGER.error("Customer payment method removal not supported by acapture payment operations processor");
        throw new UnsupportedOperationException("Customer payment method removal not supported by acapture payment operations processor");
    }

    @Nonnull
    @Override
    public boolean checkIfPaymentResultAlreadyExists(@Nonnull final Payment payment, @Nonnull final PaymentResultDto<? extends PaymentResult> paymentResultDto) {
        return false;
    }

    @Nonnull
    @Override
    public String generatePaymentRedirectUrl(@Nonnull final Long paymentId, @Nonnull final boolean createRecurringContract) {
        if(createRecurringContract) {
            LOGGER.error("Recurring payment not supported by acapture payment operations processor");
            throw new UnsupportedOperationException("Recurring payment not supported by acapture payment operations processor");
        }
        Assert.notNull(paymentId, "Payment uuid should not be null");
        final String checkoutId = acapturePaymentProviderIntegrationService.createCheckout(paymentId);
        final AcapturePaymentProviderMetadataDto metadataDto = new AcapturePaymentProviderMetadataDto(checkoutId);
        final Payment payment = paymentService.updatePaymentProviderMetadata(paymentId, metadataDto);
        Assert.isInstanceOf(ProvidedPaymentMethodProcessingChannel.class, payment.getPaymentProcessingChannel(), "Payment processing channel should be instance of ProvidedPaymentMethodProcessingChannel");
        final ProvidedPaymentMethodProcessingChannel processingChannel = (ProvidedPaymentMethodProcessingChannel) payment.getPaymentProcessingChannel();
        final AcapturePaymentSettings activePaymentSettings = acapturePaymentSettingsService.getActivePaymentSettings();
        return activePaymentSettings.getHostPageUrl() + "/" + checkoutId + "/" + processingChannel.getPaymentMethodType().getAcapturePaymentMethodType().getCode();
    }

    /* Utility methods */

}
