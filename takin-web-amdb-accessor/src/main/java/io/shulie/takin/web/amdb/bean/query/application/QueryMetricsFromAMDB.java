package io.shulie.takin.web.amdb.bean.query.application;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class QueryMetricsFromAMDB {
    private long startMilli;
    private long endMilli;
    private Boolean metricsType;
    private String eagleId;
    private List<String> eagleIds;
    /**
     * 租户ID
     */
    private String tenantAppKey;
    /**
     * 环境编码
     */
    private String envCode;
}
