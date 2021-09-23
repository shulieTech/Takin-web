package io.shulie.takin.web.common.enums.fastagentaccess;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description 新应用检测状态枚举类
 * @Author ocean_wll
 * @Date 2021/8/27 11:08 上午
 */
@Getter
@AllArgsConstructor
public enum AgentDiscoverStatusEnum {
    ING(0, "检测中"),
    SUCCESS(1, "安装成功"),
    FAIL(2, "安装失败"),
    ;

    private Integer val;
    private String desc;
}
