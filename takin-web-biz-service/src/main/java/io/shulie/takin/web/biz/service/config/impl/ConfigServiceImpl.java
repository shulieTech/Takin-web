package io.shulie.takin.web.biz.service.config.impl;

import com.pamirs.takin.common.constant.ConfigConstants;
import com.pamirs.takin.entity.domain.entity.TBaseConfig;
import io.shulie.takin.web.biz.constant.SwitchKeyFactory;
import io.shulie.takin.web.biz.init.sync.ConfigSyncService;
import io.shulie.takin.web.biz.service.BaseConfigService;
import io.shulie.takin.web.biz.service.config.ConfigService;
import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
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
    //FIXME 这个引用不对,configSyncService是初始化那块用的，用switchSyncService；
    @Autowired
    private ConfigSyncService configSyncService;

    @Override
    public void updateClusterTestSwitch(TenantCommonExt commonExt, Boolean value) {
        if (value == null) {
            return;
        }
        redisTemplate.opsForValue().set(SwitchKeyFactory.getClusterTestSwitchRedisKey(commonExt), value);
        configSyncService.syncClusterTestSwitch(WebPluginUtils.traceTenantCommonExt());
    }

    @Override
    public boolean getClusterTestSwitch(TenantCommonExt commonExt) {
        Object o = redisTemplate.opsForValue().get(SwitchKeyFactory.getClusterTestSwitchRedisKey(commonExt));
        if (!(o instanceof Boolean)) {
            return true;
        }
        return (Boolean)o;
    }

    @Override
    public void updateAllowListSwitch(TenantCommonExt commonExt, Boolean value) {
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

        redisTemplate.opsForValue().set(SwitchKeyFactory.getAllowListSwitchRedisKey(commonExt), value);
        configSyncService.syncAllowListSwitch(WebPluginUtils.traceTenantCommonExt());
    }

    @Override
    public boolean getAllowListSwitch(TenantCommonExt commonExt) {
        TBaseConfig tBaseConfig = baseConfigService.queryByConfigCode(ConfigConstants.WHITE_LIST_SWITCH);
        boolean dbResult = false;
        if (tBaseConfig != null) {
            dbResult = "1".equals(tBaseConfig.getConfigValue());
        }
        Object o = redisTemplate.opsForValue().get(SwitchKeyFactory.getAllowListSwitchRedisKey(commonExt));
        if (!(o instanceof Boolean)) {
            return dbResult;
        }
        return (Boolean)o & dbResult;
    }
}
