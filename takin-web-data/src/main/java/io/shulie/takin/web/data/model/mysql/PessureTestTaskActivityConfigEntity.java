package io.shulie.takin.web.data.model.mysql;

import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 压测任务业务活动配置
 */
@Data
@TableName(value = "t_pessure_test_task_activity_config")
public class PessureTestTaskActivityConfigEntity {
    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 压测任务流程ID
     */
    @TableField(value = "pessure_process_id")
    private Long pessureProcessId;

    /**
     * 业务活动名称
     */
    @TableField(value = "name")
    private String name;

    /**
     * 业务活动类型
     */
    @TableField(value = "type_name")
    private String typeName;

    /**
     * 压测平台场景ID
     */
    @TableField(value = "scenario_id")
    private String scenarioId;

    /**
     * 业务域
     */
    @TableField(value = "domain")
    private String domain;

    /**
     * 目标TPS
     */
    @TableField(value = "target_tps")
    private Integer targetTps;

    /**
     * 目标RT
     */
    @TableField(value = "target_rt")
    private String targetRt;

    /**
     * 目标请求成功率
     */
    @TableField(value = "request_success_rate")
    private Integer requestSuccessRate;

    /**
     * 是否删除 0-未删除、1-已删除
     */
    @TableField(value = "is_deleted")
    private Boolean isDeleted;

    /**
     * 创建时间
     */
    @TableField(value = "CREATE_TIME")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @TableField(value = "UPDATE_TIME")
    private LocalDateTime updateTime;
}
