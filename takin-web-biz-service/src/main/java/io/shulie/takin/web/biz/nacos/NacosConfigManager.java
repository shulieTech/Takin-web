package io.shulie.takin.web.biz.nacos;

import com.alibaba.fastjson.JSON;
import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.config.ConfigFactory;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.shulie.takin.web.biz.cache.AgentConfigCacheManager;
import io.shulie.takin.web.biz.cache.agentimpl.ApplicationApiManageAmdbCache;
import io.shulie.takin.web.biz.nacos.event.DynamicConfigRefreshEvent;
import io.shulie.takin.web.biz.nacos.event.ShadowConfigRefreshEvent;
import io.shulie.takin.web.common.enums.fastagentaccess.AgentConfigTypeEnum;
import io.shulie.takin.web.data.dao.fastagentaccess.impl.AgentConfigDAOImpl;
import io.shulie.takin.web.data.param.fastagentaccess.AgentConfigQueryParam;
import io.shulie.takin.web.data.result.application.AgentConfigDetailResult;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Slf4j
@Component
public class NacosConfigManager {

    /**
     * 业务线程池
     */
    private ThreadPoolExecutor threadPool;

    private ConfigService configService;

    @Resource
    private AgentConfigCacheManager agentConfigCacheManager;

    @Resource
    private ApplicationApiManageAmdbCache applicationApiManageAmdbCache;

    @Resource
    private AgentConfigDAOImpl agentConfigDAO;

    public NacosConfigManager() throws NacosException {
        String serverAddr = System.getProperty("nacos.serverAddr");
        if (StringUtils.isEmpty(serverAddr)) {
            return;
        }
        Properties properties = new Properties();
        properties.put(PropertyKeyConst.SERVER_ADDR, serverAddr);
        this.configService = ConfigFactory.createConfigService(properties);

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
        if (configService != null) {
            threadPool.submit(new ShadowConfigPushToNacosTask(event.getAppName()));
        }
    }

    /**
     * 刷新全局动态参数
     */
    @EventListener
    public void refreshDynamicConfigs(DynamicConfigRefreshEvent event) {
        if (configService != null) {
            threadPool.submit(new DynamicConfigPushToNacosTask(event.getAppName(), event.getTenantId(), event.getEnvCode(), event.getUserAppKey()));
        }
    }

    public void pushNacosConfigs(String appName, String configs) {
        // todo 通过appName找到nacos的dataId和group,这些数据需要通过心跳上传
    }

    private class ShadowConfigPushToNacosTask implements Runnable {

        private String appName;

        public ShadowConfigPushToNacosTask(String appName) {
            this.appName = appName;
        }

        @Override
        public void run() {
            try {
                Map<String, Object> configs = new HashMap<>();
                configs.put("datasource", agentConfigCacheManager.getShadowDb(appName));
                configs.put("job", agentConfigCacheManager.getShadowJobs(appName));
                configs.put("mq", agentConfigCacheManager.getShadowConsumer(appName));
                configs.put("whitelist", agentConfigCacheManager.getRemoteCallConfig(appName));
                configs.put("hbase", agentConfigCacheManager.getShadowHbase(appName));
                configs.put("redis", agentConfigCacheManager.getShadowServer(appName));
                configs.put("es", agentConfigCacheManager.getShadowEsServers(appName));
                configs.put("mock", agentConfigCacheManager.getGuards(appName));
                configs.put("trace_rule", applicationApiManageAmdbCache.get(appName));
                pushNacosConfigs(appName, JSON.toJSONString(configs));
            } catch (Exception e) {

            }
        }
    }

    private class DynamicConfigPushToNacosTask implements Runnable {

        private String appName;
        private Long tenantId;
        private String envCode;
        private String userAppKey;

        public DynamicConfigPushToNacosTask(String appName, Long tenantId, String envCode, String userAppKey) {
            this.appName = appName;
            this.tenantId = tenantId;
            this.envCode = envCode;
            this.userAppKey = userAppKey;
        }

        @Override
        public void run() {
            if (appName == null) {
                // 全局配置
                AgentConfigQueryParam queryParam = new AgentConfigQueryParam();
                queryParam.setEffectMechanism(1);
                queryParam.setTenantId(WebPluginUtils.SYS_DEFAULT_TENANT_ID);
                queryParam.setEnvCode(WebPluginUtils.SYS_DEFAULT_ENV_CODE);
                queryParam.setType(AgentConfigTypeEnum.GLOBAL.getVal());
                List<AgentConfigDetailResult> globalConfigList = agentConfigDAO.listByTypeAndTenantIdAndEnvCode(queryParam);
                Map<String, String> configMap = globalConfigList.stream().collect(Collectors.toMap(AgentConfigDetailResult::getEnKey, AgentConfigDetailResult::getDefaultValue));

                // todo推送到nacos
            } else {
                // 租户全局配置
                AgentConfigQueryParam queryParam = new AgentConfigQueryParam();
                queryParam.setEffectMechanism(1);
                queryParam.setTenantId(tenantId);
                queryParam.setEnvCode(envCode);
                queryParam.setType(AgentConfigTypeEnum.TENANT_GLOBAL.getVal());
                List<AgentConfigDetailResult> globalConfigList = agentConfigDAO.listByTypeAndTenantIdAndEnvCode(queryParam);
                Map<String, AgentConfigDetailResult> configMap = globalConfigList.stream().collect(Collectors.toMap(AgentConfigDetailResult::getEnKey, x -> x, (v1, v2) -> v2));

                // 应用配置
                queryParam.setUserAppKey(userAppKey);
                queryParam.setProjectName(appName);
                List<AgentConfigDetailResult> projectConfigList = agentConfigDAO.findProjectList(queryParam);
                // 3、将应用配置替换掉全局配置
                for (AgentConfigDetailResult detailResult : projectConfigList) {
                    // 如果应用配置的key在全局配置中不存在，则直接跳过
                    if (!configMap.containsKey(detailResult.getEnKey())) {
                        continue;
                    }
                    configMap.put(detailResult.getEnKey(), detailResult);
                }
                Map<String, String> stringMap = globalConfigList.stream().collect(Collectors.toMap(AgentConfigDetailResult::getEnKey, AgentConfigDetailResult::getDefaultValue));

            }
        }
    }


}
