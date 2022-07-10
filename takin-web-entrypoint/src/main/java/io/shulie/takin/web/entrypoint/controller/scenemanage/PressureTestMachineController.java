package io.shulie.takin.web.entrypoint.controller.scenemanage;

import io.shulie.takin.cloud.entrypoint.machine.CloudMachineApi;
import io.shulie.takin.cloud.sdk.model.request.machine.MachineAddReq;
import io.shulie.takin.cloud.sdk.model.request.machine.MachineBaseReq;
import io.shulie.takin.cloud.sdk.model.request.machine.MachineUpdateReq;
import io.shulie.takin.cloud.sdk.model.response.machine.NodeMetricsResp;
import io.shulie.takin.common.beans.annotation.ActionTypeEnum;
import io.shulie.takin.common.beans.annotation.AuthVerification;
import io.shulie.takin.common.beans.annotation.ModuleDef;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.biz.constant.BizOpConstants;
import io.shulie.takin.web.biz.pojo.request.scene.*;
import io.shulie.takin.web.common.util.BeanCopyUtils;
import io.shulie.takin.web.data.param.machine.PressureMachineUpdateParam;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/pressureMachine")
@Api(tags = "压力机管理", value = "压力机管理")
public class PressureTestMachineController {

    @Resource
    private CloudMachineApi cloudMachineApi;

    @PostMapping()
    @ApiOperation("添加压力机")
    @AuthVerification(needAuth = ActionTypeEnum.CREATE, moduleCode = BizOpConstants.ModuleCode.PRESSURE_TEST_MACHINE)
    public ResponseResult<String> create(@RequestBody @Valid PressureMachineCreateRequest request) {
        MachineAddReq addReq = new MachineAddReq();
        addReq.setNodeIp(request.getNodeIp());
        addReq.setName(request.getName());
        addReq.setPassword(request.getPassword());
        WebPluginUtils.fillCloudUserData(addReq);

        String add = cloudMachineApi.add(addReq);
        return ResponseResult.success(add);
    }


    @GetMapping("/list")
    @ApiOperation("压力机列表")
    @AuthVerification(needAuth = ActionTypeEnum.QUERY, moduleCode = BizOpConstants.ModuleCode.PRESSURE_TEST_MACHINE)
    public ResponseResult<List<PressureMachineResponse>> list() {
        ResponseResult<List<NodeMetricsResp>> list = cloudMachineApi.list();
        List<PressureMachineResponse> pressureMachineResponses = BeanCopyUtils.copyList(list.getData(), PressureMachineResponse.class);
        return ResponseResult.success(pressureMachineResponses);
    }


    @PutMapping()
    @ApiOperation("修改压力机")
    @AuthVerification(needAuth = ActionTypeEnum.UPDATE, moduleCode = BizOpConstants.ModuleCode.PRESSURE_TEST_MACHINE)
    public ResponseResult<Boolean> create(@RequestBody @Valid PressureMachineUpdateRequest request) {
        MachineUpdateReq updateReq = new MachineUpdateReq();
        updateReq.setNodeName(request.getName());
        updateReq.setUpdateName(request.getUpdateName());
        WebPluginUtils.fillCloudUserData(updateReq);
        Boolean update = cloudMachineApi.update(updateReq);
        return ResponseResult.success(update);
    }

    @DeleteMapping()
    @ApiOperation("删除压力机")
    @AuthVerification(needAuth = ActionTypeEnum.DELETE, moduleCode = BizOpConstants.ModuleCode.PRESSURE_TEST_MACHINE)
    public ResponseResult<Boolean> delete(@RequestBody @Valid PressureMachineBaseRequest request) {
        MachineBaseReq baseReq = new MachineBaseReq();
        baseReq.setNodeName(request.getName());
        WebPluginUtils.fillCloudUserData(baseReq);
        Boolean update = cloudMachineApi.delete(baseReq);
        return ResponseResult.success(update);
    }

    @PostMapping("/enable")
    @ApiOperation("启用压力机")
    @AuthVerification(needAuth = ActionTypeEnum.ENABLE_DISABLE, moduleCode = BizOpConstants.ModuleCode.PRESSURE_TEST_MACHINE)
    public ResponseResult<Boolean> enable(@RequestBody @Valid PressureMachineBaseRequest request) {
        MachineBaseReq baseReq = new MachineBaseReq();
        baseReq.setNodeName(request.getName());
        WebPluginUtils.fillCloudUserData(baseReq);
        Boolean enable = cloudMachineApi.enable(baseReq);
        return ResponseResult.success(enable);
    }

    @PostMapping("/disable")
    @ApiOperation("禁用压力机")
    @AuthVerification(needAuth = ActionTypeEnum.ENABLE_DISABLE, moduleCode = BizOpConstants.ModuleCode.PRESSURE_TEST_MACHINE)
    public ResponseResult<Boolean> disable(@RequestBody @Valid PressureMachineBaseRequest request) {
        MachineBaseReq baseReq = new MachineBaseReq();
        baseReq.setNodeName(request.getName());
        WebPluginUtils.fillCloudUserData(baseReq);
        Boolean disable = cloudMachineApi.disable(baseReq);
        return ResponseResult.success(disable);
    }
}
