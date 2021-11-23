package io.shulie.takin.web.common.enums.agentupgradeonline;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description
 * @Author ocean_wll
 * @Date 2021/11/23 10:07 上午
 */
@Getter
@AllArgsConstructor
public enum AgentUpgradeTypeEnum {
    AGENT_REPORT(0, "agent上报"),
    PROACTIVE_UPGRADE(1, "主动升级"),
    ;

    private Integer val;
    private String desc;
}
