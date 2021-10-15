package io.shulie.takin.web.data.dao.config;

/**
 * 配置表-服务的配置(ConfigServer)表数据库 dao 层
 *
 * @author liuchuan
 * @date 2021-10-12 11:17:15
 */
public interface ConfigServerDAO {

    /**
     * 通过配置 key, 获得 value
     * 如果 value 为 null, 说明全局的配置也没有...
     *
     * @param key 配置 key
     * @return 配置值
     */
    String getTenantEnvValueByKey(String key);

    /**
     * 全局的非租户的应用配置
     *
     * @param key 配置 key
     * @return 配置 value
     */
    String getGlobalNotTenantValueByKey(String key);

    /**
     * 通过配置 key, 租户 key, 环境, 获得 value
     * 如果 value 为 null, 说明全局的配置也没有...
     *
     * @param key 配置 key
     * @param tenantAppKey 租户 key
     * @param envCode 环境
     * @return 配置值
     */
    String getTenantEnvValueByKeyAndTenantAppKeyAndEnvCode(String key, String tenantAppKey, String envCode);

}

