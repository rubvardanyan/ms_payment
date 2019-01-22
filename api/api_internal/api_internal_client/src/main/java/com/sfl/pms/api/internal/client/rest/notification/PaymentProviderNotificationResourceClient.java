package com.sfl.pms.api.internal.client.rest.notification;

import com.sfl.pms.core.api.internal.model.common.result.ResultResponseModel;
import com.sfl.pms.core.api.internal.model.notification.request.CreateAdyenPaymentProviderNotificationRequest;
import com.sfl.pms.core.api.internal.model.notification.response.CreatePaymentProviderNotificationResponse;

import javax.annotation.Nonnull;

/**
 * User: Arthur Asatryan
 * Company: SFL LLC
 * Date: 2/21/16
 * Time: 7:17 PM
 */
public interface PaymentProviderNotificationResourceClient {

    /**
     * Create payment provider notification
     *
     * @param request
     * @return response
     */
    @Nonnull
    ResultResponseModel<CreatePaymentProviderNotificationResponse> createPaymentProviderNotification(@Nonnull final CreateAdyenPaymentProviderNotificationRequest request);
}
