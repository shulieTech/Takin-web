package io.shulie.takin.web.common.util;

import io.shulie.takin.web.ext.util.WebPluginUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * @author by: hezhongqi
 * @Package io.shulie.takin.web.common.util
 * @ClassName: TenantUtils
 * @Description: TODO
 * @Date: 2021/11/4 11:07
 */
public class TenantUtils {
    /**
     * 租户配置key
     */
    private static String TENANT_CONFIG_REDIS_KEY = "TAKIN:TENANT:CONFIG";

    /**
     * 获取租户配置 redis key
     * @return
     */
    public static String getTenantConfigRedisKey(String tenantAppKey,String envCode){
        return CommonUtil.generateRedisKey(TENANT_CONFIG_REDIS_KEY,
            (StringUtils.isNotEmpty(tenantAppKey)?tenantAppKey:WebPluginUtils.traceTenantAppKey()),
            (StringUtils.isNotEmpty(envCode)?envCode:WebPluginUtils.traceEnvCode()));
    }

}
