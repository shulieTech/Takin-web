package com.pamirs.takin.entity.domain.entity.report;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_report_interface_metrics")
public class ReportInterfaceMetricsEntity extends ReportActivityEntity {
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
     * 时间窗口
     */
    @TableField(value = "time_window")
    private Date timeWindow;
    /**
     * 采样后的请求数
     */
    @TableField(value = "count_after_simp")
    private Long countAfterSimp;
}
