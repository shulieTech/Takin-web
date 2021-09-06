package io.shulie.takin.web.biz.service.agentfastaccess;

import java.util.List;

import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.pojo.request.fastagentaccess.AgentDiscoverRequest;
import io.shulie.takin.web.biz.pojo.request.fastagentaccess.AgentListQueryRequest;
import io.shulie.takin.web.biz.pojo.request.fastagentaccess.ErrorLogQueryRequest;
import io.shulie.takin.web.biz.pojo.response.fastagentaccess.AgentConfigStatusCountResponse;
import io.shulie.takin.web.biz.pojo.response.fastagentaccess.AgentListResponse;
import io.shulie.takin.web.biz.pojo.response.fastagentaccess.AgentStatusStatResponse;
import io.shulie.takin.web.biz.pojo.response.fastagentaccess.ErrorLogListResponse;
import io.shulie.takin.web.biz.pojo.response.fastagentaccess.PluginLoadListResponse;
import io.shulie.takin.web.common.enums.fastagentaccess.AgentDiscoverStatusEnum;

/**
 * @Description 大数据管理接口
 * @Author ocean_wll
 * @Date 2021/8/19 11:57 上午
 */
public interface AmdbManageService {

    /**
     * 异常日志列表查询接口
     *
     * @param queryRequest 查询条件
     * @return PagingList<AgentInfoDTO>
     */
    PagingList<ErrorLogListResponse> pageErrorLog(ErrorLogQueryRequest queryRequest);

    /**
     * 根据agentId查询模块加载状态
     *
     * @param agentId agentId
     * @return PluginLoadListResponse集合
     */
    List<PluginLoadListResponse> pluginList(String agentId);

    /**
     * 探针概述查询
     *
     * @return AgentStatusStatResponse
     */
    AgentStatusStatResponse agentCountStatus();

    /**
     * 配置生效状态统计接口
     *
     * @param enKey       配置key
     * @param projectName 应用名
     * @return AgentConfigStatusCountResponse
     */
    AgentConfigStatusCountResponse agentConfigStatusCount(String enKey, String projectName);

    /**
     * 探针列表查询
     *
     * @param queryRequest 探针列表查询条件
     * @return PagingList<AgentListResponse>
     */
    PagingList<AgentListResponse> agentList(AgentListQueryRequest queryRequest);

    /**
     * 检测新应用接入状态
     *
     * @param agentDiscoverRequest 查询条件
     * @return AgentDiscoverStatusEnum枚举类
     */
    AgentDiscoverStatusEnum agentDiscover(AgentDiscoverRequest agentDiscoverRequest);
}
