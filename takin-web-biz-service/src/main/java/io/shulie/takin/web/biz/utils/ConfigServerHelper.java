package io.shulie.takin.web.biz.utils;

import javax.annotation.PostConstruct;

import cn.hutool.core.util.StrUtil;
import io.shulie.takin.web.common.constant.AppConstants;
import io.shulie.takin.web.common.enums.config.ConfigServerKeyEnum;
import io.shulie.takin.web.common.util.RedisHelper;
import io.shulie.takin.web.data.dao.config.ConfigServerDAO;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 应用配置  工具类
 *
 * @author liuchuan
 * @date 2021/8/13 3:58 下午
 */
@Component
public class ConfigServerHelper {

    /**
     * 应用配置 redis key
     * 租户:环境 下的 map
     * key -> 配置 key
     * value -> 配置 value
     */
    public static final String CONFIG_SERVER_KEY = "config:server:%s:%s";

    /**
     * 全局, 非租户的应用配置 redis key
     */
    public static final String CONFIG_SERVER_GLOBAL_NOT_TENANT_KEY = "config:server:-999:-999";

    private static ConfigServerDAO configServerDAO;

    @Autowired
    private ConfigServerDAO csd;

    /**
     * 根据配置 key, 租户 key, 环境 获取值
     *
     * @param key 配置 key
     * @param tenantAppKey 租户 key
     * @param envCode 环境
     * @return 配置值
     */
    public static String getValueByKeyAndTenantAppKeyAndEnvCode(String key, String tenantAppKey, String envCode) {
        // redis 中获取值, 没有的话, 数据库查询
        String redisKey = getConfigServerRedisKey(tenantAppKey, envCode);
        Object valueObject = RedisHelper.hashGet(redisKey, key);
        if (valueObject != null && StrUtil.isNotBlank(valueObject.toString())) {
            return valueObject.toString();
        }

        String value = configServerDAO.getTenantEnvValueByKeyAndTenantAppKeyAndEnvCode(key, tenantAppKey, envCode);
        if (StrUtil.isBlank(value)) {
            throw new IllegalArgumentException(String.format("%s 配置不存在", key));
        }

        RedisHelper.hashPut(redisKey, key, value);
        return value;
    }

    @PostConstruct
    public void init() {
        configServerDAO = csd;
    }

    /**
     * 根据配置的枚举, 配置 key 获取值
     *
     * @param configServerKeyEnum 配置的枚举
     * @return 配置值
     */
    public static String getValueByKey(ConfigServerKeyEnum configServerKeyEnum) {
        String redisKey = getRedisKey(configServerKeyEnum);
        String key = configServerKeyEnum.getNow();

        // redis 中获取值, 没有的话, 数据库查询
        Object valueObject = RedisHelper.hashGet(redisKey, key);
        if (valueObject != null && StrUtil.isNotBlank(valueObject.toString())) {
            return valueObject.toString();
        }

        String value = getValue(configServerKeyEnum, key);
        if (StrUtil.isBlank(value)) {
            throw new IllegalArgumentException(String.format("%s 配置不存在", key));
        }

        RedisHelper.hashPut(redisKey, key, value);
        return value;
    }

    /**
     * 应用配置 redis key
     *
     * @return redis key
     */
    private static String getConfigServerRedisKey(String tenantAppKey, String envCode) {
        return String.format(CONFIG_SERVER_KEY, tenantAppKey, envCode);
    }

    /**
     * 根据配置的枚举获得 配置值
     *
     * @param configServerKeyEnum 配置的枚举
     * @return 配置值
     */
    private static String getValue(ConfigServerKeyEnum configServerKeyEnum, String key) {
        return configServerKeyEnum.getIsTenant() == AppConstants.NO
            ? configServerDAO.getTenantEnvValueByKey(key)
            : configServerDAO.getGlobalNotTenantValueByKey(key);
    }

    /**
     * 根据配置的枚举获得 redis key
     *
     * @param configServerKeyEnum 配置的枚举
     * @return redis key
     */
    private static String getRedisKey(ConfigServerKeyEnum configServerKeyEnum) {
        return configServerKeyEnum.getIsTenant() == AppConstants.NO ? CONFIG_SERVER_GLOBAL_NOT_TENANT_KEY
            : getConfigServerRedisKey(WebPluginUtils.traceTenantAppKey(), WebPluginUtils.traceEnvCode());
    }

}
