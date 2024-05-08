package io.shulie.takin.web.common.pojo.bo.agent;

import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
import lombok.Data;

@Data
public class ReportMockBO extends TenantCommonExt {

    private Long reportId;
}
