package io.shulie.takin.web.data.dao.report;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import io.shulie.takin.web.common.util.DataTransformUtil;
import io.shulie.takin.web.data.mapper.mysql.ReportApplicationSummaryMapper;
import io.shulie.takin.web.data.model.mysql.ReportApplicationSummaryEntity;
import io.shulie.takin.web.data.param.report.ReportApplicationSummaryCreateParam;
import io.shulie.takin.web.data.param.report.ReportApplicationSummaryQueryParam;
import io.shulie.takin.web.data.result.report.ReportApplicationSummaryResult;
import io.shulie.takin.web.data.util.MPUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

/**
 * @author by: hezhongqi
 * @Package io.shulie.takin.web.data.result.report
 * @ClassName: ReportApplicationSummaryDAO
 * @Description: TODO
 * @Date: 2021/11/23 11:01
 */
@Component
public class ReportApplicationSummaryDAOImpl extends ServiceImpl<ReportApplicationSummaryMapper, ReportApplicationSummaryEntity>
    implements ReportApplicationSummaryDAO, MPUtil<ReportApplicationSummaryEntity> {

    @Override
    public void insertOrUpdate(ReportApplicationSummaryCreateParam param) {
        ReportApplicationSummaryEntity entity = new ReportApplicationSummaryEntity();
        BeanUtils.copyProperties(param,entity);
        this.baseMapper.insertOrUpdate(entity);
    }

    /**
     *  select
     *         <include refid="Base_Column_List"/>
     *         from t_report_application_summary
     *         where report_id = #{reportId,jdbcType=BIGINT}
     *         <if test="applicationName != null">
     *             and application_name = #{applicationName,jdbcType=VARCHAR}
     *         </if>
     *         order by machine_risk_count desc, machine_total_count desc, application_name asc
     * @param param
     * @return
     */
    @Override
    public List<ReportApplicationSummaryResult> selectByParam(ReportApplicationSummaryQueryParam param) {
        LambdaQueryWrapper<ReportApplicationSummaryEntity> queryWrapper = this.getLambdaQueryWrapper();
        queryWrapper.eq(ReportApplicationSummaryEntity::getReportId,param.getReportId());
        if(StringUtils.isNotBlank(param.getApplicationName())) {
            queryWrapper.eq(ReportApplicationSummaryEntity::getApplicationName,param.getApplicationName());
        }
        queryWrapper.orderByDesc(ReportApplicationSummaryEntity::getMachineRiskCount);
        queryWrapper.orderByDesc(ReportApplicationSummaryEntity::getMachineTotalCount);
        queryWrapper.orderByAsc(ReportApplicationSummaryEntity::getApplicationName);
        List<ReportApplicationSummaryEntity> list = this.list(queryWrapper);
        if(CollectionUtils.isEmpty(list)) {
            return Lists.newArrayList();
        }
        return DataTransformUtil.list2list(list, ReportApplicationSummaryResult.class);
    }

    @Override
    public Map<String, Object> selectCountByReportId(Long reportId) {
        return this.baseMapper.selectCountByReportId(reportId);
    }

    @Override
    public void deleteByReportId(Long reportId) {
        LambdaQueryWrapper<ReportApplicationSummaryEntity> queryWrapper = this.getLambdaQueryWrapper();
        queryWrapper.eq(ReportApplicationSummaryEntity::getReportId,reportId);
        this.remove(queryWrapper);
    }
}
