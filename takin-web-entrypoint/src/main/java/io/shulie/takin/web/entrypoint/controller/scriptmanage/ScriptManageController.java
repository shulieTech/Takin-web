package io.shulie.takin.web.entrypoint.controller.scriptmanage;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import javax.validation.Valid;

import com.google.common.collect.Lists;
import io.shulie.takin.common.beans.annotation.ActionTypeEnum;
import io.shulie.takin.common.beans.annotation.AuthVerification;
import io.shulie.takin.common.beans.annotation.ModuleDef;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.constant.BizOpConstants;
import io.shulie.takin.web.biz.pojo.request.filemanage.FileManageCreateRequest;
import io.shulie.takin.web.biz.pojo.request.filemanage.FileManageUpdateRequest;
import io.shulie.takin.web.biz.pojo.request.scriptmanage.ScriptManageDeployCreateRequest;
import io.shulie.takin.web.biz.pojo.request.scriptmanage.ScriptManageDeployDeleteRequest;
import io.shulie.takin.web.biz.pojo.request.scriptmanage.ScriptManageDeployPageQueryRequest;
import io.shulie.takin.web.biz.pojo.request.scriptmanage.ScriptManageDeployRollBackRequest;
import io.shulie.takin.web.biz.pojo.request.scriptmanage.ScriptManageDeployUpdateRequest;
import io.shulie.takin.web.biz.pojo.request.scriptmanage.ScriptTagCreateRefRequest;
import io.shulie.takin.web.biz.pojo.request.scriptmanage.SupportJmeterPluginNameRequest;
import io.shulie.takin.web.biz.pojo.request.scriptmanage.SupportJmeterPluginVersionRequest;
import io.shulie.takin.web.biz.pojo.request.scriptmanage.WebPartRequest;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.ScriptManageActivityResponse;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.ScriptManageDeployDetailResponse;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.ScriptManageDeployResponse;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.ScriptManageSceneManageResponse;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.ScriptManageStringResponse;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.ScriptManageXmlContentResponse;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.SupportJmeterPluginNameResponse;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.SupportJmeterPluginVersionResponse;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.WebPartResponse;
import io.shulie.takin.web.biz.pojo.response.tagmanage.TagManageResponse;
import io.shulie.takin.web.biz.service.scriptmanage.ScriptManageService;
import io.shulie.takin.web.common.context.OperationLogContextHolder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhaoyong
 */
@RestController
@RequestMapping("/api/scriptManage")
@Api(tags = "??????????????????")
public class ScriptManageController {

    @Autowired
    private ScriptManageService scriptManageService;

    @GetMapping("/getZipFileUrl")
    @ApiOperation(value = "????????????????????????????????????")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.SCRIPT_MANAGE,
        needAuth = ActionTypeEnum.DOWNLOAD
    )
    public ScriptManageStringResponse getZipFileUrl(@RequestParam("scriptId") @Valid Long scriptDeployId) {
        return new ScriptManageStringResponse(scriptManageService.getZipFileNameByScriptDeployId(scriptDeployId));
    }

    @PutMapping
    @ApiOperation(value = "??????????????????")
    @ModuleDef(moduleName = BizOpConstants.Modules.SCRIPT_MANAGE,
        subModuleName = BizOpConstants.SubModules.SCRIPT_MANAGE,
        logMsgKey = BizOpConstants.Message.SCRIPT_MANAGE_UPDATE)
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.SCRIPT_MANAGE,
        needAuth = ActionTypeEnum.UPDATE
    )
    public ScriptManageStringResponse updateScriptManage(
        @RequestBody @Valid ScriptManageDeployUpdateRequest scriptManageDeployUpdateRequest) {
        List<FileManageUpdateRequest> fileManageUpdateRequests = scriptManageDeployUpdateRequest
            .getFileManageUpdateRequests();
        List<FileManageUpdateRequest> attachmentsManageUpdateRequests = scriptManageDeployUpdateRequest
            .getAttachmentManageUpdateRequests();
        //??????????????????jmx??????
        List<FileManageUpdateRequest> attachmentsFilterManageUpdateRequests = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(attachmentsManageUpdateRequests)) {
            for (FileManageUpdateRequest fileRequest : attachmentsManageUpdateRequests) {
                if (StringUtils.isNotBlank(fileRequest.getFileName()) && !fileRequest.getFileName().endsWith(".jmx")) {
                    attachmentsFilterManageUpdateRequests.add(fileRequest);
                }
            }
        }
        if (CollectionUtils.isNotEmpty(attachmentsFilterManageUpdateRequests)) {
            fileManageUpdateRequests.addAll(attachmentsFilterManageUpdateRequests);
        }
        scriptManageService.updateScriptManage(scriptManageDeployUpdateRequest);
        OperationLogContextHolder.operationType(BizOpConstants.OpTypes.UPDATE);
        OperationLogContextHolder.addVars(BizOpConstants.Vars.SCRIPT_MANAGE_DEPLOY_ID,
            String.valueOf(scriptManageDeployUpdateRequest.getId()));
        OperationLogContextHolder.addVars(BizOpConstants.Vars.SCRIPT_MANAGE_DEPLOY_NAME,
            scriptManageDeployUpdateRequest.getName());
        return new ScriptManageStringResponse("????????????????????????,??????????????????????????????");
    }

    @PostMapping
    @ApiOperation(value = "??????????????????")
    @ModuleDef(moduleName = BizOpConstants.Modules.SCRIPT_MANAGE,
        subModuleName = BizOpConstants.SubModules.SCRIPT_MANAGE,
        logMsgKey = BizOpConstants.Message.SCRIPT_MANAGE_CREATE)
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.SCRIPT_MANAGE,
        needAuth = ActionTypeEnum.CREATE
    )
    public ScriptManageStringResponse createScriptManage(
        @RequestBody @Valid ScriptManageDeployCreateRequest scriptManageDeployCreateRequest) {
        List<FileManageCreateRequest> fileManageCreateRequests = scriptManageDeployCreateRequest
            .getFileManageCreateRequests();
        List<FileManageCreateRequest> attachmentsManageCreateRequests = scriptManageDeployCreateRequest
            .getAttachmentManageCreateRequests();
        //??????????????????jmx??????
        List<FileManageCreateRequest> attachmentsFilterManageCreateRequests = Lists.newArrayList();
        if (CollectionUtils.isNotEmpty(attachmentsManageCreateRequests)) {
            for (FileManageCreateRequest fileRequest : attachmentsManageCreateRequests) {
                if (StringUtils.isNotBlank(fileRequest.getFileName()) && !fileRequest.getFileName().endsWith(".jmx")) {
                    attachmentsFilterManageCreateRequests.add(fileRequest);
                }
            }
        }
        if (CollectionUtils.isNotEmpty(attachmentsFilterManageCreateRequests)) {
            fileManageCreateRequests.addAll(attachmentsFilterManageCreateRequests);
        }
        Long scriptManageId = scriptManageService.createScriptManage(scriptManageDeployCreateRequest);
        OperationLogContextHolder.operationType(BizOpConstants.OpTypes.CREATE);
        OperationLogContextHolder.addVars(BizOpConstants.Vars.SCRIPT_MANAGE_DEPLOY_ID, String.valueOf(scriptManageId));
        OperationLogContextHolder.addVars(BizOpConstants.Vars.SCRIPT_MANAGE_DEPLOY_NAME,
            scriptManageDeployCreateRequest.getName());
        return new ScriptManageStringResponse("??????????????????");
    }

    @DeleteMapping
    @ApiOperation(value = "??????????????????")
    @ModuleDef(moduleName = BizOpConstants.Modules.SCRIPT_MANAGE,
        subModuleName = BizOpConstants.SubModules.SCRIPT_MANAGE,
        logMsgKey = BizOpConstants.Message.SCRIPT_MANAGE_DELETE)
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.SCRIPT_MANAGE,
        needAuth = ActionTypeEnum.DELETE
    )
    public ScriptManageStringResponse deleteScriptManage(
        @RequestBody ScriptManageDeployDeleteRequest scriptManageDeployDeleteRequest) {
        scriptManageService.deleteScriptManage(scriptManageDeployDeleteRequest.getScriptDeployId());
        OperationLogContextHolder.operationType(BizOpConstants.OpTypes.DELETE);
        OperationLogContextHolder.addVars(BizOpConstants.Vars.SCRIPT_MANAGE_DEPLOY_ID,
            String.valueOf(scriptManageDeployDeleteRequest.getScriptDeployId()));
        return new ScriptManageStringResponse("??????????????????");
    }

    @GetMapping
    @ApiOperation(value = "??????????????????")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.SCRIPT_MANAGE,
        needAuth = ActionTypeEnum.QUERY
    )
    public ScriptManageDeployDetailResponse getScriptManageDeployDetail(@RequestParam("scriptId") Long scriptDeployId) {
        return scriptManageService.getScriptManageDeployDetail(scriptDeployId);
    }

    @PostMapping("/list")
    @ApiOperation(value = "????????????????????????")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.SCRIPT_MANAGE,
        needAuth = ActionTypeEnum.QUERY
    )
    public PagingList<ScriptManageDeployResponse> pageQueryScriptManage(
        @RequestBody @Valid ScriptManageDeployPageQueryRequest scriptManageDeployPageQueryRequest) {
        return scriptManageService.pageQueryScriptManage(scriptManageDeployPageQueryRequest);
    }

    @PostMapping("/bigfile/syncWeb")
    @ApiOperation(value = "?????????????????????????????????")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.SCRIPT_MANAGE,
        needAuth = ActionTypeEnum.CREATE
    )
    public WebPartResponse bigFileSyncRecord(
        @RequestBody WebPartRequest partRequest) {
        return scriptManageService.bigFileSyncRecord(partRequest);
    }

    @PostMapping("/createScriptTagRef")
    @ApiOperation(value = "??????????????????????????????")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.SCRIPT_MANAGE,
        needAuth = ActionTypeEnum.UPDATE
    )
    public ScriptManageStringResponse createScriptTagRef(
        @RequestBody @Valid ScriptTagCreateRefRequest scriptTagCreateRefRequest) {
        scriptManageService.createScriptTagRef(scriptTagCreateRefRequest);
        return new ScriptManageStringResponse("????????????????????????");
    }

    @GetMapping("/listScriptTag")
    @ApiOperation(value = "????????????????????????")
    public List<TagManageResponse> queryScriptTagList() {
        return scriptManageService.queryScriptTagList();
    }

    @GetMapping("/explainScriptFile")
    @ApiOperation(value = "??????????????????")
    public ScriptManageStringResponse explainScriptFile(@RequestParam String scriptFileUploadPath) {
        String xmlContent = scriptManageService.explainScriptFile(scriptFileUploadPath);
        return new ScriptManageStringResponse(xmlContent);
    }

    @GetMapping("/getFileDownLoadUrl")
    @ApiOperation(value = "????????????????????????")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.SCRIPT_MANAGE,
        needAuth = ActionTypeEnum.DOWNLOAD
    )
    public ScriptManageStringResponse getFileDownLoadUrl(@RequestParam String filePath) {
        String fileDownLoadUrl = scriptManageService.getFileDownLoadUrl(filePath);

        return new ScriptManageStringResponse(fileDownLoadUrl);
    }

    @GetMapping("/businessFlow/all")
    @ApiOperation("????????????????????????")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.BUSINESS_PROCESS,
        needAuth = ActionTypeEnum.QUERY
    )
    public List<ScriptManageSceneManageResponse> getAllScenes() {
        return scriptManageService.getAllScenes(null);
    }

    @GetMapping("/businessActivity/all")
    @ApiOperation("????????????????????????")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.BUSINESS_ACTIVITY,
        needAuth = ActionTypeEnum.QUERY
    )
    public List<ScriptManageActivityResponse> getAllActivity() {
        return scriptManageService.listAllActivities(null);
    }

    @GetMapping("/support/plugin/list")
    @ApiOperation(value = "???????????????????????????")
    public List<SupportJmeterPluginNameResponse> getSupportJmeterPluginNameList(
        @Valid SupportJmeterPluginNameRequest nameRequest) {
        return scriptManageService.getSupportJmeterPluginNameList(nameRequest);
    }

    @GetMapping("/support/plugin/list/all")
    @ApiOperation(value = "??????????????????????????????????????????")
    public List<LinkedHashMap<String, Object>> getAllJmeterPluginNameList() {
        List<SupportJmeterPluginNameResponse> old = scriptManageService.getAllJmeterPluginNameList();
        List<LinkedHashMap<String, Object>> result = new ArrayList<>(old.size());
        old.forEach(t -> t.getSinglePluginRenderResponseList().forEach(c ->
            result.add(new LinkedHashMap<String, Object>(3) {{
                put("id", c.getValue());
                put("type", t.getType());
                put("name", c.getLabel());
                put("version", getSupportJemterPluginVersionList(new SupportJmeterPluginVersionRequest() {{
                    setPluginId(c.getValue());
                }}).getVersionList());
            }})));
        return result;
    }

    @GetMapping("/support/plugin/version")
    @ApiOperation(value = "???????????????????????????")
    public SupportJmeterPluginVersionResponse getSupportJemterPluginVersionList(
        @Valid SupportJmeterPluginVersionRequest versionRequest) {
        return scriptManageService.getSupportJmeterPluginVersionList(versionRequest);
    }

    @GetMapping("/listScriptDeployByScriptId")
    @ApiOperation(value = "????????????id????????????????????????")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.SCRIPT_MANAGE,
        needAuth = ActionTypeEnum.QUERY
    )
    public List<ScriptManageDeployResponse> listScriptDeployByScriptId(
        @ApiParam(name = "scriptId", value = "??????id", required = true) @RequestParam("scriptId") Long scriptId) {
        return scriptManageService.listScriptDeployByScriptId(scriptId);
    }

    @PostMapping("/rollbackScriptDeploy")
    @ApiOperation(value = "??????????????????")
    @ModuleDef(moduleName = BizOpConstants.Modules.SCRIPT_MANAGE,
        subModuleName = BizOpConstants.SubModules.SCRIPT_MANAGE,
        logMsgKey = BizOpConstants.Message.SCRIPT_MANAGE_ROLLBACK)
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.SCRIPT_MANAGE,
        needAuth = ActionTypeEnum.QUERY
    )
    public ScriptManageStringResponse rollbackScriptDeploy(@RequestBody ScriptManageDeployRollBackRequest request) {
        String scriptManageName = scriptManageService.rollbackScriptDeploy(request.getScriptDeployId());
        OperationLogContextHolder.operationType(BizOpConstants.OpTypes.ROLLBACK);
        OperationLogContextHolder.addVars(BizOpConstants.Vars.SCRIPT_MANAGE_DEPLOY_ID, String.valueOf(request.getScriptDeployId()));
        OperationLogContextHolder.addVars(BizOpConstants.Vars.SCRIPT_MANAGE_NAME, scriptManageName);
        return new ScriptManageStringResponse("??????????????????");
    }

    @GetMapping("/compareScriptDeploy")
    @ApiOperation(value = "??????????????????")
    public List<ScriptManageXmlContentResponse> compareScriptDeploy(@RequestParam List<Long> scriptManageDeployIds) {
        return scriptManageService.getScriptManageDeployXmlContent(scriptManageDeployIds);
    }

}
