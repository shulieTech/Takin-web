package io.shulie.takin.web.common.enums.agentupgradeonline;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description
 * @Author ocean_wll
 * @Date 2021/11/17 3:27 下午
 */
@Getter
@AllArgsConstructor
public enum AgentUpgradeEnum {
    NOT_UPGRADE(0, "未升级"),
    UPGRADE_SUCCESS(1, "升级成功"),
    UPGRADE_FILE(2, "升级失败"),
    ROLLBACK(3, "已回滚"),
    UPGRADING(4,"升级中"),
    ;

    private Integer val;
    private String desc;
}
