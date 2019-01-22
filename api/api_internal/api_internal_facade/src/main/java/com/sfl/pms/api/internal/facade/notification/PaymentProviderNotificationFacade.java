package com.sfl.pms.api.internal.facade.notification;

import com.sfl.pms.core.api.internal.model.common.result.ResultResponseModel;
import com.sfl.pms.core.api.internal.model.notification.request.CreateAcapturePaymentProviderNotificationRequest;
import com.sfl.pms.core.api.internal.model.notification.request.CreateAdyenPaymentProviderNotificationRequest;
import com.sfl.pms.core.api.internal.model.notification.response.CreatePaymentProviderNotificationResponse;

import javax.annotation.Nonnull;

/**
 * User: Ruben Dilanyan
 * Company: SFL LLC
 * Date: 4/27/15
 * Time: 12:19 PM
 */
public interface PaymentProviderNotificationFacade {

    /**
     * Creates new adyen payment provider notification
     *
     * @param request
     * @return response
     */
    ResultResponseModel<CreatePaymentProviderNotificationResponse> createAdyenPaymentProviderNotificationRequest(@Nonnull final CreateAdyenPaymentProviderNotificationRequest request);

    /**
     * Create new acapture payment provider notification request
     *
     * @param request request
     * @return response
     */
    ResultResponseModel<CreatePaymentProviderNotificationResponse> createAcapturePaymentProviderNotificationRequest(@Nonnull final CreateAcapturePaymentProviderNotificationRequest request);
}
