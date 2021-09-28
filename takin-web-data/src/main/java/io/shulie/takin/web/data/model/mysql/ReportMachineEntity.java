package io.shulie.takin.web.data.model.mysql;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName(value = "t_report_machine")
public class ReportMachineEntity {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @TableField(value = "report_id")
    private Long reportId;

    /**
     * 应用名称
     */
    @TableField(value = "application_name")
    private String applicationName;

    /**
     * 机器ip
     */
    @TableField(value = "machine_ip")
    private String machineIp;

    /**
     * 机器ip
     */
    @TableField(value = "agent_id")
    private String agentId;

    /**
     * 机器基本信息
     */
    @TableField(value = "machine_base_config")
    private String machineBaseConfig;

    /**
     * 机器tps对应指标信息
     */
    @TableField(value = "machine_tps_target_config")
    private String machineTpsTargetConfig;

    /**
     * 风险计算值
     */
    @TableField(value = "risk_value")
    private BigDecimal riskValue;

    /**
     * 是否风险机器(0-否，1-是)
     */
    @TableField(value = "risk_flag")
    private Integer riskFlag;

    /**
     * 风险提示内容
     */
    @TableField(value = "risk_content")
    private String riskContent;

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
