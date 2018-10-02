package com.sfl.pms.services.payment.common.impl;

import com.sfl.pms.persistence.repositories.payment.common.AbstractPaymentRepository;
import com.sfl.pms.persistence.repositories.payment.common.PaymentRepository;
import com.sfl.pms.services.payment.common.AbstractPaymentService;
import com.sfl.pms.services.payment.common.dto.PaymentSearchParameters;
import com.sfl.pms.services.payment.common.dto.PaymentStateChangeHistoryRecordDto;
import com.sfl.pms.services.payment.common.dto.acapture.AcapturePaymentProviderMetadataDto;
import com.sfl.pms.services.payment.common.exception.PaymentNotFoundForIdException;
import com.sfl.pms.services.payment.common.impl.metadata.AcapturePaymentProviderMetadataHandler;
import com.sfl.pms.services.payment.common.model.Payment;
import com.sfl.pms.services.payment.common.model.PaymentState;
import com.sfl.pms.services.payment.common.model.PaymentStateChangeHistoryRecord;
import com.sfl.pms.services.payment.common.model.acapture.AcapturePaymentProviderMetadata;
import com.sfl.pms.services.payment.method.model.PaymentMethodType;
import org.easymock.Mock;
import org.easymock.TestSubject;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static org.easymock.EasyMock.*;
import static org.junit.Assert.*;

/**
 * User: Ruben Dilanyan
 * Company: SFL LLC
 * Date: 1/11/15
 * Time: 12:23 PM
 */
public class PaymentServiceImplTest extends AbstractPaymentServiceImplTest<Payment> {

    /* Test subject and mocks */
    @TestSubject
    private PaymentServiceImpl paymentService = new PaymentServiceImpl();

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private AcapturePaymentProviderMetadataHandler acapturePaymentProviderMetadataHandler;

    /* Constructors */
    public PaymentServiceImplTest() {
    }

    /* Test methods */
    @Test
    public void testUpdateConfirmedPaymentMethodTypeWithInvalidArguments() {
        // Test data
        final Long paymentId = 1L;
        final PaymentMethodType paymentMethodType = PaymentMethodType.IDEAL;
        // Reset
        resetAll();
        // Replay
        replayAll();
        // Run test scenario
        try {
            paymentService.updateConfirmedPaymentMethodType(null, paymentMethodType);
            fail("Exception should be thrown");
        } catch (final IllegalArgumentException ex) {
            // Expected
        }
        try {
            paymentService.updateConfirmedPaymentMethodType(paymentId, null);
            fail("Exception should be thrown");
        } catch (final IllegalArgumentException ex) {
            // Expected
        }
        // Verify
        verifyAll();
    }

    @Test
    public void testUpdateConfirmedPaymentMethodTypeWithNotExistingPaymentId() {
        // Test data
        final Long paymentId = 1L;
        final PaymentMethodType paymentMethodType = PaymentMethodType.IDEAL;
        // Reset
        resetAll();
        // Expectations
        expect(paymentRepository.findByIdWithWriteLockFlushedAndFreshData(eq(paymentId))).andReturn(null).once();
        // Replay
        replayAll();
        // Run test scenario
        try {
            paymentService.updateConfirmedPaymentMethodType(paymentId, paymentMethodType);
            fail("Exception should be thrown");
        } catch (final PaymentNotFoundForIdException ex) {
            // Expected
            assertPaymentNotFoundForIdException(ex, paymentId);
        }
        // Verify
        verifyAll();
    }

    @Test
    public void testUpdateConfirmedPaymentMethodType() {
        // Test data
        final Long paymentId = 1L;
        final Payment payment = getInstance();
        payment.setId(paymentId);
        final PaymentMethodType paymentMethodType = PaymentMethodType.IDEAL;
        // Reset
        resetAll();
        // Expectations
        expect(paymentRepository.findByIdWithWriteLockFlushedAndFreshData(eq(paymentId))).andReturn(payment).once();
        expect(paymentRepository.saveAndFlush(isA(getInstanceClass()))).andAnswer(() -> (Payment) getCurrentArguments()[0]).once();
        // Replay
        replayAll();
        // Run test scenario
        final Payment result = paymentService.updateConfirmedPaymentMethodType(paymentId, paymentMethodType);
        assertNotNull(result);
        Assert.assertEquals(paymentMethodType, result.getConfirmedPaymentMethodType());
        // Verify
        verifyAll();
    }

    @Test
    public void testUpdatePaymentStateWithIllegalArguments() {
        // Test data
        final Long paymentId = 1l;
        final PaymentStateChangeHistoryRecordDto recordDto = getServicesImplTestHelper().createPaymentStateChangeHistoryRecordDto();
        // Reset
        resetAll();
        // Replay
        replayAll();
        // Run test scenario
        try {
            paymentService.updatePaymentState(null, recordDto);
            fail("Exception should be thrown");
        } catch (final IllegalArgumentException ex) {
            // Expected
        }
        try {
            paymentService.updatePaymentState(paymentId, null);
            fail("Exception should be thrown");
        } catch (final IllegalArgumentException ex) {
            // Expected
        }
        try {
            paymentService.updatePaymentState(paymentId, new PaymentStateChangeHistoryRecordDto(null, recordDto.getInformation()));
            fail("Exception should be thrown");
        } catch (final IllegalArgumentException ex) {
            // Expected
        }
        // Verify
        verifyAll();
    }

    @Test
    public void testUpdatePaymentStateWithNotExistingPayment() {
        // Test data
        final Long paymentId = 1l;
        final PaymentStateChangeHistoryRecordDto recordDto = getServicesImplTestHelper().createPaymentStateChangeHistoryRecordDto();
        // Reset
        resetAll();
        // Expectations
        expect(paymentRepository.findByIdWithWriteLockFlushedAndFreshData(eq(paymentId))).andReturn(null).once();
        // Replay
        replayAll();
        // Run test scenario
        try {
            paymentService.updatePaymentState(paymentId, recordDto);
            fail("Exception should be thrown");
        } catch (final PaymentNotFoundForIdException ex) {
            // Expected
            assertPaymentNotFoundForIdException(ex, paymentId);
        }
        // Verify
        verifyAll();
    }

    @Test
    public void testUpdatePaymentState() {
        // Test data
        final Long paymentId = 1l;
        final PaymentStateChangeHistoryRecordDto recordDto = getServicesImplTestHelper().createPaymentStateChangeHistoryRecordDto();
        final Payment payment = getInstance();
        final PaymentState initialState = payment.getLastState();
        final int initialRecordsCount = payment.getStateChangeHistoryRecords().size();
        final Date paymentUpdateDate = payment.getUpdated();
        // Reset
        resetAll();
        // Expectations
        expect(paymentRepository.findByIdWithWriteLockFlushedAndFreshData(eq(paymentId))).andReturn(payment).once();
        expect(paymentRepository.saveAndFlush(isA(getInstanceClass()))).andAnswer(() -> {
            final Payment updatedPayment = (Payment) getCurrentArguments()[0];
            assertTrue(updatedPayment.getUpdated().compareTo(paymentUpdateDate) >= 0 && paymentUpdateDate != updatedPayment.getUpdated());
            return updatedPayment;
        }).once();
        // Replay
        replayAll();
        // Run test scenario
        final PaymentStateChangeHistoryRecord historyRecord = paymentService.updatePaymentState(paymentId, recordDto);
        getServicesImplTestHelper().assertCreatePaymentStateChangeHistoryRecord(historyRecord, recordDto);
        assertNotNull(historyRecord.getPayment());
        getServicesImplTestHelper().assertPaymentLastState(historyRecord.getPayment(), recordDto.getUpdatedState(), initialState, initialRecordsCount + 1);
        // Verify
        verifyAll();
    }

    @Test
    public void testGetCustomersCountForPaymentSearchParametersWithInvalidArguments() {
        // Reset
        resetAll();
        // Replay
        replayAll();
        // Run test scenario
        try {
            paymentService.getCustomersCountForPaymentSearchParameters(null);
            fail("Exception should be thrown");
        } catch (final IllegalArgumentException ex) {
            // Expected
        }
        // Verify
        verifyAll();
    }

    @Test
    public void testGetCustomersCountForPaymentSearchParameters() {
        // Test data
        final PaymentSearchParameters paymentSearchParameters = createPaymentSearchParameters();
        final Long expectedCount = 1L;
        // Reset
        resetAll();
        // Expectations
        expect(paymentRepository.getCustomersCountForPaymentSearchParameters(eq(paymentSearchParameters))).andReturn(expectedCount).once();
        // Replay
        replayAll();
        // Run test scenario
        final Long result = paymentService.getCustomersCountForPaymentSearchParameters(paymentSearchParameters);
        assertEquals(expectedCount, result);
        // Verify
        verifyAll();
    }

    @Test
    public void testGetCustomersForPaymentSearchParametersWithInvalidArguments() {
        // Test data
        final PaymentSearchParameters paymentSearchParameters = createPaymentSearchParameters();
        final Long startFrom = 10L;
        final Integer maxCount = 100;
        // Reset
        resetAll();
        // Replay
        replayAll();
        // Run test scenario
        try {
            paymentService.getCustomersForPaymentSearchParameters(null, startFrom, maxCount);
            fail("Exception should be thrown");
        } catch (final IllegalArgumentException ex) {
            // Expected
        }
        try {
            paymentService.getCustomersForPaymentSearchParameters(paymentSearchParameters, null, maxCount);
            fail("Exception should be thrown");
        } catch (final IllegalArgumentException ex) {
            // Expected
        }
        try {
            paymentService.getCustomersForPaymentSearchParameters(paymentSearchParameters, startFrom, null);
            fail("Exception should be thrown");
        } catch (final IllegalArgumentException ex) {
            // Expected
        }
        try {
            paymentService.getCustomersForPaymentSearchParameters(paymentSearchParameters, -1L, maxCount);
            fail("Exception should be thrown");
        } catch (final IllegalArgumentException ex) {
            // Expected
        }
        try {
            paymentService.getCustomersForPaymentSearchParameters(paymentSearchParameters, startFrom, 0);
            fail("Exception should be thrown");
        } catch (final IllegalArgumentException ex) {
            // Expected
        }
        // Verify
        verifyAll();
    }

    @Test
    public void testGetCustomersForPaymentSearchParameters() {
        // Test data
        final PaymentSearchParameters paymentSearchParameters = createPaymentSearchParameters();
        final Long startFrom = 10L;
        final Integer maxCount = 100;
        final List<Long> customerIds = Arrays.asList(1L, 2L, 3L, 4L);
        // Reset
        resetAll();
        // Expectations
        expect(paymentRepository.findCustomersForPaymentSearchParameters(eq(paymentSearchParameters), eq(startFrom.longValue()), eq(maxCount.intValue()))).andReturn(customerIds).once();
        // Replay
        replayAll();
        // Run test scenario
        final List<Long> result = paymentService.getCustomersForPaymentSearchParameters(paymentSearchParameters, startFrom, maxCount);
        assertEquals(customerIds, result);
        // Verify
        verifyAll();
    }

    @Test
    public void testGetPaymentByIdWithPessimisticWriteLockWithInvalidArguments() {
        // Reset
        resetAll();
        // Replay
        replayAll();
        // Run test scenario
        try {
            paymentService.getPaymentByIdWithPessimisticWriteLock(null);
            fail("Exception should be thrown");
        } catch (final IllegalArgumentException ex) {
            // Expected
        }
        // Verify
        verifyAll();
    }

    @Test
    public void testGetPaymentByIdWithPessimisticWriteLockWithNotExistingId() {
        // Test data
        final Long paymentID = 1l;
        // Reset
        resetAll();
        // Expectations
        expect(paymentRepository.findByIdWithWriteLockFlushedAndFreshData(eq(paymentID))).andReturn(null).once();
        // Replay
        replayAll();
        // Run test scenario
        try {
            paymentService.getPaymentByIdWithPessimisticWriteLock(paymentID);
            fail("Exception should be thrown");
        } catch (final PaymentNotFoundForIdException ex) {
            // Expected
            assertPaymentNotFoundForIdException(ex, paymentID);
        }
        // Verify
        verifyAll();
    }

    @Test
    public void testGetPaymentByIdWithPessimisticWriteLock() {
        // Test data
        final Long paymentID = 1l;
        final Payment payment = getInstance();
        // Reset
        resetAll();
        // Expectations
        expect(paymentRepository.findByIdWithWriteLockFlushedAndFreshData(eq(paymentID))).andReturn(payment).once();
        // Replay
        replayAll();
        // Run test scenario
        final Payment result = paymentService.getPaymentByIdWithPessimisticWriteLock(paymentID);
        assertNotNull(result);
        assertEquals(payment, result);
        // Verify
        verifyAll();
    }

    @Test
    public void testUpdatePaymentProviderMetadataWithInvalidArguments() {
        // Reset
        resetAll();
        // Replay
        replayAll();
        // Run test scenario
        try {
            paymentService.updatePaymentProviderMetadata(null, new AcapturePaymentProviderMetadataDto());
            fail("Exception should be thrown");
        } catch (final IllegalArgumentException ex) {
            // Expected
        }
        try {
            paymentService.updatePaymentProviderMetadata(123L, null);
            fail("Exception should be thrown");
        } catch (final IllegalArgumentException ex) {
            // Expected
        }
        // Verify
        verifyAll();
    }

    @Test
    public void testUpdatePaymentProviderMetadata() {
        // Test data
        final Long paymentId = 123L;
        final Payment payment = getServicesImplTestHelper().createOrderPayment();
        payment.setPaymentProcessingChannel(getServicesImplTestHelper().createProvidedPaymentMethodProcessingChannel());
        final AcapturePaymentProviderMetadataDto dto = getServicesImplTestHelper().createAcapturePaymentProviderMetadataDto();
        final AcapturePaymentProviderMetadata metadata = new AcapturePaymentProviderMetadata();
        metadata.setCheckoutId(UUID.randomUUID().toString());
        // Reset
        resetAll();
        // Expectations
        expect(getRepository().findOne(paymentId)).andReturn(payment);
        expect(acapturePaymentProviderMetadataHandler.convertPaymentProviderMetadataDto(dto)).andReturn(metadata);
        expect(getRepository().save(payment)).andReturn(payment);
        // Replay
        replayAll();
        // Run test scenario
        final Payment result = paymentService.updatePaymentProviderMetadata(paymentId, dto);
        assertEquals(metadata, result.getPaymentProcessingChannel().getPaymentProviderMetadata());
        // Verify
        verifyAll();
    }

    /* Utility methods */
    @Override
    protected AbstractPaymentService<Payment> getService() {
        return paymentService;
    }

    @Override
    protected AbstractPaymentRepository<Payment> getRepository() {
        return paymentRepository;
    }

    @Override
    protected Payment getInstance() {
        return getServicesImplTestHelper().createOrderPayment();
    }

    @Override
    protected Class<Payment> getInstanceClass() {
        return Payment.class;
    }

    private PaymentSearchParameters createPaymentSearchParameters() {
        final DateTime dateTime = new DateTime();
        final PaymentSearchParameters paymentSearchParameters = new PaymentSearchParameters();
        paymentSearchParameters.setPaymentState(PaymentState.PAID);
        paymentSearchParameters.setStorePaymentMethod(Boolean.TRUE);
        paymentSearchParameters.setCreatedAfterDate(dateTime.minusDays(10).toDate());
        paymentSearchParameters.setCreatedBeforeDate(dateTime.plusDays(10).toDate());
        return paymentSearchParameters;
    }


}
