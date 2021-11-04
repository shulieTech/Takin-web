package io.shulie.takin.web.common.util;

import io.shulie.takin.web.ext.util.WebPluginUtils;

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
    private static String TENANT_CONFIG_REDIS_KEY = "takin:tenant:config";

    /**
     * 获取租户配置 redis key
     * @return
     */
    public static String getTenantConfigRedisKey(){
        return CommonUtil.generateRedisKey(TENANT_CONFIG_REDIS_KEY,WebPluginUtils.traceTenantAppKey(), WebPluginUtils.traceEnvCode());
    }

}
