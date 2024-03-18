package io.shulie.takin.web.amdb.bean.result.trace;

import lombok.Data;

import java.math.BigDecimal;

/**
 * @author zhangz
 * Created on 2024/3/13 20:53
 * Email: zz052831@163.com
 */

@Data
public class EntryTraceAvgCostDTO {
    private String traceId;
    private String appName;
    private String serviceName;
    private String methodName;
    private String rpcId;
    private Integer logType;
    private String rpcType;
    private BigDecimal avgCost;
    private BigDecimal failureCount;
    private BigDecimal successCount;
    private BigDecimal totalCount;
    private BigDecimal successRate;
}
