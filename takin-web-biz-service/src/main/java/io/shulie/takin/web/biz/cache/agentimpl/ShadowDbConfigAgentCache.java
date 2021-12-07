package io.shulie.takin.web.biz.cache.agentimpl;

import java.util.List;

import bsh.This;
import com.pamirs.takin.entity.domain.vo.dsmanage.DsAgentVO;
import io.shulie.takin.web.biz.cache.AbstractAgentConfigCache;
import io.shulie.takin.web.biz.service.dsManage.DsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author shiyajian
 * create: 2020-12-17
 */
@Component
public class ShadowDbConfigAgentCache extends AbstractAgentConfigCache<List<DsAgentVO>> {

    public static final String CACHE_NAME = "t:a:c:shadow:ds";
    @Autowired
    private DsService dsService;

    public ShadowDbConfigAgentCache(@Autowired RedisTemplate redisTemplate) {
        super(CACHE_NAME, redisTemplate);
    }

    @Override
    protected List<DsAgentVO> queryValue(String namespace) {
        return dsService.getConfigs(namespace);
    }

}
