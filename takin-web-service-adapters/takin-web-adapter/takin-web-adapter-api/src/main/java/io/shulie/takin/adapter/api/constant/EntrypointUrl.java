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
     * 模块 - 资源
     */
    public final static String MODULE_RESOURCE = "resource";
    public final static String METHOD_RESOURCE_MACHINE = "example/list";
    public final static String METHOD_RESOURCE_LOCK = "lock";
    public final static String METHOD_RESOURCE_UNLOCK = "unlock";
    public final static String MODULE_RESOURCE_CHECK = "check";

    /**
     * 模块 - 任务
     */
    public final static String MODULE_RRESSURE = "job";
    public final static String METHOD_RRESSURE_START = "start";
    public final static String METHOD_RRESSURE_STOP = "stop";
    public final static String METHOD_RRESSURE_MODIFY = "config/modify";
    public final static String METHOD_RRESSURE_PARAMS = "config/get";

    /**
     * 调度器
     */
    public final static String MODULE_WATCHMAN = "watchman";
    public final static String MATHOD_WATCHMAN_STATUS = "status";
    public final static String MATHOD_WATCHMAN_RESOURCE = "resource";

    /**
     * 额外的任务
     */
    public final static String MODULE_EXCESS = "excess";
    public final static String METHOD_DATA_CALIBRATION = "job/dataCalibration";


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
}
