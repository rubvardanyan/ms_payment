package com.sfl.pms.services.payment.notification.impl;

import com.sfl.pms.persistence.utility.PersistenceUtilityService;
import com.sfl.pms.services.common.exception.ServicesRuntimeException;
import com.sfl.pms.services.payment.notification.PaymentProviderNotificationProcessingService;
import com.sfl.pms.services.payment.notification.PaymentProviderNotificationRequestService;
import com.sfl.pms.services.payment.notification.PaymentProviderNotificationService;
import com.sfl.pms.services.payment.notification.impl.processors.PaymentProviderNotificationProcessor;
import com.sfl.pms.services.payment.notification.impl.processors.acapture.AcaptureNotificationProcessor;
import com.sfl.pms.services.payment.notification.impl.processors.adyen.AdyenNotificationProcessor;
import com.sfl.pms.services.payment.notification.model.PaymentProviderNotification;
import com.sfl.pms.services.payment.notification.model.PaymentProviderNotificationRequest;
import com.sfl.pms.services.payment.notification.model.PaymentProviderNotificationRequestState;
import com.sfl.pms.services.payment.notification.model.PaymentProviderNotificationState;
import com.sfl.pms.services.payment.provider.exception.UnknownPaymentProviderTypeException;
import com.sfl.pms.services.payment.provider.model.PaymentProviderType;
import com.sfl.pms.services.util.mutable.MutableHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * User: Ruben Dilanyan
 * Company: SFL LLC
 * Date: 4/27/15
 * Time: 6:53 PM
 */
@Service
public class PaymentProviderNotificationProcessingServiceImpl implements PaymentProviderNotificationProcessingService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentProviderNotificationProcessingServiceImpl.class);

    /* Dependencies */
    @Autowired
    private PaymentProviderNotificationRequestService paymentProviderNotificationRequestService;

    @Autowired
    private PaymentProviderNotificationService paymentProviderNotificationService;

    @Autowired
    private PersistenceUtilityService persistenceUtilityService;

    @Autowired
    @Qualifier(value = "adyenNotificationProcessor")
    private AdyenNotificationProcessor adyenNotificationProcessor;

    @Autowired
    @Qualifier(value = "acaptureNotificationProcessor")
    private AcaptureNotificationProcessor acaptureNotificationProcessor;

    /* Constructors */
    public PaymentProviderNotificationProcessingServiceImpl() {
        LOGGER.debug("Initializing payment provider notification processing service");
    }

    @Override
    public List<Long> processPaymentProviderNotificationRequest(@Nonnull final Long notificationRequestId) {
        Assert.notNull(notificationRequestId, "Notification request id should not be null");
        LOGGER.debug("Processing payment provider notification request for id - {}", notificationRequestId);
        // Update state of notification request to "in progress"
        updateNotificationRequestState(notificationRequestId, PaymentProviderNotificationRequestState.PROCESSING, new LinkedHashSet<>(Arrays.asList(PaymentProviderNotificationRequestState.CREATED)));
        try {
            // Grab notification request
            final PaymentProviderNotificationRequest notificationRequest = getPaymentProviderNotificationRequest(notificationRequestId);
            final PaymentProviderNotificationProcessor notificationProcessor = getNotificationProcessor(notificationRequest.getProviderType());
            // Create payment provider notification for request
            final List<PaymentProviderNotification> notifications = createNotifications(notificationProcessor, notificationRequest);
            // Reload notifications list to return
            final List<Long> notificationIdsToReturn = new ArrayList<>();
            // Start processing of notifications
            notifications.forEach(notification -> {
                processPaymentProviderNotification(notificationProcessor, notification);
                notificationIdsToReturn.add(notification.getId());
            });
            // Update state of request
            updateNotificationRequestState(notificationRequestId, PaymentProviderNotificationRequestState.PROCESSED, new LinkedHashSet<>());
            return notificationIdsToReturn;
        } catch (final Exception ex) {
            LOGGER.error("Error occurred while processing payment provider notification request with id - {}", notificationRequestId, ex);
            updateNotificationRequestState(notificationRequestId, PaymentProviderNotificationRequestState.FAILED, new LinkedHashSet<>());
            throw new ServicesRuntimeException("Error occurred while processing notification request with id - " + notificationRequestId, ex);
        }
    }

    /* Utility methods */
    private void processPaymentProviderNotification(final PaymentProviderNotificationProcessor notificationProcessor, final PaymentProviderNotification notification) {
        // Mark notification as processing
        updateNotificationState(notification.getId(), PaymentProviderNotificationState.PROCESSING, new LinkedHashSet<>(Arrays.asList(PaymentProviderNotificationState.CREATED)));
        try {
            PaymentProviderNotification reloadedNotification = paymentProviderNotificationService.getPaymentProviderNotificationById(notification.getId());
            reloadedNotification = persistenceUtilityService.initializeAndUnProxy(reloadedNotification);
            // Process notification
            notificationProcessor.processPaymentProviderNotification(reloadedNotification);
            // Mark notification as processed
            updateNotificationState(notification.getId(), PaymentProviderNotificationState.PROCESSED, new LinkedHashSet<>());
        } catch (final Exception ex) {
            LOGGER.error("Error occurred while processing notification - {}", notification, ex);
            updateNotificationState(notification.getId(), PaymentProviderNotificationState.FAILED, new LinkedHashSet<>());
        }
    }

    private List<PaymentProviderNotification> createNotifications(final PaymentProviderNotificationProcessor notificationProcessor, final PaymentProviderNotificationRequest notificationRequest) {
        final MutableHolder<List<PaymentProviderNotification>> notificationsMutableHolder = new MutableHolder<>(null);
        persistenceUtilityService.runInNewTransaction(() -> {
            List<PaymentProviderNotification> notifications = notificationProcessor.createPaymentProviderNotificationForRequest(notificationRequest);
            LOGGER.debug("Created payment provider notifications - {} for request - {}", notifications, notificationRequest);
            notificationsMutableHolder.setValue(notifications);
        });
        return notificationsMutableHolder.getValue();
    }

    private void updateNotificationRequestState(final Long notificationRequestId, final PaymentProviderNotificationRequestState state, final Set<PaymentProviderNotificationRequestState> allowedStates) {
        persistenceUtilityService.runInNewTransaction(() -> {
            paymentProviderNotificationRequestService.updatePaymentProviderNotificationRequestState(notificationRequestId, state, allowedStates);
        });
    }

    private void updateNotificationState(final Long notificationId, final PaymentProviderNotificationState state, final Set<PaymentProviderNotificationState> allowedStates) {
        persistenceUtilityService.runInNewTransaction(() -> {
            paymentProviderNotificationService.updatePaymentProviderNotificationState(notificationId, state, allowedStates);
        });
    }

    private PaymentProviderNotificationRequest getPaymentProviderNotificationRequest(final Long notificationRequestId) {
        final PaymentProviderNotificationRequest notificationRequest = paymentProviderNotificationRequestService.getPaymentProviderNotificationRequestById(notificationRequestId);
        LOGGER.debug("Retrieved payment provider notification request - {}", notificationRequest);
        return notificationRequest;
    }

    private PaymentProviderNotificationProcessor getNotificationProcessor(final PaymentProviderType paymentProviderType) {
        switch (paymentProviderType) {
            case ADYEN: {
                return adyenNotificationProcessor;
            }
            case ACAPTURE: {
                return acaptureNotificationProcessor;
            }
            default: {
                throw new UnknownPaymentProviderTypeException(paymentProviderType);
            }
        }
    }

    /* Properties getters and setters */
    public PaymentProviderNotificationRequestService getPaymentProviderNotificationRequestService() {
        return paymentProviderNotificationRequestService;
    }

    public void setPaymentProviderNotificationRequestService(final PaymentProviderNotificationRequestService paymentProviderNotificationRequestService) {
        this.paymentProviderNotificationRequestService = paymentProviderNotificationRequestService;
    }

    public PaymentProviderNotificationService getPaymentProviderNotificationService() {
        return paymentProviderNotificationService;
    }

    public void setPaymentProviderNotificationService(final PaymentProviderNotificationService paymentProviderNotificationService) {
        this.paymentProviderNotificationService = paymentProviderNotificationService;
    }

    public PersistenceUtilityService getPersistenceUtilityService() {
        return persistenceUtilityService;
    }

    public void setPersistenceUtilityService(final PersistenceUtilityService persistenceUtilityService) {
        this.persistenceUtilityService = persistenceUtilityService;
    }

    public AdyenNotificationProcessor getAdyenNotificationProcessor() {
        return adyenNotificationProcessor;
    }

    public void setAdyenNotificationProcessor(final AdyenNotificationProcessor adyenNotificationProcessor) {
        this.adyenNotificationProcessor = adyenNotificationProcessor;
    }

    public AcaptureNotificationProcessor getAcaptureNotificationProcessor() {
        return acaptureNotificationProcessor;
    }

    public void setAcaptureNotificationProcessor(final AcaptureNotificationProcessor acaptureNotificationProcessor) {
        this.acaptureNotificationProcessor = acaptureNotificationProcessor;
    }
}
