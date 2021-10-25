package io.shulie.takin.web.ext.api.tenant;

import java.util.List;

import io.shulie.takin.web.ext.entity.tenant.TenantInfoExt;
import org.pf4j.ExtensionPoint;

/**
 * @author by: hezhongqi
 * @Package io.shulie.takin.web.ext.api.tenant
 * @ClassName: WebTenantExtApi
 * @Description: TODO
 * @Date: 2021/9/24 17:04
 */
public interface WebTenantExtApi extends ExtensionPoint {
    /**
     *根据 id 获取租户信息
     * @param tenantId
     * @return
     */
    TenantInfoExt getTenantInfo(Long tenantId);

    /**
     * 根据 userAppKey  envCode 获取 租户信息
     * @param userAppKey
     * @param envCode
     * @return
     */
    TenantInfoExt getTenantInfo(String userAppKey,String envCode);

    /**
     * 获取租户列表
     * @return
     */
    List<TenantInfoExt> getTenantInfoList();

    /**
     * 判断租户,默认存在
     * @param userAppKey
     * @param envCode
     * @return
     */
     Boolean isExistTenant(String userAppKey,String envCode);

    /**
     * 获取默认环境
     * @param userAppKey
     * @param tenantCode
     * @return
     */
    String getDefaultEnvCode(String userAppKey, String tenantCode);

    /**
     * 获取默认租户用户
     * @param userAppKey
     * @param tenantCode
     * @return
     */
    Long getDefaultUserId(String userAppKey, String tenantCode);

    /**
     * 根据租户code 获取租户列表
     * @param tenantCode
     * @return
     */
    List<TenantInfoExt> getTenantInfoListByTenantCode(String tenantCode);
}
