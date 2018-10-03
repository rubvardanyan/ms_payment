package com.sfl.pms.services.payment.settings.impl.acapture;

import com.sfl.pms.persistence.repositories.payment.settings.AbstractPaymentProviderSettingsRepository;
import com.sfl.pms.persistence.repositories.payment.settings.acapture.AcapturePaymentSettingsRepository;
import com.sfl.pms.services.payment.provider.model.PaymentProviderType;
import com.sfl.pms.services.payment.settings.acapture.AcapturePaymentSettingsService;
import com.sfl.pms.services.payment.settings.exception.PaymentProviderSettingsNotFoundForEnvironmentException;
import com.sfl.pms.services.payment.settings.impl.AbstractPaymentProviderSettingsServiceImpl;
import com.sfl.pms.services.payment.settings.model.acapture.AcapturePaymentSettings;
import com.sfl.pms.services.system.environment.model.EnvironmentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 9/28/18
 * Time: 8:20 PM
 */
@Service
public class AcapturePaymentSettingsServiceImpl extends AbstractPaymentProviderSettingsServiceImpl<AcapturePaymentSettings> implements AcapturePaymentSettingsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AcapturePaymentSettingsServiceImpl.class);

    /* Dependencies */
    @Autowired
    private AcapturePaymentSettingsRepository acapturePaymentSettingsRepository;

    @Value("#{ appProperties['acapture.environment.type']}")
    private String environmentType;

    /* Constructor */
    public AcapturePaymentSettingsServiceImpl() {
        LOGGER.debug("Initializing acapture payment settings service");
    }

    /* Public methods */
    @Nonnull
    @Override
    public AcapturePaymentSettings getActivePaymentSettings() {
        final EnvironmentType currentEnvironmentType = getEnvironmentType();
        LOGGER.debug("Getting acapture payment settings, environment type - {}", currentEnvironmentType);
        final AcapturePaymentSettings paymentSettings = acapturePaymentSettingsRepository.findByTypeAndEnvironmentType(PaymentProviderType.ACAPTURE, currentEnvironmentType);
        assertAcapturePaymentSettingsFoundForEnvironment(paymentSettings, currentEnvironmentType);
        LOGGER.debug("Found acapture payment settings for environment type - {}", currentEnvironmentType);
        return paymentSettings;
    }

    /* Utility methods */
    @Override
    protected AbstractPaymentProviderSettingsRepository<AcapturePaymentSettings> getRepository() {
        return acapturePaymentSettingsRepository;
    }

    @Override
    protected Class<AcapturePaymentSettings> getInstanceClass() {
        return AcapturePaymentSettings.class;
    }

    private EnvironmentType getEnvironmentType() {
        return EnvironmentType.valueOf(environmentType);
    }

    private void assertAcapturePaymentSettingsFoundForEnvironment(final AcapturePaymentSettings paymentSettings, final EnvironmentType environmentType) {
        if(paymentSettings == null) {
            LOGGER.error("Acapture payment settings not found for environment type - {}", environmentType);
            throw new PaymentProviderSettingsNotFoundForEnvironmentException(PaymentProviderType.ACAPTURE, environmentType);
        }
    }
}
