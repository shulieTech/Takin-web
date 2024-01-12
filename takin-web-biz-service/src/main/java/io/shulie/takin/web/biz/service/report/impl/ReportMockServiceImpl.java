package io.shulie.takin.web.biz.service.report.impl;

import com.alibaba.fastjson.JSON;
import com.pamirs.takin.common.constant.Constants;
import com.pamirs.takin.common.util.ListHelper;
import io.shulie.takin.web.biz.constant.WebRedisKeyConstant;
import io.shulie.takin.web.biz.pojo.request.report.ReportMockRequest;
import io.shulie.takin.web.biz.pojo.request.report.ReportMockResponse;
import io.shulie.takin.web.biz.service.report.ReportMockService;
import io.shulie.takin.web.data.dao.application.AppRemoteCallDAO;
import io.shulie.takin.web.data.dao.application.ApplicationDAO;
import io.shulie.takin.web.data.dao.application.LinkGuardDAO;
import io.shulie.takin.web.data.dao.report.ReportMockDAO;
import io.shulie.takin.web.data.param.report.ReportMockCreateParam;
import io.shulie.takin.web.data.result.application.AppMockCallResult;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class ReportMockServiceImpl implements ReportMockService {
    @Resource
    private ReportMockDAO reportMockDAO;
    @Resource
    private ApplicationDAO applicationDAO;
    @Resource
    private AppRemoteCallDAO appRemoteCallDAO;
    @Resource
    private LinkGuardDAO linkGuardDAO;

    @Autowired
    @Qualifier("redisTemplate")
    private RedisTemplate redisTemplate;
    @Override
    public void saveReportMockData(ReportMockRequest request) {
        ReportMockResponse mockResponse = new ReportMockResponse();
        mockResponse.setAppName("demo");
        mockResponse.setServiceName("a");
        mockResponse.setMethodName("b");
        mockResponse.setFailureCount(1L);
        mockResponse.setSuccessCount(2L);
        mockResponse.setAvgRt(120.22);

        ReportMockResponse mockResponse2 = new ReportMockResponse();
        mockResponse2.setAppName("demo");
        mockResponse2.setServiceName("/a/b");
        mockResponse2.setMethodName("GET");
        mockResponse2.setFailureCount(2L);
        mockResponse2.setSuccessCount(3L);
        mockResponse2.setAvgRt(130.22);
        List<ReportMockResponse> responseList = new ArrayList<>();
        responseList.add(mockResponse);
        responseList.add(mockResponse2);
        if(CollectionUtils.isEmpty(responseList)) {
            return;
        }
        //查询mock列表原数据
        List<String> appNameList = responseList.stream().map(ReportMockResponse::getAppName).distinct().collect(Collectors.toList());
        List<AppMockCallResult> mockList = new ArrayList<>();
        for(String appName : appNameList) {
            Long appId = getApplicationId(appName, request.getTenantId(), request.getEnvCode());
            if(appId == null) {
                continue;
            }
            mockList.addAll(getAppLinkGuardMockList(appId, request.getTenantId(), request.getEnvCode()));
            mockList.addAll(getAppRemoteCallMockList(appId, request.getTenantId(), request.getEnvCode()));
        }
        if(CollectionUtils.isEmpty(mockList)) {
            return;
        }
        //保存report_mock表
        List<ReportMockCreateParam> mockEntityList = buildReportMockEntityList(request, responseList, mockList);
        if(CollectionUtils.isNotEmpty(mockEntityList)) {
            for (ReportMockCreateParam mockEntity : mockEntityList) {
                reportMockDAO.insertOrUpdate(mockEntity);
            }
        }
    }

    private List<ReportMockCreateParam> buildReportMockEntityList(ReportMockRequest request, List<ReportMockResponse> responseList, List<AppMockCallResult> mockList) {
        List<ReportMockCreateParam> entityList = new ArrayList<>();
        Map<String, AppMockCallResult> mockMap = ListHelper.transferToMap(mockList, data -> data.getAppName()+ Constants.SPLIT+data.getMockName(), data -> data);
        for(ReportMockResponse mockResponse : responseList) {
            //mockName 1、挡板 类名#方法名 2、远程调用 请求url 或者 类名#方法名
            String key = mockResponse.getAppName() + Constants.SPLIT + mockResponse.getServiceName();
            if(mockMap.containsKey(key)) {
                entityList.add(convertReportMockResponse(request, mockResponse, mockMap.get(key)));
            } else {
                key = key + "#" + mockResponse.getMethodName();
                if(mockMap.containsKey(key)) {
                    entityList.add(convertReportMockResponse(request, mockResponse, mockMap.get(key)));
                }
            }
        }
        return entityList;
    }

    private ReportMockCreateParam convertReportMockResponse(ReportMockRequest mockRequest, ReportMockResponse mockResponse, AppMockCallResult mockCallResult) {
        ReportMockCreateParam mockEntity = new ReportMockCreateParam();
        mockEntity.setReportId(mockRequest.getReportId());
        mockEntity.setStartTime(mockRequest.getStartTime());
        mockEntity.setEndTime(mockRequest.getEndTime());
        mockEntity.setAppName(mockResponse.getAppName());
        mockEntity.setMockName(mockCallResult.getMockName());
        mockEntity.setMockScript(mockCallResult.getMockScript());
        mockEntity.setMockType(mockCallResult.getMockType());
        mockEntity.setMockStatus(mockCallResult.getMockStatus());
        mockEntity.setFailureCount(mockResponse.getFailureCount());
        mockEntity.setSuccessCount(mockResponse.getSuccessCount());
        mockEntity.setAvgRt(mockResponse.getAvgRt());
        return mockEntity;
    }

    private Long getApplicationId(String appName, Long tenantId, String envCode) {
        String key = String.format(WebRedisKeyConstant.APPLICATION_CACHE_KEY, appName, tenantId, envCode);
        if(redisTemplate.hasKey(key)) {
            Object value = redisTemplate.opsForValue().get(key);
            try {
                return Long.valueOf(value.toString());
            } catch (Exception e) {
                return null;
            }
        }
        Long appId = applicationDAO.queryIdByName(appName, tenantId, envCode);
        redisTemplate.opsForValue().set(key, appId, 12L, TimeUnit.HOURS);
        return appId;
    }

    private List<AppMockCallResult> getAppRemoteCallMockList(Long applicationId, Long tenantId, String envCode) {
        String key = String.format(WebRedisKeyConstant.REMOTE_MOCK_CACHE_KEY, applicationId, tenantId, envCode);
        if(redisTemplate.hasKey(key)) {
            try {
                return JSON.parseArray(redisTemplate.opsForValue().get(key).toString(), AppMockCallResult.class);
            } catch (Exception e) {
                return new ArrayList<>();
            }
        }
        List<AppMockCallResult> resultList = appRemoteCallDAO.getRemoteCallMockList(applicationId);
        redisTemplate.opsForValue().set(key, JSON.toJSONString(resultList), 5L, TimeUnit.MINUTES);
        return resultList;
    }

    private List<AppMockCallResult> getAppLinkGuardMockList(Long applicationId, Long tenantId, String envCode) {
        String key = String.format(WebRedisKeyConstant.LOCAL_MOCK_CACHE_KEY, applicationId, tenantId, envCode);
        if(redisTemplate.hasKey(key)) {
            try {
                return JSON.parseArray(redisTemplate.opsForValue().get(key).toString(), AppMockCallResult.class);
            } catch (Exception e) {
                return new ArrayList<>();
            }
        }
        List<AppMockCallResult> resultList = linkGuardDAO.listAppMockCallResultByAppId(applicationId);
        redisTemplate.opsForValue().set(key, JSON.toJSONString(resultList), 5L, TimeUnit.MINUTES);
        return resultList;
    }
}
