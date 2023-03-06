package io.shulie.takin.web.biz.service.scenemanage;

import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.biz.pojo.request.scene.*;
import io.shulie.takin.web.biz.pojo.response.scene.BenchmarkSuiteResponse;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

public interface MachineManageService {

    String create(PressureMachineCreateRequest request);

    PagingList<PressureMachineResponse> list(PressureMachineQueryRequest request, HttpServletRequest httpRequest);

    void update(PressureMachineUpdateRequest request);

    String delete(PressureMachineBaseRequest request, HttpServletRequest httpRequest);

    String enable(PressureMachineBaseRequest request);

    String disable(PressureMachineBaseRequest request, HttpServletRequest httpRequest);

    void syncMachine();

    String benchmarkEnable(PressureMachineBaseRequest request, HttpServletRequest httpRequest);

    PagingList<BenchmarkSuiteResponse> benchmarkSuiteList(BenchmarkSuitePageRequest request, HttpServletRequest httpRequest);

    ResponseResult<String> readExcelBachtCreate(MultipartFile file);

    /**
     * 根据tag批量部署机器
     *
     * @param request
     * @return
     */
    ResponseResult<String> benchmarkEnableByTag(HttpServletRequest httpRequest, BenchmarkMachineDeployRequest request);

    /**
     * 获取所有tag
     *
     * @return
     */
    List<String> getAllTag();

    /**
     * 根据tag获取机器列表
     *
     * @param request
     * @return
     */
    List<PressureMachineResponse> listMachinesByTag(HttpServletRequest httpRequest, PressureMachineQueryByTagRequest request);

    /**
     * 根据机器id列表获取机器信息
     *
     * @param request
     * @param httpRequest
     * @return
     */
    ResponseResult<List<PressureMachineResponse>> listMachinesByIds(@RequestBody PressureMachineQueryByTagRequest request, HttpServletRequest httpRequest);

    /**
     * 获取机器信息
     *
     * @param request
     * @param httpRequest
     * @return
     */
    ResponseResult<List<PressureMachineResponse>> listMachines(@RequestBody PressureMachineQueryByTagRequest request, HttpServletRequest httpRequest);

    /**
     * 外网安装，只选择一台机器拉取tar包，然后自动分发到所有机器，最后倒入镜像部署
     *
     * @param request
     * @param httpRequest
     * @return
     */
    String benchmarkEnableExternal(PressureMachineLoadRequest request, HttpServletRequest httpRequest);

}
