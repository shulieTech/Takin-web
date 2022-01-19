package io.shulie.takin.web.biz.cache.agentimpl;

import java.util.Map;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import io.shulie.takin.web.biz.cache.AbstractAgentConfigCache;
import io.shulie.takin.web.biz.service.linkmanage.ApplicationApiService;

/**
 * @author shiyajian
 * create: 2020-12-17
 */
@Component
public class ApplicationApiManageAmdbCache extends AbstractAgentConfigCache<Map<String, List<String>>> {

    public static final String CACHE_NAME = "t:a:c:app:api";

    @Resource
    private ApplicationApiService applicationApiService;

    public ApplicationApiManageAmdbCache(@Autowired RedisTemplate redisTemplate) {
        super(CACHE_NAME, redisTemplate);
    }

    /**
     * 白名单开关，如果缓存中不存在，去数据库查
     */
    @Override
    protected Map<String, List<String>> queryValue(String namespace) {
        return applicationApiService.pullApiV1(namespace);
    }
}
