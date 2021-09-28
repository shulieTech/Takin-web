package io.shulie.takin.web.data.model.mysql;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 性能基础数据表
 *
 * @author qianshui
 * @date 2020/11/4 下午1:53
 */
@Data
@TableName(value = "t_performance_base_data")
public class PerformanceBaseDataEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

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

    @TableField(value = "agent_id")
    private String agentId;

    @TableField(value = "app_name")
    private String appName;

    @TableField(value = "app_ip")
    private String appIp;

    @TableField(value = "process_id")
    private Long processId;

    @TableField(value = "process_name")
    private String processName;

    @TableField(value = "timestamp")
    private String timestamp;

    @TableField(value = "total_memory")
    private Double totalMemory;

    @TableField(value = "perm_memory")
    private Double permMemory;

    @TableField(value = "young_memory")
    private Double youngMemory;

    @TableField(value = "old_memory")
    private Double oldMemory;

    @TableField(value = "young_gc_count")
    private Integer youngGcCount;

    @TableField(value = "full_gc_count")
    private Integer fullGcCount;

    @TableField(value = "young_gc_cost")
    private Long youngGcCost;

    @TableField(value = "full_gc_cost")
    private Long fullGcCost;

}
