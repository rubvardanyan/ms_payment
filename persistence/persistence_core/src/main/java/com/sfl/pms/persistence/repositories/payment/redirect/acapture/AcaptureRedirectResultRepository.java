package com.sfl.pms.persistence.repositories.payment.redirect.acapture;

import com.sfl.pms.persistence.repositories.payment.redirect.AbstractPaymentProviderRedirectResultRepository;
import com.sfl.pms.services.payment.redirect.model.acapture.AcaptureRedirectResult;
import org.springframework.stereotype.Repository;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 9/30/18
 * Time: 11:13 PM
 */
@Repository
public interface AcaptureRedirectResultRepository extends AbstractPaymentProviderRedirectResultRepository<AcaptureRedirectResult> {
}
