package com.pamirs.takin.common.constant;

/**
 * @author qianshui
 * @date 2020/4/27 上午11:20
 */
public enum YNEnum implements GeneralEnum {
    /**
     * 否
     */
    NO(0, "否"),
    /**
     * 是
     */
    YES(1, "是");

    private String desc;
    private Integer value;

    YNEnum(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }

    @Override
    public String getDesc() {
        return this.desc;
    }
}
