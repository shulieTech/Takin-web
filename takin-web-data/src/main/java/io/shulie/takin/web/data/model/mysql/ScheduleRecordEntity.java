package io.shulie.takin.web.data.model.mysql;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.shulie.takin.web.data.model.mysql.base.TenantBaseEntity;
import lombok.Data;

@Data
@TableName(value = "t_schedule_record")
public class ScheduleRecordEntity extends TenantBaseEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 场景ID
     */
    @TableField(value = "scene_id")
    private Long sceneId;

    /**
     * pod数量
     */
    @TableField(value = "pod_num")
    private Integer podNum;

    /**
     * pod种类
     */
    @TableField(value = "pod_class")
    private String podClass;

    /**
     * 调度结果 0-失败 1-处理中，2-处理失败
     */
    @TableField(value = "status")
    private Integer status;

    /**
     * CPU核数
     */
    @TableField(value = "cpu_core_num")
    private BigDecimal cpuCoreNum;

    /**
     * 内存G
     */
    @TableField(value = "memory_size")
    private BigDecimal memorySize;

    /**
     * 调度时间
     */
    @TableField(value = "create_time")
    private LocalDateTime createTime;
}
