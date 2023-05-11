package io.shulie.takin.cloud.common.utils;

import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;

/**
 * @author xjz@io.shulie
 * @date 2023/5/11
 * @desc ase加解密
 */
public class AesUtil {
    
    /**
     * 16位自定义密码 只能16、32、64
     */
    public static final String KEY = "1234567891011123";
    private AesUtil(){}

    /**
     * AES加密
     * @param content 明文密码
     * @return 加密密文
     */
    public static String encoder(String content){

        byte[] byteKey = SecureUtil.generateKey(SymmetricAlgorithm.AES.getValue(), KEY.getBytes()).getEncoded();
        SymmetricCrypto aes = SecureUtil.aes(byteKey);
        return aes.encryptBase64(content);
    }

    /**
     * AES解密  在解密的时候需要使用与加密时使用的同一个AES对象或者同一个KEY才能保证解密成功
     * @param content 加密密文
     * @return 解密之后的明文
     */
    public static String decoder(String content){
        byte[] byteKey = SecureUtil.generateKey(SymmetricAlgorithm.AES.getValue(), KEY.getBytes()).getEncoded();
        SymmetricCrypto aes = SecureUtil.aes(byteKey);
        return aes.decryptStr(content);
    }
}
