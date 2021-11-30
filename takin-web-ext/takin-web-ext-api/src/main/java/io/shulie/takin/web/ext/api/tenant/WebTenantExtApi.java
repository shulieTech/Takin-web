package io.shulie.takin.web.ext.api.tenant;

import java.util.List;

import io.shulie.takin.web.ext.entity.tenant.TenantInfoExt;
import org.pf4j.ExtensionPoint;

/**
 * TODO
 *
 * @author hezhongqi
 * @date 2021/9/24 17:04
 */
public interface WebTenantExtApi extends ExtensionPoint {
    /**
     * 根据 id 获取租户信息
     *
     * @param tenantId 租户主键
     * @return 租户信息
     */
    TenantInfoExt getTenantInfo(Long tenantId);

    /**
     * 根据 userAppKey  envCode 获取 租户信息
     *
     * @param userAppKey 用户AppKey
     * @param envCode    环境编码
     * @return 租户信息
     */
    TenantInfoExt getTenantInfo(String userAppKey, String envCode);

    /**
     * 获取租户列表
     *
     * @return 租户列表
     */
    List<TenantInfoExt> getTenantInfoList();

    /**
     * 判断租户,默认存在
     *
     * @param userAppKey 用户AppKey
     * @param envCode    环境编码
     * @return 是否存在
     */
    Boolean isExistTenant(String userAppKey, String envCode);

    /**
     * 获取默认环境
     *
     * @param userAppKey 用户AppKey
     * @param tenantCode 租户编码
     * @return默认环境编码
     */
    String getDefaultEnvCode(String userAppKey, String tenantCode);

    /**
     * 获取默认租户用户
     *
     * @param userAppKey 用户AppKey
     * @param tenantCode 租户编码
     * @return 默认租户用户主键
     */
    Long getDefaultUserId(String userAppKey, String tenantCode);
}
