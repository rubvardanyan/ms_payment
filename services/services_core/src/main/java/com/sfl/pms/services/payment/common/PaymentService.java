package com.sfl.pms.services.payment.common;

import com.sfl.pms.services.payment.common.dto.PaymentSearchParameters;
import com.sfl.pms.services.payment.common.dto.PaymentStateChangeHistoryRecordDto;
import com.sfl.pms.services.payment.common.dto.metadata.PaymentProviderMetadataDto;
import com.sfl.pms.services.payment.common.model.Payment;
import com.sfl.pms.services.payment.common.model.PaymentStateChangeHistoryRecord;
import com.sfl.pms.services.payment.common.model.metadata.PaymentProviderMetadata;
import com.sfl.pms.services.payment.method.model.PaymentMethodType;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * User: Ruben Dilanyan
 * Company: SFL LLC
 * Date: 1/11/15
 * Time: 11:40 AM
 */
public interface PaymentService extends AbstractPaymentService<Payment> {

    /**
     * Returns payment for id by acquiring pessimistic write lock
     *
     * @param paymentId
     * @return payment
     */
    @Nonnull
    Payment getPaymentByIdWithPessimisticWriteLock(@Nonnull final Long paymentId);

    /**
     * Gets unique customers ids for payment search parameters
     *
     * @param searchParameters
     * @param startFrom
     * @param maxCount
     * @return customersIds
     */
    @Nonnull
    List<Long> getCustomersForPaymentSearchParameters(@Nonnull final PaymentSearchParameters searchParameters, @Nonnull final Long startFrom, @Nonnull final Integer maxCount);

    /**
     * Gets unique customer count for payment search parameters
     *
     * @param searchParameters
     * @return customersCount
     */
    @Nonnull
    Long getCustomersCountForPaymentSearchParameters(@Nonnull final PaymentSearchParameters searchParameters);

    /**
     * Updates payment state
     *
     * @param paymentId
     * @param paymentStateChangeHistoryRecordDto
     * @return paymentStateChangeHistoryRecord
     */
    @Nonnull
    PaymentStateChangeHistoryRecord updatePaymentState(@Nonnull final Long paymentId, @Nonnull final PaymentStateChangeHistoryRecordDto paymentStateChangeHistoryRecordDto);

    /**
     * Updates confirmed payment method type for payment
     *
     * @param paymentId
     * @param paymentMethodType
     * @return
     */
    @Nonnull
    Payment updateConfirmedPaymentMethodType(@Nonnull final Long paymentId, @Nonnull final PaymentMethodType paymentMethodType);

    /**
     * Updates payment provider metadata for the given payment id
     * @param paymentId payment id
     * @param dto update dto
     * @return updated payment
     */
    @Nonnull
    Payment updatePaymentProviderMetadata(@Nonnull final Long paymentId, @Nonnull final PaymentProviderMetadataDto dto);
}
