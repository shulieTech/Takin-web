package com.pamirs.takin.entity.domain.entity.report;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("t_report_activity")
public class ReportActivityEntity implements Serializable {
    /**
     * 报告Id
     */
    @TableField(value = "report_id")
    private Long reportId;
    /**
     * 应用名称
     */
    @TableField(value = "app_name")
    private String appName;
    /**
     * 服务名称
     */
    @TableField(value = "service_name")
    private String serviceName;
    /**
     * 方法名称
     */
    @TableField(value = "method_name")
    private String methodName;
    /**
     * rpcType
     */
    @TableField(value = "rpc_type")
    private String rpcType;
    /**
     * 最小耗时
     */
    @TableField(value = "min_cost")
    private Long minCost;
    /**
     * 最大耗时
     */
    @TableField(value = "max_cost")
    private Long maxCost;
    /**
     * 总耗时
     */
    @TableField(value = "sum_cost")
    private Long sumCost;
    /**
     * 请求数
     */
    @TableField(value = "req_cnt")
    private Long reqCnt;
    /**
     * 平均自耗时
     */
    @TableField(value = "avg_cost")
    private BigDecimal avgCost;
    /**
     * 创建时间
     */
    @TableField(value = "gmt_create")
    private String gmtCreate;
    /**
     * 同步时间
     */
    @TableField(value = "sync_time")
    private Date syncTime;
}
