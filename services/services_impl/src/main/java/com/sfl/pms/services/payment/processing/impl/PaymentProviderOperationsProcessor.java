package com.sfl.pms.services.payment.processing.impl;

import com.sfl.pms.services.payment.common.dto.PaymentResultDto;
import com.sfl.pms.services.payment.common.model.Payment;
import com.sfl.pms.services.payment.common.model.PaymentResult;
import com.sfl.pms.services.payment.common.model.order.request.OrderRefundRequestState;
import com.sfl.pms.services.payment.customer.method.dto.CustomerPaymentMethodDto;
import com.sfl.pms.services.payment.customer.method.dto.CustomerPaymentMethodProviderInformationDto;
import com.sfl.pms.services.payment.customer.method.model.CustomerPaymentMethod;
import com.sfl.pms.services.payment.customer.method.model.CustomerPaymentMethodProviderInformation;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.util.Assert;

import javax.annotation.Nonnull;
import java.io.Serializable;
import java.util.List;

/**
 * User: Ruben Dilanyan
 * Company: SFL LLC
 * Date: 1/13/15
 * Time: 6:59 PM
 */
public interface PaymentProviderOperationsProcessor {

    /**
     * Processes order payment
     *
     * @param paymentId
     * @return paymentResultDto
     */
    @Nonnull
    PaymentResultDto<? extends PaymentResult> processPaymentUsingCustomerPaymentMethodChannel(@Nonnull final Long paymentId);

    /**
     * Processes payment method authorization payment
     *
     * @param paymentId
     * @return paymentResultDto
     */
    @Nonnull
    PaymentResultDto<? extends PaymentResult> processPaymentUsingEncryptedPaymentMethodChannel(@Nonnull final Long paymentId);

    /**
     * Gets all saved payment methods of customer which are recognized as new, matches between authorization payment and recurring payment details and returns list
     *
     * @param customerId
     * @return customerPaymentMethodProviderData
     */
    @Nonnull
    List<CustomerPaymentMethodProviderData> getStoredRecurringPaymentMethods(@Nonnull final Long customerId);

    /**
     * Process customer payment method removal for given payment method provider info
     *
     * @param paymentMethodProviderInformationId
     * @return customerPaymentMethodRemovalData
     */
    CustomerPaymentMethodRemovalData processCustomerPaymentMethodRemoval(@Nonnull final Long paymentMethodProviderInformationId);


    /**
     * Checks if payment result already exists
     *
     * @param payment
     * @param paymentResultDto
     * @return exists
     */
    @Nonnull
    boolean checkIfPaymentResultAlreadyExists(@Nonnull final Payment payment, @Nonnull final PaymentResultDto<? extends PaymentResult> paymentResultDto);

    /**
     * Generates payment redirect URL
     *
     * @param paymentId
     * @param createRecurringContract
     * @return paymentRedirectUrl
     */
    @Nonnull
    String generatePaymentRedirectUrl(@Nonnull Long paymentId, @Nonnull boolean createRecurringContract);

    /**
     * Refunds already paid payment
     *
     * @param paymentId payment id to refund
     */
    @Nonnull
    OrderRefundRequestState refundPayment(@Nonnull Long paymentId);


    /* Inner classes */
    class CustomerPaymentMethodRemovalData implements Serializable {

        private static final long serialVersionUID = 8356947304823599375L;

        /* Properties */
        private final String status;

        /* Constructors */
        public CustomerPaymentMethodRemovalData(final String status) {
            Assert.hasText(status, "Payment method removal status should not be empty");
            this.status = status;
        }

        /* Getters and setters */
        public String getStatus() {
            return status;
        }

        /* ToString */
        @Override
        public String toString() {
            final ToStringBuilder builder = new ToStringBuilder(this);
            builder.append("status", getStatus());
            return builder.build();
        }
    }

    class CustomerPaymentMethodProviderData implements Serializable {

        private static final long serialVersionUID = -7615775105326511200L;
        /* Properties */
        private final CustomerPaymentMethodDto<? extends CustomerPaymentMethod> customerPaymentMethodDto;

        private final CustomerPaymentMethodProviderInformationDto<? extends CustomerPaymentMethodProviderInformation> customerPaymentMethodProviderInformationDto;

        public CustomerPaymentMethodProviderData(final CustomerPaymentMethodDto<? extends CustomerPaymentMethod> customerPaymentMethodDto, final CustomerPaymentMethodProviderInformationDto<? extends CustomerPaymentMethodProviderInformation> customerPaymentMethodProviderInformationDto) {
            Assert.notNull(customerPaymentMethodDto, "Customer payment method DTO should not be null");
            Assert.notNull(customerPaymentMethodProviderInformationDto, "Customer payment method provider information DTO should not be null");
            this.customerPaymentMethodDto = customerPaymentMethodDto;
            this.customerPaymentMethodProviderInformationDto = customerPaymentMethodProviderInformationDto;
        }

        public CustomerPaymentMethodDto<? extends CustomerPaymentMethod> getCustomerPaymentMethodDto() {
            return customerPaymentMethodDto;
        }

        public CustomerPaymentMethodProviderInformationDto<? extends CustomerPaymentMethodProviderInformation> getCustomerPaymentMethodProviderInformationDto() {
            return customerPaymentMethodProviderInformationDto;
        }

        /* ToString */
        @Override
        public String toString() {
            final ToStringBuilder builder = new ToStringBuilder(this);
            builder.append("customerPaymentMethodDto", customerPaymentMethodDto);
            builder.append("customerPaymentMethodProviderInformationDto", customerPaymentMethodProviderInformationDto);
            return builder.build();
        }
    }
}
