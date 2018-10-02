package com.sfl.pms.services.payment.common.impl.status;

import com.sfl.pms.externalclients.payment.adyen.model.datatypes.AdyenPaymentStatus;
import com.sfl.pms.services.payment.common.model.PaymentResultStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * User: Ruben Dilanyan
 * Company: SFL LLC
 * Date: 7/13/15
 * Time: 2:06 PM
 */
@Component
public class PaymentResultStatusMapperImpl implements PaymentResultStatusMapper {
    private static final Logger LOGGER = LoggerFactory.getLogger(PaymentResultStatusMapperImpl.class);

    private static final Map<AdyenPaymentStatus, PaymentResultStatus> ADYEN_TO_PAYMENT_STATUS_MAPPING;

    private static final List<String> ACAPTURE_SUCCESSFUL_STATUS_CODE_PATTERNS;
    private static final List<String> ACAPTURE_PENDING_STATUS_CODE_PATTERNS;
    private static final List<String> ACAPTURE_ERROR_STATUS_CODE_PATTERS;
    private static final List<String> ACAPTURE_REFUSED_STATUS_CODE_PATTERS;

    static {
        final Map<AdyenPaymentStatus, PaymentResultStatus> tempStatusMap = new HashMap<>();
        tempStatusMap.put(AdyenPaymentStatus.AUTHORISED, PaymentResultStatus.PAID);
        tempStatusMap.put(AdyenPaymentStatus.CANCELLED, PaymentResultStatus.CANCELLED);
        tempStatusMap.put(AdyenPaymentStatus.ERROR, PaymentResultStatus.ERROR);
        tempStatusMap.put(AdyenPaymentStatus.PENDING, PaymentResultStatus.PENDING);
        tempStatusMap.put(AdyenPaymentStatus.REFUSED, PaymentResultStatus.REFUSED);
        tempStatusMap.put(AdyenPaymentStatus.RECEIVED, PaymentResultStatus.RECEIVED);
        // Publish map
        ADYEN_TO_PAYMENT_STATUS_MAPPING = Collections.unmodifiableMap(tempStatusMap);

        /* Full list of acapture result codes - https://docs.acaptureservices.com/reference/resultCodes */
        ACAPTURE_SUCCESSFUL_STATUS_CODE_PATTERNS = Collections.unmodifiableList(Arrays.asList(
            "^(000\\.000\\.|000\\.100\\.1|000\\.[36])",     //Successfully processed
            "^(000\\.400\\.0[^3]|000\\.400\\.100)"          //Successfully processed but should be manually reviewed
        ));

        ACAPTURE_PENDING_STATUS_CODE_PATTERNS = Collections.unmodifiableList(Arrays.asList(
            "^(000\\.200)",                                 //Open session which should be closed in 30 minutes or timeout
            "^(800\\.400\\.5|100\\.400\\.500)"              //Open session which can be closed after few days
        ));

        ACAPTURE_ERROR_STATUS_CODE_PATTERS = Collections.unmodifiableList(Arrays.asList(
            "^(900\\.[1234]00|000\\.400\\.030)",            // Communication errors
            "^(800\\.5|999\\.|600\\.1|800\\.800\\.8)",      // System errors
            "^(600\\.[23]|500\\.[12]|800\\.121)",           //Configuration errors
            "^(100\\.380\\.[23]|100\\.380\\.101)"           //Risk management errors
        ));

        ACAPTURE_REFUSED_STATUS_CODE_PATTERS = Collections.unmodifiableList(Arrays.asList(
            "^(100\\.400|100\\.38|100\\.370\\.100|100\\.370\\.11)",         //External risk system validations
            "^(800\\.400\\.1)",                                             //Address validations
            "^(800\\.400\\.2|100\\.380\\.4|100\\.390)",                     //3D secure validations
            "^(100\\.100\\.701|800\\.[32])",                                //Blacklist validations
            "^(800\\.1[123456]0)",                                          //Risk validations
            "^(100\\.[13]50)",                                              //Registration validations
            "^(100\\.250|100\\.360)",                                       //Job validations
            "^(700\\.[1345][05]0)",                                         //Reference validations
            "^(200\\.[123]|100\\.[53][07]|800\\.900|100\\.[69]00\\.500)",   //Format validations
            "^(100\\.800)",                                                 //Address validations
            "^(100\\.[97]00)",                                              //Contact validations
            "^(100\\.100|100.2[01])",                                       //Account validations
            "^(100\\.55)",                                                  //Amount validations
            "^(000\\.100\\.2)"                                              //Chargeback results
        ));
    }

    public PaymentResultStatusMapperImpl() {
        LOGGER.debug("Initializing payment result status mapper");
    }

    @Override
    public PaymentResultStatus getPaymentResultStatusForAdyenPaymentStatus(@Nonnull final AdyenPaymentStatus adyenPaymentStatus) {
        Assert.notNull(adyenPaymentStatus, "Adyen payment status should not be null");
        return ADYEN_TO_PAYMENT_STATUS_MAPPING.get(adyenPaymentStatus);
    }

    @Override
    public PaymentResultStatus getPaymentResultStatusForAcapturePaymentStatus(final String acapturePaymentStatus) {
        for (final String pattern : ACAPTURE_SUCCESSFUL_STATUS_CODE_PATTERNS) {
            if(acapturePaymentStatus.matches(pattern))
                return PaymentResultStatus.PAID;
        }
        for (final String pattern : ACAPTURE_PENDING_STATUS_CODE_PATTERNS) {
            if(acapturePaymentStatus.matches(pattern))
                return PaymentResultStatus.PENDING;
        }
        for (final String pattern : ACAPTURE_REFUSED_STATUS_CODE_PATTERS) {
            if(acapturePaymentStatus.matches(pattern))
                return PaymentResultStatus.REFUSED;
        }
        for (final String pattern : ACAPTURE_ERROR_STATUS_CODE_PATTERS) {
            if(acapturePaymentStatus.matches(pattern))
                return PaymentResultStatus.ERROR;
        }
        LOGGER.warn("No known status pattern to match result code - {}", acapturePaymentStatus);
        return PaymentResultStatus.ERROR;
    }
}
