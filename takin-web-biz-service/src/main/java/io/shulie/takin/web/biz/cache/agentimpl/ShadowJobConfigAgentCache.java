package io.shulie.takin.web.biz.cache.agentimpl;

import java.util.List;

import com.pamirs.takin.entity.domain.entity.simplify.TShadowJobConfig;
import io.shulie.takin.web.biz.cache.AbstractAgentConfigCache;
import io.shulie.takin.web.biz.service.simplify.ShadowJobConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author shiyajian
 * create: 2020-12-17
 */
@Component
public class ShadowJobConfigAgentCache extends AbstractAgentConfigCache<List<TShadowJobConfig>> {

    public static final String CACHE_NAME = "t:a:c:shadow:job";
    @Autowired
    private ShadowJobConfigService shadowJobConfigService;

    public ShadowJobConfigAgentCache(@Autowired RedisTemplate redisTemplate) {
        super(CACHE_NAME, redisTemplate);
    }

    @Override
    protected List<TShadowJobConfig> queryValue(String namespace) {
        return shadowJobConfigService.queryByAppName(namespace);
    }

}
