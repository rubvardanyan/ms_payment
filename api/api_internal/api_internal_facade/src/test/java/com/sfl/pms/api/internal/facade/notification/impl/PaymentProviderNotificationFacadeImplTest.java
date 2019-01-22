package com.sfl.pms.api.internal.facade.notification.impl;

import com.sfl.pms.api.internal.facade.notification.exception.InvalidPaymentProviderNotificationsTokenException;
import com.sfl.pms.api.internal.facade.test.AbstractFacadeUnitTest;
import com.sfl.pms.core.api.internal.model.common.result.ErrorType;
import com.sfl.pms.core.api.internal.model.common.result.ResultResponseModel;
import com.sfl.pms.core.api.internal.model.notification.request.CreateAcapturePaymentProviderNotificationRequest;
import com.sfl.pms.core.api.internal.model.notification.request.CreateAdyenPaymentProviderNotificationRequest;
import com.sfl.pms.core.api.internal.model.notification.response.CreatePaymentProviderNotificationResponse;
import com.sfl.pms.services.payment.notification.PaymentProviderNotificationRequestService;
import com.sfl.pms.services.payment.notification.dto.PaymentProviderNotificationRequestDto;
import com.sfl.pms.services.payment.notification.event.StartPaymentProviderNotificationRequestProcessingEvent;
import com.sfl.pms.services.payment.notification.model.PaymentProviderNotificationRequest;
import com.sfl.pms.services.payment.provider.model.PaymentProviderType;
import com.sfl.pms.services.payment.settings.PaymentProviderSettingsService;
import com.sfl.pms.services.payment.settings.model.PaymentProviderSettings;
import com.sfl.pms.services.payment.settings.model.acapture.AcapturePaymentSettings;
import com.sfl.pms.services.system.event.ApplicationEventDistributionService;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.UUID;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

/**
 * User: Ruben Dilanyan
 * Company: SFL LLC
 * Date: 1/18/16
 * Time: 4:46 PM
 */
public class PaymentProviderNotificationFacadeImplTest extends AbstractFacadeUnitTest {

    /* Test subject and mocks */
    @TestSubject
    private PaymentProviderNotificationFacadeImpl paymentProviderNotificationFacade = new PaymentProviderNotificationFacadeImpl();

    @Mock
    private PaymentProviderNotificationRequestService paymentProviderNotificationRequestService;

    @Mock
    private PaymentProviderSettingsService paymentProviderSettingsService;

    @Mock
    private ApplicationEventDistributionService applicationEventDistributionService;


    /* Constructors */
    public PaymentProviderNotificationFacadeImplTest() {
    }

    /* Test methods */
    @Test
    public void testCreatePaymentProviderNotificationRequestWithInvalidArguments() {
        // Reset
        resetAll();
        // Replay
        replayAll();
        // Run test scenario
        try {
            paymentProviderNotificationFacade.createAdyenPaymentProviderNotificationRequest(null);
            fail("Exception should be thrown");
        } catch (final IllegalArgumentException ex) {
            // Expected
        }
        // Verify
        verifyAll();
    }

    @Test
    public void testCreateAdyenPaymentProviderNotificationRequestWithInvalidNotificationsToken() {
        // Test data
        final CreateAdyenPaymentProviderNotificationRequest createAdyenPaymentProviderNotificationRequest = getFacadeImplTestHelper().createCreatePaymentProviderNotificationRequest();
        final Long requestId = 1L;
        final PaymentProviderNotificationRequest request = getFacadeImplTestHelper().createPaymentProviderNotificationRequest();
        request.setId(requestId);
        final Long settingsId = 2L;
        final PaymentProviderSettings settings = getFacadeImplTestHelper().createAdyenPaymentSettings();
        settings.setNotificationsToken(createAdyenPaymentProviderNotificationRequest.getNotificationsToken() + "_dif");
        settings.setId(settingsId);
        final PaymentProviderType paymentProviderType = PaymentProviderType.valueOf(createAdyenPaymentProviderNotificationRequest.getPaymentProviderType().name());
        // Reset
        resetAll();
        // Expectations
        expect(paymentProviderSettingsService.getActivePaymentProviderSettingsForType(eq(paymentProviderType))).andReturn(settings).once();
        // Replay
        replayAll();
        try {
            // Run test scenario
            paymentProviderNotificationFacade.createAdyenPaymentProviderNotificationRequest(createAdyenPaymentProviderNotificationRequest);
        } catch (final InvalidPaymentProviderNotificationsTokenException ex) {
            // Expected
            assertInvalidPaymentProviderNotificationsTokenException(ex, paymentProviderType, createAdyenPaymentProviderNotificationRequest.getNotificationsToken(), settings.getNotificationsToken());
        }
        // Verify
        verifyAll();
    }

    @Test
    public void testCreateAdyenPaymentProviderNotificationRequestWithValidationErrors() {
        // Test data
        final CreateAdyenPaymentProviderNotificationRequest createAdyenPaymentProviderNotificationRequest = new CreateAdyenPaymentProviderNotificationRequest();
        // Reset
        resetAll();
        // Replay
        replayAll();
        // Run test scenario
        final ResultResponseModel<CreatePaymentProviderNotificationResponse> result = paymentProviderNotificationFacade.createAdyenPaymentProviderNotificationRequest(createAdyenPaymentProviderNotificationRequest);
        assertNotNull(result);
        assertNull(result.getResponse());
        assertEquals(2, result.getErrors().size());
        assertValidationErrors(result.getErrors(), new HashSet<>(Arrays.asList(
                ErrorType.PAYMENT_PROVIDER_NOTIFICATION_MISSING_RAW_CONTENT,
                ErrorType.PAYMENT_PROVIDER_NOTIFICATION_MISSING_PROVIDER_TYPE
        )));
        // Verify
        verifyAll();
    }

    @Test
    public void testCreateAdyenPaymentProviderNotificationRequest() {
        // Test data
        final CreateAdyenPaymentProviderNotificationRequest createAdyenPaymentProviderNotificationRequest = getFacadeImplTestHelper().createCreatePaymentProviderNotificationRequest();
        final Long requestId = 1L;
        final PaymentProviderNotificationRequest request = getFacadeImplTestHelper().createPaymentProviderNotificationRequest();
        request.setId(requestId);
        final Long settingsId = 2L;
        final PaymentProviderSettings settings = getFacadeImplTestHelper().createAdyenPaymentSettings();
        settings.setNotificationsToken(createAdyenPaymentProviderNotificationRequest.getNotificationsToken());
        settings.setId(settingsId);
        final PaymentProviderType paymentProviderType = PaymentProviderType.valueOf(createAdyenPaymentProviderNotificationRequest.getPaymentProviderType().name());
        // Reset
        resetAll();
        // Expectations
        expect(paymentProviderSettingsService.getActivePaymentProviderSettingsForType(eq(paymentProviderType))).andReturn(settings).once();
        expect(paymentProviderNotificationRequestService.createPaymentProviderNotificationRequest(eq(new PaymentProviderNotificationRequestDto(paymentProviderType, createAdyenPaymentProviderNotificationRequest.getRawContent(), createAdyenPaymentProviderNotificationRequest.getClientIpAddress())))).andReturn(request).once();
        applicationEventDistributionService.publishAsynchronousEvent(eq(new StartPaymentProviderNotificationRequestProcessingEvent(requestId)));
        expectLastCall().once();
        // Replay
        replayAll();
        // Run test scenario
        final ResultResponseModel<CreatePaymentProviderNotificationResponse> result = paymentProviderNotificationFacade.createAdyenPaymentProviderNotificationRequest(createAdyenPaymentProviderNotificationRequest);
        assertNotNull(result);
        assertEquals(0, result.getErrors().size());
        assertNotNull(result.getResponse());
        final CreatePaymentProviderNotificationResponse response = result.getResponse();
        assertEquals(request.getUuId(), response.getPaymentProviderNotificationRequestUuId());
        // Verify
        verifyAll();
    }

    @Test
    public void testCreateAcapturePaymentProviderNotificationRequestWithInvalidArguments() {
        // Reset
        resetAll();
        // Replay
        replayAll();
        // Run test scenario
        try {
            paymentProviderNotificationFacade.createAcapturePaymentProviderNotificationRequest(null);
            fail("Exception should be thrown");
        } catch (IllegalArgumentException ex) {
            // ignore
        }
        // Verify
        verifyAll();
    }

    @Test
    public void testCreateAcapturePaymentProviderNotificationRequestWithValidationErrors() {
        // Test data
        final CreateAcapturePaymentProviderNotificationRequest request = new CreateAcapturePaymentProviderNotificationRequest();
        request.setPaymentProviderType(null);
        // Reset
        resetAll();
        // Replay
        replayAll();
        // Run test scenario
        final ResultResponseModel<CreatePaymentProviderNotificationResponse> result = paymentProviderNotificationFacade.createAcapturePaymentProviderNotificationRequest(request);
        // Verify
        verifyAll();
        assertNotNull(result);
        assertNull(result.getResponse());
        assertNotNull(result.getErrors());
        assertEquals(4, result.getErrors().size());
        assertValidationErrors(result.getErrors(), new HashSet<>(Arrays.asList(
                ErrorType.PAYMENT_PROVIDER_NOTIFICATION_MISSING_PROVIDER_TYPE,
                ErrorType.PAYMENT_PROVIDER_NOTIFICATION_MISSING_RAW_CONTENT,
                ErrorType.PAYMENT_PROVIDER_NOTIFICATION_MISSING_AUTHENTICATION_TAG,
                ErrorType.PAYMENT_PROVIDER_NOTIFICATION_MISSING_INITIALIZATION_VECTOR
        )));
    }

    @Test
    public void testCreateAcapturePaymentProviderNotificationRequestWithInvalidNotificationsToken() {
        // Test data
        final CreateAcapturePaymentProviderNotificationRequest request = new CreateAcapturePaymentProviderNotificationRequest(
            UUID.randomUUID().toString(),
            UUID.randomUUID().toString(),
            UUID.randomUUID().toString(),
            UUID.randomUUID().toString(),
            UUID.randomUUID().toString()
        );
        final AcapturePaymentSettings acapturePaymentSettings = new AcapturePaymentSettings();
        acapturePaymentSettings.setNotificationsToken(UUID.randomUUID().toString());
        // Reset
        resetAll();
        // Expectations
        expect(paymentProviderSettingsService.getActivePaymentProviderSettingsForType(PaymentProviderType.ACAPTURE)).andReturn(acapturePaymentSettings);
        // Replay
        replayAll();
        // Run test scenario
        try {
            paymentProviderNotificationFacade.createAcapturePaymentProviderNotificationRequest(request);
            fail("Exception should be thrown");
        } catch (InvalidPaymentProviderNotificationsTokenException ex) {
            // ignore
        }
        // Verify
        verifyAll();
    }

    /* Utility methods */
    private void assertInvalidPaymentProviderNotificationsTokenException(final InvalidPaymentProviderNotificationsTokenException ex, final PaymentProviderType paymentProviderType, final String providedToken, final String requiredToken) {
        assertEquals(paymentProviderType, ex.getPaymentProviderType());
        assertEquals(providedToken, ex.getProvidedNotificationsToken());
        assertEquals(requiredToken, ex.getRequiredNotificationsToken());
    }
}
