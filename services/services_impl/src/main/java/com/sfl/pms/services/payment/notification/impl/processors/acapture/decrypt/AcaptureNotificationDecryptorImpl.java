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

    //TODO: remove, just for test purposes
    public static void main(String[] args) {
        Security.addProvider(new BouncyCastleProvider());
        final byte[] keyBytes = DatatypeConverter.parseHexBinary("C778341D7CC72DFA83FAECE43DF9FF797EFB3A5D316B952730039347D1B15B70");
        final byte[] authenticationTagBytes = DatatypeConverter.parseHexBinary("37A4DB799E715BD905F2A692B9AAE2E7");
        final byte[] initializationVectorBytes = DatatypeConverter.parseHexBinary("99FE7CF64BCD1B5E9129F3DD");
        final byte[] encryptedBytes = DatatypeConverter.parseHexBinary("238ACA5877455C191AFBD1A4DD409950A214CCA5F4506DF702F927A6F16C4798645C4CA98990A58C97BBD6B56B955155C198892143B194DAE9BCC9EE113A44AFE9A1EEB00FFFE6BF417132CC7F309685520EFFB27C3045B54F619C061CCBD497245C1305035B79DB6D9224A55EF2E31F436271F07BA429CA9B2581F4E067A8D37C7B99D35300B405404EFEFCB51758CD80D02CA396D9EB2F3CC8C58B287DA938A05F19FE061337BB3F757B3337EC4842A37F2581C78061164B91F2CA770E8FB268FB541B80E9EB51835B569CE38CBF5E15717411694E74D0DAFB15D4FEE5CAFE54EF22FA2404DBC2D242E3F0C4D314693729F830F64FB9D34559BD22854216B7EAC5E569C40C45B8EE1D7E17C3B11FA9999FBB373DC459B41F0A00994EEECE4FDBE69DADD7194DA729A982E020C305139E2BD0BFE790567709005A6922438A70A8AFD8C2F65433690ADD1067942BEDE3828E77B2B3636DBECEAE486A762600AFEC78710539C560C388ABDD1810C28C48287E809BB5F998B84CF0253CB037159DED7CD93360D7F0AE019B614470F313E247545C4B0E4FE92D3F64AE63C127379DA5971E49085C5551B6AA93F3E2742A49645E47C2E879ECCB034E73F4454CA4ED69EE3D268B5212D8F6CB500C27CB642EF823B7266FB374E3F1025848B3813ED2506F75241DDFBAA239D58A20097797096CE3ACF61BF7C5EAF12932809132AC86C6961308AD289B2081C9D073038D38C8EFA490F0EF0D742ADB74B93A14EB8C24C7D87B6B201CB6E29093206E49ADF44190708E1FF789E11B775E9A6BB7744809548FCC925418202A40DE930BC9326EEFD2A2A893439042278822B77294FCC6DB66A68CF8AB154E52B7B694C44AFE62FDBBDFDEFFBEEEE2BF440A79A7CD1A2E6F9E0E8142386B9FFD1960F47B47F13C581ED5AA3395C344080FC1B1034672B1BDF016FE23467FBF5B2337FB944DF84C5754705727E8EB41275E8AE3571171CAD4E390DDBBBB6193FE58AF46BA0AE4B2245011F8D1F3F089AFAAFA888DA8845BF7BCB97232F2B67A809A6439D8C1958B9FE8460A028DA92B997E56EFC0F3E0FD4E9307DA44400536061CEF15D104F74A62BD75F306EFDE6314B3163063CC3CB93C5B093A0BA617EC9D412964E77F9DD0D2E1B51FF0E0D16EF13082A3BAB7D1C432B88B3026FDBD7C639C587A47DBA1E810161BAB9E145B9F6BC936EDCCE52C95DE881E93CB6E8E06CEC1DECAF14567063234B408B9627D50C4EA8C6406F6DF773C3CC291180320310D29E065E9A04E3BCF2C3DAFCB5AAB539CE1FE99BD3FE84C8A0775955297D677199CCABBEDE913FA3850926A3F9243DDBB4AE2F4A7B7723EE44604CA2658D3B6EB05B1983057428997A5177F8D9504F51402A1F22F2CDAE0AA526DE452E1B5E575AA579D2153DFAD3EFEAED7ADBB5AD0782961D876C2BBF9456C9554DA88CE0BB0");
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
        System.out.println(new String(bytes, Charsets.UTF_8));
    }
}
