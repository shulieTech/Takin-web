package io.shulie.takin.web.common.enums.fastagentaccess;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * agent状态枚举
 *
 * @author ocean_wll
 * @date 2021/8/19 10:15 上午
 */
@Getter
@AllArgsConstructor
public enum AgentStatusEnum {
    /**
     * 安装成功
     */
    INSTALLED(0, "安装成功"),
    /**
     * 安装失败
     */
    INSTALL_FAILED(4, "安装失败"),
    ;

    private Integer val;
    private String desc;
}
