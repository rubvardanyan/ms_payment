package com.sfl.pms.services.payment.provider.impl.acapture;

import com.sfl.pms.externalclients.payment.acapture.communicator.AcaptureApiCommunicator;
import com.sfl.pms.externalclients.payment.acapture.model.AcaptureStatusCodes;
import com.sfl.pms.externalclients.payment.acapture.model.authentication.AcaptureAuthenticationModel;
import com.sfl.pms.externalclients.payment.acapture.model.payment.AcaptureAmountModel;
import com.sfl.pms.externalclients.payment.acapture.model.payment.PaymentType;
import com.sfl.pms.externalclients.payment.acapture.model.request.CreateCheckoutRequest;
import com.sfl.pms.externalclients.payment.acapture.model.response.CreateCheckoutResponse;
import com.sfl.pms.externalclients.payment.acapture.model.result.AcaptureResultModel;
import com.sfl.pms.services.common.exception.ServicesRuntimeException;
import com.sfl.pms.services.payment.common.PaymentService;
import com.sfl.pms.services.payment.common.model.Payment;
import com.sfl.pms.services.payment.common.model.channel.ProvidedPaymentMethodProcessingChannel;
import com.sfl.pms.services.payment.common.model.order.request.OrderPaymentRequest;
import com.sfl.pms.services.payment.common.order.request.OrderPaymentRequestService;
import com.sfl.pms.services.payment.method.acapture.AcapturePaymentMethodSettingsService;
import com.sfl.pms.services.payment.method.model.PaymentMethodType;
import com.sfl.pms.services.payment.method.model.acapture.AcapturePaymentMethodSettings;
import com.sfl.pms.services.payment.settings.acapture.AcapturePaymentSettingsService;
import com.sfl.pms.services.payment.settings.model.acapture.AcapturePaymentSettings;
import com.sfl.pms.services.test.AbstractServicesUnitTest;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Test;

import java.util.UUID;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 9/30/18
 * Time: 7:41 PM
 */
public class AcapturePaymentProviderIntegrationServiceImplTest extends AbstractServicesUnitTest {

    /* Test subject and mocks */
    @TestSubject
    private AcapturePaymentProviderIntegrationServiceImpl acapturePaymentProviderIntegrationService = new AcapturePaymentProviderIntegrationServiceImpl();

    @Mock
    private PaymentService paymentService;

    @Mock
    private AcapturePaymentMethodSettingsService paymentMethodSettingsService;

    @Mock
    private AcapturePaymentSettingsService acapturePaymentSettingsService;

    @Mock
    private AcaptureApiCommunicator acaptureApiCommunicator;

    @Mock
    private OrderPaymentRequestService orderPaymentRequestService;

    /* Constructor */
    public AcapturePaymentProviderIntegrationServiceImplTest() {
    }

    /* Test methods */
    @Test
    public void createCheckoutWithInvalidArguments() {
        // Reset
        resetAll();
        // Replay
        replayAll();
        // Run test scenario
        try {
            acapturePaymentProviderIntegrationService.createCheckout(null);
            fail("Exception should be thrown");
        } catch (IllegalArgumentException ex) {
            //success
        }
        // Verify
        verifyAll();
    }

    @Test
    public void createCheckoutWhenWrongProcessingChannel() {
        // Test data
        final Long paymentId = 127L;
        final Payment payment = getServicesImplTestHelper().createCustomerPaymentMethodAuthorizationPayment();
        // Reset
        resetAll();
        // Expectations
        expect(paymentService.getPaymentById(paymentId)).andReturn(payment);
        // Replay
        replayAll();
        // Run test scenario
        try {
            acapturePaymentProviderIntegrationService.createCheckout(paymentId);
            fail("Exception should be thrown");
        } catch (IllegalArgumentException ex) {
            //success
        }
        // Verify
        verifyAll();
    }

    @Test
    public void createCheckoutWhenAcapturePaymentMethodIsNull() {
        // Test data
        final Long paymentId = 127L;
        final Payment payment = getServicesImplTestHelper().createOrderPayment();
        final ProvidedPaymentMethodProcessingChannel processingChannel = getServicesImplTestHelper().createProvidedPaymentMethodProcessingChannel();
        processingChannel.setPaymentMethodType(PaymentMethodType.DINERS_CLUB);
        payment.setPaymentProcessingChannel(processingChannel);
        // Reset
        resetAll();
        // Expectations
        expect(paymentService.getPaymentById(paymentId)).andReturn(payment);
        // Replay
        replayAll();
        // Run test scenario
        try {
            acapturePaymentProviderIntegrationService.createCheckout(paymentId);
            fail("Exception should be thrown");
        } catch (IllegalArgumentException ex) {
            //success
        }
        // Verify
        verifyAll();
    }

    @Test
    public void createCheckoutWhenCheckoutResponseStatusCodeNotSuccessful() {
        // Test data
        final Long paymentId = 127L;
        final Payment payment = getServicesImplTestHelper().createOrderPayment();
        final OrderPaymentRequest orderPaymentRequest = getServicesImplTestHelper().createOrderPaymentRequest();
        final ProvidedPaymentMethodProcessingChannel processingChannel = getServicesImplTestHelper().createProvidedPaymentMethodProcessingChannel();
        processingChannel.setPaymentMethodType(PaymentMethodType.MASTER_CARD);
        payment.setPaymentProcessingChannel(processingChannel);
        final AcapturePaymentSettings paymentSettings = getServicesImplTestHelper().createAcapturePaymentSettings();
        final AcapturePaymentMethodSettings paymentMethodSettings = getServicesImplTestHelper().createAcapturePaymentMethodSettings();
        final CreateCheckoutRequest createCheckoutRequest = new CreateCheckoutRequest();
        createCheckoutRequest.setPaymentType(PaymentType.PRE_AUTHORIZATION);
        createCheckoutRequest.setAuthenticationModel(new AcaptureAuthenticationModel(paymentMethodSettings.getAuthorizationId()));
        createCheckoutRequest.setAmountModel(new AcaptureAmountModel(payment.getCurrency().getCode(), payment.getAmount()));
        createCheckoutRequest.setPaymentUuid(orderPaymentRequest.getUuId());
        final CreateCheckoutResponse createCheckoutResponse = new CreateCheckoutResponse();
        createCheckoutResponse.setCheckoutId(UUID.randomUUID().toString());
        createCheckoutResponse.setResult(new AcaptureResultModel(AcaptureStatusCodes.CHECKOUT_SUCCESSFULLY_DELETED.getCode(), "Result description"));
        // Reset
        resetAll();
        // Expectations
        expect(paymentService.getPaymentById(paymentId)).andReturn(payment);
        expect(acapturePaymentSettingsService.getActivePaymentSettings()).andReturn(paymentSettings);
        expect(paymentMethodSettingsService.getAcapturePaymentMethodSettingsByPaymentMethodTypeAndPaymentSettingsId(processingChannel.getPaymentMethodType().getAcapturePaymentMethodType(), paymentSettings.getId()))
                .andReturn(paymentMethodSettings);
        expect(orderPaymentRequestService.getByPaymentId(paymentId)).andReturn(orderPaymentRequest);
        expect(acaptureApiCommunicator.createCheckout(createCheckoutRequest)).andReturn(createCheckoutResponse);
        // Replay
        replayAll();
        // Run test scenario
        try {
            acapturePaymentProviderIntegrationService.createCheckout(paymentId);
            fail("Exception should be thrown");
        } catch (ServicesRuntimeException ex) {
            //success
        }
        // Verify
        verifyAll();
    }

    @Test
    public void createCheckout() {
        // Test data
        final Long paymentId = 127L;
        final Payment payment = getServicesImplTestHelper().createOrderPayment();
        final OrderPaymentRequest orderPaymentRequest = getServicesImplTestHelper().createOrderPaymentRequest();
        final ProvidedPaymentMethodProcessingChannel processingChannel = getServicesImplTestHelper().createProvidedPaymentMethodProcessingChannel();
        processingChannel.setPaymentMethodType(PaymentMethodType.MASTER_CARD);
        payment.setPaymentProcessingChannel(processingChannel);
        final AcapturePaymentSettings paymentSettings = getServicesImplTestHelper().createAcapturePaymentSettings();
        final AcapturePaymentMethodSettings paymentMethodSettings = getServicesImplTestHelper().createAcapturePaymentMethodSettings();
        final CreateCheckoutRequest createCheckoutRequest = new CreateCheckoutRequest();
        createCheckoutRequest.setPaymentType(PaymentType.PRE_AUTHORIZATION);
        createCheckoutRequest.setAuthenticationModel(new AcaptureAuthenticationModel(paymentMethodSettings.getAuthorizationId()));
        createCheckoutRequest.setAmountModel(new AcaptureAmountModel(payment.getCurrency().getCode(), payment.getAmount()));
        createCheckoutRequest.setPaymentUuid(orderPaymentRequest .getUuId());
        final CreateCheckoutResponse createCheckoutResponse = new CreateCheckoutResponse();
        createCheckoutResponse.setCheckoutId(UUID.randomUUID().toString());
        createCheckoutResponse.setResult(new AcaptureResultModel(AcaptureStatusCodes.CHECKOUT_SUCCESSFULLY_CREATED.getCode(), "Result description"));
        // Reset
        resetAll();
        // Expectations
        expect(paymentService.getPaymentById(paymentId)).andReturn(payment);
        expect(acapturePaymentSettingsService.getActivePaymentSettings()).andReturn(paymentSettings);
        expect(paymentMethodSettingsService.getAcapturePaymentMethodSettingsByPaymentMethodTypeAndPaymentSettingsId(processingChannel.getPaymentMethodType().getAcapturePaymentMethodType(), paymentSettings.getId()))
                .andReturn(paymentMethodSettings);
        expect(orderPaymentRequestService.getByPaymentId(paymentId)).andReturn(orderPaymentRequest);
        expect(acaptureApiCommunicator.createCheckout(createCheckoutRequest)).andReturn(createCheckoutResponse);
        // Replay
        replayAll();
        // Run test scenario
        final String result = acapturePaymentProviderIntegrationService.createCheckout(paymentId);
        assertEquals(createCheckoutResponse.getCheckoutId(), result);
        // Verify
        verifyAll();
    }
}