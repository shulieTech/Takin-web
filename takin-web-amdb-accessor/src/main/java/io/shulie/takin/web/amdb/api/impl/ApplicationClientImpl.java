/*
 * Copyright 2021 Shulie Technology, Co.Ltd
 * Email: opensource@shulie.io
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.shulie.takin.web.amdb.api.impl;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.Collections;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.google.common.collect.Lists;
import io.shulie.amdb.common.dto.agent.AgentInfoDTO;
import io.shulie.amdb.common.dto.instance.AgentStatusStatInfo;
import io.shulie.amdb.common.dto.instance.AppInstanceExtDTO;
import io.shulie.amdb.common.dto.instance.ModuleLoadDetailDTO;
import io.shulie.amdb.common.dto.link.entrance.ServiceInfoDTO;
import io.shulie.amdb.common.enums.EdgeTypeGroupEnum;
import io.shulie.amdb.common.enums.RpcType;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.amdb.api.ApplicationClient;
import io.shulie.takin.web.amdb.bean.common.AmdbResult;
import io.shulie.takin.web.amdb.bean.query.application.ApplicationErrorQueryDTO;
import io.shulie.takin.web.amdb.bean.query.application.ApplicationInterfaceQueryDTO;
import io.shulie.takin.web.amdb.bean.query.application.ApplicationNodeQueryDTO;
import io.shulie.takin.web.amdb.bean.query.application.ApplicationQueryDTO;
import io.shulie.takin.web.amdb.bean.query.application.ApplicationRemoteCallQueryDTO;
import io.shulie.takin.web.amdb.bean.query.fastagentaccess.ErrorLogQueryDTO;
import io.shulie.takin.web.amdb.bean.result.application.AppShadowDatabaseDTO;
import io.shulie.takin.web.amdb.bean.result.application.ApplicationBizTableDTO;
import io.shulie.takin.web.amdb.bean.result.application.ApplicationDTO;
import io.shulie.takin.web.amdb.bean.result.application.ApplicationErrorDTO;
import io.shulie.takin.web.amdb.bean.result.application.ApplicationInterfaceDTO;
import io.shulie.takin.web.amdb.bean.result.application.ApplicationNodeAgentDTO;
import io.shulie.takin.web.amdb.bean.result.application.ApplicationNodeDTO;
import io.shulie.takin.web.amdb.bean.result.application.ApplicationNodeProbeInfoDTO;
import io.shulie.takin.web.amdb.bean.result.application.ApplicationRemoteCallDTO;
import io.shulie.takin.web.amdb.util.AmdbHelper;
import io.shulie.takin.web.amdb.util.HttpClientUtil;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.common.util.JsonUtil;
import io.shulie.takin.web.common.util.application.RemoteCallUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.takin.properties.AmdbClientProperties;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * @author shiyajian
 * create: 2020-10-13
 */
@Component
@Slf4j
public class ApplicationClientImpl implements ApplicationClient {

    private static final String INTERFACE_PATH = "/amdb/link/getServiceList";
    private static final String APPLICATION_QUERY_PATH = "/amdb/db/api/app/selectByBatchAppParamsOnPostMetd";
    private static final String APPLICATION_NODE_QUERY_PATH = "/amdb/db/api/appInstance/selectByBatchAppParams";
    private static final String APPLICATION_ERROR_QUERY_PATH = "/amdb/db/api/appInstance/selectErrorInfoByParams";

    /**
     * 远程调用接口修改
     */
    private static final String APPLICATION_REMOTE_CALL_PATH = "/amdb/link/getExitList";

    /**
     * 新 应用节点列表路由
     */
    private static final String APPLICATION_NODE_PAGE = "/amdb/db/api/appInstanceStatus/queryInstanceStatus";

    /**
     * 可根据多个应用名查询，并且将传参条件放宽
     */
    private static final String APPLICATION_NODE_V2_PATH = "/amdb/db/api/appInstanceStatus/queryInstanceStatusV2";

    /**
     * 新 应用节点列表路由
     */
    private static final String APPLICATION_NODE_PAGE_V3 = "/amdb/db/api/appInstanceStatus/queryInstanceStatusV3";

    /**
     * 节点, 探针, 统计信息
     */
    private static final String APPLICATION_NODE_PROBE_INFO = "/amdb/db/api/appInstanceStatus/queryInstanceSumInfo";

    /**
     * 影子库表查询
     */
    private static final String APPLICATION_SHADOW_DATABASE_PATH = "/amdb/db/api/app/selectShadowDatabases";

    /**
     * 影子库表查询
     */
    private static final String APPLICATION_BUS_DATABASE_PATH = "/amdb/db/api/app/selectShadowBizTables";

    /**
     * 异常日志查询api
     */
    private static final String ERROR_LOG_PAGE = "/amdb/db/api/appInstance/queryAgentInfo";

    /**
     * 模块加载状态列表查询api
     */
    private static final String PLUGIN_LOAD_LIST = "/amdb/db/api/appInstance/select";

    /**
     * 探针概述查询接口
     */
    private static final String AGENT_COUNT_STATUS = "/amdb/db/api/appInstanceStatus/countStatus";

    @Autowired
    private AmdbClientProperties properties;

    @Override
    public List<ApplicationInterfaceDTO> listInterfaces(ApplicationInterfaceQueryDTO query) {
        return pageInterfaces(query).getList();
    }

    private List<ApplicationInterfaceDTO> getApplicationInterfaceDtoList(
        AmdbResult<List<ServiceInfoDTO>> amdbResponse) {
        return amdbResponse.getData().stream().map(serviceInfoDTO -> {
            ApplicationInterfaceDTO interfaceDTO = new ApplicationInterfaceDTO();
            interfaceDTO.setId("0");
            interfaceDTO.setInterfaceName(RemoteCallUtils.getInterfaceName(serviceInfoDTO.getRpcType(),
                serviceInfoDTO.getServiceName(), serviceInfoDTO.getMethodName()));

            EdgeTypeGroupEnum edgeTypeGroupEnum = EdgeTypeGroupEnum.getEdgeTypeEnum(serviceInfoDTO.getMiddlewareName());
            interfaceDTO.setInterfaceType(edgeTypeGroupEnum.getType());
            // 应用名
            interfaceDTO.setAppName(serviceInfoDTO.getAppName());
            return interfaceDTO;
        }).distinct().collect(Collectors.toList());
    }

    @Override
    public PagingList<ApplicationInterfaceDTO> pageInterfaces(ApplicationInterfaceQueryDTO query) {
        String url = properties.getUrl().getAmdb() + INTERFACE_PATH;
        query.setFieldNames("appName,middlewareName,serviceName,methodName,rpcType");
        query.setRpcType(StringUtils.join(
            Lists.newArrayList(String.valueOf(RpcType.TYPE_WEB_SERVER), String.valueOf(RpcType.TYPE_RPC)), ","));
        try {
            //            String responseEntity = HttpClientUtil.sendPost(url, query);
            //            if (StringUtils.isBlank(responseEntity)) {
            //                log.error("前往pardar查询应用的接口信息报错,请求地址：{}，响应信息：{}", url, responseEntity);
            //                return PagingList.empty();
            //            } else {
            //                AmdbResult<List<ServiceInfoDTO>> amdbResponse = JSONUtil.toBean(responseEntity,
            //                        new cn.hutool.core.lang.TypeReference<AmdbResult<List<ServiceInfoDTO>>>() {
            //                        }, true);
            //                List<ApplicationInterfaceDTO> dtos = getApplicationInterfaceDTOS(amdbResponse);
            //                return PagingList.of(dtos, amdbResponse.getTotal());
            if (StringUtils.isEmpty(query.getAppName())) {
                query.setAppName("-1");
            }
            AmdbResult<List<ServiceInfoDTO>> amdbResponse = AmdbHelper.newInStance().httpMethod(HttpMethod.POST)
                .url(url)
                .param(query)
                .eventName("查询应用的接口信息")
                .exception(TakinWebExceptionEnum.APPLICATION_MANAGE_THIRD_PARTY_ERROR)
                .list(ServiceInfoDTO.class);
            List<ApplicationInterfaceDTO> dtos = getApplicationInterfaceDtoList(amdbResponse);
            return PagingList.of(dtos, amdbResponse.getTotal());
            //            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_MANAGE_THIRD_PARTY_ERROR, e.getMessage());
        }
    }

    @Override
    public PagingList<ApplicationDTO> pageApplications(ApplicationQueryDTO query) {
        String url = properties.getUrl().getAmdb() + APPLICATION_QUERY_PATH;
        try {
            AmdbResult<List<ApplicationDTO>> amdbResponse = AmdbHelper.newInStance().httpMethod(HttpMethod.POST)
                .url(url)
                .param(query)
                .exception(TakinWebExceptionEnum.APPLICATION_MANAGE_THIRD_PARTY_ERROR)
                .eventName("查询应用信息")
                .list(ApplicationDTO.class);
            return PagingList.of(amdbResponse.getData(), amdbResponse.getTotal());

        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_MANAGE_THIRD_PARTY_ERROR, e.getMessage());
        }
    }

    @Override
    public PagingList<ApplicationNodeDTO> pageApplicationNodes(ApplicationNodeQueryDTO dto) {
        return pageApplicationNode(this.getApplicationNodeQueryPathUrl(), dto);
    }

    @Override
    public List<ApplicationErrorDTO> listErrors(ApplicationErrorQueryDTO query) {
        String url = properties.getUrl().getAmdb() + APPLICATION_ERROR_QUERY_PATH;
        try {
            //            String responseEntity = HttpClientUtil.sendGet(url, query);
            //            AmdbResult<List<ApplicationErrorDTO>> amdbResponse = JSONUtil.toBean(responseEntity,
            //                    new cn.hutool.core.lang.TypeReference<AmdbResult<List<ApplicationErrorDTO>>>() {
            //                    }, true);
            //            if (amdbResponse == null || !amdbResponse.getSuccess()) {
            //                log.error("前往amdb查询应用异常信息返回异常,响应信息：{}", JSONUtil.toJsonStr(amdbResponse));
            //                return Collections.emptyList();
            //            }
            //            List<ApplicationErrorDTO> data = amdbResponse.getData();
            //            if (CollectionUtils.isEmpty(data)) {
            //                return Collections.emptyList();
            //            }
            //            return amdbResponse.getData();

            AmdbResult<List<ApplicationErrorDTO>> amdbResponse = AmdbHelper.newInStance().url(url)
                .param(query)
                .eventName("查询应用异常信息")
                .exception(TakinWebExceptionEnum.APPLICATION_MANAGE_THIRD_PARTY_ERROR)
                .list(ApplicationErrorDTO.class);
            return amdbResponse.getData();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_MANAGE_THIRD_PARTY_ERROR, e.getMessage());
        }
    }

    @Override
    public PagingList<ApplicationNodeDTO> pageApplicationNode(ApplicationNodeQueryDTO dto) {
        return this.pageApplicationNode(this.getPageApplicationNodeUrl(), dto);
    }

    @Override
    public PagingList<ApplicationNodeDTO> pageApplicationNodeV2(ApplicationNodeQueryDTO dto) {
        return this.pageApplicationNode(this.getPageApplicationNodeUrlV2(), dto);
    }

    @Override
    public ApplicationNodeProbeInfoDTO getApplicationNodeProbeInfo(ApplicationNodeQueryDTO dto) {
        AmdbResult<ApplicationNodeProbeInfoDTO> result = AmdbHelper.newInStance().url(
                this.getApplicationNodeProbeInfoUrl())
            .param(dto)
            .eventName("查询应用节点信息")
            .exception(TakinWebExceptionEnum.APPLICATION_MANAGE_THIRD_PARTY_ERROR)
            .one(ApplicationNodeProbeInfoDTO.class);

        return result.getData();
    }

    /**
     * 节点分页
     *
     * @param url url
     * @param dto 请求参数
     * @return 节点分页
     */
    private PagingList<ApplicationNodeDTO> pageApplicationNode(String url, ApplicationNodeQueryDTO dto) {
        try {

            HttpMethod httpMethod = HttpMethod.GET;
            if (url.equals(this.getApplicationNodeQueryPathUrl())) {
                httpMethod = HttpMethod.POST;
            }
            AmdbResult<List<ApplicationNodeDTO>> amdbResponse = AmdbHelper.newInStance().httpMethod(httpMethod)
                .url(url)
                .param(dto)
                .exception(TakinWebExceptionEnum.APPLICATION_MANAGE_THIRD_PARTY_ERROR)
                .eventName("查询应用节点列表")
                .list(ApplicationNodeDTO.class);
            List<ApplicationNodeDTO> data = amdbResponse.getData();
            if (CollectionUtils.isEmpty(data)) {
                return PagingList.empty();
            }

            return PagingList.of(data, amdbResponse.getTotal());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_MANAGE_THIRD_PARTY_ERROR, e.getMessage());
            //            return PagingList.empty();
        }
    }

    /**
     * 应用节点, 探针统计信息
     *
     * @return 应用节点, 探针统计信息
     */
    private String getApplicationNodeProbeInfoUrl() {
        return properties.getUrl().getAmdb() + APPLICATION_NODE_PROBE_INFO;
    }

    /**
     * 应用节点列表url
     *
     * @return 应用节点列表url
     */
    private String getApplicationNodeQueryPathUrl() {
        return properties.getUrl().getAmdb() + APPLICATION_NODE_QUERY_PATH;
    }

    /**
     * 新 应用节点列表url
     *
     * @return 新 应用节点列表url
     */
    private String getPageApplicationNodeUrl() {
        return properties.getUrl().getAmdb() + APPLICATION_NODE_PAGE;
    }

    /**
     * 新 应用节点列表url
     *
     * @return 新 应用节点列表url
     */
    private String getPageApplicationNodeUrlV2() {
        return properties.getUrl().getAmdb() + APPLICATION_NODE_V2_PATH;
    }

    @Override
    public PagingList<ApplicationRemoteCallDTO> listApplicationRemoteCalls(ApplicationRemoteCallQueryDTO query) {
        String url = properties.getUrl().getAmdb() + APPLICATION_REMOTE_CALL_PATH;
        try {
            //            String responseEntity = HttpClientUtil.sendPost(url, query);
            //
            //            if (StringUtils.isBlank(responseEntity)) {
            //                log.error("前往amdb查询远程调用的接口信息报错,请求地址：{}，响应信息：{}", url, responseEntity);
            //                return PagingList.empty();
            //            } else {
            //                AmdbResult<List<ApplicationRemoteCallDTO>> amdbResponse = JSONUtil.toBean(responseEntity,
            //                        new cn.hutool.core.lang
            //                        .TypeReference<AmdbResult<List<ApplicationRemoteCallDTO>>>() {
            //                        }, true);
            //                return PagingList.of(amdbResponse.getData(), amdbResponse.getTotal());
            //            }

            AmdbResult<List<ApplicationRemoteCallDTO>> amdbResponse = AmdbHelper.newInStance().httpMethod(
                    HttpMethod.POST)
                .url(url)
                .param(query)
                .exception(TakinWebExceptionEnum.APPLICATION_MANAGE_THIRD_PARTY_ERROR)
                .eventName("查询远程调用的接口信息")
                .list(ApplicationRemoteCallDTO.class);
            return PagingList.of(amdbResponse.getData(), amdbResponse.getTotal());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_MANAGE_THIRD_PARTY_ERROR, e.getMessage());
        }
    }

    @Override
    public PagingList<ApplicationNodeAgentDTO> pageApplicationNodeByAgent(ApplicationNodeQueryDTO dto) {
        try {
            // 因为tro-web的分页从0开始大数据的分页从1开始，所以这里需要加1
            dto.setCurrentPage(dto.getRealCurrent());
            String url = properties.getUrl().getAmdb() + APPLICATION_NODE_PAGE_V3;
            String responseJson = HttpClientUtil.sendPost(url, dto);
            if (StrUtil.isBlank(responseJson)) {
                return PagingList.empty();
            }

            AmdbResult<List<ApplicationNodeAgentDTO>> amdbResponse = JsonUtil.json2bean(responseJson,
                new TypeReference<AmdbResult<List<ApplicationNodeAgentDTO>>>() {});

            if (amdbResponse == null || !amdbResponse.getSuccess()) {
                log.error("前往amdb查询agent应用节点返回异常,响应信息：{}", responseJson);
                return PagingList.empty();
            }

            List<ApplicationNodeAgentDTO> data = amdbResponse.getData();
            if (CollectionUtils.isEmpty(data)) {
                return PagingList.empty();
            }

            return PagingList.of(data, amdbResponse.getTotal());
        } catch (Exception e) {
            log.error("前往amdb查询agent应用节点信息报错：{}", JSONUtil.toJsonStr(dto), e);
            throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_MANAGE_THIRD_PARTY_ERROR, e.getMessage());
        }
    }

    @Override
    public PagingList<AgentInfoDTO> pageErrorLog(ErrorLogQueryDTO queryDTO) {
        String url = properties.getUrl().getAmdb() + ERROR_LOG_PAGE;
        try {
            // 因为tro-web的分页从0开始大数据的分页从1开始，所以这里需要加1
            queryDTO.setCurrentPage(queryDTO.getRealCurrent());
            String responseEntity = HttpUtil.post(url, JSONObject.parseObject(JSON.toJSONString(queryDTO)));
            if (StringUtils.isEmpty(responseEntity)) {
                return PagingList.empty();
            }
            AmdbResult<List<AgentInfoDTO>> amdbResponse = JSONUtil.toBean(responseEntity,
                new cn.hutool.core.lang.TypeReference<AmdbResult<List<AgentInfoDTO>>>() {}, true);
            if (amdbResponse == null || !amdbResponse.getSuccess()) {
                log.error("前往amdb查询异常日志返回异常,响应信息：{}", JSONUtil.toJsonStr(amdbResponse));
                return PagingList.empty();
            }
            List<AgentInfoDTO> data = amdbResponse.getData();
            if (CollectionUtils.isEmpty(data)) {
                return PagingList.empty();
            }
            return PagingList.of(data, amdbResponse.getTotal());

        } catch (Exception e) {
            log.error("前往amdb查询异常日志报错：{}", JSONUtil.toJsonStr(queryDTO), e);
            throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_MANAGE_THIRD_PARTY_ERROR, e.getMessage());
        }
    }

    @Override
    public List<ModuleLoadDetailDTO> pluginList(String agentId) {
        try {
            String url = properties.getUrl().getAmdb() + PLUGIN_LOAD_LIST + "?agentId=" + agentId;
            String responseJson = HttpClientUtil.sendGet(url);
            if (StrUtil.isBlank(responseJson)) {
                return Collections.emptyList();
            }
            AmdbResult<Object> result = JsonUtil.json2bean(responseJson,
                new TypeReference<AmdbResult<Object>>() {});
            if (result == null || !result.getSuccess()) {
                log.error("前往amdb查询模块加载状态返回异常,响应信息：{}", responseJson);
                return Collections.emptyList();
            }
            if (result.getData() != null) {
                JSONObject data = JSON.parseObject(JSON.toJSONString(result.getData()));
                String ext = data.getString("ext");
                if (StringUtils.isNotBlank(ext)) {
                    AppInstanceExtDTO extDTO = JSON.parseObject(ext, AppInstanceExtDTO.class);
                    return extDTO.getModuleLoadDetail();
                }
            }
        } catch (Exception e) {
            log.error("模块加载状态数据处理异常", e);
            throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_MANAGE_THIRD_PARTY_ERROR, e.getMessage());
        }

        return Collections.emptyList();
    }

    @Override
    public AgentStatusStatInfo agentCountStatus(String appNames) {
        if (StringUtils.isBlank(appNames)) {
            return new AgentStatusStatInfo();
        }
        ApplicationNodeAgentDTO applicationNodeAgentDTO = new ApplicationNodeAgentDTO();
        applicationNodeAgentDTO.setAppNames(appNames);
        String url = properties.getUrl().getAmdb() + AGENT_COUNT_STATUS;
        try {
            String responseEntity = HttpClientUtil.sendPost(url, applicationNodeAgentDTO);
            AmdbResult<AgentStatusStatInfo> amdbResponse = JSONUtil.toBean(responseEntity,
                new cn.hutool.core.lang.TypeReference<AmdbResult<AgentStatusStatInfo>>() {}, true);
            if (amdbResponse == null || !amdbResponse.getSuccess()) {
                log.error("前往amdb查询agent概况返回异常,响应信息：{}", JSONUtil.toJsonStr(amdbResponse));
                return null;
            }
            return amdbResponse.getData();
        } catch (Exception e) {
            log.error("前往amdb查询agent概况信息报错", e);
            throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_MANAGE_THIRD_PARTY_ERROR, e.getMessage());
        }
    }

    /**
     * 影子库表查询
     *
     * @param appName
     * @return
     */
    @Override
    public List<AppShadowDatabaseDTO> getApplicationShadowDataBaseInfo(String appName) {
        return this.getApplicationShadowDataBaseInfo(appName, "");
    }

    /**
     * 影子库表查询
     *
     * @param appName
     * @param dataSource
     * @return
     */
    @Override
    public List<AppShadowDatabaseDTO> getApplicationShadowDataBaseInfo(String appName, String dataSource) {
        String url = properties.getUrl().getAmdb() + APPLICATION_SHADOW_DATABASE_PATH;
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("appName", appName);
        paramMap.put("pageSize", Integer.MAX_VALUE);
        paramMap.put("dataSource", dataSource);
        AmdbResult<List<AppShadowDatabaseDTO>> amdbResponse = AmdbHelper.newInStance().httpMethod(HttpMethod.GET)
            .url(url)
            .param(paramMap)
            .exception(TakinWebExceptionEnum.APPLICATION_MANAGE_THIRD_PARTY_ERROR)
            .eventName("查询影子库表信息")
            .list(AppShadowDatabaseDTO.class);
        List<AppShadowDatabaseDTO> data = amdbResponse.getData();
        return data;
    }

    /**
     * 业务数据库表查询
     *
     * @param appName
     * @return
     */
    @Override
    public List<ApplicationBizTableDTO> getApplicationTable(String appName, String dataSource, String userName) {
        if (StringUtils.isBlank(userName)) {
            return Collections.emptyList();
        }
        String url = properties.getUrl().getAmdb() + APPLICATION_BUS_DATABASE_PATH;
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("appName", appName);
        paramMap.put("dataSource", dataSource);
        paramMap.put("tableUser", userName);
        AmdbResult<List<ApplicationBizTableDTO>> amdbResponse = AmdbHelper.newInStance().httpMethod(HttpMethod.GET)
            .url(url)
            .param(paramMap)
            .exception(TakinWebExceptionEnum.APPLICATION_MANAGE_THIRD_PARTY_ERROR)
            .eventName("查询业务库表信息")
            .list(ApplicationBizTableDTO.class);
        List<ApplicationBizTableDTO> data = amdbResponse.getData();
        return data;
    }
}
