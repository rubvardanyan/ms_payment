package com.sfl.pms.services.payment.processing.impl.acapture;

import com.sfl.pms.services.payment.common.PaymentService;
import com.sfl.pms.services.payment.common.dto.acapture.AcapturePaymentProviderMetadataDto;
import com.sfl.pms.services.payment.common.model.Payment;
import com.sfl.pms.services.payment.common.model.channel.ProvidedPaymentMethodProcessingChannel;
import com.sfl.pms.services.payment.method.model.PaymentMethodType;
import com.sfl.pms.services.payment.provider.impl.acapture.AcapturePaymentProviderIntegrationService;
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
 * Date: 9/28/18
 * Time: 11:54 PM
 */
public class AcapturePaymentOperationsProcessorImplTest extends AbstractServicesUnitTest {

    /* Test subject and mocks */
    @TestSubject
    private AcapturePaymentOperationsProcessorImpl acapturePaymentOperationsProcessor = new AcapturePaymentOperationsProcessorImpl();

    @Mock
    private AcapturePaymentProviderIntegrationService acapturePaymentProviderIntegrationService;

    @Mock
    private PaymentService paymentService;

    @Mock
    private AcapturePaymentSettingsService acapturePaymentSettingsService;

    /* Constructor */
    public AcapturePaymentOperationsProcessorImplTest() {
    }

    /* Test methods */
    @Test
    public void testProcessPaymentUsingCustomerPaymentMethodChannel() {
        // Reset
        resetAll();
        // Replay
        replayAll();
        // Run test scenario
        try {
            acapturePaymentOperationsProcessor.processPaymentUsingCustomerPaymentMethodChannel(12L);
            fail("Exception should be thrown");
        } catch (UnsupportedOperationException ex) {
            //success
        }
        // Verify
        verifyAll();
    }

    @Test
    public void testProcessPaymentUsingEncryptedPaymentMethodChannel() {
        // Reset
        resetAll();
        // Replay
        replayAll();
        // Run test scenario
        try {
            acapturePaymentOperationsProcessor.processPaymentUsingEncryptedPaymentMethodChannel(12L);
            fail("Exception should be thrown");
        } catch (UnsupportedOperationException ex) {
            //success
        }
        // Verify
        verifyAll();
    }

    @Test
    public void testGetStoredRecurringPaymentMethods() {
        // Reset
        resetAll();
        // Replay
        replayAll();
        // Run test scenario
        try {
            acapturePaymentOperationsProcessor.getStoredRecurringPaymentMethods(12L);
            fail("Exception should be thrown");
        } catch (UnsupportedOperationException ex) {
            //success
        }
        // Verify
        verifyAll();
    }

    @Test
    public void testProcessCustomerPaymentMethodRemoval() {
        // Reset
        resetAll();
        // Replay
        replayAll();
        // Run test scenario
        try {
            acapturePaymentOperationsProcessor.processCustomerPaymentMethodRemoval(12L);
            fail("Exception should be thrown");
        } catch (UnsupportedOperationException ex) {
            //success
        }
        // Verify
        verifyAll();
    }

    @Test
    public void testGeneratePaymentRedirectUrlWhenRecurringPayment() {
        // Reset
        resetAll();
        // Replay
        replayAll();
        // Run test scenario
        try {
            acapturePaymentOperationsProcessor.generatePaymentRedirectUrl(12L, true);
            fail("Exception should be thrown");
        } catch (UnsupportedOperationException ex) {
            //success
        }
        // Verify
        verifyAll();
    }

    @Test
    public void testGeneratePaymentRedirectUrlWithInvalidArguments() {
        // Reset
        resetAll();
        // Replay
        replayAll();
        // Run test scenario
        try {
            acapturePaymentOperationsProcessor.generatePaymentRedirectUrl(null, false);
            fail("Exception should be thrown");
        } catch (IllegalArgumentException ex) {
            //success
        }
        // Verify
        verifyAll();
    }

    @Test
    public void testGeneratePaymentRedirectUrl() {
        // Test data
        final Long paymentId = 123L;
        final String checkoutId = UUID.randomUUID().toString();
        final AcapturePaymentProviderMetadataDto metadataDto = new AcapturePaymentProviderMetadataDto(checkoutId);
        final Payment payment = getServicesImplTestHelper().createOrderPayment();
        final ProvidedPaymentMethodProcessingChannel processingChannel = getServicesImplTestHelper().createProvidedPaymentMethodProcessingChannel();
        processingChannel.setPaymentMethodType(PaymentMethodType.MASTER_CARD);
        payment.setPaymentProcessingChannel(processingChannel);
        final AcapturePaymentSettings acapturePaymentSettings = getServicesImplTestHelper().createAcapturePaymentSettings();
        final String redirectUrl = acapturePaymentSettings.getHostPageUrl() + "/" + checkoutId + "/" + processingChannel.getPaymentMethodType().getAcapturePaymentMethodType().getCode();
        // Reset
        resetAll();
        // Expectations
        expect(acapturePaymentProviderIntegrationService.createCheckout(paymentId)).andReturn(checkoutId);
        expect(paymentService.updatePaymentProviderMetadata(paymentId, metadataDto)).andReturn(payment);
        expect(acapturePaymentSettingsService.getActivePaymentSettings()).andReturn(acapturePaymentSettings);
        // Replay
        replayAll();
        // Run test scenario
        final String result = acapturePaymentOperationsProcessor.generatePaymentRedirectUrl(paymentId, false);
        assertEquals(redirectUrl, result);
        // Verify
        verifyAll();
    }
}