package io.shulie.takin.web.entrypoint.controller.scriptmanage;

import io.shulie.takin.adapter.api.model.response.file.UploadResponse;
import io.shulie.takin.common.beans.annotation.ActionTypeEnum;
import io.shulie.takin.common.beans.annotation.AuthVerification;
import io.shulie.takin.common.beans.annotation.ModuleDef;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.biz.constant.BizOpConstants;
import io.shulie.takin.web.biz.pojo.request.filemanage.FileManageUpdateRequest;
import io.shulie.takin.web.biz.pojo.request.linkmanage.BusinessFlowDataFileRequest;
import io.shulie.takin.web.biz.pojo.request.linkmanage.BusinessFlowDataFileV2Request;
import io.shulie.takin.web.biz.pojo.request.scriptmanage.PageScriptCssManageQueryRequest;
import io.shulie.takin.web.biz.pojo.request.scriptmanage.ScriptCsvAliasNameUpdateRequest;
import io.shulie.takin.web.biz.pojo.request.scriptmanage.ScriptCsvCreateTaskRequest;
import io.shulie.takin.web.biz.pojo.request.scriptmanage.ScriptCsvDataTemplateRequest;
import io.shulie.takin.web.biz.pojo.response.filemanage.FileManageResponse;
import io.shulie.takin.web.biz.pojo.response.linkmanage.BusinessFlowDetailResponse;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.*;
import io.shulie.takin.web.biz.service.datamanage.CsvManageService;
import io.shulie.takin.web.common.constant.ApiUrls;
import io.shulie.takin.web.common.context.OperationLogContextHolder;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author hezhongqi
 * @Package io.shulie.takin.web.entrypoint.controller.scriptmanage
 * @ClassName: ScriptDataController
 * @description:
 * @date 2023/9/20 17:08
 */
@RestController
@RequestMapping(ApiUrls.TAKIN_API_URL + "script/data")
@Api(tags = "接口: 脚本数据")
public class ScriptDataController {

    @Autowired
    private CsvManageService csvManageService;


    @ApiOperation("文件-删除")
    @GetMapping("/deleteFile")
//    @AuthVerification(
//            moduleCode = BizOpConstants.ModuleCode.SCRIPT_CSV_MANAGE,
//            needAuth = ActionTypeEnum.QUERY
//    )
    public ScriptManageStringResponse deleteFile(@RequestParam("fileManageId") Long fileManageId) {
        csvManageService.deleteFile(fileManageId);
        return new ScriptManageStringResponse("删除成功");
    }

    @ApiOperation("csv组件列表")
    @GetMapping("csv/list")
//    @AuthVerification(
//        moduleCode = BizOpConstants.ModuleCode.SCRIPT_CSV_MANAGE,
//        needAuth = ActionTypeEnum.QUERY
//    )
    public List<ScriptCsvDataSetResponse> pageCsvByBusinessFlowId(@RequestParam("businessFlowId") Long businessFlowId) {
        return csvManageService.listCsvByBusinessFlowId(businessFlowId);
    }

    @ApiOperation("附件列表")
    @GetMapping("annex/list")
//    @AuthVerification(
//            moduleCode = BizOpConstants.ModuleCode.SCRIPT_CSV_MANAGE,
//            needAuth = ActionTypeEnum.QUERY
//    )
    public List<FileManageResponse> listAnnexByBusinessFlowId(@RequestParam("businessFlowId") Long businessFlowId) {
        return csvManageService.listAnnexByBusinessFlowId(businessFlowId);
    }


    @ApiOperation("csv拆分,csv是否按分区排序")
    @PutMapping("csv/splitOrOrderSplit")
    public ScriptManageStringResponse spilt(@RequestBody @Valid BusinessFlowDataFileV2Request request) {
        // todo 操作日志记录
        csvManageService.spiltOrIsOrderSplit(request);
        return new ScriptManageStringResponse("设置成功");
    }


    @ApiOperation("点击生成csv，调用接口，获取相关数据")
    @GetMapping("csv/create/detail")
    public List<ScriptCsvCreateDetailResponse> createDetail(@RequestParam("businessFlowId") Long businessFlowId) {
        return csvManageService.createDetail(businessFlowId);
    }


    @ApiOperation("拉取模板数据")
    @PostMapping("csv/template/pull")
    public ScriptCsvDataTemplateResponse pullTemplate(@RequestBody ScriptCsvDataTemplateRequest request) {
        // mock数据
//        request.setAppName("msasso_a");
//        request.setMethodName("GET");
//        request.setServiceName("/bit-msa-sso/common/v2/auth/send2FACode/token/{value}/account/{value}/cipher/{value}/AK/BIT-MSA");
//        request.setAppName("fm_ac");
//        request.setMethodName("");
//        request.setServiceName("/ac/auth");

        return csvManageService.getCsvTemplate(request);
    }

    @ApiOperation("点击生成csv,并生成任务")
    @PostMapping("csv/create")
    public ScriptManageStringResponse createTask(@RequestBody List<ScriptCsvCreateTaskRequest> request) {
        csvManageService.createTask(request);
        return new ScriptManageStringResponse("任务已提交");
    }

    @ApiOperation("取消任务")
    @PutMapping("csv/task/cancel")
    public ScriptManageStringResponse cancelTask(@RequestBody ScriptCsvCreateTaskRequest request) {
        csvManageService.cancelTask(request);
        return new ScriptManageStringResponse("取消任务成功");
    }

    @ApiOperation("展示csv选择数据")
    @GetMapping("csv/file/list")
    public ScriptCsvDataSetResponse listFileCsvById(@RequestParam("businessFlowId") Long businessFlowId,
                                                    @RequestParam("scriptCsvDataSetId") Long scriptCsvDataSetId) {
        return csvManageService.listFileCsvById(businessFlowId, scriptCsvDataSetId);
    }

    @ApiOperation("csv文件选择")
    @GetMapping("csv/file/select")
    public ScriptManageStringResponse selectCsv(@RequestParam("scriptCsvDataSetId") Long scriptCsvDataSetId,
                                                    @RequestParam("fileManageId") Long fileManageId) {
        csvManageService.selectCsv(scriptCsvDataSetId, fileManageId);
        return new ScriptManageStringResponse("选择成功");
    }



    @ApiOperation("csv管理列表")
    @PostMapping("csv/manage")
    public PagingList<ScriptCsvManageResponse> cssManage(@RequestBody PageScriptCssManageQueryRequest request) {
        return  csvManageService.csvManage(request);
    }


    /**
     * 上传文件  无需权限
     * @param sceneId
     * @param scriptCsvDataSetId
     * @param file
     * @return
     */
    @PostMapping("/upload")
    @ApiOperation(value = "文件上传")
    public List<UploadResponse> upload(Long sceneId, Long scriptCsvDataSetId, List<MultipartFile> file) {
        return csvManageService.upload(sceneId,scriptCsvDataSetId, file);
    }

    @PostMapping("/uploadDataFile")
    @ApiOperation("业务流程上传css数据文件")
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
        BusinessFlowDetailResponse sceneDetailDto = csvManageService.uploadDataFile(businessFlowDataFileRequest);
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
        }
        return ResponseResult.success(sceneDetailDto);
    }


    @PostMapping("/updateAliasName")
    @ApiOperation("更新备注")
    public ScriptManageStringResponse updateAliasName(@RequestBody ScriptCsvAliasNameUpdateRequest request) {
        csvManageService.updateAliasName(request);
        return new ScriptManageStringResponse("备注更新成功");
    }


}
