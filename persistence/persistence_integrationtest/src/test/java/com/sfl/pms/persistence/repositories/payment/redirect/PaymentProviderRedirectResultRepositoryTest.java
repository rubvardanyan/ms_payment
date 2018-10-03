package com.sfl.pms.persistence.repositories.payment.redirect;

import com.sfl.pms.persistence.repositories.AbstractRepositoryTest;
import com.sfl.pms.services.payment.redirect.model.PaymentProviderRedirectResult;
import com.sfl.pms.services.payment.redirect.model.acapture.AcaptureRedirectResult;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertNull;

/**
 * User: Ruben Dilanyan
 * Company: SFL LLC
 * Date: 8/19/15
 * Time: 1:09 PM
 */
public class PaymentProviderRedirectResultRepositoryTest extends AbstractRepositoryTest {

    /* Dependencies */
    @Autowired
    private PaymentProviderRedirectResultRepository paymentProviderRedirectResultRepository;

    /* Constructors */
    public PaymentProviderRedirectResultRepositoryTest() {
    }

    /* Test methods */
    @Test
    public void testFindByIdWithPessimisticWriteLock() {
        // Prepare data
        final Long id = 1L;
        // Load and assert
        final PaymentProviderRedirectResult paymentProviderRedirectResult = paymentProviderRedirectResultRepository.findByIdWithPessimisticWriteLock(id, AcaptureRedirectResult.class);
        assertNull(paymentProviderRedirectResult);
    }
}
