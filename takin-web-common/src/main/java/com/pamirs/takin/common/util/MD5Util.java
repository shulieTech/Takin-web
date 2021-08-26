package com.pamirs.takin.common.util;

import java.security.MessageDigest;

/**
 * MD5工具类
 *
 * @author shulie
 * @version v1.0
 * @date 2018年5月21日
 * @see com.pamirs.takin.common.util.MD5UtilTest
 */
public class MD5Util {

    /**
     * 生成md5
     *
     * @param message 传入的字符串
     * @return 字符串对应的md5加密值
     * @author shulie
     * @date 2018年5月21日
     */
    public static String getMD5(String message) {
        String md5str = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] input = message.getBytes();
            byte[] buff = md.digest(input);
            md5str = bytesToHex(buff);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return md5str;
    }

    /**
     * 二进制转十六进制
     *
     * @param bytes 二进制数组
     * @return 十六进制字符串
     * @author shulie
     * @date 2018年5月21日
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuilder md5str = new StringBuilder();
        int digital;
        for (byte aByte : bytes) {
            digital = aByte;
            if (digital < 0) {
                digital += 256;
            }
            if (digital < 16) {
                md5str.append("0");
            }
            md5str.append(Integer.toHexString(digital));
        }
        return md5str.toString().toUpperCase();
    }
}
