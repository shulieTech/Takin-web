package io.shulie.takin.web.data.param.application;

import java.util.List;

import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
import lombok.Data;

/**
 * @author shiyajian
 * create: 2020-09-20
 */
@Data
public class ApplicationQueryParam extends TenantCommonExt {
    private Long userId;
    private List<Long> userIds;
    private String applicationName;
    private Long id;
    private List<Long> applicationIds;
}
