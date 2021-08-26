package com.pamirs.takin.common.ump.utils;

public enum PustTypeEnum {

    ONLY(1),     //只发微信
    FAILSMS(2),     //只发微信
    ALLFAILSMS(3);  //失败后转发短信

    private int type;

    PustTypeEnum(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
