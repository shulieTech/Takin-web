package io.shulie.takin.web.common.constant;

/**
 * 应用服务配置的 key 常量池
 *
 * @author liuchuan
 * @date 2021/10/12 12:03 下午
 */
public interface ConfigServerKeyConstants {

    String TAKIN_REMOTE_CALL_SYNC = "takin.remote-call.sync";
    String TAKIN_REPORT_OPEN_TASK = "takin.report.open.task";


    /**
     * zk 地址
     */
    String TAKIN_APPLICATION_NEW_AGENT = "takin.application.new-agent";
    String TAKIN_SCRIPT_DEBUG_RPCTYPE = "takin.script-debug.rpcType";

    String TAKIN_BASE_PATH = "takin.basePath";

    /**
     * 运行模式
     */
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
