package io.shulie.takin.web.ext.entity.tenant;

import lombok.Data;

@Data
public class TenantEngineExt {

    private String engineId;
    // 0 - 公网， 1 - 私网
    private EngineType type;
    private Long tenantId;
    private String envCode;
    private String extra;
}
