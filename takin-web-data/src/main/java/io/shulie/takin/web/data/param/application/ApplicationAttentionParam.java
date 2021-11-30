package io.shulie.takin.web.data.param.application;

import lombok.Data;
import lombok.EqualsAndHashCode;

import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;

@Data
@EqualsAndHashCode(callSuper = true)
public class ApplicationAttentionParam extends TenantCommonExt {
    private String applicationName;
    private Integer focus;
}