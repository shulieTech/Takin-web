package io.shulie.takin.cloud.common.utils;

import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import io.shulie.takin.web.ext.util.WebPluginUtils;

/**
 * @author hezhongqi
 * @author 张天赐
 * @date 2021/8/4 14:42
 */
public class CloudPluginUtils {

    /**
     * 返回用户id
     *
     * @return -
     */
    public static ContextExt getContext() {
        ContextExt ext = new ContextExt();
        WebPluginUtils.fillCloudUserData(ext);
        return ext;
    }

    /**
     * 用户主键
     *
     * @return -
     */
    public static Long getUserId() {
        return getContext().getUserId();
    }

    /**
     * 租户主键
     *
     * @return -
     */
    public static Long getTenantId() {
        return getContext().getTenantId();
    }

    /**
     * 环境编码
     *
     * @return -
     */
    public static String getEnvCode() {
        return getContext().getEnvCode();
    }

    /**
     * 返回过滤sql
     *
     * @return -
     */
    public static String getFilterSql() {
        return getContext().getFilterSql();
    }

    public static String getUserAppKey() {
        return getContext().getUserAppKey();
    }

    /**
     * 公共补充 查询 用户数据
     *
     * @param ext -
     */
    public static void fillUserData(ContextExt ext) {
        ContextExt context = getContext();
        ext.setUserId(context.getUserId());
        ext.setTenantId(context.getTenantId());
        ext.setEnvCode(context.getEnvCode());
        ext.setFilterSql(context.getFilterSql());
        ext.setUserAppKey(context.getUserAppKey());
    }
}
