package io.shulie.takin.web.biz.service;

import java.util.List;
import java.util.Objects;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.pamirs.takin.common.constant.ConfigConstants;
import com.pamirs.takin.common.constant.TakinErrorEnum;
import com.pamirs.takin.common.exception.TakinModuleException;
import com.pamirs.takin.entity.domain.entity.TBaseConfig;
import io.shulie.takin.web.biz.common.CommonService;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.data.model.mysql.BaseConfigEntity;
import io.shulie.takin.web.data.param.baseconfig.BaseConfigParam;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

/**
 * 基础配置表
 */
@Service
public class BaseConfigService extends CommonService {

    /**
     * 通过主键查询
     *
     * @return
     */
    public TBaseConfig queryByConfigCode(String configCode) {

        List<TBaseConfig> list = tbaseConfigDao.queryList(new BaseConfigParam(configCode));
        if (CollectionUtils.isEmpty(list)) {
            return null;
        }

        TBaseConfig baseConfig = list.get(0);
        return baseConfig;
    }

    public void checkExistAndInsert(String configCode) {
        TBaseConfig config = queryByConfigCode(configCode);
        if (config != null) {
            return;
        }
        config = new TBaseConfig();
        config.setConfigCode(configCode);
        config.setConfigValue(ConfigConstants.WHITE_LIST_OPEN);
        config.setConfigDesc("白名单开关：0-关闭 1-开启");
        tbaseConfigDao.insertSelective(config);
    }

    /**
     * 更新基础配置的值
     *
     * @throws TakinModuleException
     */
    public void updateBaseConfig(TBaseConfig tBaseConfig) throws TakinModuleException {
        if (StringUtils.isEmpty(tBaseConfig.getConfigCode()) || StringUtils.isEmpty(tBaseConfig.getConfigValue())) {
            throw new TakinModuleException(TakinErrorEnum.API_TAKIN_CONFCENTER_UPDATE_BASE_CONFIG_PARAM_EXCEPTION);
        }
        if (tBaseConfig.getConfigValue().length() > 128) {
            throw new TakinModuleException(
                TakinErrorEnum.API_TAKIN_CONFCENTER_UPDATE_BASE_CONFIG_VALUE_TOO_LONG_EXCEPTION);
        }
        this.updateByPrimaryKeySelective(tBaseConfig);

    }

    /**
     * 更新基础配置的值
     *
     * @throws TakinModuleException
     */
    public void addBaseConfig(TBaseConfig tBaseConfig) throws TakinModuleException {
        if (StringUtils.isEmpty(tBaseConfig.getConfigCode()) || StringUtils.isEmpty(tBaseConfig.getConfigValue())) {
            throw new TakinModuleException(TakinErrorEnum.API_TAKIN_CONFCENTER_ADD_BASE_CONFIG_EXCEPTION);
        }
        TBaseConfig source = this.selectByPrimaryKey(tBaseConfig.getConfigCode());
        if (source != null) {
            throw new TakinModuleException(TakinErrorEnum.API_TAKIN_CONFCENTER_ADD_BASE_CONFIG_EXIST);
        }
        tBaseConfig.setEnvCode(WebPluginUtils.traceEnvCode());
        tBaseConfig.setTenantId(WebPluginUtils.traceTenantId());
        tbaseConfigDao.insertSelective(tBaseConfig);
    }

    public TBaseConfig selectByPrimaryKey(String configCode) {
        QueryWrapper<BaseConfigEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("CONFIG_CODE", configCode);
        BaseConfigEntity configEntity = baseConfigMapper.selectOne(wrapper);
        if (configEntity == null) {
            return null;
        }
        TBaseConfig baseConfig = new TBaseConfig();
        BeanUtils.copyProperties(configEntity, baseConfig);
        return baseConfig;
    }

    /**
     * 租户更改基础配置 有则更新 没有则原配置上添加
     *
     * @param tBaseConfig
     * @return
     */
    public int updateByPrimaryKeySelective(TBaseConfig tBaseConfig) {
        if (null == tBaseConfig.getConfigCode()) {
            throw new TakinWebException(TakinWebExceptionEnum.ERROR_COMMON, "主键不允许为空！");
        }
        tBaseConfig.setEnvCode(WebPluginUtils.traceEnvCode());
        tBaseConfig.setTenantId(WebPluginUtils.traceTenantId());

        BaseConfigEntity configEntity = new BaseConfigEntity();
        BeanUtils.copyProperties(tBaseConfig, configEntity);
        LambdaUpdateWrapper<BaseConfigEntity> wrapper = new LambdaUpdateWrapper<>();
        wrapper.set(!StringUtils.isEmpty(tBaseConfig.getConfigValue()), BaseConfigEntity::getConfigValue,
            tBaseConfig.getConfigValue());
        wrapper.set(Objects.nonNull(tBaseConfig.getUpdateTime()), BaseConfigEntity::getUpdateTime,
            tBaseConfig.getUpdateTime());
        wrapper.set(Objects.nonNull(tBaseConfig.getCreateTime()), BaseConfigEntity::getCreateTime,
            tBaseConfig.getCreateTime());
        wrapper.set(Objects.nonNull(tBaseConfig.getUseYn()), BaseConfigEntity::getUseYn, tBaseConfig.getUseYn());
        wrapper.set(StringUtils.isEmpty(tBaseConfig.getConfigDesc()), BaseConfigEntity::getConfigDesc,
            tBaseConfig.getConfigDesc());
        wrapper.eq(BaseConfigEntity::getEnvCode, tBaseConfig.getEnvCode());
        wrapper.eq(BaseConfigEntity::getTenantId, tBaseConfig.getTenantId());
        wrapper.eq(BaseConfigEntity::getConfigCode, tBaseConfig.getConfigCode());

        if (baseConfigMapper.update(configEntity, wrapper) > 0) {
            return 1;
        }

        return tbaseConfigDao.insertSelective(tBaseConfig);
    }
}
