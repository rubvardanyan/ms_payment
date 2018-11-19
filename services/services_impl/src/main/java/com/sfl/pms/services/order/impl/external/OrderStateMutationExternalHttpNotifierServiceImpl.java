package com.sfl.pms.services.order.impl.external;

import com.sfl.pms.services.common.exception.ServicesRuntimeException;
import com.sfl.pms.services.order.external.OrderStateMutationExternalNotifierService;
import com.sfl.pms.services.order.model.OrderState;
import com.sfl.pms.services.payment.common.model.PaymentState;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * User: Ruben Dilanyan
 * Company: SFL LLC
 * Date: 1/22/16
 * Time: 5:01 PM
 */
@Service("orderStateMutationExternalHttpNotifierService")
public class OrderStateMutationExternalHttpNotifierServiceImpl implements OrderStateMutationExternalNotifierService {

    private static final Logger LOGGER = LoggerFactory.getLogger(OrderStateMutationExternalHttpNotifierServiceImpl.class);

    /* Constants */
    private static final String PARAMETER_NAME_ORDER_UUID = "orderuuid";

    private static final String PARAMETER_NAME_PAYMENT_UUID = "paymentuuid";

    private static final String PARAMETER_NAME_ORDER_STATE = "orderstate";

    private static final String PARAMETER_NAME_TEMPORARY_STATE = "temporarystate";

    /* Properties */
    @Value("#{ appProperties['order.mutation.notifier.http.url']}")
    private String externalNotificationHttpUrl;

    @Autowired
    private HttpClient httpClient;

    /* Constructors */
    public OrderStateMutationExternalHttpNotifierServiceImpl() {
        LOGGER.debug("Initializing order state mutation external HTTP notifier service");
    }

    @Override
    public void notifyOrderStateMutation(@Nonnull final String orderUuId, @Nonnull final OrderState orderState, @Nullable final PaymentState paymentState, @Nullable final String paymentUuid) {
        Assert.notNull(orderUuId, "Order uuid should not be null");
        Assert.notNull(orderState, "Order state should not be null");
        LOGGER.debug("Updating external party using HTTP for order state mutation, order uuid - {}, order state - {}", orderUuId, orderState);
        HttpGet httpGet = null;
        try {
            // Build execution URI
            final URIBuilder uriBuilder = new URIBuilder(externalNotificationHttpUrl);
            uriBuilder.addParameter(PARAMETER_NAME_ORDER_UUID, orderUuId);
            uriBuilder.addParameter(PARAMETER_NAME_ORDER_STATE, orderState.name());
            if (paymentUuid != null) {
                uriBuilder.addParameter(PARAMETER_NAME_PAYMENT_UUID, paymentUuid);
            }
            if (paymentState != null) {
                uriBuilder.addParameter(PARAMETER_NAME_TEMPORARY_STATE, String.valueOf(paymentState.isTemporaryState()));
            }
            // Create HTTP method
            httpGet = new HttpGet(uriBuilder.build());
            final HttpResponse httpResponse = httpClient.execute(httpGet);
            final int responseStatus = httpResponse.getStatusLine().getStatusCode();
            LOGGER.debug("Executed order state mutation external notification via HTTP, order uuid - {}, order state - {}, HTTP response status - {}", orderUuId, orderState, responseStatus);
        } catch (final Exception ex) {
            final String message = "Error occurred while executing order state mutation notification for order uuid - " + orderUuId + ", order state - " + orderState;
            LOGGER.error(message, ex);
            throw new ServicesRuntimeException(message, ex);
        } finally {
            if (httpGet != null) {
                httpGet.releaseConnection();
            }
        }

    }

    /* Properties getters and setters */
    public String getExternalNotificationHttpUrl() {
        return externalNotificationHttpUrl;
    }

    public void setExternalNotificationHttpUrl(final String externalNotificationHttpUrl) {
        this.externalNotificationHttpUrl = externalNotificationHttpUrl;
    }

    public HttpClient getHttpClient() {
        return httpClient;
    }

    public void setHttpClient(final HttpClient httpClient) {
        this.httpClient = httpClient;
    }
}
