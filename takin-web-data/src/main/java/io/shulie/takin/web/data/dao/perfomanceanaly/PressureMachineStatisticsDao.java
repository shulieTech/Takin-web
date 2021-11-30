package io.shulie.takin.web.data.dao.perfomanceanaly;

import java.util.List;

import io.shulie.takin.web.data.param.perfomanceanaly.PressureMachineStatisticsInsertParam;
import io.shulie.takin.web.data.param.perfomanceanaly.PressureMachineStatisticsQueryParam;
import io.shulie.takin.web.data.result.perfomanceanaly.PressureMachineStatisticsResult;

/**
 * @author mubai
 * @date 2020-11-13 11:38
 */
public interface PressureMachineStatisticsDao {

    void insert(PressureMachineStatisticsInsertParam param);

    List<PressureMachineStatisticsResult> queryByExample(PressureMachineStatisticsQueryParam param);

    PressureMachineStatisticsResult getNewlyStatistics();

    PressureMachineStatisticsResult statistics();


}
