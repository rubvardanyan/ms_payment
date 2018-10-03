package com.sfl.pms.persistence.repositories.payment.method;

import com.sfl.pms.services.payment.method.model.PaymentMethodSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 9/28/18
 * Time: 9:22 PM
 */
@Repository
public interface AbstractPaymentMethodSettingsRepository<T extends PaymentMethodSettings> extends JpaRepository<T, Long> {
}
