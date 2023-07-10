package com.pamirs.takin.common.enums;

public enum ResponseResultEnum {

    RESP_SUCCESS("0", "响应成功"),
    RESP_FAILURE("1", "响应失败"),
    ASSERT_FAILURE("2", "断言失败"),;

    public String code;

    public String desc;

    ResponseResultEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
