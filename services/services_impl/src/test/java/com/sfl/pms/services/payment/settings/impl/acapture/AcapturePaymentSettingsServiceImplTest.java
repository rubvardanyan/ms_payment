package com.sfl.pms.services.payment.settings.impl.acapture;

import com.sfl.pms.persistence.repositories.payment.settings.AbstractPaymentProviderSettingsRepository;
import com.sfl.pms.persistence.repositories.payment.settings.acapture.AcapturePaymentSettingsRepository;
import com.sfl.pms.services.payment.provider.model.PaymentProviderType;
import com.sfl.pms.services.payment.settings.AbstractPaymentProviderSettingsService;
import com.sfl.pms.services.payment.settings.exception.PaymentProviderSettingsNotFoundForEnvironmentException;
import com.sfl.pms.services.payment.settings.impl.AbstractPaymentProviderSettingsServiceImplTest;
import com.sfl.pms.services.payment.settings.model.acapture.AcapturePaymentSettings;
import com.sfl.pms.services.system.environment.model.EnvironmentType;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Before;
import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.*;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 9/28/18
 * Time: 8:23 PM
 */
public class AcapturePaymentSettingsServiceImplTest extends AbstractPaymentProviderSettingsServiceImplTest<AcapturePaymentSettings> {

    /* Constants */
    private static final EnvironmentType ENVIRONMENT_TYPE = EnvironmentType.TEST;

    /* Test subject and mocks */
    @TestSubject
    private AcapturePaymentSettingsServiceImpl acapturePaymentSettingsService = new AcapturePaymentSettingsServiceImpl();

    @Mock
    private AcapturePaymentSettingsRepository acapturePaymentSettingsRepository;

    @Before
    public void setUp() throws Exception {
        ReflectionTestUtils.setField(acapturePaymentSettingsService, "environmentType", ENVIRONMENT_TYPE.toString());
    }

    /* Test methods */
    @Test
    public void testGetActivePaymentSettingsWhenDoesNotExist() {
        // Reset
        resetAll();
        // Expectations
        expect(acapturePaymentSettingsRepository.findByTypeAndEnvironmentType(PaymentProviderType.ACAPTURE, ENVIRONMENT_TYPE)).andReturn(null);
        // Replay
        replayAll();
        // Run test scenario
        try {
            acapturePaymentSettingsService.getActivePaymentSettings();
            fail("Exception should be thrown");
        } catch (PaymentProviderSettingsNotFoundForEnvironmentException ex) {
            //success
        }
        // Verify
        verifyAll();
    }

    /* Test methods */
    @Test
    public void testGetActivePaymentSettings() {
        //Test data
        final AcapturePaymentSettings paymentSettings = getServicesImplTestHelper().createAcapturePaymentSettings();
        // Reset
        resetAll();
        // Expectations
        expect(acapturePaymentSettingsRepository.findByTypeAndEnvironmentType(PaymentProviderType.ACAPTURE, ENVIRONMENT_TYPE)).andReturn(paymentSettings);
        // Replay
        replayAll();
        // Run test scenario
        final AcapturePaymentSettings result = acapturePaymentSettingsService.getActivePaymentSettings();
        assertNotNull(result);
        assertEquals(paymentSettings, result);
        // Verify
        verifyAll();
    }

    /* Override methods */
    @Override
    protected AbstractPaymentProviderSettingsService<AcapturePaymentSettings> getService() {
        return acapturePaymentSettingsService;
    }

    @Override
    protected AbstractPaymentProviderSettingsRepository<AcapturePaymentSettings> getRepository() {
        return acapturePaymentSettingsRepository;
    }

    @Override
    protected Class<AcapturePaymentSettings> getInstanceClass() {
        return AcapturePaymentSettings.class;
    }

    @Override
    protected AcapturePaymentSettings getInstance() {
        return getServicesImplTestHelper().createAcapturePaymentSettings();
    }
}