package io.shulie.takin.web.common.constant;

/**
 * @author liuchuan
 * @date 2021/11/19 3:49 下午
 */
public interface CacheConstants {

    /**
     * 缓存键，agentConfig
     */
    String CACHE_KEY_AGENT_CONFIG = "agentConfig";

    /**
     * 生成带有租户信息的缓存键
     */
    String CACHE_KEY_GENERATOR_BY_TENANT_INFO = "cacheKeyGeneratorByTenantInfo";

    /**
     * 缓存键, agent应用节点
     */
    String CACHE_KEY_AGENT_APPLICATION_NODE = "agentApplicationNode";

}
