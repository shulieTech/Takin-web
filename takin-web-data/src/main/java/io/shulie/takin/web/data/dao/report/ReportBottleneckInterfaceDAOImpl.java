package io.shulie.takin.web.data.dao.report;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import io.shulie.takin.web.data.param.report.ReportLocalQueryParam;
import io.shulie.takin.web.common.util.CommonUtil;
import io.shulie.takin.web.data.mapper.mysql.ReportBottleneckInterfaceMapper;
import io.shulie.takin.web.data.model.mysql.ReportBottleneckInterfaceEntity;
import io.shulie.takin.web.data.param.report.ReportBottleneckInterfaceCreateParam;
import io.shulie.takin.web.data.result.report.ReportBottleneckInterfaceResult;
import io.shulie.takin.web.data.util.MPUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

/**
 * @author by: hezhongqi
 * @Package io.shulie.takin.web.data.dao.report
 * @ClassName: TReportBottleneckInterfaceDAO
 * @Description: TODO
 * @Date: 2021/11/23 10:49
 */
@Component
public class ReportBottleneckInterfaceDAOImpl extends ServiceImpl<ReportBottleneckInterfaceMapper, ReportBottleneckInterfaceEntity>
    implements ReportBottleneckInterfaceDAO, MPUtil<ReportBottleneckInterfaceEntity> {
    @Override
    public void insertBatch(List<ReportBottleneckInterfaceCreateParam> params) {
       List<ReportBottleneckInterfaceEntity> entities = CommonUtil.list2list(params,ReportBottleneckInterfaceEntity.class);
       this.saveBatch(entities);
    }

    /**
     *  select
     *         <include refid="Base_Column_List"/>
     *         from t_report_bottleneck_interface
     *         where report_id = #{reportId,jdbcType=BIGINT}
     *         order by sort_no
     * @param queryParam
     * @return
     */
    @Override
    public List<ReportBottleneckInterfaceResult> selectByExample(ReportLocalQueryParam queryParam) {
        LambdaQueryWrapper<ReportBottleneckInterfaceEntity> queryWrapper = this.getLambdaQueryWrapper();
        queryWrapper.eq(ReportBottleneckInterfaceEntity::getReportId,queryParam.getReportId());
        queryWrapper.orderByDesc(ReportBottleneckInterfaceEntity::getSortNo);
        List<ReportBottleneckInterfaceEntity> list = this.list(queryWrapper);
        if(CollectionUtils.isEmpty(list)) {
            return Lists.newArrayList();
        }
        return CommonUtil.list2list(list, ReportBottleneckInterfaceResult.class);
    }

    @Override
    public Long selectCountByReportId(Long reportId) {
        LambdaQueryWrapper<ReportBottleneckInterfaceEntity> queryWrapper = this.getLambdaQueryWrapper();
        queryWrapper.eq(ReportBottleneckInterfaceEntity::getReportId,reportId);
        return this.count(queryWrapper);
    }

    @Override
    public void deleteByReportId(Long reportId) {
        LambdaQueryWrapper<ReportBottleneckInterfaceEntity> queryWrapper = this.getLambdaQueryWrapper();
        queryWrapper.eq(ReportBottleneckInterfaceEntity::getReportId,reportId);
        this.remove(queryWrapper);
    }
}
