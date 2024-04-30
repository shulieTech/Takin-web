package io.shulie.takin.web.biz.pojo.request.agent;

import lombok.Data;

import java.io.Serializable;

@Data
public class AgentMockDataRequest implements Serializable {
    private Long reportId;
    private Long totalCount;
    private Long successCount;
    private Long failCount;
    private Long totalRt;
    private Long startTime;
    private Long endTime;
    private String appName;
    private String agentId;
    private String service;
    private String method;
}
