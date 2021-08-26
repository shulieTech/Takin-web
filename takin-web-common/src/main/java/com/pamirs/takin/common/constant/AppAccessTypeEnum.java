package com.pamirs.takin.common.constant;

/**
 * @author mubai<chengjiacai @ shulie.io>
 * @date 2020-03-18 11:19
 */
public enum AppAccessTypeEnum {
    /**
     * 正常
     */
    NORMAL("正常", 0),
    /**
     * 待配置
     */
    UNSETTING("待配置", 1),
    /**
     * 待检测
     */
    UNUPLOAD("待检测", 2),
    /**
     * 异常
     */
    EXCEPTION("异常", 3);

    private String name;
    private Integer value;

    AppAccessTypeEnum(String name, Integer value) {
        this.name = name;
        this.value = value;
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
}
