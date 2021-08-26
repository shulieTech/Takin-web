package com.pamirs.takin.common.enums.command;

import jdk.nashorn.internal.objects.annotations.Constructor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author 无涯
 * @date 2021/1/21 10:51 上午
 * 用于枚举命令
 */
@AllArgsConstructor
@Getter
public enum AgentCommandEnum {
    // 拉取 agentTrace 方法追踪用,版本对应takin-4.6.1
    PULL_AGENT_TRACE_COMMAND("PULL_AGENT_TRACE_COMMAND","方法追踪通道"),
    // dump数据拉取
    PULL_DUMP_HEAP_COMMAND("PULL_DUMP_HEAP_COMMAND","dump数据")
    ;

    private String commandId;
    private String description;


}
