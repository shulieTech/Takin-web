package io.shulie.takin.web.data.param.agent;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class AgentMockDataCreateParam implements Serializable {
    private Long reportId;
    private Date collectStartTime;
    private Date collectEndTime;
    private String appName;
    private String agentId;
    private String mockService;
    private String mockMethod;
    private Long totalCost;
    private Long failureCount;
    private Long successCount;
    private Date createTime;
    private Long tenantId;
    private String envCode;
}
