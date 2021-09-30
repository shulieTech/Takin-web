package io.shulie.takin.web.biz.cache;

import java.util.Set;

import javax.annotation.PostConstruct;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author shiyajian
 * create: 2020-12-17
 */
@Slf4j
public abstract class AbstractAgentConfigCache<T> implements AgentCacheSupport<T> {

    private final String cacheName;
    private final RedisTemplate redisTemplate;

    public AbstractAgentConfigCache(String cacheName,RedisTemplate redisTemplate) {
        this.cacheName = cacheName;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public T get(String userAppKey,String envCode,String namespace) {
        T result = (T)redisTemplate.opsForValue().get(getCacheKey(userAppKey,envCode,namespace));
        if (result == null) {
            result = queryValue(namespace,userAppKey,envCode);
            redisTemplate.opsForValue().set(getCacheKey(userAppKey,envCode,namespace), result);
        }
        return result;
    }

    /**
     *
     * @param userAppKey 租户标识
     * @param envCode 环境编码
     * @param namespace
     */
    @Override
    public void evict(String userAppKey,String envCode,String namespace) {
        redisTemplate.delete(getCacheKey(userAppKey,envCode,namespace));
    }

    /**
     * 项目重启之后，缓存清空下
     */
    @PostConstruct
    private void reset() {
        String beClearKey = this.cacheName + "*";
        if (!"*".equals(beClearKey)) {
            Set keys = redisTemplate.keys(beClearKey);
            if (CollectionUtils.isNotEmpty(keys)) {
                keys.forEach(key -> {
                    redisTemplate.delete(key);
                });
                log.info("清除key:{}对应的缓存成功", beClearKey);
            }
        }
    }

    /**
     *
     * @param userAppKey 租户标识
     * @param envCode 环境编码
     * @param namespace
     * @return
     */
    private String getCacheKey(String userAppKey,String envCode,String namespace) {
        String key = cacheName;
        if (namespace != null) {
            key += ":"+userAppKey+":"+envCode+":"+namespace;
        }
        return key;
    }

    /**
     * 查询值
     * @param userAppKey 租户标识
     * @param envCode 环境编码
     * @param namespace 命名空间
     * @return 值
     */
    protected abstract T queryValue(String userAppKey,String envCode,String namespace);

}
