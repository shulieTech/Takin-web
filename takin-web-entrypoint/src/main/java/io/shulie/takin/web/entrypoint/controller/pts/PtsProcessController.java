package io.shulie.takin.web.entrypoint.controller.pts;

import com.alibaba.fastjson.JSON;
import com.pamirs.takin.entity.domain.entity.TBaseConfig;
import io.shulie.takin.cloud.common.enums.pts.PtsThreadGroupTypeEnum;
import io.shulie.takin.cloud.ext.content.enums.SamplerTypeEnum;
import io.shulie.takin.common.beans.annotation.ActionTypeEnum;
import io.shulie.takin.common.beans.annotation.AuthVerification;
import io.shulie.takin.common.beans.annotation.ModuleDef;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.jmeter.adapter.JmeterFunctionAdapter;
import io.shulie.takin.web.biz.constant.BizOpConstants;
import io.shulie.takin.web.biz.pojo.request.pts.*;
import io.shulie.takin.web.biz.pojo.response.linkmanage.BusinessFlowDetailResponse;
import io.shulie.takin.web.biz.pojo.response.pts.*;
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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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

    @Resource
    @Qualifier("redisTemplate")
    private RedisTemplate redisTemplate;

    private static final String JMETER_FUNCTION = "JMETER_FUNCTION";

    private static final String JMETER_JAVA_REQUEST = "JMETER_JAVE_REQUEST";

    private final String PTS_SAVE_KEY ="TAKIN:web:pts.processSave:%s";

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
    public ResponseResult<BusinessFlowDetailResponse> saveProcess(@Valid @RequestBody PtsSceneRequest request) {
        Long requestId = request.getId();
        String key = null;
        if(requestId != null) {
            key = String.format(PTS_SAVE_KEY, requestId);
        }
        try {
            if(key != null) {
                if(redisTemplate.hasKey(key)) {
                    return ResponseResult.fail("保存失败：请勿重复提交", "");
                }
                redisTemplate.opsForValue().set(key, "1", 15, TimeUnit.SECONDS);
            }
            //预判断
            List<PtsCsvRequest> csvList = request.getDataSource().getCsvs();
            if (CollectionUtils.isNotEmpty(csvList)) {
                for (PtsCsvRequest csvRequest : csvList) {
                    csvRequest.setFileName(StringUtils.trimToEmpty(csvRequest.getFileName()));
                    if (StringUtils.contains(csvRequest.getParams(), "，")) {
                        return ResponseResult.fail("0", "保存失败：请使用西文逗号来分割CSV数据的变量名", "");
                    }
                    if (!StringUtils.endsWith(csvRequest.getFileName(), ".txt") &&
                            !StringUtils.endsWith(csvRequest.getFileName(), ".csv")) {
                        return ResponseResult.fail("0", "保存失败：CSV数据的文件名必须以.txt或.csv结尾", "");
                    }
                }
            }
            /**
             * 线程组setUp，tearDown类型最多1个
             */
            if (CollectionUtils.isEmpty(request.getLinks())) {
                return ResponseResult.fail("0", "保存失败：链路至少出现1次", "");
            }
            List<PtsLinkRequest> setUpList = request.getLinks().stream().filter(data -> data.getLinkType().equals(PtsThreadGroupTypeEnum.SETUP.getType())).collect(Collectors.toList());
            if (setUpList.size() > 1) {
                return ResponseResult.fail("0", "保存失败：setUp链路最多出现1次", "");
            }
            List<PtsLinkRequest> tearDownList = request.getLinks().stream().filter(data -> data.getLinkType().equals(PtsThreadGroupTypeEnum.TEARDOWN.getType())).collect(Collectors.toList());
            if (tearDownList.size() > 1) {
                return ResponseResult.fail("0", "保存失败：tearDown链路最多出现1次", "");
            }
            List<PtsLinkRequest> nullLinkList = request.getLinks().stream().filter(data -> StringUtils.isBlank(data.getLinkName())).collect(Collectors.toList());
            if (nullLinkList.size() > 0) {
                return ResponseResult.fail("0", "保存失败：链路名称不能为空", "");
            }
            List<PtsApiRequest> apiList = new ArrayList<>();
            request.getLinks().forEach(data -> apiList.addAll(data.getApis()));
            if (CollectionUtils.isEmpty(apiList)) {
                return ResponseResult.fail("0", "保存失败：API接口至少出现1次", "");
            }
            List<PtsApiRequest> nullApiList = apiList.stream().filter(data -> StringUtils.isBlank(data.getApiName())).collect(Collectors.toList());
            if (nullApiList.size() > 0) {
                return ResponseResult.fail("0", "保存失败：API接口名称不能为空", "");
            }
            /**
             * HTTP请求校验，HTTP-URL
             */
            for (PtsApiRequest apiRequest : apiList) {
                apiRequest.getBase().setRequestUrl(StringUtils.replace(apiRequest.getBase().getRequestUrl(), " ", ""));
                if (apiRequest.getApiType().equals(SamplerTypeEnum.HTTP.getType())
                        && (StringUtils.isNotBlank(apiRequest.getBase().getRequestUrl())
                        && !apiRequest.getBase().getRequestUrl().startsWith("http://")
                        && !apiRequest.getBase().getRequestUrl().startsWith("https://"))) {
                    return ResponseResult.fail("0", "保存失败：URL格式不正确,接口名称=" + apiRequest.getApiName(), "");
                }
            }

            return ResponseResult.success(ptsProcessService.saveProcess(request));
        } catch (Throwable e) {
            return ResponseResult.fail("保存失败：" + e.getMessage(), "");
        } finally {
            if(key != null) {
                //不能立即删除，保存时有异步操作，会引发问题-保存失败：创建部署脚本失败！请查看takin-web日志
                redisTemplate.opsForValue().set(key, "1", 3, TimeUnit.SECONDS);
            }
        }
    }

    @ApiOperation("PTS业务流程详情")
    @GetMapping("/process/detail")
    @AuthVerification(
            moduleCode = BizOpConstants.ModuleCode.BUSINESS_ACTIVITY,
            needAuth = ActionTypeEnum.QUERY
    )
    public ResponseResult detailActivity(@RequestParam(name = "id") Long id) {
        String key = String.format(PTS_SAVE_KEY, id);
        if(redisTemplate.hasKey(key)) {
            return ResponseResult.fail("业务流程保存中...，请稍后再试", "");
        }
        JmeterJavaRequestResponse javaConfig = getJavaRequestConfig("IB2");
        PtsSceneResponse sceneResponse = ptsProcessService.detailProcess(id);
        //处理Get请求，拼参数到url里
        dealLinks((sceneResponse.getPreLink()), javaConfig);
        for(PtsLinkRequest linkRequest : sceneResponse.getLinks()) {
            dealLinks(linkRequest, javaConfig);
        }
        return ResponseResult.success(sceneResponse);
    }

    @ApiOperation("发起调试")
    @PostMapping("/process/debug")
    public ResponseResult debugScene(@Valid @RequestBody IdRequest request) {
        return ptsProcessService.debugProcess(request.getId());
    }

    @ApiOperation("API调试列表|明细")
    @GetMapping("/process/debug/record/list")
    public ResponseResult<PtsDebugResponse> debugRecordList(@RequestParam(name = "id") Long id) {
        PtsDebugResponse debugResponse = ptsProcessService.getDebugRecord(id);
        if(debugResponse.getHasException()) {
            return ResponseResult.fail("0", debugResponse.getExceptionMessage(), "");
        } else {
            return ResponseResult.success(debugResponse);
        }
    }

    @ApiOperation("API调试日志")
    @GetMapping("/process/debug/log")
    public ResponseResult debugRecordLog(@RequestParam(name = "id") Long id) {
        return ResponseResult.success(ptsProcessService.getDebugLog(id));
    }

    @ApiOperation("JavaRequest详情")
    @GetMapping("/javaRequest/detail")
    public JmeterJavaRequestResponse getJavaRequestByType(@RequestParam(name = "javaType") String javaType) {
        return getJavaRequestConfig(javaType);
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

    private JmeterJavaRequestResponse getJavaRequestConfig(String javaType) {
        TBaseConfig baseConfig = baseConfigService.queryByConfigCode(JMETER_JAVA_REQUEST);
        if(baseConfig == null) {
            return new JmeterJavaRequestResponse();
        }
        List<JmeterJavaRequestResponse> responseList = JSON.parseArray(baseConfig.getConfigValue(), JmeterJavaRequestResponse.class);
        JmeterJavaRequestResponse requestResponse = responseList.stream().filter(data -> data.getJavaType().equals(javaType)).findFirst().orElse(new JmeterJavaRequestResponse());
        List<JmeterJavaRequestParamsResponse> params = requestResponse.getParams();
        for(int i = 0; i < params.size(); i++) {
            params.get(i).setSortNum(i + 1);
        }
        return requestResponse;
    }

    private void dealLinks(PtsLinkRequest linkRequest, JmeterJavaRequestResponse javaConfig) {
        for(PtsApiRequest apiRequest : linkRequest.getApis()) {
            if(apiRequest.getApiType().equals(SamplerTypeEnum.HTTP.getType())) {
                if (apiRequest.getBase().getRequestMethod().equalsIgnoreCase("GET")) {
                    String url = apiRequest.getBase().getRequestUrl();
                    List<KeyValueRequest> forms = apiRequest.getBody().getForms();
                    if (CollectionUtils.isEmpty(forms)) {
                        continue;
                    }
                    StringBuffer sb = new StringBuffer();
                    for (KeyValueRequest keyValueRequest : forms) {
                        sb.append(keyValueRequest.getKey());
                        sb.append("=");
                        sb.append(keyValueRequest.getValue());
                        sb.append("&");
                    }
                    sb.deleteCharAt(sb.length() - 1);
                    apiRequest.getBase().setRequestUrl(appendUrlAndParams(url, sb.toString()));
                }
            } else if (apiRequest.getApiType().equals(SamplerTypeEnum.JAVA.getType())) {
                Map<String, JmeterJavaRequestParamsResponse> paramMap = new HashMap<>();
                javaConfig.getParams().stream().forEach(data -> {
                    paramMap.put(data.getParamName(), data);
                });
                if(CollectionUtils.isNotEmpty(apiRequest.getParam().getParams())) {
                    apiRequest.getParam().getParams().stream().forEach(param -> {
                        if (paramMap.containsKey(param.getParamName())) {
                            param.setAllowEdit(false);
                            param.setParamCNDesc(paramMap.get(param.getParamName()).getParamCNDesc());
                            param.setParamType(paramMap.get(param.getParamName()).getParamType());
                            param.setRequire(paramMap.get(param.getParamName()).getRequire());
                            param.setSortNum(paramMap.get(param.getParamName()).getSortNum());
                        } else {
                            param.setSortNum(99999);
                        }
                    });
                    apiRequest.getParam().getParams().sort((o1, o2) -> {
                        if(o1.getSortNum() < o2.getSortNum()) {
                            return -1;
                        } else if (o1.getSortNum() > o2.getSortNum()) {
                            return 1;
                        } else {
                            return o1.getParamName().compareTo(o2.getParamName());
                        }
                    });
                }
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
