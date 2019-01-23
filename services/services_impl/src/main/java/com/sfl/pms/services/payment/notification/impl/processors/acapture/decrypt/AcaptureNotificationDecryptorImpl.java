package com.sfl.pms.services.payment.notification.impl.processors.acapture.decrypt;

import com.sfl.pms.services.common.exception.ServicesRuntimeException;
import com.sfl.pms.services.payment.settings.acapture.AcapturePaymentSettingsService;
import com.sfl.pms.services.payment.settings.model.acapture.AcapturePaymentSettings;
import org.apache.commons.io.Charsets;
import org.apache.commons.lang3.ArrayUtils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.annotation.Nonnull;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

/**
 * User: Ruben Vardanyan
 * Company: SFL LLC
 * Date: 1/23/19
 * Time: 4:48 PM
 */
@Component
public class AcaptureNotificationDecryptorImpl implements AcaptureNotificationDecryptor {
    private static final Logger LOGGER = LoggerFactory.getLogger(AcaptureNotificationDecryptorImpl.class);

    private static final  String CIPHER_CODE = "AES/GCM/NoPadding";

    /* Dependencies */
    @Autowired
    private AcapturePaymentSettingsService acapturePaymentSettingsService;

    /* Constructor */
    public AcaptureNotificationDecryptorImpl() {
        LOGGER.debug("Initializing acapture notification decryptor");
    }

    @Nonnull
    @Override
    public String decryptNotificationPayload(@Nonnull final String payload, @Nonnull final String authenticationTag, @Nonnull final String initializationVector) {
        Assert.notNull(payload, "Payload should not be null");
        Assert.notNull(authenticationTag, "Authentication tag should not be null");
        Assert.notNull(initializationVector, "Initialization vector should not be null");
        Security.addProvider(new BouncyCastleProvider());
        final AcapturePaymentSettings paymentSettings = acapturePaymentSettingsService.getActivePaymentSettings();
        final String decryptionKey = paymentSettings.getNotificationDecryptionKey();
        final byte[] keyBytes = DatatypeConverter.parseHexBinary(decryptionKey);
        final byte[] authenticationTagBytes = DatatypeConverter.parseHexBinary(authenticationTag);
        final byte[] initializationVectorBytes = DatatypeConverter.parseHexBinary(initializationVector);
        final byte[] encryptedBytes = DatatypeConverter.parseHexBinary(payload);
        final byte[] cipherBytes = ArrayUtils.addAll(encryptedBytes, authenticationTagBytes);
        final SecretKeySpec keySpec = new SecretKeySpec(keyBytes, 0, 32, "AES");
        final Cipher cipher;
        try {
            cipher = Cipher.getInstance(CIPHER_CODE);
        } catch (NoSuchPaddingException | NoSuchAlgorithmException e) {
            LOGGER.error("Error occurred during getting cipher instance for code - {}", CIPHER_CODE);
            throw new ServicesRuntimeException("Error occurred during getting cipher instance for code " + CIPHER_CODE);
        }
        try {
            cipher.init(Cipher.DECRYPT_MODE, keySpec, new IvParameterSpec(initializationVectorBytes));
        } catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
            LOGGER.error("Error occurred during initializing cipher with mode - {}", Cipher.DECRYPT_MODE);
            throw new ServicesRuntimeException("Error occurred during initializing cipher with mode " + Cipher.DECRYPT_MODE);
        }
        final byte[] bytes;
        try {
            bytes = cipher.doFinal(cipherBytes);
        } catch (BadPaddingException | IllegalBlockSizeException e) {
            LOGGER.error("Error occurred during acapture payload decryption");
            throw new ServicesRuntimeException("Error occurred during acapture payload decryption");
        }
        return new String(bytes, Charsets.UTF_8);
    }
}
