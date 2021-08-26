package io.shulie.takin.web.data.dao.perfomanceanaly;

import io.shulie.takin.web.data.param.machine.PressureMachineLogInsertParam;
import io.shulie.takin.web.data.param.machine.PressureMachineLogQueryParam;
import io.shulie.takin.web.data.result.perfomanceanaly.PressureMachineLogResult;

import java.util.List;

/**
 * @author mubai
 * @date 2020-11-16 10:19
 */
public interface PressureMachineLogDao {

    void insert(PressureMachineLogInsertParam param);

    List<PressureMachineLogResult> queryList(PressureMachineLogQueryParam queryParam);

    // 清理指定时间之前的数据
    void clearRubbishData(String time);

}
