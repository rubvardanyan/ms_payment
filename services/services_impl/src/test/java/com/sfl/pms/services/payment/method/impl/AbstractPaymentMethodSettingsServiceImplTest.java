package com.sfl.pms.services.payment.method.impl;

import com.sfl.pms.persistence.repositories.payment.method.AbstractPaymentMethodSettingsRepository;
import com.sfl.pms.services.payment.method.AbstractPaymentMethodSettingsService;
import com.sfl.pms.services.payment.method.exception.PaymentMethodSettingsNotFoundForIdException;
import com.sfl.pms.services.payment.method.model.PaymentMethodSettings;
import com.sfl.pms.services.test.AbstractServicesUnitTest;
import org.junit.Test;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 9/28/18
 * Time: 9:26 PM
 */
public abstract class AbstractPaymentMethodSettingsServiceImplTest<T extends PaymentMethodSettings> extends AbstractServicesUnitTest {

    /* Constructor */
    public AbstractPaymentMethodSettingsServiceImplTest() {
    }

    /* Test methods */
    @Test
    public void testGetPaymentMethodSettingsByIdWithInvalidArguments() {
        // Reset
        resetAll();
        // Replay
        replayAll();
        // Run test scenario
        try {
            getService().getPaymentMethodSettingsById(null);
            fail();
        } catch (IllegalArgumentException ex) {
            //success
        }
        // Verify
        verifyAll();
    }

    @Test
    public void testGetPaymentMethodSettingsByIdWhenDoesNotExist() {
        final Long paymentMethodSettingsId = 123L;
        // Reset
        resetAll();
        // Expectations
        expect(getRepository().findOne(paymentMethodSettingsId)).andReturn(null);
        // Replay
        replayAll();
        // Run test scenario
        try {
            getService().getPaymentMethodSettingsById(paymentMethodSettingsId);
            fail();
        } catch (PaymentMethodSettingsNotFoundForIdException ex) {
            assertEquals(getInstanceClass(), ex.getEntityClass());
            assertEquals(paymentMethodSettingsId, ex.getId());
        }
        // Verify
        verifyAll();
    }

    @Test
    public void testGetPaymentMethodSettingsById() {
        final Long paymentMethodSettingsId = 123L;
        final T paymentMethodSettings = getInstance();
        // Reset
        resetAll();
        // Expectations
        expect(getRepository().findOne(paymentMethodSettingsId)).andReturn(paymentMethodSettings);
        // Replay
        replayAll();
        // Run test scenario
        final T result = getService().getPaymentMethodSettingsById(paymentMethodSettingsId);
        assertNotNull(result);
        assertEquals(paymentMethodSettings, result);
        // Verify
        verifyAll();
    }

    /* Abstract methods */
    protected abstract Class<T> getInstanceClass();

    protected abstract T getInstance();

    protected abstract AbstractPaymentMethodSettingsService<T> getService();

    protected abstract AbstractPaymentMethodSettingsRepository<T> getRepository();
}
