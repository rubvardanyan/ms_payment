package com.sfl.pms.services.payment.redirect.impl.acapture;

import com.sfl.pms.persistence.repositories.payment.redirect.AbstractPaymentProviderRedirectResultRepository;
import com.sfl.pms.persistence.repositories.payment.redirect.acapture.AcaptureRedirectResultRepository;
import com.sfl.pms.services.payment.redirect.acapture.AcaptureRedirectResultService;
import com.sfl.pms.services.payment.redirect.dto.redirect.acapture.AcaptureRedirectResultDto;
import com.sfl.pms.services.payment.redirect.impl.AbstractPaymentProviderRedirectResultServiceImpl;
import com.sfl.pms.services.payment.redirect.model.acapture.AcaptureRedirectResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Nonnull;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 9/30/18
 * Time: 11:11 PM
 */
@Service
public class AcaptureRedirectResultServiceImpl extends AbstractPaymentProviderRedirectResultServiceImpl<AcaptureRedirectResult> implements AcaptureRedirectResultService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AcaptureRedirectResultServiceImpl.class);

    /* Dependencies */
    @Autowired
    private AcaptureRedirectResultRepository acaptureRedirectResultRepository;

    /* Constructor */
    public AcaptureRedirectResultServiceImpl() {
        LOGGER.debug("Initializing acapture redirect result service");
    }

    /* Public methods */
    @Nonnull
    @Override
    public AcaptureRedirectResult createPaymentProviderRedirectResult(@Nonnull final AcaptureRedirectResultDto redirectResultDto) {
        assertAcaptureRedirectResultDto(redirectResultDto);
        final AcaptureRedirectResult acaptureRedirectResult = new AcaptureRedirectResult(true);
        redirectResultDto.updateDomainEntityProperties(acaptureRedirectResult);
        return acaptureRedirectResultRepository.save(acaptureRedirectResult);
    }

    @Override
    protected AbstractPaymentProviderRedirectResultRepository<AcaptureRedirectResult> getRepository() {
        return acaptureRedirectResultRepository;
    }

    @Override
    protected Class<AcaptureRedirectResult> getInstanceClass() {
        return AcaptureRedirectResult.class;
    }

    /* Utility methods */
    private void assertAcaptureRedirectResultDto(final AcaptureRedirectResultDto dto) {
        Assert.notNull(dto, "Acapture redirect result dto should not be null");
        Assert.notNull(dto.getCheckoutId(), "Checkout id in acapture redirect result dto should not be null");
        Assert.notNull(dto.getResourcePath(), "Resource path in acapture redirect result dto should not be null");
    }
}
