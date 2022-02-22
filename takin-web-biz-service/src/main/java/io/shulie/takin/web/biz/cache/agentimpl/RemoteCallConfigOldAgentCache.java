package io.shulie.takin.web.biz.cache.agentimpl;

import io.shulie.takin.web.biz.cache.AbstractAgentConfigCache;
import io.shulie.takin.web.biz.service.linkmanage.AppRemoteCallService;
import io.shulie.takin.web.common.vo.agent.OldAgentRemoteCallVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 老版白名单  老版agent 白名单接口 适配新版远程调用接口
 * @author hezhongqi
 */
@Component
public class RemoteCallConfigOldAgentCache extends AbstractAgentConfigCache<OldAgentRemoteCallVO> {

    public static final String CACHE_NAME = "t:a:c:remote:call:old";
    @Autowired
    private AppRemoteCallService appRemoteCallService;

    public RemoteCallConfigOldAgentCache(@Autowired RedisTemplate redisTemplate) {
        super(CACHE_NAME, redisTemplate);
    }

    @Override
    protected OldAgentRemoteCallVO queryValue(String namespace) {
        return appRemoteCallService.oldAgentSelect(namespace);
    }
}
