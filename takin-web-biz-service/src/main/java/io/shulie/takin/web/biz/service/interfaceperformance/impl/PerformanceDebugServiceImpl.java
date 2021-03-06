package io.shulie.takin.web.biz.service.interfaceperformance.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.common.collect.Maps;
import com.pamirs.pradar.log.parser.utils.ResultCodeUtils;
import io.shulie.takin.cloud.common.utils.Md5Util;
import io.shulie.takin.utils.json.JsonHelper;
import io.shulie.takin.web.biz.pojo.request.interfaceperformance.*;
import io.shulie.takin.web.biz.service.interfaceperformance.PerformanceDebugService;
import io.shulie.takin.web.biz.service.interfaceperformance.PerformanceParamService;
import io.shulie.takin.web.biz.service.interfaceperformance.PerformanceResultService;
import io.shulie.takin.web.common.enums.interfaceperformance.PerformanceDebugErrorEnum;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.common.util.FileUtils;
import io.shulie.takin.web.common.util.MD5Tool;
import io.shulie.takin.web.common.util.RedisClientUtil;
import io.shulie.takin.web.data.dao.interfaceperformance.PerformanceConfigDAO;
import io.shulie.takin.web.data.mapper.mysql.InterfacePerformanceConfigMapper;
import io.shulie.takin.web.data.model.mysql.InterfacePerformanceConfigEntity;
import io.shulie.takin.web.data.result.filemanage.FileManageResponse;
import io.shulie.takin.web.ext.util.WebPluginUtils;
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
 * @date 2022/5/19 1:46 ??????
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
     * ??????????????????
     *
     * @param request
     */
    @Override
    public String debug(PerformanceDebugRequest request) {
        if (request.getId() == null) {
            throw new TakinWebException(TakinWebExceptionEnum.INTERFACE_PERFORMANCE_QUERY_ERROR, "?????????Id??????");
        }
        // 1????????????????????????????????????
        InterfacePerformanceConfigEntity queryEntity = interfacePerformanceConfigMapper.selectById(request.getId());
        if (queryEntity == null) {
            throw new TakinWebException(TakinWebExceptionEnum.INTERFACE_PERFORMANCE_QUERY_ERROR, "??????????????????????????????");
        }
        // ????????????????????????
        if (queryEntity.getStatus() == 1) {
            throw new TakinWebException(TakinWebExceptionEnum.INTERFACE_PERFORMANCE_DEBUG_ERROR, "?????????????????????");
        }
        // 2????????????????????????????????????
        PerformanceParamDetailRequest detailRequest = new PerformanceParamDetailRequest();
        detailRequest.setConfigId(request.getId());
        PerformanceParamDetailResponse detailResponse = performanceParamService.detail(detailRequest);
        // 3?????????????????????
        Map<String, Map<String, List<Object>>> fileIdDataMap = buildFileIdDataMap(request, detailResponse);

        InterfacePerformanceConfigEntity updateEntity = new InterfacePerformanceConfigEntity();
        updateEntity.setStatus(1);
        updateEntity.setId(queryEntity.getId());
        updateEntity.setGmtModified(new Date());
        // ????????????????????????
        performanceConfigDAO.updateById(updateEntity);

        // ???????????????????????????ID,?????????????????????,config???Id
        String uuId = UUID.randomUUID().toString();
        request.setResultId(uuId);
        // 5???????????????
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
     * ??????????????????,???????????????
     */
    @Override
    public String simple_debug(PerformanceDebugRequest request) {
        // 1????????????????????????????????????
        InterfacePerformanceConfigEntity queryEntity = PerformanceConvert.convertConfigDebugEntity(request);
        queryEntity.setId(request.getId());
        // 2????????????????????????????????????
        PerformanceParamDetailResponse detailResponse = new PerformanceParamDetailResponse();
        if (request.getId() != null) {
            // 2????????????????????????????????????
            PerformanceParamDetailRequest detailRequest = new PerformanceParamDetailRequest();
            detailRequest.setConfigId(request.getId());
            detailResponse = performanceParamService.detail(detailRequest);
        }
        // ???????????????????????????ID,?????????????????????,config???Id
        String uuId = UUID.randomUUID().toString();
        request.setResultId(uuId);

        // 3?????????????????????
        Map<String, Map<String, List<Object>>> fileIdDataMap = buildFileIdDataMap(request, detailResponse);
        // 4???????????????
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
     * ????????????????????????
     */
    private Map<String, Map<String, List<Object>>> buildFileIdDataMap(PerformanceDebugRequest request, PerformanceParamDetailResponse detailResponse) {
        // ??????Id?????????????????????
        Map<String, Map<String, List<Object>>> fileIdDataMap = Maps.newHashMap();
        if (detailResponse == null) {
            return fileIdDataMap;
        }
        List<PerformanceParamRequest> paramList = detailResponse.getParamList();
        // ??????????????????Map
        Map<String, List<Object>> fileData = Maps.newHashMap();
        // ???????????? Map,key = paramName
        if (CollectionUtils.isNotEmpty(paramList)) {
            // ?????????????????????
            List<PerformanceParamRequest> fileParamList = paramList.stream().filter(param -> param.getType() == 2).collect(Collectors.toList());
            if (CollectionUtils.isNotEmpty(fileParamList)) {
                // ??????????????????????????????????????????????????????????????????
                List<FileManageResponse> fileManageResponseList = detailResponse.getFileManageResponseList();
                // ???????????????????????????
                Map<String, List<FileManageResponse>> fileMap = fileManageResponseList.stream().
                        collect(Collectors.groupingBy(c -> String.valueOf(c.getId())));

                for (int idx = 0; idx < fileParamList.size(); idx++) {
                    // ????????????Id
                    Long fileId = fileParamList.get(idx).getFileId();
                    FileManageResponse tmpFileResponse = fileMap.get(String.valueOf(fileId)).get(0);
                    // ??????????????????
                    String path = tmpFileResponse.getUploadPath();
                    if (fileIdDataMap.containsKey(String.valueOf(fileId))) {
                        continue;
                    }
                    fileData = FileUtils.readAll(path);
                    // ?????????
                    fileIdDataMap.put(String.valueOf(fileId), fileData);
                    // ??????????????????????????????
                    Long maxCount = request.getRelateFileMaxCount() == null ? 0 : request.getRelateFileMaxCount();
                    // ??????????????????
                    Long fileCount = 0L;
                    // ?????????????????????????????????
                    for (Map.Entry<String, List<Object>> entry : fileData.entrySet()) {
                        fileCount = fileCount > entry.getValue().size() ? fileCount : entry.getValue().size();
                    }
                    // ?????????????????????????????????
                    request.setRelateFileMaxCount(maxCount > fileCount ? maxCount : fileCount);
                }
            }
        }
        return fileIdDataMap;
    }

    /**
     * ????????????
     */
    private void processRequest(Map<String, Map<String, List<Object>>> fileIdDataMap,
                                PerformanceDebugRequest request,
                                InterfacePerformanceConfigEntity configEntity,
                                PerformanceParamDetailResponse detailResponse,
                                boolean isSimpleDebug) {
        // ??????????????????????????????,???????????????????????????
        Long requestCount = request.getRequestCount();
        Long relateFileMaxCount = request.getRelateFileMaxCount();
        if (relateFileMaxCount > 0) {
            // ??????????????????????????????,??????????????????
            requestCount = requestCount > relateFileMaxCount ? relateFileMaxCount : requestCount;
        }
        try {
            // ????????????????????????????????????????????????????????????,??????????????????????????????????????????
            redisClientUtil.setString(performanceDebugUtil.formatResultKey(request.getResultId()), "1", 120, TimeUnit.SECONDS);

            ContentTypeVO contentTypeVO = Optional.ofNullable(JsonHelper.json2Bean(
                    configEntity.getContentType(), ContentTypeVO.class)).orElse(new ContentTypeVO());
            // ??????restTemplate
            RestTemplate restTemplate = performanceDebugUtil.createResultTemplate(
                    configEntity.getIsRedirect(),
                    configEntity.getTimeout(),
                    contentTypeVO);
            for (int idx = 0; idx < requestCount; idx++) {
                // ??????url
                String requestUrl = configEntity.getRequestUrl();
                requestUrl = performanceDebugUtil.generateBasicResult(fileIdDataMap, requestUrl, idx, detailResponse);
                configEntity.setRequestUrl(requestUrl);
                // ??????header
                String header = configEntity.getHeaders();
                header = performanceDebugUtil.generateBasicResult(fileIdDataMap, header, idx, detailResponse);
                configEntity.setHeaders(header);
                // ????????????
                String body = configEntity.getBody();
                body = performanceDebugUtil.generateBasicResult(fileIdDataMap, body, idx, detailResponse);
                configEntity.setBody(body);

                // 1???????????????
                PerformanceResultCreateInput insertResult = new PerformanceResultCreateInput();
                insertResult.setConfigId(configEntity.getId());
                insertResult.setRequestUrl(configEntity.getRequestUrl());
                insertResult.setHttpMethod(configEntity.getHttpMethod());
                try {
                    HttpEntity<?> requeryEntity;
                    ResponseEntity responseEntity;
                    // ?????????
                    HttpHeaders headers = performanceDebugUtil.buildHeader(
                            configEntity.getHeaders(),
                            configEntity.getCookies(),
                            contentTypeVO);
                    requeryEntity = new HttpEntity<>(configEntity.getBody(), headers);
                    if (HttpMethod.GET.name().equals(configEntity.getHttpMethod().toUpperCase())) {
                        // ??????????????????,body???Header
                        insertResult.setRequest(JsonHelper.bean2Json(requeryEntity));
                        responseEntity = restTemplate.exchange(configEntity.getRequestUrl(),
                                HttpMethod.GET, requeryEntity, String.class);
                    } else {
                        // ??????????????????,body???Header
                        insertResult.setRequest(JsonHelper.bean2Json(requeryEntity));
                        responseEntity = restTemplate.exchange(configEntity.getRequestUrl(),
                                Objects.requireNonNull(HttpMethod.resolve(configEntity.getHttpMethod().toUpperCase())),
                                requeryEntity, String.class);
                    }
                    // ????????????
                    if (!ResultCodeUtils.isOk(String.valueOf(responseEntity.getStatusCode().value()))) {
                        refreshDebugErrorMessage(PerformanceDebugErrorEnum.REQUEST_FAILED, insertResult, responseEntity.getStatusCode());
                    }
                    // ???????????????
                    insertResult.setStatus(responseEntity.getStatusCodeValue());
                    insertResult.setResponse(JsonHelper.bean2Json(responseEntity));
                } catch (Exception e) {
                    log.error("???????????? --> ????????????: {}", e.getMessage(), e);
                    insertResult.setStatus(400);
                    refreshDebugErrorMessage(PerformanceDebugErrorEnum.REQUEST_FAILED, insertResult, e.getLocalizedMessage());
                }
                insertResult.setResultId(request.getResultId());
                // ??????????????????
                performanceResultService.add(insertResult);
            }
        } catch (Throwable e) {
            log.error("???????????????????????????{}", ExceptionUtils.getStackTrace(e));
        } finally {
            redisClientUtil.del(performanceDebugUtil.formatResultKey(request.getResultId()));
        }
        // ??????????????????????????????????????????
        if (!isSimpleDebug) {
            // ?????????????????????
            InterfacePerformanceConfigEntity updateEntity = new InterfacePerformanceConfigEntity();
            updateEntity.setStatus(0);
            updateEntity.setId(request.getId());
            updateEntity.setGmtModified(new Date());
            // ?????????????????????
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
