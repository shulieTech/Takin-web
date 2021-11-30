package io.shulie.takin.web.data.mapper.mysql;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.shulie.takin.web.data.model.mysql.ReportMachineEntity;
import org.apache.ibatis.annotations.Param;

public interface ReportMachineMapper extends BaseMapper<ReportMachineEntity> {

    int insertOrUpdate(ReportMachineEntity entity);

    /**
     * 分应用数、汇总总机器数、风险机器数
     *
     * @param reportId
     * @return
     */
    List<Map<String, Object>> selectCountByReport(@Param("reportId") Long reportId);

    /**
     * 更新机器tps指标数据
     *
     * @param entity
     * @return
     */
    int updateTpsTargetConfig(ReportMachineEntity entity);


    /**
     * 更新机器风险数据
     *
     * @param entity
     * @return
     */
    int updateRiskContent(ReportMachineEntity entity);



}
