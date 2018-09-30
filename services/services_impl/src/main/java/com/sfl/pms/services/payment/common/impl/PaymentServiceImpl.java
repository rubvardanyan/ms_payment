package com.sfl.pms.services.payment.common.impl;

import com.sfl.pms.persistence.repositories.payment.common.AbstractPaymentRepository;
import com.sfl.pms.persistence.repositories.payment.common.PaymentRepository;
import com.sfl.pms.services.common.exception.ServicesRuntimeException;
import com.sfl.pms.services.payment.common.PaymentService;
import com.sfl.pms.services.payment.common.dto.PaymentSearchParameters;
import com.sfl.pms.services.payment.common.dto.PaymentStateChangeHistoryRecordDto;
import com.sfl.pms.services.payment.common.dto.metadata.PaymentProviderMetadataDto;
import com.sfl.pms.services.payment.common.impl.metadata.AcapturePaymentProviderMetadataHandler;
import com.sfl.pms.services.payment.common.impl.metadata.PaymentProviderMetadataHandler;
import com.sfl.pms.services.payment.common.model.Payment;
import com.sfl.pms.services.payment.common.model.PaymentStateChangeHistoryRecord;
import com.sfl.pms.services.payment.common.model.metadata.PaymentProviderMetadata;
import com.sfl.pms.services.payment.method.model.PaymentMethodType;
import com.sfl.pms.services.payment.provider.exception.UnknownPaymentProviderTypeException;
import com.sfl.pms.services.payment.provider.model.PaymentProviderType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import javax.annotation.Nonnull;
import java.util.List;

/**
 * User: Ruben Dilanyan
 * Company: SFL LLC
 * Date: 1/11/15
 * Time: 12:08 PM
 */
@Service
public class PaymentServiceImpl extends AbstractPaymentServiceImpl<Payment> implements PaymentService {
    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentServiceImpl.class);

    /* Dependencies */
    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private AcapturePaymentProviderMetadataHandler acapturePaymentProviderMetadataHandler;

    /* Constructors */
    public PaymentServiceImpl() {
        LOGGER.debug("Initializing payment service");
    }

    @Transactional
    @Nonnull
    @Override
    public Payment getPaymentByIdWithPessimisticWriteLock(@Nonnull final Long paymentId) {
        assertPaymentIdNotNull(paymentId);
        LOGGER.debug("Loading payment for id - {}, payment class - {} with pessimistic write lock", paymentId, getInstanceClass());
        final Payment payment = paymentRepository.findByIdWithWriteLockFlushedAndFreshData(paymentId);
        assertPaymentNotNullForId(payment, paymentId);
        LOGGER.debug("Successfully retrieved payment for id - {}, payment class - {} with pessimistic write lock", paymentId, getInstanceClass());
        return payment;
    }

    @Nonnull
    @Override
    public List<Long> getCustomersForPaymentSearchParameters(@Nonnull final PaymentSearchParameters searchParameters, @Nonnull final Long startFrom, @Nonnull final Integer maxCount) {
        assertPaymentSearchParameters(searchParameters);
        Assert.notNull(startFrom, "Start from should not be null");
        Assert.isTrue(startFrom >= 0L, "Start from should be greater or equal then 0");
        Assert.notNull(maxCount, "Results max count should not be null");
        Assert.isTrue(maxCount > 0, "Max count should be greater then 0");
        LOGGER.debug("Searching for customers for payment method search parameters - {}, start from - {}, max count - {}", searchParameters, startFrom, maxCount);
        final List<Long> customers = paymentRepository.findCustomersForPaymentSearchParameters(searchParameters, startFrom, maxCount);
        LOGGER.debug("Got {} customers for payment method search parameters - {}, start from - {}, max count - {}", customers.size(), searchParameters, startFrom, maxCount);
        return customers;
    }

    @Nonnull
    @Override
    public Long getCustomersCountForPaymentSearchParameters(@Nonnull final PaymentSearchParameters searchParameters) {
        assertPaymentSearchParameters(searchParameters);
        LOGGER.debug("Getting customers count for payment search parameters - {}", searchParameters);
        final Long count = paymentRepository.getCustomersCountForPaymentSearchParameters(searchParameters);
        LOGGER.debug("Found - {} customers for search parameters - {}", count, searchParameters);
        return count;
    }

    @Transactional
    @Nonnull
    @Override
    public PaymentStateChangeHistoryRecord updatePaymentState(@Nonnull final Long paymentId, @Nonnull final PaymentStateChangeHistoryRecordDto paymentStateChangeHistoryRecordDto) {
        assertPaymentIdNotNull(paymentId);
        assertPaymentStateChangeHistoryRecordDto(paymentStateChangeHistoryRecordDto);
        LOGGER.debug("Updating state for payment with id - {}, payment state change record DTO - {}", paymentId, paymentStateChangeHistoryRecordDto);
        Payment payment = paymentRepository.findByIdWithWriteLockFlushedAndFreshData(paymentId);
        assertPaymentNotNullForId(payment, paymentId);
        final PaymentStateChangeHistoryRecord historyRecord = payment.updatePaymentState(paymentStateChangeHistoryRecordDto.getUpdatedState(), paymentStateChangeHistoryRecordDto.getInformation());
        // Update payment
        payment = getRepository().saveAndFlush(payment);
        LOGGER.debug("Successfully update payment state for payment with id - {}, payment state change history record - {}", payment.getId(), historyRecord);
        return historyRecord;
    }

    @Transactional
    @Nonnull
    @Override
    public Payment updateConfirmedPaymentMethodType(@Nonnull final Long paymentId, @Nonnull final PaymentMethodType paymentMethodType) {
        assertPaymentIdNotNull(paymentId);
        Assert.notNull(paymentMethodType, "Payment method type should not be null");
        LOGGER.debug("Updating confirmed payment method for payment with id - {}, conformed payment method- {}", paymentId, paymentMethodType);
        // Make sure to plush before loading fresh copy from database
        Payment payment = paymentRepository.findByIdWithWriteLockFlushedAndFreshData(paymentId);
        assertPaymentNotNullForId(payment, paymentId);
        // Update confirmed payment method
        payment.setConfirmedPaymentMethodType(paymentMethodType);
        payment = paymentRepository.saveAndFlush(payment);
        LOGGER.debug("Successfully updated confirmed payment method for payment with id - {}, payment method - {}", paymentId, paymentMethodType);
        return payment;
    }

    @Nonnull
    @Override
    public Payment updatePaymentProviderMetadata(@Nonnull final Long paymentId, @Nonnull final PaymentProviderMetadataDto dto) {
        assertPaymentIdNotNull(paymentId);
        Assert.notNull(dto);
        Assert.notNull(dto.getPaymentProviderType());
        final Payment payment = getRepository().findOne(paymentId);
        final PaymentProviderMetadataHandler paymentProviderMetadataHandler = getPaymentProviderMetadataHandler(dto.getPaymentProviderType());
        final PaymentProviderMetadata paymentProviderMetadata = paymentProviderMetadataHandler.convertPaymentProviderMetadataDto(dto);
        payment.getPaymentProcessingChannel().setPaymentProviderMetadata(paymentProviderMetadata);
        paymentProviderMetadata.setPaymentProcessingChannel(payment.getPaymentProcessingChannel());
        return getRepository().save(payment);
    }

    /* Utility methods */
    @Override
    protected AbstractPaymentRepository<Payment> getRepository() {
        return paymentRepository;
    }

    @Override
    protected Class<Payment> getInstanceClass() {
        return Payment.class;
    }

    private void assertPaymentSearchParameters(final PaymentSearchParameters searchParameters) {
        Assert.notNull(searchParameters, "Payment search parameters should not be null");
    }

    private void assertPaymentStateChangeHistoryRecordDto(final PaymentStateChangeHistoryRecordDto recordDto) {
        Assert.notNull(recordDto, "Payment state change history record DTO should not be null");
        Assert.notNull(recordDto.getUpdatedState(), "Updated state in payment state change history record DTO should not be null");
    }

    private PaymentProviderMetadataHandler getPaymentProviderMetadataHandler(final PaymentProviderType paymentProviderType) {
        switch (paymentProviderType) {
            case ACAPTURE:
                return acapturePaymentProviderMetadataHandler;
            default:
                LOGGER.error("Unknown payment provider type - {}", paymentProviderType);
                throw new UnknownPaymentProviderTypeException(paymentProviderType);
        }
    }


    /* Properties getters and setters */
    public PaymentRepository getPaymentRepository() {
        return paymentRepository;
    }

    public void setPaymentRepository(final PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }
}
