package io.shulie.takin.web.biz.service.scenemanage;

import cn.hutool.http.HttpRequest;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.biz.pojo.request.scene.*;
import io.shulie.takin.web.biz.pojo.response.scene.BenchmarkSuiteResponse;
import org.springframework.http.server.ServerHttpRequest;

public interface MachineManageService {

    String create(PressureMachineCreateRequest request);

    PagingList<PressureMachineResponse> list(PressureMachineQueryRequest request, ServerHttpRequest httpRequest);

    void update(PressureMachineUpdateRequest request);

    void delete(PressureMachineBaseRequest request);

    String enable(PressureMachineBaseRequest request);

    String disable(PressureMachineBaseRequest request);

    void syncMachine();

    String benchmarkEnable(PressureMachineBaseRequest request,ServerHttpRequest httpRequest);

    PagingList<BenchmarkSuiteResponse> benchmarkSuiteList(BenchmarkSuitePageRequest request, ServerHttpRequest httpRequest);

    ResponseResult<String> deployProgress(Long id);
}
