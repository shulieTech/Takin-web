package io.shulie.takin.web.common.exception;

import io.shulie.takin.parent.exception.entity.ExceptionReadable;
import lombok.AllArgsConstructor;

/**
 * @author caijianying
 */
@AllArgsConstructor
public enum TakinWebExceptionEnum implements ExceptionReadable {
    ERROR_COMMON("0000-U000", "通用错误"),

    /**
     * 压测场景
     */
    SCENE_VALIDATE_ERROR("0100-" + ErrorConstant.VALIDATE_ERROR, "压测场景参数校验"),
    SCENE_ADD_ERROR("0100-" + ErrorConstant.ADD_ERROR, "新增压测场景异常"),
    SCENE_UPDATE_ERROR("0100-" + ErrorConstant.UPDATE_ERROR, "编辑压测场景异常"),
    SCENE_DELETE_ERROR("0100-" + ErrorConstant.DELETE_ERROR, "删除压测场景异常"),
    SCENE_THIRD_PARTY_ERROR("0100-" + ErrorConstant.THIRD_PARTY_ERROR, "压测场景第三方返回异常"),
    SCENE_START_STATUS_ERROR("0101-" + ErrorConstant.STATUS_ERROR, "压测场景启动模块状态异常"),
    SCENE_START_VALIDATE_ERROR("0101-" + ErrorConstant.VALIDATE_ERROR, "压测场景启动模块参数校验"),
    SCENE_REPORT_THIRD_PARTY_ERROR("0102-" + ErrorConstant.THIRD_PARTY_ERROR, "压测场景报告第三方返回异常"),
    SCENE_REPORT_VALIDATE_ERROR("0102-" + ErrorConstant.VALIDATE_ERROR, "压测报告数据校验异常"),
    SCENE_REPORT_LINK_DETAIL_THIRD_PARTY_ERROR("0103-" + ErrorConstant.THIRD_PARTY_ERROR, "压测场景报告流量明细第三方返回异常"),

    /**
     * 脚本
     */
    SCRIPT_VALIDATE_ERROR("0200-" + ErrorConstant.VALIDATE_ERROR, "脚本参数校验"),
    SCRIPT_ADD_ERROR("0200-" + ErrorConstant.ADD_ERROR, "创建脚本异常"),
    SCRIPT_UPDATE_ERROR("0200-" + ErrorConstant.UPDATE_ERROR, "编辑脚本异常"),
    SCRIPT_DELETE_ERROR("0200-" + ErrorConstant.DELETE_ERROR, "删除脚本异常"),
    SCRIPT_THIRD_PARTY_ERROR("0200-" + ErrorConstant.THIRD_PARTY_ERROR, "脚本第三方返回异常"),
    SCRIPT_DEBUG_REPEAT_ERROR("0201-" + ErrorConstant.OPT_REPEAT_ERROR, "脚本调试重复操作异常"),
    SCRIPT_DEBUG_VALIDATE_ERROR("0201-" + ErrorConstant.VALIDATE_ERROR, "脚本调试校验异常"),
    SCRIPT_DEBUG_DATA_PROCESS_ERROR("0201-" + ErrorConstant.DATA_PROCESS_ERROR, "脚本调试数据处理异常"),
    OPS_SCRIPT_VALIDATE_ERROR("0202-" + ErrorConstant.VALIDATE_ERROR, "运维脚本参数校验"),
    OPS_SCRIPT_FILE_VALIDATE_ERROR("0202-" + ErrorConstant.VALIDATE_ERROR, "脚本文件参数校验"),
    OPS_SCRIPT_FILE_ADD_ERROR("0202-" + ErrorConstant.ADD_ERROR, "创建脚本文件异常"),
    OPS_SCRIPT_FILE_UPDATE_ERROR("0202-" + ErrorConstant.UPDATE_ERROR, "编辑脚本文件异常"),
    OPS_SCRIPT_FILE_DELETE_ERROR("0202-" + ErrorConstant.DELETE_ERROR, "删除脚本文件异常"),
    OPS_SCRIPT_FILE_UPLOAD_ERROR("0202-" + ErrorConstant.FILE_UPLOAD_ERROR, "上传脚本文件异常"),

    /**
     * 应用管理
     */
    APPLICATION_MANAGE_VALIDATE_ERROR("0300-" + ErrorConstant.VALIDATE_ERROR, "应用数据校验"),
    APPLICATION_MANAGE_NO_DATA_PERMISSION_ERROR("0300-" + ErrorConstant.NO_DATA_PERMISSION, "应用无数据权限"),
    APPLICATION_MANAGE_THIRD_PARTY_ERROR("0300-" + ErrorConstant.THIRD_PARTY_ERROR, "应用第三方返回异常"),
    APPLICATION_JOB_VALIDATE_ERROR("0301-" + ErrorConstant.VALIDATE_ERROR, "应用 job 校验数据校验"),
    APPLICATION_WHITELIST_VALIDATE_ERROR("0302-" + ErrorConstant.VALIDATE_ERROR, "应用白名单校验异常"),
    APPLICATION_CONFIG_FILE_VALIDATE_ERROR("0303-" + ErrorConstant.FILE_VALIDATE_ERROR, "应用配置文件校验异常"),
    APPLICATION_CONFIG_FILE_IMPORT_ERROR("0303-" + ErrorConstant.FILE_IMPORT_ERROR, "应用配置文件导入异常"),
    APPLICATION_CONFIG_FILE_CREATE_ERROR("0303-" + ErrorConstant.FILE_CREATE_ERROR, "应用配置文件创建异常"),
    APPLICATION_SHADOW_THIRD_PARTY_ERROR("0304-" + ErrorConstant.THIRD_PARTY_ERROR, "应用影子消费者第三方返回异常"),
    APPLICATION_ENTRANCE_THIRD_PARTY_ERROR("0305-" + ErrorConstant.THIRD_PARTY_ERROR, "应用入口链路第三方返回异常"),
    APPLICATION_UNSTALL_AGENT_ERROR("0306-" + ErrorConstant.THIRD_PARTY_ERROR, "卸载应用agent异常"),
    APPLICATION_RESUME_AGENT_ERROR("0307-" + ErrorConstant.THIRD_PARTY_ERROR, "恢复应用agent异常"),
    APPLICATION_TRACE_LOG_AGENT_ERROR("0307-" + ErrorConstant.THIRD_PARTY_ERROR, "trace日志获取异常"),
    APPLICATION_QUERY_TEMP_ACTIVITY_METRICS_STEP1_ERROR("0308-" + ErrorConstant.THIRD_PARTY_ERROR, "查询临时业务活动指标step1"),
    APPLICATION_QUERY_TEMP_ACTIVITY_METRICS_STEP2_ERROR("0309-" + ErrorConstant.THIRD_PARTY_ERROR, "查询临时业务活动指标step2"),

    /**
     * agent
     */
    AGENT_REGISTER_API("0501-" + ErrorConstant.VALIDATE_ERROR, "Agent 上报 api, 数据校验"),
    AGENT_REGISTER_API_ERROR("0501-" + ErrorConstant.DATA_PROCESS_ERROR, "Agent 上报 api, 数据处理异常"),
    AGENT_PUSH_APPLICATION_VALIDATE_ERROR("0502-" + ErrorConstant.VALIDATE_ERROR, "Agent 上报应用, 数据校验"),
    AGENT_PUSH_APPLICATION_ADD_ERROR("0502-" + ErrorConstant.ADD_ERROR, "Agent 上报应用, 新增错误"),
    AGENT_PUSH_APPLICATION_STATUS_VALIDATE_ERROR("0506-" + ErrorConstant.VALIDATE_ERROR, "Agent 上报应用状态, 数据校验"),
    AGENT_UPDATE_SHADOW_JOB_UPDATE_ERROR("0503-" + ErrorConstant.UPDATE_ERROR, "Agent 影子配置修改错误"),
    AGENT_UPDATE_SHADOW_JOB_VALIDATE_ERROR("0503-" + ErrorConstant.VALIDATE_ERROR, "Agent 影子配置校验错误"),
    AGENT_UPLOAD_CONFIG_MSG_ERROR("0503-" + ErrorConstant.VALIDATE_ERROR, "Agent 上传 配置数据 相关, 校验错误"),

    /**
     * agent 更新 agent 版本, 校验错误
     */
    AGENT_UPDATE_AGENT_VERSION_VALIDATE_ERROR("0504-" + ErrorConstant.VALIDATE_ERROR, "Agent 更新 agent 版本, 校验错误"),
    AGENT_UPDATE_AGENT_VERSION_UPDATE_ERROR("0504-" + ErrorConstant.UPDATE_ERROR, "Agent 更新 agent 版本, 更新错误"),
    AGENT_UPLOAD_TRACE_VALIDATE_ERROR("0505-" + ErrorConstant.VALIDATE_ERROR, "Agent 上传 trace 相关, 校验错误"),

    /**
     * 链路梳理
     */
    LINK_VALIDATE_ERROR("0400-" + ErrorConstant.VALIDATE_ERROR, "链路梳理校验异常"),
    LINK_ADD_ERROR("0400-" + ErrorConstant.ADD_ERROR, "链路梳理创建异常"),
    LINK_QUERY_ERROR("0400-" + ErrorConstant.QUERY_ERROR, "链路梳理查询异常"),
    LINK_UPDATE_ERROR("0400-" + ErrorConstant.UPDATE_ERROR, "链路梳理修改异常"),
    LINK_THIRD_PARTY_ERROR("0400-" + ErrorConstant.THIRD_PARTY_ERROR, "链路梳理第三方返回异常"),

    /**
     * 调试工具
     */
    FAST_DEBUG_VALIDATE_ERROR("0700-" + ErrorConstant.VALIDATE_ERROR, "调试工具校验异常"),
    FAST_DEBUG_STATUS_ERROR("0700-" + ErrorConstant.STATUS_ERROR, "快速调试状态异常"),
    FAST_DEBUG_FILE_COMPARE_ERROR("0700-" + ErrorConstant.FILE_COMPARE_ERROR, "快速调试文件比对异常"),
    FAST_DEBUG_QUERY_NULL_ERROR("0700-" + ErrorConstant.QUERY_NULL_ERROR, "快速调试查询异常"),
    FAST_DEBUG_CALL_STACK_ERROR("0700-" + ErrorConstant.DATA_PROCESS_ERROR, "快速调试查询异常"),
    FAST_DEBUG_GET_APP_LOG_ERROR("0700-" + ErrorConstant.QUERY_ERROR, "快速调试应用日志查询异常"),
    FAST_DEBUG_GET_AGENT_LOG_ERROR("0700-" + ErrorConstant.QUERY_ERROR, "快速调试agent日志查询异常"),
    FAST_DEBUG_GET_AGENT_LOG_NAME_ERROR("0700-" + ErrorConstant.QUERY_ERROR, "快速调试agent日志文件名称查询异常"),
    /**
     * 巡检场景
     */
    PATROL_SCENE_VALIDATE_ERROR("0900-" + ErrorConstant.VALIDATE_ERROR, "巡检场景参数校验"),
    PATROL_SCENE_ADD_ERROR("0900-" + ErrorConstant.ADD_ERROR, "新增巡检场景异常"),
    PATROL_SCENE_UPDATE_ERROR("0900-" + ErrorConstant.UPDATE_ERROR, "编辑巡检场景异常"),
    PATROL_SCENE_DELETE_ERROR("0900-" + ErrorConstant.DELETE_ERROR, "删除巡检场景异常"),
    PATROL_SCENE_THIRD_PARTY_ERROR("0900-" + ErrorConstant.THIRD_PARTY_ERROR, "巡检场景第三方返回异常"),
    PATROL_SCENE_START_STATUS_ERROR("0900-" + ErrorConstant.STATUS_ERROR, "巡检场景启动模块状态异常"),
    PATROL_SCENE_STOP_STATUS_ERROR("0900-" + ErrorConstant.STATUS_ERROR, "巡检场景停止模块状态异常"),
    PATROL_SCENE_START_VALIDATE_ERROR("0900-" + ErrorConstant.STATUS_ERROR, "巡检场景启动模块参数校验"),
    PATROL_SCENE_QUERY_ERROR("0900-" + ErrorConstant.QUERY_NULL_ERROR, "巡检场景查询异常"),

    /**
     * 看板管理
     */
    PATROL_BOARD_ADD_ERROR("1000-" + ErrorConstant.ADD_ERROR, "新增巡检看板异常"),
    PATROL_BOARD_UPDATE_ERROR("1000-" + ErrorConstant.UPDATE_ERROR, "编辑巡检看板异常"),
    PATROL_BOARD_DELETE_ERROR("1000-" + ErrorConstant.DELETE_ERROR, "删除巡检看板异常"),

    /**
     * 技术节点
     */
    PATROL_TECH_NODE_ADD_ERROR("1100-" + ErrorConstant.ADD_ERROR, "新增技术节点异常"),

    /**
     * 巡检断言
     */
    PATROL_ASSERT_ADD_ERROR("1200-" + ErrorConstant.ADD_ERROR, "新增断言异常"),

    /**
     * 巡检异常管理
     */
    PATROL_EXCEPTION_ADD_ERROR("1300-" + ErrorConstant.ADD_ERROR, "新增异常数据失败"),
    PATROL_EXCEPTION_QUERY_ERROR("1300-" + ErrorConstant.QUERY_NULL_ERROR, "查询巡检异常失败"),

    /**
     * 影子配置
     */
    SHADOW_CONFIG_CREATE_ERROR("1400-" + ErrorConstant.VALIDATE_ERROR, "新增影子配置异常"),
    ;

    private final String errorCode;

    private final String defaultValue;

    @Override
    public String getErrorCode() {
        return ErrorConstant.ERROR_PREFIX + this.errorCode;
    }

    @Override
    public String getDefaultValue() {
        return this.defaultValue;
    }

}
