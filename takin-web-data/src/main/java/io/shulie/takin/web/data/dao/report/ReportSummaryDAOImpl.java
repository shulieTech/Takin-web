package io.shulie.takin.web.data.dao.report;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.shulie.takin.web.data.mapper.mysql.ReportSummaryMapper;
import io.shulie.takin.web.data.model.mysql.ReportSummaryEntity;
import io.shulie.takin.web.data.param.report.ReportSummaryCreateParam;
import io.shulie.takin.web.data.result.report.ReportSummaryResult;
import io.shulie.takin.web.data.util.MPUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * @author by: hezhongqi
 * @Package io.shulie.takin.web.data.dao.report
 * @ClassName: ReportSummaryDao
 * @Description: TODO
 * @Date: 2021/11/23 09:55
 */
@Component
public class ReportSummaryDAOImpl extends ServiceImpl<ReportSummaryMapper, ReportSummaryEntity>
    implements ReportSummaryDAO, MPUtil<ReportSummaryEntity> {

    /**
     *  insert into t_report_summary (id, report_id, bottleneck_interface_count,
     *                                       risk_machine_count, business_activity_count,
     *                                       unachieve_business_activity_count, application_count,
     *                                       machine_count, warn_count)
     *         values (#{id,jdbcType=BIGINT}, #{reportId,jdbcType=BIGINT}, #{bottleneckInterfaceCount,jdbcType=INTEGER},
     *                 #{riskMachineCount,jdbcType=INTEGER}, #{businessActivityCount,jdbcType=INTEGER},
     *                 #{unachieveBusinessActivityCount,jdbcType=INTEGER}, #{applicationCount,jdbcType=INTEGER},
     *                 #{machineCount,jdbcType=INTEGER}, #{warnCount,jdbcType=INTEGER})
     * @param param
     */
    @Override
    public void insertOrUpdate(ReportSummaryCreateParam param) {
        LambdaQueryWrapper<ReportSummaryEntity> queryWrapper = this.getLambdaQueryWrapper();
        queryWrapper.eq(ReportSummaryEntity::getReportId,param.getReportId());
        ReportSummaryEntity summaryEntity = this.getOne(queryWrapper);

        ReportSummaryEntity entity = new ReportSummaryEntity();
        BeanUtils.copyProperties(param,entity);
        if(summaryEntity != null) {
            entity.setId(summaryEntity.getId());
            this.updateById(entity);
            return;
        }
        this.save(entity);
    }

    @Override
    public ReportSummaryResult selectOneByReportId(Long reportId) {
        LambdaQueryWrapper<ReportSummaryEntity> queryWrapper = this.getLambdaQueryWrapper();
        queryWrapper.eq(ReportSummaryEntity::getReportId,reportId);
        ReportSummaryEntity entity = this.getOne(queryWrapper);
        if(entity == null) {
            return null;
        }
        ReportSummaryResult result = new ReportSummaryResult();
        BeanUtils.copyProperties(entity,result);
        return result;
    }

    @Override
    public void deleteByReportId(Long reportId) {
        LambdaQueryWrapper<ReportSummaryEntity> queryWrapper = this.getLambdaQueryWrapper();
        queryWrapper.eq(ReportSummaryEntity::getReportId,reportId);
        this.remove(queryWrapper);
    }
}
