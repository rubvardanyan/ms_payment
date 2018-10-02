package com.sfl.pms.services.payment.common;

import com.sfl.pms.services.customer.model.Customer;
import com.sfl.pms.services.payment.common.dto.PaymentSearchParameters;
import com.sfl.pms.services.payment.common.dto.PaymentStateChangeHistoryRecordDto;
import com.sfl.pms.services.payment.common.dto.acapture.AcapturePaymentProviderMetadataDto;
import com.sfl.pms.services.payment.common.dto.order.OrderPaymentDto;
import com.sfl.pms.services.payment.common.model.Payment;
import com.sfl.pms.services.payment.common.model.PaymentState;
import com.sfl.pms.services.payment.common.model.PaymentStateChangeHistoryRecord;
import com.sfl.pms.services.payment.common.model.acapture.AcapturePaymentProviderMetadata;
import com.sfl.pms.services.payment.common.model.order.OrderPayment;
import com.sfl.pms.services.payment.method.model.PaymentMethodType;
import com.sfl.pms.services.payment.provider.model.PaymentProviderType;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static org.junit.Assert.*;

/**
 * User: Ruben Dilanyan
 * Company: SFL LLC
 * Date: 1/11/15
 * Time: 12:35 PM
 */
public class PaymentServiceIntegrationTest extends AbstractPaymentServiceIntegrationTest<Payment> {

    /* Dependencies */
    @Autowired
    private PaymentService paymentService;

    /* Constructors */
    public PaymentServiceIntegrationTest() {
    }

    /* Test methods */
    @Test
    public void testUpdateConfirmedPaymentMethodType() {
        // Prepare data
        final Payment payment = getInstance();
        assertNull(payment.getConfirmedPaymentMethodType());
        final PaymentMethodType paymentMethodType = PaymentMethodType.IDEAL;
        flushAndClear();
        // Update confirmed payment method
        Payment result = paymentService.updateConfirmedPaymentMethodType(payment.getId(), paymentMethodType);
        assertNotNull(result);
        Assert.assertEquals(paymentMethodType, result.getConfirmedPaymentMethodType());
        // Flush, clear, reload and assert
        flushAndClear();
        result = paymentService.getPaymentById(payment.getId());
        assertNotNull(result);
        Assert.assertEquals(paymentMethodType, result.getConfirmedPaymentMethodType());
    }

    @Test
    public void testUpdatePaymentState() {
        // Create payment
        Payment payment = getInstance();
        final PaymentState initialState = payment.getLastState();
        final int initialRecordsCount = payment.getStateChangeHistoryRecords().size();
        final int expectedRecordsCount = initialRecordsCount + 1;
        final PaymentStateChangeHistoryRecordDto recordDto = getServicesTestHelper().createPaymentStateChangeHistoryRecordDto();
        flushAndClear();
        // Update state
        PaymentStateChangeHistoryRecord historyRecord = paymentService.updatePaymentState(payment.getId(), recordDto);
        assertPaymentStateChangeHistoryRecord(historyRecord, recordDto, initialState, expectedRecordsCount);
        // Flush, clear and assert again
        flushAndClear();
        payment = getService().getPaymentById(payment.getId());
        getServicesTestHelper().assertPaymentLastState(historyRecord.getPayment(), recordDto.getUpdatedState(), initialState, expectedRecordsCount);
    }

    @Test
    public void testGetPaymentByIdWithPessimisticWriteLock() {
        // Prepare data
        final Payment payment = getInstance();
        Payment result = paymentService.getPaymentByIdWithPessimisticWriteLock(payment.getId());
        assertEquals(payment, result);
        // Flush, reload and assert
        flushAndClear();
        result = getService().getPaymentById(payment.getId());
        assertEquals(payment, result);
    }

    @Test
    public void testGetCustomersForPaymentSearchParametersWithCreatedBeforeFilter() {
        // Create payment
        final Payment payment = getServicesTestHelper().createOrderPayment();
        assertNotNull(payment);
        final Long customerId = payment.getCustomer().getId();
        final DateTime dateTime = new DateTime(payment.getCreated());
        // Create search parameters
        final PaymentSearchParameters parameters = new PaymentSearchParameters().setCreatedBeforeDate(null);
        flushAndClear();
        // Execute search
        List<Long> customerIds = paymentService.getCustomersForPaymentSearchParameters(parameters, 0L, Integer.MAX_VALUE);
        assertCustomerIds(customerIds, new HashSet<>(Arrays.asList(customerId)));
        // Set created before filter
        parameters.setCreatedBeforeDate(dateTime.minusSeconds(1).toDate());
        customerIds = paymentService.getCustomersForPaymentSearchParameters(parameters, 0L, Integer.MAX_VALUE);
        assertCustomerIds(customerIds, new HashSet<>());
        // Set created before filter
        parameters.setCreatedBeforeDate(dateTime.plusSeconds(1).toDate());
        customerIds = paymentService.getCustomersForPaymentSearchParameters(parameters, 0L, Integer.MAX_VALUE);
        assertCustomerIds(customerIds, new HashSet<>(Arrays.asList(customerId)));
    }

    @Test
    public void testGetCustomersCountForPaymentSearchParametersWithCreatedBeforeFilter() {
        // Create payment
        final Payment payment = getServicesTestHelper().createOrderPayment();
        assertNotNull(payment);
        final Long customerId = payment.getCustomer().getId();
        final DateTime dateTime = new DateTime(payment.getCreated());
        // Create search parameters
        final PaymentSearchParameters parameters = new PaymentSearchParameters().setCreatedBeforeDate(null);
        flushAndClear();
        // Execute search
        Long customerIdsCount = paymentService.getCustomersCountForPaymentSearchParameters(parameters);
        assertCustomerIdsCount(customerIdsCount, new HashSet<>(Arrays.asList(customerId)));
        // Set created before filter
        parameters.setCreatedBeforeDate(dateTime.minusSeconds(1).toDate());
        customerIdsCount = paymentService.getCustomersCountForPaymentSearchParameters(parameters);
        assertCustomerIdsCount(customerIdsCount, new HashSet<>());
        // Set created before filter
        parameters.setCreatedBeforeDate(dateTime.plusSeconds(1).toDate());
        customerIdsCount = paymentService.getCustomersCountForPaymentSearchParameters(parameters);
        assertCustomerIdsCount(customerIdsCount, new HashSet<>(Arrays.asList(customerId)));
    }

    @Test
    public void testGetCustomersForPaymentSearchParametersWithCreatedAfterFilter() {
        // Create payment
        final Payment payment = getServicesTestHelper().createOrderPayment();
        assertNotNull(payment);
        final Long customerId = payment.getCustomer().getId();
        final DateTime dateTime = new DateTime(payment.getCreated());
        // Create search parameters
        final PaymentSearchParameters parameters = new PaymentSearchParameters().setCreatedAfterDate(null);
        flushAndClear();
        // Execute search
        List<Long> customerIds = paymentService.getCustomersForPaymentSearchParameters(parameters, 0L, Integer.MAX_VALUE);
        assertCustomerIds(customerIds, new HashSet<>(Arrays.asList(customerId)));
        // Set created after filter
        parameters.setCreatedAfterDate(dateTime.plusSeconds(1).toDate());
        customerIds = paymentService.getCustomersForPaymentSearchParameters(parameters, 0L, Integer.MAX_VALUE);
        assertCustomerIds(customerIds, new HashSet<>());
        // Set created after filter
        parameters.setCreatedAfterDate(dateTime.minusSeconds(1).toDate());
        customerIds = paymentService.getCustomersForPaymentSearchParameters(parameters, 0L, Integer.MAX_VALUE);
        assertCustomerIds(customerIds, new HashSet<>(Arrays.asList(customerId)));
    }

    @Test
    public void testGetCustomersCountForPaymentSearchParametersWithCreatedAfterFilter() {
        // Create payment
        final Payment payment = getServicesTestHelper().createOrderPayment();
        assertNotNull(payment);
        final Long customerId = payment.getCustomer().getId();
        final DateTime dateTime = new DateTime(payment.getCreated());
        // Create search parameters
        final PaymentSearchParameters parameters = new PaymentSearchParameters().setCreatedAfterDate(null);
        flushAndClear();
        // Execute search
        Long customerIds = paymentService.getCustomersCountForPaymentSearchParameters(parameters);
        assertCustomerIdsCount(customerIds, new HashSet<>(Arrays.asList(customerId)));
        // Set created after filter
        parameters.setCreatedAfterDate(dateTime.plusSeconds(1).toDate());
        customerIds = paymentService.getCustomersCountForPaymentSearchParameters(parameters);
        assertCustomerIdsCount(customerIds, new HashSet<>());
        // Set created after filter
        parameters.setCreatedAfterDate(dateTime.minusSeconds(1).toDate());
        customerIds = paymentService.getCustomersCountForPaymentSearchParameters(parameters);
        assertCustomerIdsCount(customerIds, new HashSet<>(Arrays.asList(customerId)));
    }

    @Test
    public void testGetCustomersForPaymentSearchParametersWithStorePaymentMethodFilter() {
        // Create payment
        final Customer paymentWithStoredFlagCustomer = getServicesTestHelper().createCustomer("dummy@dummy.com");
        final Customer paymentWithOutStoredFlagCustomer = getServicesTestHelper().createCustomer("ruben.dilanyan@sflpro.com");
        final Long paymentWithStoredFlagCustomerId = paymentWithStoredFlagCustomer.getId();
        final Long paymentWithOutStoredFlagCustomerId = paymentWithOutStoredFlagCustomer.getId();
        final OrderPaymentDto orderPaymentDto = getServicesTestHelper().createOrderPaymentDto();
        orderPaymentDto.setStorePaymentMethod(true);
        final Payment paymentWithStoredFlag = getServicesTestHelper().createOrderPayment(orderPaymentDto, paymentWithStoredFlagCustomer);
        assertNotNull(paymentWithStoredFlag);
        orderPaymentDto.setStorePaymentMethod(false);
        final Payment paymentWithOutStoredFlag = getServicesTestHelper().createOrderPayment(orderPaymentDto, paymentWithOutStoredFlagCustomer);
        assertNotNull(paymentWithOutStoredFlag);
        // Create search parameters
        final PaymentSearchParameters parameters = new PaymentSearchParameters().setStorePaymentMethod(null);
        flushAndClear();
        // Execute search
        List<Long> customerIds = paymentService.getCustomersForPaymentSearchParameters(parameters, 0L, Integer.MAX_VALUE);
        assertCustomerIds(customerIds, new HashSet<>(Arrays.asList(paymentWithStoredFlagCustomerId, paymentWithOutStoredFlagCustomerId)));
        // Set with stored payment method
        parameters.setStorePaymentMethod(Boolean.TRUE);
        customerIds = paymentService.getCustomersForPaymentSearchParameters(parameters, 0L, Integer.MAX_VALUE);
        assertCustomerIds(customerIds, new HashSet<>(Arrays.asList(paymentWithStoredFlagCustomerId)));
        // Set with stored payment method
        parameters.setStorePaymentMethod(Boolean.FALSE);
        customerIds = paymentService.getCustomersForPaymentSearchParameters(parameters, 0L, Integer.MAX_VALUE);
        assertCustomerIds(customerIds, new HashSet<>(Arrays.asList(paymentWithOutStoredFlagCustomerId)));
    }

    @Test
    public void testGetCustomersCountForPaymentSearchParametersWithStorePaymentMethodFilter() {
        // Create payment
        final Customer paymentWithStoredFlagCustomer = getServicesTestHelper().createCustomer("dummy@dummy.com");
        final Customer paymentWithOutStoredFlagCustomer = getServicesTestHelper().createCustomer("ruben.dilanyan@sflpro.com");
        final Long paymentWithStoredFlagCustomerId = paymentWithStoredFlagCustomer.getId();
        final Long paymentWithOutStoredFlagCustomerId = paymentWithOutStoredFlagCustomer.getId();
        final OrderPaymentDto orderPaymentDto = getServicesTestHelper().createOrderPaymentDto();
        orderPaymentDto.setStorePaymentMethod(true);
        final Payment paymentWithStoredFlag = getServicesTestHelper().createOrderPayment(orderPaymentDto, paymentWithStoredFlagCustomer);
        assertNotNull(paymentWithStoredFlag);
        orderPaymentDto.setStorePaymentMethod(false);
        final Payment paymentWithOutStoredFlag = getServicesTestHelper().createOrderPayment(orderPaymentDto, paymentWithOutStoredFlagCustomer);
        assertNotNull(paymentWithOutStoredFlag);
        // Create search parameters
        final PaymentSearchParameters parameters = new PaymentSearchParameters().setStorePaymentMethod(null);
        flushAndClear();
        // Execute search
        Long customerIdsCount = paymentService.getCustomersCountForPaymentSearchParameters(parameters);
        assertCustomerIdsCount(customerIdsCount, new HashSet<>(Arrays.asList(paymentWithStoredFlagCustomerId, paymentWithOutStoredFlagCustomerId)));
        // Set with stored payment method
        parameters.setStorePaymentMethod(Boolean.TRUE);
        customerIdsCount = paymentService.getCustomersCountForPaymentSearchParameters(parameters);
        assertCustomerIdsCount(customerIdsCount, new HashSet<>(Arrays.asList(paymentWithStoredFlagCustomerId)));
        // Set with stored payment method
        parameters.setStorePaymentMethod(Boolean.FALSE);
        customerIdsCount = paymentService.getCustomersCountForPaymentSearchParameters(parameters);
        assertCustomerIdsCount(customerIdsCount, new HashSet<>(Arrays.asList(paymentWithOutStoredFlagCustomerId)));
    }


    @Test
    public void testGetCustomersForPaymentSearchParametersWithPaymentProviderFilter() {
        // Create payment
        final Payment adyenPayment = getServicesTestHelper().createOrderPayment();
        final Long adyenPaymentCustomerId = adyenPayment.getCustomer().getId();
        // Create search parameters
        final PaymentSearchParameters parameters = new PaymentSearchParameters().setPaymentProviderType(null);
        flushAndClear();
        // Execute search
        List<Long> customerIds = paymentService.getCustomersForPaymentSearchParameters(parameters, 0L, Integer.MAX_VALUE);
        assertCustomerIds(customerIds, new HashSet<>(Arrays.asList(adyenPaymentCustomerId)));
        // Set payment provider filter to BrainTree
        parameters.setPaymentProviderType(PaymentProviderType.BRAINTREE);
        customerIds = paymentService.getCustomersForPaymentSearchParameters(parameters, 0L, Integer.MAX_VALUE);
        assertCustomerIds(customerIds, new HashSet<>());
        // Set payment provider filter to Adyen
        parameters.setPaymentProviderType(PaymentProviderType.ADYEN);
        customerIds = paymentService.getCustomersForPaymentSearchParameters(parameters, 0L, Integer.MAX_VALUE);
        assertCustomerIds(customerIds, new HashSet<>(Arrays.asList(adyenPaymentCustomerId)));
    }

    @Test
    public void testGetCustomersCountForPaymentSearchParametersWithPaymentProviderFilter() {
        // Create payment
        final Payment adyenPayment = getServicesTestHelper().createOrderPayment();
        final Long adyenPaymentCustomerId = adyenPayment.getCustomer().getId();
        // Create search parameters
        final PaymentSearchParameters parameters = new PaymentSearchParameters().setPaymentProviderType(null);
        flushAndClear();
        // Execute search
        Long customerIdsCount = paymentService.getCustomersCountForPaymentSearchParameters(parameters);
        assertCustomerIdsCount(customerIdsCount, new HashSet<>(Arrays.asList(adyenPaymentCustomerId)));
        // Set payment provider filter to BrainTree
        parameters.setPaymentProviderType(PaymentProviderType.BRAINTREE);
        customerIdsCount = paymentService.getCustomersCountForPaymentSearchParameters(parameters);
        assertCustomerIdsCount(customerIdsCount, new HashSet<>());
        // Set payment provider filter to Adyen
        parameters.setPaymentProviderType(PaymentProviderType.ADYEN);
        customerIdsCount = paymentService.getCustomersCountForPaymentSearchParameters(parameters);
        assertCustomerIdsCount(customerIdsCount, new HashSet<>(Arrays.asList(adyenPaymentCustomerId)));
    }


    @Test
    public void testGetCustomersForPaymentSearchParametersWithPaymentStateFilter() {
        if (!isEnablePaymentIntegrationTests()) {
            return;
        }
        // Create payment
        final Payment notPayedOrderPayment = getServicesTestHelper().createOrderPayment();
        final Payment paidOrderPayment = getServicesTestHelper().createOrderPaymentAndPayUsingAdyen();
        final Long notPayedOrderPaymentCustomer = notPayedOrderPayment.getCustomer().getId();
        final Long paidOrderPaymentCustomer = paidOrderPayment.getCustomer().getId();
        // Create search parameters
        final PaymentSearchParameters parameters = new PaymentSearchParameters().setPaymentState(null);
        flushAndClear();
        // Execute search
        List<Long> customerIds = paymentService.getCustomersForPaymentSearchParameters(parameters, 0L, Integer.MAX_VALUE);
        assertCustomerIds(customerIds, new HashSet<>(Arrays.asList(notPayedOrderPaymentCustomer, paidOrderPaymentCustomer)));
        // Set payment state filter
        parameters.setPaymentState(PaymentState.PAID);
        customerIds = paymentService.getCustomersForPaymentSearchParameters(parameters, 0L, Integer.MAX_VALUE);
        assertCustomerIds(customerIds, new HashSet<>(Arrays.asList(paidOrderPaymentCustomer)));
    }

    @Test
    public void testGetCustomersCountForPaymentSearchParametersWithPaymentStateFilter() {
        if (!isEnablePaymentIntegrationTests()) {
            return;
        }
        // Create payment
        final Payment notPayedOrderPayment = getServicesTestHelper().createOrderPayment();
        final Payment paidOrderPayment = getServicesTestHelper().createOrderPaymentAndPayUsingAdyen();
        final Long notPayedOrderPaymentCustomer = notPayedOrderPayment.getCustomer().getId();
        final Long paidOrderPaymentCustomer = paidOrderPayment.getCustomer().getId();
        // Create search parameters
        final PaymentSearchParameters parameters = new PaymentSearchParameters().setPaymentState(null);
        flushAndClear();
        // Execute search
        Long customerIdsCount = paymentService.getCustomersCountForPaymentSearchParameters(parameters);
        assertCustomerIdsCount(customerIdsCount, new HashSet<>(Arrays.asList(notPayedOrderPaymentCustomer, paidOrderPaymentCustomer)));
        // Set payment state filter
        parameters.setPaymentState(PaymentState.PAID);
        customerIdsCount = paymentService.getCustomersCountForPaymentSearchParameters(parameters);
        assertCustomerIdsCount(customerIdsCount, new HashSet<>(Arrays.asList(paidOrderPaymentCustomer)));
    }

    @Test
    public void  testUpdatePaymentProviderMetadata() {
        if(!isEnablePaymentIntegrationTests()) {
            return;
        }
        //Create payment
        final OrderPayment payment = getServicesTestHelper().createOrderPayment();
        final String checkoutId = UUID.randomUUID().toString();
        final AcapturePaymentProviderMetadataDto metadataDto = new AcapturePaymentProviderMetadataDto(checkoutId);
        paymentService.updatePaymentProviderMetadata(payment.getId(), metadataDto);
        flushAndClear();
        // Reload payment
        final Payment result = paymentService.getPaymentById(payment.getId());
        assertTrue(result.getPaymentProcessingChannel().getPaymentProviderMetadata() instanceof AcapturePaymentProviderMetadata);
        final AcapturePaymentProviderMetadata acaptureMetadata = (AcapturePaymentProviderMetadata) result.getPaymentProcessingChannel().getPaymentProviderMetadata();
        assertEquals(checkoutId, acaptureMetadata.getCheckoutId());
    }

    /* Utility methods */
    private void assertCustomerIds(final List<Long> result, final Set<Long> expectedCustomerIds) {
        assertEquals(expectedCustomerIds.size(), result.size());
        result.forEach(customerId -> {
            assertTrue(expectedCustomerIds.contains(customerId));
        });
    }

    private void assertCustomerIdsCount(final Long result, final Set<Long> expectedCustomerIds) {
        assertEquals(expectedCustomerIds.size(), result.intValue());
    }

    private void assertPaymentStateChangeHistoryRecord(final PaymentStateChangeHistoryRecord historyRecord, final PaymentStateChangeHistoryRecordDto recordDto, final PaymentState initialState, final int expectedRecordsCount) {
        getServicesTestHelper().assertCreatePaymentStateChangeHistoryRecord(historyRecord, recordDto);
        getServicesTestHelper().assertPaymentLastState(historyRecord.getPayment(), recordDto.getUpdatedState(), initialState, expectedRecordsCount);
    }

    @Override
    protected AbstractPaymentService<Payment> getService() {
        return paymentService;
    }

    @Override
    protected Payment getInstance() {
        return getServicesTestHelper().createOrderPayment();
    }
}
