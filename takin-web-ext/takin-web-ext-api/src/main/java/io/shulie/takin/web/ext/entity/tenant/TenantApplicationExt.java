package io.shulie.takin.web.ext.entity.tenant;

import java.util.List;
import java.util.Map;

import lombok.Data;

/**
 * @author by: hezhongqi
 * @Package io.shulie.takin.web.ext.entity.tenant
 * @ClassName: TenantApplicationExt
 * @Description: TODO
 * @Date: 2021/11/15 12:03
 */
@Data
public class TenantApplicationExt {
    private String tenantCode;
    private String tenantAppKey;
    private Map<String, List<String>> envAppMap;
}
