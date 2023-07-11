package io.shulie.takin.web.data.model.mysql;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.shulie.takin.web.data.model.mysql.base.TenantBaseEntity;
import lombok.Data;

/**
 * 性能基础数据表
 *
 * @author qianshui
 * @date 2020/11/4 下午1:53
 */
@Data
@TableName(value = "t_performance_base_data")
public class PerformanceBaseDataEntity extends TenantBaseEntity {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

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
