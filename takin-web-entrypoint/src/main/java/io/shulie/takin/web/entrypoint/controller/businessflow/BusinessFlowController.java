package io.shulie.takin.web.entrypoint.controller.businessflow;

import io.shulie.takin.cloud.common.utils.JsonUtil;
import io.shulie.takin.common.beans.annotation.ActionTypeEnum;
import io.shulie.takin.common.beans.annotation.AuthVerification;
import io.shulie.takin.common.beans.annotation.ModuleDef;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.biz.constant.BizOpConstants;
import io.shulie.takin.web.biz.constant.BizOpConstants.Vars;
import io.shulie.takin.web.biz.pojo.request.filemanage.FileManageUpdateRequest;
import io.shulie.takin.web.biz.pojo.request.linkmanage.*;
import io.shulie.takin.web.biz.pojo.request.scriptmanage.PluginConfigCreateRequest;
import io.shulie.takin.web.biz.pojo.response.linkmanage.BusinessFlowDetailResponse;
import io.shulie.takin.web.biz.pojo.response.linkmanage.BusinessFlowListResponse;
import io.shulie.takin.web.biz.pojo.response.linkmanage.BusinessFlowMatchResponse;
import io.shulie.takin.web.biz.pojo.response.linkmanage.BusinessFlowThreadResponse;
import io.shulie.takin.web.biz.service.scene.SceneService;
import io.shulie.takin.web.common.constant.ApiUrls;
import io.shulie.takin.web.common.context.OperationLogContextHolder;
import io.shulie.takin.web.common.enums.activity.BusinessTypeEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author 赵勇
 */
@Slf4j
@RestController
@RequestMapping(ApiUrls.TAKIN_API_URL + "businessFlow")
@Api(tags = "businessFlow", value = "业务流程接口")
public class BusinessFlowController {

    @Resource
    private SceneService sceneService;

    @PostMapping("/parseScriptAndSave")
    @ApiOperation("解析脚本并保存业务流程")
    @ModuleDef(
        moduleName = BizOpConstants.Modules.LINK_CARDING,
        subModuleName = BizOpConstants.SubModules.BUSINESS_PROCESS,
        logMsgKey = BizOpConstants.Message.MESSAGE_BUSINESS_PROCESS_CREATE2
    )
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.BUSINESS_PROCESS,
        needAuth = ActionTypeEnum.CREATE
    )
    public ResponseResult<BusinessFlowDetailResponse> parseScriptAndSave(@RequestBody @Valid BusinessFlowParseRequest businessFlowParseRequest) {
        BusinessFlowDetailResponse sceneDetailDto = sceneService.parseScriptAndSave(businessFlowParseRequest);

        // 操作日志
        OperationLogContextHolder.operationType(BizOpConstants.OpTypes.CREATE);
        if (null != sceneDetailDto) {
            OperationLogContextHolder.addVars(Vars.BUSINESS_FLOW_ID, String.valueOf(sceneDetailDto.getId()));
            OperationLogContextHolder.addVars(Vars.BUSINESS_FLOW_NAME, sceneDetailDto.getBusinessProcessName());
        }
        return ResponseResult.success(sceneDetailDto);
    }



    @PostMapping("/uploadDataFile")
    @ApiOperation("业务流程上传数据文件")
    @ModuleDef(
        moduleName = BizOpConstants.Modules.LINK_CARDING,
        subModuleName = BizOpConstants.SubModules.BUSINESS_PROCESS,
        logMsgKey = BizOpConstants.Message.MESSAGE_BUSINESS_PROCESS_UPDATEFile
    )
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.BUSINESS_PROCESS,
        needAuth = ActionTypeEnum.UPDATE
    )
    public ResponseResult<BusinessFlowDetailResponse> uploadDataFile(@RequestBody @Valid BusinessFlowDataFileRequest businessFlowDataFileRequest) {
        BusinessFlowDetailResponse sceneDetailDto = sceneService.uploadDataFile(businessFlowDataFileRequest);
        // 操作日志
        OperationLogContextHolder.operationType(BizOpConstants.OpTypes.UPDATE);
        if (null != businessFlowDataFileRequest) {
            OperationLogContextHolder.addVars(BizOpConstants.Vars.BUSINESS_FLOW_ID, String.valueOf(businessFlowDataFileRequest.getId()));
            List<FileManageUpdateRequest> files = businessFlowDataFileRequest.getFileManageUpdateRequests();
            if (CollectionUtils.isNotEmpty(files)) {
                String newFiles = files.stream().filter(Objects::nonNull)
                    .filter(f -> null == f.getId())
                    .map(FileManageUpdateRequest::getFileName)
                    .collect(Collectors.joining(","));
                OperationLogContextHolder.addVars("newFiles", newFiles);
                String updateFiles = files.stream().filter(Objects::nonNull)
                    .filter(f -> null != f.getId())
                    .filter(f -> f.getIsDeleted() != 1)
                    .map(FileManageUpdateRequest::getFileName)
                    .collect(Collectors.joining(","));
                String deleteFiles = files.stream().filter(Objects::nonNull)
                    .filter(f -> f.getIsDeleted() == 1)
                    .map(FileManageUpdateRequest::getFileName)
                    .collect(Collectors.joining(","));
                OperationLogContextHolder.addVars("newFiles", newFiles);
                OperationLogContextHolder.addVars("updateFiles", updateFiles);
                OperationLogContextHolder.addVars("deleteFiles", deleteFiles);
            }
            List<PluginConfigCreateRequest> plugins = businessFlowDataFileRequest.getPluginList();
            // 补充操作日志的参数
            // * 但是这个业务流程应该没有插件参数
            if (CollectionUtils.isNotEmpty(plugins)) {
                OperationLogContextHolder.addVars("plugins", JsonUtil.toJson(plugins));
            } else {
                OperationLogContextHolder.addVars("plugins", "");
            }
        }
        return ResponseResult.success(sceneDetailDto);
    }

    @PostMapping("/autoMatchActivity")
    @ApiOperation("自动匹配业务活动")
    @ModuleDef(
        moduleName = BizOpConstants.Modules.LINK_CARDING,
        subModuleName = BizOpConstants.SubModules.BUSINESS_PROCESS,
        logMsgKey = BizOpConstants.Message.MESSAGE_BUSINESS_FLOW
    )
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.BUSINESS_PROCESS,
        needAuth = ActionTypeEnum.UPDATE
    )
    public ResponseResult<BusinessFlowMatchResponse> autoMatchActivity(@RequestBody @Valid BusinessFlowAutoMatchRequest businessFlowAutoMatchRequest) {
        BusinessFlowMatchResponse sceneDetailDto = sceneService.autoMatchActivity(businessFlowAutoMatchRequest.getId());
        OperationLogContextHolder.operationType(BizOpConstants.OpTypes.UPDATE);
        OperationLogContextHolder.addVars("data", JsonUtil.toJson(businessFlowAutoMatchRequest));
        return ResponseResult.success(sceneDetailDto);
    }

    @PostMapping("/matchActivity")
    @ApiOperation("匹配业务活动")
    @ModuleDef(
        moduleName = BizOpConstants.Modules.LINK_CARDING,
        subModuleName = BizOpConstants.SubModules.BUSINESS_PROCESS,
        logMsgKey = BizOpConstants.Message.MESSAGE_BUSINESS_FLOW
    )
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.BUSINESS_PROCESS,
        needAuth = ActionTypeEnum.UPDATE
    )
    public ResponseResult<Boolean> matchActivity(@RequestBody @Valid SceneLinkRelateRequest sceneLinkRelateRequest) {
        // 针对虚拟业务活动
        if(BusinessTypeEnum.VIRTUAL_BUSINESS.getType().equals(sceneLinkRelateRequest.getBusinessType())) {
            sceneLinkRelateRequest.setEntracePath(sceneLinkRelateRequest.getPath());
            sceneLinkRelateRequest.setEntrance(sceneLinkRelateRequest.getPath());
        }
        if (!checkPath(sceneLinkRelateRequest.getPath(),sceneLinkRelateRequest.getEntracePath())){
            return ResponseResult.fail("入口不匹配","");
        }
        sceneService.matchActivity(sceneLinkRelateRequest);
        // 操作日志
        if (null != sceneLinkRelateRequest) {
            OperationLogContextHolder.operationType(null == sceneLinkRelateRequest.getId() ? BizOpConstants.OpTypes.CREATE : BizOpConstants.OpTypes.UPDATE);
            OperationLogContextHolder.addVars("data", JsonUtil.toJson(sceneLinkRelateRequest));
        }
        return ResponseResult.success(Boolean.TRUE);
    }

    private boolean checkPath(String path, String entracePath) {
        if (StringUtils.isNotBlank(path) && StringUtils.isNotBlank(entracePath)) {
            String[] s1 = path.split("\\|");
            String[] s2 = entracePath.split("\\|");
            if (s1.length == 2 && s2.length == 2) {
                return StringUtils.equals(s1[0], s2[0]) && StringUtils.equals(s1[1], s2[1]);
            }
        }
        return true;
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
        BusinessFlowThreadResponse response = sceneService.getThreadGroupDetail(id, xpathMd5);
        return ResponseResult.success(response);
    }

    @GetMapping("/scene/list")
    @ApiOperation("业务流程列表查询")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.BUSINESS_PROCESS,
        needAuth = ActionTypeEnum.QUERY
    )
    public PagingList<BusinessFlowListResponse> getBusinessFlowList(@ApiParam("业务流程名称") String businessFlowName, @ApiParam("部门id") Long deptId, Integer current, Integer pageSize) {
        BusinessFlowPageQueryRequest queryRequest = new BusinessFlowPageQueryRequest();
        queryRequest.setCurrentPage(current);
        queryRequest.setPageSize(pageSize);
        queryRequest.setBusinessFlowName(businessFlowName);
        queryRequest.setDeptId(deptId);
        return sceneService.getBusinessFlowList(queryRequest);
    }

}
