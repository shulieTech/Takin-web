package io.shulie.takin.web.biz.service.perfomanceanaly;

import io.shulie.takin.web.biz.pojo.request.perfomanceanaly.PressureMachineLogQueryRequest;
import io.shulie.takin.web.biz.pojo.response.perfomanceanaly.PressureMachineLogResponse;

/**
 * @author mubai
 * @date 2020-11-16 13:52
 */
public interface PressureMachineLogService {

    PressureMachineLogResponse queryByExample(PressureMachineLogQueryRequest request);

}
