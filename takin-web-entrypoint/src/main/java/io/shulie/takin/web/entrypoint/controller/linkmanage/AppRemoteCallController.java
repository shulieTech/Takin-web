/*
 * Copyright (c) 2021. Shulie Technology, Co.Ltd
 * Email: shulie@shulie.io
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package io.shulie.takin.web.entrypoint.controller.linkmanage;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import io.shulie.takin.common.beans.annotation.ActionTypeEnum;
import io.shulie.takin.common.beans.annotation.AuthVerification;
import io.shulie.takin.common.beans.annotation.ModuleDef;
import io.shulie.takin.common.beans.component.SelectVO;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.constant.BizOpConstants;
import io.shulie.takin.web.biz.constant.BizOpConstants.Message;
import io.shulie.takin.web.biz.constant.BizOpConstants.OpTypes;
import io.shulie.takin.web.biz.constant.BizOpConstants.Vars;
import io.shulie.takin.web.biz.pojo.input.application.AppRemoteCallQueryInput;
import io.shulie.takin.web.biz.pojo.input.application.AppRemoteCallUpdateInput;
import io.shulie.takin.web.biz.pojo.output.application.AppRemoteCallOutput;
import io.shulie.takin.web.biz.pojo.request.application.AppRemoteCallConfigRequest;
import io.shulie.takin.web.biz.pojo.request.application.AppRemoteCallQueryRequest;
import io.shulie.takin.web.biz.pojo.request.application.AppRemoteCallUpdateRequest;
import io.shulie.takin.web.biz.pojo.response.application.AppRemoteCallConfigResponse;
import io.shulie.takin.web.biz.pojo.response.application.AppRemoteCallResponse;
import io.shulie.takin.web.biz.pojo.response.application.AppRemoteCallStringResponse;
import io.shulie.takin.web.biz.service.linkManage.AppRemoteCallService;
import io.shulie.takin.web.common.constant.ApiUrls;
import io.shulie.takin.web.common.context.OperationLogContextHolder;
import io.shulie.takin.web.common.enums.application.AppRemoteCallConfigEnum;
import io.shulie.takin.web.common.enums.application.AppRemoteCallTypeEnum;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.common.vo.application.AppRemoteCallListVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author 无涯
 * @date 2021/5/29 12:56 上午
 */
@Slf4j
@RestController
@RequestMapping(ApiUrls.TAKIN_API_URL)
@Api(tags = "远程调用", value = "远程调用管理接口")
public class AppRemoteCallController {

    @Autowired
    private AppRemoteCallService appRemoteCallService;

    @ApiOperation("远程调用添加接口")
    @PostMapping("/application/remote/call")
    @ModuleDef(
        moduleName = BizOpConstants.Modules.APPLICATION_MANAGE,
        subModuleName = BizOpConstants.SubModules.REMOTE_CALL,
        logMsgKey = Message.MESSAGE_REMOTE_CALL_CREATE
    )
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
        needAuth = ActionTypeEnum.UPDATE
    )
    public AppRemoteCallStringResponse insert(@Valid @RequestBody AppRemoteCallUpdateRequest request) {
        OperationLogContextHolder.addVars(BizOpConstants.Vars.INTERFACE, request.getInterfaceName());
        OperationLogContextHolder.addVars(BizOpConstants.Vars.INTERFACE_TYPE, AppRemoteCallTypeEnum.getEnum(request.getInterfaceType()).getDesc());
        OperationLogContextHolder.addVars(BizOpConstants.Vars.REMOTE_CALL_CONFIG, AppRemoteCallConfigEnum.getEnum(request.getType()).getConfigName());
        AppRemoteCallUpdateInput input = new AppRemoteCallUpdateInput();
        BeanUtils.copyProperties(request, input);
        appRemoteCallService.update(input);
        return new AppRemoteCallStringResponse("操作成功");
    }

    @ApiOperation("远程调用详情接口")
    @GetMapping("/application/remote/call")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
        needAuth = ActionTypeEnum.QUERY
    )
    public AppRemoteCallResponse getById(@RequestParam("id") Long id) {
        AppRemoteCallOutput output = appRemoteCallService.getById(id);
        AppRemoteCallResponse response = new AppRemoteCallResponse();
        BeanUtils.copyProperties(output, response);
        return response;
    }

    @ApiOperation("远程调用异常&&统计接口")
    @GetMapping("/application/remote/call/getException")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
        needAuth = ActionTypeEnum.QUERY
    )
    public AppRemoteCallStringResponse getException(@RequestParam("applicationId") Long applicationId) {
        return new AppRemoteCallStringResponse(appRemoteCallService.getException(applicationId));
    }

    @ApiOperation("远程调用列表接口")
    @GetMapping("/application/remote/call/list")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
        needAuth = ActionTypeEnum.QUERY
    )
    public PagingList<AppRemoteCallListVO> pageList(AppRemoteCallQueryRequest request) {
        AppRemoteCallQueryInput input = new AppRemoteCallQueryInput();
        BeanUtils.copyProperties(request, input);
        return appRemoteCallService.pagingList(input);
    }

    @ApiOperation("远程接口配置类型可用性筛选")
    @GetMapping("/application/remote/call/config/select")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
        needAuth = ActionTypeEnum.QUERY
    )
    public List<SelectVO> getConfigSelect(@RequestParam("interfaceType") Integer interfaceType,
        @RequestParam(value = "serverAppName", required = false) String serverAppName) {
        return appRemoteCallService.getConfigSelect(interfaceType, serverAppName);
    }

    @ApiOperation("远程调用批量配置接口")
    @PostMapping("/application/remote/call/config")
    @ModuleDef(
        moduleName = BizOpConstants.Modules.APPLICATION_MANAGE,
        subModuleName = BizOpConstants.SubModules.REMOTE_CALL,
        logMsgKey = Message.MESSAGE_BATCH_REMOTE_CALL_UPDATE
    )
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.APPLICATION_MANAGE,
        needAuth = ActionTypeEnum.UPDATE
    )
    public AppRemoteCallConfigResponse batchConfig(@RequestBody AppRemoteCallConfigRequest request){
        if(request.getType() != AppRemoteCallConfigEnum.CLOSE_CONFIGURATION.getType().intValue()
            && request.getType() != AppRemoteCallConfigEnum.OPEN_WHITELIST.getType().intValue()) {
            throw new TakinWebException(TakinWebExceptionEnum.APPLICATION_MANAGE_VALIDATE_ERROR, "参数type值不合法");
        }
        OperationLogContextHolder.operationType(OpTypes.UPDATE);
        OperationLogContextHolder.addVars(Vars.INTERFACE, request.getAppIds().stream().map(String::valueOf).collect(Collectors.joining(",")));
        OperationLogContextHolder.addVars(Vars.REMOTE_CALL_CONFIG, AppRemoteCallConfigEnum.OPEN_WHITELIST.getConfigName());
        appRemoteCallService.batchConfig(request);
        return new AppRemoteCallConfigResponse("操作成功");
    }
}
