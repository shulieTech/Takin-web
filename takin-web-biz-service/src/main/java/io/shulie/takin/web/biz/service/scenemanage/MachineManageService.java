package io.shulie.takin.web.biz.service.scenemanage;

import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.biz.pojo.request.scene.*;
import io.shulie.takin.web.biz.pojo.response.scene.BenchmarkSuiteResponse;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public interface MachineManageService {

    String create(PressureMachineCreateRequest request);

    PagingList<PressureMachineResponse> list(PressureMachineQueryRequest request, HttpServletRequest httpRequest);

    void update(PressureMachineUpdateRequest request);

    String delete(PressureMachineBaseRequest request, HttpServletRequest httpRequest);

    String enable(PressureMachineBaseRequest request);

    String disable(PressureMachineBaseRequest request,HttpServletRequest httpRequest);

    void syncMachine();

    String benchmarkEnable(PressureMachineBaseRequest request,HttpServletRequest httpRequest);

    PagingList<BenchmarkSuiteResponse> benchmarkSuiteList(BenchmarkSuitePageRequest request, HttpServletRequest httpRequest);

    String readExcelBachtCreate(MultipartFile file) throws IOException;
}
