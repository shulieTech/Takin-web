package io.shulie.takin.web.data.model.mysql;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 压测任务业务流程配置
 */
@Data
@TableName(value = "t_pessure_test_task_process_config")
public class PessureTestTaskProcessConfigEntity {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 压测任务名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 压测任务类型
     */
    @TableField(value = "type")
    private Boolean type;

    /**
     * 压测引擎ID
     */
    @TableField(value = "engine_id")
    private Long engineId;

    /**
     * 压测引擎名称
     */
    @TableField(value = "engine_name")
    private String engineName;

    /**
     * 业务流程ID
     */
    @TableField(value = "process_id")
    private Long processId;

    /**
     * 业务流程名称
     */
    @TableField(value = "process_name")
    private String processName;

    /**
     * PTS场景ID
     */
    @TableField(value = "scenario_id")
    private String scenarioId;

    /**
     * 0-为启动、1-执行中、2-压测结束
     */
    @TableField(value = "status")
    private Boolean status;

    /**
     * 压测最新时间
     */
    @TableField(value = "pessure_time")
    private LocalDateTime pessureTime;

    /**
     * 是否删除 0-未删除、1-已删除
     */
    @TableField(value = "is_deleted")
    private Boolean isDeleted;

    /**
     * 创建时间
     */
    @TableField(value = "create_time")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "update_time")
    private LocalDateTime updateTime;

    /**
     * 租户id
     */
    @TableField(value = "customer_id",fill = FieldFill.INSERT)
    private Long tenantId;
    /**
     * 用户id
     */
    private Long userId;
    /**
     * 环境编码
     */
    @TableField(value = "env_code",fill = FieldFill.INSERT)
    private String envCode;
}
