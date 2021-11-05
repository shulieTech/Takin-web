package io.shulie.takin.web.entrypoint.controller.businessflow;

import com.pamirs.takin.entity.domain.dto.linkmanage.ScriptJmxNode;
import io.shulie.takin.common.beans.annotation.ActionTypeEnum;
import io.shulie.takin.common.beans.annotation.AuthVerification;
import io.shulie.takin.common.beans.annotation.ModuleDef;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.biz.constant.BizOpConstants;
import io.shulie.takin.web.biz.pojo.request.linkmanage.BusinessFlowDataFileRequest;
import io.shulie.takin.web.biz.pojo.request.linkmanage.BusinessFlowPageQueryRequest;
import io.shulie.takin.web.biz.pojo.request.linkmanage.BusinessFlowParseRequest;
import io.shulie.takin.web.biz.pojo.request.linkmanage.SceneLinkRelateRequest;
import io.shulie.takin.web.biz.pojo.response.linkmanage.BusinessFlowDetailResponse;
import io.shulie.takin.web.biz.pojo.response.linkmanage.BusinessFlowListResponse;
import io.shulie.takin.web.biz.pojo.response.linkmanage.BusinessFlowMatchResponse;
import io.shulie.takin.web.biz.service.scene.SceneService;
import io.shulie.takin.web.common.constant.APIUrls;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

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
    public ResponseResult<BusinessFlowDetailResponse> parseScriptAndSave(@RequestBody BusinessFlowParseRequest businessFlowParseRequest) {
        try {
            BusinessFlowDetailResponse sceneDetailDto = sceneService.parseScriptAndSave(businessFlowParseRequest);
            return ResponseResult.success(sceneDetailDto);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new TakinWebException(TakinWebExceptionEnum.LINK_ADD_ERROR, e.getMessage());
        }
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
    public ResponseResult<BusinessFlowDetailResponse> uploadDataFile(@RequestBody BusinessFlowDataFileRequest businessFlowDataFileRequest) {
        try {
            BusinessFlowDetailResponse sceneDetailDto = sceneService.uploadDataFile(businessFlowDataFileRequest);
            return ResponseResult.success(sceneDetailDto);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new TakinWebException(TakinWebExceptionEnum.LINK_UPDATE_ERROR, e.getMessage());
        }
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
    public ResponseResult<BusinessFlowMatchResponse> autoMatchActivity(@NotNull Long id) {
        try {
            BusinessFlowMatchResponse sceneDetailDto = sceneService.autoMatchActivity(id);
            return ResponseResult.success(sceneDetailDto);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new TakinWebException(TakinWebExceptionEnum.LINK_UPDATE_ERROR, e.getMessage());
        }
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
    public ResponseResult<Boolean> matchActivity(@RequestBody SceneLinkRelateRequest sceneLinkRelateRequest) {
        try {
            sceneService.matchActivity(sceneLinkRelateRequest);
            return ResponseResult.success(Boolean.TRUE);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new TakinWebException(TakinWebExceptionEnum.LINK_UPDATE_ERROR, e.getMessage());
        }
    }

    @GetMapping("/scene/detail")
    @ApiOperation("业务流程详情获取")
    @AuthVerification(
            moduleCode = BizOpConstants.ModuleCode.BUSINESS_PROCESS,
            needAuth = ActionTypeEnum.QUERY
    )
    public ResponseResult<BusinessFlowDetailResponse> getSceneDetail(@NotNull Long id) {
        try {
            BusinessFlowDetailResponse dto = sceneService.getBusinessFlowDetail(id);
            return ResponseResult.success(dto);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            throw new TakinWebException(TakinWebExceptionEnum.LINK_QUERY_ERROR, e.getMessage());
        }
    }


    @GetMapping("/scene/threadGroupDetail")
    @ApiOperation("业务流程详情获取线程组内容详情")
    @AuthVerification(
            moduleCode = BizOpConstants.ModuleCode.BUSINESS_PROCESS,
            needAuth = ActionTypeEnum.QUERY
    )
    public ResponseResult<List<ScriptJmxNode>> getThreadGroupDetail(@NotNull Long id, @NotNull String xpathMd5) {
        List<ScriptJmxNode> list = sceneService.getThreadGroupDetail(id,xpathMd5);
        return ResponseResult.success(list);
    }

    @GetMapping("/scene/list")
    @ApiOperation("业务流程列表查询")
    @AuthVerification(
            moduleCode = BizOpConstants.ModuleCode.BUSINESS_PROCESS,
            needAuth = ActionTypeEnum.QUERY
    )
    public ResponseResult<PagingList<BusinessFlowListResponse>> getBusinessFlowList(@ApiParam("业务流程名称") String businessFlowName, Integer current, Integer pageSize) {
        BusinessFlowPageQueryRequest queryRequest = new BusinessFlowPageQueryRequest();
        queryRequest.setCurrentPage(current);
        queryRequest.setPageSize(pageSize);
        queryRequest.setBusinessFlowName(businessFlowName);
        PagingList<BusinessFlowListResponse> dto = sceneService.getBusinessFlowList(queryRequest);
        return ResponseResult.success(dto);
    }

}
