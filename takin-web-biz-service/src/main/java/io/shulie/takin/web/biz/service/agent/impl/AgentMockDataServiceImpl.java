package io.shulie.takin.web.biz.service.agent.impl;

import cn.hutool.core.date.DateUtil;
import io.shulie.takin.web.biz.pojo.output.report.ReportDetailOutput;
import io.shulie.takin.web.biz.pojo.request.agent.AgentMockDataRequest;
import io.shulie.takin.web.biz.pojo.request.agent.AgentMockDataResponse;
import io.shulie.takin.web.biz.service.agent.AgentMockDataService;
import io.shulie.takin.web.biz.service.report.ReportService;
import io.shulie.takin.web.common.util.DataTransformUtil;
import io.shulie.takin.web.data.dao.agent.AgentMockDataDAO;
import io.shulie.takin.web.data.param.agent.AgentMockDataCreateParam;
import io.shulie.takin.web.data.result.agent.AgentMockDataResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@Slf4j
public class AgentMockDataServiceImpl implements AgentMockDataService {
    @Autowired
    private AgentMockDataDAO agentMockDataDAO;
    @Autowired
    private ReportService reportService;
    @Override
    public void saveAgentMockData(List<AgentMockDataRequest> requestList) {
        String taskId = requestList.get(0).getReportId();
        int pos = taskId.indexOf("z");
        if(pos == -1) {
            return;
        }
        Long reportId = 0L;
        try {
            reportId = Long.parseLong(taskId.substring(0, pos), 16);
        } catch (Exception e) {
            return;
        }
        ReportDetailOutput output = reportService.getSimpleReportByReportId(reportId);
        if(output == null) {
            return;
        }
        //开始时间12小时内
        if(DateUtil.parseDateTime(output.getStartTime()).getTime() + 12 * 60 * 60 * 1000 < System.currentTimeMillis()) {
            return;
        }
        for(AgentMockDataRequest request : requestList) {
            AgentMockDataCreateParam param = new AgentMockDataCreateParam();
            try {
                param.setReportId(reportId);
                param.setAppName(request.getAppName());
                param.setAgentId(request.getAgentId());
                param.setMockService(request.getService());
                param.setMockMethod(request.getMethod());
                param.setTotalCost(request.getTotalRt());
                param.setCreateTime(new Date());
                param.setCollectStartTime(new Date(request.getStartTime()));
                param.setCollectEndTime(new Date(request.getEndTime()));
                param.setSuccessCount(request.getSuccessCount());
                param.setFailureCount(request.getFailCount());
                param.setTenantId(output.getTenantId());
                param.setEnvCode(output.getEnvCode());
                agentMockDataDAO.insert(param);
            } catch (Exception e) {
                log.error("保存agentMockData数据失败:{}", e.getMessage(), e);
            }
        }
        requestList.clear();
    }

    @Override
    public List<AgentMockDataResponse> getListByReportId(Long reportId) {
        List<AgentMockDataResult> list = agentMockDataDAO.getMockDataListByReportId(reportId);
        return DataTransformUtil.list2list(list, AgentMockDataResponse.class);
    }
}
