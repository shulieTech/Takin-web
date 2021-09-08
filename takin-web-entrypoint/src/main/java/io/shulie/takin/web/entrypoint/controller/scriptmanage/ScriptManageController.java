package io.shulie.takin.web.entrypoint.controller.scriptmanage;

import java.util.List;

import javax.validation.Valid;

import com.google.common.collect.Lists;
import io.shulie.takin.common.beans.annotation.ModuleDef;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.service.scriptmanage.ScriptManageService;
import io.shulie.takin.web.common.annocation.AuthVerification;
import io.shulie.takin.web.biz.constant.BizOpConstants;
import io.shulie.takin.web.common.context.OperationLogContextHolder;
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
import io.shulie.takin.common.beans.annotation.ActionTypeEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
@Api(tags = "脚本文件管理")
public class ScriptManageController {

    @Autowired
    private ScriptManageService scriptManageService;

    @GetMapping("/getZipFileUrl")
    @ApiOperation(value = "打包下载脚本实例所有文件")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.SCRIPT_MANAGE,
        needAuth = ActionTypeEnum.DOWNLOAD
    )
    public ScriptManageStringResponse getZipFileUrl(@RequestParam("scriptId") @Valid Long scriptDeployId) {
        String zipFileUrl = scriptManageService.getZipFileUrl(scriptDeployId);
        return new ScriptManageStringResponse(zipFileUrl);
    }

    @PutMapping
    @ApiOperation(value = "修改脚本文件")
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
        //附件不能上传jmx文件
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
        return new ScriptManageStringResponse("编辑脚本配置成功,压测数据文件位点清零");
    }

    @PostMapping
    @ApiOperation(value = "创建脚本文件")
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
        //附件不能上传jmx文件
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
        return new ScriptManageStringResponse("创建脚本成功");
    }

    @DeleteMapping
    @ApiOperation(value = "删除脚本文件")
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
        return new ScriptManageStringResponse("删除脚本成功");
    }

    @GetMapping
    @ApiOperation(value = "查询脚本详情")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.SCRIPT_MANAGE,
        needAuth = ActionTypeEnum.QUERY
    )
    public ScriptManageDeployDetailResponse getScriptManageDeployDetail(@RequestParam("scriptId") Long scriptDeployId) {
        return scriptManageService.getScriptManageDeployDetail(scriptDeployId);
    }

    @PostMapping("/list")
    @ApiOperation(value = "查询脚本文件列表")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.SCRIPT_MANAGE,
        needAuth = ActionTypeEnum.QUERY
    )
    public PagingList<ScriptManageDeployResponse> pageQueryScriptManage(
        @RequestBody @Valid ScriptManageDeployPageQueryRequest scriptManageDeployPageQueryRequest) {
        return scriptManageService.pageQueryScriptManage(scriptManageDeployPageQueryRequest);
    }

    @PostMapping("/bigfile/syncWeb")
    @ApiOperation(value = "大文件上传脚本同步数据")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.SCRIPT_MANAGE,
        needAuth = ActionTypeEnum.CREATE
    )
    public WebPartResponse bigFileSyncRecord(
        @RequestBody WebPartRequest partRequest) {
        return scriptManageService.bigFileSyncRecord(partRequest);
    }

    @PostMapping("/createScriptTagRef")
    @ApiOperation(value = "新增脚本标签关联关系")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.SCRIPT_MANAGE,
        needAuth = ActionTypeEnum.UPDATE
    )
    public ScriptManageStringResponse createScriptTagRef(
        @RequestBody @Valid ScriptTagCreateRefRequest scriptTagCreateRefRequest) {
        scriptManageService.createScriptTagRef(scriptTagCreateRefRequest);
        return new ScriptManageStringResponse("关联关系添加成功");
    }

    @GetMapping("/listScriptTag")
    @ApiOperation(value = "查询脚本标签列表")
    public List<TagManageResponse> queryScriptTagList() {
        return scriptManageService.queryScriptTagList();
    }

    @GetMapping("/explainScriptFile")
    @ApiOperation(value = "解析脚本文件")
    public ScriptManageStringResponse explainScriptFile(@RequestParam String scriptFileUploadPath) {
        String xmlContent = scriptManageService.explainScriptFile(scriptFileUploadPath);
        return new ScriptManageStringResponse(xmlContent);
    }

    @GetMapping("/getFileDownLoadUrl")
    @ApiOperation(value = "获取文件下载路径")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.SCRIPT_MANAGE,
        needAuth = ActionTypeEnum.DOWNLOAD
    )
    public ScriptManageStringResponse getFileDownLoadUrl(@RequestParam String filePath) {
        String fileDownLoadUrl = scriptManageService.getFileDownLoadUrl(filePath);

        return new ScriptManageStringResponse(fileDownLoadUrl);
    }

    @GetMapping("/businessFlow/all")
    @ApiOperation("业务流程列表查询")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.BUSINESS_PROCESS,
        needAuth = ActionTypeEnum.QUERY
    )
    public List<ScriptManageSceneManageResponse> getAllScenes() {
        return scriptManageService.getAllScenes(null);
    }

    @GetMapping("/businessActivity/all")
    @ApiOperation("业务活动列表查询")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.BUSINESS_ACTIVITY,
        needAuth = ActionTypeEnum.QUERY
    )
    public List<ScriptManageActivityResponse> getAllActivity() {
        return scriptManageService.listAllActivities(null);
    }

    @GetMapping("/support/plugin/list")
    @ApiOperation(value = "获取支持的插件列表")
    public List<SupportJmeterPluginNameResponse> getSupportJmeterPluginNameList(
        @Valid SupportJmeterPluginNameRequest nameRequest) {
        return scriptManageService.getSupportJmeterPluginNameList(nameRequest);
    }

    @GetMapping("/support/plugin/version")
    @ApiOperation(value = "获取支持的插件版本")
    public SupportJmeterPluginVersionResponse getSupportJemterPluginVersionList(
        @Valid SupportJmeterPluginVersionRequest versionRequest) {
        return scriptManageService.getSupportJmeterPluginVersionList(versionRequest);
    }

    @GetMapping("/listScriptDeployByScriptId")
    @ApiOperation(value = "根据脚本id查询脚本实例列表")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.SCRIPT_MANAGE,
        needAuth = ActionTypeEnum.QUERY
    )
    public List<ScriptManageDeployResponse> listScriptDeployByScriptId(
        @ApiParam(name = "scriptId", value = "脚本id", required = true) @RequestParam("scriptId") Long scriptId) {
        return scriptManageService.listScriptDeployByScriptId(scriptId);
    }

    @PostMapping("/rollbackScriptDeploy")
    @ApiOperation(value = "回滚历史脚本")
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
        return new ScriptManageStringResponse("回滚脚本成功");
    }

    @GetMapping("/compareScriptDeploy")
    @ApiOperation(value = "脚本内容对比")
    public List<ScriptManageXmlContentResponse> compareScriptDeploy(@RequestParam List<Long> scriptManageDeployIds) {
        return scriptManageService.getScriptManageDeployXmlContent(scriptManageDeployIds);
    }

}
