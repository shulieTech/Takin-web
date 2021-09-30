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
    TenantInfoExt getTenantInfo(String tenantId);

    /**
     * 根据userappkey 获取 租户信息
     * @param userAppKey
     * @return
     */
    TenantInfoExt getTenantInfoByUserAppKey(String userAppKey);

    /**
     * 获取租户列表
     * @return
     */
    List<TenantInfoExt> getTenantInfoList();
}
