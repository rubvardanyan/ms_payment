package com.sfl.pms.services.payment.method.impl.acapture;

import com.sfl.pms.persistence.repositories.payment.method.AbstractPaymentMethodSettingsRepository;
import com.sfl.pms.persistence.repositories.payment.method.acapture.AcapturePaymentMethodSettingsRepository;
import com.sfl.pms.services.common.exception.ServicesRuntimeException;
import com.sfl.pms.services.payment.method.AbstractPaymentMethodSettingsService;
import com.sfl.pms.services.payment.method.impl.AbstractPaymentMethodSettingsServiceImplTest;
import com.sfl.pms.services.payment.method.model.PaymentMethodType;
import com.sfl.pms.services.payment.method.model.acapture.AcapturePaymentMethodSettings;
import com.sfl.pms.services.payment.method.model.acapture.AcapturePaymentMethodType;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Test;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 9/28/18
 * Time: 9:49 PM
 */
public class AcapturePaymentMethodSettingsServiceImplTest extends AbstractPaymentMethodSettingsServiceImplTest<AcapturePaymentMethodSettings> {

    /* Test subject and mocks */
    @TestSubject
    private AcapturePaymentMethodSettingsServiceImpl acapturePaymentMethodSettingsService = new AcapturePaymentMethodSettingsServiceImpl();

    @Mock
    private AcapturePaymentMethodSettingsRepository acapturePaymentMethodSettingsRepository;

    /* Test methods */
    @Test
    public void testGetAcapturePaymentMethodSettingsByPaymentMethodTypeAndPaymentSettingsIdWithInvalidArguments() {
        // Reset
        resetAll();
        // Replay
        replayAll();
        //Run test scenario
        try {
            acapturePaymentMethodSettingsService.getAcapturePaymentMethodSettingsByPaymentMethodTypeAndPaymentSettingsId(null, 123L);
            fail("Exception should be thrown");
        } catch (IllegalArgumentException ex) {
            //success
        }
        try {
            acapturePaymentMethodSettingsService.getAcapturePaymentMethodSettingsByPaymentMethodTypeAndPaymentSettingsId(AcapturePaymentMethodType.MASTER_CARD, null);
            fail("Exception should be thrown");
        } catch (IllegalArgumentException ex) {
            //success
        }
        // Verify
        verifyAll();
    }

    @Test
    public void testGetAcapturePaymentMethodSettingsByPaymentMethodTypeAndPaymentSettingsIdWhenDoesNotExist() {
        // Test data
        final Long paymentSettingsId = 123L;
        final AcapturePaymentMethodType acapturePaymentMethodType = AcapturePaymentMethodType.IDEAL;
        final PaymentMethodType paymentMethodType = PaymentMethodType.getPaymentMethodTypeForAcapturePaymentMethod(acapturePaymentMethodType);
        // Reset
        resetAll();
        // Expectations
        expect(acapturePaymentMethodSettingsRepository.findByPaymentMethodTypeAndPaymentSettingsId(paymentMethodType, paymentSettingsId)).andReturn(null);
        // Replay
        replayAll();
        //Run test scenario
        try {
            acapturePaymentMethodSettingsService.getAcapturePaymentMethodSettingsByPaymentMethodTypeAndPaymentSettingsId(acapturePaymentMethodType, paymentSettingsId);
            fail("Exception should be thrown");
        } catch (ServicesRuntimeException ex) {
            //success
        }
        // Verify
        verifyAll();
    }

    @Test
    public void testGetAcapturePaymentMethodSettingsByPaymentMethodTypeAndPaymentSettingsId() {
        // Test data
        final Long paymentSettingsId = 123L;
        final AcapturePaymentMethodType acapturePaymentMethodType = AcapturePaymentMethodType.IDEAL;
        final PaymentMethodType paymentMethodType = PaymentMethodType.getPaymentMethodTypeForAcapturePaymentMethod(acapturePaymentMethodType);
        final AcapturePaymentMethodSettings acapturePaymentMethodSettings = getServicesImplTestHelper().createAcapturePaymentMethodSettings();
        // Reset
        resetAll();
        // Expectations
        expect(acapturePaymentMethodSettingsRepository.findByPaymentMethodTypeAndPaymentSettingsId(paymentMethodType, paymentSettingsId)).andReturn(acapturePaymentMethodSettings);
        // Replay
        replayAll();
        //Run test scenario
        final AcapturePaymentMethodSettings result = acapturePaymentMethodSettingsService.getAcapturePaymentMethodSettingsByPaymentMethodTypeAndPaymentSettingsId(acapturePaymentMethodType, paymentSettingsId);
        assertNotNull(result);
        assertEquals(acapturePaymentMethodSettings, result);
        // Verify
        verifyAll();
    }

    /* Override methods */
    @Override
    protected Class<AcapturePaymentMethodSettings> getInstanceClass() {
        return AcapturePaymentMethodSettings.class;
    }

    @Override
    protected AcapturePaymentMethodSettings getInstance() {
        return getServicesImplTestHelper().createAcapturePaymentMethodSettings();
    }

    @Override
    protected AbstractPaymentMethodSettingsService<AcapturePaymentMethodSettings> getService() {
        return acapturePaymentMethodSettingsService;
    }

    @Override
    protected AbstractPaymentMethodSettingsRepository<AcapturePaymentMethodSettings> getRepository() {
        return acapturePaymentMethodSettingsRepository;
    }
}
