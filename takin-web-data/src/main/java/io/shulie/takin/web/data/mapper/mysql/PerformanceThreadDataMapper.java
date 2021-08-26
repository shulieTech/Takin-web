package io.shulie.takin.web.data.mapper.mysql;

import java.util.List;

import io.shulie.takin.web.data.model.mysql.PerformanceThreadDataEntity;
import io.shulie.takin.web.data.result.perfomanceanaly.PerformanceThreadCountResult;
import org.apache.ibatis.annotations.Select;

/**
* @author qianshui
 * @date 2020/11/4 下午2:21
 */
public interface PerformanceThreadDataMapper extends MyBatisPlusMapper<PerformanceThreadDataEntity> {

    @Select("SELECT base_id, count(id) as threadCount " +
        "from t_performance_thread_data " +
        "where base_id in " +
        "<foreach item=\'item\' index=\'index\' collection=\'list\' open=\'(\' separator=\',\' close=\')\'>" +
        "#{item}" +
        "</foreach>"
        + " group by base_id")
    List<PerformanceThreadCountResult> selectPerformanceThreadCount(List<Long> baseIds);
}
