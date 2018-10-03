package com.sfl.pms.services.payment.provider.adyen;

import com.sfl.pms.services.payment.provider.PaymentProviderIntegrationService;
import com.sfl.pms.services.payment.provider.dto.AdyenRedirectUrlGenerationDto;
import com.sfl.pms.services.payment.provider.model.adyen.AdyenRecurringContractType;
import com.sfl.pms.services.payment.redirect.dto.redirect.adyen.AdyenRedirectResultDto;

import javax.annotation.Nonnull;

/**
 * User: Ruben Dilanyan
 * Company: SFL LLC
 * Date: 6/30/15
 * Time: 2:49 PM
 */
public interface AdyenPaymentProviderIntegrationService extends PaymentProviderIntegrationService {

    /**
     * Generates payment provider redirection URL for provided DTO
     *
     * @param redirectUrlGenerationDto
     * @return redirectionUrl
     */
    @Nonnull
    String generatePaymentProviderRedirectUrlForDto(@Nonnull final AdyenRedirectUrlGenerationDto redirectUrlGenerationDto);

    /**
     * Generates redirection DTO for provided payment
     *
     * @param paymentId
     * @param recurringContractType
     * @return redirectUrlGenerationDto
     */
    @Nonnull
    AdyenRedirectUrlGenerationDto createAdyenRedirectGenerationDtoForPayment(@Nonnull final Long paymentId, @Nonnull final AdyenRecurringContractType recurringContractType);


    /**
     * Verify signature for Adyen redirect result
     *
     * @param resultDto
     * @return validSignature
     */
    boolean verifySignatureForAdyenRedirectResult(@Nonnull final AdyenRedirectResultDto resultDto);

    /**
     * Calculate signature for Adyen redirect result
     *
     * @param resultDto
     * @return signature
     */
    @Nonnull
    String calculateSignatureForAdyenRedirectResult(@Nonnull final AdyenRedirectResultDto resultDto);
}
