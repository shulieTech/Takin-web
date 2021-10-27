package io.shulie.takin.web.biz.pojo.request.application;

import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
import lombok.Data;

/**
 * @author caijianying
 */
@Data
public class ApplicationAttentionParam extends TenantCommonExt {
    private String applicationName;
    private Integer focus;
}
