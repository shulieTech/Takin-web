package io.shulie.takin.web.biz.service.scenemanage;

import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.pojo.request.scene.*;

public interface MachineManageService {

    String create(PressureMachineCreateRequest request);

    PagingList<PressureMachineResponse> list(PressureMachineQueryRequest request);

    void update(PressureMachineUpdateRequest request);

    void delete(PressureMachineBaseRequest request);

    String enable(PressureMachineBaseRequest request);

    String disable(PressureMachineBaseRequest request);

    void syncMachine();
}
