package io.shulie.takin.web.common.enums.fastagentaccess;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * agent配置value类型
 *
 * @author ocean_wll
 * @date 2021/8/13 10:05 上午
 */
@AllArgsConstructor
@Getter
public enum AgentConfigValueTypeEnum {
    /**
     * 文本
     */
    TEXT(0, "文本"),
    /**
     * 单选
     */
    RADIO(1, "单选"),
    ;

    private Integer val;
    private String desc;
}
