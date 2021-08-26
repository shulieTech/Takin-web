package io.shulie.takin.web.common.exception;

import io.shulie.takin.parent.exception.entity.ExceptionReadable;
import lombok.Getter;

/**
 * @author 无涯
 * @date 2021/6/28 4:21 下午
 */
@Getter
@Deprecated
public enum ExceptionCode implements ExceptionReadable {
    /**
     * 请求错误
     */
    SERVLET_ERROR("0000_0000_0000", "请求错误"),

    /**
     * 脚本相关
     */
    SCRIPT_MANAGE_CREATE_PARAM_VALID_ERROR("0000_0001_0001", "创建脚本参数校验失败"),
    /**
     * 脚本创建验证错误
     */
    SCRIPT_MANAGE_CREATE_VALID_ERROR("0000_0001_0002", "创建脚本校验失败"),
    SCRIPT_MANAGE_UPDATE_PARAM_VALID_ERROR("0000_0001_0003", ""),

    /**
     * 脚本更新验证错误
     */
    SCRIPT_MANAGE_UPDATE_VALID_ERROR("0000_0001_0004", ""),

    SCRIPT_MANAGE_DELETE_VALID_ERROR("0000_0001_0007", ""),
    SCRIPT_MANAGE_TAG_ADD_VALID_ERROR("0000_0001_0008", ""),
    SCRIPT_MANAGE_ROLLBACK_VALID_ERROR("0000_0001_0009", ""),
    SCRIPT_MANAGE_VALID_NO_CONFIG("0000_0001_0010", "没有样例配置"),
    SCRIPT_MANAGE_ERROR("0000_0001_0012", ""),
    SCRIPT_MANAGE_EXECUTE_ERROR("0000_0001_0011", "脚本执行失败"),
    /**
     * 脚本调试记录相关
     * <p>
     * 脚本调试错误
     */
    SCRIPT_DEBUG_COMMON_ERROR("0000_0001_0034", "脚本调试公共错误"),
    SCRIPT_DEBUG_REQUEST_ERROR("0000_0001_0032", "脚本调试请求流量明细错误"),
    SCRIPT_DEBUG_CHECK_STATUS_ERROR("0000_0001_0033", "脚本调试状态检查错误"),

    /**
     * 数据源管理相关
     */
    DATASOURCE_MANAGE_TAG_ADD_VALID_ERROR("0000_0005_0001", "标签添加失败"),
    DATASOURCE_ADD_ERROR("0000_0005_0002", "数据源添加失败"),
    DATASOURCE_UPDATE_ERROR("0000_0005_0003", "数据源更新失败"),
    DATASOURCE_DELETE_ERROR("0000_0005_0004", "数据源删除失败"),
    DATASOURCE_TEST_CONNECTION_ERROR("0000_0005_0006", "数据源调试失败"),

    /**
     * 漏数配置相关
     */
    LEAK_SQL_REF_INVALID_ERROR("0000_0006_0002", "不合法的SQL"),
    LEAK_SQL_REF_RUN_ERROR("0000_0006_0003", "SQL验证失败"),

    /**
     * 验证任务相关
     */
    VERIFY_TASK_RUN_FAILED("0000_0007_0002", "验证任务运行失败"),

    /**
     * agent通道异常
     */
    AGENT_REGISTER_ERROR("0000_0009_0001", "agent尚未注册，请检查应用接入状态"),
    AGENT_SEND_ERROR("0000_0009_0002", "上传命令异常"),
    AGENT_RESPONSE_ERROR("0000_0009_0003", "agent响应异常"),

    /**
     * 性能分析 本地方法
     */
    TRACE_MANAGE_PARAM_VALID_ERROR("0000_0010_0001", "本地方法参数校验异常"),
    TRACE_MANAGE_VALID_ERROR("0000_0010_0002", "本地方法校验异常"),
    TRACE_MANAGE_TIMEOUT("0000_0010_0003", "追踪超时"),
    TRACE_MANAGE_ERROR("0000_0010_0004", "追踪失败"),
    DUMP_ERROR("0000_0010_0005", "dump异常"),

    /**
     * 压测调度异常
     */
    SCENE_STOP_ERROR("0000_0012_0001", ""),
    SCENE_CHECK_ERROR("0000_0012_0002", ""),
    /**
     * 场景标签相关
     */
    SCENE_SCHEDULER_TASK_SCENE_ID_VALID_ERROR("0000_0013_0002", ""),
    SCENE_SCHEDULER_TASK_EXECUTE_TIME_VALID_ERROR("0000_0013_0003", ""),

    /**
     * 黑名单
     */
    BLACKLIST_ADD_ERROR("0000_0013_0001", ""),
    BLACKLIST_UPDATE_ERROR("0000_0013_0002", ""),
    BLACKLIST_DELETE_ERROR("0000_0013_0003", ""),
    BLACKLIST_SEARCH_ERROR("0000_0013_0005", ""),

    /**
     * 挡板
     */
    GUARD_PARAM_ERROR("0000_0014_0001", ""),

    /**
     * job
     */
    JOB_PARAM_ERROR("0000_0015_0001", ""),

    /**
     * 白名单格式校验
     */
    WHITELIST_FORMAT_APP_ERROR("0000_0016_0002", ""),

    /**
     * excel导入导出
     */
    EXCEL_IMPORT_ERROR("0000_0017_0001", ""),

    /**
     * pod num
     */
    POD_NUM_EMPTY("0000_0018_0001", ""),

    /**
     * 探针相关
     */
    PROBE_CREATE_ERROR("0000_0050_0001", "新增探针错误"),
    AGENT_APPLICATION_NODE_PROBE_GET_FILE_ERROR("0000_0050_0003", "获得探针包错误"),
    AGENT_APPLICATION_NODE_PROBE_UPDATE_OPERATE_RESULT_ERROR("0000_0050_0004", "更新探针包操作错误"),

    /**
     * 远程调用
     */
    REMOTE_CALL_CONFIG_CHECK_ERROR("0000_0021_0001", ""),
    REMOTE_CALL_CONFIG_GET_ERROR("0000_0021_0002", ""),

    /**
     * 拓扑图异常
     */
    APP_LINK_TOPOLOGY_ERROR("0000_0022_0001", ""),

    /**
     * agent 上报应用中间件错误
     */
    APPLICATION_MIDDLEWARE_PUSH_ERROR("0000_0101_0002", "agent 上报应用中间件错误!"),

    /**
     * 通用错误
     */
    ERROR_COMMON("0000_0100_0001", "通用错误"),

    ZK_ERROR("0000_0102_0001", ""),

    AGENT_INTERFACE_ERROR("0000_0103_0002",""),

    ;

    private final String errorCode;
    private final String defaultValue;

    ExceptionCode(String errorCode, String defaultValue) {
        this.errorCode = errorCode;
        this.defaultValue = defaultValue;
    }
}
