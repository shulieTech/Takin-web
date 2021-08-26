package io.shulie.takin.web.biz.service.perfomanceanaly;

import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.pojo.input.PressureMachineInput;
import io.shulie.takin.web.biz.pojo.request.perfomanceanaly.PressureMachineDeleteRequest;
import io.shulie.takin.web.biz.pojo.request.perfomanceanaly.PressureMachineUpdateRequest;
import io.shulie.takin.web.biz.pojo.response.perfomanceanaly.PressureMachineResponse;
import io.shulie.takin.web.data.param.machine.PressureMachineQueryParam;

/**
 * @author mubai
 * @date 2020-11-12 21:03
 */
public interface PressureMachineService {

    Long insert(PressureMachineInput input);

    void upload(PressureMachineInput input);

    PagingList<PressureMachineResponse> queryByExample(PressureMachineQueryParam param);

    void update(PressureMachineUpdateRequest request);

    void delete(PressureMachineDeleteRequest request);

    PressureMachineResponse getByIp(String ip);

    void updatePressureMachineStatus(Long id, Integer status);

}
