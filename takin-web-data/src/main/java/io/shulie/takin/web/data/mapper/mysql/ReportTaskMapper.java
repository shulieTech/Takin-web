package io.shulie.takin.web.data.mapper.mysql;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.pamirs.takin.entity.domain.entity.report.ReportTaskEntity;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface ReportTaskMapper extends BaseMapper<ReportTaskEntity> {

    @Update("update t_report_task set `state` = '1', gmt_update = now() where report_id = #{reportId}")
    void startSync(@Param("reportId") String reportId);

    @Update("update t_report_task set `state` = '2', gmt_update = now() where report_id = #{reportId}")
    void syncSuccess(@Param("reportId") String reportId);
}
