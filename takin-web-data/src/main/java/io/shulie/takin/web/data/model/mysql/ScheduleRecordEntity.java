package io.shulie.takin.web.data.model.mysql;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "t_schedule_record")
public class ScheduleRecordEntity {
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
