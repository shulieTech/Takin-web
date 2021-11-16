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
    UNKNOWN(0, "未知"),
    RUNNING(1, "运行中"),
    ERROR(2,"异常"),
    WAIT_RESTART(3,"待重启"),
    SLEEP(4,"休眠"),
    UNINSTALL(5,"卸载"),
    ;

    private Integer val;
    private String desc;
}
