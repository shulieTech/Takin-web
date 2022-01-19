package io.shulie.takin.web.biz.cache.agentimpl;

import com.pamirs.takin.entity.domain.dto.ApplicationSwitchStatusDTO;
import io.shulie.takin.web.biz.cache.AbstractAgentConfigCache;
import io.shulie.takin.web.biz.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author shiyajian
 * create: 2020-12-18
 */
@Component
public class PressureSwitchConfigAgentCache extends AbstractAgentConfigCache<ApplicationSwitchStatusDTO> {

    public static final String CACHE_NAME = "t:a:c:pressure:switch";
    @Autowired
    private ApplicationService applicationService;

    public PressureSwitchConfigAgentCache(@Autowired RedisTemplate redisTemplate) {
        super(CACHE_NAME, redisTemplate);
    }

    /**
     * 压测开关，如果缓存中不存在，去数据库查
     */
    @Override
    protected ApplicationSwitchStatusDTO queryValue(String namespace) {
        return applicationService.agentGetUserSwitchInfo();
    }

}
