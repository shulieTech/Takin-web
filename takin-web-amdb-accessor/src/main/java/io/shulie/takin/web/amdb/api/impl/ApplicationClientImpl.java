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

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import io.shulie.amdb.common.dto.link.entrance.ServiceInfoDTO;
import io.shulie.amdb.common.enums.EdgeTypeGroupEnum;
import io.shulie.amdb.common.enums.RpcType;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.amdb.bean.common.AmdbResult;
import io.shulie.takin.web.amdb.bean.query.application.ApplicationErrorQueryDTO;
import io.shulie.takin.web.amdb.bean.query.application.ApplicationInterfaceQueryDTO;
import io.shulie.takin.web.amdb.bean.query.application.ApplicationNodeQueryDTO;
import io.shulie.takin.web.amdb.bean.query.application.ApplicationQueryDTO;
import io.shulie.takin.web.amdb.bean.query.application.ApplicationRemoteCallQueryDTO;
import io.shulie.takin.web.amdb.bean.result.application.ApplicationDTO;
import io.shulie.takin.web.amdb.bean.result.application.ApplicationErrorDTO;
import io.shulie.takin.web.amdb.bean.result.application.ApplicationInterfaceDTO;
import io.shulie.takin.web.amdb.bean.result.application.ApplicationNodeDTO;
import io.shulie.takin.web.amdb.bean.result.application.ApplicationNodeProbeInfoDTO;
import io.shulie.takin.web.amdb.bean.result.application.ApplicationRemoteCallDTO;
import io.shulie.takin.web.amdb.util.AmdbHelper;
import io.shulie.takin.web.amdb.api.ApplicationClient;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
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
     * 节点, 探针, 统计信息
     */
    private static final String APPLICATION_NODE_PROBE_INFO = "/amdb/db/api/appInstanceStatus/queryInstanceSumInfo";

    @Autowired
    private AmdbClientProperties properties;

    @Override
    public List<ApplicationInterfaceDTO> listInterfaces(ApplicationInterfaceQueryDTO query) {
        return pageInterfaces(query).getList();
    }

    private List<ApplicationInterfaceDTO> getApplicationInterfaceDtoList(AmdbResult<List<ServiceInfoDTO>> amdbResponse) {
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
        query.setRpcType(StringUtils.join(Lists.newArrayList(String.valueOf(RpcType.TYPE_WEB_SERVER), String.valueOf(RpcType.TYPE_RPC)), ","));
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
            if (StringUtils.isEmpty(query.getAppName())){
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
//            String params = JSONUtil.toJsonStr(query);
//            String responseEntity = HttpClientUtil.sendPost(url, query);
//            if (StringUtils.isEmpty(responseEntity)) {
//                log.error("向amdb发起POST请求查询应用信息返回为空！amdbUrl=" + url + ",入参=" + params + "");
//                throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_MANAGE_THIRD_PARTY_ERROR, "向amdb发起POST请求查询应用信息返回为空！具体见日志。");
//            }
//            AmdbResult<List<ApplicationDTO>> amdbResponse = JSONUtil.toBean(responseEntity,
//                    new cn.hutool.core.lang.TypeReference<AmdbResult<List<ApplicationDTO>>>() {
//                    }, true);
//            String JSON = JSONUtil.toJsonStr(amdbResponse);
//            if (amdbResponse == null || !amdbResponse.getSuccess()) {
//                log.error("向amdb发起POST请求查询应用信息返回异常,amdbUrl=" + url + ",入参=" + params + "，响应信息：" + JSON);
//                throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_MANAGE_THIRD_PARTY_ERROR, "向amdb发起POST请求查询应用信息返回异常!具体见日志。");
//            }
//            List<ApplicationDTO> data = amdbResponse.getData();
//            if (CollectionUtils.isEmpty(data)) {
//                log.error("向amdb发起POST请求查询应用信息返回状态为成功，但应用信息数据为空！amdbUrl=" + url + ",入参=" + params + ",响应信息：" + JSON + "");
//            }
//            return PagingList.of(data, amdbResponse.getTotal());

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
        AmdbResult<ApplicationNodeProbeInfoDTO> result = AmdbHelper.newInStance().url(this.getApplicationNodeProbeInfoUrl())
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
            AmdbResult<List<ApplicationNodeDTO>> amdbResponse = AmdbHelper.newInStance().httpMethod(HttpMethod.GET)
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
//                        new cn.hutool.core.lang.TypeReference<AmdbResult<List<ApplicationRemoteCallDTO>>>() {
//                        }, true);
//                return PagingList.of(amdbResponse.getData(), amdbResponse.getTotal());
//            }

            AmdbResult<List<ApplicationRemoteCallDTO>> amdbResponse = AmdbHelper.newInStance().httpMethod(HttpMethod.POST)
                    .url(url)
                    .param(query)
                    .exception(TakinWebExceptionEnum.APPLICATION_MANAGE_THIRD_PARTY_ERROR)
                    .eventName("查询远程调用的接口信息")
                    .list(ApplicationRemoteCallDTO.class);
            return PagingList.of(amdbResponse.getData(), amdbResponse.getTotal());
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_MANAGE_THIRD_PARTY_ERROR,e.getMessage());
        }
    }
}
