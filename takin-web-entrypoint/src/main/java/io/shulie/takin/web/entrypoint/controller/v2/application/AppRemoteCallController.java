package io.shulie.takin.web.entrypoint.controller.v2.application;

import io.shulie.takin.common.beans.annotation.ActionTypeEnum;
import io.shulie.takin.common.beans.annotation.AuthVerification;
import io.shulie.takin.common.beans.component.SelectVO;
import io.shulie.takin.web.biz.constant.BizOpConstants;
import io.shulie.takin.web.biz.service.linkManage.AppRemoteCallService;
import io.shulie.takin.web.common.constant.APIUrls;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author 南风
 * @date 2021/8/25 11:25 上午
 */
@Slf4j
@RestController("v2.application.remote")
@RequestMapping(APIUrls.TAKIN_API_URL+"v2")
@Api(tags = "接口-v2:远程调用", value = "远程调用管理接口")
public class AppRemoteCallController {

    @Resource
    private AppRemoteCallService appRemoteCallService;

    @ApiOperation("远程接口配置类型可用性筛选")
    @GetMapping("/application/remote/call/config/select")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
        needAuth = ActionTypeEnum.QUERY
    )
    public List<SelectVO> getConfigSelect(@RequestParam("interfaceType") Integer interfaceType,
                                          @RequestParam("typeName") String typeName) {
        return appRemoteCallService.getConfigSelectV2(interfaceType,typeName);
    }

    @ApiOperation("远程接口类型可用性筛选")
    @GetMapping("/application/remote/call/interface/type/select")
    @AuthVerification(
            moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
            needAuth = ActionTypeEnum.QUERY
    )
    public List<SelectVO> getInterfaceTypeSelect() {
        return appRemoteCallService.getInterfaceTypeSelect();
    }

}
