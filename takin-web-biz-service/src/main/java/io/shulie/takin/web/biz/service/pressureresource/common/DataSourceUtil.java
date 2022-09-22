package io.shulie.takin.web.biz.service.pressureresource.common;

import com.pamirs.takin.common.util.MD5Util;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceRelateDsEntity;
import io.shulie.takin.web.ext.util.WebPluginUtils;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/9/7 3:56 PM
 */
public class DataSourceUtil {
    /**
     * 数据源唯一
     *
     * @param dsEntity
     * @return
     */
    public static String generateKey(PressureResourceRelateDsEntity dsEntity) {
        String key = String.format("%d-%s-%s-%d-%s",
                dsEntity.getResourceId(),
                dsEntity.getAppName(),
                dsEntity.getBusinessDatabase(),
                WebPluginUtils.traceTenantId(),
                WebPluginUtils.traceEnvCode());
        return MD5Util.getMD5(key);
    }

    /**
     * 数据源唯一
     *
     * @return
     */
    public static String generateDsKey(Long resourceId, String businessDatabase) {
        String key = String.format("%d-%s-%d-%s",
                resourceId,
                businessDatabase,
                WebPluginUtils.traceTenantId(),
                WebPluginUtils.traceEnvCode());
        return MD5Util.getMD5(key);
    }

    public static String generateDsUniqueKey(Long resourceId, String appName, String businessDatabase) {
        String key = String.format("%d-%s-%s-%d-%s",
                resourceId,
                appName,
                businessDatabase,
                WebPluginUtils.traceTenantId(),
                WebPluginUtils.traceEnvCode());
        return MD5Util.getMD5(key);
    }
}
