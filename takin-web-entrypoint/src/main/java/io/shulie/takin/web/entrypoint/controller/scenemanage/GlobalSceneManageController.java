package io.shulie.takin.web.entrypoint.controller.scenemanage;

import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.biz.pojo.request.scenemanage.GlobalSceneManageRequest;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/globalscenemanage")
@Api(tags = "公共压测场景管理")
public class GlobalSceneManageController {

    @ApiOperation("将场景共享")
    @PostMapping("sceneToGlobal")
    public ResponseResult<String> sceneToGlobal(GlobalSceneManageRequest request) throws TakinWebException {
        if (request == null || request.getSceneManageId() == null){
            return ResponseResult.fail("必要参数不能为空","场景id不能为空");
        }
        return ResponseResult.success("共享成功");
    }
}
