package io.shulie.takin.web.biz.cache;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import io.shulie.takin.web.common.util.CommonUtil;
import io.shulie.takin.web.ext.util.WebPluginUtils;
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


    public AbstractAgentConfigCache(String cacheName, RedisTemplate redisTemplate) {
        this.cacheName = cacheName;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public T get(String namespace) {
        T result = (T)redisTemplate.opsForValue().get(getCacheKey(namespace));
        if (result == null) {
            result = queryValue(namespace);
            redisTemplate.opsForValue().set(getCacheKey(namespace), result,5, TimeUnit.MINUTES);
        }
        return result;
    }

    /**
     * @param namespace
     */
    @Override
    public void evict(String namespace) {
        redisTemplate.delete(getCacheKey(namespace));
    }

    /**
     * 项目重启之后，缓存清空下
     */
    @PostConstruct
    public void reset() {
        String beClearKey = this.cacheName + "*";
        if (!"*".equals(beClearKey)) {
            Set<String> keys = redisTemplate.keys(beClearKey);
            if (CollectionUtils.isNotEmpty(keys)) {
                keys.forEach(redisTemplate::delete);
                log.info("清除key:{}对应的缓存成功", beClearKey);
            }
        }
    }

    /**
     * @param namespace
     * @return
     */
    private String getCacheKey(String namespace) {
        return CommonUtil.generateRedisKey(cacheName,
            WebPluginUtils.traceTenantCode(), WebPluginUtils.traceEnvCode(), namespace);

    }

    /**
     * 查询值
     *
     * @param userAppKey 租户标识
     * @param envCode    环境编码
     * @param namespace  命名空间
     * @return 值
     */
    protected T queryValue(String userAppKey, String envCode, String namespace) {
        // TODO 具体实现 - 张天赐修改,为了编译通过
        return null;
    }

    /**
     * 新版本貌似用上面的方法替代了
     *
     *  TODO 具体实现 - 张天赐修改,为了编译通过
     *
     * @param namespace -
     * @return -
     */
    protected abstract T queryValue(String namespace);
}
