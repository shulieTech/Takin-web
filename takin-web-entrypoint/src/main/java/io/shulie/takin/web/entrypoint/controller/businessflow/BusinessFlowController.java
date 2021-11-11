package io.shulie.takin.web.entrypoint.controller.businessflow;

import io.shulie.takin.common.beans.annotation.ActionTypeEnum;
import io.shulie.takin.common.beans.annotation.AuthVerification;
import io.shulie.takin.common.beans.annotation.ModuleDef;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.biz.constant.BizOpConstants;
import io.shulie.takin.web.biz.pojo.request.linkmanage.*;
import io.shulie.takin.web.biz.pojo.response.linkmanage.BusinessFlowDetailResponse;
import io.shulie.takin.web.biz.pojo.response.linkmanage.BusinessFlowListResponse;
import io.shulie.takin.web.biz.pojo.response.linkmanage.BusinessFlowMatchResponse;
import io.shulie.takin.web.biz.pojo.response.linkmanage.BusinessFlowThreadResponse;
import io.shulie.takin.web.biz.service.scene.SceneService;
import io.shulie.takin.web.common.constant.APIUrls;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@Slf4j
@RestController
@RequestMapping(APIUrls.TAKIN_API_URL + "businessFlow")
@Api(tags = "businessFlow", value = "业务流程接口")
public class BusinessFlowController {

    @Autowired
    private SceneService sceneService;


    @PostMapping("/parseScriptAndSave")
    @ApiOperation("解析脚本并保存业务流程")
    @ModuleDef(
            moduleName = BizOpConstants.Modules.LINK_CARDING,
            subModuleName = BizOpConstants.SubModules.BUSINESS_PROCESS,
            logMsgKey = BizOpConstants.Message.MESSAGE_BUSINESS_PROCESS_CREATE
    )
    @AuthVerification(
            moduleCode = BizOpConstants.ModuleCode.BUSINESS_PROCESS,
            needAuth = ActionTypeEnum.CREATE
    )
    public ResponseResult<BusinessFlowDetailResponse> parseScriptAndSave(@RequestBody @Valid BusinessFlowParseRequest businessFlowParseRequest) {
        BusinessFlowDetailResponse sceneDetailDto = sceneService.parseScriptAndSave(businessFlowParseRequest);
        return ResponseResult.success(sceneDetailDto);
    }

    @PostMapping("/uploadDataFile")
    @ApiOperation("业务流程上传数据文件")
    @ModuleDef(
            moduleName = BizOpConstants.Modules.LINK_CARDING,
            subModuleName = BizOpConstants.SubModules.BUSINESS_PROCESS,
            logMsgKey = BizOpConstants.Message.MESSAGE_BUSINESS_PROCESS_UPDATE
    )
    @AuthVerification(
            moduleCode = BizOpConstants.ModuleCode.BUSINESS_PROCESS,
            needAuth = ActionTypeEnum.UPDATE
    )
    public ResponseResult<BusinessFlowDetailResponse> uploadDataFile(@RequestBody @Valid BusinessFlowDataFileRequest businessFlowDataFileRequest) {
        BusinessFlowDetailResponse sceneDetailDto = sceneService.uploadDataFile(businessFlowDataFileRequest);
        return ResponseResult.success(sceneDetailDto);
    }

    @PostMapping("/autoMatchActivity")
    @ApiOperation("自动匹配业务活动")
    @ModuleDef(
            moduleName = BizOpConstants.Modules.LINK_CARDING,
            subModuleName = BizOpConstants.SubModules.BUSINESS_PROCESS,
            logMsgKey = BizOpConstants.Message.MESSAGE_BUSINESS_PROCESS_UPDATE
    )
    @AuthVerification(
            moduleCode = BizOpConstants.ModuleCode.BUSINESS_PROCESS,
            needAuth = ActionTypeEnum.UPDATE
    )
    public ResponseResult<BusinessFlowMatchResponse> autoMatchActivity(@RequestBody @Valid BusinessFlowAutoMatchRequest businessFlowAutoMatchRequest) {
        BusinessFlowMatchResponse sceneDetailDto = sceneService.autoMatchActivity(businessFlowAutoMatchRequest.getId());
        return ResponseResult.success(sceneDetailDto);
    }

    @PostMapping("/matchActivity")
    @ApiOperation("匹配业务活动")
    @ModuleDef(
            moduleName = BizOpConstants.Modules.LINK_CARDING,
            subModuleName = BizOpConstants.SubModules.BUSINESS_PROCESS,
            logMsgKey = BizOpConstants.Message.MESSAGE_BUSINESS_PROCESS_UPDATE
    )
    @AuthVerification(
            moduleCode = BizOpConstants.ModuleCode.BUSINESS_PROCESS,
            needAuth = ActionTypeEnum.UPDATE
    )
    public ResponseResult<Boolean> matchActivity(@RequestBody @Valid SceneLinkRelateRequest sceneLinkRelateRequest) {
        sceneService.matchActivity(sceneLinkRelateRequest);
        return ResponseResult.success(Boolean.TRUE);
    }

    @GetMapping("/scene/detail")
    @ApiOperation("业务流程详情获取")
    @AuthVerification(
            moduleCode = BizOpConstants.ModuleCode.BUSINESS_PROCESS,
            needAuth = ActionTypeEnum.QUERY
    )
    public ResponseResult<BusinessFlowDetailResponse> getSceneDetail(@NotNull Long id) {
        BusinessFlowDetailResponse dto = sceneService.getBusinessFlowDetail(id);
        return ResponseResult.success(dto);
    }

    @PutMapping("/scene")
    @ApiOperation("业务流程更新")
    @AuthVerification(
            moduleCode = BizOpConstants.ModuleCode.BUSINESS_PROCESS,
            needAuth = ActionTypeEnum.QUERY
    )
    public ResponseResult<Boolean> getSceneDetail(@Valid @RequestBody BusinessFlowUpdateRequest businessFlowUpdateRequest) {
        sceneService.updateBusinessFlow(businessFlowUpdateRequest);
        return ResponseResult.success(Boolean.TRUE);
    }


    @GetMapping("/scene/threadGroupDetail")
    @ApiOperation("业务流程详情获取线程组内容详情")
    @AuthVerification(
            moduleCode = BizOpConstants.ModuleCode.BUSINESS_PROCESS,
            needAuth = ActionTypeEnum.QUERY
    )
    public ResponseResult<BusinessFlowThreadResponse> getThreadGroupDetail(@NotNull Long id, @NotNull String xpathMd5) {
        BusinessFlowThreadResponse response = sceneService.getThreadGroupDetail(id,xpathMd5);
        return ResponseResult.success(response);
    }

    @GetMapping("/scene/list")
    @ApiOperation("业务流程列表查询")
    @AuthVerification(
            moduleCode = BizOpConstants.ModuleCode.BUSINESS_PROCESS,
            needAuth = ActionTypeEnum.QUERY
    )
    public PagingList<BusinessFlowListResponse> getBusinessFlowList(@ApiParam("业务流程名称") String businessFlowName, Integer current, Integer pageSize) {
        BusinessFlowPageQueryRequest queryRequest = new BusinessFlowPageQueryRequest();
        queryRequest.setCurrentPage(current);
        queryRequest.setPageSize(pageSize);
        queryRequest.setBusinessFlowName(businessFlowName);
        return sceneService.getBusinessFlowList(queryRequest);
    }

}
