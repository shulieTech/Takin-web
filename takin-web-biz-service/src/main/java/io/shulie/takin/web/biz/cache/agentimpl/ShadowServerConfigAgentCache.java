package io.shulie.takin.web.biz.cache.agentimpl;

import java.util.List;

import io.shulie.takin.web.biz.cache.AbstractAgentConfigCache;
import io.shulie.takin.web.biz.pojo.output.application.ShadowServerConfigurationOutput;
import io.shulie.takin.web.biz.service.dsManage.DsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author shiyajian
 * create: 2020-12-17
 */
@Component
public class ShadowServerConfigAgentCache
    extends AbstractAgentConfigCache<List<ShadowServerConfigurationOutput>> {

    public static final String CACHE_NAME = "t:a:c:shadow:server";
    @Autowired
    private DsService dsService;

    public ShadowServerConfigAgentCache(@Autowired RedisTemplate redisTemplate) {
        super(CACHE_NAME, redisTemplate);
    }

    @Override
    protected List<ShadowServerConfigurationOutput> queryValue(String namespace) {
        return dsService.getShadowServerConfigs(namespace);
    }

}
