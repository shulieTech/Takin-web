package io.shulie.takin.web.data.mapper.mysql;

import io.shulie.takin.web.data.model.mysql.PerformanceBaseDataEntity;
import io.shulie.takin.web.data.param.perfomanceanaly.PerformanceBaseQueryParam;
import org.apache.ibatis.annotations.Select;

/**
 * @author qianshui
 * @date 2020/11/4 下午2:21
 */
public interface PerformanceBaseDataMapper extends MyBatisPlusMapper<PerformanceBaseDataEntity> {

    @Select("select agent_id, app_name, app_ip, process_id, process_name from t_performance_base_data"
        + " where timestamp >= #{startTime} and timestamp <= #{endTime}"
        + " and app_name=#{appName} and app_ip=#{appIp} and agent_id=#{agentId} limit 1")
    PerformanceBaseDataEntity selectOneRecord(PerformanceBaseQueryParam param);
}
