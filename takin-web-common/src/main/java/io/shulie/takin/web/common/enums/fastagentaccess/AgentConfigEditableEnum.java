package io.shulie.takin.web.common.enums.fastagentaccess;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description 配置是否可编辑枚举类
 * @Author ocean_wll
 * @Date 2021/8/13 10:06 上午
 */
@AllArgsConstructor
@Getter
public enum AgentConfigEditableEnum {
    CAN(0, "可编辑"),
    NOT(1, "不可编辑"),
    ;

    private Integer val;
    private String desc;
}
