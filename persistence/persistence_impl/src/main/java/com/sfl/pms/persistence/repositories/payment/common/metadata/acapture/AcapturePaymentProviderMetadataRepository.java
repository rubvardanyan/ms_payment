package com.sfl.pms.persistence.repositories.payment.common.metadata.acapture;

import com.sfl.pms.services.payment.common.model.acapture.AcapturePaymentProviderMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.annotation.Nonnull;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 10/1/18
 * Time: 2:42 AM
 */
@Repository
public interface AcapturePaymentProviderMetadataRepository extends JpaRepository<AcapturePaymentProviderMetadata, Long> {

    AcapturePaymentProviderMetadata findByCheckoutId(@Nonnull final String checkoutId);
}
