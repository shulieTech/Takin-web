package com.pamirs.takin.entity.domain.entity.report;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

/**
 * @author shiyajian
 * create: 2020-08-21
 */
@Data
public class TakinTraceEntry {

    private Long id;

    private String appName;

    private String entry;

    private String method;

    private String status;

    private Long startTime;

    private Long endTime;

    private Long processTime;

    private String traceId;
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
