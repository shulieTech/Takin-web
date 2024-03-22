package io.shulie.takin.web.biz.pojo.output.scene;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author zhangz
 * Created on 2024/3/14 17:58
 * Email: zz052831@163.com
 */
@Data
public class EntryTraceAvgCostOutput {
    private Long activityId;
    private String traceId;
    private String appName;
    private String serviceName;
    private String methodName;
    private String middlewareName;
    private String rpcId;
    private Integer logType;
    private String rpcType;
    private BigDecimal avgCost;
    private BigDecimal failureCount;
    private BigDecimal successCount;
    private BigDecimal totalCount;
    private BigDecimal successRate;
    private Integer samplingInterval;
}
