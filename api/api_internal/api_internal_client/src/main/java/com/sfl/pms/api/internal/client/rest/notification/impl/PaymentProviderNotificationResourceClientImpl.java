package com.sfl.pms.api.internal.client.rest.notification.impl;

import com.sfl.pms.api.internal.client.rest.common.AbstractResourceClient;
import com.sfl.pms.api.internal.client.rest.notification.PaymentProviderNotificationResourceClient;
import com.sfl.pms.core.api.internal.model.common.result.ResultResponseModel;
import com.sfl.pms.core.api.internal.model.notification.request.CreateAdyenPaymentProviderNotificationRequest;
import com.sfl.pms.core.api.internal.model.notification.response.CreatePaymentProviderNotificationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;

/**
 * User: Arthur Asatryan
 * Company: SFL LLC
 * Date: 2/21/16
 * Time: 7:20 PM
 */

public class PaymentProviderNotificationResourceClientImpl extends AbstractResourceClient implements PaymentProviderNotificationResourceClient {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentProviderNotificationResourceClientImpl.class);

    //region Constants
    private static final String RESOURCE_BASE_PATH = "provider/notification";

    private static final String PROCESS_ADYEN_PAYMENT_NOTIFICATION_PATH = "create";
    //endregion

    //region Constructors
    public PaymentProviderNotificationResourceClientImpl(final Client client, final String apiPath) {
        super(client, apiPath);
        LOGGER.debug("Initializing payment provider notification resource client");
    }
    //endregion

    //region Public methods
    @Nonnull
    @Override
    public ResultResponseModel<CreatePaymentProviderNotificationResponse> createPaymentProviderNotification(@Nonnull final CreateAdyenPaymentProviderNotificationRequest request) {
        return getClient()
                .target(getApiPath())
                .path(RESOURCE_BASE_PATH)
                .path(PROCESS_ADYEN_PAYMENT_NOTIFICATION_PATH)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .post(Entity.entity(request, MediaType.APPLICATION_JSON_TYPE), new GenericType<ResultResponseModel<CreatePaymentProviderNotificationResponse>>() {
                });
    }
    //endregion
}
