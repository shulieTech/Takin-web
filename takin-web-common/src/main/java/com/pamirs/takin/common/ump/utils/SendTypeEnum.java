package com.pamirs.takin.common.ump.utils;

import java.util.List;
import java.util.ArrayList;

/**
 * @author zhh
 * @author 2018/9/1
 */
public enum SendTypeEnum {
    /**
     * SMS
     */
    SMS(0b001),
    /**
     * 微信-
     */
    WECHART_OFFICIAL(0b010),
    /**
     * 微信-订阅
     */
    WECHART_PUB(0b100),
    /**
     * 支付宝
     */
    ALIPAY(0b1000);

    private final int type;

    SendTypeEnum(int type) {
        this.type = type;
    }

    /**
     * getSendTypes
     *
     * @param t -
     * @return -
     */
    public static List<SendTypeEnum> getSendTypes(int t) {
        List<SendTypeEnum> sendTypeEnumList = new ArrayList<>();
        for (SendTypeEnum sendTypeEnum : SendTypeEnum.values()) {
            if (sendTypeEnum.isType(t)) {
                sendTypeEnumList.add(sendTypeEnum);
            }
        }
        return sendTypeEnumList;
    }

    public int getType() {
        return type;
    }

    /**
     * isType
     *
     * @param t -
     * @return -
     */
    private boolean isType(int t) {
        return (t & type) > 0;
    }
}
