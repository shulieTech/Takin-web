package io.shulie.takin.web.data.model.mysql;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "JOB_STATUS_TRACE_LOG")
public class JobStatusTraceLogEntity {
    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    @TableField(value = "job_name")
    private String jobName;

    @TableField(value = "original_task_id")
    private String originalTaskId;

    @TableField(value = "task_id")
    private String taskId;

    @TableField(value = "slave_id")
    private String slaveId;

    @TableField(value = "source")
    private String source;

    @TableField(value = "execution_type")
    private String executionType;

    @TableField(value = "sharding_item")
    private String shardingItem;

    @TableField(value = "state")
    private String state;

    @TableField(value = "message")
    private String message;

    @TableField(value = "creation_time")
    private LocalDateTime creationTime;
}
