package io.shulie.takin.web.data.mapper.mysql;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pamirs.takin.entity.domain.entity.report.ReportActivityInterfaceEntity;
import org.apache.ibatis.annotations.Update;

public interface ReportActivityInterfaceMapper extends BaseMapper<ReportActivityInterfaceEntity> {

    @Update("update t_report_activity_interface set service_avg_cost = #{serviceAvgCost}, "
        + " cost_percent = (avg_cost * 100 / service_avg_cost)"
        + " where report_id = #{reportId} and entrance_app_name = #{entranceAppName}"
        + " and entrance_service_name = #{entranceServiceName} and entrance_method_name = #{entranceMethodName}"
        + " and entrance_rpc_type = #{entranceRpcType}")
    void updateInterfaceCostPercent(ReportActivityInterfaceEntity entity);
}
