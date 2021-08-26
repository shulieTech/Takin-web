package io.shulie.takin.web.biz.agent;

import io.shulie.takin.channel.bean.CommandRespType;
import io.shulie.takin.web.common.constant.AgentUrls;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author 无涯
 * @date 2021/1/22 3:49 下午
 * agent 命令枚举
 */
@AllArgsConstructor
@Getter
public enum AgentCommandEnum {
    /**
     *
     */
    PULL_AGENT_DUMP_HEADDUMP_COMMAND("DEFAULT_CHANNEL", "dump", "heapdump", CommandRespType.COMMAND_CALLBACK, "", 10000L,
            false),
    /**
     *
     */
    PULL_AGENT_LOG_COMMAND("DEFAULT_CHANNEL", "pullAgentLog", "log-data-pusher", CommandRespType.COMMAND_HTTP_PUSH,
            "/api/fast/debug/log/upload", 10000L, false),
    /**
     *
     */
    PULL_APP_LOG_COMMAND("DEFAULT_CHANNEL", "pullAppLog", "log-data-pusher", CommandRespType.COMMAND_HTTP_PUSH,
            "/api/fast/debug/log/upload", 10000L, false),
    /**
     * 拉取 agentTrace 方法追踪用,版本对应takin-4.6.1
     */
    PULL_AGENT_INFO_TRACE_COMMAND("DEFAULT_CHANNEL", "info", "trace", CommandRespType.COMMAND_HTTP_PUSH,
            AgentUrls.PREFIX_URL + AgentUrls.PERFORMANCE_TRACE_URL,
            10000L,
            false);

    private String commandId;
    private String command;
    private String moduleId;
    private CommandRespType commandRespType;
    private String responsePushUrl;
    /**
     * 命令响应时间
     */
    private Long timeoutMillis;
    private Boolean isAllowMultipleExecute;

}
