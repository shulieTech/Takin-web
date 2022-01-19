package io.shulie.takin.web.biz.cache.agentimpl;

import java.util.List;

import io.shulie.takin.web.biz.cache.AbstractAgentConfigCache;
import io.shulie.takin.web.biz.service.ApplicationPluginsConfigService;
import io.shulie.takin.web.common.common.Separator;
import io.shulie.takin.web.data.param.application.ApplicationPluginsConfigParam;
import io.shulie.takin.web.data.result.application.ApplicationPluginsConfigVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author shiyajian
 * create: 2020-12-17
 */
@Component
public class ApplicationPluginConfigAgentCache extends AbstractAgentConfigCache<String> {

    public static final String CACHE_NAME = "t:a:c:app:plugin";
    @Autowired
    private ApplicationPluginsConfigService configService;

    public ApplicationPluginConfigAgentCache(@Autowired RedisTemplate redisTemplate) {
        super(CACHE_NAME, redisTemplate);
    }

    @Override
    protected String queryValue(String namespace) {
        final String[] appConfigKey = namespace.split(Separator.defautSeparator().getValue());
        ApplicationPluginsConfigParam param = new ApplicationPluginsConfigParam(){{
            setApplicationName(appConfigKey[0]);
            setConfigKey(appConfigKey[1]);
        }};
        List<ApplicationPluginsConfigVO> list = configService.getListByParam(param);
        if (list != null && !list.isEmpty()) {
            return list.get(0).getConfigValue();
        }
        return null;
    }
}
