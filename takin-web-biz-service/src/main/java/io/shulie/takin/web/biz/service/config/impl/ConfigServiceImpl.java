package io.shulie.takin.web.biz.service.config.impl;

import com.pamirs.takin.common.constant.ConfigConstants;
import com.pamirs.takin.entity.domain.entity.TBaseConfig;
import io.shulie.takin.web.biz.constant.SwitchKeyFactory;
import io.shulie.takin.web.biz.init.sync.ConfigSyncService;
import io.shulie.takin.web.biz.service.BaseConfigService;
import io.shulie.takin.web.biz.service.config.ConfigService;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author shiyajian
 * create: 2020-09-18
 */
@Component
@Slf4j
public class ConfigServiceImpl implements ConfigService {

    @Autowired
    @Qualifier("redisTemplate")
    private RedisTemplate redisTemplate;
    @Autowired
    private BaseConfigService baseConfigService;
    @Autowired
    private ConfigSyncService configSyncService; //FIXME 这个引用不对,configSyncService是初始化那块用的，用switchSyncService；

    @Override
    public void updateClusterTestSwitch(String userAppKey, Boolean value) {
        if (value == null) {
            return;
        }
        redisTemplate.opsForValue().set(SwitchKeyFactory.getClusterTestSwitchRedisKey(userAppKey), value);
        configSyncService.syncClusterTestSwitch(WebPluginUtils.getTenantUserAppKey());
    }

    @Override
    public boolean getClusterTestSwitch(String userAppKey) {
        Object o = redisTemplate.opsForValue().get(SwitchKeyFactory.getClusterTestSwitchRedisKey(userAppKey));
        if (!(o instanceof Boolean)) {
            return true;
        }
        return (Boolean)o;
    }

    @Override
    public void updateAllowListSwitch(String userAppKey, Boolean value) {
        if (value == null) {
            return;
        }
        try {
            baseConfigService.checkExistAndInsert(ConfigConstants.WHITE_LIST_SWITCH);
            TBaseConfig config = new TBaseConfig();
            config.setConfigCode(ConfigConstants.WHITE_LIST_SWITCH);
            config.setConfigValue(value ? ConfigConstants.WHITE_LIST_OPEN : ConfigConstants.WHITE_LIST_CLOSE);
            baseConfigService.updateBaseConfig(config);
        } catch (Exception e) {
            log.error("发生错误", e);
        }

        redisTemplate.opsForValue().set(SwitchKeyFactory.getAllowListSwitchRedisKey(userAppKey), value);
        configSyncService.syncAllowListSwitch(WebPluginUtils.getTenantUserAppKey());
    }

    @Override
    public boolean getAllowListSwitch(String userAppKey) {
        TBaseConfig tBaseConfig = baseConfigService.queryByConfigCode(ConfigConstants.WHITE_LIST_SWITCH);
        boolean dbResult = false;
        if (tBaseConfig != null) {
            dbResult = "1".equals(tBaseConfig.getConfigValue());
        }
        Object o = redisTemplate.opsForValue().get(SwitchKeyFactory.getAllowListSwitchRedisKey(userAppKey));
        if (!(o instanceof Boolean)) {
            return dbResult;
        }
        return (Boolean)o & dbResult;
    }
}
