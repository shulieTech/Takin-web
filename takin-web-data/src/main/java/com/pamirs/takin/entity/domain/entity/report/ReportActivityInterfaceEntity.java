package com.pamirs.takin.entity.domain.entity.report;

import java.math.BigDecimal;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_report_activity_interface")
public class ReportActivityInterfaceEntity extends ReportActivityEntity {
    /**
     * 入口应用名称
     */
    @TableField(value = "entrance_app_name")
    private String entranceAppName;
    /**
     * 入口接口名称
     */
    @TableField(value = "entrance_service_name")
    private String entranceServiceName;
    /**
     * 入口方法名称
     */
    @TableField(value = "entrance_method_name")
    private String entranceMethodName;
    /**
     * 入口rpcType
     */
    @TableField(value = "entrance_rpc_type")
    private String entranceRpcType;
    /**
     * 耗时占比
     */
    @TableField(value = "cost_percent")
    private BigDecimal costPercent;
    /**
     * 业务活动平均耗时
     */
    @TableField(value = "service_avg_cost")
    private BigDecimal serviceAvgCost;
}
