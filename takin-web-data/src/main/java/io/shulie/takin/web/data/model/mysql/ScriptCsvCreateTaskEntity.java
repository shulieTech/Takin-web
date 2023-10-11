package io.shulie.takin.web.data.model.mysql;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * csv生成任务表
 */
@Data
@TableName(value = "t_script_csv_create_task")
public class ScriptCsvCreateTaskEntity {
    /**
     * 主键id
     */
    @TableId(value = "id", type = IdType.INPUT)
    private Long id;

    /**
     * 任务开始时间
     */
    @TableField(value = "task_start_time")
    private LocalDateTime taskStartTime;

    /**
     * 任务完成时间
     */
    @TableField(value = "task_end_time")
    private LocalDateTime taskEndTime;

    /**
     * 模板变量(存储起止时间，应用名，接口，接口类型)
     */
    @TableField(value = "template_variable")
    private String templateVariable;

    /**
     * 模板内容，存对应变量生成的模板内容
     */
    @TableField(value = "template_content")
    private String templateContent;

    /**
     * 脚本csv变量的jsonPath映射
     */
    @TableField(value = "script_csv_variable_json_path")
    private String scriptCsvVariableJsonPath;

    /**
     * 当前生成进度
     */
    @TableField(value = "current_create_schedule")
    private String currentCreateSchedule;

    /**
     * 生成状态(0：生成中，1：排队中，2：已生成，3已取消)
     */
    @TableField(value = "create_status")
    private Integer createStatus;

    /**
     * 备注
     */
    @TableField(value = "remark")
    private String remark;

    /**
     * 别名
     */
    @TableField(value = "alias_name")
    private String aliasName;

    /**
     * 部门ID（模块ID）
     */
    @TableField(value = "dept_id")
    private Long deptId;

    /**
     * 业务流程ID
     */
    @TableField(value = "business_flow_id")
    private Long businessFlowId;

    /**
     * 业务活动ID
     */
    @TableField(value = "link_id")
    private Long linkId;

    /**
     * csv组件表Id
     */
    @TableField(value = "script_csv_data_set_id")
    private Long scriptCsvDataSetId;

    /**
     * 插入时间
     */
    @TableField(value = "CREATE_TIME")
    private LocalDateTime createTime;

    /**
     * 变更时间
     */
    @TableField(value = "UPDATE_TIME")
    private LocalDateTime updateTime;

    @TableField(value = "ENV_CODE")
    private String envCode;
    @TableField(value = "TENANT_ID")
    private Long tenantId;
}