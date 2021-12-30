package io.shulie.takin.web.biz.service.impl;

import java.math.BigDecimal;
import java.math.MathContext;

import cn.hutool.core.date.LocalDateTimeUtil;
import cn.hutool.core.util.StrUtil;
import io.shulie.takin.web.biz.pojo.vo.AnnualReportContentVO;
import io.shulie.takin.web.biz.response.AnnualReportDetailResponse;
import io.shulie.takin.web.biz.service.AnnualReportService;
import io.shulie.takin.web.common.constant.CacheConstants;
import io.shulie.takin.web.common.util.JsonUtil;
import io.shulie.takin.web.data.dao.AnnualReportDAO;
import io.shulie.takin.web.data.result.AnnualReportDetailResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * 第三方登录服务表(AnnualReport)service
 *
 * @author liuchuan
 * @date 2021-12-30 10:54:24
 */
@Service
public class AnnualReportServiceImpl implements AnnualReportService {

    @Autowired
    private AnnualReportDAO annualReportDAO;

    @Cacheable(CacheConstants.CACHE_KEY_ANNUAL_REPORT)
    @Override
    public AnnualReportDetailResponse getAnnualReportByTenantId(Long tenantId) {
        AnnualReportDetailResult annualReport = annualReportDAO.getByTenantId(tenantId);
        Assert.notNull(annualReport, "租户年度报告不存在!");
        Assert.isTrue(StrUtil.isNotBlank(annualReport.getContent()), "租户年度报告数据不存在!");
        AnnualReportContentVO annualReportContentVO = JsonUtil.json2Bean(annualReport.getContent(),
            AnnualReportContentVO.class);
        Assert.notNull(annualReportContentVO, "租户年度报告不存在!");

        // 转换
        AnnualReportDetailResponse response = new AnnualReportDetailResponse();
        response.setTenantId(tenantId);
        response.setTenantName(annualReport.getTenantName());
        response.setTenantLogo(annualReport.getTenantLogo());

        annualReportContentVO.setStartDate("2020年08月18日");

        // 压测比例
        Integer maxPressureTime = annualReportContentVO.getMaxPressureTime();
        Integer totalPressureTime = annualReportContentVO.getTotalPressureTime();
        if (totalPressureTime == 0) {
            annualReportContentVO.setPressureProportion(BigDecimal.ZERO);
        } else {
            annualReportContentVO.setPressureProportion(BigDecimal.valueOf(maxPressureTime)
                .divide(BigDecimal.valueOf(totalPressureTime), MathContext.DECIMAL32));
        }

        // 优化时间
        annualReportContentVO.setDiffAvgRt(
            annualReportContentVO.getBeforeAvgRt().subtract(annualReportContentVO.getAfterAvgRt()));

        // 最晚时间
        if (annualReportContentVO.getLastDateTime() == null) {
            annualReportContentVO.setLastDate("");
            annualReportContentVO.setLastTime("");
        } else {
            annualReportContentVO.setLastDate(LocalDateTimeUtil.format(annualReportContentVO.getLastDateTime(), "MM月dd日"));
            annualReportContentVO.setLastTime(LocalDateTimeUtil.format(annualReportContentVO.getLastDateTime(), "HH:mm"));
        }
        
        response.setContent(annualReportContentVO);
        return response;
    }

}
