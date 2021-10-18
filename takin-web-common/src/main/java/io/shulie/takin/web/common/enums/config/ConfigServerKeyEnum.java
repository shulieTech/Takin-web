package io.shulie.takin.web.common.enums.config;

import io.shulie.takin.web.common.constant.AppConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author liuchuan
 * @date 2021/10/14 2:40 下午
 */
@Getter
@AllArgsConstructor
public enum ConfigServerKeyEnum implements AppConstants {

    /**
     * zk 配置
     */
    TAKIN_CONFIG_ZOOKEEPER_ADDRESS("takin.config.zk.addr", "takin.config.zk.addr", NO),

    /**
     * takin-web url 配置
     */
    AGENT_TAKIN_WEB_URL("agent.interactive.takin.web.url", "agent.interactive.takin.web.url", NO),

    /**
     * ops 脚本路径 配置
     */
    TAKIN_FILE_OPS_SCRIPT_PATH("file.ops_script.path", "takin.file.ops_script.path", NO),

    /**
     * 指定linux执行脚本用户
     */
    TAKIN_FILE_OPS_SCRIPT_DEPLOY_USER("file.ops_script.deploy_user", "takin.file.ops_script.deploy_user", NO),

    /**
     * 是否执行添加linux用户的操作
     */
    TAKIN_FILE_OPS_SCRIPT_DEPLOY_USER_ENABLE("file.ops_script.deploy_user_enable", "takin.file.ops_script.deploy_user_enable", NO),

    /**
     * 白名单长度校验
     */
    TAKIN_WHITE_LIST_NUMBER_LIMIT("whitelist.number.limit", "takin.white_list.number.limit", NO),

    /**
     * 是否开启白名单重名校验
     */
    TAKIN_WHITE_LIST_DUPLICATE_NAME_CHECK("whitelist.duplicate.name.check", "takin.white_list.duplicate.name.check", YES),
    /**
     * 白名单文件
     */
    TAKIN_WHITE_LIST_CONFIG_PATH("spring.config.whiteListPath", "takin.white_list.config.path", NO),

    /**
     * 从 amdb 同步远程调用到 web
     */
    TAKIN_REMOTE_CALL_SYNC("tro-web.remote-call.sync", "takin.remote-call.sync", YES),

    /**
     * 远程调用是否自动加入
     */
    TAKIN_REMOTE_CALL_AUTO_JOIN_WHITE("remote.call.auto.join.white", "takin.remote.call.auto.join.white", YES),

    /**
     * 压测数据报告收集相关 开关
     */
    TAKIN_REPORT_OPEN_TASK("open.report.task", "takin.report.open.task", YES),

    /**
     * cloud 域名
     */
    TAKIN_CLOUD_URL("takin.cloud.url", "takin.cloud.url", NO),

    /**
     * 上传文件保存路径
     */
    TAKIN_DATA_PATH("data.path", "takin.data.path", NO),

    /**
     * 客户端下载地址
     */
    TAKIN_REMOTE_CLIENT_DOWNLOAD_URI("remote.client.download.uri", "takin.remote.client.download.uri", NO),

    /**
     * 是否进行脚本检查
     */
    TAKIN_SCRIPT_CHECK("script.check", "takin.script.check", YES),

    /**
     * 是否开启流量验证
     */
    TAKIN_LINK_FLOW_CHECK_ENABLE("link.flow.check.enable", "takin.link.flow.check.enable", YES),

    /**
     * 应用下的 新旧 agent 配置, 1 新, 0 旧, -1 无配置
     */
    TAKIN_APPLICATION_NEW_AGENT("takin-web.application.new-agent", "takin.application.new-agent", YES),

    /**
     * agent 注册地址
     */
    AGENT_REGISTERED_PATH("agent.registered.path", "agent.registered.path", NO),

    /**
     * 性能数据分析
     */
    PERFORMANCE_BASE_AGENT_FREQUENCY("performance.base.agent.frequency", "performance.base.agent.frequency", YES),

    /**
     * 暂不知
     */
    TAKIN_QUERY_ASYNC_CRITICA_VALUE("query.async.critica.value", "takin.query.async.critica.value", NO),

    /**
     * 性能数据清空时间 单位秒, 几天之前的xxx
     */
    TAKIN_PERFORMANCE_CLEAR_SECOND("performance.clear.second", "takin.performance.clear.second", NO),
    ;

    /**
     * 原先的 key
     */
    private final String old;

    /**
     * 改造后的 key
     */
    private final String now;

    /**
     * 是否用于租户, 1 是, 0 否
     */
    private final Integer isTenant;

}
