package com.pamirs.takin.common.util;

/**
 * @author 何仲奇
 * @date 2020/9/2 9:08 下午
 * @see MD5Util
 */
public class MD5UtilTest {
    public static void main(String[] args) {
        String md5 = MD5Util.getMD5("pradar" + "pradar-takin");
        System.out.println(md5);
    }

}
