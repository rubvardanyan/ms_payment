package com.sfl.pms.services.payment.processing.impl.order;

import com.sfl.pms.persistence.utility.PersistenceUtilityService;
import com.sfl.pms.services.common.exception.ServicesRuntimeException;
import com.sfl.pms.services.order.external.OrderStateMutationExternalNotifierService;
import com.sfl.pms.services.order.model.OrderState;
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
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;
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

    @Autowired
    private OrderStateMutationExternalNotifierService orderStateMutationExternalNotifierService;

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
        final OrderRefundRequest orderRefundRequest = saveRefundRequest(requestId, state);
        final RefundState refundState = RefundState.stateFor(state);
        if (!refundState.isCompleted()) {
            return;
        }
        orderStateMutationExternalNotifierService.notifyOrderStateMutation(
                orderRefundRequest.getOrderPaymentRequest().getOrder().getUuId(),
                refundState.orderState,
                refundState.paymentState,
                orderRefundRequest.getOrderPaymentRequest().getUuId()
        );
    }

    private OrderRefundRequest saveRefundRequest(final Long requestId, final OrderRefundRequestState state) {
        final Mutable<OrderRefundRequest> refundRequest = new MutableObject<>();
        persistenceUtilityService.runInNewTransaction(() -> {
            final OrderRefundRequest orderRefundRequest = orderRefundRequestService.getById(requestId);
            orderRefundRequestService.updateState(orderRefundRequest.getUuId(), state);
            refundRequest.setValue(orderRefundRequest);
        });
        return refundRequest.getValue();
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

    private final static class RefundState {
        private final OrderState orderState;
        private final PaymentState paymentState;

        RefundState(final OrderState orderState, final PaymentState paymentState) {
            this.orderState = orderState;
            this.paymentState = paymentState;
        }

        static RefundState stateFor(final OrderRefundRequestState state) {
            if (OrderRefundRequestState.PROCESSED.equals(state)) {
                return new RefundState(OrderState.REFUNDED, PaymentState.REFUNDED);
            }
            if (OrderRefundRequestState.FAILED.equals(state)) {
                return new RefundState(OrderState.REFUND_FAILED, PaymentState.REFUND_FAILED);
            }
            return RefundState.incomplete();

        }

        private static RefundState incomplete() {
            return new RefundState(null, null);
        }

        boolean isCompleted() {
            return orderState != null;
        }
    }
}
