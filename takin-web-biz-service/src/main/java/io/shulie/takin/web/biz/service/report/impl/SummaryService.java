package io.shulie.takin.web.biz.service.report.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;

import com.google.common.collect.Lists;
import com.pamirs.takin.common.util.DateUtils;
import com.pamirs.takin.entity.domain.dto.report.ReportDetailDTO;
import com.pamirs.takin.entity.domain.entity.report.TpsTarget;
import com.pamirs.takin.entity.domain.entity.report.TpsTargetArray;
import com.pamirs.takin.entity.domain.risk.Metrices;
import io.shulie.takin.web.biz.service.report.ReportService;
import io.shulie.takin.web.biz.service.risk.util.DateUtil;
import io.shulie.takin.web.data.common.InfluxDatabaseManager;
import io.shulie.takin.web.data.dao.report.*;
import io.shulie.takin.web.data.param.report.ReportApplicationSummaryCreateParam;
import io.shulie.takin.web.data.param.report.ReportMachineUpdateParam;
import io.shulie.takin.web.data.param.report.ReportSummaryCreateParam;
import io.shulie.takin.web.data.result.baseserver.BaseServerResult;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 报告汇总接口
 *
 * @author qianshui
 * @date 2020/7/28 下午1:55
 */
@Component
@Slf4j
public class SummaryService {

    @Resource
    private ReportBottleneckInterfaceDAO reportBottleneckInterfaceDAO;

    @Resource
    private ReportApplicationSummaryDAO reportApplicationSummaryDAO;

    @Autowired
    private ReportService reportService;

    @Resource
    private ReportSummaryDAO reportSummaryDAO;

    @Resource
    private ReportMachineDAO reportMachineDAO;
    @Resource
    private ReportMockDAO reportMockDAO;

    @Autowired
    private ReportDataCache reportDataCache;

    @Autowired
    private InfluxDatabaseManager influxDatabaseManager;

    public void calcApplicationSummary(Long reportId) {
        List<Map<String, Object>> dataList = reportMachineDAO.selectCountByReport(reportId);
        if (CollectionUtils.isEmpty(dataList)) {
            return;
        }

        List<ReportApplicationSummaryCreateParam> applications = Lists.newArrayList();
        for (Map<String, Object> dataMap : dataList) {
            String applicationName = (String)dataMap.get("application_name");
            if (StringUtils.isBlank(applicationName)) {
                continue;
            }
            Integer totalCount = convertLong((Long)dataMap.get("count"));
            Integer riskCount = convertBigDecimal((BigDecimal)dataMap.get("riskSum"));

            ReportApplicationSummaryCreateParam application = new ReportApplicationSummaryCreateParam();
            application.setReportId(reportId);
            application.setApplicationName(applicationName);
            application.setMachineTotalCount(totalCount);
            application.setMachineRiskCount(riskCount);
            applications.add(application);
        }
        if (CollectionUtils.isEmpty(applications)) {
            return;
        }
        applications.forEach(reportApplicationSummaryDAO::insertOrUpdate);
    }

    public void calcReportSummay(Long reportId) {
        Integer bottleneckInterfaceCount = convertLong(reportBottleneckInterfaceDAO.selectCountByReportId(reportId));

        Integer appCount = 0;
        Integer totalCount = 0;
        Integer riskCount = 0;
        Map<String, Object> countMap = reportApplicationSummaryDAO.selectCountByReportId(reportId);
        if (MapUtils.isNotEmpty(countMap)) {
            appCount = convertLong((Long)countMap.get("count"));
            totalCount = convertBigDecimal((BigDecimal)countMap.get("totalSum"));
            riskCount = convertBigDecimal((BigDecimal)countMap.get("riskSum"));
        }

        Integer warnCount = 0;
        Integer businessCount = 0;
        Integer passBusinessCount = 0;
        Map<String, Object> cloudMap = reportService.queryReportCount(reportId);
        log.info("call cloud ReportSummary Success, reportId={},data={}", reportId,cloudMap.toString());
        if (MapUtils.isNotEmpty(cloudMap)) {
            warnCount = (Integer)cloudMap.get("warnCount");
            businessCount = (Integer)cloudMap.get("count");
            passBusinessCount = (Integer)cloudMap.get("passSum");
        }
        warnCount = warnCount != null ? warnCount : 0;
        businessCount = businessCount != null ? businessCount : 0;
        passBusinessCount = passBusinessCount != null ? passBusinessCount : 0;
        Long mockCount =reportMockDAO.selectCountMockByReportId(reportId);
        mockCount = mockCount != null ? mockCount : 0;
        ReportSummaryCreateParam reportSummary = new ReportSummaryCreateParam();
        reportSummary.setReportId(reportId);
        reportSummary.setBottleneckInterfaceCount(bottleneckInterfaceCount);
        reportSummary.setRiskMachineCount(riskCount);
        reportSummary.setBusinessActivityCount(businessCount);
        reportSummary.setUnachieveBusinessActivityCount(businessCount - passBusinessCount);
        reportSummary.setApplicationCount(appCount);
        reportSummary.setMachineCount(totalCount);
        reportSummary.setWarnCount(warnCount);
        reportSummary.setMockCount(mockCount.intValue());
        reportSummary.setTenantId(WebPluginUtils.traceTenantId());
        reportSummary.setEnvCode(WebPluginUtils.traceEnvCode());
        reportSummaryDAO.insertOrUpdate(reportSummary);
        log.debug("Build ReportSummary Success, reportId={}", reportId);
    }

    public void calcTpsTarget(Long reportId) {
        ReportDetailDTO reportDetailDTO = reportDataCache.getReportDetailDTO(reportId);
        if(reportDetailDTO == null) {
            log.error("calcTpsTarget 未找到报告【{}】",reportId);
            return;
        }
        List<Metrices> metrics = reportDataCache.listAllMetricsData(reportId);
        List<String> applications = reportDataCache.getApplications(reportId);
        if (CollectionUtils.isEmpty(applications)) {
            return;
        }
        //获取Min Max 压测时间 防止metrics 无数据
        long minTime = DateUtil.parseSecondFormatter(reportDetailDTO.getStartTime()).getTime();
        long maxTime = System.currentTimeMillis();
        if(CollectionUtils.isNotEmpty(metrics)) {
            minTime = metrics.stream().map(Metrices::getTime).min((Long::compareTo)).orElse(0L);
            maxTime = metrics.stream().map(Metrices::getTime).max((Long::compareTo)).orElse(0L);
        }else {
            if(reportDetailDTO.getEndTime() != null) {
                maxTime = reportDetailDTO.getEndTime().getTime();
            }else if(maxTime - minTime >= 5*60*1000L){
                //追加最大时间查询 5分钟
                maxTime = minTime + 5*60*1000;
            }
        }

        //多往前选5秒
        minTime = minTime - 5 * 1000;
        //多往后选15s
        maxTime = maxTime + 15 * 1000;

        for (String applicationName : applications) {
            //机器信息
            long startTime = System.currentTimeMillis();
            String searchAppIdSql = "select distinct(app_ip) as app_ip from app_base_data " +
                "where time>=" + minTime + "ms and time <= " + maxTime + "ms and tag_app_name = '" + applicationName + "'" +
                // 增加租户
                " and tenant_app_key = '" + WebPluginUtils.traceTenantAppKey() + "'" +
                " and env_code = '" + WebPluginUtils.traceEnvCode() + "'";

            Collection<BaseServerResult> appIds = influxDatabaseManager.query(BaseServerResult.class, searchAppIdSql);
            log.debug("search appIds :{},cost time : {}", searchAppIdSql, System.currentTimeMillis() - startTime);
            if (CollectionUtils.isEmpty(appIds)) {
                continue;
            }
            List<String> hosts = appIds.stream().map(BaseServerResult::getAppIp).collect(Collectors.toList());
            for (String host : hosts) {
                long baseTime = System.currentTimeMillis();
                String searchBaseSql = "select time, app_ip, cpu_rate, cpu_load, mem_rate, iowait, net_bandwidth_rate" +
                    " from app_base_data where time>=" + minTime + "ms and time <= " + maxTime
                    + "ms and tag_app_name = '" + applicationName + "'" + " and tag_app_ip = '" + host + "'" +
                    // 增加租户
                    " and tenant_app_key = '" + WebPluginUtils.traceTenantAppKey() + "'" +
                    " and env_code = '" + WebPluginUtils.traceEnvCode() + "'";
                Collection<BaseServerResult> bases = influxDatabaseManager.query(BaseServerResult.class, searchBaseSql);
                log.debug("search baseSql :{},cost time = {} ", searchBaseSql, System.currentTimeMillis() - baseTime);
                TpsTargetArray array = calcTpsTarget(metrics, bases);
                if (array == null) {
                    continue;
                }
                ReportMachineUpdateParam reportMachine = new ReportMachineUpdateParam();
                reportMachine.setReportId(reportId);
                reportMachine.setApplicationName(applicationName);
                reportMachine.setMachineIp(host);
                reportMachine.setMachineTpsTargetConfig(JSON.toJSONString(array));
                reportMachineDAO.updateTpsTargetConfig(reportMachine);
            }
        }
    }

    private TpsTargetArray calcTpsTarget(List<Metrices> metrics, Collection<BaseServerResult> vos) {
        if (CollectionUtils.isEmpty(vos)) {
            return null;
        }
        // metric数据为空兼容
        if(CollectionUtils.isEmpty(metrics)) {
            // 展示原数据
            metrics = vos.stream().map(result -> {
                Metrices metrices = new Metrices();
                metrices.setTime(result.getTime().toEpochMilli());
                metrices.setAvgTps(0D);
                return metrices;
            }).collect(Collectors.toList());
        }
        List<BaseServerResult> bases = Lists.newArrayList(vos);
        int currentIndex = 0;
        List<TpsTarget> targets = Lists.newArrayList();
        for (Metrices metric : metrics) {
            TpsTarget target = new TpsTarget();
            target.setTime(convertLongToTime(metric.getTime()));
            target.setTps(metric.getAvgTps().intValue());
            for (int j = currentIndex; j < bases.size(); j++) {
                if (bases.get(j).getExtTime() <= metric.getTime()) {
                    continue;
                }
                if (currentIndex < j) {
                    double cpu = bases.subList(currentIndex, j).stream().filter(data -> data.getCpuRate() != null)
                        .mapToDouble(BaseServerResult::getCpuRate).average().orElse(0D);
                    double loading = bases.subList(currentIndex, j).stream().filter(data -> data.getCpuLoad() != null)
                        .mapToDouble(BaseServerResult::getCpuLoad).average().orElse(0D);
                    double memory = bases.subList(currentIndex, j).stream().filter(data -> data.getMemRate() != null)
                        .mapToDouble(BaseServerResult::getMemRate).average().orElse(0D);
                    double io = bases.subList(currentIndex, j).stream().filter(data -> data.getIoWait() != null)
                        .mapToDouble(BaseServerResult::getIoWait).average().orElse(0D);
                    double mbps = bases.subList(currentIndex, j).stream().filter(
                            data -> data.getNetBandWidthRate() != null).mapToDouble(BaseServerResult::getNetBandWidthRate)
                        .average().orElse(0D);

                    target.setCpu(new BigDecimal((int)cpu));
                    target.setLoading(new BigDecimal(loading).setScale(2, RoundingMode.HALF_UP));
                    target.setMemory(new BigDecimal(memory).setScale(2, RoundingMode.HALF_UP));
                    target.setIo(new BigDecimal(io).setScale(2, RoundingMode.HALF_UP));
                    target.setNetwork(new BigDecimal(mbps).setScale(2, RoundingMode.HALF_UP));
                }
                currentIndex = j;
                break;
            }

            if (target.getCpu() != null) {
                targets.add(target);
            }
        }
        if (CollectionUtils.isEmpty(targets)) {
            return null;
        }
        //转化为array
        return convert2TpsTargetArray(targets);
    }

    private Integer convertLong(Long param) {
        if (param == null) {
            return 0;
        }
        return param.intValue();
    }

    private Integer convertBigDecimal(BigDecimal param) {
        if (param == null) {
            return 0;
        }
        return param.intValue();
    }

    private TpsTargetArray convert2TpsTargetArray(List<TpsTarget> tpsList) {
        if (CollectionUtils.isEmpty(tpsList)) {
            return null;
        }

        String[] time = new String[tpsList.size()];

        Integer[] tps = new Integer[tpsList.size()];

        BigDecimal[] cpu = new BigDecimal[tpsList.size()];

        BigDecimal[] loading = new BigDecimal[tpsList.size()];

        BigDecimal[] memory = new BigDecimal[tpsList.size()];

        BigDecimal[] io = new BigDecimal[tpsList.size()];

        BigDecimal[] network = new BigDecimal[tpsList.size()];

        for (int i = 0; i < tpsList.size(); i++) {
            time[i] = tpsList.get(i).getTime();
            tps[i] = tpsList.get(i).getTps();
            cpu[i] = tpsList.get(i).getCpu();
            loading[i] = tpsList.get(i).getLoading();
            memory[i] = tpsList.get(i).getMemory();
            io[i] = tpsList.get(i).getIo();
            network[i] = tpsList.get(i).getNetwork();
        }
        TpsTargetArray array = new TpsTargetArray();
        array.setTime(time);
        array.setTps(tps);
        array.setCpu(cpu);
        array.setLoading(loading);
        array.setMemory(memory);
        array.setIo(io);
        array.setNetwork(network);
        return array;
    }

    private String convertLongToTime(Long time) {
        if (time == null) {
            return null;
        }
        String date = DateUtils.transferDate(time);
        if (StringUtils.length(date) == 19) {
            return date.substring(11);
        }
        return date;
    }
}
