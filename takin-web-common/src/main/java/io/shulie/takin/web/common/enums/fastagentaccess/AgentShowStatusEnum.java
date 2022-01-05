package io.shulie.takin.web.common.enums.fastagentaccess;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description agent显示状态枚举
 * @Author 南风
 * @Date 2021/11/16 10:15 上午
 */
@Getter
@AllArgsConstructor
public enum AgentShowStatusEnum {
    NORMAL(1, "正常"),
    ERROR(2,"异常"),

    ;

    private Integer val;
    private String desc;
}
