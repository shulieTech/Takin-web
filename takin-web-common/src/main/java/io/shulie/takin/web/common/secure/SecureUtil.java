package io.shulie.takin.web.common.secure;

import cn.hutool.crypto.symmetric.AES;

/**
 * @author HengYu
 * @className SecureUtil
 * @date 2021/4/15 10:46 上午
 * @description  加密解密工具类
 */
public class SecureUtil {

    /**
     * 数据源 加密key encryption
     */
    private static final byte[] DS_ENCRYPTION_BYTES_KEYS = new byte[]{3, -93, 109, 83, 112, 48, -8, 46, -44, 89, 64, -42, -71, -8, -51, 16};

    /**
     * 数据加密方法
     * @param param 需要加密的值
     * @return
     */
    public static String encryption(String param){
        byte[] key = DS_ENCRYPTION_BYTES_KEYS;
        AES aes = cn.hutool.crypto.SecureUtil.aes(key);
        String value = aes.encryptHex(String.valueOf(param));
        return value;
    }

    /**
     * 数据加密方法
     * @param param 需要加密的值
     * @return
     */
    public static String decrypt(String param){
        byte[] key = DS_ENCRYPTION_BYTES_KEYS;
        AES aes = cn.hutool.crypto.SecureUtil.aes(key);
        String value = aes.decryptStr(String.valueOf(param));
        return value;
    }


}
