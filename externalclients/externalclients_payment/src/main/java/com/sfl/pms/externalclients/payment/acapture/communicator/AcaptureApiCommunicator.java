package com.sfl.pms.externalclients.payment.acapture.communicator;

import com.sfl.pms.externalclients.payment.acapture.model.request.CheckPaymentStatusRequest;
import com.sfl.pms.externalclients.payment.acapture.model.request.CreateCheckoutRequest;
import com.sfl.pms.externalclients.payment.acapture.model.request.SubmitRefundRequest;
import com.sfl.pms.externalclients.payment.acapture.model.response.CheckPaymentStatusResponse;
import com.sfl.pms.externalclients.payment.acapture.model.response.CreateCheckoutResponse;
import com.sfl.pms.externalclients.payment.acapture.model.response.SubmitRefundResponse;

import javax.annotation.Nonnull;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 9/30/18
 * Time: 7:45 PM
 */
public interface AcaptureApiCommunicator {

    @Nonnull
    CreateCheckoutResponse createCheckout(@Nonnull final CreateCheckoutRequest request);

    @Nonnull
    CheckPaymentStatusResponse checkPaymentStatus(@Nonnull final CheckPaymentStatusRequest request);

    @Nonnull
    SubmitRefundResponse submitRefund(@Nonnull final SubmitRefundRequest request);
}
