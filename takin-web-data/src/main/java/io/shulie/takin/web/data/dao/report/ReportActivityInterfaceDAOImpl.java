package io.shulie.takin.web.data.dao.report;

import java.util.List;

import javax.annotation.Resource;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.pamirs.takin.entity.domain.entity.report.ReportActivityInterfaceEntity;
import io.shulie.takin.web.data.mapper.mysql.ReportActivityInterfaceMapper;
import io.shulie.takin.web.data.param.report.ReportActivityInterfaceQueryParam;
import io.shulie.takin.web.data.param.report.ReportActivityInterfaceQueryParam.EntranceParam;
import io.shulie.takin.web.data.util.MPUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;

@Repository
public class ReportActivityInterfaceDAOImpl
    extends ServiceImpl<ReportActivityInterfaceMapper, ReportActivityInterfaceEntity>
    implements ReportActivityInterfaceDAO, MPUtil<ReportActivityInterfaceEntity> {

    @Resource
    private ReportActivityInterfaceMapper mapper;

    @Override
    public boolean batchInsert(List<ReportActivityInterfaceEntity> entityList) {
        return this.saveBatch(entityList, 200);
    }

    @Override
    public List<ReportActivityInterfaceEntity> selectByReportId(String reportId) {
        LambdaQueryWrapper<ReportActivityInterfaceEntity> wrapper = this.getLambdaQueryWrapper()
            .eq(ReportActivityInterfaceEntity::getReportId, reportId);
        return this.list(wrapper);
    }

    @Override
    public List<ReportActivityInterfaceEntity> queryByParam(ReportActivityInterfaceQueryParam param) {
        return this.list(buildQueryWrapper(param));
    }

    @Override
    public void updateInterfaceCostPercent(List<ReportActivityInterfaceEntity> entities) {
        if (CollectionUtils.isNotEmpty(entities)) {
            entities.forEach(entity -> mapper.updateInterfaceCostPercent(entity));
        }
    }

    private Wrapper<ReportActivityInterfaceEntity> buildQueryWrapper(ReportActivityInterfaceQueryParam param) {
        LambdaQueryWrapper<ReportActivityInterfaceEntity> wrapper = this.getLambdaQueryWrapper()
            .eq(ReportActivityInterfaceEntity::getReportId, param.getReportId());
        List<EntranceParam> entrances = param.getEntrances();
        if (CollectionUtils.isNotEmpty(entrances)) {
            wrapper.and(wp -> entrances.forEach(entrance -> wp.or(
                    p -> p.eq(ReportActivityInterfaceEntity::getEntranceAppName, entrance.getAppName())
                        .eq(ReportActivityInterfaceEntity::getEntranceServiceName, entrance.getServiceName())
                        .eq(ReportActivityInterfaceEntity::getEntranceMethodName, entrance.getMethodName())
                        .eq(ReportActivityInterfaceEntity::getEntranceRpcType, entrance.getRpcType())
                )));
        }
        String sortField = param.getSortField();
        if (StringUtils.isNotBlank(sortField)) {
            // 驼峰转下划线
            wrapper.last(" order by " + StrUtil.toUnderlineCase(sortField) + " " + param.getSortType() + " ");
        }
        return wrapper;
    }
}
