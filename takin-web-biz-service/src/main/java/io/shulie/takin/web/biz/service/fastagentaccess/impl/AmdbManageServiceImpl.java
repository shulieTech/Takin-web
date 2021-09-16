package io.shulie.takin.web.biz.service.fastagentaccess.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.google.common.base.Joiner;
import io.shulie.amdb.common.dto.agent.AgentInfoDTO;
import io.shulie.amdb.common.dto.agent.AgentStatInfoDTO;
import io.shulie.amdb.common.dto.instance.AgentStatusStatInfo;
import io.shulie.amdb.common.dto.instance.ModuleLoadDetailDTO;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.amdb.api.AgentConfigClient;
import io.shulie.takin.web.amdb.api.ApplicationClient;
import io.shulie.takin.web.amdb.bean.query.application.ApplicationNodeQueryDTO;
import io.shulie.takin.web.amdb.bean.query.fastagentaccess.ErrorLogQueryDTO;
import io.shulie.takin.web.amdb.bean.result.application.ApplicationNodeAgentDTO;
import io.shulie.takin.web.biz.pojo.request.fastagentaccess.AgentDiscoverRequest;
import io.shulie.takin.web.biz.pojo.request.fastagentaccess.AgentListQueryRequest;
import io.shulie.takin.web.biz.pojo.request.fastagentaccess.ErrorLogQueryRequest;
import io.shulie.takin.web.biz.pojo.response.fastagentaccess.AgentConfigStatusCountResponse;
import io.shulie.takin.web.biz.pojo.response.fastagentaccess.AgentListResponse;
import io.shulie.takin.web.biz.pojo.response.fastagentaccess.AgentStatusStatResponse;
import io.shulie.takin.web.biz.pojo.response.fastagentaccess.ErrorLogListResponse;
import io.shulie.takin.web.biz.pojo.response.fastagentaccess.PluginLoadListResponse;
import io.shulie.takin.web.biz.service.fastagentaccess.AgentConfigService;
import io.shulie.takin.web.biz.service.fastagentaccess.AmdbManageService;
import io.shulie.takin.web.common.enums.fastagentaccess.AgentDiscoverStatusEnum;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

/**
 * @Description
 * @Author ocean_wll
 * @Date 2021/8/19 12:01 下午
 */
@Service
public class AmdbManageServiceImpl implements AmdbManageService {

    @Autowired
    private ApplicationClient applicationClient;

    @Autowired
    private AgentConfigClient agentConfigClient;

    @Autowired
    private AgentConfigService agentConfigService;

    @Override
    public PagingList<ErrorLogListResponse> pageErrorLog(ErrorLogQueryRequest queryRequest) {
        ErrorLogQueryDTO queryDTO = new ErrorLogQueryDTO();
        BeanUtils.copyProperties(queryRequest, queryDTO);
        queryDTO.setAgentInfo(queryRequest.getKeyword());
        queryDTO.setUserAppKey(WebPluginUtils.getTenantUserAppKey());

        if (StringUtils.isEmpty(queryRequest.getProjectName())) {
            List<String> appNameList = agentConfigService.getAllApplication("");
            if (CollectionUtils.isEmpty(appNameList)) {
                return PagingList.empty();
            }
            String appNames = Joiner.on(",").join(appNameList);
            queryDTO.setAppNames(appNames);
        } else {
            queryDTO.setAppName(queryRequest.getProjectName());
        }

        PagingList<AgentInfoDTO> pagingList = applicationClient.pageErrorLog(queryDTO);
        List<ErrorLogListResponse> list = pagingList.getList().stream().map(item -> {
            ErrorLogListResponse response = new ErrorLogListResponse();
            response.setAgentId(item.getAgentId());
            response.setProjectName(item.getAppName());
            response.setAgentInfo(item.getAgentInfo());
            response.setAgentTimestamp(new Date(item.getAgentTimestamp()));
            return response;
        }).collect(Collectors.toList());
        return PagingList.of(list, pagingList.getTotal());
    }

    @Override
    public List<PluginLoadListResponse> pluginList(String agentId) {
        List<ModuleLoadDetailDTO> moduleLoadDetailDTOList = applicationClient.pluginList(agentId);
        if (CollectionUtils.isEmpty(moduleLoadDetailDTOList)) {
            return new ArrayList<>();
        }
        return moduleLoadDetailDTOList.stream().map(item -> {
            PluginLoadListResponse response = new PluginLoadListResponse();
            BeanUtils.copyProperties(item, response);
            return response;
        }).collect(Collectors.toList());
    }

    @Override
    public AgentStatusStatResponse agentCountStatus() {
        List<String> appNameList = agentConfigService.getAllApplication("");
        String appNames = Joiner.on(",").join(appNameList);
        AgentStatusStatInfo statusStatInfo = applicationClient.agentCountStatus(appNames);
        AgentStatusStatResponse response = new AgentStatusStatResponse();
        if (statusStatInfo != null) {
            //强调：这里没有写反，大数据的接口需要这里转换一下
            response.setAgentCount(statusStatInfo.getProbeCount());
            response.setAgentFailCount(statusStatInfo.getProbeFailCount());
            response.setProbeCount(statusStatInfo.getAgentCount());
            response.setProbeFailCount(statusStatInfo.getAgentFailCount());
        }
        return response;
    }

    @Override
    public AgentConfigStatusCountResponse agentConfigStatusCount(String enKey, String projectName) {
        AgentStatInfoDTO statInfoDTO = agentConfigClient.agentConfigStatusCount(enKey, projectName);
        if (statInfoDTO == null) {
            return null;
        }
        AgentConfigStatusCountResponse response = new AgentConfigStatusCountResponse();
        BeanUtils.copyProperties(statInfoDTO, response);
        return response;
    }

    @Override
    public PagingList<AgentListResponse> agentList(AgentListQueryRequest queryRequest) {
        ApplicationNodeQueryDTO queryDTO = new ApplicationNodeQueryDTO();
        // ocean_wll 这里没有写错，大数据那边接口字段就是这么定义的
        BeanUtils.copyProperties(queryRequest, queryDTO);
        queryDTO.setAgentStatus(queryRequest.getProbeStatus());
        queryDTO.setProbeStatus(queryRequest.getAgentStatus());

        if (StringUtils.isEmpty(queryRequest.getProjectName())) {
            List<String> appNameList = agentConfigService.getAllApplication("");
            // 如果当前用户没有应用权限，则直接返回空集合
            if (CollectionUtils.isEmpty(appNameList)) {
                return PagingList.empty();
            }
            String appNames = Joiner.on(",").join(appNameList);
            queryDTO.setAppNames(appNames);
        } else {
            queryDTO.setAppName(queryRequest.getProjectName());
        }

        PagingList<ApplicationNodeAgentDTO> responsePagingList = applicationClient.pageApplicationNodeByAgent(queryDTO);
        List<ApplicationNodeAgentDTO> dtoList = responsePagingList.getList();
        if (CollectionUtils.isEmpty(dtoList)) {
            return PagingList.empty();
        }
        List<AgentListResponse> responseList = dtoList.stream().map(item -> {
            AgentListResponse response = new AgentListResponse();
            BeanUtils.copyProperties(item, response);
            // 强调：这个的set没有写错，大数据那边的字段含义就是这样的
            response.setAgentStatus(item.getProbeStatus());
            response.setAgentErrorMsg(item.getErrorMsg());
            response.setProbeStatus(item.getAgentStatus());
            response.setProbeErrorMsg(item.getAgentErrorMsg());
            response.setProjectName(item.getAppName());
            return response;
        }).collect(Collectors.toList());
        return PagingList.of(responseList, responsePagingList.getTotal());
    }

    @Override
    public AgentDiscoverStatusEnum agentDiscover(AgentDiscoverRequest agentDiscoverRequest) {
        // 如果查询时间超过6分钟则直接返回安装失败
        if (agentDiscoverRequest.getCheckDate().getTime() + 6 * 60 * 1000 < System.currentTimeMillis()) {
            return AgentDiscoverStatusEnum.FAIL;
        }
        AgentListQueryRequest queryRequest = new AgentListQueryRequest();
        queryRequest.setProjectName(agentDiscoverRequest.getProjectName());
        queryRequest.setMinUpdateDate(agentDiscoverRequest.getDownloadScriptDate());
        PagingList<AgentListResponse> pagingList = agentList(queryRequest);
        if (pagingList.getTotal() > 0) {
            return AgentDiscoverStatusEnum.SUCCESS;
        } else {
            return AgentDiscoverStatusEnum.ING;
        }
    }
}
