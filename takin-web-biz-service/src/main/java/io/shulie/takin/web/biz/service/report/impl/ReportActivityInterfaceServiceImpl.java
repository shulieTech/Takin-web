package io.shulie.takin.web.biz.service.report.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pamirs.takin.entity.domain.dto.report.ReportPerformanceInterfaceDTO;
import com.pamirs.takin.entity.domain.entity.report.ReportActivityEntity;
import com.pamirs.takin.entity.domain.entity.report.ReportActivityInterfaceEntity;
import io.shulie.takin.cloud.sdk.model.ScriptNodeSummaryBean;
import io.shulie.takin.cloud.sdk.model.common.DataBean;
import io.shulie.takin.web.amdb.api.ReportClient;
import io.shulie.takin.web.amdb.bean.query.report.ReportQueryDTO;
import io.shulie.takin.web.amdb.bean.query.trace.EntranceRuleDTO;
import io.shulie.takin.web.amdb.bean.result.report.ReportActivityInterfaceDTO;
import io.shulie.takin.web.biz.pojo.request.report.ReportPerformanceInterfaceRequest;
import io.shulie.takin.web.biz.service.report.ReportActivityInterfaceService;
import io.shulie.takin.web.biz.service.report.ReportRealTimeService;
import io.shulie.takin.web.data.dao.report.ReportActivityDAO;
import io.shulie.takin.web.data.dao.report.ReportActivityInterfaceDAO;
import io.shulie.takin.web.data.param.report.ReportActivityInterfaceQueryParam;
import io.shulie.takin.web.data.param.report.ReportActivityInterfaceQueryParam.EntranceParam;
import io.shulie.takin.web.diff.api.report.ReportApi;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ReportActivityInterfaceServiceImpl implements ReportActivityInterfaceService {

    @Resource
    private ReportClient reportClient;

    @Resource
    private ReportActivityInterfaceDAO interfaceDAO;

    @Resource
    private ReportActivityDAO activityDAO;

    @Resource
    private ReportRealTimeService reportRealTimeService;

    @Resource
    private ReportApi reportApi;

    @Override
    public void syncActivityInterface(String reportId) {
        if (log.isDebugEnabled()) {
            log.debug("开始同步压测报告[{}]业务活动接口数据", reportId);
        }
        List<ReportActivityInterfaceDTO> interfaces = reportClient.listReportActivityInterface(
            new ReportQueryDTO(reportId));
        if (CollectionUtils.isNotEmpty(interfaces)) {
            Date now = new Date();
            List<ReportActivityInterfaceEntity> entityList = interfaces.stream().map(activity -> {
                ReportActivityInterfaceEntity entity = new ReportActivityInterfaceEntity();
                BeanUtils.copyProperties(activity, entity);
                entity.setSyncTime(now);
                return entity;
            }).collect(Collectors.toList());
            interfaceDAO.batchInsert(entityList);
            if (log.isDebugEnabled()) {
                log.debug("同步压测报告[{}]业务活动接口数据完成", reportId);
            }
            updateAssociateCost(Long.valueOf(reportId));
        } else {
            if (log.isDebugEnabled()) {
                log.debug("同步压测报告[{}]业务活动接口数据结束，没有需要同步的数据", reportId);
            }
        }
    }

    private void updateAssociateCost(Long reportId) {
        Map<EntranceParam, BigDecimal> metricsCost = syncActivityInfluxMetrics(reportId);
        if (!metricsCost.isEmpty()) {
            updateActivityCost(reportId, metricsCost);
            updateActivityInterfaceCostPercent(reportId, metricsCost);
        }
    }

    private void updateActivityInterfaceCostPercent(Long reportId, Map<EntranceParam, BigDecimal> metricsCost) {
        List<ReportActivityInterfaceEntity> entities = new ArrayList<>(metricsCost.size());
        for (Entry<EntranceParam, BigDecimal> entry : metricsCost.entrySet()) {
            EntranceParam key = entry.getKey();
            ReportActivityInterfaceEntity entity = new ReportActivityInterfaceEntity();
            entity.setReportId(reportId);
            entity.setEntranceAppName(key.getAppName());
            entity.setEntranceServiceName(key.getServiceName());
            entity.setEntranceMethodName(key.getMethodName());
            entity.setEntranceRpcType(key.getRpcType());
            entity.setServiceAvgCost(entry.getValue());
            entities.add(entity);
        }
        interfaceDAO.updateInterfaceCostPercent(entities);
    }

    private void updateActivityCost(Long reportId, Map<EntranceParam, BigDecimal> metricsCost) {
        List<ReportActivityEntity> entities = new ArrayList<>(metricsCost.size());
        for (Entry<EntranceParam, BigDecimal> entry : metricsCost.entrySet()) {
            EntranceParam key = entry.getKey();
            ReportActivityEntity entity = new ReportActivityEntity();
            entity.setReportId(reportId);
            entity.setAppName(key.getAppName());
            entity.setServiceName(key.getServiceName());
            entity.setMethodName(key.getMethodName());
            entity.setRpcType(key.getRpcType());
            entity.setAvgCost(entry.getValue());
            entities.add(entity);
        }
        activityDAO.updateEntranceAvgCost(entities);
    }

    /**
     * 同步业务活动的平均rt
     */
    private Map<EntranceParam, BigDecimal> syncActivityInfluxMetrics(Long reportId) {
        // 耗时树结果
        Map<String, BigDecimal> avgRt = new HashMap<>();
        reportApi.getSummaryList(reportId).getScriptNodeSummaryBeans()
            .forEach(summaryBean -> {
                recursionExtractActivityRt(summaryBean, avgRt);
            });
        Map<String, EntranceRuleDTO> ruleMap = queryEntrance("", null, reportId).stream()
            .collect(Collectors.toMap(rule -> String.valueOf(rule.getLinkId()), Function.identity(),
                (oldVal, newVal) -> oldVal
            ));
        Map<EntranceParam, BigDecimal> resultMap = new HashMap<>(avgRt.size());
        for (Entry<String, BigDecimal> entry : avgRt.entrySet()) {
            String linkId = entry.getKey();
            EntranceRuleDTO rule = ruleMap.get(linkId);
            if (rule != null && StringUtils.countMatches(rule.getEntrance(), "|") == 2) {
                EntranceParam entity = new EntranceParam();
                entity.setAppName(rule.getAppName());
                String[] entranceArr = rule.getEntrance().split("\\|");
                entity.setServiceName(entranceArr[1]);
                entity.setMethodName(entranceArr[0]);
                entity.setRpcType(entranceArr[2]);
                resultMap.putIfAbsent(entity, entry.getValue());
            }
        }
        return resultMap;
    }

    @Override
    public Pair<List<ReportPerformanceInterfaceDTO>, Long> queryInterfaceByRequest(ReportPerformanceInterfaceRequest request) {
        ReportActivityInterfaceQueryParam queryParam = buildMetricsParam(request);
        Page<ReportActivityInterfaceEntity> page = PageHelper.startPage(queryParam.getCurrentPage() + 1, queryParam.getPageSize());
        List<ReportActivityInterfaceEntity> entities = interfaceDAO.queryByParam(queryParam);
        List<ReportPerformanceInterfaceDTO> interfaces = entities.stream().map(interfaceEntity -> {
            ReportPerformanceInterfaceDTO dto = new ReportPerformanceInterfaceDTO();
            BeanUtils.copyProperties(interfaceEntity, dto);
            return dto;
        }).collect(Collectors.toList());
        return Pair.of(interfaces, page.getTotal());
    }

    @Override
    public List<ReportPerformanceInterfaceDTO> queryTop5ByRequest(ReportPerformanceInterfaceRequest request) {
        request.setCurrent(0);
        request.setCurrentPage(5);
        request.setSortField("avg_cost");
        request.setSortType("desc");
        return this.queryInterfaceByRequest(request).getKey();
    }

    @Override
    public List<ReportActivityInterfaceEntity> selectByReportId(String reportId) {
        return interfaceDAO.selectByReportId(reportId);
    }

    /**
     * 构造入口请求条件
     *
     * @param request 性能接口请求
     * @return 入口请求条件
     */
    private ReportActivityInterfaceQueryParam buildMetricsParam(ReportPerformanceInterfaceRequest request) {
        ReportActivityInterfaceQueryParam metricsQueryParam = new ReportActivityInterfaceQueryParam();
        BeanUtils.copyProperties(request, metricsQueryParam);
        List<EntranceRuleDTO> entrances = queryEntrance(request.getXpathMd5(), request.getSceneId(), request.getReportId());
        List<EntranceParam> entranceParamList = entrances.stream()
            .filter(entrance -> StringUtils.countMatches(entrance.getEntrance(), "|") == 2)
            .map(entrance -> {
                EntranceParam param = new EntranceParam();
                param.setAppName(entrance.getAppName());
                String[] entranceArr = entrance.getEntrance().split("\\|");
                param.setServiceName(entranceArr[1]);
                param.setMethodName(entranceArr[0]);
                param.setRpcType(entranceArr[2]);
                return param;
            }).collect(Collectors.toList());
        metricsQueryParam.setEntrances(entranceParamList);
        return metricsQueryParam;
    }

    private List<EntranceRuleDTO> queryEntrance(String xpathMd5, Long sceneId, Long reportId) {
        List<Long> activityIds = reportRealTimeService.querySceneActivities(xpathMd5, sceneId, reportId);
        return reportRealTimeService.getEntryListByBusinessActivityIds(activityIds);
    }

    // 获取树中业务活动的平均rt
    private void recursionExtractActivityRt(ScriptNodeSummaryBean summaryBean, Map<String, BigDecimal> rtMap) {
        Long activityId = summaryBean.getActivityId();
        if (Objects.nonNull(activityId) && activityId.compareTo(0L) > 0) {
            DataBean avgRt = summaryBean.getAvgRt();
            Object result;
            if (Objects.nonNull(avgRt) && Objects.nonNull(result = avgRt.getResult())) {
                rtMap.put(String.valueOf(activityId), new BigDecimal(result.toString()));
            }

        }
        List<ScriptNodeSummaryBean> children = summaryBean.getChildren();
        if (CollectionUtils.isNotEmpty(children)) {
            children.forEach(child -> recursionExtractActivityRt(child, rtMap));
        }
    }
}
