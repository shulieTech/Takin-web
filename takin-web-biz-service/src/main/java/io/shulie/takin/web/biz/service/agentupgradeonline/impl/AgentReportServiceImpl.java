package io.shulie.takin.web.biz.service.agentupgradeonline.impl;

import cn.hutool.core.collection.CollStreamUtil;
import io.shulie.takin.web.biz.service.agentupgradeonline.AgentReportService;
import io.shulie.takin.web.data.dao.agentupgradeonline.AgentReportDAO;
import io.shulie.takin.web.data.result.application.AgentReportDetailResult;
import io.shulie.takin.web.data.result.application.ApplicationTagRefDetailResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 探针心跳数据(AgentReport)service
 *
 * @author ocean_wll
 * @date 2021-11-09 20:33:00
 */
@Service
public class AgentReportServiceImpl implements AgentReportService {

    @Autowired
    private AgentReportDAO agentReportDAO;

    @Override
    public List<AgentReportDetailResult> getList(List<Long> appIds) {
        if(CollectionUtils.isEmpty(appIds)){
            return Collections.emptyList();
        }
        return agentReportDAO.getList(appIds);
    }

    @Override
    public List<AgentReportDetailResult> getListByStatus(List<Integer> statusList) {
        if(CollectionUtils.isEmpty(statusList)){
            return Collections.emptyList();
        }
        return agentReportDAO.getListByStatus(statusList);
    }

    @Override
    public Map<Long, Integer> appId2Count() {
        List<AgentReportDetailResult> list = agentReportDAO.getList();

        Map<Long, List<AgentReportDetailResult>> appId2Detail = CollStreamUtil.groupByKey(list,
                AgentReportDetailResult::getApplicationId);

        Map<Long,Integer> appId2CountMap = new HashMap<>();
        appId2Detail.forEach((k,v) ->
                appId2CountMap.put(k,v.size())
        );
        return appId2CountMap;
    }
}
