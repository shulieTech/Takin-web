package io.shulie.takin.web.biz.nacos;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.google.gson.Gson;
import com.pamirs.takin.common.constant.ConfigConstants;
import com.pamirs.takin.entity.domain.dto.config.WhiteListSwitchDTO;
import io.shulie.takin.web.biz.cache.AgentConfigCacheManager;
import io.shulie.takin.web.biz.cache.agentimpl.ApplicationApiManageAmdbCache;
import io.shulie.takin.web.biz.nacos.event.DynamicConfigRefreshEvent;
import io.shulie.takin.web.biz.nacos.event.ShadowConfigRefreshEvent;
import io.shulie.takin.web.biz.nacos.event.ShadowConfigRemoveEvent;
import io.shulie.takin.web.biz.nacos.event.SwitchConfigRefreshEvent;
import io.shulie.takin.web.biz.pojo.request.fastagentaccess.AgentConfigQueryRequest;
import io.shulie.takin.web.biz.pojo.response.fastagentaccess.AgentConfigListResponse;
import io.shulie.takin.web.biz.service.fastagentaccess.AgentConfigService;
import io.shulie.takin.web.common.enums.fastagentaccess.AgentConfigTypeEnum;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.common.util.CommonUtil;
import io.shulie.takin.web.data.dao.application.ApplicationDAO;
import io.shulie.takin.web.data.dao.fastagentaccess.impl.AgentConfigDAOImpl;
import io.shulie.takin.web.data.mapper.mysql.ClusterNacosConfigurationMapper;
import io.shulie.takin.web.data.model.mysql.ClusterNacosConfiguration;
import io.shulie.takin.web.data.param.application.ApplicationQueryParam;
import io.shulie.takin.web.data.param.fastagentaccess.AgentConfigQueryParam;
import io.shulie.takin.web.data.result.application.AgentConfigDetailResult;
import io.shulie.takin.web.data.result.application.ApplicationDetailResult;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class NacosConfigManager {

    private final Map<String, ConfigService> configServices = new HashMap<>();

    @Resource
    private AgentConfigCacheManager agentConfigCacheManager;

    @Resource
    private ApplicationApiManageAmdbCache applicationApiManageAmdbCache;

    @Resource
    private AgentConfigDAOImpl agentConfigDAO;

    @Resource
    private ClusterNacosConfigurationMapper clusterNacosConfigurationMapper;

    @Resource
    private ApplicationDAO applicationDAO;

    @Resource
    private AgentConfigService agentConfigService;

    @PostConstruct
    public void init() {
        List<ClusterNacosConfiguration> nacosClusters = Optional.ofNullable(clusterNacosConfigurationMapper.selectAll())
                .orElse(new ArrayList<>());

        for (ClusterNacosConfiguration nacosCluster : nacosClusters) {
            ConfigService nacosConfigService;

            Properties properties = new Properties();
            properties.put(PropertyKeyConst.SERVER_ADDR, nacosCluster.getNacosServerAddr());

            if (nacosCluster.getNacosNamespace() != null) {
                properties.put(PropertyKeyConst.NAMESPACE, nacosCluster.getNacosNamespace());
            }

            if (nacosCluster.getNacosUsername() != null) {
                properties.put(PropertyKeyConst.USERNAME, nacosCluster.getNacosUsername());
            }

            if (nacosCluster.getNacosPassword() != null) {
                properties.put(PropertyKeyConst.PASSWORD, nacosCluster.getNacosPassword());
            }

            try {
                nacosConfigService = ConfigFactory.createConfigService(properties);
            } catch (NacosException e) {
                log.error("NACOS: Failed to connect to the nacos server! Address={}, {}", nacosCluster.getNacosServerAddr(), e.toString());
                continue;
            }

            configServices.put(nacosCluster.getClusterName(), nacosConfigService);
        }

    }

    /**
     * 触发缓存失效
     *
     * @param event
     */
    @EventListener
    public void refreshShadowConfigs(ShadowConfigRefreshEvent event) {
        if (configServices.isEmpty()) {
            throw new TakinWebException(TakinWebExceptionEnum.NACOS_PUSH_ERROR, "推送nacos失败,没有可用的nacos服务");
        }
        String appName = event.getAppName();
        String clusterName = queryClusterName(appName, WebPluginUtils.traceEnvCode(), WebPluginUtils.traceTenantId());
        if (clusterName == null) {
            log.error("当前应用:{}没有对应的clusterName，不进行nacos同步", appName);
            throw new TakinWebException(TakinWebExceptionEnum.NACOS_PUSH_ERROR, "应用名:" + event.getAppName() + "推送nacos失败,没有对应的clusterName:" + clusterName);
        }
        if (!configServices.containsKey(clusterName)) {
            log.error("不存在应用指定的集群中心nacos配置，应用名称:{}, 集群名称:{}", appName, clusterName);
            throw new TakinWebException(TakinWebExceptionEnum.NACOS_PUSH_ERROR, "应用名:" + event.getAppName() + "推送nacos失败,应用对应的nacos集群中心" + clusterName + "不存在");
        }
        this.refreshShadowConfigs(appName, configServices.get(clusterName));
    }

    /**
     * 删除应用nacos配置
     *
     * @param event
     */
    @EventListener
    public void deleteShadowConfigs(ShadowConfigRemoveEvent event) {
        if (configServices.isEmpty()) {
            return;
        }
        String appName = event.getAppName();
        String clusterName = event.getClusterName();

        if (clusterName == null) {
            log.warn("当前应用:{}没有对应的clusterName，没有进行nacos删除", appName);
            return;
        }
        if (!configServices.containsKey(clusterName)) {
            log.warn("不存在应用指定的集群中心nacos配置，应用名称:{}, 集群名称:{}", appName, clusterName);
        }
        ConfigService configService = configServices.get(clusterName);
        this.removeConfig(configService, appName);
    }

    private void removeConfig(ConfigService configService, String appName) {
        if (configService != null) {
            try {
                int count = 0;
                while (!configService.removeConfig(appName, "APP")) {
                    count++;
                    log.warn("应用{}删除nacos配置失败,当前为第{}次删除", appName, count);
                    if (count > 3) {
                        log.error("应用{}删除nacos配置3次之后失败，放弃删除", appName);
                        throw new TakinWebException(TakinWebExceptionEnum.NACOS_PUSH_ERROR, "应用名:" + appName + "删除nacos配置3次之后失败");
                    }
                }
            } catch (NacosException e) {
                log.error("删除nacos配置出现异常", e);
                throw new TakinWebException(TakinWebExceptionEnum.NACOS_PUSH_ERROR, "应用名:" + appName + "删除nacos配置出现异常:" + e.getErrMsg());
            }
        }
    }

    /**
     * 刷新动态参数
     */
    @EventListener
    public void refreshDynamicConfigs(DynamicConfigRefreshEvent event) {
        if (configServices.isEmpty()) {
            return;
        }
        String appName = event.getAppName();
        // 刷新全局配置
        if (appName == null) {
            this.refreshGlobalDynamicConfig();
            return;
        }
        WebPluginUtils.setTraceTenantContext(event.getCommonExt());
        // 刷新应用配置
        ApplicationDetailResult result = applicationDAO.getApplicationByTenantIdAndName(appName);
        if (result == null || result.getClusterName() == null) {
            return;
        }
        this.refreshShadowConfigs(appName, configServices.get(result.getClusterName()));
    }

    /**
     * 推送开关配置和白名单开关
     *
     * @param event
     */
    @EventListener
    public void refreshSwitchConfigs(SwitchConfigRefreshEvent event) {
        this.refreshSwitchConfig();
    }

    /**
     * 把配置推送给指定数据中心的nacos集群
     *
     * @param dataId
     * @param group
     * @param configService
     * @param configs
     */
    public void pushNacosConfigs(String dataId, String group, ConfigService configService, String configs) {
        try {
            boolean result = configService.publishConfig(dataId, group, configs);
            if (!result) {
                throw new TakinWebException(TakinWebExceptionEnum.NACOS_PUSH_ERROR, "dataId:" + dataId + "推送nacos失败");
            }
        } catch (NacosException e) {
            log.error("推送配置到nacos发生异常,dataId:{}, group:{}, content:{}", dataId, group, configs, e);
            throw new TakinWebException(TakinWebExceptionEnum.NACOS_PUSH_ERROR, "dataId:" + dataId + "推送nacos出现异常:" + e.getErrMsg());
        }
    }


    /**
     * 把配置推送给所有数据中心的nacos集群
     *
     * @param dataId
     * @param group
     * @param configString
     */
    public void pushNacosConfigs(String dataId, String group, String configString) {
        for (Map.Entry<String, ConfigService> entry : configServices.entrySet()) {
            this.pushNacosConfigs(dataId, group, entry.getValue(), configString);
        }
    }

    public String queryClusterName(String appName, String envCode, Long tenantId) {
        if (configServices.isEmpty()) {
            return null;
        }
        String key = buildCacheKey(appName, envCode, tenantId);
        ApplicationQueryParam param = new ApplicationQueryParam();
        param.setApplicationName(appName);
        param.setEnvCode(envCode);
        param.setTenantId(tenantId);
        List<ApplicationDetailResult> result = applicationDAO.getApplicationList(param);
        if (result == null || result.isEmpty()) {
            return null;
        }
        return result.get(0).getClusterName();
    }

    private String buildCacheKey(String appName, String envCode, Long tenantId) {
        return String.format("%s:%s:%s", tenantId, envCode, appName);
    }

    private Map<String, String> buildApplicationDynamicConfigs(String appName, Long tenantId, String envCode, String userAppKey) {
        // 租户全局配置
        AgentConfigQueryRequest queryRequest = new AgentConfigQueryRequest();
        queryRequest.setReadProjectConfig(false);
        List<AgentConfigListResponse> globalConfigList = agentConfigService.list(queryRequest);
        Map<String, AgentConfigListResponse> configMap = globalConfigList.stream().collect(Collectors.toMap(AgentConfigListResponse::getEnKey, x -> x, (v1, v2) -> v2));

        AgentConfigQueryParam queryParam = new AgentConfigQueryParam();
        queryParam.setEffectMechanism(1);
        queryParam.setTenantId(tenantId);
        queryParam.setEnvCode(envCode);
        queryParam.setType(AgentConfigTypeEnum.TENANT_GLOBAL.getVal());
        // 应用配置
        queryParam.setUserAppKey(userAppKey);
        queryParam.setProjectName(appName);
        Map<String, String> appMap = new HashMap<>();
        List<AgentConfigDetailResult> projectConfigList = agentConfigDAO.findProjectList(queryParam);
        // 3、将应用配置替换掉全局配置
        for (AgentConfigDetailResult detailResult : projectConfigList) {
            // 如果应用配置的key在全局配置中不存在，则直接跳过
            if (!configMap.containsKey(detailResult.getEnKey())) {
                continue;
            }
            appMap.put(detailResult.getEnKey(), detailResult.getDefaultValue());
        }
        return appMap;
    }

    /**
     * 是否使用nacos做配置中心
     *
     * @return
     */
    public boolean useNacosForConfigCenter() {
        return !configServices.isEmpty();
    }

    private void refreshShadowConfigs(String appName, ConfigService configService) {

        Map<String, Object> configs = new HashMap<>();
        configs.put("datasource", agentConfigCacheManager.getShadowDb(appName));
        configs.put("job", agentConfigCacheManager.getShadowJobs(appName));
        configs.put("mq", agentConfigCacheManager.getShadowConsumer(appName));
        configs.put("whitelist", agentConfigCacheManager.getRemoteCallConfig(appName));
        configs.put("hbase", agentConfigCacheManager.getShadowHbase(appName));
        configs.put("redis", agentConfigCacheManager.getShadowServer(appName));
        configs.put("es", agentConfigCacheManager.getShadowEsServers(appName));
        configs.put("mock", agentConfigCacheManager.getGuards(appName));
        Map<String, List<String>> values = applicationApiManageAmdbCache.get(appName);
        configs.put("trace_rule", values == null ? new HashMap<>() : values);
        configs.put("dynamic_config", buildApplicationDynamicConfigs(appName, WebPluginUtils.traceTenantId(), WebPluginUtils.traceEnvCode(), WebPluginUtils.traceTenantAppKey()));
        configs.put("redis-expire", agentConfigCacheManager.getAppPluginConfig(CommonUtil.generateRedisKey(appName, "redis_expire")));
        this.pushNacosConfigs(appName, "APP", configService, new Gson().toJson(configs));
    }

    private void refreshGlobalDynamicConfig() {
        // 全局配置
        AgentConfigQueryRequest queryRequest = new AgentConfigQueryRequest();
        queryRequest.setReadProjectConfig(false);
        List<AgentConfigListResponse> configListResponses = agentConfigService.list(queryRequest);
        Map<String, String> configMap = configListResponses.stream().collect(Collectors.toMap(AgentConfigListResponse::getEnKey, AgentConfigListResponse::getDefaultValue));
        // 全局配置每个nacos都推送
        this.pushNacosConfigs("globalConfig", "GLOBAL_CONFIG", JSON.toJSONString(configMap));
    }


    private void refreshSwitchConfig() {
        Map<String, Object> values = new HashMap<>();
        WhiteListSwitchDTO switchDTO = new WhiteListSwitchDTO();
        switchDTO.setConfigCode(ConfigConstants.WHITE_LIST_SWITCH);
        switchDTO.setSwitchFlagFix(agentConfigCacheManager.getAllowListSwitch());
        values.put("whiteListSwitch", switchDTO);

        values.put("globalSwithc", agentConfigCacheManager.getPressureSwitch());
        this.pushNacosConfigs("clusterConfig", "CLUSTER_CONFIG", JSON.toJSONString(values));
    }

}
