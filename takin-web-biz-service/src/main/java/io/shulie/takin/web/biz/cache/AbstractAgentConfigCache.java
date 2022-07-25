package io.shulie.takin.web.biz.cache;

import java.util.Set;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import io.shulie.takin.web.biz.service.DistributedLock;
import io.shulie.takin.web.biz.service.impl.RedissonDistributedLock;
import io.shulie.takin.web.common.util.CommonUtil;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author shiyajian
 * create: 2020-12-17
 */
@Slf4j
public abstract class AbstractAgentConfigCache<T> implements AgentCacheSupport<T> {

    private final String cacheName;
    private final RedisTemplate redisTemplate;

    // 默认不清除redis缓存
    @Value("${takin.enable.clearDsDataConfig:false}")
    private boolean isClear;

    // 应用相关配置的失效时间
    @Value("${takin.app.config.expire:5}")
    private long app_config_expire;

    // 加锁处理
    @Value("${takin.enable.configDataLock:true}")
    private boolean configDataLock;

    @Autowired
    private DistributedLock distributedLock;

    public AbstractAgentConfigCache(String cacheName, RedisTemplate redisTemplate) {
        this.cacheName = cacheName;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public T get(String namespace) {
        if (configDataLock) {
            return getLock(namespace, 0);
        } else {
            T result = (T) redisTemplate.opsForValue().get(getCacheKey(namespace));
            if (result == null) {
                result = queryValue(namespace);
                redisTemplate.opsForValue().set(getCacheKey(namespace), result, 5, TimeUnit.MINUTES);
            }
            return result;
        }
    }

    /**
     * 1、失效的时候，直接将key给删除了,
     * 2、key5分钟失效时间
     * 引起缓存击穿,基于redis分布式锁处理,
     * 有一个线程去重构缓存，其他线程等待获取值
     * 这里暂时用这个方式
     */
    public T getLock(String namespace, int count) {
        // 增加一个计数器,防止线程一直读取不到,递归退出,1s就退出，等待下次处理
        if (count > 200) {
            log.warn("已超过递归次数,但还是未获取到值,namespace:" + namespace);
            return null;
        }
        String cacheKey = getCacheKey(namespace);
        T result = (T) redisTemplate.opsForValue().get(cacheKey);
        if (result == null) {
            // 单独走一个查询的分布式key
            String queryLockKey = "t:data:query:" + cacheKey;
            // 多等待下再去获取值,读取数据也不是那么快
            boolean isLock = distributedLock.tryLock(queryLockKey, 100L, 1000L, TimeUnit.MILLISECONDS);
            if (isLock) {
                try {
                    result = queryValue(namespace);
                    redisTemplate.opsForValue().set(cacheKey, result, app_config_expire, TimeUnit.MINUTES);
                } catch (Throwable e) {
                    log.error("数据操作失败 " + ExceptionUtils.getStackTrace(e));
                } finally {
                    distributedLock.unLockSafely(queryLockKey);
                }
            } else {
                // 等待过程中,没有获取到锁,当前线程重新处理获取一遍值,一直拿不到可完蛋
                getLock(namespace, ++count);
            }
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
        if (!isClear) {
            return;
        }
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
     * <p>
     *  TODO 具体实现 - 张天赐修改,为了编译通过
     *
     * @param namespace -
     * @return -
     */
    protected abstract T queryValue(String namespace);
}
