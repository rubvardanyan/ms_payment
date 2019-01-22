package com.sfl.pms.core.api.internal.model.notification.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sfl.pms.core.api.internal.model.common.AbstractRequestModel;
import com.sfl.pms.core.api.internal.model.common.result.ErrorResponseModel;
import com.sfl.pms.core.api.internal.model.common.result.ErrorType;
import com.sfl.pms.core.api.internal.model.provider.PaymentProviderClientType;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Ruben Dilanyan
 * Company: SFL LLC
 * Date: 1/18/16
 * Time: 3:54 PM
 */
public class CreateAdyenPaymentProviderNotificationRequest extends AbstractPaymentProviderNotificationRequest {
    private static final long serialVersionUID = 8263071127817154681L;

    /* Constructor */
    public CreateAdyenPaymentProviderNotificationRequest() {
        super(PaymentProviderClientType.ADYEN);
    }
}
