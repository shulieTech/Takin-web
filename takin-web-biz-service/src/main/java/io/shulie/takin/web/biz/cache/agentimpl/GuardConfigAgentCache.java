package io.shulie.takin.web.biz.cache.agentimpl;

import java.util.List;

import com.pamirs.takin.entity.domain.vo.guardmanage.LinkGuardVo;
import io.shulie.takin.web.biz.cache.AbstractAgentConfigCache;
import io.shulie.takin.web.biz.service.linkManage.LinkGuardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author shiyajian
 * create: 2020-12-17
 */
@Component
public class GuardConfigAgentCache extends AbstractAgentConfigCache<List<LinkGuardVo>> {

    public static final String CACHE_NAME = "t:a:c:guard";
    @Autowired
    private LinkGuardService linkGuardService;

    public GuardConfigAgentCache(@Autowired RedisTemplate redisTemplate) {
        super(CACHE_NAME, redisTemplate);
    }

    @Override
    protected List<LinkGuardVo> queryValue(String namespace) {
        return linkGuardService.agentSelect(namespace);
    }
}
