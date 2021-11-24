package io.shulie.takin.web.common.enums.fastagentaccess;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Description agent心态状态枚举
 * @Author 南风
 * @Date 2021/11/16 10:15 上午
 */
@Getter
@AllArgsConstructor
public enum AgentReportStatusEnum {
    BEGIN(-1, "启动"),
    UNKNOWN(0, "未知"),
    STARTING(1, "启动中"),
    WAIT_RESTART(2, "升级待重启"),
    RUNNING(3, "运行中"),
    ERROR(4, "异常"),
    SLEEP(5, "休眠"),
    UNINSTALL(6, "卸载"),
    ;

    private Integer val;
    private String desc;
}
