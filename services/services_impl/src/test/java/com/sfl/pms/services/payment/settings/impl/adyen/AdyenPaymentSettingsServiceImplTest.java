package com.sfl.pms.services.payment.settings.impl.adyen;

import com.sfl.pms.persistence.repositories.payment.settings.AbstractPaymentProviderSettingsRepository;
import com.sfl.pms.persistence.repositories.payment.settings.adyen.AdyenPaymentSettingsRepository;
import com.sfl.pms.services.payment.provider.model.PaymentProviderType;
import com.sfl.pms.services.payment.settings.AbstractPaymentProviderSettingsService;
import com.sfl.pms.services.payment.settings.dto.adyen.AdyenPaymentSettingsDto;
import com.sfl.pms.services.payment.settings.exception.PaymentProviderSettingsExistsForEnvironmentException;
import com.sfl.pms.services.payment.settings.exception.PaymentProviderSettingsNotFoundForEnvironmentException;
import com.sfl.pms.services.payment.settings.impl.AbstractPaymentProviderSettingsServiceImplTest;
import com.sfl.pms.services.payment.settings.impl.adyen.AdyenPaymentSettingsServiceImpl;
import com.sfl.pms.services.payment.settings.model.adyen.AdyenPaymentSettings;
import com.sfl.pms.services.system.environment.model.EnvironmentType;
import org.easymock.IAnswer;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.junit.Test;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

/**
 * User: Ruben Dilanyan
 * Company: SFL LLC
 * Date: 1/6/15
 * Time: 1:14 PM
 */
public class AdyenPaymentSettingsServiceImplTest extends AbstractPaymentProviderSettingsServiceImplTest<AdyenPaymentSettings> {

    /* Test subject and mocks */
    @TestSubject
    private AdyenPaymentSettingsServiceImpl adyenPaymentSettingsService = new AdyenPaymentSettingsServiceImpl();

    @Mock
    private AdyenPaymentSettingsRepository adyenPaymentSettingsRepository;

    /* Constructors */
    public AdyenPaymentSettingsServiceImplTest() {
    }

    /* Test methods */
    @Test
    public void testGetActivePaymentSettingsWhenNoMatchingSettingsExist() {
        // Test data
        adyenPaymentSettingsService.setEnvironmentType(EnvironmentType.ACCEPTANCE.toString());
        // Reset
        resetAll();
        // Expectations
        expect(adyenPaymentSettingsRepository.findByTypeAndEnvironmentType(eq(PaymentProviderType.ADYEN), eq(EnvironmentType.ACCEPTANCE))).andReturn(null).once();
        // Replay
        replayAll();
        // Run test scenario
        try {
            adyenPaymentSettingsService.getActivePaymentSettings();
            fail("Exception should be thrown");
        } catch (final PaymentProviderSettingsNotFoundForEnvironmentException ex) {
            // Expected
            assertPaymentProviderSettingsNotFoundForEnvironmentException(ex, EnvironmentType.ACCEPTANCE, PaymentProviderType.ADYEN);
        }
        // Verify
        verifyAll();
    }

    @Test
    public void testGetActivePaymentSettings() {
        // Test data
        final AdyenPaymentSettings adyenPaymentSettings = getServicesImplTestHelper().createAdyenPaymentSettings();
        adyenPaymentSettingsService.setEnvironmentType(EnvironmentType.ACCEPTANCE.toString());
        // Reset
        resetAll();
        // Expectations
        expect(adyenPaymentSettingsRepository.findByTypeAndEnvironmentType(eq(PaymentProviderType.ADYEN), eq(EnvironmentType.ACCEPTANCE))).andReturn(adyenPaymentSettings).once();
        // Replay
        replayAll();
        // Run test scenario
        final AdyenPaymentSettings result = adyenPaymentSettingsService.getActivePaymentSettings();
        assertNotNull(adyenPaymentSettings);
        assertEquals(adyenPaymentSettings, result);
        // Verify
        verifyAll();
    }

    @Test
    public void testCreatePaymentProviderSettingsWithInvalidArguments() {
        // Test data
        final AdyenPaymentSettingsDto paymentSettingsDto = getServicesImplTestHelper().createAdyenPaymentSettingsDto();
        // Reset
        resetAll();
        // Replay
        replayAll();
        // Run test scenario
        try {
            adyenPaymentSettingsService.createPaymentProviderSettings(null);
            fail("Exception should be thrown");
        } catch (final IllegalArgumentException ex) {
            // Expected
        }
        try {
            adyenPaymentSettingsService.createPaymentProviderSettings(new AdyenPaymentSettingsDto(null, paymentSettingsDto.getUserName(), paymentSettingsDto.getPassword(), paymentSettingsDto.getNotificationsToken()));
            fail("Exception should be thrown");
        } catch (final IllegalArgumentException ex) {
            // Expected
        }
        try {
            adyenPaymentSettingsService.createPaymentProviderSettings(new AdyenPaymentSettingsDto(paymentSettingsDto.getEnvironmentType(), null, paymentSettingsDto.getPassword(), paymentSettingsDto.getNotificationsToken()));
            fail("Exception should be thrown");
        } catch (final IllegalArgumentException ex) {
            // Expected
        }
        try {
            adyenPaymentSettingsService.createPaymentProviderSettings(new AdyenPaymentSettingsDto(paymentSettingsDto.getEnvironmentType(), paymentSettingsDto.getUserName(), null, paymentSettingsDto.getNotificationsToken()));
            fail("Exception should be thrown");
        } catch (final IllegalArgumentException ex) {
            // Expected
        }
        try {
            adyenPaymentSettingsService.createPaymentProviderSettings(new AdyenPaymentSettingsDto(paymentSettingsDto.getEnvironmentType(), paymentSettingsDto.getUserName(), paymentSettingsDto.getPassword(), null));
            fail("Exception should be thrown");
        } catch (final IllegalArgumentException ex) {
            // Expected
        }
        // Verify
        verifyAll();
    }

    @Test
    public void testCreatePaymentProviderSettingsWhenSettingsExistsForEnvironment() {
        // Test data
        final AdyenPaymentSettingsDto paymentSettingsDto = getServicesImplTestHelper().createAdyenPaymentSettingsDto();
        final AdyenPaymentSettings paymentSettings = getServicesImplTestHelper().createAdyenPaymentSettings(paymentSettingsDto);
        // Reset
        resetAll();
        // Expectations
        expect(adyenPaymentSettingsRepository.findByTypeAndEnvironmentType(eq(PaymentProviderType.ADYEN), eq(paymentSettingsDto.getEnvironmentType()))).andReturn(paymentSettings).once();
        // Replay
        replayAll();
        // Run test scenario
        try {
            adyenPaymentSettingsService.createPaymentProviderSettings(paymentSettingsDto);
            fail("Exception should be thrown");
        } catch (final PaymentProviderSettingsExistsForEnvironmentException ex) {
            // Expected
            assertPaymentProviderSettingsExistsForEnvironmentException(ex, paymentSettingsDto.getEnvironmentType(), PaymentProviderType.ADYEN);
        }
        // Verify
        verifyAll();
    }

    @Test
    public void testCreatePaymentProviderSettings() {
        // Test data
        final AdyenPaymentSettingsDto paymentSettingsDto = getServicesImplTestHelper().createAdyenPaymentSettingsDto();
        // Reset
        resetAll();
        // Expectations
        expect(adyenPaymentSettingsRepository.findByTypeAndEnvironmentType(eq(PaymentProviderType.ADYEN), eq(paymentSettingsDto.getEnvironmentType()))).andReturn(null).once();
        expect(adyenPaymentSettingsRepository.save(isA(AdyenPaymentSettings.class))).andAnswer(new IAnswer<AdyenPaymentSettings>() {
            @Override
            public AdyenPaymentSettings answer() throws Throwable {
                return (AdyenPaymentSettings) getCurrentArguments()[0];
            }
        }).once();
        // Replay
        replayAll();
        // Run test scenario
        final AdyenPaymentSettings paymentSettings = adyenPaymentSettingsService.createPaymentProviderSettings(paymentSettingsDto);
        getServicesImplTestHelper().assertAdyenPaymentSettings(paymentSettings, paymentSettingsDto);
        // Verify
        verifyAll();
    }

    /* Utility methods */
    private void assertPaymentProviderSettingsNotFoundForEnvironmentException(final PaymentProviderSettingsNotFoundForEnvironmentException ex, final EnvironmentType environmentType, final PaymentProviderType paymentProviderType) {
        assertEquals(environmentType, ex.getEnvironmentType());
        assertEquals(paymentProviderType, ex.getPaymentProviderType());
    }

    private void assertPaymentProviderSettingsExistsForEnvironmentException(final PaymentProviderSettingsExistsForEnvironmentException ex, final EnvironmentType environmentType, final PaymentProviderType paymentProviderType) {
        assertEquals(paymentProviderType, ex.getPaymentProviderType());
        assertEquals(environmentType, ex.getEnvironmentType());
    }

    @Override
    protected AbstractPaymentProviderSettingsService<AdyenPaymentSettings> getService() {
        return adyenPaymentSettingsService;
    }

    @Override
    protected AbstractPaymentProviderSettingsRepository<AdyenPaymentSettings> getRepository() {
        return adyenPaymentSettingsRepository;
    }

    @Override
    protected Class<AdyenPaymentSettings> getInstanceClass() {
        return AdyenPaymentSettings.class;
    }

    @Override
    protected AdyenPaymentSettings getInstance() {
        return getServicesImplTestHelper().createAdyenPaymentSettings();
    }
}
