package com.sfl.pms.services.payment.processing.impl.order;

import com.sfl.pms.persistence.utility.PersistenceUtilityService;
import com.sfl.pms.services.common.exception.ServicesRuntimeException;
import com.sfl.pms.services.payment.common.model.PaymentState;
import com.sfl.pms.services.payment.common.model.order.OrderPayment;
import com.sfl.pms.services.payment.common.model.order.request.OrderRefundRequest;
import com.sfl.pms.services.payment.common.model.order.request.OrderRefundRequestState;
import com.sfl.pms.services.payment.common.order.request.OrderRefundRequestService;
import com.sfl.pms.services.payment.processing.impl.PaymentProviderOperationsProcessor;
import com.sfl.pms.services.payment.processing.impl.acapture.AcapturePaymentOperationsProcessor;
import com.sfl.pms.services.payment.processing.impl.adyen.AdyenPaymentOperationsProcessor;
import com.sfl.pms.services.payment.processing.order.OrderRefundRequestProcessorService;
import com.sfl.pms.services.payment.provider.exception.UnknownPaymentProviderTypeException;
import com.sfl.pms.services.payment.provider.model.PaymentProviderType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Nonnull;

/**
 * User: Ruben Dilanyan
 * Company: SFL LLC
 * Date: 1/17/15
 * Time: 1:00 PM
 */
@Service
public class OrderRefundRequestProcessorServiceImpl implements OrderRefundRequestProcessorService {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderRefundRequestProcessorServiceImpl.class);

    /* Dependencies */
    @Autowired
    private OrderRefundRequestService orderRefundRequestService;

    @Autowired
    private PersistenceUtilityService persistenceUtilityService;

    @Autowired
    private AcapturePaymentOperationsProcessor acapturePaymentOperationsProcessor;

    @Autowired
    private AdyenPaymentOperationsProcessor adyenPaymentOperationsProcessor;

    /* Constructors */
    public OrderRefundRequestProcessorServiceImpl() {
        LOGGER.debug("Initializing order refund processing service");
    }

    @Nonnull
    @Override
    public void processOrderRefundRequest(@Nonnull final Long orderRefundRequestId) {
        Assert.notNull(orderRefundRequestId, "Order refund request id should not be null");
        LOGGER.debug("Processing order refund request with id - {}", orderRefundRequestId);
        // Update state on request
        updateRefundMethodRequestState(orderRefundRequestId, OrderRefundRequestState.PROCESSING);
        try {
            final OrderRefundRequest orderRefundRequest = orderRefundRequestService.getById(orderRefundRequestId);
            final OrderPayment payment = orderRefundRequest.getOrderPaymentRequest().getOrderPayment();
            Assert.isTrue(orderRefundRequest.getPaymentProviderType().equals(payment.getPaymentProviderType()), "Refund request payment provider type should match with payment provider type of requested payment");
            Assert.isTrue(payment.getLastState().equals(PaymentState.PAID), "Cannot refund not paid payment");
            final PaymentProviderOperationsProcessor operationsProcessor = getPaymentProviderProcessorForPaymentProviderType(payment.getPaymentProviderType());
            final OrderRefundRequestState orderRefundRequestState = operationsProcessor.refundPayment(payment.getId());
            updateRefundMethodRequestState(orderRefundRequestId, orderRefundRequestState);
        } catch (final Exception ex) {
            LOGGER.error("Error occurred during processing order payment request - " + orderRefundRequestId, ex);
            // Update state on request
            updateRefundMethodRequestState(orderRefundRequestId, OrderRefundRequestState.FAILED);
            // Rethrow error
            throw new ServicesRuntimeException(ex);
        }
    }

    /* Utility methods */
    private void updateRefundMethodRequestState(final Long requestId, final OrderRefundRequestState state) {
        persistenceUtilityService.runInNewTransaction(() -> {
            final OrderRefundRequest orderRefundRequest = orderRefundRequestService.getById(requestId);
            orderRefundRequestService.updateState(orderRefundRequest.getUuId(), state);
        });
        //todo: vas call qup-core for status update chack with Hayk
    }

    private PaymentProviderOperationsProcessor getPaymentProviderProcessorForPaymentProviderType(final PaymentProviderType paymentProviderType) {
        switch (paymentProviderType) {
            case ACAPTURE:
                return acapturePaymentOperationsProcessor;
            case ADYEN:
                return adyenPaymentOperationsProcessor;
            default: {
                LOGGER.error("Unkown payment provider - {}", paymentProviderType);
                throw new UnknownPaymentProviderTypeException(paymentProviderType);
            }
        }
    }
}
