package io.shulie.takin.web.amdb.api.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONObject;
import com.google.common.collect.Lists;
import io.shulie.amdb.common.dto.link.entrance.ServiceInfoDTO;
import io.shulie.amdb.common.dto.link.topology.LinkTopologyDTO;
import io.shulie.amdb.common.enums.RpcType;
import io.shulie.amdb.common.request.link.ServiceQueryParam;
import io.shulie.amdb.common.request.link.TopologyQueryParam;
import io.shulie.takin.web.amdb.api.ApplicationEntranceClient;
import io.shulie.takin.web.amdb.bean.common.AmdbResult;
import io.shulie.takin.web.amdb.bean.common.EntranceTypeInfo;
import io.shulie.takin.web.amdb.bean.query.application.QueryMetricsFromAMDB;
import io.shulie.takin.web.amdb.bean.query.application.AmdbTenantDTO;
import io.shulie.takin.web.amdb.bean.query.application.TempTopologyQuery1;
import io.shulie.takin.web.amdb.bean.query.application.TempTopologyQuery2;
import io.shulie.takin.web.amdb.util.AmdbHelper;
import io.shulie.takin.web.amdb.util.EntranceTypeUtils;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.takin.properties.AmdbClientProperties;
import org.springframework.http.HttpMethod;
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
    public static final String APPLICATION_ENTRANCES_TOPOLOGY_PATH_TEMP = "/amdb/link/getLinkTopologyForTemp";

    public static final String APPLICATION_ENTRANCES_UNKNOWN_UPDATE_TO_OUTER = "/amdb/link/updateUnKnowNode";

    public static final String QUERY_TEMP_ACTIVITY_METRICS_STEP1 = "/amdb/db/api/metrics/entranceFromChickHouse";
    public static final String QUERY_TEMP_ACTIVITY_METRICS_STEP2 = "/amdb/db/api/metrics/metricFromChickHouse";
    public static final String QUERY_METRICS = "/amdb/db/api/metrics/metricFromInfluxdb";


    @Autowired
    private AmdbClientProperties properties;

    @Override
    public List<ServiceInfoDTO> getApplicationEntrances(String applicationName, String entranceType, String serviceName, int currentPage, int pageSize) {
        String url = properties.getUrl().getAmdb() + APPLICATION_ENTRANCES_PATH;
        ServiceQueryParam entranceQueryParam = new ServiceQueryParam();
        // 获取rpcType
        if (StringUtils.isNotBlank(entranceType)) {
            EntranceTypeInfo info = EntranceTypeUtils.getRpcType(entranceType);
            entranceQueryParam.setRpcType(info.getRpcType());
            if (StringUtils.isNotBlank(info.getMiddlewareName())) {
                entranceQueryParam.setMiddlewareName(info.getMiddlewareName());
            }
        } else {
            //查询所有的type
            entranceQueryParam.setRpcType("");
        }
        entranceQueryParam.setAppName(applicationName);
        entranceQueryParam.setTenantAppKey(WebPluginUtils.traceTenantAppKey());
        entranceQueryParam.setEnvCode(WebPluginUtils.traceEnvCode());
        entranceQueryParam.setCurrentPage(currentPage);
        entranceQueryParam.setPageSize(pageSize);
        if (StringUtils.isNotBlank(serviceName)) {
            entranceQueryParam.setServiceName(serviceName);
        }
        try {
            AmdbResult<List<ServiceInfoDTO>> amdbResponse = AmdbHelper.builder().url(url)
                .param(entranceQueryParam)
                .eventName("查询入口信息")
                .exception(TakinWebExceptionEnum.APPLICATION_ENTRANCE_THIRD_PARTY_ERROR)
                .list(ServiceInfoDTO.class);
            return amdbResponse.getData();
        } catch (Exception e) {
            throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_ENTRANCE_THIRD_PARTY_ERROR, e.getMessage(),
                e);
        }
    }

    @Override
    public String queryMetricsFromAMDB1(TempTopologyQuery1 tempTopologyQuery1) {
        String url = properties.getUrl().getAmdb() + QUERY_TEMP_ACTIVITY_METRICS_STEP1;
        this.populateTenantDTO(tempTopologyQuery1);
        try {
            AmdbResult<String> amdbResponse = AmdbHelper.builder().url(url)
                .httpMethod(HttpMethod.POST)
                .param(tempTopologyQuery1)
                .eventName("查询临时业务活动指标step1")
                .exception(TakinWebExceptionEnum.APPLICATION_QUERY_TEMP_ACTIVITY_METRICS_STEP1_ERROR)
                .one(String.class);

            String data = amdbResponse.getData();
            return data;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_QUERY_TEMP_ACTIVITY_METRICS_STEP1_ERROR,
                e.getMessage());
        }
    }

    @Override
    public JSONObject queryMetricsFromAMDB2(TempTopologyQuery2 tempTopologyQuery2) {
        String url = properties.getUrl().getAmdb() + QUERY_TEMP_ACTIVITY_METRICS_STEP2;
        this.populateTenantDTO(tempTopologyQuery2);
        try {
            AmdbResult<JSONObject> amdbResponse = AmdbHelper.builder().url(url)
                .httpMethod(HttpMethod.POST)
                .param(tempTopologyQuery2)
                .eventName("查询临时业务活动指标step2")
                .exception(TakinWebExceptionEnum.APPLICATION_QUERY_TEMP_ACTIVITY_METRICS_STEP2_ERROR)
                .one(JSONObject.class);

            JSONObject data = amdbResponse.getData();
            return data;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_QUERY_TEMP_ACTIVITY_METRICS_STEP2_ERROR,
                e.getMessage());
        }
    }

    @Override
    public JSONObject queryMetrics(QueryMetricsFromAMDB queryMetricsFromAMDB) {
        String url = properties.getUrl().getAmdb() + QUERY_METRICS;

        try {
            AmdbResult<JSONObject> amdbResponse = AmdbHelper.builder().url(url)
                    .httpMethod(HttpMethod.POST)
                    .param(queryMetricsFromAMDB)
                    .eventName("查询指标")
                    .exception(TakinWebExceptionEnum.APPLICATION_QUERY_METRICS_ERROR)
                    .one(JSONObject.class);

            JSONObject data = amdbResponse.getData();
            return data;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_QUERY_METRICS_ERROR, e.getMessage());
        }
    }

    ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
    @Override
    public List<JSONObject> queryBatchMetrics(QueryMetricsFromAMDB queryMetricsFromAMDB) {
        String url = properties.getUrl().getAmdb() + QUERY_METRICS;
        AmdbResult<List<JSONObject>> amdbResponseObj = new AmdbResult<>();
        amdbResponseObj.setData(new ArrayList<>());
        List<List<String>> resultList = Lists.partition(queryMetricsFromAMDB.getEagleIds(), 300);
        List<CompletableFuture<Void>> futures = resultList.stream().map(result -> {
            Runnable runnableTask = () -> {
                QueryMetricsFromAMDB tempObj = BeanUtil.copyProperties(queryMetricsFromAMDB,QueryMetricsFromAMDB.class, "eagleIds");
                tempObj.setEagleIds(result);
                AmdbResult<List<JSONObject>> amdbResponse = AmdbHelper.builder().url(url)
                        .httpMethod(HttpMethod.POST)
                        .param(tempObj)
                        .eventName("批量查询指标")
                        .exception(TakinWebExceptionEnum.APPLICATION_QUERY_METRICS_ERROR)
                        .list(JSONObject.class);
                amdbResponseObj.getData().addAll(amdbResponse.getData());
            };
            return CompletableFuture.runAsync(runnableTask, cachedThreadPool);
        }).collect(Collectors.toList());
        try {
            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();
            return amdbResponseObj.getData();
        }catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_QUERY_METRICS_ERROR, e.getMessage());
        }
    }

    @Override
    public LinkTopologyDTO getApplicationEntrancesTopology(boolean tempActivity, String applicationName, String linkId,
        String serviceName,
        String method, String rpcType, String extend) {
        String url;
        if (tempActivity) {
            url = properties.getUrl().getAmdb() + APPLICATION_ENTRANCES_TOPOLOGY_PATH_TEMP;
        } else {
            url = properties.getUrl().getAmdb() + APPLICATION_ENTRANCES_TOPOLOGY_PATH;
        }
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
        topologyQueryParam.setTenantAppKey(WebPluginUtils.traceTenantAppKey());
        topologyQueryParam.setEnvCode(WebPluginUtils.traceEnvCode());
        try {
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
            throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_ENTRANCE_THIRD_PARTY_ERROR, e.getMessage(),
                e);
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
        topologyQueryParam.setTenantAppKey(WebPluginUtils.traceTenantAppKey());
        topologyQueryParam.setEnvCode(WebPluginUtils.traceEnvCode());
        try {
            AmdbResult<Object> amdbResponse = AmdbHelper.builder().url(url)
                .param(topologyQueryParam)
                .eventName("更新未知应用")
                .exception(TakinWebExceptionEnum.APPLICATION_ENTRANCE_THIRD_PARTY_ERROR)
                .one(Object.class);
            return amdbResponse.getSuccess();
        } catch (Exception e) {
            throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_ENTRANCE_THIRD_PARTY_ERROR, e.getMessage(),
                e);
        }
    }

    @Override
    public List<ServiceInfoDTO> getMqTopicGroups(String applicationName) {
        String url = properties.getUrl().getAmdb() + APPLICATION_ENTRANCES_PATH;
        ServiceQueryParam entranceQueryParam = new ServiceQueryParam();
        entranceQueryParam.setRpcType(RpcType.TYPE_MQ + "");
        entranceQueryParam.setAppName(applicationName);
        entranceQueryParam.setTenantAppKey(WebPluginUtils.traceTenantAppKey());
        entranceQueryParam.setEnvCode(WebPluginUtils.traceEnvCode());
        try {
            AmdbResult<List<ServiceInfoDTO>> amdbResponse = AmdbHelper.builder().url(url)
                .param(entranceQueryParam)
                .eventName("查询MQ消费者")
                .exception(TakinWebExceptionEnum.APPLICATION_SHADOW_THIRD_PARTY_ERROR)
                .list(ServiceInfoDTO.class);
            return amdbResponse.getData();
        } catch (Exception e) {
            throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_SHADOW_THIRD_PARTY_ERROR, e.getMessage(), e);
        }
    }

    private void populateTenantDTO(AmdbTenantDTO amdbTenantDTO) {
        if (Objects.nonNull(amdbTenantDTO)) {
            amdbTenantDTO.setTenantAppKey(WebPluginUtils.traceTenantAppKey());
            amdbTenantDTO.setEnvCode(WebPluginUtils.traceEnvCode());
        }
    }
}
