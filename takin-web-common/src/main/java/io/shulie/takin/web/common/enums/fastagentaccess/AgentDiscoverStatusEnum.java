package io.shulie.takin.web.common.enums.fastagentaccess;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 新应用检测状态枚举类
 *
 * @author ocean_wll
 * @date 2021/8/27 11:08 上午
 */
@Getter
@AllArgsConstructor
public enum AgentDiscoverStatusEnum {
    /**
     * 检测中
     */
    ING(0, "检测中"),
    /**
     * 安装成功
     */
    SUCCESS(1, "安装成功"),
    /**
     * 安装失败
     */
    FAIL(2, "安装失败"),
    ;

    private Integer val;
    private String desc;
}
