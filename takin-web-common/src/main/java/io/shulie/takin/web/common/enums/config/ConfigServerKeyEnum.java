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
    TAKIN_FILE_OPS_SCRIPT_DEPLOY_USER_ENABLE("file.ops_script.deploy_user_enable",
        "takin.file.ops_script.deploy_user_enable", NO),

    /**
     * 白名单长度校验
     */
    TAKIN_WHITE_LIST_NUMBER_LIMIT("whitelist.number.limit", "takin.white_list.number.limit", NO),

    /**
     * 是否开启白名单重名校验
     */
    TAKIN_WHITE_LIST_DUPLICATE_NAME_CHECK("whitelist.duplicate.name.check", "takin.white_list.duplicate.name.check",
        YES),
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
     * 文件上传到 cloud 的地址
     */
    TAKIN_FILE_UPLOAD_URL("file.upload.url", "takin.file.upload.url", NO),

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
     * 查询分片的 标准值
     */
    TAKIN_QUERY_ASYNC_CRITICA_VALUE("query.async.critica.value", "takin.query.async.critica.value", NO),

    /**
     * 性能数据清空时间 单位秒, 几天之前的xxx
     */
    TAKIN_PERFORMANCE_CLEAR_SECOND("performance.clear.second", "takin.performance.clear.second", NO),

    /**
     * 是否更新版本
     */
    AGENT_HTTP_UPDATE_VERSION("agent.http.update.version", "agent.http.update.version", YES),

    /**
     * 每个租户在定时任务中允许执行的最大线程数
     */
    PER_TENANT_ALLOW_TASK_THREADS_MAX("", "per.tenant.allow.task.threads.max", YES),

    /**
     * 压测引擎上传时间间隔 单位毫秒
     */
    TAKIN_PRESSURE_MACHINE_UPLOAD_INTERVAL_TIME("pressure.machine.upload.interval.time",
        "takin.pressure.machine.upload.interval.time", NO),

    /**
     * 脚本调试支持的 rpcType mq 下的
     * 以 逗号隔开
     * 默认 kafka, 可以扩展 rocket mq 等..
     * 暂时这么设计
     */
    TAKIN_SCRIPT_DEBUG_RPC_TYPE("takin-web.script-debug.rpcType", "takin.script-debug.rpcType", NO),

    /**
     * 压测时, 调试时, 否检查应用
     */
    TAKIN_START_TASK_CHECK_APPLICATION("start.task.check.application", "takin.start.task.check.application", YES),

    /**
     * 统计时间, 单位 毫秒
     */
    TAKIN_RISK_COLLECT_TIME("risk.collect.time", "takin.risk.collect.time", NO),

    /**
     * 等待时间 单位秒
     */
    TAKIN_PRADAR_SWITCH_PROCESSING_WAIT_TIME("pradar.switch.processing.wait.time",
        "takin.pradar.switch.processing.wait.time", NO),

    /**
     * 租户 id
     */
    TAKIN_TENANT_ID("customer.id", "takin.customer.id", NO),

    /**
     * 最大CPU使用率
     */
    TAKIN_RISK_MAX_NORM_SCALE("risk.max.norm.scale", "takin.risk.max.norm.scale", NO),

    /**
     * 最大CPU Load
     */
    TAKIN_RISK_MAX_NORM_MAX_LOAD("risk.max.norm.maxLoad", "takin.risk.max.norm.maxLoad", NO),

    /**
     * 黑名单修复
     */
    TAKIN_BLACKLIST_DATA_FIX_ENABLE("blacklist.data.fix.enable", "takin.blacklist.data.fix.enable", NO),

    /**
     * 修复旧的业务数据
     */
    TAKIN_LINK_FIX_ENABLE("link.fix.enable", "takin.link.fix.enable", NO),

    /**
     * 文件上传地址
     */
    TAKIN_FILE_UPLOAD_USER_DATA_DIR("file.upload.user.data.dir", "takin.file.upload.user.data.dir", NO),

    /**
     * 文件上传临时路径
     */
    TAKIN_FILE_UPLOAD_TMP_PATH("file.upload.tmp.path", "takin.file.upload.tmp.path", NO),

    /**
     * 文件脚本上传路径
     */
    TAKIN_FILE_UPLOAD_SCRIPT_PATH("file.upload.script.path", "takin.file.upload.script.path", NO),

    /**
     * 基础路径
     */
    TAKIN_BASE_PATH("basePath", "takin.basePath", NO),

    /**
     * 插件 环境
     */
    TAKIN_PLUGIN_RUN_MODE("runMode", "takin.plugin.runMode", NO),

    /**
     * 插件路径
     */
    TAKIN_PLUGIN_PATH("pluginPath", "takin.plugin.path", NO),

    /**
     * plugin config file path
     */
    TAKIN_PLUGIN_CONFIG_FILE_PATH("pluginConfigFilePath", "takin.plugin.configFilePath", NO),

    /**
     * 版本
     */
    TAKIN_PLUGIN_VERSION("version", "takin.plugin.version", NO),

    /**
     * 路径前缀
     */
    TAKIN_PLUGIN_PREFIX_PATH("pluginPrefixPath", "takin.plugin.prefixPath", NO),

    /**
     * 排列的插件id, 字符串 json 数组
     */
    TAKIN_PLUGIN_SORT_PLUGIN_IDS("sortPluginIds", "takin.plugin.sortPluginIds", NO),

    /**
     * 禁用的插件, 字符串 json 数组
     */
    TAKIN_PLUGIN_DISABLE_PLUGIN_IDS("disablePluginIds", "takin.plugin.disablePluginIds", NO),

    /**
     * 启用的插件, 字符串 json 数组
     */
    TAKIN_PLUGIN_ENABLE_PLUGIN_IDS("enablePluginIds", "", NO),

    /**
     * 允许正确的版本
     */
    TAKIN_PLUGIN_EXACT_VERSION_ALLOWED("exactVersionAllowed", "takin.plugin.exactVersionAllowed", NO),

    /**
     * 启用插件前缀
     */
    TAKIN_PLUGIN_ENABLE_PLUGIN_PREFIX_PATH_PLUGIN_ID("enablePluginPrefixPathPluginId", "takin.plugin.enablePluginPrefixPathPluginId", NO),

    /**
     * 调试工具调用栈保存路径
     */
    TAKIN_FAST_DEBUG_CALL_STACK_PATH("fast.debug.call.stack.path", "takin.fast.debug.call.stack.path", NO),

    /**
     * 调试工具调用栈监听时间, 单位秒
     */
    TAKIN_FAST_WATCH_TIME_SECOND("fast.debug.watch.time.second", "takin.fast.watch.time.second", NO),

    /**
     * 钉钉推送是否开启
     */
    TAKIN_LOGIN_DING_DING_PUSH_ENABLE("login.dingding.push.enable", "takin.login.dingding.push.enable", NO),

    /**
     * 钉钉推送 url
     */
    TAKIN_LOGIN_DING_DING_PUSH_URL("login.dingding.push.url", "takin.login.dingding.push.url", NO),

    /**
     * 试用用户的默认流量
     */
    TAKIN_ASSET_BALANCE_DEFAULT_TRY("", "takin.asset.balance.default.try", NO),

    /**
     * 正式用户的默认流量
     */
    TAKIN_ASSET_BALANCE_DEFAULT_FORMAL("", "takin.asset.balance.default.formal", NO),

    /**
     * 创建租户默认的密码
     */
    TAKIN_TENANT_DEFAULT_PASSWORD("", "takin.tenant.default.password", YES),

    /**
     * 是否同步数据到zk
     */
    TAKIN_ENABLE_SYN_CONFIG("", "takin.enable.syn.config", NO),

    /**
     * 升级文档地址
     */
    TAKIN_UPGRADE_DOCUMENT_URL("", "takin.upgrade.document.url", NO),

    /**
     * 租户集群选择策略
     */
    TAKIN_ENGINE_SELECTOR_STRATEGY("", "takin.engine.selector.strategy", YES),
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
