package io.shulie.takin.web.entrypoint.controller.pts;

import com.alibaba.fastjson.JSON;
import com.pamirs.takin.entity.domain.entity.TBaseConfig;
import io.shulie.takin.common.beans.annotation.ActionTypeEnum;
import io.shulie.takin.common.beans.annotation.AuthVerification;
import io.shulie.takin.common.beans.annotation.ModuleDef;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.jmeter.adapter.JmeterFunctionAdapter;
import io.shulie.takin.web.biz.constant.BizOpConstants;
import io.shulie.takin.web.biz.pojo.request.pts.*;
import io.shulie.takin.web.biz.pojo.response.linkmanage.BusinessFlowDetailResponse;
import io.shulie.takin.web.biz.pojo.response.pts.JmeterFunctionResponse;
import io.shulie.takin.web.biz.pojo.response.pts.PtsDebugResponse;
import io.shulie.takin.web.biz.pojo.response.pts.PtsSceneResponse;
import io.shulie.takin.web.biz.service.ActivityService;
import io.shulie.takin.web.biz.service.BaseConfigService;
import io.shulie.takin.web.biz.service.pts.PtsParseJmxToObjectTools;
import io.shulie.takin.web.biz.service.pts.PtsProcessService;
import io.shulie.takin.web.common.constant.ApiUrls;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.jmeter.functions.InvalidVariableException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * @author junshi
 * @ClassName PtsProcessController
 * @Description
 * @createTime 2023年03月15日 16:11
 */
@RestController
@RequestMapping(ApiUrls.TAKIN_API_URL + "pts")
@Api(tags = "pts-脚本调试")
@Slf4j
public class PtsProcessController {

    @Autowired
    private ActivityService activityService;

    @Autowired
    private PtsProcessService ptsProcessService;

    @Value("${script.temp.path}")
    private String tempPath;

    @Autowired
    private BaseConfigService baseConfigService;

    private static final String JMETER_FUNCTION = "JMETER_FUNCTION";

    @ApiOperation("解析jmx文件")
    @PostMapping("/parse/jmx")
    public PtsSceneResponse parseJmx(MultipartFile file) {
        PtsSceneResponse sceneResponse = new PtsSceneResponse();
        if(file.isEmpty()) {
            sceneResponse.getMessage().add("上传文件不能为空");
            return sceneResponse;
        }
        String originalFilename = null;
        try {
            originalFilename = URLEncoder.encode(file.getOriginalFilename(), "UTF-8").replaceAll("\\+", "%20");
        } catch (UnsupportedEncodingException e) {
            sceneResponse.getMessage().add("解析文件名失败");
            log.error("解析文件名失败={}", e.getMessage());
            return sceneResponse;
        }
        if(originalFilename == null || !originalFilename.endsWith(".jmx")) {
            sceneResponse.getMessage().add("上传文件格式不正确，仅支持.jmx结尾的文件");
            return sceneResponse;
        }
        //保存文件到本地
        File destDir = new File(tempPath + "/pts");
        destDir.mkdirs();
        File destFile = new File(tempPath + "/pts/"+originalFilename);
        if(destFile.exists()) {
            destFile.delete();
        }
        try {
            file.transferTo(destFile);
        } catch (IOException e) {
            sceneResponse.getMessage().add("上传文件失败");
            log.error("上传文件失败={}", e.getMessage());
            return sceneResponse;
        }
        //解析文件为对象
        return PtsParseJmxToObjectTools.parseJmxFile(tempPath + "/pts/"+originalFilename, false);
    }

    @ApiOperation(value = "保存业务流程")
    @PostMapping("/process/add")
    @ModuleDef(
        moduleName = BizOpConstants.Modules.LINK_CARDING,
        subModuleName = BizOpConstants.SubModules.BUSINESS_PROCESS,
        logMsgKey = BizOpConstants.Message.MESSAGE_BUSINESS_PROCESS_CREATE2
    )
    @AuthVerification(
        moduleCode = BizOpConstants.ModuleCode.BUSINESS_PROCESS,
        needAuth = ActionTypeEnum.CREATE
    )
    public BusinessFlowDetailResponse saveProcess(@Valid @RequestBody PtsSceneRequest request) {
        return ptsProcessService.saveProcess(request);
    }

//    @ApiOperation("查询PTS业务活动列表")
//    @GetMapping("/businessActivity/page/list")
//    @AuthVerification(
//        moduleCode = BizOpConstants.ModuleCode.BUSINESS_ACTIVITY,
//        needAuth = ActionTypeEnum.QUERY
//    )
//    public PagingList<ActivityListResponse> pageActivities(@Valid PtsActivityQueryRequest queryRequest) {
//        ActivityQueryRequest request = new ActivityQueryRequest();
//        return activityService.pageActivities(request);
//    }
//
    @ApiOperation("PTS业务流程详情")
    @GetMapping("/process/detail")
    @AuthVerification(
            moduleCode = BizOpConstants.ModuleCode.BUSINESS_ACTIVITY,
            needAuth = ActionTypeEnum.QUERY
    )
    public PtsSceneResponse detailActivity(@RequestParam(name = "id") Long id) {
        PtsSceneResponse sceneResponse = ptsProcessService.detailProcess(id);
        //处理Get请求，拼参数到url里
        dealLinks((sceneResponse.getPreLink()));
        for(PtsLinkRequest linkRequest : sceneResponse.getLinks()) {
            dealLinks(linkRequest);
        }
        return sceneResponse;
    }

    @ApiOperation("发起调试")
    @PostMapping("/process/debug")
    public ResponseResult debugScene(@Valid @RequestBody IdRequest request) {
        return ResponseResult.success(ptsProcessService.debugProcess(request.getId()));
    }

    @ApiOperation("API调试列表|明细")
    @GetMapping("/process/debug/record/list")
    public PtsDebugResponse debugRecordList(@RequestParam(name = "id") Long id) {
        return ptsProcessService.getDebugRecord(id);
    }

    @ApiOperation("API调试日志")
    @GetMapping("/process/debug/log")
    public ResponseResult debugRecordLog(@RequestParam(name = "id") Long id) {
        return ResponseResult.success(ptsProcessService.getDebugLog(id));
    }

    @ApiOperation("函数列表")
    @GetMapping("/function/list")
    public List<JmeterFunctionResponse> listJmxFunction() {
        TBaseConfig baseConfig = baseConfigService.queryByConfigCode(JMETER_FUNCTION);
        if(baseConfig == null) {
            return new ArrayList<>();
        }
        return JSON.parseArray(baseConfig.getConfigValue(), JmeterFunctionResponse.class);
    }

    @ApiOperation("函数调试")
    @PostMapping("/function/debug")
    public ResponseResult debugJmxFunction(@Valid @RequestBody FunctionDebugRequest request) {
        JmeterFunctionAdapter functionAdapter = JmeterFunctionAdapter.getInstance();
        String funcName = functionAdapter.getFunctionName(request.getFuncStr());
        if(StringUtils.isBlank(funcName)) {
            log.error("函数名称不能为空,函数={}", request.getFuncStr());
            throw new TakinWebException(TakinWebExceptionEnum.SCRIPT_DEBUG_VALIDATE_ERROR, "函数名称不能为空！");
        }
        if(!functionAdapter.supportFunction(funcName)) {
            log.error("函数不支持,函数={}", request.getFuncStr());
            throw new TakinWebException(TakinWebExceptionEnum.SCRIPT_DEBUG_VALIDATE_ERROR, "函数不支持！");
        }
        try {
            return ResponseResult.success(functionAdapter.execute(request.getFuncStr()));
        } catch (InvalidVariableException e) {
            log.error("函数调试异常,函数={},异常={}", request.getFuncStr(), e.getMessage());
            throw new TakinWebException(TakinWebExceptionEnum.SCRIPT_DEBUG_DATA_PROCESS_ERROR, "函数调试异常！");
        }
    }

    private void dealLinks(PtsLinkRequest linkRequest) {
        for(PtsApiRequest apiRequest : linkRequest.getApis()) {
            if(apiRequest.getBase().getRequestMethod().equalsIgnoreCase("GET")) {
                String url = apiRequest.getBase().getRequestUrl();
                List<KeyValueRequest> forms = apiRequest.getBody().getForms();
                if(CollectionUtils.isEmpty(forms)) {
                    continue;
                }
                StringBuffer sb = new StringBuffer();
                for(KeyValueRequest keyValueRequest : forms) {
                    sb.append(keyValueRequest.getKey());
                    sb.append("=");
                    sb.append(keyValueRequest.getValue());
                    sb.append("&");
                }
                sb.deleteCharAt(sb.length() - 1);
                apiRequest.getBase().setRequestUrl(appendUrlAndParams(url, sb.toString()));
            }
        }
    }

    private String appendUrlAndParams(String url, String params) {
        StringBuffer newUrl = new StringBuffer(url);
        if(StringUtils.isBlank(params)) {
            return newUrl.toString();
        }
        if(url.contains("?")) {
            if(!url.endsWith("&") && !url.endsWith("?")) {
                newUrl.append("&");
            }
            newUrl.append(params);
        } else {
            newUrl.append("?");
            newUrl.append(params);
        }
        return newUrl.toString();
    }
}
