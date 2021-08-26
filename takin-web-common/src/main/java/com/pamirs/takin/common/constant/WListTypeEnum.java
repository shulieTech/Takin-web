package com.pamirs.takin.common.constant;

/**
 * @author shulie
 * @description
 * @create 2019-04-03 19:36:07
 */
public enum WListTypeEnum {

    HTTP("1", "HTTP"),
    DUBBO("2", "DUBBO"),
    FORBIDDEN("3", "禁止压测"),
    JOB("4", "JOB"),
    MQ("5", "MQ");

    /**
     * 白名单类型：1HTTP 2DUBBO 3禁止压测 4JOB 5MQ
     */
    private String value;

    private String name;

    WListTypeEnum(String value, String name) {
        this.value = value;
        this.name = name;
    }

    /**
     * Gets the value of value.
     *
     * @return the value of value
     * @author shulie
     * @version 1.0
     */
    public String getValue() {
        return value;
    }

    public String getName() {
        return name;

    }
}
