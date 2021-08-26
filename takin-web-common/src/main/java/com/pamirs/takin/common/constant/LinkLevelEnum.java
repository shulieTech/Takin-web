package com.pamirs.takin.common.constant;

/**
 * 链路级别枚举
 *
 * @author shulie
 * @version v1.0
 * @2018年5月16日
 */
public enum LinkLevelEnum {
    //一级链路标志，queryApplicationListByLinkInfo方法中使用
    FIRST_LINK_LEVEL("FIRST_LINK"),
    //二级链路标志，queryApplicationListByLinkInfo方法中使用
    SECOND_LINK_LEVEL("SECOND_LINK"),
    //基础链路标志，queryApplicationListByLinkInfo方法中使用
    BASE_LINK_LEVEL("BASE_LINK");

    //链路级别名称
    private String name;

    /**
     * 构造方法
     *
     * @param name 链路级别名称
     */
    LinkLevelEnum(String name) {
        this.name = name;
    }

    /**
     * 获取name属性值
     *
     * @return 返回name值
     */
    public String getName() {
        return name;
    }

    /**
     * 设置name属性
     */
    public void setName(String name) {
        this.name = name;
    }
}
