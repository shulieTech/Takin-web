package io.shulie.takin.web.biz.service.report.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.pamirs.takin.common.util.DateUtils;
import com.pamirs.takin.entity.domain.dto.report.ReportDetailDTO;
import com.pamirs.takin.entity.domain.entity.report.TpsTarget;
import com.pamirs.takin.entity.domain.entity.report.TpsTargetArray;
import com.pamirs.takin.entity.domain.risk.Metrices;
import io.shulie.takin.web.biz.service.report.ReportService;
import io.shulie.takin.web.biz.service.risk.util.DateUtil;
import io.shulie.takin.web.data.dao.baseserver.BaseServerDao;
import io.shulie.takin.web.data.dao.report.ReportApplicationSummaryDAO;
import io.shulie.takin.web.data.dao.report.ReportBottleneckInterfaceDAO;
import io.shulie.takin.web.data.dao.report.ReportMachineDAO;
import io.shulie.takin.web.data.dao.report.ReportSummaryDAO;
import io.shulie.takin.web.data.param.baseserver.AppBaseDataQuery;
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

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

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

    @Autowired
    private ReportDataCache reportDataCache;

    @Resource
    private BaseServerDao baseServerDao;

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
            warnCount = convertLong((Long)cloudMap.get("warnCount"));
            businessCount = convertLong((Long)cloudMap.get("count"));
            passBusinessCount = convertBigDecimal((BigDecimal)cloudMap.get("passSum"));
        }

        ReportSummaryCreateParam reportSummary = new ReportSummaryCreateParam();
        reportSummary.setReportId(reportId);
        reportSummary.setBottleneckInterfaceCount(bottleneckInterfaceCount);
        reportSummary.setRiskMachineCount(riskCount);
        reportSummary.setBusinessActivityCount(businessCount);
        reportSummary.setUnachieveBusinessActivityCount(businessCount - passBusinessCount);
        reportSummary.setApplicationCount(appCount);
        reportSummary.setMachineCount(totalCount);
        reportSummary.setWarnCount(warnCount);
        reportSummary.setTenantId(WebPluginUtils.traceTenantId());
        reportSummary.setEnvCode(WebPluginUtils.traceEnvCode());
        reportSummaryDAO.insertOrUpdate(reportSummary);
        log.debug("Build ReportSummary Success, reportId={}", reportId);
    }

    public void calcTpsTarget(Long reportId,Long maxTime) {
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
        if (maxTime == null) {
            maxTime = System.currentTimeMillis();
        }
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
            AppBaseDataQuery query = new AppBaseDataQuery();
            Map<String, String> fieldAndAlias = new HashMap<>();
            fieldAndAlias.put("distinct(app_ip)", "app_ip");
            query.setFieldAndAlias(fieldAndAlias);
            query.setStartTime(minTime);
            query.setEndTime(maxTime);
            query.setAppName(applicationName);
            List<BaseServerResult> appIds = baseServerDao.listBaseServerResult(query);
            log.debug("search appIds ,cost time : {}", System.currentTimeMillis() - startTime);
            if (CollectionUtils.isEmpty(appIds)) {
                continue;
            }
            List<String> hosts = appIds.stream().map(BaseServerResult::getAppIp).collect(Collectors.toList());
            for (String host : hosts) {
                long baseTime = System.currentTimeMillis();
                AppBaseDataQuery searchBaseQuery = new AppBaseDataQuery();
                Map<String, String> searchBaseFieldAndAlias = new HashMap<>();
                searchBaseFieldAndAlias.put("time", "time");
                searchBaseFieldAndAlias.put("app_ip", "app_ip");
                searchBaseFieldAndAlias.put("cpu_rate", "cpu_rate");
                searchBaseFieldAndAlias.put("cpu_load", "cpu_load");
                searchBaseFieldAndAlias.put("mem_rate", "mem_rate");
                searchBaseFieldAndAlias.put("iowait", "iowait");
                searchBaseFieldAndAlias.put("net_bandwidth_rate", "net_bandwidth_rate");
                searchBaseFieldAndAlias.put("young_gc_count", "young_gc_count");
                searchBaseFieldAndAlias.put("young_gc_cost", "young_gc_cost");
                searchBaseFieldAndAlias.put("full_gc_count", "full_gc_count");
                searchBaseFieldAndAlias.put("full_gc_cost", "full_gc_cost");
                searchBaseQuery.setFieldAndAlias(searchBaseFieldAndAlias);
                searchBaseQuery.setStartTime(minTime);
                searchBaseQuery.setEndTime(maxTime);
                searchBaseQuery.setAppName(applicationName);
                searchBaseQuery.setAppId(host);
                List<BaseServerResult> bases = baseServerDao.listBaseServerResult(searchBaseQuery);
                log.debug("search baseSql ,cost time = {} ", System.currentTimeMillis() - baseTime);
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
                metrices.setTime(result.getTime());
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
                    double cpu = bases.subList(currentIndex, j).stream().filter(data -> data.getCpuRate() != null).mapToDouble(BaseServerResult::getCpuRate).average().orElse(0D);
                    double loading = bases.subList(currentIndex, j).stream().filter(data -> data.getCpuLoad() != null).mapToDouble(BaseServerResult::getCpuLoad).average().orElse(0D);
                    double memory = bases.subList(currentIndex, j).stream().filter(data -> data.getMemRate() != null).mapToDouble(BaseServerResult::getMemRate).average().orElse(0D);
                    double io = bases.subList(currentIndex, j).stream().filter(data -> data.getIoWait() != null).mapToDouble(BaseServerResult::getIoWait).average().orElse(0D);
                    double mbps = bases.subList(currentIndex, j).stream().filter(data -> data.getNetBandWidthRate() != null).mapToDouble(BaseServerResult::getNetBandWidthRate).average().orElse(0D);
                    double youngGcCount = bases.subList(currentIndex, j).stream().filter(data -> data.getYoungGcCount() != null).mapToDouble(BaseServerResult::getYoungGcCount).average().orElse(0D);
                    double youngGcCost = bases.subList(currentIndex, j).stream().filter(data -> data.getYoungGcCost() != null).mapToDouble(BaseServerResult::getYoungGcCost).average().orElse(0D);
                    double fullGcCount = bases.subList(currentIndex, j).stream().filter(data -> data.getFullGcCount() != null).mapToDouble(BaseServerResult::getFullGcCount).average().orElse(0D);
                    double fullGcCost = bases.subList(currentIndex, j).stream().filter(data -> data.getFullGcCost() != null).mapToDouble(BaseServerResult::getFullGcCost).average().orElse(0D);

                    target.setCpu(new BigDecimal((int)cpu));
                    target.setLoading(new BigDecimal(loading).setScale(2, RoundingMode.HALF_UP));
                    target.setMemory(new BigDecimal(memory).setScale(2, RoundingMode.HALF_UP));
                    target.setIo(new BigDecimal(io).setScale(2, RoundingMode.HALF_UP));
                    target.setNetwork(new BigDecimal(mbps).setScale(2, RoundingMode.HALF_UP));
                    target.setYoungGcCount(new BigDecimal(Optional.ofNullable(youngGcCount).orElse(0D)).setScale(2, RoundingMode.HALF_UP));
                    target.setYoungGcCost(new BigDecimal(Optional.ofNullable(youngGcCost).orElse(0D)).setScale(2, RoundingMode.HALF_UP));
                    target.setFullGcCount(new BigDecimal(Optional.ofNullable(fullGcCount).orElse(0D)).setScale(2, RoundingMode.HALF_UP));
                    target.setFullGcCost(new BigDecimal(Optional.ofNullable(fullGcCost).orElse(0D)).setScale(2, RoundingMode.HALF_UP));
                    target.setGcCost(target.getYoungGcCost().add(target.getFullGcCost()));
                    target.setGcCount(target.getYoungGcCount().add(target.getFullGcCount()));
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

        BigDecimal[] youngGcCount = new BigDecimal[tpsList.size()];

        BigDecimal[] youngGcCost = new BigDecimal[tpsList.size()];

        BigDecimal[] fullGcCount = new BigDecimal[tpsList.size()];

        BigDecimal[] fullGcCost = new BigDecimal[tpsList.size()];

        BigDecimal[] gcCount = new BigDecimal[tpsList.size()];

        BigDecimal[] gcCost = new BigDecimal[tpsList.size()];

        for (int i = 0; i < tpsList.size(); i++) {
            time[i] = tpsList.get(i).getTime();
            tps[i] = tpsList.get(i).getTps();
            cpu[i] = tpsList.get(i).getCpu();
            loading[i] = tpsList.get(i).getLoading();
            memory[i] = tpsList.get(i).getMemory();
            io[i] = tpsList.get(i).getIo();
            network[i] = tpsList.get(i).getNetwork();
            youngGcCount[i] = tpsList.get(i).getYoungGcCount();
            youngGcCost[i] = tpsList.get(i).getYoungGcCost();
            fullGcCount[i] = tpsList.get(i).getFullGcCount();
            fullGcCost[i] = tpsList.get(i).getFullGcCost();
            gcCount[i] = tpsList.get(i).getGcCount();
            gcCost[i] = tpsList.get(i).getGcCost();
        }
        TpsTargetArray array = new TpsTargetArray();
        array.setTime(time);
        array.setTps(tps);
        array.setCpu(cpu);
        array.setLoading(loading);
        array.setMemory(memory);
        array.setIo(io);
        array.setNetwork(network);
        array.setYoungGcCount(youngGcCount);
        array.setYoungGcCost(youngGcCost);
        array.setFullGcCount(fullGcCount);
        array.setFullGcCost(fullGcCost);
        array.setGcCount(gcCount);
        array.setGcCost(gcCost);
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
