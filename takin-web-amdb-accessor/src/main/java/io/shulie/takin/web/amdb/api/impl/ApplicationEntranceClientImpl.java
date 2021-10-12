package io.shulie.takin.web.amdb.api.impl;

import java.util.List;

import io.shulie.amdb.common.dto.link.entrance.ServiceInfoDTO;
import io.shulie.amdb.common.dto.link.topology.LinkTopologyDTO;
import io.shulie.amdb.common.enums.RpcType;
import io.shulie.amdb.common.request.link.ServiceQueryParam;
import io.shulie.amdb.common.request.link.TopologyQueryParam;
import io.shulie.takin.web.amdb.bean.common.AmdbResult;
import io.shulie.takin.web.amdb.bean.common.EntranceTypeInfo;
import io.shulie.takin.web.amdb.util.AmdbHelper;
import io.shulie.takin.web.amdb.util.EntranceTypeUtils;
import io.shulie.takin.web.amdb.api.ApplicationEntranceClient;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.takin.properties.AmdbClientProperties;
import org.springframework.stereotype.Component;

/**
 * @author shiyajian
 * create: 2020-12-29
 */
@Component
@Slf4j
public class ApplicationEntranceClientImpl implements ApplicationEntranceClient {

    //public static final String APPLICATION_ENTRANCES_PATH = "/amdb/link/getEntranceList";
    public static final String APPLICATION_ENTRANCES_PATH = "/amdb/link/getServiceList";

    public static final String APPLICATION_ENTRANCES_TOPOLOGY_PATH = "/amdb/link/getLinkTopology";

    public static final String APPLICATION_ENTRANCES_UNKNOWN_UPDATE_TO_OUTER = "/amdb/link/updateUnKnowNode";

    @Autowired
    private AmdbClientProperties properties;

    @Override
    public List<ServiceInfoDTO> getApplicationEntrances(String applicationName, String entranceType) {
        String url = properties.getUrl().getAmdb() + APPLICATION_ENTRANCES_PATH;
        ServiceQueryParam entranceQueryParam = new ServiceQueryParam();
        // 获取rpcType
        if(StringUtils.isNotBlank(entranceType)){
            EntranceTypeInfo info = EntranceTypeUtils.getRpcType(entranceType);
            entranceQueryParam.setRpcType(info.getRpcType());
            if(StringUtils.isNotBlank(info.getMiddlewareName())) {
                entranceQueryParam.setMiddlewareName(info.getMiddlewareName());
            }
        }else{
            //查询所有的type
            entranceQueryParam.setRpcType("");
        }
        entranceQueryParam.setAppName(applicationName);
        entranceQueryParam.setUserAppKey(WebPluginUtils.getTenantAppKey());
        entranceQueryParam.setEnvCode(WebPluginUtils.getEnvCode());
        try {
            AmdbResult<List<ServiceInfoDTO>> amdbResponse = AmdbHelper.builder().url(url)
                .param(entranceQueryParam)
                .eventName("查询入口信息")
                .exception(TakinWebExceptionEnum.APPLICATION_ENTRANCE_THIRD_PARTY_ERROR)
                .list(ServiceInfoDTO.class);
            return amdbResponse.getData();
        } catch (Exception e) {
            throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_ENTRANCE_THIRD_PARTY_ERROR,e.getMessage(),e);
        }
    }

    @Override
    public LinkTopologyDTO getApplicationEntrancesTopology(String applicationName, String linkId, String serviceName,
        String method, String rpcType, String extend) {
        String url = properties.getUrl().getAmdb() + APPLICATION_ENTRANCES_TOPOLOGY_PATH;
        TopologyQueryParam topologyQueryParam = new TopologyQueryParam();
        topologyQueryParam.setAppName(applicationName);
        if (method != null) {
            topologyQueryParam.setMethod(method);
        }
        if (rpcType != null) {
            topologyQueryParam.setRpcType(rpcType);
        }
        if (serviceName != null) {
            topologyQueryParam.setServiceName(serviceName);
        }
        if (extend != null) {
            topologyQueryParam.setExtend(extend);
        }
        topologyQueryParam.setUserAppKey(WebPluginUtils.getTenantAppKey());
        topologyQueryParam.setEnvCode("envCode");
        try {
            //            String responseEntity = HttpClientUtil.sendGet(url, topologyQueryParam);
            //            if (StringUtils.isBlank(responseEntity)) {
            //                log.error("前往amdb查询拓扑图信息返回异常,请求地址：{}，请求参数：{}", url, JSON.toJSONString(topologyQueryParam));
            //                return null;
            //            }
            //            AmdbResult<LinkTopologyDTO> amdbResponse = JSONUtil.toBean(responseEntity,
            //                new TypeReference<AmdbResult<LinkTopologyDTO>>() {}, true);
            //            if (amdbResponse == null || !amdbResponse.getSuccess()) {
            //                log.error("前往amdb查询拓扑图信息返回异常,请求地址：{}，请求参数：{}", url, JSON.toJSONString(topologyQueryParam));
            //                return null;
            //            }
            AmdbResult<LinkTopologyDTO> amdbResponse = AmdbHelper.builder().url(url)
                .param(topologyQueryParam)
                .eventName("查询拓扑图信息")
                .exception(TakinWebExceptionEnum.APPLICATION_ENTRANCE_THIRD_PARTY_ERROR)
                .one(LinkTopologyDTO.class);

            LinkTopologyDTO data = amdbResponse.getData();
            if (data == null) {
                return data;
            }
            data.getNodes().forEach(node -> {
                if (node.getNodeName() == null) {
                    node.setNodeName("");
                }
            });
            return data;
        } catch (Exception e) {
            throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_ENTRANCE_THIRD_PARTY_ERROR,e.getMessage(),e);
        }
    }

    @Override
    public Boolean updateUnknownNodeToOuter(String applicationName, String linkId, String serviceName, String method,
        String rpcType, String extend, String nodeId) {
        String url = properties.getUrl().getAmdb() + APPLICATION_ENTRANCES_UNKNOWN_UPDATE_TO_OUTER;
        TopologyQueryParam topologyQueryParam = new TopologyQueryParam();
        topologyQueryParam.setAppName(applicationName);
        if (method != null) {
            topologyQueryParam.setMethod(method);
        }
        if (rpcType != null) {
            topologyQueryParam.setRpcType(rpcType);
        }
        if (serviceName != null) {
            topologyQueryParam.setServiceName(serviceName);
        }
        if (extend != null) {
            topologyQueryParam.setExtend(extend);
        }
        topologyQueryParam.setId(nodeId);
        topologyQueryParam.setUserAppKey(WebPluginUtils.getTenantAppKey());
        topologyQueryParam.setEnvCode("envCode");
        try {
            //            String responseEntity = HttpClientUtil.sendGet(url, topologyQueryParam);
            //            if (StringUtils.isBlank(responseEntity)) {
            //                log.error("前往amdb更新未知应用返回异常,请求地址：{}，请求参数：{}", url, JSON.toJSONString(topologyQueryParam));
            //                return null;
            //            }
            //
            //            AmdbResult amdbResponse = JSONUtil.toBean(responseEntity,
            //                new TypeReference<AmdbResult>() {}, true);
            //
            //            if (amdbResponse == null || !amdbResponse.getSuccess()) {
            //                log.error("前往amdb更新未知应用返回异常,请求地址：{}，请求参数：{}", url, JSON.toJSONString(topologyQueryParam));
            //                return null;
            //            }
            AmdbResult<Object> amdbResponse = AmdbHelper.builder().url(url)
                .param(topologyQueryParam)
                .eventName("更新未知应用")
                .exception(TakinWebExceptionEnum.APPLICATION_ENTRANCE_THIRD_PARTY_ERROR)
                .one(Object.class);


            return amdbResponse.getSuccess();
        } catch (Exception e) {
            throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_ENTRANCE_THIRD_PARTY_ERROR,e.getMessage(),e);
        }
    }

    @Override
    public List<ServiceInfoDTO> getMqTopicGroups(String applicationName) {
        String url = properties.getUrl().getAmdb() + APPLICATION_ENTRANCES_PATH;
        ServiceQueryParam entranceQueryParam = new ServiceQueryParam();
        entranceQueryParam.setRpcType(RpcType.TYPE_MQ + "");
        entranceQueryParam.setAppName(applicationName);
        entranceQueryParam.setUserAppKey(WebPluginUtils.getTenantAppKey());
        entranceQueryParam.setEnvCode(WebPluginUtils.getEnvCode());
        try {
            AmdbResult<List<ServiceInfoDTO>> amdbResponse = AmdbHelper.builder().url(url)
                .param(entranceQueryParam)
                .eventName("查询MQ消费者")
                .exception(TakinWebExceptionEnum.APPLICATION_SHADOW_THIRD_PARTY_ERROR)
                .list(ServiceInfoDTO.class);

            return amdbResponse.getData();
        } catch (Exception e) {
            throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_SHADOW_THIRD_PARTY_ERROR,e.getMessage(),e);
        }
    }
}
