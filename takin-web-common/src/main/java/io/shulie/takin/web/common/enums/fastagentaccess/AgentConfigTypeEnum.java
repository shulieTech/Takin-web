package io.shulie.takin.web.common.enums.fastagentaccess;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description 探针配置类型枚举
 * @Author ocean_wll
 * @Date 2021/8/13 9:56 上午
 */
@AllArgsConstructor
@Getter
public enum AgentConfigTypeEnum {
    GLOBAL(0, "全局配置"),
    PROJECT(1, "应用配置"),
    ;

    private Integer val;
    private String desc;

}
