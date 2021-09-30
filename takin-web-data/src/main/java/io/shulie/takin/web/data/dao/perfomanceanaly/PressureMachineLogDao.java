package io.shulie.takin.web.data.dao.perfomanceanaly;

import java.util.List;

import io.shulie.takin.web.data.param.machine.PressureMachineLogInsertParam;
import io.shulie.takin.web.data.param.machine.PressureMachineLogQueryParam;
import io.shulie.takin.web.data.result.perfomanceanaly.PressureMachineLogResult;

/**
 * @author mubai
 * @date 2020-11-16 10:19
 */
public interface PressureMachineLogDao {

    void insert(PressureMachineLogInsertParam param);

    List<PressureMachineLogResult> queryList(PressureMachineLogQueryParam queryParam);
}
