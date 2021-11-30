package io.shulie.takin.web.biz.service.report.impl;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.pamirs.takin.entity.dao.confcenter.TApplicationMntDao;
import com.pamirs.takin.entity.domain.dto.report.ReportApplicationDTO;
import com.pamirs.takin.entity.domain.dto.report.ReportDetailDTO;
import com.pamirs.takin.entity.domain.entity.TApplicationMnt;
import com.pamirs.takin.entity.domain.risk.Metrices;
import io.shulie.takin.cloud.sdk.model.response.report.MetricesResponse;
import io.shulie.takin.web.biz.service.report.ReportService;
import io.shulie.takin.web.common.enums.ContextSourceEnum;
import io.shulie.takin.web.common.util.RedisHelper;
import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * 压测数据准备，存放内存
 *
 * @author qianshui
 * @date 2020/7/29 下午2:54
 */
@Component
@Slf4j
public class ReportDataCache {

    /**
     * 指标信息
     */
    private static final String REPORT_METRIC_KEY = "takin:web:report:metric:key:%s:%s:%s";

    /**
     * 报告明细
     */
    private static final String REPORT_DETAIL_KEY = "takin:web:report:detail:key:%s:%s:%s";
    /**
     * 报告应用信息
     */
    private static final String REPORT_APPLICATION_KEY = "takin:web:report:application:key:%s:%s:%s";

    @Autowired
    private TApplicationMntDao tApplicationMntDao;

    @Autowired
    private ReportService reportService;

    @Autowired
    private ReportApplicationService reportApplicationService;

    @Autowired
    @Qualifier("redisTemplate")
    private RedisTemplate redisTemplate;

    /**
     * 准备基础数据
     */
    public void readyCloudReportData(Long reportId) {
        queryReportDetail(reportId);

        queryAllMetricsData(reportId);

        fetchApplications(reportId);
    }

    /**
     * 查询报告基本信息
     */
    private void queryReportDetail(Long reportId) {
        ReportApplicationDTO reportApplication = reportApplicationService.getReportApplication(reportId);
        final ReportDetailDTO reportDetail = reportApplication.getReportDetail();
        final TenantCommonExt commonExt = WebPluginUtils.fillTenantCommonExt(reportDetail.getTenantId(),
            reportDetail.getEnvCode());
        if (Objects.isNull(commonExt) || StringUtils.isBlank(commonExt.getTenantAppKey())){
            log.error("租户AppKey 不能为空！");
            return;
        }
        String tenantAppKey = commonExt.getTenantAppKey();
        if (reportApplication.getReportDetail() != null) {
            WebPluginUtils.setTraceTenantContext(reportDetail.getTenantId(),tenantAppKey,reportDetail.getEnvCode(),commonExt.getTenantCode(),
                ContextSourceEnum.JOB.getCode());
            redisTemplate.opsForValue().set(getReportDetailKey(reportId), reportApplication.getReportDetail());
            log.info("Report Id={}, Status={}，endTime = {}", reportId, reportApplication.getReportDetail().getTaskStatus(),
                reportApplication.getReportDetail().getEndTime());
        }
        if (CollectionUtils.isNotEmpty(reportApplication.getApplicationNames())) {
            redisTemplate.opsForSet().add(getReportApplicationKey(reportId), reportApplication.getApplicationNames().toArray());
            log.info("Report Id={}, applicationName={}", reportId, JSON.toJSONString(reportApplication.getApplicationNames()));
        }
    }

    /**
     * 组装应用
     *
     * @return
     */
    private String getReportApplicationKey(Long reportId) {

        return String.format(REPORT_APPLICATION_KEY, WebPluginUtils.traceTenantId(), WebPluginUtils.traceEnvCode(), reportId);
    }

    /**
     * 组装详情报告
     *
     * @return
     */
    private String getReportDetailKey(Long reportId) {
        return String.format(REPORT_DETAIL_KEY, WebPluginUtils.traceTenantId(), WebPluginUtils.traceEnvCode(), reportId);
    }

    /**
     * 组装指标数据
     *
     * @return
     */
    private String getReportMetricKey(Long reportId) {
        return String.format(REPORT_METRIC_KEY, WebPluginUtils.traceTenantId(), WebPluginUtils.traceEnvCode(), reportId);
    }

    /**
     * 查询all类型的所有压测指标数据
     *
     * @param reportId 报告id
     */
    private void queryAllMetricsData(Long reportId) {
        ReportDetailDTO reportDetail = this.getReportDetailDTO(reportId);
        if (reportDetail == null) {
            return;
        }

        // 指标 map
        List<MetricesResponse> metricsList = reportService.queryMetrics(reportId, reportDetail.getSceneId());

        if (CollectionUtils.isEmpty(metricsList)) {
            log.error("ReportDataCache Cache Jmeter Metric is null");
            return;
        }

        log.info("ReportDataCache Cache Jmeter Metrics Data Size={}, One Sample: {}",
            metricsList.size(), metricsList.get(0));

        // 指标 redis key
        String reportMetricKey = this.getReportMetricKey(reportId);
        for (MetricesResponse metrics : metricsList) {
            // redis hash 结构存放指标, time 时间戳为 key, avgTps 为 value
            if (metrics.getTime() == null) {
                continue;
            }
            RedisHelper.hashPut(reportMetricKey, metrics.getTime().toString(), metrics.getAvgTps());
        }
    }

    /**
     * 获取报告详情
     *
     * @return
     */
    public ReportDetailDTO getReportDetailDTO(Long reportId) {
        // 解析 redis
        if (!redisTemplate.hasKey(getReportDetailKey(reportId))) {
            return null;
        }
        ReportDetailDTO reportDetail = (ReportDetailDTO)redisTemplate.opsForValue().get(getReportDetailKey(reportId));
        if (reportDetail == null || reportDetail.getSceneId() == null) {
            // 确保数据完整性
            return null;
        }
        return reportDetail;
    }

    /**
     * 获取应用列表
     */
    private void fetchApplications(Long reportId) {
        ReportDetailDTO reportDetail = getReportDetailDTO(reportId);
        if (reportDetail == null) {
            return;
        }
        if (CollectionUtils.isEmpty(reportDetail.getBusinessActivity())) {
            log.error("报告中关联的业务活动为空");
            return;
        }
        Set<Long> appSet = Sets.newHashSet();
        reportDetail.getBusinessActivity().forEach(
            data -> appSet.addAll(splitApplicationIds(data.getApplicationIds())));
        if (appSet.size() == 0) {
            log.error("报告中关联的应用为空");
            return;
        }
        List<TApplicationMnt> appsList = tApplicationMntDao.queryApplicationMntListByIds(Lists.newArrayList(appSet));
        if (CollectionUtils.isEmpty(appsList)) {
            log.error("报告中关联的应用为空");
            return;
        }
        List<String> applications = appsList.stream().map(TApplicationMnt::getApplicationName)
            .filter(StringUtils::isNoneBlank).distinct().collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(applications)) {
            redisTemplate.opsForSet().add(getReportApplicationKey(reportId), applications.toArray());
            log.info("Report Id={},报告中关联的应用有 applicationName={}", reportId, JSON.toJSONString(applications));
        }
    }

    /**
     * 根据 reportId 查询所有的指标
     *
     * @param reportId 报告id
     * @return 指标列表
     */
    public List<Metrices> listAllMetricsData(Long reportId) {
        String reportMetricKey = this.getReportMetricKey(reportId);
        if (!RedisHelper.hasKey(reportMetricKey)) {
            return Collections.emptyList();
        }

        // 根据 redis key, 获取整个 hash, key 是时间戳, value 是 avgTps
        Map<Object, Object> metricsMap = RedisHelper.hashGetAll(reportMetricKey);
        if (org.springframework.util.CollectionUtils.isEmpty(metricsMap)) {
            return Collections.emptyList();
        }

        // 使用 json 转换一下
        return metricsMap.entrySet().stream().map(entry -> {
            Metrices metrices = new Metrices();
            metrices.setTime(Long.valueOf(entry.getKey().toString()));
            metrices.setAvgTps(Double.valueOf(entry.getValue().toString()));
            return metrices;
        }).collect(Collectors.toList());
    }

    /**
     * 获取应用列表
     *
     * @return
     */
    public List<String> getApplications(Long reportId) {
        if (!redisTemplate.hasKey(getReportApplicationKey(reportId))) {
            return Lists.newArrayList();
        }
        Set<String> resultSet = redisTemplate.opsForSet().members(getReportApplicationKey(reportId));
        return Lists.newArrayList(resultSet);
    }

    public void clearDataCache(Long reportId) {
        redisTemplate.delete(getReportDetailKey(reportId));
        redisTemplate.delete(getReportApplicationKey(reportId));
        redisTemplate.delete(getReportMetricKey(reportId));
    }

    private List<Long> splitApplicationIds(String applicationIds) {
        if (StringUtils.isBlank(applicationIds)) {
            return Collections.EMPTY_LIST;
        }
        String[] args = applicationIds.split(",");
        List<Long> dataList = Lists.newArrayList();
        for (String arg : args) {
            dataList.add(Long.parseLong(arg));
        }
        return dataList;
    }
}
