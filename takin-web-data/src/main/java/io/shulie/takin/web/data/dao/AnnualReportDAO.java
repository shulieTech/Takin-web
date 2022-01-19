package io.shulie.takin.web.data.dao;

import io.shulie.takin.web.data.result.AnnualReportDetailResult;

/**
 * 第三方登录服务表(AnnualReport)表数据库 dao 层
 *
 * @author liuchuan
 * @date 2021-12-30 10:54:01
 */
public interface AnnualReportDAO {

    /**
     * 获得租户的年度报告
     *
     * @param tenantId 租户id
     * @return 年度报告
     */
    AnnualReportDetailResult getByTenantId(Long tenantId);

}

