package io.shulie.takin.web.biz.cache.agentimpl;

import com.pamirs.takin.common.constant.ConfigConstants;
import com.pamirs.takin.entity.domain.entity.TBaseConfig;
import io.shulie.takin.web.biz.cache.AbstractAgentConfigCache;
import io.shulie.takin.web.biz.service.BaseConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author shiyajian
 * create: 2020-12-17
 */
@Component
public class AllowListSwitchConfigAgentCache extends AbstractAgentConfigCache<Boolean> {

    public static final String CACHE_NAME = "t:a:c:allowlist:switch";

    @Autowired
    private BaseConfigService baseConfigService;

    public AllowListSwitchConfigAgentCache(@Autowired RedisTemplate redisTemplate) {
        super(CACHE_NAME, redisTemplate);
    }

    /**
     * 白名单开关，如果缓存中不存在，去数据库查
     */
    @Override
    protected Boolean queryValue(String namespace) {
        TBaseConfig tBaseConfig = baseConfigService.queryByConfigCode(ConfigConstants.WHITE_LIST_SWITCH);
        return "1".equals(tBaseConfig.getConfigValue());
    }
}
