package com.pamirs.takin.common.ump.utils;

/**
 * @author -
 */
public class SendTypeUtils {

    public static int genType(SendTypeEnum... sendTypeEnums) {
        int t = 0;
        for (SendTypeEnum sendTypeEnum : sendTypeEnums) {
            t = t | sendTypeEnum.getType();
        }
        return t;
    }

    public static void main(String[] args) {
        int sms = genType(SendTypeEnum.SMS);
        System.out.println(sms);
        int wechatOff = genType(SendTypeEnum.WECHART_OFFICIAL);
        System.out.println(wechatOff);
        int sendType = SendTypeUtils.genType(SendTypeEnum.WECHART_OFFICIAL, SendTypeEnum.SMS);
        System.out.println(sendType);
    }
}
