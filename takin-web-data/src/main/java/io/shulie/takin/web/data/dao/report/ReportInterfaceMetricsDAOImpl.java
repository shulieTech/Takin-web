package io.shulie.takin.web.data.dao.report;

import java.util.List;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pamirs.takin.entity.domain.entity.report.ReportInterfaceMetricsEntity;
import io.shulie.takin.web.data.mapper.mysql.ReportInterfaceMetricsMapper;
import io.shulie.takin.web.data.param.report.ReportInterfaceMetricsQueryParam;
import io.shulie.takin.web.data.param.report.ReportInterfaceMetricsQueryParam.MetricsServiceParam;
import io.shulie.takin.web.data.util.MPUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Repository;

@Repository
public class ReportInterfaceMetricsDAOImpl extends ServiceImpl<ReportInterfaceMetricsMapper, ReportInterfaceMetricsEntity>
    implements ReportInterfaceMetricsDAO, MPUtil<ReportInterfaceMetricsEntity> {

    @Override
    public boolean batchInsert(List<ReportInterfaceMetricsEntity> entityList) {
        return this.saveBatch(entityList, 200);
    }

    @Override
    public List<ReportInterfaceMetricsEntity> queryByParam(ReportInterfaceMetricsQueryParam param) {
        return list(buildQueryWrapper(param));
    }

    private Wrapper<ReportInterfaceMetricsEntity> buildQueryWrapper(ReportInterfaceMetricsQueryParam param) {
        LambdaQueryWrapper<ReportInterfaceMetricsEntity> wrapper = this.getLambdaQueryWrapper()
            .eq(ReportInterfaceMetricsEntity::getReportId, param.getReportId());
        List<MetricsServiceParam> services = param.getServices();
        if (CollectionUtils.isNotEmpty(services)) {
            wrapper.and(wp -> services.forEach(entrance -> wp.or(
                    p -> p.eq(ReportInterfaceMetricsEntity::getAppName, entrance.getAppName())
                        .eq(ReportInterfaceMetricsEntity::getServiceName, entrance.getServiceName())
                        .eq(ReportInterfaceMetricsEntity::getMethodName, entrance.getMethodName())
                        .eq(ReportInterfaceMetricsEntity::getRpcType, entrance.getRpcType())
                        .eq(ReportInterfaceMetricsEntity::getEntranceAppName, entrance.getEntranceAppName())
                        .eq(ReportInterfaceMetricsEntity::getEntranceServiceName, entrance.getEntranceServiceName())
                        .eq(ReportInterfaceMetricsEntity::getEntranceMethodName, entrance.getEntranceMethodName())
                        .eq(ReportInterfaceMetricsEntity::getEntranceRpcType, entrance.getEntranceRpcType())
                )));
        }
        return wrapper;
    }
}
