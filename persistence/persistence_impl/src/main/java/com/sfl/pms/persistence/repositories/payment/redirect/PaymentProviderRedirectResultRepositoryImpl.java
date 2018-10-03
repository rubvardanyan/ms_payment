package com.sfl.pms.persistence.repositories.payment.redirect;

import com.sfl.pms.services.payment.redirect.model.PaymentProviderRedirectResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import javax.annotation.Nonnull;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;

/**
 * User: Ruben Dilanyan
 * Company: SFL LLC
 * Date: 8/19/15
 * Time: 12:45 PM
 */
public class PaymentProviderRedirectResultRepositoryImpl implements PaymentProviderRedirectResultRepositoryCustom {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentProviderRedirectResultRepositoryImpl.class);

    /* Dependencies */
    @PersistenceContext
    private EntityManager entityManager;

    /* Constructors */
    public PaymentProviderRedirectResultRepositoryImpl() {
        LOGGER.debug("Initializing payment provider redirect result repository");
    }

    @Override
    public <T extends PaymentProviderRedirectResult> T findByIdWithPessimisticWriteLock(@Nonnull final Long id, @Nonnull Class<T> clazz) {
        Assert.notNull(id, "Payment provider redirect result id should not be null");
        LOGGER.debug("Loading payment provider redirect result with id - {} using pessimistic write lock.", id);
        final T paymentProviderRedirectResult = entityManager.find(clazz, id, LockModeType.PESSIMISTIC_WRITE);
        LOGGER.debug("Successfully retrieved payment provider redirect result with id - {} using pessimistic write lock. Payment provider redirect result - {}", id, paymentProviderRedirectResult);
        return paymentProviderRedirectResult;
    }
}
