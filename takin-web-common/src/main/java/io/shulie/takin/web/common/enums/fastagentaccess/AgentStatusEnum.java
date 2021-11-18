package io.shulie.takin.web.common.enums.fastagentaccess;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description agent状态枚举
 * @Author ocean_wll
 * @Date 2021/8/19 10:15 上午
 */
@Getter
@AllArgsConstructor
public enum AgentStatusEnum {
    INSTALLED(0, "INSTALLED", "安装成功"),
    INSTALL_FAILED(4, "INSTALL_FAILED", "安装失败"),
    ;

    private Integer val;
    private String code;
    private String desc;
}
