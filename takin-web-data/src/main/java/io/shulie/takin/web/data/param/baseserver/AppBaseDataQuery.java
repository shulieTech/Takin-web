package io.shulie.takin.web.data.param.baseserver;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class AppBaseDataQuery {

    private Map<String, String> fieldAndAlias;

    private Long startTime;

    private Long endTime;

    private String appName;

    private String appId;

    private String agentId;

    /**
     * groupBy字段
     */
    private List<String> groupByFields;

    /**
     * 租户标识
     */
    private String tenantAppKey;
    /**
     * 环境标识
     */
    private String envCode;
}
