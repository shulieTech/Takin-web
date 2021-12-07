package io.shulie.takin.web.common.enums.fastagentaccess;

import lombok.Getter;
import lombok.AllArgsConstructor;

/**
 * 配置是否可编辑枚举类
 *
 * @author ocean_wll
 * @date 2021/8/13 10:06 上午
 */
@Getter
@AllArgsConstructor
public enum AgentConfigEditableEnum {
    /**
     * 可编辑
     */
    CAN(0, "可编辑"),
    /**
     * 不可编辑
     */
    NOT(1, "不可编辑"),
    ;

    private Integer val;
    private String desc;
}
