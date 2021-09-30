package io.shulie.takin.web.biz.cache;

import java.util.Collections;
import java.util.List;

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
import io.shulie.takin.web.common.vo.agent.AgentRemoteCallVO;
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

    /**
     * 获得白名单开关的缓存结果
     */
    public boolean getAllowListSwitch(String userAppKey,String envCode) {
        return allowListSwitchConfigCache.get(userAppKey,envCode,null);
    }

    /**
     * 开关缓存清空，下次查询时候重新加载
     */
    public void evictAllowListSwitch(String userAppKey,String envCode) {
        allowListSwitchConfigCache.evict(userAppKey,envCode,null);
    }

    public List<TShadowJobConfig> getShadowJobs(String userAppKey,String envCode,String appName) {
        return shadowJobConfigCache.get(userAppKey, envCode, appName);
    }

    public void evictShadowJobs(String userAppKey,String envCode,String appName) {
        shadowJobConfigCache.evict(userAppKey, envCode, appName);
    }

    public List<ShadowServerConfigurationOutput> getShadowServer(String userAppKey,String envCode,String appName) {
        return shadowServerConfigCache.get(userAppKey, envCode, appName);
    }

    public void evictShadowServer(String userAppKey,String envCode,String appName) {
        shadowServerConfigCache.evict(userAppKey, envCode, appName);
    }

    public List<DsAgentVO> getShadowDb(String userAppKey,String envCode,String appName) {
        return shadowDbConfigCache.get(userAppKey, envCode, appName);
    }

    public void evictShadowDb(String userAppKey,String envCode,String appName) {

        shadowDbConfigCache.evict(userAppKey, envCode, appName);
    }

    public List<LinkGuardVo> getGuards(String userAppKey,String envCode,String appName) {
        return guardConfigCache.get(userAppKey, envCode, appName);
    }

    public void evictGuards(String userAppKey,String envCode,String appName) {
        guardConfigCache.evict(userAppKey, envCode, appName);
    }

    /**
     * 远程调用
     */
    public void evictRecallCalls(String userAppKey,String envCode,String appName) {
        remoteCallConfigAgentCache.evict(userAppKey, envCode, appName);
    }

    public ApplicationSwitchStatusDTO getPressureSwitch(String userAppKey,String envCode) {
        return pressureSwitchConfigCache.get(userAppKey,envCode,null);
    }

    public void evictPressureSwitch(String userAppKey,String envCode) {
        pressureSwitchConfigCache.evict(userAppKey,envCode,null);
    }

    public List<ShadowConsumerVO> getShadowConsumer(String userAppKey,String envCode,String appName) {
        return shadowConsumerConfigAgentCache.get(userAppKey, envCode, appName);
    }

    public void evictShadowConsumer(String userAppKey,String envCode,String appName) {
        shadowConsumerConfigAgentCache.evict(userAppKey, envCode, appName);
    }

    /**
     * 获取影子消费者配置业务逻辑
     *
     * @param appName 应用名称
     * @return
     */
    public List<DsServerVO> getShadowEsServers(String userAppKey,String envCode,String appName) {
        return shadowEsServerConfigAgentCache.get(userAppKey, envCode, appName);
    }

    /**
     * 清空缓存，从新加载
     *
     * @param appName 应用名称
     * @param userAppKey 租户标识
     * @param envCode 环境编码
     */
    public void evictShadowEsServers(String userAppKey,String envCode,String appName) {
        shadowEsServerConfigAgentCache.evict(userAppKey, envCode, appName);
    }

    /**
     * 清空缓存，从新加载
     *
     * @param appName 应用名称
     */
    public void evictShadowKafkaCluster(String userAppKey,String envCode,String appName) {
        shadowKafkaClusterConfigAgentCache.evict(userAppKey, envCode, appName);
    }

    /**
     * 获取kafka影子配置
     *
     * @return
     */
    public List<DsServerVO> getShadowKafkaCluster(String userAppKey,String envCode,String appName) {
        return shadowKafkaClusterConfigAgentCache.get(userAppKey, envCode, appName);
    }

    /**
     * 清空影子消费者配置业务逻辑
     *
     * @param applicationName 应用名称
     * @param userAppKey 租户标识
     * @param envCode 环境编码
     * @return
     */
    public void evictShadowHbase(String userAppKey,String envCode,String applicationName) {
        shadowHbaseConfigAgentCache.evict(userAppKey, envCode, applicationName);
    }

    /**
     * 获取影子消费者配置业务逻辑
     * @param userAppKey 租户标识
     * @param envCode 环境编码
     * @param applicationName 应用名称
     * @return
     */
    public List<DsServerVO> getShadowHbase(String userAppKey,String envCode,String applicationName) {
        List<DsServerVO> list = shadowHbaseConfigAgentCache.get(userAppKey, envCode, applicationName);
        return CollectionUtils.isEmpty(list) ? Collections.emptyList() : list;
    }

    /**
     * 获取影子消费者配置业务逻辑
     * @param envCode 环境编码
     * @param userAppKey 租户标识
     * @param appName 应用名称
     * @return
     */
    public AgentRemoteCallVO getRemoteCallConfig(String userAppKey,String envCode,String appName) {
        return remoteCallConfigAgentCache.get(userAppKey, envCode, appName);
    }
}

