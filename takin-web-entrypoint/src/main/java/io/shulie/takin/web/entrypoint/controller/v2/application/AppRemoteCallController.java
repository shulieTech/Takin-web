package io.shulie.takin.web.entrypoint.controller.v2.application;

import java.util.List;

import javax.annotation.Resource;
import javax.validation.Valid;

import io.shulie.takin.common.beans.annotation.ActionTypeEnum;
import io.shulie.takin.common.beans.annotation.AuthVerification;
import io.shulie.takin.common.beans.annotation.ModuleDef;
import io.shulie.takin.common.beans.component.SelectVO;
import io.shulie.takin.web.biz.constant.BizOpConstants;
import io.shulie.takin.web.biz.constant.BizOpConstants.Message;
import io.shulie.takin.web.biz.constant.BizOpConstants.OpTypes;
import io.shulie.takin.web.biz.constant.BizOpConstants.Vars;
import io.shulie.takin.web.biz.pojo.output.application.AppRemoteCallOutputV2;
import io.shulie.takin.web.biz.pojo.request.application.AppRemoteCallBatchUpdateV2Request;
import io.shulie.takin.web.biz.pojo.request.application.AppRemoteCallCreateV2Request;
import io.shulie.takin.web.biz.pojo.request.application.AppRemoteCallDelV2Request;
import io.shulie.takin.web.biz.pojo.request.application.AppRemoteCallUpdateV2Request;
import io.shulie.takin.web.biz.pojo.response.application.AppRemoteCallStringResponse;
import io.shulie.takin.web.biz.pojo.response.application.AppRemoteCallV2Response;
import io.shulie.takin.web.biz.service.linkmanage.AppRemoteCallService;
import io.shulie.takin.web.common.common.Response;
import io.shulie.takin.web.common.constant.ApiUrls;
import io.shulie.takin.web.common.context.OperationLogContextHolder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author ??????
 * @date 2021/8/25 11:25 ??????
 */
@Slf4j
@RestController("v2.application.remote")
@RequestMapping(ApiUrls.TAKIN_API_URL+"v2")
@Api(tags = "??????-v2:????????????", value = "????????????????????????")
public class AppRemoteCallController {

    @Resource
    private AppRemoteCallService appRemoteCallService;

    @ApiOperation("???????????????????????????????????????")
    @GetMapping("/application/remote/call/config/select")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
        needAuth = ActionTypeEnum.QUERY
    )
    public List<SelectVO> getConfigSelect(@ApiParam(name = "interfaceType",value = "????????????",required = true)
                                              @RequestParam("interfaceType") String interfaceType) {
        return appRemoteCallService.getConfigSelectV2(interfaceType);
    }

    @ApiOperation("?????????????????????????????????")
    @GetMapping("/application/remote/call/interface/type/select")
    @AuthVerification(
            moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
            needAuth = ActionTypeEnum.QUERY
    )
    public List<SelectVO> getInterfaceTypeSelect() {
        return appRemoteCallService.getInterfaceTypeSelect();
    }

    @ApiOperation("????????????????????????")
    @PostMapping("/application/remote/call/add")
    @ModuleDef(
            moduleName = BizOpConstants.Modules.APPLICATION_MANAGE,
            subModuleName = BizOpConstants.SubModules.REMOTE_CALL,
            logMsgKey = BizOpConstants.Message.MESSAGE_MANUAL_REMOTE_CALL_CREATE
    )
    @AuthVerification(
            moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
            needAuth = ActionTypeEnum.CREATE
    )
    public AppRemoteCallStringResponse insert(@ApiParam(required=true) @Validated @RequestBody AppRemoteCallCreateV2Request request) {
        OperationLogContextHolder.operationType(OpTypes.CREATE);
        OperationLogContextHolder.addVars(Vars.APPLICATION_ID, String.valueOf(request.getApplicationId()));
        OperationLogContextHolder.addVars(Vars.INTERFACE, request.getInterfaceName());
        OperationLogContextHolder.addVars(Vars.INTERFACE_TYPE, request.getInterfaceType());
        OperationLogContextHolder.addVars(Vars.REMOTE_CALL_CONFIG, String.valueOf(request.getType()));
        appRemoteCallService.create(request);
        return new AppRemoteCallStringResponse("????????????");
    }

    @ApiOperation("????????????????????????")
    @PostMapping("/application/remote/call/update")
    @ModuleDef(
            moduleName = BizOpConstants.Modules.APPLICATION_MANAGE,
            subModuleName = BizOpConstants.SubModules.REMOTE_CALL,
            logMsgKey = Message.MESSAGE_REMOTE_CALL_UPDATE
    )
    @AuthVerification(
            moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
            needAuth = ActionTypeEnum.UPDATE
    )
    public AppRemoteCallStringResponse update(@ApiParam(required=true) @Valid @RequestBody AppRemoteCallUpdateV2Request request) {
        OperationLogContextHolder.operationType(OpTypes.UPDATE);
        OperationLogContextHolder.addVars(BizOpConstants.Vars.INTERFACE, request.getInterfaceName());
        OperationLogContextHolder.addVars(BizOpConstants.Vars.REMOTE_CALL_CONFIG, String.valueOf(request.getType()));
        appRemoteCallService.updateV2(request);
        return new AppRemoteCallStringResponse("????????????");
    }

    @ApiOperation("????????????????????????")
    @GetMapping("/application/remote/call/detail")
    @AuthVerification(
            moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
            needAuth = ActionTypeEnum.QUERY
    )
    public AppRemoteCallV2Response getById(@ApiParam(name = "id",value = "??????id",required = true) @RequestParam("id") Long id) {
        AppRemoteCallOutputV2 output = appRemoteCallService.getByIdV2(id);
        AppRemoteCallV2Response response = new AppRemoteCallV2Response();
        BeanUtils.copyProperties(output, response);
        return response;
    }


    @ApiOperation("????????????????????????")
    @DeleteMapping("/application/remote/call/delete")
    @ModuleDef(
        moduleName = BizOpConstants.Modules.APPLICATION_MANAGE,
        subModuleName = BizOpConstants.SubModules.REMOTE_CALL,
        logMsgKey = Message.MESSAGE_REMOTE_CALL_DELETE
    )
    @AuthVerification(
            moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
            needAuth = ActionTypeEnum.DELETE
    )
    public Response delete(@ApiParam(required=true) @Valid @RequestBody AppRemoteCallDelV2Request request) {
        OperationLogContextHolder.operationType(OpTypes.DELETE);
        appRemoteCallService.deleteById(request.getId());
        return Response.success();
    }

    @ApiOperation("??????????????????????????????")
    @PostMapping("/application/remote/call/update/batch")
    @ModuleDef(
            moduleName = BizOpConstants.Modules.APPLICATION_MANAGE,
            subModuleName = BizOpConstants.SubModules.REMOTE_CALL,
            logMsgKey = Message.MESSAGE_REMOTE_CALL_UPDATE
    )
    @AuthVerification(
            moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
            needAuth = ActionTypeEnum.UPDATE
    )
    public AppRemoteCallStringResponse batchUpdate(@ApiParam(required=true) @Valid @RequestBody AppRemoteCallBatchUpdateV2Request request) {
        appRemoteCallService.batchUpdateV2(request);
        return new AppRemoteCallStringResponse("????????????");
    }
}
