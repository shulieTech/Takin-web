package io.shulie.takin.web.biz.service.agent;

import io.shulie.takin.web.biz.pojo.request.agent.AgentMockDataRequest;
import io.shulie.takin.web.biz.pojo.request.agent.AgentMockDataResponse;

import java.util.Date;
import java.util.List;

public interface AgentMockDataService {

    void saveAgentMockData(List<AgentMockDataRequest> requestList);

    List<AgentMockDataResponse> getListByReportId(Long reportId);

    void clearExpireData(Date beforeDate);
}
