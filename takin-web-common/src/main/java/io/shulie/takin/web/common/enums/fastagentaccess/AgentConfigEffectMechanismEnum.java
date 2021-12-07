package io.shulie.takin.web.common.enums.fastagentaccess;

import lombok.Getter;
import lombok.AllArgsConstructor;

/**
 * 配置生效机制
 *
 * @author ocean_wll
 * @date 2021/8/13 10:02 上午
 */
@Getter
@AllArgsConstructor
public enum AgentConfigEffectMechanismEnum {
    /**
     * 重启生效
     */
    REBOOT(0, "重启生效"),
    /**
     * 立即生效
     */
    IMMEDIATELY(1, "立即生效"),
    ;

    private Integer val;
    private String desc;
}
