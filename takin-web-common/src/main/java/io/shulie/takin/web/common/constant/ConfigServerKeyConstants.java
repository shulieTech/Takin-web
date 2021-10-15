package io.shulie.takin.web.common.constant;

/**
 * 应用服务配置的 key 常量池
 *
 * @author liuchuan
 * @date 2021/10/12 12:03 下午
 */
public interface ConfigServerKeyConstants {

    String TAKIN_LICENSE = "takin.license";
    String TAKIN_FAST_DEBUG_CALL_STACK_PATH = "takin.fast.debug.call.stack.path";
    String TAKIN_FAST_WATCH_TIME_SECOND = "takin.fast.watch.time.second";
    String TAKIN_LOGIN_DINGDING_PUSH_ENABLE = "takin.login.dingding.push.enable";
    String TAKIN_LOGIN_DINGDING_PUSH_URL = "takin.login.dingding.push.url";
    String TAKIN_REMOTE_CALL_SYNC = "takin.remote-call.sync";
    String TAKIN_WHITE_LIST_NUMBER_LIMIT = "takin.white_list.number.limit";
    String TAKIN_WHITE_LIST_DUPLICATE_NAME_CHECK = "takin.white_list.duplicate.name.check";
    String TAKIN_WHITE_LIST_CONFIG_PATH = "takin.white_list.config.path";
    String TAKIN_REPORT_OPEN_TASK = "takin.report.open.task";

    /**
     * zk 地址
     */
    String TAKIN_CONFIG_ZK_ADDR = "takin.config.zk.addr";
    String TAKIN_REMOTE_CLIENT_DOWNLOAD_URI = "takin.remote.client.download.uri";
    String TAKIN_APPLICATION_NEW_AGENT = "takin.application.new-agent";
    String AGENT_INTERACTIVE_TAKIN_WEB_URL = "agent.interactive.takin.web.url";
    String AGENT_HTTP_UPDATE_VERSION = "agent.http.update.version";
    String AGENT_REGISTERED_PATH = "agent.registered.path";
    String PERFORMANCE_BASE_AGENT_FREQUENCY_ = "performance.base.agent.frequency.";
    String TAKIN_QUERY_ASYNC_CRITICA_VALUE = "takin.query.async.critica.value";
    String TAKIN_PERFORMANCE_CLEAR_SECOND = "takin.performance.clear.second";
    String TAKIN_PRESSURE_MACHINE_UPLOAD_INTERVAL_TIME = "takin.pressure.machine.upload.interval.time";
    String SPRING_PERFORMANCE_INFLUXDB_DATABASE = "spring.performance.influxdb.database";
    String SPRING_INFLUXDB_URL = "spring.influxdb.url";
    String SPRING_INFLUXDB_DATABASE = "spring.influxdb.database";
    String SPRING_INFLUXDB_USER = "spring.influxdb.user";
    String SPRING_INFLUXDB_PASSWORD = "spring.influxdb.password";
    String TAKIN_SCRIPT_DEBUG_RPCTYPE = "takin.script-debug.rpcType";
    String TAKIN_START_TASK_CHECK_APPLICATION = "takin.start.task.check.application";
    String TAKIN_RISK_COLLECT_TIME = "takin.risk.collect.time";
    String TAKIN_PRADAR_SWITCH_PROCESSING_WAIT_TIME = "takin.pradar.switch.processing.wait.time";

    /**
     * 租户 id
     */
    String TAKIN_CUSTOMER_ID = "takin.customer.id";
    String TAKIN_RISK_MAX_NORM_SCALE = "takin.risk.max.norm.scale";
    String TAKIN_RISK_MAX_NORM_MAXLOAD = "takin.risk.max.norm.maxLoad";
    String TAKIN_BLACKLIST_DATA_FIX_ENABLE = "takin.blacklist.data.fix.enable";
    String TAKIN_LINK_FIX_ENABLE = "takin.link.fix.enable";
    String TAKIN_FILE_UPLOAD_URL = "takin.file.upload.url";
    String TAKIN_FILE_UPLOAD_USER_DATA_DIR = "takin.file.upload.user.data.dir";
    String TAKIN_FILE_UPLOAD_TMP_PATH = "takin.file.upload.tmp.path";
    String TAKIN_FILE_UPLOAD_SCRIPT_PATH = "takin.file.upload.script.path";
    String TAKIN_FAST_DEBUG_UPLOAD_LOG_PATH = "takin.fast.debug.upload.log.path";

    /**
     * 存储位置
     */
    String TAKIN_DATA_PATH = "takin.data.path";

    String TAKIN_BASE_PATH = "takin.basePath";

    /**
     * 运行模式
     */
    String TAKIN_RUN_MODE = "takin.runMode";
    String TAKIN_PLUGINPATH = "takin.pluginPath";
    String TAKIN_PLUGINCONFIGFILEPATH = "takin.pluginConfigFilePath";
    String TAKIN_PLUGINPREFIXPATH = "takin.pluginPrefixPath";
    String TAKIN_ENABLEPLUGINPREFIXPATHPLUGINID = "takin.enablePluginPrefixPathPluginId";
    String TAKIN_ENABLEPLUGINIDS = "takin.enablePluginIds";
    String TAKIN_DISABLEPLUGINIDS = "takin.disablePluginIds";
    String TAKIN_SORTPLUGINIDS = "takin.sortPluginIds";
    String TAKIN_VERSION = "takin.version";
    String TAKIN_EXACTVERSIONALLOWED = "takin.exactVersionAllowed";

}
