package com.sfl.pms.queue.consumer.payment.redirect;

import com.sfl.pms.queue.consumer.test.AbstractQueueConsumerIntegrationTest;
import com.sfl.pms.services.payment.common.model.order.OrderPayment;
import com.sfl.pms.services.payment.redirect.adyen.AdyenRedirectResultService;
import com.sfl.pms.services.payment.redirect.dto.redirect.adyen.AdyenRedirectResultDto;
import com.sfl.pms.services.payment.redirect.model.PaymentProviderRedirectResultState;
import com.sfl.pms.services.payment.redirect.model.adyen.AdyenRedirectResult;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;

/**
 * User: Ruben Dilanyan
 * Company: SFL LLC
 * Date: 7/14/15
 * Time: 8:40 PM
 */
public class PaymentProviderRedirectResultProcessingQueueConsumerServiceIntegrationTest extends AbstractQueueConsumerIntegrationTest {

    /* Dependencies */
    @Autowired
    private PaymentProviderRedirectResultProcessingQueueConsumerService paymentProviderRedirectResultProcessingQueueConsumerService;

    @Autowired
    private AdyenRedirectResultService adyenRedirectResultService;

    /* Constructors */
    public PaymentProviderRedirectResultProcessingQueueConsumerServiceIntegrationTest() {
    }

    /* Test methods */
    @Test
    public void testProcessPaymentProviderRedirectResult() {
        // Prepare data
        OrderPayment payment = getServicesTestHelper().createOrderPayment();
        final AdyenRedirectResultDto redirectResultDto = getServicesTestHelper().createAdyenRedirectResultForRealPaymentDto(payment);
        AdyenRedirectResult adyenRedirectResult = getServicesTestHelper().createAdyenRedirectResult(redirectResultDto);
        assertEquals(PaymentProviderRedirectResultState.CREATED, adyenRedirectResult.getState());
        flushAndClear();
        // Start processing
        paymentProviderRedirectResultProcessingQueueConsumerService.processPaymentProviderRedirectResult(adyenRedirectResult.getId());
        flushAndClear();
        // Reload adyen redirect result
        adyenRedirectResult = adyenRedirectResultService.getPaymentProviderRedirectResultById(adyenRedirectResult.getId());
        assertEquals(PaymentProviderRedirectResultState.PROCESSED, adyenRedirectResult.getState());
    }

}
