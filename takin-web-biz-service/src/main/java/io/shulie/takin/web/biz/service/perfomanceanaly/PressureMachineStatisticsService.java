package io.shulie.takin.web.biz.service.perfomanceanaly;

import java.util.List;

import io.shulie.takin.web.biz.pojo.request.perfomanceanaly.PressureMachineStatisticsRequest;
import io.shulie.takin.web.biz.pojo.response.perfomanceanaly.PressureMachineStatisticsResponse;
import io.shulie.takin.web.biz.pojo.response.perfomanceanaly.TypeValueDateVo;
import io.shulie.takin.web.data.param.perfomanceanaly.PressureMachineStatisticsInsertParam;
import io.shulie.takin.web.data.result.perfomanceanaly.PressureMachineStatisticsResult;

/**
 * @author mubai
 * @date 2020-11-13 11:34
 */
public interface PressureMachineStatisticsService {

    /**
     * 统计机器信息
     */
    void statistics();

    void insert(PressureMachineStatisticsInsertParam param);

    PressureMachineStatisticsResponse getNewlyStatistics();

    List<TypeValueDateVo> queryByExample(PressureMachineStatisticsRequest request);

    PressureMachineStatisticsResult getStatistics();

    void clearRubbishData();

}
