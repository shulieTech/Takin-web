package com.pamirs.takin.entity.domain.entity.report;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class ReportSummary {

    private Long id;

    private Long reportId;

    private Integer bottleneckInterfaceCount;

    private Integer riskMachineCount;

    private Integer businessActivityCount;

    private Integer unachieveBusinessActivityCount;

    private Integer applicationCount;

    private Integer machineCount;

    private Integer warnCount;
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