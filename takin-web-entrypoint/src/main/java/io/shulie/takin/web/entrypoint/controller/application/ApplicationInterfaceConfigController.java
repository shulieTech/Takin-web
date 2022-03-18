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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("api/application/interface/configs")
@RestController
public class ApplicationInterfaceConfigController {

    @Resource
    private ApplicationRemoteInterfaceConfigService interfaceConfigService;

    @PostMapping("main_type/add")
    public ResponseResult<String> addInterfaceTypeMain(@RequestBody InterfaceTypeMainCreateRequest request) {
        return exec(() -> interfaceConfigService.addInterfaceTypeMain(request), "新增成功");
    }

    @GetMapping("main_type/list")
    public ResponseResult<Object> queryInterfaceTypeMain() {
        return execQuery(() -> interfaceConfigService.queryInterfaceTypeMain());
    }

    @PostMapping("child_type/add")
    public ResponseResult<String> addInterfaceTypeChild(@RequestBody InterfaceTypeChildCreateRequest request) {
        return exec(() -> interfaceConfigService.addInterfaceTypeChild(request), "新增成功");
    }

    @GetMapping("child_type/list")
    public ResponseResult<Object> queryInterfaceTypeChild() {
        return execQuery(() -> interfaceConfigService.queryInterfaceTypeChild());
    }

    @PostMapping("config/add")
    public ResponseResult<String> addInterfaceTypeConfig(@RequestBody InterfaceTypeConfigCreateRequest request) {
        return exec(() -> interfaceConfigService.addInterfaceTypeConfig(request), "新增成功");
    }

    @GetMapping("config/list")
    public ResponseResult<Object> queryInterfaceTypeConfig() {
        return execQuery(() -> interfaceConfigService.queryInterfaceTypeConfig());
    }

    @DeleteMapping("config/delete/{id}")
    public ResponseResult<String> deleteInterfaceTypeConfig(@PathVariable("id") Long id) {
        return exec(() -> interfaceConfigService.deleteInterfaceTypeConfig(id), "删除成功");
    }

    @PostMapping("remote_config/add")
    public ResponseResult<String> addRemoteCallConfig(@RequestBody RemoteCallConfigCreateRequest request) {
        return exec(() -> interfaceConfigService.addRemoteCallConfig(request), "新增成功");
    }

    @GetMapping("remote_config/list")
    public ResponseResult<Object> queryRemoteCallConfig() {
        return execQuery(() -> interfaceConfigService.queryRemoteCallConfig());
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
