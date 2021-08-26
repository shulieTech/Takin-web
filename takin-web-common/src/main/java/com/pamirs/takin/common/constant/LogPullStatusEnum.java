package com.pamirs.takin.common.constant;

/**
 * @author mubai
 * @date 2020-12-29 14:25
 */


public enum LogPullStatusEnum {
    PULLING(1, "PULLING"),
    PULLED(0, "PULLED"),
    TIMEOUT(2,"TIMEOUT");

    private Integer code;
    private String name;

    LogPullStatusEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
