package io.shulie.takin.web.entrypoint.controller.opsscript;

import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import com.google.common.collect.Maps;
import com.pamirs.takin.entity.dao.dict.TDictionaryDataMapper;
import com.pamirs.takin.entity.domain.vo.TDictionaryVo;
import io.shulie.takin.common.beans.annotation.ModuleDef;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.biz.constant.BizOpConstants;
import io.shulie.takin.web.biz.service.OpsScriptFileService;
import io.shulie.takin.web.biz.service.OpsScriptManageService;
import io.shulie.takin.web.common.constant.ApiUrls;
import io.shulie.takin.web.common.domain.WebResponse;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.data.dao.dictionary.DictionaryDataDAO;
import io.shulie.takin.web.data.param.opsscript.OpsScriptParam;
import io.shulie.takin.web.data.param.opsscript.OpsUploadFileParam;
import io.shulie.takin.web.data.result.opsscript.OpsScriptDetailVO;
import io.shulie.takin.web.data.result.opsscript.OpsScriptVO;
import io.shulie.takin.common.beans.annotation.ActionTypeEnum;
import io.shulie.takin.common.beans.annotation.AuthVerification;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 运维脚本主表(OpsScriptManage)表控制层
 *
 * @author caijy
 * @since 2021-06-16 10:41:43
 */
@RestController
@RequestMapping(ApiUrls.TAKIN_API_URL + "opsScriptManage")
@Api(tags = "运维脚本-接口")
public class OpsScriptManageController {

    @Resource(type = OpsScriptManageService.class)
    OpsScriptManageService opsScriptManageService;

    @Resource(type = DictionaryDataDAO.class)
    DictionaryDataDAO dictionaryDataDao;

    @Resource(type = OpsScriptFileService.class)
    OpsScriptFileService opsScriptFileService;

    @ApiOperation("列表接口")
    @GetMapping("page")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.OPS_SCRIPT_MANAGE,
        needAuth = ActionTypeEnum.QUERY
    )
    public PagingList<OpsScriptVO> page(OpsScriptParam param) {
        return opsScriptManageService.page(param);
    }

    @ApiOperation("运维脚本详情")
    @GetMapping("detail")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.OPS_SCRIPT_MANAGE,
        needAuth = ActionTypeEnum.QUERY
    )
    public OpsScriptDetailVO detail(OpsScriptParam param) {
        return opsScriptManageService.detail(param);
    }

    @ApiOperation("获取运维脚本类型")
    @GetMapping("/getScriptType")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.OPS_SCRIPT_MANAGE,
        needAuth = ActionTypeEnum.QUERY
    )
    public List<TDictionaryVo> getScriptType() {
        return dictionaryDataDao.getDictByCode("OPS_SCRIPT_TYPE");
    }

    @ApiOperation("保存运维脚本")
    @PostMapping("/add")
    @ModuleDef(
        moduleName = BizOpConstants.Modules.OPS_SCRIPT_MANAGE,
        subModuleName = BizOpConstants.SubModules.OPS_SCRIPT_MANAGE,
        logMsgKey = BizOpConstants.Message.OPS_SCRIPT_MANAGE_CREATE
    )
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.OPS_SCRIPT_MANAGE,
        needAuth = ActionTypeEnum.CREATE
    )
    public Boolean add(@RequestBody OpsScriptParam param) {
        return opsScriptManageService.add(param);
    }

    @ApiOperation("编辑运维脚本")
    @PutMapping("/update")
    @ModuleDef(
        moduleName = BizOpConstants.Modules.OPS_SCRIPT_MANAGE,
        subModuleName = BizOpConstants.SubModules.OPS_SCRIPT_MANAGE,
        logMsgKey = BizOpConstants.Message.OPS_SCRIPT_MANAGE_UPDATE
    )
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.OPS_SCRIPT_MANAGE,
        needAuth = ActionTypeEnum.UPDATE
    )
    public Boolean update(@RequestBody OpsScriptParam param) {
        return opsScriptManageService.update(param);
    }

    @ApiOperation("删除运维脚本")
    @DeleteMapping("/delete")
    @ModuleDef(
        moduleName = BizOpConstants.Modules.OPS_SCRIPT_MANAGE,
        subModuleName = BizOpConstants.SubModules.OPS_SCRIPT_MANAGE,
        logMsgKey = BizOpConstants.Message.OPS_SCRIPT_MANAGE_DELETE
    )
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.OPS_SCRIPT_MANAGE,
        needAuth = ActionTypeEnum.DELETE
    )
    public Boolean delete(@RequestBody OpsScriptParam param) {
        return opsScriptManageService.delete(param);
    }

    @PostMapping("/upload")
    @ApiOperation(value = "文件上传")
    public WebResponse upload(List<MultipartFile> file) {
        if (file == null || file.size() == 0) {
            throw new TakinWebException(TakinWebExceptionEnum.OPS_SCRIPT_FILE_VALIDATE_ERROR, "上传文件不能为空");
        }
        return WebResponse.success(opsScriptFileService.upload(file, 1));
    }

    @PostMapping("/editFile")
    @ApiOperation(value = "编辑文件内容")
    public WebResponse editFile(@RequestBody OpsUploadFileParam param) {
        if (param.getPath() == null) {
            throw new TakinWebException(TakinWebExceptionEnum.OPS_SCRIPT_FILE_UPDATE_ERROR, "文件路径不能为空！");
        }
        return WebResponse.success(opsScriptFileService.editFile(param));
    }

    @GetMapping("/viewFile")
    @ApiOperation(value = "查看文件内容")
    public WebResponse viewFile(String path) {
        if (path == null) {
            throw new TakinWebException(TakinWebExceptionEnum.OPS_SCRIPT_FILE_VALIDATE_ERROR, "文件路径不能为空！");
        }
        return WebResponse.success(opsScriptFileService.viewFile(path));
    }

    @PostMapping("/attachment/upload")
    @ApiOperation(value = "附件上传")
    public WebResponse uploadAttachment(List<MultipartFile> file) {
        if (file == null || file.size() == 0) {
            throw new TakinWebException(TakinWebExceptionEnum.OPS_SCRIPT_FILE_VALIDATE_ERROR, "上传附件不能为空");
        }
        return WebResponse.success(opsScriptFileService.upload(file, 2));
    }

    @DeleteMapping
    @ApiOperation(value = "文件删除")
    public ResponseResult delete(String uploadId) {
        opsScriptFileService.delete(uploadId);
        //根据文件： 删除大文件行数，删除大文件起始位置
        return ResponseResult.success();
    }

    @ApiOperation("执行脚本")
    @PostMapping("/execute")
    @ModuleDef(
        moduleName = BizOpConstants.Modules.OPS_SCRIPT_MANAGE,
        subModuleName = BizOpConstants.SubModules.OPS_SCRIPT_MANAGE,
        logMsgKey = BizOpConstants.Message.OPS_SCRIPT_MANAGE_CREATE
    )
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.OPS_SCRIPT_MANAGE,
        needAuth = ActionTypeEnum.UPDATE
    )
    public Boolean execute(@RequestBody OpsScriptParam param) {
        return opsScriptManageService.execute(param);
    }

    @ApiOperation("获取执行实况")
    @GetMapping("/getExcutionLog")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.OPS_SCRIPT_MANAGE,
        needAuth = ActionTypeEnum.QUERY
    )
    public WebResponse getExcutionLog(String id) {
        return WebResponse.success(opsScriptManageService.getExcutionLog(id));
    }

    @ApiOperation("获取执行结果")
    @GetMapping("/getExcutionResult")
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.OPS_SCRIPT_MANAGE,
        needAuth = ActionTypeEnum.QUERY
    )
    public WebResponse getExcutionResult(String id) {
        return WebResponse.success(opsScriptManageService.getExcutionResult(id));
    }
}
