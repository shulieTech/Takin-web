package io.shulie.takin.web.biz.cache.agentimpl;

import java.util.List;

import io.shulie.takin.web.biz.agent.vo.ShadowConsumerVO;
import io.shulie.takin.web.biz.cache.AbstractAgentConfigCache;
import io.shulie.takin.web.biz.service.ShadowConsumerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author shiyajian
 * create: 2020-12-17
 */
@Component
public class ShadowConsumerConfigAgentCache extends AbstractAgentConfigCache<List<ShadowConsumerVO>> {

    public static final String CACHE_NAME = "t:a:c:shadow:consumer";
    @Autowired
    private ShadowConsumerService shadowConsumerService;

    public ShadowConsumerConfigAgentCache(@Autowired RedisTemplate redisTemplate) {
        super(CACHE_NAME, redisTemplate);
    }

    @Override
    protected List<ShadowConsumerVO> queryValue(String namespace) {
        return shadowConsumerService.agentSelect(namespace);
    }

}
