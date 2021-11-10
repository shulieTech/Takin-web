package io.shulie.takin.web.biz.service.report.impl;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import cn.hutool.core.bean.BeanUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.pamirs.takin.entity.dao.confcenter.TApplicationMntDao;
import com.pamirs.takin.entity.domain.dto.report.ReportApplicationDTO;
import com.pamirs.takin.entity.domain.dto.report.ReportDetailDTO;
import com.pamirs.takin.entity.domain.entity.TApplicationMnt;
import io.shulie.takin.web.biz.pojo.output.report.ReportDetailOutput;
import io.shulie.takin.web.biz.service.report.ReportService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 报告应用数据
 *
 * @author qianshui
 * @date 2020/7/29 下午2:54
 */
@Component
@Slf4j
public class ReportApplicationService {

    @Autowired
    private ReportService reportService;

    @Resource
    private TApplicationMntDao tApplicationMntDao;

    public ReportDetailDTO getDetail(Long reportId) {
        ReportDetailOutput response = reportService.getReportByReportId(reportId);
        return BeanUtil.copyProperties(response, ReportDetailDTO.class);
    }

    public ReportApplicationDTO getReportApplication(Long reportId) {
        ReportApplicationDTO reportApplication = new ReportApplicationDTO();
        ReportDetailDTO reportDetail = this.getDetail(reportId);
        reportApplication.setReportDetail(reportDetail);
        if (reportDetail == null || CollectionUtils.isEmpty(reportDetail.getBusinessActivity())) {
            return reportApplication;
        }
        Set<Long> appSet = Sets.newHashSet();
        reportDetail.getBusinessActivity().forEach(
            data -> appSet.addAll(splitApplicationIds(data.getApplicationIds())));
        if (appSet.size() == 0) {
            return reportApplication;
        }
        List<TApplicationMnt> appsList = tApplicationMntDao.queryApplicationMntListByIds(Lists.newArrayList(appSet));
        if (CollectionUtils.isEmpty(appsList)) {
            return reportApplication;
        }
        reportApplication.setApplicationNames(appsList.stream().filter(data -> StringUtils.isNoneBlank(data.getApplicationName()))
            .map(TApplicationMnt::getApplicationName).distinct().collect(Collectors.toList()));

        return reportApplication;
    }

    private List<Long> splitApplicationIds(String applicationIds) {
        if (StringUtils.isBlank(applicationIds)) {
            return Collections.EMPTY_LIST;
        }
        String[] args = applicationIds.split(",");
        List<Long> dataList = Lists.newArrayList();
        for (String arg : args) {
            dataList.add(Long.parseLong(arg));
        }
        return dataList;
    }
}
