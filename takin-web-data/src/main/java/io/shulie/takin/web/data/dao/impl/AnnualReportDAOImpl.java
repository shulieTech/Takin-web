package io.shulie.takin.web.data.dao.impl;

import io.shulie.takin.web.common.util.DataTransformUtil;
import io.shulie.takin.web.data.mapper.mysql.AnnualReportMapper;
import io.shulie.takin.web.data.model.mysql.AnnualReportEntity;
import io.shulie.takin.web.data.dao.AnnualReportDAO;
import io.shulie.takin.web.data.result.AnnualReportDetailResult;
import io.shulie.takin.web.data.util.MPUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 第三方登录服务表(AnnualReport)表数据库 dao 层实现
 *
 * @author liuchuan
 * @date 2021-12-30 10:54:01
 */
@Service
public class AnnualReportDAOImpl implements AnnualReportDAO, MPUtil<AnnualReportEntity> {

    @Autowired
    private AnnualReportMapper annualReportMapper;

    @Override
    public AnnualReportDetailResult getByTenantId(Long tenantId) {
        return DataTransformUtil.copyBeanPropertiesWithNull(annualReportMapper.selectOne(this.getLambdaQueryWrapper()
            .eq(AnnualReportEntity::getTenantId, tenantId)), AnnualReportDetailResult.class);
    }

}

