package io.shulie.takin.web.entrypoint.controller.scenemanage;

import io.shulie.takin.common.beans.annotation.ActionTypeEnum;
import io.shulie.takin.common.beans.annotation.AuthVerification;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.biz.constant.BizOpConstants;
import io.shulie.takin.web.biz.pojo.request.scene.*;
import io.shulie.takin.web.biz.pojo.response.scene.BenchmarkSuiteResponse;
import io.shulie.takin.web.biz.service.scenemanage.MachineManageService;
import io.shulie.takin.web.biz.utils.ExcelUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Arrays;
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
        if (failContent != null) {
            return ResponseResult.fail("添加失败" + failContent, null);
        }
        return ResponseResult.success("添加成功");
    }


    @GetMapping("/list")
    @ApiOperation("压力机列表")
    @AuthVerification(needAuth = ActionTypeEnum.QUERY, moduleCode = BizOpConstants.ModuleCode.PRESSURE_TEST_MACHINE)
    public PagingList<PressureMachineResponse> list(PressureMachineQueryRequest request, HttpServletRequest httpRequest) {
        return machineManageService.list(request, httpRequest);
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
    public ResponseResult<String> delete(@RequestBody @Valid PressureMachineBaseRequest request, HttpServletRequest httpRequest) {
        String failContent = machineManageService.delete(request, httpRequest);
        if (failContent != null) {
            return ResponseResult.fail("删除失败:" + failContent, null);
        }
        return ResponseResult.success("删除成功");
    }

    @PostMapping("/enable")
    @ApiOperation("takin-部署压力机")
    @AuthVerification(needAuth = ActionTypeEnum.ENABLE_DISABLE, moduleCode = BizOpConstants.ModuleCode.PRESSURE_TEST_MACHINE)
    public ResponseResult<String> enable(@RequestBody @Valid PressureMachineBaseRequest request) {
        String failContent = machineManageService.enable(request);
        if (failContent != null) {
            return ResponseResult.fail("部署失败" + failContent, null);
        }
        return ResponseResult.success("部署成功");
    }

    @PostMapping("/benchmarkEnable")
    @ApiOperation("benchmark-部署压力机")
    @AuthVerification(needAuth = ActionTypeEnum.ENABLE_DISABLE, moduleCode = BizOpConstants.ModuleCode.PRESSURE_TEST_MACHINE)
    public ResponseResult<String> benchmarkEnable(@RequestBody @Valid PressureMachineBaseRequest request, HttpServletRequest httpRequest) {
        String failContent = machineManageService.benchmarkEnable(request, httpRequest);
        if (failContent != null) {
            return ResponseResult.fail("部署失败" + failContent, null);
        }
        return ResponseResult.success("部署成功");
    }

    @GetMapping("/benchmarkSuiteList")
    @ApiOperation("benchmark组件列表")
    @AuthVerification(needAuth = ActionTypeEnum.QUERY, moduleCode = BizOpConstants.ModuleCode.PRESSURE_TEST_MACHINE)
    public PagingList<BenchmarkSuiteResponse> benchmarkSuiteList(BenchmarkSuitePageRequest request, HttpServletRequest httpRequest) {
        return machineManageService.benchmarkSuiteList(request, httpRequest);
    }


    @PostMapping("/disable")
    @ApiOperation("卸载压力机")
    @AuthVerification(needAuth = ActionTypeEnum.ENABLE_DISABLE, moduleCode = BizOpConstants.ModuleCode.PRESSURE_TEST_MACHINE)
    public ResponseResult<String> disable(@RequestBody @Valid PressureMachineBaseRequest request, HttpServletRequest httpRequest) {
        String failContent = machineManageService.disable(request, httpRequest);
        if (failContent != null) {
            return ResponseResult.fail("卸载失败" + failContent, null);
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

    @PostMapping("/createMachinebByExecl")
    @ApiOperation("根据excel批量新增机器")
//    @ApiImplicitParams({@ApiImplicitParam(name = "file", value = "导入的 excel", paramType = "form", dataType = "file")})
//    @AuthVerification(needAuth = ActionTypeEnum.CREATE, moduleCode = BizOpConstants.ModuleCode.PRESSURE_TEST_MACHINE)
    public void createMachinebByExecl(@RequestParam MultipartFile file) {
        //校验文件
        MultipartFile[] files = {file};
        try {
            new ExcelUtil().verify(files);
            this.machineManageService.readExcelBachtCreate(file);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/benchmarkEnableByTag")
    @ApiOperation("benchmark-批量部署压力机根据tag")
//    @AuthVerification(needAuth = ActionTypeEnum.ENABLE_DISABLE, moduleCode = BizOpConstants.ModuleCode.PRESSURE_TEST_MACHINE)
    public ResponseResult<String> benchmarkEnableByTag(@RequestBody String tag, HttpServletRequest httpRequest) {
        String failContent = machineManageService.benchmarkEnableByTag(httpRequest, tag);
        if (failContent != null) {
            return ResponseResult.fail("部署失败" + failContent, null);
        }
        return ResponseResult.success("部署成功");
    }

    @PostMapping("/getAllTag")
    @ApiOperation("benchmark-获取所有机器tag")
    @AuthVerification(needAuth = ActionTypeEnum.QUERY, moduleCode = BizOpConstants.ModuleCode.PRESSURE_TEST_MACHINE)
    public ResponseResult<List<String>> getAllTag() {
        return ResponseResult.success(this.machineManageService.getAllTag());
    }

    @PostMapping("/listMachinesByTag")
    @ApiOperation("benchmark-批量获取压力机根据tag")
    @AuthVerification(needAuth = ActionTypeEnum.ENABLE_DISABLE, moduleCode = BizOpConstants.ModuleCode.PRESSURE_TEST_MACHINE)
    public ResponseResult<PagingList<PressureMachineResponse>> listMachinesByTag(PressureMachineQueryByTagRequest request, HttpServletRequest httpRequest) {
        return ResponseResult.success(this.machineManageService.listMachinesByTag(httpRequest, request));
    }

    @PostMapping("/upload")
    public void upload(@RequestBody MultipartFile multipartFile) {
        //校验文件
        MultipartFile[] files = {multipartFile};
        try {
            new ExcelUtil().verify(files);
            this.machineManageService.readExcelBachtCreate(multipartFile);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/upload2")
    public void upload2(@RequestParam(value = "multipartFile", required = false) MultipartFile multipartFile) {
        //校验文件
        MultipartFile[] files = {multipartFile};
        try {
            new ExcelUtil().verify(files);
            this.machineManageService.readExcelBachtCreate(multipartFile);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/upload3")
    public void upload3(@RequestParam MultipartFile multipartFile) {
        //校验文件
        MultipartFile[] files = {multipartFile};
        try {
            new ExcelUtil().verify(files);
            this.machineManageService.readExcelBachtCreate(multipartFile);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}