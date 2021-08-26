package io.shulie.takin.web.data.dao.perfomanceanaly;

import io.shulie.takin.web.data.param.perfomanceanaly.PressureMachineStatisticsInsertParam;
import io.shulie.takin.web.data.param.perfomanceanaly.PressureMachineStatisticsQueryParam;
import io.shulie.takin.web.data.result.perfomanceanaly.PressureMachineStatisticsResult;

import java.util.List;

/**
 * @author mubai
 * @date 2020-11-13 11:38
 */
public interface PressureMachineStatisticsDao {

    void insert(PressureMachineStatisticsInsertParam param);

    List<PressureMachineStatisticsResult> queryByExample(PressureMachineStatisticsQueryParam param);

    PressureMachineStatisticsResult getNewlyStatistics();

    PressureMachineStatisticsResult statistics();

    //清理90天之前的数据
    void clearRubbishData(String time);

}
