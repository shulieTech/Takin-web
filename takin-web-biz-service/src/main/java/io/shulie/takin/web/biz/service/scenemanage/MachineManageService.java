package io.shulie.takin.web.biz.service.scenemanage;

import io.shulie.takin.common.beans.page.PagingDevice;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.pojo.request.scene.PressureMachineBaseRequest;
import io.shulie.takin.web.biz.pojo.request.scene.PressureMachineCreateRequest;
import io.shulie.takin.web.biz.pojo.request.scene.PressureMachineResponse;
import io.shulie.takin.web.biz.pojo.request.scene.PressureMachineUpdateRequest;

public interface MachineManageService {

    String create(PressureMachineCreateRequest request);

    PagingList<PressureMachineResponse> list(PagingDevice pagingDevice);

    void update(PressureMachineUpdateRequest request);

    void delete(PressureMachineBaseRequest request);

    String enable(PressureMachineBaseRequest request);

    String disable(PressureMachineBaseRequest request);

    void syncMachine();
}
