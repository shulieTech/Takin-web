package io.shulie.takin.web.entrypoint.controller.scriptmanage;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.pamirs.takin.entity.domain.entity.TBaseConfig;
import io.shulie.takin.common.beans.annotation.ModuleDef;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.utils.file.FileManagerHelper;
import io.shulie.takin.utils.json.JsonHelper;
import io.shulie.takin.web.biz.service.BaseConfigService;
import io.shulie.takin.web.biz.service.scriptmanage.ShellScriptManageService;
import io.shulie.takin.web.common.annocation.AuthVerification;
import io.shulie.takin.web.biz.constant.BizOpConstants;
import io.shulie.takin.web.common.context.OperationLogContextHolder;
import io.shulie.takin.web.common.exception.ExceptionCode;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.biz.pojo.input.scriptmanage.ShellExecuteInput;
import io.shulie.takin.web.biz.pojo.input.scriptmanage.ShellScriptManageCreateInput;
import io.shulie.takin.web.biz.pojo.input.scriptmanage.ShellScriptManagePageQueryInput;
import io.shulie.takin.web.biz.pojo.input.scriptmanage.ShellScriptManageUpdateInput;
import io.shulie.takin.web.biz.pojo.output.scriptmanage.shell.ScriptExecuteOutput;
import io.shulie.takin.web.biz.pojo.output.scriptmanage.shell.ShellScriptManageContentOutput;
import io.shulie.takin.web.biz.pojo.output.scriptmanage.shell.ShellScriptManageDetailOutput;
import io.shulie.takin.web.biz.pojo.output.scriptmanage.shell.ShellScriptManageExecuteOutput;
import io.shulie.takin.web.biz.pojo.output.scriptmanage.shell.ShellScriptManageOutput;
import io.shulie.takin.web.biz.pojo.request.scriptmanage.shell.ShellExecuteRequest;
import io.shulie.takin.web.biz.pojo.request.scriptmanage.shell.ShellScriptManageCreateRequest;
import io.shulie.takin.web.biz.pojo.request.scriptmanage.shell.ShellScriptManageDeleteRequest;
import io.shulie.takin.web.biz.pojo.request.scriptmanage.shell.ShellScriptManagePageQueryRequest;
import io.shulie.takin.web.biz.pojo.request.scriptmanage.shell.ShellScriptManageRequest;
import io.shulie.takin.web.biz.pojo.request.scriptmanage.shell.ShellScriptManageUpdateRequest;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.ScriptManageStringResponse;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.shell.ScriptExecuteResponse;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.shell.ShellScriptManageContentResponse;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.shell.ShellScriptManageDetailResponse;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.shell.ShellScriptManageDownloadResponse;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.shell.ShellScriptManageExecuteResponse;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.shell.ShellScriptManageResponse;
import io.shulie.takin.web.biz.pojo.response.tagmanage.TagManageResponse;
import io.shulie.takin.common.beans.annotation.ActionTypeEnum;
import io.shulie.takin.web.common.constant.BaseConfigConstant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
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
 * @author 无涯
 * @date 2020/12/8 3:40 下午
 */
@RestController
@RequestMapping("/api/shellScriptManage")
@Api(tags = "shell脚本管理")
public class ShellScriptManageController {

    @Autowired
    private ShellScriptManageService shellScriptManageService;
    @Autowired
    private BaseConfigService baseConfigService;

    /**
     * shell新增
     *
     * @return
     */
    @PostMapping
    @ApiOperation(value = "创建Shell脚本")
    @ModuleDef(moduleName = BizOpConstants.Modules.SCRIPT_MANAGE,
        subModuleName = BizOpConstants.SubModules.SHELL_SCRIPT_MANAGE,
        logMsgKey = BizOpConstants.Message.SCRIPT_MANAGE_CREATE)
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.SCRIPT_MANAGE,
        needAuth = ActionTypeEnum.CREATE
    )
    public ScriptManageStringResponse createScriptManage(@RequestBody @Valid ShellScriptManageCreateRequest request) {
        checkScriptManageRequest(request);
        ShellScriptManageCreateInput input = new ShellScriptManageCreateInput();
        BeanUtils.copyProperties(request, input);
        Long scriptManageId = shellScriptManageService.createScriptManage(input);
        OperationLogContextHolder.operationType(BizOpConstants.OpTypes.CREATE);
        OperationLogContextHolder.addVars(BizOpConstants.Vars.SCRIPT_MANAGE_DEPLOY_ID, String.valueOf(scriptManageId));
        OperationLogContextHolder.addVars(BizOpConstants.Vars.SCRIPT_MANAGE_DEPLOY_NAME, request.getName());
        return new ScriptManageStringResponse("创建脚本成功");
    }

    private void checkScriptManageRequest(ShellScriptManageRequest request) {
        if (request == null) {
            throw new TakinWebException(ExceptionCode.SCRIPT_MANAGE_CREATE_PARAM_VALID_ERROR, "入参为空！");
        }
        if (StringUtils.isBlank(request.getName())) {
            throw new TakinWebException(ExceptionCode.SCRIPT_MANAGE_CREATE_PARAM_VALID_ERROR, "脚本名称为空！");
        }
        if (StringUtils.isEmpty(request.getContent())) {
            throw new TakinWebException(ExceptionCode.SCRIPT_MANAGE_CREATE_PARAM_VALID_ERROR, "脚本内容为空！");
        }
        if (request.getName().length() > 16) {
            throw new TakinWebException(ExceptionCode.SCRIPT_MANAGE_CREATE_PARAM_VALID_ERROR, "脚本名称长度大于16！");
        }
        if (StringUtils.isNotBlank(request.getDescription()) && request.getDescription().length() > 200) {
            throw new TakinWebException(ExceptionCode.SCRIPT_MANAGE_CREATE_PARAM_VALID_ERROR, "脚本描述长度大于200！");
        }
    }

    @PutMapping
    @ApiOperation(value = "修改shell脚本")
    @ModuleDef(moduleName = BizOpConstants.Modules.SCRIPT_MANAGE,
        subModuleName = BizOpConstants.SubModules.SHELL_SCRIPT_MANAGE,
        logMsgKey = BizOpConstants.Message.SCRIPT_MANAGE_UPDATE)
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.SCRIPT_MANAGE,
        needAuth = ActionTypeEnum.UPDATE
    )
    public ScriptManageStringResponse updateScriptManage(@RequestBody @Valid ShellScriptManageUpdateRequest request) {
        checkScriptManageRequest(request);
        if (request.getScriptDeployId() == null) {
            throw new TakinWebException(ExceptionCode.SCRIPT_MANAGE_CREATE_PARAM_VALID_ERROR, "更新必须传脚本实例id！");
        }
        ShellScriptManageUpdateInput input = new ShellScriptManageUpdateInput();
        BeanUtils.copyProperties(request, input);
        String content = shellScriptManageService.updateScriptManage(input);
        OperationLogContextHolder.operationType(BizOpConstants.OpTypes.UPDATE);
        OperationLogContextHolder.addVars(BizOpConstants.Vars.SCRIPT_MANAGE_DEPLOY_NAME, request.getName());
        OperationLogContextHolder.addVars(BizOpConstants.Vars.SCRIPT_MANAGE_DEPLOY_ID, String.valueOf(request.getScriptDeployId()));
        return new ScriptManageStringResponse(content);
    }

    /**
     * 删除shell脚本
     *
     * @return
     */
    @DeleteMapping
    @ApiOperation(value = "删除shell文件")
    @ModuleDef(moduleName = BizOpConstants.Modules.SCRIPT_MANAGE,
        subModuleName = BizOpConstants.SubModules.SHELL_SCRIPT_MANAGE,
        logMsgKey = BizOpConstants.Message.SCRIPT_MANAGE_SCRIPTID_DELETE)
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.SCRIPT_MANAGE,
        needAuth = ActionTypeEnum.DELETE
    )
    public ScriptManageStringResponse deleteScriptManage(@RequestBody ShellScriptManageDeleteRequest request) {
        shellScriptManageService.deleteScriptManage(request.getScriptId());
        OperationLogContextHolder.operationType(BizOpConstants.OpTypes.DELETE);
        OperationLogContextHolder.addVars(BizOpConstants.Vars.SCRIPT_MANAGE_ID, String.valueOf(request.getScriptId()));
        return new ScriptManageStringResponse("删除脚本成功");
    }

    /**
     * 统一下载 文件内容在web的nfs中
     * 文件存放记录放到t_base_config
     *
     * @return
     */
    @GetMapping("/getDownload")
    @ApiOperation(value = "获取下载样例")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.SCRIPT_MANAGE,
        needAuth = ActionTypeEnum.DOWNLOAD
    )
    public List<ShellScriptManageDownloadResponse> getDownload() {
        TBaseConfig tBaseConfig = baseConfigService.queryByConfigCode(BaseConfigConstant.SHELL_SCRIPT_DOWNLOAD_SAMPLE);
        if (tBaseConfig == null) {
            throw new TakinWebException(ExceptionCode.SCRIPT_MANAGE_VALID_NO_CONFIG, "没有" + BaseConfigConstant.SHELL_SCRIPT_DOWNLOAD_SAMPLE + "配置");
        }
        List<ShellScriptManageDownloadResponse> result = JsonHelper.json2List(tBaseConfig.getConfigValue(), ShellScriptManageDownloadResponse.class);
        result.forEach(resp -> {
            try {
                if (new File(resp.getUrl()).exists()) {
                    resp.setContent(FileManagerHelper.readFileToString(new File(resp.getUrl()), "UTF-8"));
                } else {
                    resp.setContent("文件不存在");
                }
            } catch (IOException e) {
                resp.setContent("");
            }
        });
        return result;
    }

    @GetMapping
    @ApiOperation(value = "查询shell详情")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.SCRIPT_MANAGE,
        needAuth = ActionTypeEnum.QUERY
    )
    public ShellScriptManageDetailResponse getScriptManageDetail(@RequestParam("scriptId") Long scriptId) {
        ShellScriptManageDetailOutput output = shellScriptManageService.getScriptManageDetail(scriptId);
        ShellScriptManageDetailResponse response = new ShellScriptManageDetailResponse();
        BeanUtils.copyProperties(output, response);
        return response;
    }

    @GetMapping("/history")
    @ApiOperation(value = "查看shell版本内容")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.SCRIPT_MANAGE,
        needAuth = ActionTypeEnum.QUERY
    )
    public ShellScriptManageContentResponse compareScriptDeploy(@RequestParam("scriptId") Long scriptId, @RequestParam("version") Integer version) {
        ShellScriptManageContentResponse response = new ShellScriptManageContentResponse();
        ShellScriptManageContentOutput output = shellScriptManageService.getShellScriptManageContent(scriptId, version);
        if (output != null) {
            BeanUtils.copyProperties(output, response);
        }
        return response;
    }

    /**
     * 获取shell列表
     *
     * @return
     */
    @PostMapping("/list")
    @ApiOperation(value = "查询shell列表")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.SCRIPT_MANAGE,
        needAuth = ActionTypeEnum.QUERY
    )
    public PagingList<ShellScriptManageResponse> pageQueryScriptManage(@RequestBody @Valid ShellScriptManagePageQueryRequest request) {
        ShellScriptManagePageQueryInput input = new ShellScriptManagePageQueryInput();
        BeanUtils.copyProperties(request, input);
        PagingList<ShellScriptManageOutput> outputPage = shellScriptManageService.pageQueryScriptManage(input);
        List<ShellScriptManageOutput> outputList = outputPage.getList();
        List<ShellScriptManageResponse> responses = outputList.stream().map(output -> {
            ShellScriptManageResponse response = new ShellScriptManageResponse();
            BeanUtils.copyProperties(output, response);

            if (output.getTagManageOutputs() != null) {
                List<TagManageResponse> tagManageResponses = output.getTagManageOutputs().stream().map(tag -> {
                    TagManageResponse manageResponse = new TagManageResponse();
                    BeanUtils.copyProperties(tag, manageResponse);
                    return manageResponse;
                }).collect(Collectors.toList());
                response.setTagManageResponses(tagManageResponses);
            }
            return response;
        }).collect(Collectors.toList());
        return PagingList.of(responses, outputPage.getTotal());
    }

    @GetMapping("/execute")
    @ApiOperation(value = "运行shell脚本")
    @ModuleDef(moduleName = BizOpConstants.Modules.SCRIPT_MANAGE,
        subModuleName = BizOpConstants.SubModules.SHELL_SCRIPT_MANAGE,
        logMsgKey = BizOpConstants.Message.SCRIPT_MANAGE_EXECUTE)
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.SCRIPT_MANAGE,
        needAuth = ActionTypeEnum.START_STOP
    )
    public ShellScriptManageExecuteResponse execute(@RequestParam("scriptDeployId") Long scriptDeployId) {
        ShellScriptManageExecuteOutput output = shellScriptManageService.execute(scriptDeployId);
        ShellScriptManageExecuteResponse response = new ShellScriptManageExecuteResponse();
        OperationLogContextHolder.operationType(BizOpConstants.OpTypes.EXECUTE);
        OperationLogContextHolder.addVars(BizOpConstants.Vars.SCRIPT_MANAGE_DEPLOY_ID, String.valueOf(scriptDeployId));
        response.setSuccess(output.getSuccess());
        response.setMessage(
            output.getMessage() != null && output.getMessage().size() > 0 ? output.getMessage().get(output.getMessage().size() - 1) : "");
        return response;
    }

    @GetMapping("/getExecuteResult")
    @ApiOperation(value = "获取脚本执行结果")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.SCRIPT_MANAGE,
        needAuth = ActionTypeEnum.QUERY
    )
    public PagingList<ScriptExecuteResponse> getExecuteResult(ShellExecuteRequest request) {
        ShellExecuteInput input = new ShellExecuteInput();
        BeanUtils.copyProperties(request, input);
        PagingList<ScriptExecuteOutput> outputs = shellScriptManageService.getExecuteResult(input);
        List<ScriptExecuteResponse> responses = outputs.getList().stream().map(output -> {
            ScriptExecuteResponse response = new ScriptExecuteResponse();
            BeanUtils.copyProperties(output, response);
            return response;
        }).collect(Collectors.toList());

        return PagingList.of(responses, outputs.getTotal());
    }

}
