package io.shulie.takin.web.entrypoint.controller.tenant;

import java.util.List;

import io.shulie.takin.web.biz.constant.BizOpConstants;
import io.shulie.takin.web.common.context.OperationLogContextHolder;
import io.shulie.takin.web.common.domain.WebResponse;
import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author by: hezhongqi
 * @Package io.shulie.takin.web.entrypoint.controller.tenant
 * @ClassName: TenantController
 * @Description: 租户接口
 * @Date: 2021/10/20 15:58
 */
public class TenantController {

    @GetMapping
    @ApiOperation("获取租户列表")
    public List<tenant> add() {
        sceneManageService.checkParam(sceneVO);
        sceneManageService.addScene(sceneVO);
        OperationLogContextHolder.operationType(BizOpConstants.OpTypes.CREATE);
        OperationLogContextHolder.addVars(BizOpConstants.Vars.SCENE_NAME, sceneVO.getPressureTestSceneName());
        return WebResponse.success();
    }

}
