package io.shulie.takin.web.biz.service.report.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import com.google.common.collect.Lists;
import com.pamirs.takin.entity.domain.dto.report.ReportPerformanceCostTrendDTO;
import com.pamirs.takin.entity.domain.dto.report.ReportPerformanceCostTrendDTO.ServiceMetrics;
import com.pamirs.takin.entity.domain.dto.report.ReportPerformanceInterfaceDTO;
import com.pamirs.takin.entity.domain.entity.report.ReportActivityInterfaceEntity;
import com.pamirs.takin.entity.domain.entity.report.ReportInterfaceMetricsEntity;
import io.shulie.takin.web.amdb.api.ReportClient;
import io.shulie.takin.web.amdb.bean.query.report.ReportQueryDTO;
import io.shulie.takin.web.amdb.bean.query.report.ReportQueryDTO.InterfaceParam;
import io.shulie.takin.web.amdb.bean.result.report.ReportInterfaceMetricsDTO;
import io.shulie.takin.web.biz.pojo.request.report.ReportPerformanceCostTrendRequest;
import io.shulie.takin.web.biz.pojo.request.report.ReportPerformanceCostTrendRequest.ServiceParam;
import io.shulie.takin.web.biz.pojo.request.report.ReportPerformanceInterfaceRequest;
import io.shulie.takin.web.biz.service.report.ReportActivityInterfaceService;
import io.shulie.takin.web.biz.service.report.ReportInterfaceMetricsService;
import io.shulie.takin.web.data.dao.report.ReportInterfaceMetricsDAO;
import io.shulie.takin.web.data.dao.report.ReportTaskDAO;
import io.shulie.takin.web.data.param.report.ReportInterfaceMetricsQueryParam;
import io.shulie.takin.web.data.param.report.ReportInterfaceMetricsQueryParam.MetricsServiceParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ReportInterfaceMetricsServiceImpl implements ReportInterfaceMetricsService {

    @Resource
    private ReportClient reportClient;

    @Resource
    private ReportActivityInterfaceService interfaceService;

    @Resource
    private ReportInterfaceMetricsDAO metricsDAO;

    @Resource
    private ReportTaskDAO taskDAO;

    @Resource(name = "reportInterfaceMetricsSyncThreadPool")
    private ExecutorService reportInterfaceMetricsSyncThreadPool;

    private static final int SLICE_COUNT = 5;

    private static final int ONE_DAY_RANGE = 24 * 60 * 12;

    @Override
    public void syncInterfaceMetrics(String reportId) {
        if (log.isDebugEnabled()) {
            log.debug("开始同步压测报告[{}]业务活动接口窗口数据", reportId);
        }
        List<ReportActivityInterfaceEntity> entities = interfaceService.selectByReportId(reportId);
        if (CollectionUtils.isEmpty(entities)) {
            if (log.isDebugEnabled()) {
                log.debug("同步压测报告[{}]业务活动接口窗口数据结束，没有需要同步的数据", reportId);
            }
            return;
        }
        CompletableFuture.allOf(Lists.partition(entities, SLICE_COUNT).stream()
                .map(entityList -> CompletableFuture.runAsync(() -> syncInterfaceMetrics(reportId, entityList),
                    reportInterfaceMetricsSyncThreadPool)).toArray(CompletableFuture[]::new))
            .thenRun(() -> syncSuccessTask(reportId));
    }

    @Override
    public void syncSuccessTask(String reportId) {
        if (log.isDebugEnabled()) {
            log.debug("开始更新压测报告[{}]任务状态", reportId);
        }
        taskDAO.syncSuccess(reportId);
        if (log.isDebugEnabled()) {
            log.debug("更新压测报告[{}]任务状态完成", reportId);
        }
    }

    @Override
    public ReportPerformanceCostTrendDTO queryCostTrend(ReportPerformanceCostTrendRequest request) {
        fillServiceParamIfNecessary(request);
        ReportInterfaceMetricsQueryParam param = new ReportInterfaceMetricsQueryParam();
        BeanUtils.copyProperties(request, param, "services");
        List<ServiceParam> services = request.getServices();
        if (CollectionUtils.isNotEmpty(services)) {
            List<MetricsServiceParam> serviceParams = services.stream().map(service -> {
                MetricsServiceParam serviceParam = new MetricsServiceParam();
                BeanUtils.copyProperties(service, serviceParam);
                return serviceParam;
            }).collect(Collectors.toList());
            param.setServices(serviceParams);
        }
        List<ReportInterfaceMetricsEntity> entities = metricsDAO.queryByParam(param);
        return reassembledInterfaceMetrics(entities);
    }

    private void syncInterfaceMetrics(String reportId, List<ReportActivityInterfaceEntity> entityList) {
        ReportQueryDTO query = new ReportQueryDTO(reportId);
        List<InterfaceParam> params = entityList.stream().map(entry -> {
            InterfaceParam param = new InterfaceParam();
            BeanUtils.copyProperties(entry, param);
            return param;
        }).collect(Collectors.toList());
        query.setParams(params);
        List<ReportInterfaceMetricsDTO> metricsList = reportClient.listReportInterfaceMetrics(query);
        if (CollectionUtils.isNotEmpty(metricsList)) {
            Date now = new Date();
            List<ReportInterfaceMetricsEntity> entities = metricsList.stream().map(metrics -> {
                ReportInterfaceMetricsEntity entity = new ReportInterfaceMetricsEntity();
                BeanUtils.copyProperties(metrics, entity);
                entity.setSyncTime(now);
                return entity;
            }).collect(Collectors.toList());
            metricsDAO.batchInsert(entities);
        }
    }

    private ReportPerformanceCostTrendDTO reassembledInterfaceMetrics(List<ReportInterfaceMetricsEntity> entities) {
        if (CollectionUtils.isEmpty(entities)) {
            return new ReportPerformanceCostTrendDTO();
        }
        Map<DateTime, List<ReportInterfaceMetricsEntity>> data = entities.stream().collect(
            Collectors.groupingBy(entity -> DateTime.of(entity.getTimeWindow()), TreeMap::new, Collectors.toList()));
        int size = data.size();
        String dateFormatter = size >= ONE_DAY_RANGE ? DatePattern.NORM_DATETIME_PATTERN : DatePattern.NORM_TIME_PATTERN;
        ReportPerformanceCostTrendDTO dto = new ReportPerformanceCostTrendDTO();

        List<String> times = new ArrayList<>(size);
        Map<ServiceMetrics, List<String>> metricsMap = new HashMap<>();
        for (Entry<DateTime, List<ReportInterfaceMetricsEntity>> entry : data.entrySet()) {
            times.add(entry.getKey().toString(dateFormatter));
            List<ReportInterfaceMetricsEntity> entryValue = entry.getValue();
            entryValue.forEach(value -> {
                ServiceMetrics metrics = new ServiceMetrics();
                BeanUtils.copyProperties(value, metrics);
                List<String> dataList = metricsMap.computeIfAbsent(metrics, k -> {
                    List<String> metricsDataList = new ArrayList<>();
                    metrics.setData(metricsDataList);
                    return metricsDataList;
                });
                dataList.add(value.getAvgCost().toPlainString());
            });
        }
        dto.setTime(times);
        dto.setList(new ArrayList<>(metricsMap.keySet()));
        return dto;
    }

    private void fillServiceParamIfNecessary(ReportPerformanceCostTrendRequest request) {
        List<ServiceParam> services = request.getServices();
        if (CollectionUtils.isEmpty(services)) {
            ReportPerformanceInterfaceRequest interfaceRequest = new ReportPerformanceInterfaceRequest();
            interfaceRequest.setReportId(request.getReportId());
            interfaceRequest.setSceneId(request.getSceneId());
            interfaceRequest.setXpathMd5(request.getXpathMd5());
            List<ReportPerformanceInterfaceDTO> entities = interfaceService.queryTop5ByRequest(interfaceRequest);
            List<ServiceParam> params = entities.stream().map(entity -> {
                ServiceParam param = new ServiceParam();
                BeanUtils.copyProperties(entity, param);
                return param;
            }).collect(Collectors.toList());
            request.setServices(params);
        }
    }
}
