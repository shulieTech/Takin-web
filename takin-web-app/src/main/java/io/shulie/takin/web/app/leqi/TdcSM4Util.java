package io.shulie.takin.web.app.leqi;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.Provider;
import java.security.Security;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.pqc.math.linearalgebra.ByteUtils;
import org.bouncycastle.util.encoders.Hex;

public final class TdcSM4Util {
    private static final Charset ENCODING;
    public static final String ALGORITHM_NAME = "SM4";
    public static final String ALGORITHM_NAME_ECB_PADDING = "SM4/ECB/PKCS5Padding";
    public static final String privateKey = "1079832465abcdef1079832465fedcba";
    public static final String iv = "1079832465fedcba";

    private static Cipher generateEcbCipher(String algorithmName, int mode, byte[] key) throws Exception {
        /* 73*/
        Cipher cipher = Cipher.getInstance(algorithmName, "BC");
        SecretKeySpec sm4Key = new SecretKeySpec(key, ALGORITHM_NAME);
        /* 75*/
        cipher.init(mode, sm4Key);
        /* 76*/
        return cipher;
    }

    public static String encryptEcb(String hexKey, String paramStr) throws Exception {
        /* 90*/
        byte[] keyData = ByteUtils.fromHexString((String) hexKey);
        /* 92*/
        byte[] srcData = paramStr.getBytes(ENCODING);
        /* 94*/
        byte[] cipherArray = TdcSM4Util.encryptEcbPadding(keyData, srcData);
        /* 96*/
        String cipherText = Base64.getEncoder().encodeToString(cipherArray);
        /* 97*/
        return cipherText;
    }

    public static byte[] encryptEcbPadding(byte[] key, byte[] data) throws Exception {
        /*109*/
        Cipher cipher = TdcSM4Util.generateEcbCipher(ALGORITHM_NAME_ECB_PADDING, 1, key);
        /*110*/
        return cipher.doFinal(data);
    }

    public static String decryptEcb(String hexKey, String cipherText) throws Exception {
        /*125*/
        byte[] keyData = ByteUtils.fromHexString((String) hexKey);
        /*127*/
        byte[] cipherData = Base64.getDecoder().decode(cipherText);
        /*129*/
        byte[] srcData = TdcSM4Util.decryptEcbPadding(keyData, cipherData);
        String decryptStr = new String(srcData, ENCODING);
        /*132*/
        return decryptStr;
    }

    public static byte[] decryptEcbPadding(byte[] key, byte[] cipherText) throws Exception {
        /*144*/
        Cipher cipher = TdcSM4Util.generateEcbCipher(ALGORITHM_NAME_ECB_PADDING, 2, key);
        /*145*/
        return cipher.doFinal(cipherText);
    }

    public static String decryptSM4(String key, String iv, String encryptedData) throws Exception {
        Security.addProvider((Provider) new BouncyCastleProvider());
        /*151*/
        byte[] keyBytes = Hex.decode((String) key);
        /*152*/
        byte[] ivBytes = Hex.decode((String) iv);
        /*153*/
        byte[] encryptedBytes = Hex.decode((String) encryptedData);
        SecretKeySpec secretKeySpec = new SecretKeySpec(keyBytes, ALGORITHM_NAME);
        /*156*/
        Cipher cipher = Cipher.getInstance(ALGORITHM_NAME_ECB_PADDING, "BC");
        /*157*/
        cipher.init(2, secretKeySpec);
        /*159*/
        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
        return new String(decryptedBytes);
    }

    private TdcSM4Util() {
    }

    static {
        Security.addProvider((Provider) new BouncyCastleProvider());
        /* 41*/
        ENCODING = StandardCharsets.UTF_8;
    }
}
