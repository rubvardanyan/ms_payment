package com.sfl.pms.services.payment.method.impl.acapture;

import com.sfl.pms.persistence.repositories.payment.method.AbstractPaymentMethodSettingsRepository;
import com.sfl.pms.persistence.repositories.payment.method.acapture.AcapturePaymentMethodSettingsRepository;
import com.sfl.pms.services.common.exception.ServicesRuntimeException;
import com.sfl.pms.services.payment.method.acapture.AcapturePaymentMethodSettingsService;
import com.sfl.pms.services.payment.method.impl.AbstractPaymentMethodSettingsServiceImpl;
import com.sfl.pms.services.payment.method.model.PaymentMethodSettings;
import com.sfl.pms.services.payment.method.model.PaymentMethodType;
import com.sfl.pms.services.payment.method.model.acapture.AcapturePaymentMethodSettings;
import com.sfl.pms.services.payment.method.model.acapture.AcapturePaymentMethodType;
import com.sfl.pms.services.payment.settings.impl.acapture.AcapturePaymentSettingsServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Nonnull;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 9/28/18
 * Time: 9:50 PM
 */
@Service
public class AcapturePaymentMethodSettingsServiceImpl extends AbstractPaymentMethodSettingsServiceImpl<AcapturePaymentMethodSettings> implements AcapturePaymentMethodSettingsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AcapturePaymentSettingsServiceImpl.class);

    /* Dependencies */
    @Autowired
    private AcapturePaymentMethodSettingsRepository acapturePaymentSettingsRepository;

    /* Constructor */
    public AcapturePaymentMethodSettingsServiceImpl() {
        LOGGER.debug("Initializing acapture payment method settings service");
    }

    /* Public methods */

    @Nonnull
    @Override
    public AcapturePaymentMethodSettings getAcapturePaymentMethodSettingsByPaymentMethodTypeAndPaymentSettingsId(@Nonnull final AcapturePaymentMethodType acapturePaymentMethodType, @Nonnull final Long paymentSettingsId) {
        assertAcapturePaymentMethodType(acapturePaymentMethodType);
        Assert.notNull(paymentSettingsId, "Payment settings id should not be null");
        final PaymentMethodType paymentMethodType = PaymentMethodType.getPaymentMethodTypeForAcapturePaymentMethod(acapturePaymentMethodType);
        LOGGER.debug("Getting acapture payment method settings for payment method type - {}, payment settings id - {}", paymentMethodType, paymentSettingsId);
        final AcapturePaymentMethodSettings paymentMethodSettings = acapturePaymentSettingsRepository.findByPaymentMethodTypeAndPaymentSettingsId(paymentMethodType, paymentSettingsId);
        assertPaymentMethodSettingsFoundForPaymentTypeAndPaymentSettingsId(paymentMethodSettings, paymentMethodType, paymentSettingsId);
        return paymentMethodSettings;
    }

    /* Override methods */
    @Override
    protected AbstractPaymentMethodSettingsRepository<AcapturePaymentMethodSettings> getRepository() {
        return acapturePaymentSettingsRepository;
    }

    @Override
    protected Class<AcapturePaymentMethodSettings> getInstanceClass() {
        return AcapturePaymentMethodSettings.class;
    }

    /* Utility methods */
    private void assertAcapturePaymentMethodType(final AcapturePaymentMethodType acapturePaymentMethodType) {
        Assert.notNull(acapturePaymentMethodType, "Acapture payment method type should not be null");
    }

    private void assertPaymentMethodSettingsFoundForPaymentTypeAndPaymentSettingsId(final PaymentMethodSettings paymentMethodSettings, final PaymentMethodType paymentMethodType, final Long paymentMethodSettingsId) {
        if(paymentMethodSettings == null) {
            LOGGER.error("Payment method settings not found for payment method type - {}, payment settings id - {}", paymentMethodType, paymentMethodSettingsId);
            throw new ServicesRuntimeException("Payment method settings not found for payment method type - " + paymentMethodType + " payment settings id - " + paymentMethodSettingsId);
        }
    }
}
