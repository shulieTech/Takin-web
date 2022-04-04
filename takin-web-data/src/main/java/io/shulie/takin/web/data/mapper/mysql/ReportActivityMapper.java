package io.shulie.takin.web.data.mapper.mysql;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pamirs.takin.entity.domain.entity.report.ReportActivityEntity;
import org.apache.ibatis.annotations.Update;

public interface ReportActivityMapper extends BaseMapper<ReportActivityEntity> {

    @Update("update t_report_activity set avg_cost = #{avgCost} where report_id = #{reportId}"
        + " and app_name = #{appName} and service_name = #{serviceName} and method_name = #{methodName} "
        + " and rpc_type = #{rpcType}")
    void updateEntranceAvgCost(ReportActivityEntity entity);
}
