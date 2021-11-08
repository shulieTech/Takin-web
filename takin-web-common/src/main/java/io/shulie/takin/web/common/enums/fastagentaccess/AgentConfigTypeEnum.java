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
    ;

    private Integer val;
    private String desc;

}
