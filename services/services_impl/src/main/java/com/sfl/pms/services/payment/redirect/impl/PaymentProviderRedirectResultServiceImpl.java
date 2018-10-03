package com.sfl.pms.services.payment.redirect.impl;

import com.sfl.pms.persistence.repositories.payment.redirect.AbstractPaymentProviderRedirectResultRepository;
import com.sfl.pms.persistence.repositories.payment.redirect.PaymentProviderRedirectResultRepository;
import com.sfl.pms.services.common.exception.ServicesRuntimeException;
import com.sfl.pms.services.payment.provider.model.PaymentProviderType;
import com.sfl.pms.services.payment.redirect.PaymentProviderRedirectResultService;
import com.sfl.pms.services.payment.redirect.exception.PaymentProviderRedirectResultStateNotAllowedException;
import com.sfl.pms.services.payment.redirect.model.PaymentProviderRedirectResult;
import com.sfl.pms.services.payment.redirect.model.PaymentProviderRedirectResultState;
import com.sfl.pms.services.payment.redirect.model.acapture.AcaptureRedirectResult;
import com.sfl.pms.services.payment.redirect.model.adyen.AdyenRedirectResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Nonnull;
import java.util.Date;
import java.util.Set;

/**
 * User: Ruben Dilanyan
 * Company: SFL LLC
 * Date: 7/11/15
 * Time: 12:36 PM
 */
@Service
public class PaymentProviderRedirectResultServiceImpl extends AbstractPaymentProviderRedirectResultServiceImpl<PaymentProviderRedirectResult> implements PaymentProviderRedirectResultService {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentProviderRedirectResultServiceImpl.class);

    /* Properties */
    @Autowired
    private PaymentProviderRedirectResultRepository paymentProviderRedirectResultRepository;

    /* Constructors */
    public PaymentProviderRedirectResultServiceImpl() {
        LOGGER.debug("Initializing payment provider redirect result service");
    }

    @Transactional
    @Nonnull
    @Override
    public PaymentProviderRedirectResult updatePaymentProviderRedirectResultState(@Nonnull final Long paymentProviderRedirectResultId, @Nonnull final PaymentProviderRedirectResultState state, @Nonnull final Set<PaymentProviderRedirectResultState> allowedInitialStates) {
        assertPaymentProviderRedirectResultIdNotNull(paymentProviderRedirectResultId);
        Assert.notNull(state, "Payment provider redirect result state should not be null");
        Assert.notNull(allowedInitialStates, "Payment provider redirect result allowed initial states should not be null");
        LOGGER.debug("Updating payment provider redirect result state with id - {}, state - {}, allowed initial states - {}", paymentProviderRedirectResultId, state, allowedInitialStates);
        final PaymentProviderRedirectResult instance = paymentProviderRedirectResultRepository.findOne(paymentProviderRedirectResultId);
        PaymentProviderRedirectResult notification;
        //TODO: find better solution for postgres database nullable left outer join with write lock
        if(instance.getType().equals(PaymentProviderType.ADYEN)) {
            notification = paymentProviderRedirectResultRepository.findByIdWithPessimisticWriteLock(paymentProviderRedirectResultId, AdyenRedirectResult.class);
        } else if(instance.getType().equals(PaymentProviderType.ACAPTURE)) {
            notification = paymentProviderRedirectResultRepository.findByIdWithPessimisticWriteLock(paymentProviderRedirectResultId, AcaptureRedirectResult.class);
        } else {
            LOGGER.error("Unknown payment provider");
            throw new ServicesRuntimeException("Unknown payment provider");
        }
        assertPaymentProviderRedirectResultNotNullForId(notification, paymentProviderRedirectResultId);
        final PaymentProviderRedirectResultState initialState = notification.getState();
        if (allowedInitialStates.size() != 0) {
            assertNotificationRequestState(state, allowedInitialStates, initialState);
        }
        // Update state
        notification.setState(state);
        notification.setUpdated(new Date());
        // Persist request
        notification = paymentProviderRedirectResultRepository.save(notification);
        LOGGER.debug("Successfully updated state for payment provider redirect result with id - {}, new state - {}, initial state - {}", paymentProviderRedirectResultId, state, initialState);
        return notification;
    }

    /* Utility methods */
    @Override
    protected AbstractPaymentProviderRedirectResultRepository<PaymentProviderRedirectResult> getRepository() {
        return paymentProviderRedirectResultRepository;
    }

    @Override
    protected Class<PaymentProviderRedirectResult> getInstanceClass() {
        return PaymentProviderRedirectResult.class;
    }

    private void assertNotificationRequestState(final PaymentProviderRedirectResultState state, final Set<PaymentProviderRedirectResultState> allowedInitialStates, final PaymentProviderRedirectResultState initialState) {
        if (!allowedInitialStates.contains(initialState)) {
            LOGGER.error("{} state is not allowed since request has state - {} but expected request states are - {}", state, initialState, allowedInitialStates);
            throw new PaymentProviderRedirectResultStateNotAllowedException(state, initialState, allowedInitialStates);
        }
    }

    /* Properties getters and setters */
    public PaymentProviderRedirectResultRepository getPaymentProviderRedirectResultRepository() {
        return paymentProviderRedirectResultRepository;
    }

    public void setPaymentProviderRedirectResultRepository(PaymentProviderRedirectResultRepository paymentProviderRedirectResultRepository) {
        this.paymentProviderRedirectResultRepository = paymentProviderRedirectResultRepository;
    }
}
