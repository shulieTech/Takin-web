package io.shulie.takin.web.biz.service.report.impl;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.pamirs.takin.entity.domain.entity.report.ReportActivityEntity;
import io.shulie.takin.web.amdb.api.ReportClient;
import io.shulie.takin.web.amdb.bean.query.report.ReportQueryDTO;
import io.shulie.takin.web.amdb.bean.result.report.ReportActivityDTO;
import io.shulie.takin.web.biz.service.report.ReportActivityService;
import io.shulie.takin.web.data.dao.report.ReportActivityDAO;
import io.shulie.takin.web.data.dao.report.ReportTaskDAO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ReportActivityServiceImpl implements ReportActivityService {

    @Resource
    private ReportClient reportClient;

    @Resource
    private ReportActivityDAO activityDAO;

    @Resource
    private ReportTaskDAO reportTaskDAO;

    @Override
    public void startSync(String reportId) {
        reportTaskDAO.startSync(reportId);
    }

    @Override
    public void syncActivity(String reportId) {
        if (log.isDebugEnabled()) {
            log.debug("开始同步压测报告[{}]业务活动数据", reportId);
        }
        List<ReportActivityDTO> activities = reportClient.listReportActivity(new ReportQueryDTO(reportId));
        if (CollectionUtils.isNotEmpty(activities)) {
            Date now = new Date();
            List<ReportActivityEntity> entityList = activities.stream().map(activity -> {
                ReportActivityEntity entity = new ReportActivityEntity();
                BeanUtils.copyProperties(activity, entity);
                entity.setSyncTime(now);
                return entity;
            }).collect(Collectors.toList());
            activityDAO.batchInsert(entityList);
            if (log.isDebugEnabled()) {
                log.debug("同步压测报告[{}]业务活动数据完成", reportId);
            }
        } else {
            if (log.isDebugEnabled()) {
                log.debug("同步压测报告[{}]业务活动数据结束，没有需要同步的数据", reportId);
            }
        }
    }
}
