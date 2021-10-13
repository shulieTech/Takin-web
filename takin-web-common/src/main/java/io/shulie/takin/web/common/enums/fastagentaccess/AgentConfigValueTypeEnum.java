package io.shulie.takin.web.common.enums.fastagentaccess;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description agent配置value类型
 * @Author ocean_wll
 * @Date 2021/8/13 10:05 上午
 */
@AllArgsConstructor
@Getter
public enum AgentConfigValueTypeEnum {
    TEXT(0, "文本"),
    RADIO(1, "单选"),
    ;

    private Integer val;
    private String desc;
}
