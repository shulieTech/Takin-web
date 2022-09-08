package io.shulie.takin.web.biz.cache.agentimpl;

import java.util.List;

import com.pamirs.takin.common.enums.ds.DsTypeEnum;
import com.pamirs.takin.entity.domain.vo.dsmanage.DsServerVO;
import io.shulie.takin.web.biz.cache.AbstractAgentConfigCache;
import io.shulie.takin.web.biz.service.dsManage.DsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author hengyu
 * create: 2021-04-14
 */
@Component
public class ShadowKafkaClusterConfigAgentCache extends AbstractAgentConfigCache<List<DsServerVO>> {

    public static final String CACHE_NAME = "t:a:c:shadow:kafka";

    @Autowired
    private DsService dsService;

    public ShadowKafkaClusterConfigAgentCache(@Autowired RedisTemplate redisTemplate) {
        super(CACHE_NAME, redisTemplate);
    }

    @Override
    protected List<DsServerVO> queryValue(String namespace) {
        return dsService.getShadowDsServerConfigs(namespace, DsTypeEnum.SHADOW_KAFKA_CLUSTER);
    }

}
