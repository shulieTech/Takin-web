package io.shulie.takin.web.data.dao.config.impl;

import java.util.List;

import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import io.shulie.takin.web.common.constant.AppConstants;
import io.shulie.takin.web.data.dao.config.ConfigServerDAO;
import io.shulie.takin.web.data.mapper.mysql.ConfigServerMapper;
import io.shulie.takin.web.data.model.mysql.ConfigServerEntity;
import io.shulie.takin.web.data.param.config.UpdateConfigServerParam;
import io.shulie.takin.web.data.result.config.ConfigServerDetailResult;
import io.shulie.takin.web.data.util.MPUtil;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 配置表-服务的配置(ConfigServer)表数据库 dao 层实现
 *
 * @author liuchuan
 * @date 2021-10-12 11:17:20
 */
@Service
public class ConfigServerDAOImpl implements ConfigServerDAO, MPUtil<ConfigServerEntity>, AppConstants {

    @Autowired
    private ConfigServerMapper configServerMapper;

    @Override
    public String getTenantEnvValueByKey(String key) {
        ConfigServerDetailResult tenantEnvConfig = this.getTenantEnvConfigByKey(key);
        return tenantEnvConfig == null ? null : tenantEnvConfig.getValue();
    }

    @Override
    public String getGlobalNotTenantValueByKey(String key) {
        ConfigServerEntity configServer = configServerMapper.selectOne(this.getLimitOneLambdaQueryWrapper()
            .select(ConfigServerEntity::getValue).eq(ConfigServerEntity::getKey, key)
            .eq(ConfigServerEntity::getIsGlobal, YES).eq(ConfigServerEntity::getIsTenant, NO));
        return configServer == null ? "" : configServer.getValue();
    }

    @Override
    public String getTenantEnvValueByKeyAndTenantAppKeyAndEnvCode(String key, String tenantAppKey, String envCode) {
        List<ConfigServerDetailResult> valueList = configServerMapper.selectTenantEnvListByKey(key,
            WebPluginUtils.traceTenantAppKey(), WebPluginUtils.traceEnvCode());
        return valueList.isEmpty() ? null : valueList.get(0).getValue();
    }

    @Override
    public ConfigServerDetailResult getTenantEnvConfigByKey(String key) {
        List<ConfigServerDetailResult> valueList = configServerMapper.selectTenantEnvListByKey(key,
            WebPluginUtils.traceTenantAppKey(), WebPluginUtils.traceEnvCode());
        return valueList.isEmpty() ? null : valueList.get(0);
    }

    @Override
    public boolean updateGlobalValueByKey(UpdateConfigServerParam updateConfigServerParam) {
        return SqlHelper.retBool(configServerMapper.update(null, this.getLambdaUpdateWrapper()
            .eq(ConfigServerEntity::getKey, updateConfigServerParam.getKey())
            .eq(ConfigServerEntity::getIsTenant, AppConstants.NO)
            .set(ConfigServerEntity::getValue, updateConfigServerParam.getValue())));

    }

}

