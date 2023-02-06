package io.shulie.takin.web.biz.nacos;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.util.TypeUtils;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.common.utils.CollectionUtils;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.google.gson.Gson;
import com.pamirs.takin.common.constant.ConfigConstants;
import com.pamirs.takin.entity.domain.dto.config.WhiteListSwitchDTO;
import io.shulie.takin.web.biz.cache.AgentConfigCacheManager;
import io.shulie.takin.web.biz.cache.agentimpl.ApplicationApiManageAmdbCache;
import io.shulie.takin.web.biz.nacos.event.DynamicConfigRefreshEvent;
import io.shulie.takin.web.biz.nacos.event.ShadowConfigRefreshEvent;
import io.shulie.takin.web.biz.nacos.event.SwitchConfigRefreshEvent;
import io.shulie.takin.web.biz.pojo.bo.ConfigListQueryBO;
import io.shulie.takin.web.biz.pojo.request.fastagentaccess.AgentConfigQueryRequest;
import io.shulie.takin.web.biz.pojo.response.fastagentaccess.AgentConfigListResponse;
import io.shulie.takin.web.biz.service.fastagentaccess.AgentConfigService;
import io.shulie.takin.web.common.enums.ContextSourceEnum;
import io.shulie.takin.web.common.enums.fastagentaccess.AgentConfigTypeEnum;
import io.shulie.takin.web.data.dao.application.ApplicationDAO;
import io.shulie.takin.web.data.dao.fastagentaccess.impl.AgentConfigDAOImpl;
import io.shulie.takin.web.data.mapper.mysql.ClusterNacosConfigurationMapper;
import io.shulie.takin.web.data.model.mysql.ClusterNacosConfiguration;
import io.shulie.takin.web.data.param.application.ApplicationQueryParam;
import io.shulie.takin.web.data.param.fastagentaccess.AgentConfigQueryParam;
import io.shulie.takin.web.data.result.application.AgentConfigDetailResult;
import io.shulie.takin.web.data.result.application.ApplicationDetailResult;
import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class NacosConfigManager {

    /**
     * 业务线程池
     */
    private ThreadPoolExecutor threadPool;

    private Map<String, ConfigService> configServices = new HashMap<>();

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
        List<ClusterNacosConfiguration> nacosEntities;
        try {
            nacosEntities = clusterNacosConfigurationMapper.selectAll();
        } catch (Exception e) {
            return;
        }
        if (CollectionUtils.isEmpty(nacosEntities)) {
            return;
        }
        try {
            for (ClusterNacosConfiguration entity : nacosEntities) {
                Properties properties = new Properties();
                properties.put(PropertyKeyConst.SERVER_ADDR, entity.getNacosServerAddr());
                if (entity.getNacosNamespace() != null) {
                    properties.put(PropertyKeyConst.NAMESPACE, entity.getNacosNamespace());
                }
                ConfigService service = ConfigFactory.createConfigService(properties);
                configServices.put(entity.getClusterName(), service);
            }
        } catch (Exception e) {
            log.error("创建nacos连接时失败, 不使用nacos作为配置中心", e);
            configServices = Collections.EMPTY_MAP;
            return;
        }

        ThreadFactory factory = new ThreadFactoryBuilder().setNameFormat("nacos-config-push-thread-%d").build();
        //丢弃任务，写异常日志，但是不抛出异常
        RejectedExecutionHandler handler = (r, executor) -> log.error("Task " + r.toString() + " rejected from " + executor.toString());
        int processors = Runtime.getRuntime().availableProcessors();
        threadPool = new ThreadPoolExecutor(processors + 1, 2 * processors, 1L, TimeUnit.MINUTES,
                new LinkedBlockingQueue<Runnable>(100), factory, handler);
    }

    /**
     * 触发缓存失效
     *
     * @param event
     */
    @EventListener
    public void refreshShadowConfigs(ShadowConfigRefreshEvent event) {
        if (configServices.isEmpty()) {
            return;
        }
        String appName = event.getAppName();
        TenantCommonExt commonExt = event.getCommonExt();
        String clusterName = queryClusterName(appName, commonExt.getEnvCode(), commonExt.getTenantId());
        if (clusterName == null) {
            return;
        }
        if (!configServices.containsKey(clusterName)) {
            log.warn("不存在应用指定的集群中心nacos配置，应用名称:{}, 集群名称:{}", appName, clusterName);
        }
        threadPool.submit(new ShadowConfigsRefreshTask(appName, commonExt, configServices.get(clusterName)));
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
            threadPool.submit(new GlobalDynamicConfigRefreshTask(event.getCommonExt()));
            return;
        }
        WebPluginUtils.setTraceTenantContext(event.getCommonExt());
        // 刷新应用配置
        ApplicationDetailResult result = applicationDAO.getApplicationByTenantIdAndName(appName);
        String clusterName = result.getClusterName();
        if (result == null || clusterName == null) {
            return;
        }
        threadPool.submit(new ShadowConfigsRefreshTask(appName, event.getCommonExt(), configServices.get(clusterName)));

    }

    /**
     * 推送开关配置和白名单开关
     *
     * @param event
     */
    @EventListener
    public void refreshSwitchConfigs(SwitchConfigRefreshEvent event) {
        threadPool.submit(new SwitchConfigRefreshTask());
    }

    public void pushNacosConfigs(String dataId, String group, ConfigService configService, String configs) {
        try {
            configService.publishConfig(dataId, group, configs);
        } catch (NacosException e) {
            log.error("推送配置到nacos发生异常,dataId:{}, group:{}, content:{}", dataId, group, configs);
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
        Map<String,String> appMap = new HashMap<>();
        List<AgentConfigDetailResult> projectConfigList = agentConfigDAO.findProjectList(queryParam);
        // 3、将应用配置替换掉全局配置
        for (AgentConfigDetailResult detailResult : projectConfigList) {
            // 如果应用配置的key在全局配置中不存在，则直接跳过
            if (!configMap.containsKey(detailResult.getEnKey())) {
                continue;
            }
            appMap.put(detailResult.getEnKey(),detailResult.getDefaultValue());
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

    /**
     * 影子配置和应用动态参数配置
     */
    private class ShadowConfigsRefreshTask implements Runnable {

        private String appName;
        private TenantCommonExt commonExt;
        private ConfigService configService;

        public ShadowConfigsRefreshTask(String appName, TenantCommonExt commonExt, ConfigService configService) {
            this.appName = appName;
            this.commonExt = commonExt;
            this.configService = configService;
        }

        @Override
        public void run() {
            commonExt.setSource(ContextSourceEnum.FRONT.getCode());
            WebPluginUtils.setTraceTenantContext(commonExt);

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
            configs.put("dynamic_config", buildApplicationDynamicConfigs(appName, commonExt.getTenantId(), commonExt.getEnvCode(), commonExt.getTenantAppKey()));
            configs.put("reids-expire", agentConfigCacheManager.getAppPluginConfig(appName));
            pushNacosConfigs(appName, "APP", configService, new Gson().toJson(configs));
        }
    }

    /**
     * 全局动态参数配置刷新
     */
    private class GlobalDynamicConfigRefreshTask implements Runnable {

        private TenantCommonExt commonExt;

        public GlobalDynamicConfigRefreshTask(TenantCommonExt commonExt) {
            this.commonExt = commonExt;
        }

        @Override
        public void run() {
            // 全局配置
            WebPluginUtils.setTraceTenantContext(commonExt);
            AgentConfigQueryRequest queryRequest = new AgentConfigQueryRequest();
            queryRequest.setReadProjectConfig(false);
            List<AgentConfigListResponse> configListResponses = agentConfigService.list(queryRequest);
            Map<String, String> configMap = configListResponses.stream().collect(Collectors.toMap(AgentConfigListResponse::getEnKey, AgentConfigListResponse::getDefaultValue));
            // 全局配置每个nacos都推送
            configServices.entrySet().forEach(entry -> pushNacosConfigs("globalConfig", "GLOBAL_CONFIG", entry.getValue(), JSON.toJSONString(configMap)));
        }
    }

    /**
     * 压测开关和白名单开关配置
     */
    private class SwitchConfigRefreshTask implements Runnable {

        @Override
        public void run() {
            Map<String, Object> values = new HashMap<>();
            WhiteListSwitchDTO switchDTO = new WhiteListSwitchDTO();
            switchDTO.setConfigCode(ConfigConstants.WHITE_LIST_SWITCH);
            switchDTO.setSwitchFlagFix(agentConfigCacheManager.getAllowListSwitch());
            values.put("whiteListSwitch", switchDTO);

            values.put("globalSwithc", agentConfigCacheManager.getPressureSwitch());
            configServices.entrySet().forEach(entry -> pushNacosConfigs("clusterConfig", "CLUSTER_CONFIG", entry.getValue(), JSON.toJSONString(values)));

        }
    }

}
