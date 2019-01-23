package com.sfl.pms.services.payment.notification.impl.acapture;

import com.sfl.pms.persistence.repositories.payment.notification.AbstractPaymentProviderNotificationRepository;
import com.sfl.pms.persistence.repositories.payment.notification.acapture.AcapturePaymentProviderNotificationRepository;
import com.sfl.pms.services.payment.notification.acapture.AcapturePaymentProviderNotificationService;
import com.sfl.pms.services.payment.notification.dto.acapture.AcapturePaymentProviderNotificationDto;
import com.sfl.pms.services.payment.notification.exception.InvalidPaymentProviderNotificationRequestTypeException;
import com.sfl.pms.services.payment.notification.impl.AbstractPaymentProviderNotificationServiceImpl;
import com.sfl.pms.services.payment.notification.model.PaymentProviderNotificationRequest;
import com.sfl.pms.services.payment.notification.model.acapture.AcapturePaymentProviderNotification;
import com.sfl.pms.services.payment.provider.model.PaymentProviderType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Nonnull;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 1/23/19
 * Time: 9:15 PM
 */
@Service
public class AcapturePaymentProviderNotificationServiceImpl extends AbstractPaymentProviderNotificationServiceImpl<AcapturePaymentProviderNotification> implements AcapturePaymentProviderNotificationService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AcapturePaymentProviderNotificationServiceImpl.class);

    /* Dependencies */
    @Autowired
    private AcapturePaymentProviderNotificationRepository acapturePaymentProviderNotificationRepository;

    /* Constructor */
    public AcapturePaymentProviderNotificationServiceImpl() {
        LOGGER.debug("Initializing acapture payment provider notification service");
    }

    /* Public methods */
    @Nonnull
    @Override
    public AcapturePaymentProviderNotification createPaymentProviderNotification(@Nonnull final Long notificationRequestId, @Nonnull final AcapturePaymentProviderNotificationDto notificationDto) {
        Assert.notNull(notificationRequestId, "Notification request id should not be null");
        assertAcapturePaymentProviderNotificationDto(notificationDto);
        LOGGER.debug("Creating Acapture payment provider notification for request with id - {}, DTO - {}", notificationRequestId, notificationDto);
        // Load notification request
        final PaymentProviderNotificationRequest notificationRequest = getPaymentProviderNotificationRequestService().getPaymentProviderNotificationRequestById(notificationRequestId);
        // Assert notification request provider type
        assertNotificationRequestPaymentProviderType(notificationRequest);
        // Create notification
        AcapturePaymentProviderNotification notification = new AcapturePaymentProviderNotification(true);
        // Set properties
        notificationDto.updateDomainEntityProperties(notification);
        notification.setRequest(notificationRequest);
        // Persist notification
        notification = acapturePaymentProviderNotificationRepository.save(notification);
        LOGGER.debug("Successfully created new Acapture payment provider notification with id - {}, notification - {}", notification.getId(), notification);
        return notification;
    }

    private void assertNotificationRequestPaymentProviderType(final PaymentProviderNotificationRequest request) {
        LOGGER.error("Payment provider notification request with id - {} has provider type of - {}, expected provider type  - {}", request.getId(), request.getProviderType(), PaymentProviderType.ACAPTURE);
        throw new InvalidPaymentProviderNotificationRequestTypeException(request.getId(), request.getProviderType(), PaymentProviderType.ACAPTURE);
    }

    private void assertAcapturePaymentProviderNotificationDto(final AcapturePaymentProviderNotificationDto notificationDto) {
        Assert.notNull(notificationDto);
        Assert.notNull(notificationDto.getNdc());
        Assert.notNull(notificationDto.getResultCode());
        Assert.notNull(notificationDto.getResultDescription());
        Assert.notNull(notificationDto.getNotificationId());
        Assert.notNull(notificationDto.getRawContent());
    }

    @Override
    protected AbstractPaymentProviderNotificationRepository<AcapturePaymentProviderNotification> getRepository() {
        return acapturePaymentProviderNotificationRepository;
    }

    @Override
    protected Class<AcapturePaymentProviderNotification> getInstanceClass() {
        return AcapturePaymentProviderNotification.class;
    }
}
