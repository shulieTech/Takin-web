package com.pamirs.takin.common.constant;

/**
 * 瓶颈等级(1、严重，2、普通，3、正常)
 *
 * @author shulie
 * @version v1.0
 * @2018年5月16日
 */
public enum LinkBottleneckLevelEnum {
    BOTTLENECK_LEVEL_SERIOUS(1, "严重"),
    BOTTLENECK_LEVEL_GENERAl(2, "普通"),
    BOTTLENECK_LEVEL_NORMAL(3, "正常");

    private int code;
    private String name;

    /**
     * 构造方法
     *
     * @param name
     */
    LinkBottleneckLevelEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return this.code;
    }
}
