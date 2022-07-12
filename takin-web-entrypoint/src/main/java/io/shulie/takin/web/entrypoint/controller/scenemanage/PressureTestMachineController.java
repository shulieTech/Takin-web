package io.shulie.takin.web.entrypoint.controller.scenemanage;

import io.shulie.takin.cloud.entrypoint.machine.CloudMachineApi;
import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import io.shulie.takin.cloud.sdk.model.request.machine.MachineAddReq;
import io.shulie.takin.cloud.sdk.model.request.machine.MachineBaseReq;
import io.shulie.takin.cloud.sdk.model.request.machine.MachineUpdateReq;
import io.shulie.takin.cloud.sdk.model.response.machine.NodeMetricsResp;
import io.shulie.takin.common.beans.annotation.ActionTypeEnum;
import io.shulie.takin.common.beans.annotation.AuthVerification;
import io.shulie.takin.common.beans.annotation.ModuleDef;
import io.shulie.takin.common.beans.page.PagingDevice;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.biz.constant.BizOpConstants;
import io.shulie.takin.web.biz.pojo.request.scene.*;
import io.shulie.takin.web.biz.pojo.response.placeholdermanage.PlaceholderManageResponse;
import io.shulie.takin.web.biz.service.scenemanage.MachineManageService;
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
    private MachineManageService machineManageService;

    @PostMapping()
    @ApiOperation("添加压力机")
    @AuthVerification(needAuth = ActionTypeEnum.CREATE, moduleCode = BizOpConstants.ModuleCode.PRESSURE_TEST_MACHINE)
    public ResponseResult<String> create(@RequestBody @Valid PressureMachineCreateRequest request) {
        String failContent = machineManageService.create(request);
        if (failContent != null){
            return ResponseResult.fail("添加失败",failContent);
        }
        return ResponseResult.success("添加成功");
    }


    @GetMapping("/list")
    @ApiOperation("压力机列表")
    @AuthVerification(needAuth = ActionTypeEnum.QUERY, moduleCode = BizOpConstants.ModuleCode.PRESSURE_TEST_MACHINE)
    public PagingList<PressureMachineResponse> list(PagingDevice pagingDevice) {
        return machineManageService.list(pagingDevice);
    }


    @PutMapping()
    @ApiOperation("修改压力机")
    @AuthVerification(needAuth = ActionTypeEnum.UPDATE, moduleCode = BizOpConstants.ModuleCode.PRESSURE_TEST_MACHINE)
    public ResponseResult<String> update(@RequestBody @Valid PressureMachineUpdateRequest request) {
        machineManageService.update(request);
        return ResponseResult.success("更新成功");
    }

    @DeleteMapping()
    @ApiOperation("删除压力机")
    @AuthVerification(needAuth = ActionTypeEnum.DELETE, moduleCode = BizOpConstants.ModuleCode.PRESSURE_TEST_MACHINE)
    public ResponseResult<String> delete(@RequestBody @Valid PressureMachineBaseRequest request) {
        machineManageService.delete(request);
        return ResponseResult.success("删除成功");
    }

    @PostMapping("/enable")
    @ApiOperation("部署压力机")
    @AuthVerification(needAuth = ActionTypeEnum.ENABLE_DISABLE, moduleCode = BizOpConstants.ModuleCode.PRESSURE_TEST_MACHINE)
    public ResponseResult<String> enable(@RequestBody @Valid PressureMachineBaseRequest request) {
        String failContent = machineManageService.enable(request);
        if (failContent != null){
            return ResponseResult.fail("部署失败",failContent);
        }
        return ResponseResult.success("部署成功");
    }

    @PostMapping("/disable")
    @ApiOperation("卸载压力机")
    @AuthVerification(needAuth = ActionTypeEnum.ENABLE_DISABLE, moduleCode = BizOpConstants.ModuleCode.PRESSURE_TEST_MACHINE)
    public ResponseResult<String> disable(@RequestBody @Valid PressureMachineBaseRequest request) {
        String failContent = machineManageService.disable(request);
        if (failContent != null){
            return ResponseResult.fail("卸载失败",failContent);
        }
        return ResponseResult.success("卸载成功");
    }

    @GetMapping("/syncMachine")
    @ApiOperation("同步机器信息")
    @AuthVerification(needAuth = ActionTypeEnum.CREATE, moduleCode = BizOpConstants.ModuleCode.PRESSURE_TEST_MACHINE)
    public ResponseResult<String> syncMachine() {
        machineManageService.syncMachine();
        return ResponseResult.success("同步成功");
    }
}
