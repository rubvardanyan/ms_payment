package com.sfl.pms.services.payment.redirect.impl.adyen;

import com.sfl.pms.persistence.repositories.payment.redirect.AbstractPaymentProviderRedirectResultRepository;
import com.sfl.pms.persistence.repositories.payment.redirect.adyen.AdyenRedirectResultRepository;
import com.sfl.pms.services.payment.redirect.adyen.AdyenRedirectResultService;
import com.sfl.pms.services.payment.redirect.dto.redirect.adyen.AdyenRedirectResultDto;
import com.sfl.pms.services.payment.redirect.impl.AbstractPaymentProviderRedirectResultServiceImpl;
import com.sfl.pms.services.payment.redirect.model.adyen.AdyenRedirectResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Nonnull;

/**
 * User: Ruben Dilanyan
 * Company: SFL LLC
 * Date: 7/11/15
 * Time: 12:38 PM
 */
@Service
public class AdyenRedirectResultServiceImpl extends AbstractPaymentProviderRedirectResultServiceImpl<AdyenRedirectResult> implements AdyenRedirectResultService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdyenRedirectResultServiceImpl.class);

    /* Dependencies */
    @Autowired
    private AdyenRedirectResultRepository adyenRedirectResultRepository;

    /* Constructors */
    public AdyenRedirectResultServiceImpl() {
        LOGGER.debug("Initializing Adyen redirect result service");
    }

    @Transactional
    @Nonnull
    @Override
    public AdyenRedirectResult createPaymentProviderRedirectResult(@Nonnull final AdyenRedirectResultDto redirectResultDto) {
        assertAdyenRedirectResultDto(redirectResultDto);
        LOGGER.debug("Creating Adyen redirect result using DTO - {}", redirectResultDto);
        AdyenRedirectResult adyenRedirectResult = new AdyenRedirectResult(true);
        redirectResultDto.updateDomainEntityProperties(adyenRedirectResult);
        adyenRedirectResult = adyenRedirectResultRepository.save(adyenRedirectResult);
        LOGGER.debug("Successfully created Adyen redirect result with id - {}, result - {}", adyenRedirectResult.getId(), adyenRedirectResult);
        return adyenRedirectResult;
    }


    /* Utility methods */
    private void assertAdyenRedirectResultDto(final AdyenRedirectResultDto resultDto) {
        Assert.notNull(resultDto, "Result DTO should not be null");
        Assert.notNull(resultDto.getAuthResult(), "Auth result in result DTO should not be null");
        Assert.notNull(resultDto.getMerchantReference(), "Merchant reference in result DTO should not be null");
        Assert.notNull(resultDto.getSkinCode(), "Skin code in result DTO should not be null");
        Assert.notNull(resultDto.getMerchantSig(), "Merchant signature in result DTO should not be null");
    }

    @Override
    protected AbstractPaymentProviderRedirectResultRepository<AdyenRedirectResult> getRepository() {
        return adyenRedirectResultRepository;
    }

    @Override
    protected Class<AdyenRedirectResult> getInstanceClass() {
        return AdyenRedirectResult.class;
    }

    /* Properties getters and setters */
    public AdyenRedirectResultRepository getAdyenRedirectResultRepository() {
        return adyenRedirectResultRepository;
    }

    public void setAdyenRedirectResultRepository(final AdyenRedirectResultRepository adyenRedirectResultRepository) {
        this.adyenRedirectResultRepository = adyenRedirectResultRepository;
    }
}
