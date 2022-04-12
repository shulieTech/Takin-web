package io.shulie.takin.web.data.dao.report;

import java.util.Date;

import javax.annotation.Resource;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pamirs.takin.entity.domain.entity.report.ReportTaskEntity;
import io.shulie.takin.web.data.mapper.mysql.ReportTaskMapper;
import io.shulie.takin.web.data.util.MPUtil;
import org.springframework.stereotype.Repository;

@Repository
public class ReportTaskDAOImpl extends ServiceImpl<ReportTaskMapper, ReportTaskEntity>
    implements ReportTaskDAO, MPUtil<ReportTaskEntity> {

    @Resource
    private ReportTaskMapper reportTaskMapper;

    @Override
    public void startAnalyze(Long reportId) {
        ReportTaskEntity entity = new ReportTaskEntity();
        entity.setReportId(reportId);
        entity.setGmtCreate(new Date());
        entity.setState(0);
        this.save(entity);
    }

    @Override
    public void startSync(String reportId) {
        reportTaskMapper.startSync(reportId);
    }

    public void syncSuccess(String reportId) {
        reportTaskMapper.syncSuccess(reportId);
    }
}
