package io.shulie.takin.web.ext.api.ecloud;

import io.shulie.takin.web.ext.entity.ecloud.TenantPackageInfoExt;
import io.shulie.takin.web.ext.entity.tenant.TenantInfoExt;
import org.pf4j.ExtensionPoint;

import java.util.List;

/**
 *
 *
 * @author housk
 */
public interface WebOpenEcloudExtApi extends ExtensionPoint {
    /**
     * 根据 id 获取租户生效套餐
     *
     * @param tenantId 租户主键
     * @return 租户信息
     */
    TenantPackageInfoExt getTenantPackageInfo(Long tenantId);

    /**
     * 根据 用户套餐Id， 扣除压测次数
     *
     * @param tenantPackageId 用户套餐Id
     */
    void reduceTimesOfCustomer(Long tenantPackageId);

}
