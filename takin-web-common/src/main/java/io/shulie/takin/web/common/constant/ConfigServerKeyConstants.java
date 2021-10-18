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
    String TAKIN_REPORT_OPEN_TASK = "takin.report.open.task";


    /**
     * zk 地址
     */
    String TAKIN_REMOTE_CLIENT_DOWNLOAD_URI = "takin.remote.client.download.uri";
    String TAKIN_APPLICATION_NEW_AGENT = "takin.application.new-agent";
    String TAKIN_SCRIPT_DEBUG_RPCTYPE = "takin.script-debug.rpcType";

    /**
     * 租户 id
     */
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
