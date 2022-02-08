package io.shulie.takin.web.biz.cache;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;

import io.shulie.takin.web.common.util.CommonUtil;
import io.shulie.takin.web.common.vo.agent.AgentRemoteCallVO;
import io.shulie.takin.web.common.vo.agent.AgentRemoteCallVO.RemoteCall;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.compress.utils.Lists;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author shiyajian
 * create: 2020-12-17
 */
@Slf4j
public abstract class AbstractAgentConfigCache<T> implements AgentCacheSupport<T> {

    private final String cacheName;
    private final RedisTemplate redisTemplate;

    private String WHITE_SIGN = "white";

    public AbstractAgentConfigCache(String cacheName, RedisTemplate redisTemplate) {
        this.cacheName = cacheName;
        this.redisTemplate = redisTemplate;
    }

    @Override
    public T get(String namespace) {
        T result = null;
        if(redisTemplate.hasKey(getCacheKey(namespace)) && DataType.HASH.equals(redisTemplate.type(getCacheKey(namespace)))) {
            result = (T)redisTemplate.opsForHash().entries(getCacheKey(namespace));
        }else {
            result = (T)redisTemplate.opsForValue().get(getCacheKey(namespace));
        }

        if (result == null) {
            result = queryValue(namespace);
            if(result instanceof AgentRemoteCallVO) {
                // 清除缓存
                redisTemplate.delete(getCacheKey(namespace) + ":"+ WHITE_SIGN);
                // 如果是远程调用模块，则另处理
                AgentRemoteCallVO vo = (AgentRemoteCallVO)queryValue(namespace);
                // 数据分开存储
                if (vo != null) {
                    List<RemoteCall> remoteCalls = vo.getWLists();
                    redisTemplate.opsForList().leftPushAll(getCacheKey(namespace) + ":"+ WHITE_SIGN, remoteCalls);
                    vo.setWLists(Lists.newArrayList());
                    redisTemplate.opsForValue().set(getCacheKey(namespace),vo);
                }
            }else if(result instanceof Map){
                // map类型 使用map存储
                redisTemplate.opsForHash().putAll(getCacheKey(namespace),(Map)result);
            }else {
                redisTemplate.opsForValue().set(getCacheKey(namespace), result);
            }
            return result;
        }
        if(result instanceof AgentRemoteCallVO) {
            AgentRemoteCallVO callVO  = (AgentRemoteCallVO)result;
            List<RemoteCall> fromRemoteCache = redisTemplate.opsForList().range(getCacheKey(namespace) + ":" + WHITE_SIGN, 0, -1);
            callVO.setWLists(fromRemoteCache);
            return (T)callVO;
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
    private void reset() {
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
