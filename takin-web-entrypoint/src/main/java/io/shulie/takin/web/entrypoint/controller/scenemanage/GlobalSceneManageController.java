package io.shulie.takin.web.entrypoint.controller.scenemanage;

import io.shulie.takin.common.beans.annotation.ActionTypeEnum;
import io.shulie.takin.common.beans.annotation.AuthVerification;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.biz.constant.BizOpConstants;
import io.shulie.takin.web.biz.pojo.request.scenemanage.GlobalSceneManageRequest;
import io.shulie.takin.web.biz.pojo.response.scenemanage.GlobalSceneManageResponse;
import io.shulie.takin.web.biz.service.scenemanage.GlobalSceneManageService;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/api/globalscenemanage")
@Api(tags = "公共压测场景管理")
public class GlobalSceneManageController {

    @Resource
    private GlobalSceneManageService globalSceneManageService;

    @ApiOperation("将场景共享")
    @PostMapping("/sceneToGlobal")
    @AuthVerification(needAuth = ActionTypeEnum.UPDATE, moduleCode = BizOpConstants.ModuleCode.PRESSURE_TEST_SCENE)
    public ResponseResult<String> sceneToGlobal(@RequestBody GlobalSceneManageRequest request) throws TakinWebException {
        if (request == null || request.getSceneManageId() == null) {
            return ResponseResult.fail("必要参数不能为空", "场景id不能为空");
        }
        globalSceneManageService.sceneToGlobal(request.getSceneManageId());
        return ResponseResult.success("共享成功");
    }

    @ApiOperation("停止场景共享")
    @PostMapping("/cancelSceneToGlobal")
    @AuthVerification(needAuth = ActionTypeEnum.UPDATE, moduleCode = BizOpConstants.ModuleCode.PRESSURE_TEST_SCENE)
    public ResponseResult<String> cancelSceneToGlobal(@RequestBody GlobalSceneManageRequest request) throws TakinWebException {
        if (request == null || request.getSceneManageId() == null) {
            return ResponseResult.fail("必要参数不能为空", "场景id不能为空");
        }
        globalSceneManageService.cancelSceneToGlobal(request.getSceneManageId());
        return ResponseResult.success("取消共享成功");
    }

    @ApiOperation("将共享场景私有化")
    @PostMapping("/globalToScene")
    public ResponseResult<String> globalToScene(@RequestBody GlobalSceneManageRequest request) throws TakinWebException {
        if (request == null || request.getId() == null) {
            return ResponseResult.fail("必要参数不能为空", "共享id不能为空");
        }
        globalSceneManageService.globalToScene(request.getId());
        return ResponseResult.success("共享成功");
    }

    @ApiOperation("共享场景列表")
    @GetMapping("/list")
    @AuthVerification(needAuth = ActionTypeEnum.QUERY, moduleCode = BizOpConstants.ModuleCode.PRESSURE_TEST_SCENE)
    public PagingList<GlobalSceneManageResponse> list(@ApiParam(name = "current", value = "页码", required = true) Integer current,
                                                      @ApiParam(name = "pageSize", value = "页大小", required = true) Integer pageSize,
                                                      @ApiParam(name = "name", value = "共享场景名称") String name) throws TakinWebException {

        return globalSceneManageService.list(current, pageSize, name);
    }
}
