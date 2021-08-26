package com.pamirs.takin.common.constant;

/**
* @author qianshui
 * @date 2020/5/11 下午7:58
 */
public enum TimeUnitEnum {

    HOUR("h", "时"),
    MINUTE("m", "分"),
    SECOND("s", "秒");

    private String value;
    private String name;

    TimeUnitEnum(String value, String name) {
        this.value = value;
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
