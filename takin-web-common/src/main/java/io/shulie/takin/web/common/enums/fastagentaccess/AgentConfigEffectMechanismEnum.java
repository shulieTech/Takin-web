package io.shulie.takin.web.common.enums.fastagentaccess;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description 配置生效机制
 * @Author ocean_wll
 * @Date 2021/8/13 10:02 上午
 */
@AllArgsConstructor
@Getter
public enum AgentConfigEffectMechanismEnum {
    REBOOT(0, "重启生效"),
    IMMEDIATELY(1, "立即生效"),
    ;

    private Integer val;
    private String desc;
}
