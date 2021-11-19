package io.shulie.takin.web.common.enums.fastagentaccess;

import lombok.Getter;
import lombok.AllArgsConstructor;

/**
 * 探针配置类型枚举
 *
 * @author ocean_wll
 * @date 2021/8/13 9:56 上午
 */
@Getter
@AllArgsConstructor
public enum AgentConfigTypeEnum {

    /**
     * 全局配置
     */
    GLOBAL(0, "全局配置"),
    /**
     * 应用配置
     */
    PROJECT(1, "应用配置"),

    /**
     * 租户全局配置
     */
    TENANT_GLOBAL(6, "租户全局配置"),
    ;

    private final Integer val;
    private final String desc;

    /**
     * 全局配置
     *
     * @param type 类型
     * @return 是否是
     */
    public static boolean isGlobal(Integer type) {
        return GLOBAL.getVal().equals(type);
    }

    /**
     * 租户全局配置
     *
     * @param type 类型
     * @return 是否是
     */
    public static boolean isTenantGlobal(Integer type) {
        return TENANT_GLOBAL.getVal().equals(type);
    }

    /**
     * 应用配置
     *
     * @param type 类型
     * @return 是否是
     */
    public static boolean isProject(Integer type) {
        return PROJECT.getVal().equals(type);
    }

}
