package io.shulie.takin.web.data.param.application;

import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
import lombok.Data;

@Data
public class ApplicationAttentionParam extends TenantCommonExt {
    private String applicationName;
    private Integer focus;
}