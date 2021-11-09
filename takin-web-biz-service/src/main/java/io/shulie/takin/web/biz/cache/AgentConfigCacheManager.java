package io.shulie.takin.web.biz.cache;

import java.util.Collections;
import java.util.List;

import com.pamirs.takin.common.constant.AppSwitchEnum;
import com.pamirs.takin.entity.domain.dto.ApplicationSwitchStatusDTO;
import com.pamirs.takin.entity.domain.entity.simplify.TShadowJobConfig;
import com.pamirs.takin.entity.domain.vo.dsmanage.DsAgentVO;
import com.pamirs.takin.entity.domain.vo.dsmanage.DsServerVO;
import com.pamirs.takin.entity.domain.vo.guardmanage.LinkGuardVo;
import io.shulie.takin.web.biz.agent.vo.ShadowConsumerVO;
import io.shulie.takin.web.biz.cache.agentimpl.AllowListSwitchConfigAgentCache;
import io.shulie.takin.web.biz.cache.agentimpl.GuardConfigAgentCache;
import io.shulie.takin.web.biz.cache.agentimpl.PressureSwitchConfigAgentCache;
import io.shulie.takin.web.biz.cache.agentimpl.RemoteCallConfigAgentCache;
import io.shulie.takin.web.biz.cache.agentimpl.ShadowConsumerConfigAgentCache;
import io.shulie.takin.web.biz.cache.agentimpl.ShadowDbConfigAgentCache;
import io.shulie.takin.web.biz.cache.agentimpl.ShadowEsServerConfigAgentCache;
import io.shulie.takin.web.biz.cache.agentimpl.ShadowHbaseConfigAgentCache;
import io.shulie.takin.web.biz.cache.agentimpl.ShadowJobConfigAgentCache;
import io.shulie.takin.web.biz.cache.agentimpl.ShadowKafkaClusterConfigAgentCache;
import io.shulie.takin.web.biz.cache.agentimpl.ShadowServerConfigAgentCache;
import io.shulie.takin.web.biz.pojo.output.application.ShadowServerConfigurationOutput;
import io.shulie.takin.web.biz.service.ApplicationService;
import io.shulie.takin.web.common.vo.agent.AgentRemoteCallVO;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * @author shiyajian
 * create: 2020-12-17
 */
@Component
public class AgentConfigCacheManager {

    @Autowired
    private AllowListSwitchConfigAgentCache allowListSwitchConfigCache;

    @Autowired
    private ShadowServerConfigAgentCache shadowServerConfigCache;

    @Autowired
    private ShadowDbConfigAgentCache shadowDbConfigCache;

    @Autowired
    private ShadowJobConfigAgentCache shadowJobConfigCache;

    @Autowired
    private GuardConfigAgentCache guardConfigCache;

    @Autowired
    private RemoteCallConfigAgentCache remoteCallConfigAgentCache;

    @Autowired
    private PressureSwitchConfigAgentCache pressureSwitchConfigCache;

    @Autowired
    private ShadowConsumerConfigAgentCache shadowConsumerConfigAgentCache;

    @Autowired
    private ShadowEsServerConfigAgentCache shadowEsServerConfigAgentCache;

    @Autowired
    private ShadowKafkaClusterConfigAgentCache shadowKafkaClusterConfigAgentCache;

    @Autowired
    private ShadowHbaseConfigAgentCache shadowHbaseConfigAgentCache;

    @Autowired
    private ApplicationService applicationService;

    /**
     * 获得白名单开关的缓存结果
     */
    public boolean getAllowListSwitch() {
        return allowListSwitchConfigCache.get(null);
    }

    /**
     * 开关缓存清空，下次查询时候重新加载
     */
    public void evictAllowListSwitch() {
        allowListSwitchConfigCache.evict(null);
    }

    public List<TShadowJobConfig> getShadowJobs(String appName) {
        return shadowJobConfigCache.get(appName);
    }

    public void evictShadowJobs(String appName) {
        shadowJobConfigCache.evict(appName);
    }

    public List<ShadowServerConfigurationOutput> getShadowServer(String appName) {
        return shadowServerConfigCache.get(appName);
    }

    public void evictShadowServer(String appName) {
        shadowServerConfigCache.evict(appName);
    }

    public List<DsAgentVO> getShadowDb(String appName) {
        return shadowDbConfigCache.get(appName);
    }

    public void evictShadowDb(String appName) {
        shadowDbConfigCache.evict(appName);
    }

    public List<LinkGuardVo> getGuards(String appName) {
        return guardConfigCache.get(appName);
    }

    public void evictGuards(String appName) {
        guardConfigCache.evict(appName);
    }

    /**
     * 远程调用
     */
    public void evictRecallCalls(String appName) {
        remoteCallConfigAgentCache.evict(appName);
    }

    public ApplicationSwitchStatusDTO getPressureSwitch() {
        ApplicationSwitchStatusDTO applicationSwitchStatusDTO = pressureSwitchConfigCache.get(null);
        String silenceSwitch = applicationService.getUserSilenceSwitchStatusForVo(WebPluginUtils.getCustomerId());
        String silenceSwitchOn = AppSwitchEnum.OPENED.getCode().equals(silenceSwitch) ?
                AppSwitchEnum.CLOSED.getCode() : AppSwitchEnum.OPENED.getCode();
        applicationSwitchStatusDTO.setSilenceSwitchOn(silenceSwitchOn);
        return applicationSwitchStatusDTO;
    }

    public void evictPressureSwitch() {
        pressureSwitchConfigCache.evict(null);
    }

    public List<ShadowConsumerVO> getShadowConsumer(String appName) {
        return shadowConsumerConfigAgentCache.get(appName);
    }

    public void evictShadowConsumer(String appName) {
        shadowConsumerConfigAgentCache.evict(appName);
    }

    /**
     * 获取影子消费者配置业务逻辑
     *
     * @param appName 应用名称
     * @return
     */
    public List<DsServerVO> getShadowEsServers(String appName) {
        return shadowEsServerConfigAgentCache.get(appName);
    }

    /**
     * 清空缓存，从新加载
     *
     * @param appName 应用名称
     */
    public void evictShadowEsServers(String appName) {
        shadowEsServerConfigAgentCache.evict(appName);
    }

    /**
     * 清空缓存，从新加载
     *
     * @param appName 应用名称
     */
    public void evictShadowKafkaCluster(String appName) {
        shadowKafkaClusterConfigAgentCache.evict(appName);
    }

    /**
     * 获取kafka影子配置
     *
     * @return
     */
    public List<DsServerVO> getShadowKafkaCluster(String appName) {
        return shadowKafkaClusterConfigAgentCache.get(appName);
    }

    /**
     * 清空影子消费者配置业务逻辑
     *
     * @param applicationName 应用名称
     * @return
     */
    public void evictShadowHbase(String applicationName) {
        shadowHbaseConfigAgentCache.evict(applicationName);
    }

    /**
     * 获取影子消费者配置业务逻辑
     *
     * @param applicationName 应用名称
     * @return
     */
    public List<DsServerVO> getShadowHbase(String applicationName) {
        List<DsServerVO> list = shadowHbaseConfigAgentCache.get(applicationName);
        return CollectionUtils.isEmpty(list) ? Collections.emptyList() : list;
    }

    /**
     * 获取影子消费者配置业务逻辑
     *
     * @param appName 应用名称
     * @return
     */
    public AgentRemoteCallVO getRemoteCallConfig(String appName) {
        return remoteCallConfigAgentCache.get(appName);
    }
}

