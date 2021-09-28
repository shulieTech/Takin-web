package com.pamirs.takin.entity.domain.entity.report;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class ReportMachine {

    private Long id;

    private Long reportId;

    private String applicationName;

    private String machineIp;

    private String machineBaseConfig;

    private BigDecimal riskValue;

    private Integer riskFlag;

    private String riskContent;

    private String machineTpsTargetConfig;

    private String agentId;
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
