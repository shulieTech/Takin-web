package com.pamirs.takin.common.constant;

/**
 * @author mubai<chengjiacai @ shulie.io>
 * @date 2020-04-07 10:13
 */
public enum AppSwitchEnum {
    OPENED("已开启", 0, "OPENED"),
    OPENING("开启中", 1, "OPENING"),
    OPEN_FAILING("开启异常", 2, "OPEN_FAILING"),
    CLOSED("已关闭", 3, "CLOSED"),
    CLOSING("关闭中", 4, "CLOSING"),
    CLOSE_FAILING("关闭异常", 5, "CLOSE_FAILING");

    private String name;
    private Integer value;
    private String code;

    AppSwitchEnum(String name, Integer value, String code) {
        this.name = name;
        this.value = value;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

}
