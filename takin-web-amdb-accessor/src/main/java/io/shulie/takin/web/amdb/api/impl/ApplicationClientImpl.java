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
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.common.util.application.RemoteCallUtils;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.takin.properties.AmdbClientProperties;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;

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
     * ????????????????????????
     */
    private static final String APPLICATION_REMOTE_CALL_PATH = "/amdb/link/getExitList";

    /**
     * ??? ????????????????????????
     */
    private static final String APPLICATION_NODE_PAGE = "/amdb/db/api/appInstanceStatus/queryInstanceStatus";

    /**
     * ????????????????????????????????????????????????????????????
     */
    private static final String APPLICATION_NODE_V2_PATH = "/amdb/db/api/appInstanceStatus/queryInstanceStatusV2";

    /**
     * ??? ????????????????????????
     */
    private static final String APPLICATION_NODE_PAGE_V3 = "/amdb/db/api/appInstanceStatus/queryInstanceStatusV3";

    /**
     * ??????, ??????, ????????????
     */
    private static final String APPLICATION_NODE_PROBE_INFO = "/amdb/db/api/appInstanceStatus/queryInstanceSumInfo";

    /**
     * ??????????????????
     */
    private static final String APPLICATION_SHADOW_DATABASE_PATH = "/amdb/db/api/app/selectShadowDatabases";

    /**
     * ??????????????????
     */
    private static final String APPLICATION_BUS_DATABASE_PATH = "/amdb/db/api/app/selectShadowBizTables";

    /**
     * ??????????????????api
     */
    private static final String ERROR_LOG_PAGE = "/amdb/db/api/appInstance/queryAgentInfo";

    /**
     * ??????????????????????????????api
     */
    private static final String PLUGIN_LOAD_LIST = "/amdb/db/api/appInstance/select";

    /**
     * ????????????????????????
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
            // ?????????
            interfaceDTO.setAppName(serviceInfoDTO.getAppName());
            return interfaceDTO;
        }).distinct().collect(Collectors.toList());
    }

    @Override
    public PagingList<ApplicationInterfaceDTO> pageInterfaces(ApplicationInterfaceQueryDTO query) {
        String url = properties.getUrl().getAmdb() + INTERFACE_PATH;
        query.setTenantAppKey(WebPluginUtils.traceTenantAppKey());
        query.setEnvCode(WebPluginUtils.traceEnvCode());
        query.setFieldNames("appName,middlewareName,serviceName,methodName,rpcType");
        query.setRpcType(StringUtils.join(
            Lists.newArrayList(String.valueOf(RpcType.TYPE_WEB_SERVER), String.valueOf(RpcType.TYPE_RPC)), ","));
        try {
            if (StringUtils.isEmpty(query.getAppName())) {
                query.setAppName("-1");
            }
            AmdbResult<List<ServiceInfoDTO>> amdbResponse = AmdbHelper.builder().httpMethod(HttpMethod.POST)
                .url(url)
                .param(query)
                .eventName("???????????????????????????")
                .exception(TakinWebExceptionEnum.APPLICATION_MANAGE_THIRD_PARTY_ERROR)
                .list(ServiceInfoDTO.class);
            List<ApplicationInterfaceDTO> dtos = getApplicationInterfaceDtoList(amdbResponse);
            return PagingList.of(dtos, amdbResponse.getTotal());
        } catch (Exception e) {
            throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_MANAGE_THIRD_PARTY_ERROR, e.getMessage(), e);
        }
    }

    @Override
    public PagingList<ApplicationDTO> pageApplications(ApplicationQueryDTO query) {
        query.setTenantAppKey(WebPluginUtils.traceTenantAppKey());
        query.setEnvCode(WebPluginUtils.traceEnvCode());
        String url = properties.getUrl().getAmdb() + APPLICATION_QUERY_PATH;
        try {
            AmdbResult<List<ApplicationDTO>> amdbResponse = AmdbHelper.builder().httpMethod(HttpMethod.POST)
                .url(url)
                .param(query)
                .exception(TakinWebExceptionEnum.APPLICATION_MANAGE_THIRD_PARTY_ERROR)
                .eventName("??????????????????")
                .list(ApplicationDTO.class);
            return PagingList.of(amdbResponse.getData(), amdbResponse.getTotal());

        } catch (Exception e) {
            throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_MANAGE_THIRD_PARTY_ERROR, e.getMessage(), e);
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
            query.setTenantAppKey(WebPluginUtils.traceTenantAppKey());
            query.setEnvCode(WebPluginUtils.traceEnvCode());
            AmdbResult<List<ApplicationErrorDTO>> amdbResponse = AmdbHelper.builder().url(url)
                .param(query)
                .eventName("????????????????????????")
                .exception(TakinWebExceptionEnum.APPLICATION_MANAGE_THIRD_PARTY_ERROR)
                .list(ApplicationErrorDTO.class);
            return amdbResponse.getData();
        } catch (Exception e) {
            throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_MANAGE_THIRD_PARTY_ERROR, e.getMessage(), e);
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
        dto.setTenantAppKey(WebPluginUtils.traceTenantAppKey());
        dto.setEnvCode(WebPluginUtils.traceEnvCode());
        AmdbResult<ApplicationNodeProbeInfoDTO> result = AmdbHelper.builder().url(
            this.getApplicationNodeProbeInfoUrl())
            .param(dto)
            .eventName("????????????????????????")
            .exception(TakinWebExceptionEnum.APPLICATION_MANAGE_THIRD_PARTY_ERROR)
            .one(ApplicationNodeProbeInfoDTO.class);
        return result.getData();
    }

    /**
     * ????????????
     *
     * @param url url
     * @param dto ????????????
     * @return ????????????
     */
    private PagingList<ApplicationNodeDTO> pageApplicationNode(String url, ApplicationNodeQueryDTO dto) {
        try {
            dto.setTenantAppKey(WebPluginUtils.traceTenantAppKey());
            dto.setEnvCode(WebPluginUtils.traceEnvCode());

            HttpMethod httpMethod = HttpMethod.GET;
            if (url.equals(this.getApplicationNodeQueryPathUrl())) {
                httpMethod = HttpMethod.POST;
            }
            AmdbResult<List<ApplicationNodeDTO>> amdbResponse = AmdbHelper.builder().httpMethod(httpMethod)
                .url(url)
                .param(dto)
                .exception(TakinWebExceptionEnum.APPLICATION_MANAGE_THIRD_PARTY_ERROR)
                .eventName("????????????????????????")
                .list(ApplicationNodeDTO.class);
            List<ApplicationNodeDTO> data = amdbResponse.getData();
            return PagingList.of(data, amdbResponse.getTotal());
        } catch (Exception e) {
            throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_MANAGE_THIRD_PARTY_ERROR, e.getMessage(), e);
        }
    }

    /**
     * ????????????, ??????????????????
     *
     * @return ????????????, ??????????????????
     */
    private String getApplicationNodeProbeInfoUrl() {
        return properties.getUrl().getAmdb() + APPLICATION_NODE_PROBE_INFO;
    }

    /**
     * ??????????????????url
     *
     * @return ??????????????????url
     */
    private String getApplicationNodeQueryPathUrl() {
        return properties.getUrl().getAmdb() + APPLICATION_NODE_QUERY_PATH;
    }

    /**
     * ??? ??????????????????url
     *
     * @return ??? ??????????????????url
     */
    private String getPageApplicationNodeUrl() {
        return properties.getUrl().getAmdb() + APPLICATION_NODE_PAGE;
    }

    /**
     * ??? ??????????????????url
     *
     * @return ??? ??????????????????url
     */
    private String getPageApplicationNodeUrlV2() {
        return properties.getUrl().getAmdb() + APPLICATION_NODE_V2_PATH;
    }

    @Override
    public PagingList<ApplicationRemoteCallDTO> listApplicationRemoteCalls(ApplicationRemoteCallQueryDTO query) {
        String url = properties.getUrl().getAmdb() + APPLICATION_REMOTE_CALL_PATH;
        try {
            query.setTenantAppKey(WebPluginUtils.traceTenantAppKey());
            query.setEnvCode(WebPluginUtils.traceEnvCode());
            AmdbResult<List<ApplicationRemoteCallDTO>> amdbResponse = AmdbHelper.builder().httpMethod(
                HttpMethod.POST)
                .url(url)
                .param(query)
                .exception(TakinWebExceptionEnum.APPLICATION_MANAGE_THIRD_PARTY_ERROR)
                .eventName("?????????????????????????????????")
                .list(ApplicationRemoteCallDTO.class);
            return PagingList.of(amdbResponse.getData(), amdbResponse.getTotal());
        } catch (Exception e) {
            throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_MANAGE_THIRD_PARTY_ERROR, e.getMessage(), e);
        }
    }

    @Override
    public PagingList<ApplicationNodeAgentDTO> pageApplicationNodeByAgent(ApplicationNodeQueryDTO dto) {
        try {
            // ??????tro-web????????????0???????????????????????????1??????????????????????????????1
            dto.setCurrentPage(dto.getRealCurrent());
            dto.setTenantAppKey(WebPluginUtils.traceTenantAppKey());
            dto.setEnvCode(WebPluginUtils.traceEnvCode());
            String url = properties.getUrl().getAmdb() + APPLICATION_NODE_PAGE_V3;

            AmdbResult<List<ApplicationNodeAgentDTO>> amdbResponse = AmdbHelper.builder().httpMethod(
                HttpMethod.POST)
                .url(url)
                .param(dto)
                .exception(TakinWebExceptionEnum.APPLICATION_MANAGE_THIRD_PARTY_ERROR)
                .eventName("??????agent????????????")
                .list(ApplicationNodeAgentDTO.class);
            return PagingList.of(amdbResponse.getData(), amdbResponse.getTotal());
        } catch (Exception e) {
            throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_MANAGE_THIRD_PARTY_ERROR, e.getMessage(), e);
        }
    }

    /**
     * @param queryDTO ????????????
     * @return
     */
    @Override
    public PagingList<AgentInfoDTO> pageErrorLog(ErrorLogQueryDTO queryDTO) {
        String url = properties.getUrl().getAmdb() + ERROR_LOG_PAGE;
        try {
            // ??????tro-web????????????0???????????????????????????1??????????????????????????????1
            queryDTO.setTenantAppKey(WebPluginUtils.traceTenantAppKey());
            queryDTO.setEnvCode(WebPluginUtils.traceEnvCode());

            AmdbResult<List<AgentInfoDTO>> amdbResponse = AmdbHelper.builder().httpMethod(HttpMethod.POST)
                .url(url)
                .param(queryDTO)
                .exception(TakinWebExceptionEnum.APPLICATION_MANAGE_THIRD_PARTY_ERROR)
                .eventName("??????????????????")
                .list(AgentInfoDTO.class);
            return PagingList.of(amdbResponse.getData(), amdbResponse.getTotal());

        } catch (Exception e) {
            throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_MANAGE_THIRD_PARTY_ERROR, e.getMessage(), e);
        }
    }

    @Override
    public List<ModuleLoadDetailDTO> pluginList(String agentId) {
        String url = properties.getUrl().getAmdb() + PLUGIN_LOAD_LIST + "?agentId=" + agentId
            + "&userAppKey=" + WebPluginUtils.traceTenantAppKey() + "&envCode=" + WebPluginUtils.traceEnvCode();
        try {
            AmdbResult<Object> result = AmdbHelper.builder()
                .url(url)
                .exception(TakinWebExceptionEnum.APPLICATION_MANAGE_THIRD_PARTY_ERROR)
                .eventName("????????????????????????")
                .one(Object.class);

            if (result.getData() != null) {
                JSONObject data = JSON.parseObject(JSON.toJSONString(result.getData()));
                String ext = data.getString("ext");
                if (StringUtils.isNotBlank(ext)) {
                    AppInstanceExtDTO extDTO = JSON.parseObject(ext, AppInstanceExtDTO.class);
                    return extDTO.getModuleLoadDetail();
                }
            }
        } catch (Exception e) {
            throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_MANAGE_THIRD_PARTY_ERROR, e.getMessage(), e);
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
        applicationNodeAgentDTO.setTenantAppKey(WebPluginUtils.traceTenantAppKey());
        applicationNodeAgentDTO.setEnvCode(WebPluginUtils.traceEnvCode());
        String url = properties.getUrl().getAmdb() + AGENT_COUNT_STATUS;
        try {
            AmdbResult<AgentStatusStatInfo> amdbResponse = AmdbHelper.builder()
                .httpMethod(HttpMethod.POST)
                .param(applicationNodeAgentDTO)
                .url(url)
                .exception(TakinWebExceptionEnum.APPLICATION_MANAGE_THIRD_PARTY_ERROR)
                .eventName("??????agent??????")
                .one(AgentStatusStatInfo.class);
            return amdbResponse.getData();
        } catch (Exception e) {
            throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_MANAGE_THIRD_PARTY_ERROR, e.getMessage(), e);
        }
    }

    /**
     * ??????????????????
     *
     * @param appName
     * @return
     */
    @Override
    public List<AppShadowDatabaseDTO> getApplicationShadowDataBaseInfo(String appName) {
        return this.getApplicationShadowDataBaseInfo(appName, "");
    }

    /**
     * ??????????????????
     *
     * @param appName
     * @param dataSource
     * @return
     */
    @Override
    public List<AppShadowDatabaseDTO> getApplicationShadowDataBaseInfo(String appName, String dataSource) {
        String url = properties.getUrl().getAmdb() + APPLICATION_SHADOW_DATABASE_PATH;
        Map<String, Object> paramMap =  new HashMap<>();
        paramMap.put("appName",appName);
        paramMap.put("pageSize",Integer.MAX_VALUE);
        paramMap.put("dataSource",dataSource);
        paramMap.put("envCode",WebPluginUtils.traceEnvCode());
        paramMap.put("tenantAppKey",WebPluginUtils.traceTenantAppKey());
        AmdbResult<List<AppShadowDatabaseDTO>> amdbResponse = AmdbHelper.builder().httpMethod(HttpMethod.GET)
                .url(url)
                .param(paramMap)
                .exception(TakinWebExceptionEnum.APPLICATION_MANAGE_THIRD_PARTY_ERROR)
                .eventName("????????????????????????")
                .list(AppShadowDatabaseDTO.class);
        List<AppShadowDatabaseDTO> data = amdbResponse.getData();
        return data;
    }

    /**
     * ????????????????????????
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
        Map<String,String> paramMap =  new HashMap<>();
        paramMap.put("appName",appName);
        paramMap.put("dataSource",dataSource);
        paramMap.put("tableUser",userName);
        paramMap.put("envCode",WebPluginUtils.traceEnvCode());
        paramMap.put("tenantAppKey",WebPluginUtils.traceTenantAppKey());
        AmdbResult<List<ApplicationBizTableDTO>> amdbResponse = AmdbHelper.builder().httpMethod(HttpMethod.GET)
                .url(url)
                .param(paramMap)
                .exception(TakinWebExceptionEnum.APPLICATION_MANAGE_THIRD_PARTY_ERROR)
                .eventName("????????????????????????")
                .list(ApplicationBizTableDTO.class);
        List<ApplicationBizTableDTO> data = amdbResponse.getData();
        return data;
    }
}
