package io.shulie.takin.web.biz.service.config;

import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;

/**
 * @author shiyajian
 * create: 2020-09-18
 */
public interface ConfigService {

    void updateClusterTestSwitch(TenantCommonExt commonExt, Boolean value);

    boolean getClusterTestSwitch(TenantCommonExt commonExt);

    void updateAllowListSwitch(TenantCommonExt commonExt, Boolean value);

    boolean getAllowListSwitch(TenantCommonExt commonExt);
}
