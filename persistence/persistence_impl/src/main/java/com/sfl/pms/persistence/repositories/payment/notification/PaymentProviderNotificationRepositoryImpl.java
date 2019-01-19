package com.sfl.pms.persistence.repositories.payment.notification;

import com.sfl.pms.services.payment.notification.model.PaymentProviderNotification;
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
 * Time: 12:37 PM
 */
public class PaymentProviderNotificationRepositoryImpl implements PaymentProviderNotificationRepositoryCustom {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentProviderNotificationRepositoryImpl.class);

    /* Dependencies */
    @PersistenceContext
    private EntityManager entityManager;

    /* Constructors */
    public PaymentProviderNotificationRepositoryImpl() {
        LOGGER.debug("Initializing payment provider notification repository");
    }

    @Override
    public PaymentProviderNotification findByIdWithPessimisticWriteLock(@Nonnull final Long id) {
        Assert.notNull(id, "Payment provider notification id should not be null");
        LOGGER.debug("Loading payment provider notification with id - {} using pessimistic write lock.", id);
        entityManager.flush();
        final PaymentProviderNotification paymentProviderNotification = entityManager.find(PaymentProviderNotification.class, id, LockModeType.PESSIMISTIC_WRITE);
        LOGGER.debug("Successfully retrieved payment provider notification with id - {} using pessimistic write lock. Payment provider notification - {}", id, paymentProviderNotification);
        return paymentProviderNotification;
    }
}
