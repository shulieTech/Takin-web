package io.shulie.takin.web.data.model.mysql;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "JOB_EXECUTION_LOG")
public class JobExecutionLogEntity {
    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    @TableField(value = "job_name")
    private String jobName;

    @TableField(value = "task_id")
    private String taskId;

    @TableField(value = "hostname")
    private String hostname;

    @TableField(value = "ip")
    private String ip;

    @TableField(value = "sharding_item")
    private Integer shardingItem;

    @TableField(value = "execution_source")
    private String executionSource;

    @TableField(value = "failure_cause")
    private String failureCause;

    @TableField(value = "is_success")
    private Integer isSuccess;

    @TableField(value = "start_time")
    private LocalDateTime startTime;

    @TableField(value = "complete_time")
    private LocalDateTime completeTime;
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
