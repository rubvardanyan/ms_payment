package com.sfl.pms.services.payment.notification.impl.processors.acapture;

import com.sfl.pms.services.payment.notification.acapture.AcapturePaymentProviderNotificationService;
import com.sfl.pms.services.payment.notification.dto.acapture.AcapturePaymentProviderNotificationDto;
import com.sfl.pms.services.payment.notification.impl.processors.acapture.json.AcaptureNotificationJsonDeserializer;
import com.sfl.pms.services.payment.notification.impl.processors.acapture.json.model.AcapturePaymentNotificationJsonModel;
import com.sfl.pms.services.payment.notification.impl.processors.acapture.json.model.AcapturePaymentNotificationPayloadJsonModel;
import com.sfl.pms.services.payment.notification.model.PaymentProviderNotification;
import com.sfl.pms.services.payment.notification.model.PaymentProviderNotificationRequest;
import com.sfl.pms.services.payment.notification.model.acapture.AcapturePaymentProviderNotification;
import com.sfl.pms.services.payment.provider.model.PaymentProviderType;
import com.sfl.pms.services.test.AbstractServicesUnitTest;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Test;

import java.util.List;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.*;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 1/23/19
 * Time: 8:32 PM
 */
public class AcaptureNotificationProcessorImplTest extends AbstractServicesUnitTest {

    /* Test subject and mocks */
    @TestSubject
    private AcaptureNotificationProcessor acaptureNotificationProcessor = new AcaptureNotificationProcessorImpl();

    @Mock
    private AcaptureNotificationJsonDeserializer acaptureNotificationJsonDeserializer;

    @Mock
    private AcapturePaymentProviderNotificationService acapturePaymentProviderNotificationService;

    /* Constructor */
    public AcaptureNotificationProcessorImplTest() {

    }

    /* Test methods */
    @Test
    public void testCreatePaymentProviderNotificationForRequestWithInvalidArguments() {
        // Reset
        resetAll();
        // Replay
        replayAll();
        // Run test scenario
        try {
            acaptureNotificationProcessor.createPaymentProviderNotificationForRequest(null);
            fail("Exception should be thrown");
        } catch (IllegalArgumentException ex) {
            //ignore
        }
        // Verify
        verifyAll();
    }

    @Test
    public void testCreatePaymentProviderNotificationForRequest() {
        // Test data
        final PaymentProviderNotificationRequest notificationRequest = getServicesImplTestHelper().createPaymentProviderNotificationRequest();
        final AcapturePaymentNotificationJsonModel jsonModel = getServicesImplTestHelper().createAcapturePaymentNotificationJsonModel();
        final AcapturePaymentNotificationPayloadJsonModel notificationPayload = jsonModel.getPayload();
        final AcapturePaymentProviderNotificationDto notificationDto = new AcapturePaymentProviderNotificationDto(
                notificationRequest.getRawContent(),
                notificationRequest.getClientIpAddress(),
                notificationPayload.getId(),
                notificationPayload.getResult().getCode(),
                notificationPayload.getResult().getDescription(),
                notificationPayload.getNdc()
        );
        final AcapturePaymentProviderNotification notification = getServicesImplTestHelper().createAcapturePaymentProviderNotification();
        // Reset
        resetAll();
        // Expectations
        expect(acaptureNotificationJsonDeserializer.deserializePaymentNotificationPayload(notificationRequest.getRawContent())).andReturn(notificationPayload);
        expect(acapturePaymentProviderNotificationService.createPaymentProviderNotification(notificationRequest.getId(), notificationDto)).andReturn(notification);
        // Replay
        replayAll();
        // Run test scenario
        final List<PaymentProviderNotification> result = acaptureNotificationProcessor.createPaymentProviderNotificationForRequest(notificationRequest);
        // Verify
        verifyAll();
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(notification, result.get(0));
    }

    @Test
    public void testProcessPaymentProviderNotificationWithInvalidArguments() {
        // Reset
        resetAll();
        // Replay
        replayAll();
        // Run test scenario
        try {
            acaptureNotificationProcessor.processPaymentProviderNotification(null);
            fail("Exception should be thrown");
        } catch (IllegalArgumentException ex) {
            // ignore
        }
        try {
            final AcapturePaymentProviderNotification notification = getServicesImplTestHelper().createAcapturePaymentProviderNotification();
            notification.setType(PaymentProviderType.ADYEN);
            acaptureNotificationProcessor.processPaymentProviderNotification(notification);
            fail("Exception should be thrown");
        } catch (IllegalArgumentException ex) {
            // ignore
        }
        try {
            acaptureNotificationProcessor.processPaymentProviderNotification(getServicesImplTestHelper().createAdyenPaymentProviderNotification());
            fail("Exception should be thrown");
        } catch (IllegalArgumentException ex) {
            // ignore
        }
        // Verify
        verifyAll();
    }
}