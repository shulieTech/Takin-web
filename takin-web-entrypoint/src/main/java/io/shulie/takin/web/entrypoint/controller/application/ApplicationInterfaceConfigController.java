package io.shulie.takin.web.entrypoint.controller.application;

import java.util.List;
import java.util.concurrent.Callable;

import javax.annotation.Resource;

import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.biz.pojo.request.application.InterfaceTypeChildCreateRequest;
import io.shulie.takin.web.biz.pojo.request.application.InterfaceTypeConfigCreateRequest;
import io.shulie.takin.web.biz.pojo.request.application.InterfaceTypeMainCreateRequest;
import io.shulie.takin.web.biz.pojo.request.application.RemoteCallConfigCreateRequest;
import io.shulie.takin.web.biz.service.linkmanage.ApplicationRemoteInterfaceConfigService;
import io.shulie.takin.web.common.constant.ApiUrls;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping(ApiUrls.TAKIN_API_URL + "application/interface/configs")
@RestController
@Api(tags = "远程调用", value = "远程调用配置接口")
public class ApplicationInterfaceConfigController {

    @Resource
    private ApplicationRemoteInterfaceConfigService interfaceConfigService;

    @ApiOperation("远程调用中间件主类型新增")
    @PostMapping("main_type/add")
    public ResponseResult<String> addInterfaceTypeMain(@RequestBody InterfaceTypeMainCreateRequest request) {
        return exec(() -> interfaceConfigService.addInterfaceTypeMain(request), "新增成功");
    }

    @ApiOperation("远程调用中间件主类型查询")
    @GetMapping("main_type/list")
    public ResponseResult<Object> queryInterfaceTypeMain() {
        return execQuery(() -> interfaceConfigService.queryInterfaceTypeMain());
    }

    @ApiOperation("远程调用中间件子类型新增")
    @PostMapping("child_type/add")
    public ResponseResult<String> addInterfaceTypeChild(@RequestBody InterfaceTypeChildCreateRequest request) {
        return exec(() -> interfaceConfigService.addInterfaceTypeChild(request), "新增成功");
    }

    @ApiOperation("远程调用中间件子类型查询")
    @GetMapping("child_type/list")
    public ResponseResult<Object> queryInterfaceTypeChild() {
        return execQuery(() -> interfaceConfigService.queryInterfaceTypeChild());
    }

    @ApiOperation("远程调用中间件子类型-配置类型新增")
    @PostMapping("config/add")
    public ResponseResult<String> addInterfaceTypeConfig(@RequestBody InterfaceTypeConfigCreateRequest request) {
        return exec(() -> interfaceConfigService.addInterfaceTypeConfig(request), "新增成功");
    }

    @ApiOperation("远程调用中间件子类型-配置类型查询")
    @GetMapping("config/list")
    public ResponseResult<Object> queryInterfaceTypeConfig() {
        return execQuery(() -> interfaceConfigService.queryInterfaceTypeConfig());
    }

    @ApiOperation("远程调用中间列子类型-配置类型删除")
    @DeleteMapping("config/delete/{id}")
    public ResponseResult<String> deleteInterfaceTypeConfig(@PathVariable("id") Long id) {
        return exec(() -> interfaceConfigService.deleteInterfaceTypeConfig(id), "删除成功");
    }

    @ApiOperation("远程调用配置类型新增")
    @PostMapping("remote_config/add")
    public ResponseResult<String> addRemoteCallConfig(@RequestBody RemoteCallConfigCreateRequest request) {
        return exec(() -> interfaceConfigService.addRemoteCallConfig(request), "新增成功");
    }

    @ApiOperation("远程调用配置类型查询")
    @GetMapping("remote_config/list")
    public ResponseResult<Object> queryRemoteCallConfig() {
        return execQuery(() -> interfaceConfigService.queryRemoteCallConfig());
    }

    @ApiOperation("trace查询调用类型查询")
    @GetMapping("middleware/list")
    public ResponseResult<Object> queryTraceMiddlewareType() {
        return execQuery(() -> interfaceConfigService.queryMiddlewareTypeList());
    }

    private ResponseResult<String> exec(Runnable r, String message) {
        try {
            r.run();
            return ResponseResult.success(message);
        } catch (Exception e) {
            return ResponseResult.fail(e.getMessage(), "");
        }
    }

    private ResponseResult<Object> execQuery(Callable<List<?>> call) {
        try {
            List<?> result = call.call();
            return ResponseResult.success(result, (long)result.size());
        } catch (Exception e) {
            return ResponseResult.fail(e.getMessage(), "");
        }
    }
}
