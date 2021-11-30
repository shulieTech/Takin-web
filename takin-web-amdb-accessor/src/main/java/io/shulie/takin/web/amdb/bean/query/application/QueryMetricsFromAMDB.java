package io.shulie.takin.web.amdb.bean.query.application;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QueryMetricsFromAMDB {
    private long startMilli;
    private long endMilli;
    private Boolean metricsType;
    private String eagleId;

    /**
     * 租户ID
     */
    private String tenantAppKey;
    /**
     * 环境编码
     */
    private String envCode;
}
