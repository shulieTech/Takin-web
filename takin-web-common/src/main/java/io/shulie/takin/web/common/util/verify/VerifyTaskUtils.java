package io.shulie.takin.web.common.util.verify;

import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
import io.shulie.takin.web.ext.util.WebPluginUtils;

/**
 * @author by: hezhongqi
 * @Package io.shulie.takin.web.common.util.verify
 * @ClassName: VerifyTaskUtils
 * @Description: 漏数验证数据
 * @Date: 2022/1/18 11:29
 */
public class VerifyTaskUtils {

    /**
     * map key
     * @param refType
     * @param refId
     * @return
     */
    public static String getVerifyTaskRedisMapKey(Integer refType,Long refId) {
        return WebPluginUtils.traceTenantId() + "$" + WebPluginUtils.traceEnvCode() + "$" + WebPluginUtils.traceTenantAppKey()  + "$"  + refType + "$" + refId;
    }

    /**
     * 获取租户信息
     * @param redisKey
     * @return
     */
    public static TenantCommonExt getTenantInfo(String redisKey) {
        String[] temp = redisKey.split("\\$");
        TenantCommonExt ext = new TenantCommonExt();
        ext.setTenantId(Long.parseLong(temp[0]));
        ext.setEnvCode(temp[1]);
        ext.setTenantAppKey(temp[2]);
        return ext;
    }

    /**
     * 获取refType
     * @param redisKey
     * @return
     */
    public static Integer getRefType(String redisKey) {
        String[] temp = redisKey.split("\\$");
        return Integer.parseInt(temp[3]);
    }

    /**
     * 获取refId
     * @param redisKey
     * @return
     */
    public static Long getRefId(String redisKey) {
        String[] temp = redisKey.split("\\$");
        return Long.parseLong(temp[4]);
    }
}
