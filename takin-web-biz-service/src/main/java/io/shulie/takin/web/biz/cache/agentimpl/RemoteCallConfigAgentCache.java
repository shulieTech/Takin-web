package io.shulie.takin.web.biz.cache.agentimpl;

import io.shulie.takin.web.biz.cache.AbstractAgentConfigCache;
import io.shulie.takin.web.biz.service.linkManage.AppRemoteCallService;
import io.shulie.takin.web.common.vo.agent.AgentRemoteCallVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author 无涯
 * @date 2021/6/4 4:32 下午
 */
@Component
public class RemoteCallConfigAgentCache extends AbstractAgentConfigCache<AgentRemoteCallVO> {

    public static final String CACHE_NAME = "t:a:c:remote:call";
    @Autowired
    private AppRemoteCallService appRemoteCallService;

    public RemoteCallConfigAgentCache(@Autowired RedisTemplate redisTemplate) {
        super(CACHE_NAME, redisTemplate);
    }

    @Override
    protected AgentRemoteCallVO queryValue(String namespace) {
        return appRemoteCallService.agentSelect(namespace);
    }
}
