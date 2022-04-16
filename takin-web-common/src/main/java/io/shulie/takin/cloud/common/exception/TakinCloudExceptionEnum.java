package io.shulie.takin.cloud.common.exception;

import io.shulie.takin.parent.exception.entity.ExceptionReadable;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author shiyajian
 * create: 2020-09-04
 * 这边添加的所有code，需要在message.properties中有对应的值
 */
@Getter
@AllArgsConstructor
public enum TakinCloudExceptionEnum implements ExceptionReadable {

    /**
     * 通用校验错误
     */
    COMMON_VERIFY_ERROR("cloud-000-s0000", "校验失败"),

    /**
     * 压测场景相关模块 cloud-001-
     * 0200新增  0300更新 0400查询 0500文件相关
     */
    SCENE_MANAGE_ADD_ERROR("cloud-001-s0201", "新增场景失败"),
    SCENE_MANAGE_BUILD_PARAM_ERROR("cloud-001-s0202", "场景构建参数失败"),

    SCENE_MANAGE_UPDATE_VERIFY_ERROR("cloud-001-s0300", "更新场景校验失败"),
    SCENE_MANAGE_UPDATE_ERROR("cloud-001-s0301", "更新场景失败"),
    SCENE_MANAGE_UPDATE_FILE_ERROR("cloud-001-s0302", "场景管理之更新相关文件失败!"),
    SCENE_MANAGE_UPDATE_LIFE_CYCLE_ERROR("cloud-001-s0303", "场景管理之更新生命周期失败!"),

    SCENE_MANAGE_GET_ERROR("cloud-001-s0400", "获取场景失败"),

    SCENE_MANAGE_UPLOAD_FILE_ERROR("cloud-001-s0500", "上传文件失败"),
    SCENE_CSV_FILE_SPLIT_ERROR("cloud-001-s0501", "文件拆分失败"),
    SCENE_CSV_POD_NOT_FETCH("cloud-001-s0502", "pod数量大于分片数量"),
    SCENE_MANAGE_FILE_COPY_ERROR("cloud-001-s0503", "上传文件复制失败"),
    BIGFILE_UPLOAD_VERIFY_ERROR("cloud-001-u0504", "大文件上传校验失败"),
    BIGFILE_UPLOAD_ERROR("cloud-001-u0505", "大文件上传失败"),
    EMPTY_DIRECTORY_ERROR("cloud-001-u0506", "场景文件夹下未找到对应的数据文件"),

    TOO_MUCH_FILE_ERROR("cloud-001-u0507", "场景文件夹下找到多个数据文件"),

    SCENE_JMX_FILE_CHECK_ERROR("cloud-001-s0508", "脚本文件校验失败"),

    ENGINE_PLUGIN_PARAM_VERIFY_ERROR("cloud-001-s0600", "引擎插件参数校验失败"),
    SCENE_MANAGE_ALLOCATION_ERROR("cloud-001-s0601", "压测场景分配异常"),

    SCRIPT_VERITY_ERROR("cloud-001-s0701", "脚本校验异常"),

    SCRIPT_ANALYZE_PARAMS_ERROR("cloud-001-s0800", "脚本解析参数错误"),
    SCRIPT_FILE_NOT_EXISTS("cloud-001-s0801", "脚本文件不存在"),
    SCRIPT_ANALYZE_FAILED("cloud-001-s0802", "脚本解析失败"),

    /**
     * 压测任务、压测报告模块 cloud-002-
     * 0200+启动   0300+查询  0400+停止 0500+压测中
     */
    TASK_START_VERIFY_ERROR("cloud-002-s0200", "任务启动校验异常"),
    SCENE_START_ERROR_FILE_SLICING("cloud-002-s0201", "任务启动失败，文件拆分中"),
    TASK_START_ERROR_CHECK_POD("cloud-002-s0202", "任务启动校验pod异常"),
    INSPECT_TASK_START_ERROR("cloud-002-s0203", "巡检任务启动失败"),
    TASK_START_BUILD_SAL("cloud-002-s0204", "任务启动构建sla失败"),

    REPORT_GET_ERROR("cloud-002-s0300", "报告查询异常"),

    TASK_STOP_VERIFY_ERROR("cloud-002-s0400", "任务停止校验异常"),
    TASK_STOP_SAL_FINISH_ERROR("cloud-002-s0401", "任务停止sla异常"),
    TASK_STOP_DELETE_TASK_ERROR("cloud-002-s0402", "任务停止删除异常"),
    TASK_STOP_DEAL_REPORT_ERROR("cloud-002-s0403", "任务停止处理报告信息异常"),
    INSPECT_TASK_STOP_ERROR("cloud-002-s0404", "巡检任务停止失败"),

    TASK_RUNNING_GET_RUNNING_JOB_KEY("cloud-002-s0500", "获取运行中的job失败"),
    TASK_RUNNING_LOG_PUSH_ERROR("cloud-002-s0501", "推送日志失败"),
    TASK_RUNNING_PARAM_VERIFY_ERROR("cloud-002-s0502", "参数校验失败"),
    TASK_RUNNING_RECEIVE_PT_DATA_ERROR("cloud-002-s0503", "接收压测数据失败"),
    TASK_RUNNING_SAL_METRICS_DATA_ERROR("cloud-002-s0504", "任务运行采集数据失败"),
    TASK_RUNNING_DEAL_PT_DATA_ERROR("cloud-002-s0505", "处理压测数据异常"),

    SCHEDULE_RECORD_GET_ERROR("cloud-002-s0600", "调度记录查询异常"),
    REPORT_ALLOCATION_ERROR("cloud-002-s0601", "报告分配异常"),

    /**
     * k8s调度模块 cloud-003-
     */
    K8S_NODE_EMPTY("cloud-003-s0200", "未找到k8s节点"),
    SCHEDULE_START_ERROR("cloud-003-s0201", "启动调度失败"),
    SAE_START_APPLICATION_ERROR("cloud-003-s0202", "sae启动应用失败"),
    SAE_INVOKE_ERROR("cloud-003-t0203", "sae调用失败"),
    K8S_INIT_ERROR("cloud-003-s0204", "k8s初始化失败"),

    /**
     * 用户鉴权相关模块 cloud-004-
     */
    LICENSE_PARAM_VERIFY_ERROR("cloud-004-s0200", "license 参数校验失败"),
    TRO_USER_QUERY_ERROR("cloud-004-u0201", "用户查询异常"),
    TRO_USER_BE_FORBIDDEN("cloud-004-u0202", "用户被禁用"),
    TRO_USER_PARAM_VERIFY_ERROR("cloud-004-u0203", "用户参数校验异常"),
    TRO_USER_ADD_ERROR("cloud-004-u0204", "新增用户异常"),
    TRO_USER_UPDATE_ERROR("cloud-004-u0205", "更新用户异常"),

    AUTHORITY_INTERCEPTOR_DEAL_ERROR("cloud-004-s0300", "权限处理异常"),

    /**
     * 流量管理模块 cloud-005-
     */
    FLOW_ACCOUNT_RECHARGE_VERIFY_ERROR("cloud-005-s0200", "流量充值校验失败"),
    ACCOUNT_PARAM_VERIFY_ERROR("cloud-005-s0201", "账户参数异常"),
    ACCOUNT_QUERY_ERROR("cloud-005-s0202", "账户查询异常"),
    ACCOUNT_PLATFORM_QUERY_ERROR("cloud-005-s0203", "账户平台查询异常"),
    ACCOUNT_DELETE_ERROR("cloud-005-s0204", "账户删除异常"),

    /**
     * 中间件对比模块 cloud-006-
     */
    MIDDLEWARE_JAR_IMPORT_ERROR("cloud-006-s0200", "中间件jar包导入错误"),
    MIDDLEWARE_JAR_COMPARE_ERROR("cloud-006-s0201", "中间件jar包比对错误"),

    MIDDLEWARE_JAR_GET_LOCK_ERROR("cloud-006-s0300", "获取锁失败"),

    /**
     * cloud sdk插件加载 cloud-007-
     */
    CLOUD_LOAD_JAR_ERROR("cloud-007-s0200", "加载jar包失败"),
    CLOUD_LOAD_SDK_ERROR("cloud-007-s0201", "加载sdk包失败"),

    /**
     * 压测机器实例管理模块 cloud-008-
     */
    PRECESS_INSTANCE_PARAM_VERIFY_ERROR("cloud-008-s0200", "创建压测实例校验错误"),
    PRECESS_INSTANCE_NO_SDK("cloud-008-s0201", "压测实例没有对应的sdk"),

    MACHINE_SPEC_VERIFY_ERROR("cloud-008-s0300", "压测机空间校验失败"),
    MACHINE_TASK_VERIFY_ERROR("cloud-008-s0301", "压测机任务校验失败"),
    MACHINE_TASK_LOG_VERIFY_ERROR("cloud-008-s0302", "压测机任务日志校验失败"),
    PRESSURE_MACHINE_VERIFY_ERROR("cloud-008-s0303", "压测机校验失败"),

    /**
     * 通用模块 cloud-009-  0300+文件处理异常
     */
    ENCRYPTION_BCRYPT_ERROR("cloud-009-s0200", "BCRYPT处理失败"),
    UNSUPPORTED_ENCODING_ERROR("cloud-009-s0201", "不支持的编码"),
    SSL_SOCKET_ERROR("cloud-009-s0202", "SSL socket 创建失败"),
    ENCRYPTION_AES_ERROR("cloud-009-s0203", "AES处理失败"),
    DICTIONARY_DATA_PARSE_ERROR("cloud-009-s0204", "字典数据解析异常"),
    XML_PARSE_ERROR("cloud-009-s0205", "xml解析异常"),
    UNKNOWN_HOST_ERROR("cloud-009-s0206", "未知的端口异常"),
    SOCKET_CONNECT_ERROR("cloud-009-s0207", "socket链接异常"),
    LINUX_CMD_EXECUTE_ERROR("cloud-009-s0208", "LINUX命令执行异常"),
    REDIS_CMD_EXECUTE_ERROR("cloud-009-s0209", "REDIS命令执行异常"),
    HTTP_CMD_EXECUTE_ERROR("cloud-009-s0210", "HTTP命令执行异常"),
    DATE_PARSE_ERROR("cloud-009-s0211", "时间解析异常"),
    JSON_PARSE_ERROR("cloud-009-s0212", "json处理失败"),
    PLUGIN_NOT_FIND("cloud-009-s0213", "没有找到插件"),

    FILE_COPY_ERROR("cloud-009-s0300", "复制文件失败"),
    FILE_READ_ERROR("cloud-009-s0301", "读取文件失败"),
    FILE_CLOSE_ERROR("cloud-009-s0302", "文件关闭失败"),
    FILE_CMD_EXECUTE_ERROR("cloud-009-s0303", "文件命令执行异常"),
    FILE_NOT_FOUND_ERROR("cloud-009-s0304", "文件找不到异常"),
    FILE_ZIP_ERROR("cloud-009-s0305", "文件zip压缩失败"),
    FILE_TAR_ERROR("cloud-009-s0306", "文件tar处理失败"),

    /**
     * 云平台管理 cloud-010-
     */
    CLOUD_PLATFORM_VERIFY_ERROR("cloud-010-s0200", "云平台管理校验失败"),
    CLOUD_PLATFORM_UPDATE_ERROR("cloud-010-s0201", "云平台管理更新失败"),
    CLOUD_PLATFORM_CONFIRM_ERROR("cloud-010-s0202", "云平台管理确认失败"),
    CLOUD_PLATFORM_ADD_ERROR("cloud-010-s0203", "云平台管理新增失败"),
    CLOUD_PLATFORM_QUERY_ERROR("cloud-010-s0204", "云平台管理查询失败"),
    CLOUD_PLATFORM_DELETE_ERROR("cloud-010-s0205", "云平台管理删除失败"),

    /**
     * 数据签名
     */
    DATA_SIGN_ERROR("cloud-010-s0205-s0403","数据签名异常"),
    ;

    private final String errorCode;

    private final String defaultValue;

}
