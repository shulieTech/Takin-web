package io.shulie.takin.web.entrypoint.controller.dashboard;

import javax.annotation.Resource;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.shulie.takin.web.biz.constant.BizOpConstants;
import com.pamirs.takin.entity.domain.vo.ApplicationVo;
import io.shulie.takin.common.beans.annotation.ModuleDef;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import io.shulie.takin.common.beans.annotation.ActionTypeEnum;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.biz.constant.DashboardExceptionCode;
import io.shulie.takin.common.beans.annotation.AuthVerification;
import io.shulie.takin.web.common.context.OperationLogContextHolder;
import io.shulie.takin.web.biz.service.dashboard.ApplicationService;
import io.shulie.takin.web.biz.pojo.response.dashboard.AppPressureSwitchSetResponse;
import io.shulie.takin.web.biz.pojo.response.dashboard.ApplicationSwitchStatusResponse;

/**
 * 应用管理模块
 *
 * @author 张天赐
 */
@RequestMapping("api/application")
@RestController("dashboard-ApplicationController")
@Api(tags = "接口: 应用管理中心", value = "应用管理中心")
public class ApplicationController {
    @Resource
    private ApplicationService applicationService;

    @ApiOperation("获取应用压测开关状态接口")
    @GetMapping("center/app/switch")
    public ApplicationSwitchStatusResponse getAppSwitchInfo() {
        return applicationService.getUserAppSwitchInfo();
    }

    @ApiOperation("压测全局开关接口")
    @PutMapping("center/app/switch")
    @ModuleDef(
        moduleName = BizOpConstants.Modules.CONFIG_CENTER,
        subModuleName = BizOpConstants.SubModules.PRESSURE_TEST_SWITCH,
        logMsgKey = BizOpConstants.Message.MESSAGE_PRESSURE_TEST_SWITCH_ACTION
    )
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.PRESSURE_TEST_SWITCH,
        needAuth = ActionTypeEnum.ENABLE_DISABLE
    )
    public AppPressureSwitchSetResponse getAppSwitch(
        @RequestBody ApplicationVo vo) {
        if (vo == null || vo.getPressureEnable() == null) {
            throw new TakinWebException(DashboardExceptionCode.DEFAULT, "pressureEnable 不能为空");
        }
        OperationLogContextHolder.operationType(
            vo.getPressureEnable() ? BizOpConstants.OpTypes.OPEN : BizOpConstants.OpTypes.CLOSE);
        OperationLogContextHolder.addVars(BizOpConstants.Vars.ACTION,
            vo.getPressureEnable() ? BizOpConstants.OpTypes.OPEN : BizOpConstants.OpTypes.CLOSE);
        return applicationService.setUserAppPressureSwitch(vo.getPressureEnable());
    }
}
