package com.sfl.pms.services.payment.notification.impl.adyen;

import com.sfl.pms.persistence.repositories.payment.notification.AbstractPaymentProviderNotificationRepository;
import com.sfl.pms.persistence.repositories.payment.notification.PaymentProviderNotificationRepository;
import com.sfl.pms.persistence.repositories.payment.notification.adyen.AdyenPaymentProviderNotificationRepository;
import com.sfl.pms.services.payment.notification.adyen.AdyenPaymentProviderNotificationService;
import com.sfl.pms.services.payment.notification.dto.adyen.AdyenPaymentProviderNotificationDto;
import com.sfl.pms.services.payment.notification.exception.InvalidPaymentProviderNotificationRequestTypeException;
import com.sfl.pms.services.payment.notification.impl.AbstractPaymentProviderNotificationServiceImpl;
import com.sfl.pms.services.payment.notification.model.PaymentProviderNotificationRequest;
import com.sfl.pms.services.payment.notification.model.adyen.AdyenPaymentProviderNotification;
import com.sfl.pms.services.payment.provider.model.PaymentProviderType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Nonnull;

/**
 * User: Ruben Dilanyan
 * Company: SFL LLC
 * Date: 4/26/15
 * Time: 12:02 AM
 */
@Service
public class AdyenPaymentProviderNotificationServiceImpl extends AbstractPaymentProviderNotificationServiceImpl<AdyenPaymentProviderNotification> implements AdyenPaymentProviderNotificationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdyenPaymentProviderNotificationServiceImpl.class);

    /* Dependencies */
    @Autowired
    private AdyenPaymentProviderNotificationRepository adyenPaymentProviderNotificationRepository;

    /* Constructors */
    public AdyenPaymentProviderNotificationServiceImpl() {
        LOGGER.debug("Initializing Adyen payment provider notification service");
    }

    @Transactional
    @Nonnull
    @Override
    public AdyenPaymentProviderNotification createPaymentProviderNotification(@Nonnull final Long notificationRequestId, @Nonnull final AdyenPaymentProviderNotificationDto notificationDto) {
        Assert.notNull(notificationRequestId, "Notification request id should not be null");
        assertAdyenPaymentProviderNotificationDto(notificationDto);
        LOGGER.debug("Creating Adyen payment provider notification for request with id - {}, DTO - {}", notificationRequestId, notificationDto);
        // Load notification request
        final PaymentProviderNotificationRequest notificationRequest = getPaymentProviderNotificationRequestService().getPaymentProviderNotificationRequestById(notificationRequestId);
        // Assert notification request provider type
        assertNotificationRequestPaymentProviderType(notificationRequest);
        // Create notification
        AdyenPaymentProviderNotification notification = new AdyenPaymentProviderNotification(true);
        // Set properties
        notificationDto.updateDomainEntityProperties(notification);
        notification.setRequest(notificationRequest);
        // Persist notification
        notification = adyenPaymentProviderNotificationRepository.save(notification);
        LOGGER.debug("Successfully created new Adyen payment provider notification with id - {}, notification - {}", notification.getId(), notification);
        return notification;
    }

    /* Utility methods */
    @Override
    protected AbstractPaymentProviderNotificationRepository<AdyenPaymentProviderNotification> getRepository() {
        return adyenPaymentProviderNotificationRepository;
    }

    @Override
    protected Class<AdyenPaymentProviderNotification> getInstanceClass() {
        return AdyenPaymentProviderNotification.class;
    }

    private void assertAdyenPaymentProviderNotificationDto(final AdyenPaymentProviderNotificationDto notificationDto) {
        Assert.notNull(notificationDto, "Notification request DTO should not be null");
        Assert.notNull(notificationDto.getRawContent(), "Raw content in notification request DTO should not be null");
    }

    private void assertNotificationRequestPaymentProviderType(final PaymentProviderNotificationRequest request) {
        if (!PaymentProviderType.ADYEN.equals(request.getProviderType())) {
            LOGGER.error("Payment provider notification request with id - {} has provider type of - {}, expected provider type  - {}", request.getId(), request.getProviderType(), PaymentProviderType.ADYEN);
            throw new InvalidPaymentProviderNotificationRequestTypeException(request.getId(), request.getProviderType(), PaymentProviderType.ADYEN);
        }
    }

    /* Properties getters and setters */
    public AdyenPaymentProviderNotificationRepository getAdyenPaymentProviderNotificationRepository() {
        return adyenPaymentProviderNotificationRepository;
    }

    public void setAdyenPaymentProviderNotificationRepository(final AdyenPaymentProviderNotificationRepository adyenPaymentProviderNotificationRepository) {
        this.adyenPaymentProviderNotificationRepository = adyenPaymentProviderNotificationRepository;
    }
}
