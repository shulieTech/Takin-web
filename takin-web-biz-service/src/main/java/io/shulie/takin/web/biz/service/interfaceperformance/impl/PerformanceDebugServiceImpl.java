package io.shulie.takin.web.biz.service.interfaceperformance.impl;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Maps;
import com.pamirs.pradar.log.parser.utils.ResultCodeUtils;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.jmeter.adapter.JmeterFunctionAdapter;
import io.shulie.takin.utils.json.JsonHelper;
import io.shulie.takin.web.biz.pojo.request.interfaceperformance.*;
import io.shulie.takin.web.biz.pojo.request.scriptmanage.PageScriptDebugRequestRequest;
import io.shulie.takin.web.biz.pojo.request.scriptmanage.ScriptDebugDoDebugRequest;
import io.shulie.takin.web.biz.pojo.response.linkmanage.BusinessFlowDetailResponse;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.ScriptDebugDetailResponse;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.ScriptDebugRequestListResponse;
import io.shulie.takin.web.biz.pojo.response.scriptmanage.ScriptDebugResponse;
import io.shulie.takin.web.biz.service.interfaceperformance.PerformanceConfigService;
import io.shulie.takin.web.biz.service.interfaceperformance.PerformanceDebugService;
import io.shulie.takin.web.biz.service.interfaceperformance.PerformanceParamService;
import io.shulie.takin.web.biz.service.interfaceperformance.PerformanceResultService;
import io.shulie.takin.web.biz.service.scene.SceneService;
import io.shulie.takin.web.biz.service.scriptmanage.ScriptDebugService;
import io.shulie.takin.web.common.enums.interfaceperformance.PerformanceDebugErrorEnum;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.common.util.FileUtils;
import io.shulie.takin.web.common.util.RedisClientUtil;
import io.shulie.takin.web.data.dao.interfaceperformance.PerformanceConfigDAO;
import io.shulie.takin.web.data.dao.interfaceperformance.PerformanceRelateshipDAO;
import io.shulie.takin.web.data.mapper.mysql.InterfacePerformanceConfigMapper;
import io.shulie.takin.web.data.model.mysql.InterfacePerformanceConfigEntity;
import io.shulie.takin.web.data.model.mysql.InterfacePerformanceConfigSceneRelateShipEntity;
import io.shulie.takin.web.data.result.filemanage.FileManageResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/5/19 1:46 下午
 */
@Slf4j
@Service
public class PerformanceDebugServiceImpl implements PerformanceDebugService {
    @Resource
    private PerformanceConfigDAO performanceConfigDAO;

    @Resource
    private PerformanceConfigService performanceConfigService;

    @Resource
    private InterfacePerformanceConfigMapper interfacePerformanceConfigMapper;

    @Resource
    private PerformanceParamService performanceParamService;

    @Resource
    private PerformanceDebugUtil performanceDebugUtil;

    @Resource
    private ThreadPoolExecutor performanceDebugThreadPool;

    @Resource
    private PerformanceResultService performanceResultService;

    @Resource
    private RedisClientUtil redisClientUtil;

    @Resource
    private PerformanceRelateshipDAO performanceRelateshipDAO;

    @Resource
    private SceneService sceneService;

    @Autowired
    private ScriptDebugService scriptDebugService;

    // 调试走web还是压力引擎(1-web,2-压力引擎)
    @Value("${takin.interface.debug.type:1}")
    private Integer takin_debug_type;

    /**
     * 开启调试功能
     *
     * @param request
     */
    @Override
    public String debug(PerformanceDebugRequest request) {
        if (request.getId() == null) {
            throw new TakinWebException(TakinWebExceptionEnum.INTERFACE_PERFORMANCE_QUERY_ERROR, "未设置Id参数");
        }
        // 1、读取数据库中的调试数据
        InterfacePerformanceConfigEntity queryEntity = interfacePerformanceConfigMapper.selectById(request.getId());
        if (queryEntity == null) {
            throw new TakinWebException(TakinWebExceptionEnum.INTERFACE_PERFORMANCE_QUERY_ERROR, "未查询到当前配置信息");
        }
        // 调试中不可再操作
        if (queryEntity.getStatus() == 1) {
            throw new TakinWebException(TakinWebExceptionEnum.INTERFACE_PERFORMANCE_DEBUG_ERROR, "场景已在调试中");
        }
        // 2、读取文件关联的文件信息
        PerformanceParamDetailRequest detailRequest = new PerformanceParamDetailRequest();
        detailRequest.setConfigId(request.getId());
        PerformanceParamDetailResponse detailResponse = performanceParamService.detail(detailRequest);
        // 3、处理请求参数
        Map<String, Map<String, List<Object>>> fileIdDataMap = buildFileIdDataMap(request, detailResponse);

        InterfacePerformanceConfigEntity updateEntity = new InterfacePerformanceConfigEntity();
        updateEntity.setStatus(1);
        updateEntity.setId(queryEntity.getId());
        updateEntity.setGmtModified(new Date());
        // 更新状态为调试中
        performanceConfigDAO.updateById(updateEntity);

        // 生成一个临时的配置ID,给前端查询使用,config表Id
        String uuId = UUID.randomUUID().toString();
        request.setResultId(uuId);
        // 5、发起请求
        CompletableFuture.runAsync(() -> processRequest(
                        fileIdDataMap,
                        request,
                        queryEntity,
                        detailResponse,
                        false),
                performanceDebugThreadPool);
        return uuId;
    }

    /**
     * 简单调试使用,不保存数据
     */
    @Override
    public String simple_debug(PerformanceDebugRequest request) {
        // 1、读取数据库中的调试数据
        InterfacePerformanceConfigEntity queryEntity = PerformanceConvert.convertConfigDebugEntity(request);
        queryEntity.setId(request.getId());
        // 2、读取文件关联的文件信息
        PerformanceParamDetailResponse detailResponse = new PerformanceParamDetailResponse();
        if (request.getId() != null) {
            // 2、读取文件关联的文件信息
            PerformanceParamDetailRequest detailRequest = new PerformanceParamDetailRequest();
            detailRequest.setConfigId(request.getId());
            detailResponse = performanceParamService.detail(detailRequest);
        }
        // 生成一个临时的配置ID,给前端查询使用,config表Id
        String uuId = UUID.randomUUID().toString();
        request.setResultId(uuId);

        // 3、处理请求参数
        Map<String, Map<String, List<Object>>> fileIdDataMap = buildFileIdDataMap(request, detailResponse);
        // 4、发起请求
        PerformanceParamDetailResponse finalDetailResponse = detailResponse;
        CompletableFuture.runAsync(() -> processRequest(
                        fileIdDataMap,
                        request,
                        queryEntity,
                        finalDetailResponse,
                        true),
                performanceDebugThreadPool);
        return uuId;
    }

    /**
     * 调试，走脚本调试功能
     *
     * @param request
     * @return
     */
    @Override
    public String simple_debug_ext(PerformanceDebugRequest request) {
        // 找到业务流程
        Long configId = request.getId();
        String uuId = request.getResultId();
        try {
            InterfacePerformanceConfigEntity configEntity = interfacePerformanceConfigMapper.selectById(configId);
            InterfacePerformanceConfigSceneRelateShipEntity shipEntity = performanceRelateshipDAO.relationShipEntityById(configId);
            // 获取业务流程Id
            Long flowId = shipEntity.getFlowId();
            // 获取业务流程详情
            BusinessFlowDetailResponse detailResponse = sceneService.getBusinessFlowDetail(flowId);
            // 找到关联信息
            ScriptDebugDoDebugRequest debugDoDebugRequest = new ScriptDebugDoDebugRequest();
            debugDoDebugRequest.setRequestNum(request.getRequestCount());
            debugDoDebugRequest.setConcurrencyNum(1);
            debugDoDebugRequest.setScriptDeployId(detailResponse.getScriptDeployId());

            ScriptDebugResponse scriptDebugResponse = scriptDebugService.debug(debugDoDebugRequest);

            // 是否存在错误信息
            List<String> errorMessages = scriptDebugResponse.getErrorMessages();
            if (CollectionUtils.isNotEmpty(errorMessages)) {
                // 保存信息到结果表
                PerformanceResultCreateInput insertResult = new PerformanceResultCreateInput();
                insertResult.setConfigId(configEntity.getId());
                insertResult.setRequestUrl(configEntity.getRequestUrl());
                insertResult.setHttpMethod(configEntity.getHttpMethod());
                ContentTypeVO contentTypeVO = Optional.ofNullable(JsonHelper.json2Bean(
                        configEntity.getContentType(), ContentTypeVO.class)).orElse(new ContentTypeVO());
                HttpHeaders headers = performanceDebugUtil.buildHeader(
                        configEntity.getHeaders(),
                        configEntity.getCookies(),
                        contentTypeVO);
                HttpEntity<?> requeryEntity = new HttpEntity<>("", headers);
                insertResult.setRequest(JsonHelper.bean2Json(requeryEntity));

                ResponseEntity responseEntity = new ResponseEntity("", HttpStatus.BAD_REQUEST);
                insertResult.setResponse(JsonHelper.bean2Json(responseEntity));
                insertResult.setStatus(400);
                insertResult.setErrorMessage(JSON.toJSONString(errorMessages));
                insertResult.setResultId(request.getResultId());
                // 保存请求结果
                performanceResultService.add(insertResult);
            } else {
                // 这个结果获取比较慢,稍微等待时间长一点
                redisClientUtil.setString(performanceDebugUtil.formatResultKey(uuId), "1", 480, TimeUnit.SECONDS);
                // 异步转换下脚本调试结果和原来的takin压测结果
                CompletableFuture.runAsync(() -> convertDebugResult(
                        uuId,
                        scriptDebugResponse.getScriptDebugId(),
                        configEntity), performanceDebugThreadPool);
            }
        } catch (Throwable e) {
            log.error("调试异常" + ExceptionUtils.getStackTrace(e));
            // 异常了，把key失效掉
            redisClientUtil.delete(performanceDebugUtil.formatStratDebugKey(String.valueOf(configId)));
            throw new RuntimeException("调试异常,当前场景或保存失败,请重新保存后调试!!!");
        }
        return uuId;
    }

    /**
     * 需要判断走web调试还是脚本调试
     *
     * @param request
     * @return
     */
    @Override
    public String start(PerformanceDebugRequest request) {
        // 配置优先,开关,优先走web调试
        int debug_type = 1;
        // 判断下url,header,body里面是否存在函数,如果不存在函数的话,走web调试
        String patternStr = request.getRequestUrl() + "&" + request.getHeaders() + "&" + request.getBody();
        List<String> funPatternList = performanceDebugUtil.generateFunPattern(patternStr);
        if (CollectionUtils.isEmpty(funPatternList)) {
            debug_type = 1;
        } else {
            // 如果存在函数,判断函数是否支持，如果有一个不支持的话,走脚本调试
            if (takin_debug_type == 1) {
                for (int i = 0; i < funPatternList.size(); i++) {
                    String fun = funPatternList.get(i);
                    if (fun.contains("(")) {
                        // RandomString
                        fun = fun.substring(0, fun.indexOf("("));
                    }
                    fun = "__" + fun;
                    // 有一个不支持的话,所有的都不支持
                    if (!JmeterFunctionAdapter.getInstance().supportFunction(fun)) {
                        log.warn("当前函数不支持", fun);
                        debug_type = 2; // 走脚本调试功能
                        break;
                    }
                }
            } else if (takin_debug_type == 2) {
                debug_type = 2; // 走脚本调试
            } else {
                log.warn("UNKNOW_TYPE", takin_debug_type);
            }
        }
        if (debug_type == 1) {
            return this.simple_debug(request);
        } else {
            if (request.getId() == null) {
                //
                throw new RuntimeException("当前场景消息体存在Jmeter函数,请先保存再继续调试!");
            }
            // 生成一个临时的配置ID,给前端查询使用,config表Id
            String uuId = UUID.randomUUID().toString();
            request.setResultId(uuId);
            // 获取调试启动标识
            String startDebugKey = performanceDebugUtil.formatStratDebugKey(String.valueOf(request.getId()));
            String startDebugValue = redisClientUtil.getString(startDebugKey);
            if (StringUtils.isNotBlank(startDebugValue)) {
                throw new RuntimeException("当前场景存在未完成的调试任务，请等待调试结果输出！！！");
            }
            // 空的,开启调试
            redisClientUtil.setString(startDebugKey, "1", 120, TimeUnit.SECONDS);
            // 异步处理，这里校验逻辑比较多,处理比较慢
            CompletableFuture.runAsync(() -> this.simple_debug_ext(request), performanceDebugThreadPool);
            return uuId;
        }
    }

    public void convertDebugResult(String resultId,
                                   Long scriptDebugId,
                                   InterfacePerformanceConfigEntity configEntity) {
        try {
            ContentTypeVO contentTypeVO = Optional.ofNullable(JsonHelper.json2Bean(
                    configEntity.getContentType(), ContentTypeVO.class)).orElse(new ContentTypeVO());
            while (true) {
                // 获取脚本调试详情
                // 脚本调试记录状态, 0 未启动, 1 启动中,2 请求中, 3 请求结束, 4 调试成功, 5 调试失败"
                // "失败类型, 10 启动通知超时失败, 20 漏数失败, 30 非200检查失败, 后面会扩展"
                ScriptDebugDetailResponse detailResponse = scriptDebugService.getById(scriptDebugId);
                // 5结束，4是200毫秒后跳转
                if (detailResponse.getStatus() == 4 ||
                        (detailResponse.getStatus() == 5 && detailResponse.getFailedType() >= 20)) {
                    // 调试成功了,去读取请求信息
                    PageScriptDebugRequestRequest request = new PageScriptDebugRequestRequest();
                    request.setScriptDebugId(scriptDebugId);
                    request.setCurrent(0);
                    // 全部查出来
                    request.setPageSize(1000);
                    PagingList<ScriptDebugRequestListResponse> pageList = scriptDebugService.pageScriptDebugRequest(request);
                    if (pageList != null && !pageList.isEmpty()) {
                        List<ScriptDebugRequestListResponse> responseList = pageList.getList();
                        // 转换到调试结果表
                        for (int i = 0; i < responseList.size(); i++) {
                            ScriptDebugRequestListResponse response = responseList.get(i);
                            // 保存信息到结果表
                            PerformanceResultCreateInput insertResult = new PerformanceResultCreateInput();
                            insertResult.setConfigId(configEntity.getId());
                            insertResult.setRequestUrl(configEntity.getRequestUrl());
                            insertResult.setHttpMethod(configEntity.getHttpMethod());

                            // 请求头
                            HttpHeaders headers = performanceDebugUtil.buildHeader(
                                    configEntity.getHeaders(),
                                    configEntity.getCookies(),
                                    contentTypeVO);
                            insertResult.setResultId(resultId);
                            HttpEntity<?> requeryEntity = new HttpEntity<>(response.getRequestBody(), headers);
                            insertResult.setRequest(JsonHelper.bean2Json(requeryEntity));
                            // 0就是正常,1是失败
                            insertResult.setStatus(response.getResponseStatus() == 0 ? 200 : 500);
                            HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
                            if (response.getResponseStatus() == 0) {
                                httpStatus = HttpStatus.OK;
                            }
                            ResponseEntity responseEntity = new ResponseEntity(response.getResponseBody(), httpStatus);
                            insertResult.setResponse(JsonHelper.bean2Json(responseEntity));
                            // 保存请求结果
                            performanceResultService.add(insertResult);
                        }
                    }
                    // 结束循环
                    break;
                }
                // 内部异常,记录失败信息
                if (detailResponse.getStatus() == 5) {
                    // 保存信息到结果表
                    PerformanceResultCreateInput insertResult = new PerformanceResultCreateInput();
                    insertResult.setConfigId(configEntity.getId());
                    insertResult.setRequestUrl(configEntity.getRequestUrl());
                    insertResult.setHttpMethod(configEntity.getHttpMethod());

                    insertResult.setResultId(resultId);
                    HttpHeaders headers = performanceDebugUtil.buildHeader(
                            configEntity.getHeaders(),
                            configEntity.getCookies(),
                            contentTypeVO);
                    HttpEntity<?> requeryEntity = new HttpEntity<>("", headers);
                    insertResult.setRequest(JsonHelper.bean2Json(requeryEntity));
                    // 0就是正常,1是失败
                    insertResult.setStatus(400);
                    ResponseEntity responseEntity = new ResponseEntity(detailResponse.getRemark(), HttpStatus.INTERNAL_SERVER_ERROR);
                    insertResult.setResponse(JsonHelper.bean2Json(responseEntity));
                    // 保存请求结果
                    performanceResultService.add(insertResult);
                    // 结束循环
                    break;
                }
                // 2s循环一次
                TimeUnit.SECONDS.toSeconds(2);
            }
        } catch (Throwable e) {
            log.error("获取结果失败" + ExceptionUtils.getStackTrace(e));
        } finally {
            redisClientUtil.delete(performanceDebugUtil.formatResultKey(resultId));
            redisClientUtil.delete(performanceDebugUtil.formatStratDebugKey(String.valueOf(configEntity.getId())));
        }
    }

    /**
     * 构建文件请求参数
     */
    private Map<String, Map<String, List<Object>>> buildFileIdDataMap(PerformanceDebugRequest request, PerformanceParamDetailResponse detailResponse) {
        // 文件Id对应的文件数据
        Map<String, Map<String, List<Object>>> fileIdDataMap = Maps.newHashMap();
        if (detailResponse == null) {
            return fileIdDataMap;
        }
        List<PerformanceParamRequest> paramList = detailResponse.getParamList();
        // 文件列和数据Map
        Map<String, List<Object>> fileData = Maps.newHashMap();
        // 解析参数 Map,key = paramName
        if (CollectionUtils.isNotEmpty(paramList)) {
            // 过滤出文件参数
            List<PerformanceParamRequest> fileParamList = paramList.stream().filter(param -> param.getType() == 2).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(fileParamList)) {
                // 文件参数不为空的时候，获取文件路径，读取数据
                List<FileManageResponse> fileManageResponseList = detailResponse.getFileManageResponseList();
                // 获取所有的文件路径
                Map<String, List<FileManageResponse>> fileMap = fileManageResponseList.stream().
                        collect(Collectors.groupingBy(c -> String.valueOf(c.getId())));

                for (int idx = 0; idx < fileParamList.size(); idx++) {
                    // 获取文件Id
                    Long fileId = fileParamList.get(idx).getFileId();
                    FileManageResponse tmpFileResponse = fileMap.get(String.valueOf(fileId)).get(0);
                    // 获取文件路径
                    String path = tmpFileResponse.getUploadPath();
                    if (fileIdDataMap.containsKey(String.valueOf(fileId))) {
                        continue;
                    }
                    fileData = FileUtils.readAll(path);
                    // 设置值
                    fileIdDataMap.put(String.valueOf(fileId), fileData);
                    // 设置一个文件最大条数
                    Integer maxCount = request.getRelateFileMaxCount() == null ? 0 : request.getRelateFileMaxCount();
                    // 文件中的条数
                    Integer fileCount = 0;
                    // 找当前文件中的最大条数
                    for (Map.Entry<String, List<Object>> entry : fileData.entrySet()) {
                        fileCount = fileCount > entry.getValue().size() ? fileCount : entry.getValue().size();
                    }
                    // 设置一个文件的最大条数
                    request.setRelateFileMaxCount(maxCount > fileCount ? maxCount : fileCount);
                }
            }
        }
        return fileIdDataMap;
    }

    /**
     * 发起请求
     */
    private void processRequest(Map<String, Map<String, List<Object>>> fileIdDataMap,
                                PerformanceDebugRequest request,
                                InterfacePerformanceConfigEntity configEntity,
                                PerformanceParamDetailResponse detailResponse,
                                boolean isSimpleDebug) {
        // 获取请求文件最大条数,把所有文件数据跑完
        Integer requestCount = request.getRequestCount();
        Integer relateFileMaxCount = request.getRelateFileMaxCount();
        if (relateFileMaxCount > 0) {
            // 请求条数大于文件条数,则用请求条数
            requestCount = requestCount > relateFileMaxCount ? relateFileMaxCount : requestCount;
        }
        ContentTypeVO contentTypeVO = Optional.ofNullable(JsonHelper.json2Bean(
                configEntity.getContentType(), ContentTypeVO.class)).orElse(new ContentTypeVO());
        try {
            // 这里处理个状态标记，确认请求是否发送完成,获取结果的时候前端不需要轮训
            redisClientUtil.setString(performanceDebugUtil.formatResultKey(request.getResultId()), "1", 120, TimeUnit.SECONDS);
            // 构建restTemplate
            RestTemplate restTemplate = performanceDebugUtil.createResultTemplate(
                    configEntity.getIsRedirect(),
                    configEntity.getTimeout(),
                    contentTypeVO);

            // 临时变量
            String requestUrlStr = configEntity.getRequestUrl();
            String headerStr = configEntity.getHeaders();
            String bodyStr = configEntity.getBody();

            for (int idx = 0; idx < requestCount; idx++) {
                // 替换url
                String tmpRequestUrl = performanceDebugUtil.generateBasicResult(fileIdDataMap, requestUrlStr, idx, detailResponse);
                tmpRequestUrl = performanceDebugUtil.generateJmeterResult(tmpRequestUrl);
                configEntity.setRequestUrl(tmpRequestUrl);

                // 替换header
                String tmpHeader = performanceDebugUtil.generateBasicResult(fileIdDataMap, headerStr, idx, detailResponse);
                tmpHeader = performanceDebugUtil.generateJmeterResult(tmpHeader);
                configEntity.setHeaders(tmpHeader);

                // 替换参数
                String tmpBody = performanceDebugUtil.generateBasicResult(fileIdDataMap, bodyStr, idx, detailResponse);
                // body中的函数替换
                tmpBody = performanceDebugUtil.generateJmeterResult(tmpBody);
                configEntity.setBody(tmpBody);

                // 1、请求参数
                PerformanceResultCreateInput insertResult = new PerformanceResultCreateInput();
                insertResult.setConfigId(configEntity.getId());
                insertResult.setRequestUrl(configEntity.getRequestUrl());
                insertResult.setHttpMethod(configEntity.getHttpMethod());
                try {
                    HttpEntity<?> requeryEntity;
                    ResponseEntity responseEntity;
                    // 请求头
                    HttpHeaders headers = performanceDebugUtil.buildHeader(
                            configEntity.getHeaders(),
                            configEntity.getCookies(),
                            contentTypeVO);
                    requeryEntity = new HttpEntity<>(configEntity.getBody(), headers);
                    if (HttpMethod.GET.name().equals(configEntity.getHttpMethod().toUpperCase())) {
                        // 设置请求信息,body和Header
                        insertResult.setRequest(JsonHelper.bean2Json(requeryEntity));
                        responseEntity = restTemplate.exchange(configEntity.getRequestUrl(),
                                HttpMethod.GET, requeryEntity, String.class);
                    } else {
                        // 设置请求信息,body和Header
                        insertResult.setRequest(JsonHelper.bean2Json(requeryEntity));
                        responseEntity = restTemplate.exchange(configEntity.getRequestUrl(),
                                Objects.requireNonNull(HttpMethod.resolve(configEntity.getHttpMethod().toUpperCase())),
                                requeryEntity, String.class);
                    }
                    // 错误返回
                    if (!ResultCodeUtils.isOk(String.valueOf(responseEntity.getStatusCode().value()))) {
                        refreshDebugErrorMessage(PerformanceDebugErrorEnum.REQUEST_FAILED, insertResult, responseEntity.getStatusCode());
                    }
                    // 设置状态码
                    insertResult.setStatus(responseEntity.getStatusCodeValue());
                    insertResult.setResponse(JsonHelper.bean2Json(responseEntity));
                } catch (Exception e) {
                    log.error("调试错误 --> 错误信息: {}", e.getMessage(), e);
                    insertResult.setStatus(400);
                    refreshDebugErrorMessage(PerformanceDebugErrorEnum.REQUEST_FAILED, insertResult, e.getLocalizedMessage());
                }
                insertResult.setResultId(request.getResultId());
                // 保存请求结果
                performanceResultService.add(insertResult);
            }
        } catch (Throwable e) {
            // 1、请求参数
            PerformanceResultCreateInput insertResult = new PerformanceResultCreateInput();
            insertResult.setConfigId(configEntity.getId());
            insertResult.setRequestUrl(configEntity.getRequestUrl());
            insertResult.setHttpMethod(configEntity.getHttpMethod());
            ResponseEntity responseEntity = new ResponseEntity(HttpStatus.BAD_REQUEST);
            // 请求头
            HttpHeaders headers = performanceDebugUtil.buildHeader(
                    configEntity.getHeaders(),
                    configEntity.getCookies(),
                    contentTypeVO);
            HttpEntity<?> requeryEntity = new HttpEntity<>(configEntity.getBody(), headers);
            insertResult.setRequest(JsonHelper.bean2Json(requeryEntity));
            insertResult.setResponse(JsonHelper.bean2Json(responseEntity));
            insertResult.setStatus(400);
            refreshDebugErrorMessage(PerformanceDebugErrorEnum.REQUEST_FAILED, insertResult, e.getLocalizedMessage());
            insertResult.setResultId(request.getResultId());
            // 保存请求结果
            performanceResultService.add(insertResult);
            log.error("单接口压测场景异常{}", ExceptionUtils.getStackTrace(e));
        } finally {
            redisClientUtil.del(performanceDebugUtil.formatResultKey(request.getResultId()));
        }
        // 非简单调试，要更新配置表数据
        if (!isSimpleDebug) {
            // 更新场景已完成
            InterfacePerformanceConfigEntity updateEntity = new InterfacePerformanceConfigEntity();
            updateEntity.setStatus(0);
            updateEntity.setId(request.getId());
            updateEntity.setGmtModified(new Date());
            // 更新状态为完成
            performanceConfigDAO.updateById(updateEntity);
        }
    }

    private void refreshDebugErrorMessage(PerformanceDebugErrorEnum errorEnum, PerformanceResultCreateInput insertResult,
                                          Object message) {
        Map<String, String> map = Maps.newHashMap();
        String error = String.format(errorEnum.getErrorName(), message);
        if (StringUtils.isNotBlank(insertResult.getErrorMessage())) {
            map = JsonHelper.string2Obj(insertResult.getErrorMessage(), new TypeReference<Map<String, String>>() {
            });
        }
        map.compute(errorEnum.getType(), (k, v) -> v == null ? error : (v + "," + error));
        insertResult.setErrorMessage(JsonHelper.bean2Json(map));
    }
}
