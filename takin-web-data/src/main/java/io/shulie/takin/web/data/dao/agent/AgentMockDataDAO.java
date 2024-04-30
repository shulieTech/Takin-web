package io.shulie.takin.web.data.dao.agent;

import io.shulie.takin.web.data.param.agent.AgentMockDataCreateParam;
import io.shulie.takin.web.data.result.agent.AgentMockDataResult;

import java.util.List;

public interface AgentMockDataDAO {

    void insert(AgentMockDataCreateParam param);

    List<AgentMockDataResult> getMockDataListByReportId(Long reportId);
}
