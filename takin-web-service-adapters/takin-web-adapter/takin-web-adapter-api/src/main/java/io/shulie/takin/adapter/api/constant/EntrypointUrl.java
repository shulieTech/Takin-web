package io.shulie.takin.adapter.api.constant;

/**
 * 入口配置
 *
 * @author 张天赐
 */
@SuppressWarnings("SpellCheckingInspection")
public class EntrypointUrl {
    public static String join(String... path) {
        return  BASIC + "/"
                + String.join("/", path);
    }

    /**
     * 基础路径
     */
    public final static String BASIC = "";

    /**
     * 模块 - 通用接口
     */
    public final static String MODULE_COMMON = "common";
    public final static String METHOD_COMMON_VERSION = "version";


    /**
     * 模块 - 回调
     */
    public final static String MODULE_CALLBACK = "callback";
    // 改的的话需要调整ee中的agentInterceptor
    public final static String METHOD_ENGINE_CALLBACK_TASK_RESULT_NOTIFY = "";
    public final static String CALL_BACK_PATH = EntrypointUrl.join("api", EntrypointUrl.MODULE_CALLBACK,
        EntrypointUrl.METHOD_ENGINE_CALLBACK_TASK_RESULT_NOTIFY);

    /**
     * 模块 - 任务
     */
    public final static String MODULE_RRESSURE = "job";
    public final static String METHOD_RRESSURE_START = "pressure/start";
    public final static String METHOD_RRESSURE_STOP = "pressure/stop";
    public final static String METHOD_FILE_ANNOUNCE = "file/announce";
    public final static String METHOD_FILE_CHECK = "script/announce";
    public final static String METHOD_DATA_CALIBRATION = "calibration/announce";
    public final static String METHOD_RESOURCE_LOCK = "resource/lock";
    public final static String METHOD_RESOURCE_UNLOCK = "resource/unlock";
    public final static String METHOD_RRESSURE_MODIFY = "expand/pressure/config/modify";
    public final static String METHOD_RRESSURE_PARAMS = "expand/pressure/config/get";
    public final static String MODULE_RESOURCE_CHECK = "expand/resource/check";
    public final static String METHOD_RESOURCE_MACHINE = "expand/resource/example/list";
    public final static String METHOD_SCRIPT_BUILD = "/expand/script/build";

    /**
     * 调度器
     */
    private final static String BATCH = "/batch";
    public final static String MODULE_WATCHMAN = "watchman";
    public final static String MATHOD_WATCHMAN_LIST = "list";
    public final static String MATHOD_WATCHMAN_STATUS = "status";
    public final static String MATHOD_WATCHMAN_STATUS_BATCH = MATHOD_WATCHMAN_STATUS + BATCH;
    public final static String MATHOD_WATCHMAN_RESOURCE = "resource";
    public final static String MATHOD_WATCHMAN_RESOURCE_BATCH = MATHOD_WATCHMAN_RESOURCE + BATCH;
    public final static String MATHOD_WATCHMAN_REGISTE = "registe";
    public final static String MATHOD_WATCHMAN_UPDATE = "update";
    public final static String MATHOD_WATCHMAN_UPDATE_BATCH = MATHOD_WATCHMAN_UPDATE + BATCH;

    /**
     * 模块 - 文件管理
     */
    public final static String MODULE_FILE = "file";
    /**
     * 模块 - 大文件管理
     */
    public final static String MODULE_FILE_BIG = MODULE_FILE + "/big";
    public final static String METHOD_BIG_FILE_UPLOAD = "upload";
    public final static String METHOD_BIG_FILE_COMPACT = "compact";
    public final static String METHOD_BIG_FILE_DOWNLOAD = "download";

    public final static String MODULE_ENGINE = "engine";
    public final static String METHOD_FILE_DOWNLOAD = "file/download";
    public final static String ENGINE_FILE_DOWNLOAD = EntrypointUrl.join("api", EntrypointUrl.MODULE_ENGINE,
        EntrypointUrl.METHOD_FILE_DOWNLOAD);

    /**
     * 压力机注册
     */
    public final static String CLUSTER_REGISTER = EntrypointUrl.join("api", "tenant/engine/register");
}
