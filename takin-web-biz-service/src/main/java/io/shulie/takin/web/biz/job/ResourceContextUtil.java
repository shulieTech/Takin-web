package io.shulie.takin.web.biz.job;

import io.shulie.takin.web.common.enums.ContextSourceEnum;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceEntity;
import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
import io.shulie.takin.web.ext.entity.tenant.TenantInfoExt;
import io.shulie.takin.web.ext.util.WebPluginUtils;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/9/28 10:22 AM
 */
public class ResourceContextUtil {
    /**
     * 根据Resource设置上下文
     *
     * @param resource
     */
    public static void setTenantContext(PressureResourceEntity resource) {
        if (resource == null) {
            return;
        }
        TenantCommonExt commonExt = new TenantCommonExt();
        commonExt.setSource(ContextSourceEnum.JOB.getCode());
        commonExt.setEnvCode(resource.getEnvCode());
        commonExt.setTenantId(resource.getTenantId());
        TenantInfoExt tenantInfoExt = WebPluginUtils.getTenantInfo(resource.getTenantId());
        if (tenantInfoExt == null) {
            return;
        }
        String tenantCode = tenantInfoExt.getTenantCode();
        String tenantAppKey = tenantInfoExt.getTenantAppKey();
        commonExt.setTenantAppKey(tenantAppKey);
        commonExt.setTenantCode(tenantCode);
        WebPluginUtils.setTraceTenantContext(commonExt);
    }
}
