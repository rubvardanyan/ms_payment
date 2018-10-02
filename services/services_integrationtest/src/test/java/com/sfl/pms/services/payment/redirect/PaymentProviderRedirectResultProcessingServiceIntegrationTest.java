package com.sfl.pms.services.payment.redirect;

import com.sfl.pms.services.payment.common.auth.CustomerPaymentMethodAuthorizationPaymentService;
import com.sfl.pms.services.payment.common.model.Payment;
import com.sfl.pms.services.payment.common.model.PaymentState;
import com.sfl.pms.services.payment.common.model.auth.CustomerPaymentMethodAuthorizationPayment;
import com.sfl.pms.services.payment.common.model.order.OrderPayment;
import com.sfl.pms.services.payment.common.order.OrderPaymentService;
import com.sfl.pms.services.payment.method.model.PaymentMethodType;
import com.sfl.pms.services.payment.method.model.adyen.AdyenPaymentMethodType;
import com.sfl.pms.services.payment.redirect.adyen.AdyenRedirectResultService;
import com.sfl.pms.services.payment.redirect.dto.redirect.adyen.AdyenRedirectResultDto;
import com.sfl.pms.services.payment.redirect.model.PaymentProviderRedirectResultState;
import com.sfl.pms.services.payment.redirect.model.adyen.AdyenRedirectResult;
import com.sfl.pms.services.test.AbstractServiceIntegrationTest;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * User: Ruben Dilanyan
 * Company: SFL LLC
 * Date: 7/14/15
 * Time: 8:15 PM
 */
public class PaymentProviderRedirectResultProcessingServiceIntegrationTest extends AbstractServiceIntegrationTest {

    /* Dependencies */
    @Autowired
    private PaymentProviderRedirectResultProcessingService paymentProviderRedirectResultProcessingService;

    @Autowired
    private AdyenRedirectResultService adyenRedirectResultService;

    @Autowired
    private OrderPaymentService orderPaymentService;

    @Autowired
    private CustomerPaymentMethodAuthorizationPaymentService customerPaymentMethodAuthorizationPaymentService;

    /* Constructors */
    public PaymentProviderRedirectResultProcessingServiceIntegrationTest() {
    }

    /* Test methods */
    @Test
    public void testProcessPaymentProviderRedirectResultForOrderPayment() {
        // Prepare data
        OrderPayment payment = getServicesTestHelper().createOrderPayment();
        final AdyenRedirectResultDto redirectResultDto = getServicesTestHelper().createAdyenRedirectResultForRealPaymentDto(payment);
        AdyenRedirectResult adyenRedirectResult = getServicesTestHelper().createAdyenRedirectResult(redirectResultDto);
        assertEquals(PaymentProviderRedirectResultState.CREATED, adyenRedirectResult.getState());
        flushAndClear();
        // Start processing
        paymentProviderRedirectResultProcessingService.processPaymentProviderRedirectResult(adyenRedirectResult.getId());
        flushAndClear();
        // Reload adyen redirect result
        adyenRedirectResult = adyenRedirectResultService.getPaymentProviderRedirectResultById(adyenRedirectResult.getId());
        payment = orderPaymentService.getPaymentById(payment.getId());
        assertProcessedAdyenRedirectResult(adyenRedirectResult, payment);
    }

    @Ignore
    @Test
    public void testProcessPaymentProviderRedirectResultForPaymentMethodAuthorizationPayment() {
        // Prepare data
        CustomerPaymentMethodAuthorizationPayment payment = getServicesTestHelper().createCustomerPaymentMethodAuthorizationPayment();
        final AdyenRedirectResultDto redirectResultDto = getServicesTestHelper().createAdyenRedirectResultForRealPaymentDto(payment);
        AdyenRedirectResult adyenRedirectResult = getServicesTestHelper().createAdyenRedirectResult(redirectResultDto);
        assertEquals(PaymentProviderRedirectResultState.CREATED, adyenRedirectResult.getState());
        flushAndClear();
        // Start processing
        paymentProviderRedirectResultProcessingService.processPaymentProviderRedirectResult(adyenRedirectResult.getId());
        flushAndClear();
        // Reload adyen redirect result
        adyenRedirectResult = adyenRedirectResultService.getPaymentProviderRedirectResultById(adyenRedirectResult.getId());
        payment = customerPaymentMethodAuthorizationPaymentService.getPaymentById(payment.getId());
        assertProcessedAdyenRedirectResult(adyenRedirectResult, payment);
    }

    /* Utility methods */
    private void assertProcessedAdyenRedirectResult(final AdyenRedirectResult adyenRedirectResult, final Payment payment) {
        assertEquals(PaymentProviderRedirectResultState.PROCESSED, adyenRedirectResult.getState());
        assertNotNull(adyenRedirectResult.getPayment());
        assertEquals(payment.getId(), adyenRedirectResult.getPayment().getId());
        // Assert payment
        Assert.assertEquals(PaymentState.PAID, payment.getLastState());
        Assert.assertEquals(PaymentMethodType.getPaymentMethodTypeForAdyenPaymentMethod(AdyenPaymentMethodType.getAdyenPaymentMethodTypeForCode(adyenRedirectResult.getPaymentMethod())), payment.getConfirmedPaymentMethodType());
    }

}
