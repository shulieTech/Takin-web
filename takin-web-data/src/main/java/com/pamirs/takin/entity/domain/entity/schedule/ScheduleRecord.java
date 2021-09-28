package com.pamirs.takin.entity.domain.entity.schedule;

import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class ScheduleRecord {
    private Long id;

    private Long sceneId;

    private Integer podNum;

    private String podClass;

    private Integer status;

    private Integer cpuCoreNum;

    private BigDecimal memorySize;

    private Date createTime;

    /**
     * 租户id
     */
    @TableField(value = "customer_id",fill = FieldFill.INSERT)
    private long tenantId;
    /**
     * 用户id
     */
    private long userId;
    /**
     * 环境编码
     */
    @TableField(value = "env_code",fill = FieldFill.INSERT)
    private String envCode;
}
