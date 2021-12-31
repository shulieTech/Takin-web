package io.shulie.takin.web.biz.service;

import io.shulie.takin.web.biz.response.AnnualReportDetailResponse;

/**
 * 第三方登录服务表(AnnualReport)service
 *
 * @author liuchuan
 * @date 2021-12-30 10:54:24
 */
public interface AnnualReportService {

    /**
     * 获得租户的年度报告
     *
     * @param tenantId 租户id
     * @return 年度报告
     */
    AnnualReportDetailResponse getAnnualReportByTenantId(Long tenantId);

}
