package io.shulie.takin.web.ext.entity.tenant;

import java.util.List;
import java.util.Map;

import lombok.Data;

/**
 * @author by: hezhongqi
 * @Package io.shulie.takin.web.ext.entity.tenant
 * @ClassName: TenantInfoConfigExt
 * @Description: 租户全量配置
 * @Date: 2021/11/9 14:58
 */
@Data
public class TenantInfoConfigExt {
    private String tenantAppKey;
    private String tenantCode;
    /**
     * key：value =  环境：配置
     */
    private Map<String,List<TenantConfigExt>> configs;
}
