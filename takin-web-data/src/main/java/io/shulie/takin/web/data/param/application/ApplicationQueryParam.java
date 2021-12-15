package io.shulie.takin.web.data.param.application;

import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;

import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;

/**
 * @author shiyajian
 * @date 2020-09-20
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ApplicationQueryParam extends TenantCommonExt {
    private Long userId;
    private List<Long> userIds;
    private String applicationName;
    private Long id;
    private List<Long> applicationIds;
}
