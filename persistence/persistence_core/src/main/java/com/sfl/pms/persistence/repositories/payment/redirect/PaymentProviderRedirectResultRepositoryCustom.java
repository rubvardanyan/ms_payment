package com.sfl.pms.persistence.repositories.payment.redirect;

import com.sfl.pms.services.payment.redirect.model.PaymentProviderRedirectResult;

import javax.annotation.Nonnull;

/**
 * User: Ruben Dilanyan
 * Company: SFL LLC
 * Date: 8/19/15
 * Time: 12:44 PM
 */
public interface PaymentProviderRedirectResultRepositoryCustom {

    /**
     * Gets payment provider redirect result by id with pessimistic write lock
     *
     * @param id
     * @return paymentProviderRedirectResult
     */
    <T extends PaymentProviderRedirectResult> T findByIdWithPessimisticWriteLock(@Nonnull final Long id, @Nonnull Class<T> clazz);
}
