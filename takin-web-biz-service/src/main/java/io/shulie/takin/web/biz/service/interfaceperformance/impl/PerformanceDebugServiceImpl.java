package io.shulie.takin.web.biz.service.interfaceperformance.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Maps;
import com.pamirs.pradar.log.parser.utils.ResultCodeUtils;
import io.shulie.takin.utils.json.JsonHelper;
import io.shulie.takin.web.biz.pojo.request.interfaceperformance.*;
import io.shulie.takin.web.biz.service.interfaceperformance.PerformanceDebugService;
import io.shulie.takin.web.biz.service.interfaceperformance.PerformanceParamService;
import io.shulie.takin.web.biz.service.interfaceperformance.PerformanceResultService;
import io.shulie.takin.web.common.enums.interfaceperformance.PerformanceDebugErrorEnum;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.common.util.FileUtils;
import io.shulie.takin.web.common.util.RedisClientUtil;
import io.shulie.takin.web.data.dao.interfaceperformance.PerformanceConfigDAO;
import io.shulie.takin.web.data.mapper.mysql.InterfacePerformanceConfigMapper;
import io.shulie.takin.web.data.model.mysql.InterfacePerformanceConfigEntity;
import io.shulie.takin.web.data.result.filemanage.FileManageResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
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
                    Long maxCount = request.getRelateFileMaxCount() == null ? 0 : request.getRelateFileMaxCount();

                    // 设置一个文件的最大条数
                    request.setRelateFileMaxCount(maxCount > fileData.values().size() ? maxCount : fileData.values().size());
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
        Long requestCount = request.getRequestCount();
        Long relateFileMaxCount = request.getRelateFileMaxCount();
        try {
            // 这里处理个状态标记，确认请求是否发送完成,获取结果的时候前端不需要轮训
            redisClientUtil.setString(performanceDebugUtil.formatResultKey(request.getResultId()), "1", 5000, TimeUnit.SECONDS);

            ContentTypeVO contentTypeVO = JsonHelper.json2Bean(configEntity.getContentType(), ContentTypeVO.class);
            // 构建restTemplate
            RestTemplate restTemplate = performanceDebugUtil.createResultTemplate(
                    configEntity.getIsRedirect(),
                    configEntity.getTimeout(),
                    contentTypeVO);
            for (int idx = 0; idx < requestCount; idx++) {
                // 替换url
                String requestUrl = configEntity.getRequestUrl();
                requestUrl = performanceDebugUtil.generateBasicResult(fileIdDataMap, requestUrl, idx, detailResponse);
                configEntity.setRequestUrl(requestUrl);
                // 替换header
                String header = configEntity.getHeaders();
                header = performanceDebugUtil.generateBasicResult(fileIdDataMap, header, idx, detailResponse);
                configEntity.setHeaders(header);
                // 替换参数
                String body = configEntity.getBody();
                body = performanceDebugUtil.generateBasicResult(fileIdDataMap, body, idx, detailResponse);
                configEntity.setBody(body);

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
                    refreshDebugErrorMessage(PerformanceDebugErrorEnum.REQUEST_FAILED, insertResult, e.getLocalizedMessage());
                }

                insertResult.setResultId(request.getResultId());
                // 保存请求结果
                performanceResultService.add(insertResult);
            }
        } catch (Throwable e) {
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
        String error = String.format(errorEnum.getErrorName(), message.toString());
        if (StringUtils.isNotBlank(insertResult.getErrorMessage())) {
            map = JsonHelper.string2Obj(insertResult.getErrorMessage(), new TypeReference<Map<String, String>>() {
            });
        }
        map.compute(errorEnum.getType(), (k, v) -> v == null ? error : (v + "," + error));
        insertResult.setErrorMessage(JsonHelper.bean2Json(map));
    }


}
