package io.shulie.takin.web.amdb.bean.query.application;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TempTopologyQuery2 {
    private String fromAppName;
    private String appName;
    private String middlewareName;
    private String service;
    private String method;
    private String entranceStr;
    // -1 混合 0 业务流量 1 压测流量
    private int clusterTest;
    private String startTime;
    private String endTime;
    private int timeGap;
}
