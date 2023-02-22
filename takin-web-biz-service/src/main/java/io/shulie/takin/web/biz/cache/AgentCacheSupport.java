package io.shulie.takin.web.biz.cache;

/**
 * @author shiyajian
 * create: 2020-12-17
 */
public interface AgentCacheSupport<T> {

    /**
     * 获得缓存
     */
    T get(String namespace);

    /**
     * 缓存失效
     */
    void evict(String namespace, boolean isPublish);
}
