package io.shulie.takin.web.biz.service.risk.impl;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.pamirs.takin.common.zk.FormatUtils;

import com.pamirs.takin.entity.domain.dto.report.ReportDetailDTO;
import com.pamirs.takin.entity.domain.entity.linkmanage.structure.Category;

import com.pamirs.takin.entity.domain.risk.BaseAppVo;
import com.pamirs.takin.entity.domain.risk.LinkCount;
import com.pamirs.takin.entity.domain.risk.Metrices;
import com.pamirs.takin.entity.domain.risk.ReportLinkDetail;
import io.shulie.takin.web.biz.pojo.response.linkmanage.BusinessLinkResponse;
import io.shulie.takin.web.biz.service.linkmanage.LinkManageService;
import io.shulie.takin.web.biz.service.report.impl.ReportDataCache;
import io.shulie.takin.web.biz.service.risk.ProblemAnalysisService;
import io.shulie.takin.web.biz.service.risk.util.DateUtil;
import io.shulie.takin.web.biz.service.scene.ApplicationBusinessActivityService;
import io.shulie.takin.web.biz.utils.LinkDataCalcUtil;
import io.shulie.takin.web.biz.utils.VolumnUtil;
import io.shulie.takin.web.common.enums.config.ConfigServerKeyEnum;
import io.shulie.takin.web.data.common.InfluxDatabaseManager;
import io.shulie.takin.web.data.dao.application.ApplicationNodeDAO;
import io.shulie.takin.web.data.dao.baseserver.BaseServerDao;
import io.shulie.takin.web.data.dao.report.ReportBottleneckInterfaceDAO;
import io.shulie.takin.web.data.dao.report.ReportMachineDAO;
import io.shulie.takin.web.data.param.application.ApplicationNodeQueryParam;
import io.shulie.takin.web.data.param.baseserver.BaseServerParam;
import io.shulie.takin.web.data.param.baseserver.InfluxAvgParam;
import io.shulie.takin.web.data.param.baseserver.ProcessBaseRiskParam;
import io.shulie.takin.web.data.param.baseserver.TimeMetricsDetailParam;
import io.shulie.takin.web.data.param.baseserver.TimeMetricsParam;
import io.shulie.takin.web.data.param.report.ReportBottleneckInterfaceCreateParam;
import io.shulie.takin.web.data.param.report.ReportMachineUpdateParam;
import io.shulie.takin.web.data.result.baseserver.BaseServerResult;
import io.shulie.takin.web.data.result.baseserver.InfluxAvgResult;
import io.shulie.takin.web.data.result.baseserver.LinkDetailResult;
import io.shulie.takin.web.data.result.report.ReportMachineResult;
import io.shulie.takin.web.data.result.risk.BaseRiskResult;
import io.shulie.takin.web.data.result.risk.LinkDataResult;
import io.shulie.takin.web.data.util.ConfigServerHelper;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author xingchen
 * @ClassName: BaseRiskServiceImpl
 * @Package: io.shulie.takin.report.service.impl
 * @date 2020/7/2717:26
 */
@Service(value = "problemAnalysisService")
public class ProblemAnalysisServiceImpl implements ProblemAnalysisService {
    public static final String UNKNOW_HTTP = "??????(HTTP)";
    public static final String UNKNOW_RPC = "??????(RPC)";
    private static final Logger logger = LoggerFactory.getLogger(ProblemAnalysisServiceImpl.class);

    @Autowired
    private BaseServerDao baseServerDao;
    @Autowired
    private LinkManageService linkManageService;
    @Autowired
    private ApplicationBusinessActivityService applicationBusinessActivityService;
    @Autowired
    private ReportDataCache reportDataCache;
    @Autowired
    private LinkDataCalcUtil linkDataCalcUtil;
    private final ThreadLocal<Integer> totalCountLocal = new ThreadLocal<>();
    private final ThreadLocal<Double> firstRt = new ThreadLocal<>();
    @Autowired
    private ReportMachineDAO reportMachineDAO;
    @Autowired
    private ReportBottleneckInterfaceDAO reportBottleneckInterfaceDAO;

    @Autowired
    private InfluxDatabaseManager influxDatabaseManager;

    @Autowired
    private ApplicationNodeDAO applicationNodeDAO;

    /**
     * ??????????????????????????????
     */
    @Override
    public void syncMachineData(Long reportId) {
        long startTime = System.currentTimeMillis();
        long endTime = System.currentTimeMillis();
        ReportDetailDTO dto = reportDataCache.getReportDetailDTO(reportId);
        if (dto == null) {
            return;
        }
        if (StringUtils.isNotBlank(dto.getStartTime())) {
            startTime = DateUtil.parseSecondFormatter(dto.getStartTime()).getTime();
        }
        // ?????????????????? ???5????????????
        int riskTime = ConfigServerHelper.getIntegerValueByKey(ConfigServerKeyEnum.TAKIN_RISK_COLLECT_TIME);
        if (endTime - startTime >= riskTime) {
            startTime = endTime - riskTime;
        }

        final long sTime = formatTimestamp(startTime);
        final long eTime = formatTimestamp(endTime);

        List<BaseAppVo> baseAppVoList = Lists.newArrayList();

        /**
         * ????????????????????????????????????
         */
        List<String> appNameList = reportDataCache.getApplications(reportId);

        //?????????????????????agentId --> ????????????amdb?????????
        ApplicationNodeQueryParam param = new ApplicationNodeQueryParam();
        param.setApplicationNames(appNameList);
        List<String> onlineAgentIds = applicationNodeDAO.getOnlineAgentIds(param);
        appNameList.forEach(appName -> {
            Collection<BaseServerResult> baseList = baseServerDao.queryBaseServer(new BaseServerParam(sTime, eTime, appName));
            if (CollectionUtils.isNotEmpty(baseList)) {
                logger.debug("??????{}???????????????{},?????????????????????{}-{},???influx???????????????????????????:{}", dto.getId(), appName, sTime, eTime, baseList.size());
                List<BaseAppVo> tmpList = baseList.stream().map(base -> {
                    BaseAppVo vo = new BaseAppVo();
                    vo.setCore(formatDouble(base.getCpuCores()).intValue());
                    vo.setDisk(formatDouble(base.getDisk()));
                    vo.setMbps(formatDouble(base.getNetBandwidth()));
                    vo.setMemory(formatDouble(base.getMemory()));
                    vo.setAppIp(base.getTagAppIp());
                    vo.setAppName(appName);
                    vo.setReportId(reportId);
                    vo.setAgentIp(base.getTagAgentId());
                    return vo;
                }).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(tmpList)) {
                    baseAppVoList.addAll(tmpList);
                }
            } else {
                logger.debug("??????{}???????????????{},?????????????????????{}-{},???influx??????????????????????????????", dto.getId(), appName, sTime, eTime);
            }
        });

        // ??????????????????
        if (CollectionUtils.isNotEmpty(baseAppVoList)) {

            Map<String, List<BaseAppVo>> appMap = baseAppVoList.stream().collect(Collectors.groupingBy(this::fetchApp));

            List<ReportMachineUpdateParam> insertList = Lists.newArrayList();
            appMap.values().forEach(value -> {

                if(CollectionUtils.isEmpty(value)) {
                    return;
                }
                BaseAppVo baseAppVo = value.stream().filter(base -> {
                    // agentId ???????????? ??????????????????
                    if(CollectionUtils.isNotEmpty(onlineAgentIds) && !onlineAgentIds.contains(base.getAgentIp())) {
                        return false;
                    }
                    return true;
                }).collect(Collectors.toList()).get(0);
                if (baseAppVo != null) {
                    ReportMachineUpdateParam tmp = new ReportMachineUpdateParam();
                    tmp.setReportId(baseAppVo.getReportId());
                    tmp.setMachineIp(baseAppVo.getAppIp());
                    tmp.setApplicationName(baseAppVo.getAppName());
                    tmp.setRiskFlag(0);
                    tmp.setAgentId(baseAppVo.getAgentIp());
                    /**
                     * ???????????? ?????? ??????
                     */
                    baseAppVo.setDisk(VolumnUtil.convertByte2Gb(baseAppVo.getDisk()));
                    baseAppVo.setMemory(VolumnUtil.convertByte2Gb(baseAppVo.getMemory()));

                    //?????????????????????
                    baseAppVo.setAppIp(null);
                    baseAppVo.setAppName(null);
                    baseAppVo.setReportId(null);
                    baseAppVo.setAgentIp(null);
                    tmp.setMachineBaseConfig(JSON.toJSONString(baseAppVo));
                    // ????????????
                    WebPluginUtils.transferTenantParam(WebPluginUtils.traceTenantCommonExt(),tmp);
                    insertList.add(tmp);
                }
            });
            if (CollectionUtils.isNotEmpty(insertList)) {
                // machineTpsTargetConfig ?????????????????????
                insertList.forEach(reportMachineDAO::insertOrUpdate);
            }
        }
    }

    /**
     * ??????????????????
     */
    @Override
    public void checkRisk(Long reportId) {
        List<BaseRiskResult> riskVoList = this.processRisk(reportId);
        if (CollectionUtils.isEmpty(riskVoList)) {
            return;
        }

        //????????????????????????
        riskVoList.forEach(vo -> {
            ReportMachineUpdateParam reportMachine = new ReportMachineUpdateParam();
            reportMachine.setReportId(vo.getReportId());
            reportMachine.setApplicationName(vo.getAppName());
            reportMachine.setMachineIp(vo.getAppIp());
            reportMachine.setRiskFlag(1);
            //reportMachine.setRiskValue();
            if(StringUtils.isNotBlank(vo.getContent())) {
                reportMachine.setRiskContent(vo.getContent());
            }
            reportMachineDAO.updateRiskContent(reportMachine);
        });
    }

    /**
     * ??????????????????ID??????????????????
     *
     * @return
     */
    @Override
    public List<BaseRiskResult> processRisk(Long reportId) {
        /**
         * 1???????????????ID???????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
         * 2??????????????????????????????????????????????????????tps?????????tps
         *  2-1???????????????????????????????????????????????????
         *  2-2??????????????????????????????????????????????????????
         *  2-3??????????????????????????????????????????
         *  2-4???????????????tps??????????????????tps
         *      2-4-1?????????????????????tps
         */
        List<BaseRiskResult> results = Lists.newArrayList();
        long startTime = System.currentTimeMillis();
        long endTime = System.currentTimeMillis();
        ReportDetailDTO dto = reportDataCache.getReportDetailDTO(reportId);
        if (StringUtils.isNotBlank(dto.getStartTime())) {
            startTime = DateUtil.parseSecondFormatter(dto.getStartTime()).getTime();
        }

        if (dto.getEndTime() != null) {
            endTime = dto.getEndTime().getTime();
        }
        // ????????????????????????
        if (CollectionUtils.isEmpty(dto.getBusinessActivity())) {
            return results;
        }

        final long sTime = formatTimestamp(startTime);
        final long eTime = formatTimestamp(endTime);
        dto.getBusinessActivity().forEach(ba -> {
            Long businessActivityId = ba.getBusinessActivityId();
            BigDecimal maxTps = ba.getMaxTps() != null ? ba.getMaxTps() : new BigDecimal("0");
            // ??????TPS
            BigDecimal destTps = BigDecimal.valueOf(
                    Double.parseDouble(ObjectUtils.toString(ba.getTps().getValue(), "0")));
            // ????????????????????????????????????????????????
            List<String> appNameList = applicationBusinessActivityService.processAppNameByBusinessActiveId(
                    businessActivityId);

            /**
             * ??????????????????TPS
             */
            List<BaseRiskResult> tmpList;
            if (maxTps.compareTo(destTps) > 0) {
                tmpList = processOverRisk(appNameList, dto.getSceneId(), reportId, destTps);
            } else {
                tmpList = processBaseRisk(appNameList, sTime, eTime, reportId);
            }
            if (CollectionUtils.isNotEmpty(tmpList)) {
                results.addAll(tmpList);
            }
        });

        return results;
    }

    /**
     * ????????????,??????????????????????????????????????????tps???rt
     */
    @Override
    public void processBottleneck(Long reportId) {
        long startTime = System.currentTimeMillis();
        long endTime = System.currentTimeMillis();
        ReportDetailDTO dto = reportDataCache.getReportDetailDTO(reportId);
        // ????????????????????????
        if (Objects.isNull(dto) || CollectionUtils.isEmpty(dto.getBusinessActivity())) {
            return;
        }
        if (StringUtils.isNotBlank(dto.getStartTime())) {
            startTime = DateUtil.parseSecondFormatter(dto.getStartTime()).getTime();
        }
        if (dto.getEndTime() != null) {
            endTime = dto.getEndTime().getTime();
        }

        final long sTime = formatTimestamp(startTime);
        final long eTime = formatTimestamp(endTime);
        List<LinkDataResult> bottleneckList = Lists.newArrayList();
        dto.getBusinessActivity().stream().forEach(ba -> {
            List<LinkDataResult> linkDataResultList = processLinkDataById(ba.getBusinessActivityId(), sTime, eTime);
            if (CollectionUtils.isNotEmpty(linkDataResultList)) {
                //??????????????????
                LinkCount linkCount = new LinkCount();
                linkDataCalcUtil.calcLinkWeight(linkCount, null, linkDataResultList);
                //??????????????????
                BigDecimal avg = linkDataCalcUtil.calcAvgRate(linkCount.getCount());
                linkDataCalcUtil.getBottleneckInterfaces(linkDataResultList, linkCount.getCount(), avg, bottleneckList);
            }
        });
        //?????????????????????
        if (CollectionUtils.isEmpty(bottleneckList)) {
            return;
        }
        bottleneckList.sort(((o1, o2) -> {
            if (o1.getRealWeight().compareTo(o2.getRealWeight()) > 0) {
                return -1;
            } else if (o1.getRealWeight().compareTo(o2.getRealWeight()) < 0) {
                return 1;
            }
            return 0;
        }));
        //????????????
        List<ReportBottleneckInterfaceCreateParam> recordList = Lists.newArrayList();
        Set<String> sets = Sets.newHashSet();
        int sortNo = 1;
        for (int i = 0; i < bottleneckList.size(); i++) {
            LinkDataResult data = bottleneckList.get(i);
            String value = data.getAppName() + "|" + data.getEvent();
            if (sets.contains(value)) {
                return;
            }
            sets.add(value);
            ReportBottleneckInterfaceCreateParam record = new ReportBottleneckInterfaceCreateParam();
            record.setReportId(reportId);
            record.setSortNo(sortNo);
            record.setApplicationName(data.getAppName());
            record.setInterfaceType(data.getEventType());
            record.setInterfaceName(data.getEvent());
            record.setTps(BigDecimal.valueOf(data.getTps()));
            record.setRt(BigDecimal.valueOf(data.getRt()));
            record.setErrorReqs(data.getErrorCount());
            record.setNodeCount(data.getNodeCount());
            record.setBottleneckWeight(data.getRealWeight());
            record.setTenantId(WebPluginUtils.traceTenantId());
            record.setEnvCode(WebPluginUtils.traceEnvCode());
            recordList.add(record);
            sortNo++;
        }
        reportBottleneckInterfaceDAO.insertBatch(recordList);
    }

    /**
     * ??????????????????ID???????????????????????????????????????
     *
     * @param startTime ?????????
     *
     * @return
     */
    @Override
    public ReportLinkDetail queryLinkDetail(Long businessActivityId, Long startTime, Long endTime) {
        ReportLinkDetail detail = new ReportLinkDetail();
        BusinessLinkResponse businessLinkResponse = linkManageService.getBussisnessLinkDetail(
                String.valueOf(businessActivityId));
        if (businessLinkResponse == null || StringUtils.isBlank(businessLinkResponse.getLinkName())) {
            return detail;
        }
        String node = businessLinkResponse.getTechLinkResponse().getBody_after();
        if (StringUtils.isBlank(node)) {
            node = businessLinkResponse.getTechLinkResponse().getBody_before();
            if (StringUtils.isBlank(node)) {
                return detail;
            }
        }

        Category category = JSON.parseObject(node, Category.class);
        if (category == null || StringUtils.isBlank(category.getApplicationName())) {
            return detail;
        }
        removeUnknownNode(category);

        List<ReportLinkDetail> parent = Lists.newArrayList();
        /**
         * ???????????????????????????traceID
         */
        long sTime = formatTimestamp(startTime);
        long eTime = formatTimestamp(endTime);
        String traceId = queryTraceId(category.getApplicationName(), sTime, eTime, category.getEvent());
        Map<String, String> aggMap = Maps.newHashMap();
        buildLinkDetail(true, category.getApplicationName(), parent, Arrays.asList(category), sTime, eTime, traceId,
                aggMap);

        if (CollectionUtils.isNotEmpty(parent)) {
            detail = parent.get(0);
        }
        return detail;
    }

    /**
     * ??????????????????
     */
    private Category removeUnknownNode(Category category) {
        if (null == category) {
            return category;
        }
        String appName = category.getApplicationName();
        if (UNKNOW_HTTP.equals(appName) || UNKNOW_RPC.equals(appName)) {
            category = null;
            return category;
        }
        if (null == category.getChildren() || category.getChildren().isEmpty()) {
            return category;
        }
        Iterator<Category> iterator = category.getChildren().iterator();
        while (iterator.hasNext()) {
            Category child = removeUnknownNode(iterator.next());
            if (null == child) {
                iterator.remove();
            }
        }
        return category;
    }

    private String queryTraceId(String appName, Long sTime, Long eTime, String event) {
        String traceId = "";
        InfluxAvgParam influxAvgParam = new InfluxAvgParam();
        influxAvgParam.setSTime(sTime);
        influxAvgParam.setETime(eTime);
        influxAvgParam.setAppName(appName);
        influxAvgParam.setEvent(event);
        Collection<InfluxAvgResult> influxAvgVoList = baseServerDao.queryTraceId(influxAvgParam);
        if (CollectionUtils.isEmpty(influxAvgVoList)) {
            return traceId;
        }
        Collection<InfluxAvgResult> sortedList = influxAvgVoList.stream().sorted(
                Comparator.comparingDouble(InfluxAvgResult::getRt)).collect(Collectors.toList());
        return sortedList.stream().findFirst().get().getTraceId();
    }

    private void buildLinkDetail(boolean isParent, String invokeName, List<ReportLinkDetail> parent,
                                 List<Category> subList,
                                 long stime, long eTime, String traceId, Map<String, String> aggMap) {
        if (CollectionUtils.isEmpty(subList)) {
            return;
        }

        for (int i = 0; i < subList.size(); i++) {
            Category tmp = subList.get(i);

            ReportLinkDetail data = new ReportLinkDetail();
            data.setAppName(tmp.getApplicationName());
            data.setEvent(tmp.getEvent());
            if (aggMap.containsKey(invokeName + tmp.getEvent())) {
                continue;
            } else {
                aggMap.put(invokeName + tmp.getEvent(), "");
            }
            data.setServiceName(tmp.getServiceDetail());
            data.setEvent(tmp.getEvent());
            // ????????????????????????metrics??????
            LinkDetailResult tmpDetail = queryTimeMetricsDetail(invokeName, tmp.getEvent(), tmp.getServiceType(),
                    tmp.getApplicationName(), stime, eTime);
            data.setEventType(tmp.getServiceType());
            data.setMinRt((tmpDetail.getMinRt() == null || tmpDetail.getMinRt() == 0) ?
                    "< 1ms" : FormatUtils.humanReadableTimeSpan(tmpDetail.getMinRt().longValue()));
            data.setMaxRt((tmpDetail.getMaxRt() == null || tmpDetail.getMaxRt() == 0) ?
                    "< 1ms" : FormatUtils.humanReadableTimeSpan(tmpDetail.getMaxRt().longValue()));
            data.setAvgRt(tmpDetail.getAvgRt() == null ? 0L : tmpDetail.getAvgRt());
            data.setTotalCount(tmpDetail.getTotalCount());
            data.setUuid(UUID.randomUUID().toString());
            data.setTps(tmpDetail.getTps());
            if (isParent) {
                data.setRequestRate(1.0D);
                totalCountLocal.set(data.getTotalCount());
                firstRt.set(tmpDetail.getAvgRt());
            } else {
                Integer totalCount = totalCountLocal.get();
                Integer totalDetail = tmpDetail.getTotalCount();
                if (totalCount == 0) {
                    data.setRequestRate(0D);
                } else {
                    data.setRequestRate(formatDouble(totalDetail.doubleValue() / totalCount).doubleValue());
                }

                // TODO ?????????????????????rt,??????????????????????????????????????????
                if (tmpDetail.getAvgRt() > firstRt.get()) {
                    data.setAvgRt(firstRt.get());
                }
            }
            data.setTraceId(traceId);
            if (StringUtils.isNotBlank(tmpDetail.getAppName())) {
                data.setAppName(tmpDetail.getAppName());
            }
            parent.add(data);

            // ???????????????
            List<Category> sub = tmp.getChildren();
            if (CollectionUtils.isNotEmpty(sub)) {
                data.setChildren(Lists.newArrayList());
                buildLinkDetail(false, data.getAppName(), data.getChildren(), sub, stime, eTime, traceId, aggMap);
            }
        }
    }

    /**
     * ??????????????????Id??????????????????
     *
     * @return
     */
    private List<LinkDataResult> processLinkDataById(Long businessActivityId, long sTime, long eTime) {
        if (businessActivityId == null || businessActivityId <= 0){
            return null;
        }
        List<LinkDataResult> linkDataResultList = Lists.newArrayList();
        BusinessLinkResponse businessLinkResponse = linkManageService.getBussisnessLinkDetail(
                String.valueOf(businessActivityId));
        if (businessLinkResponse == null || StringUtils.isBlank(businessLinkResponse.getLinkName())) {
            return linkDataResultList;
        }

        String node = businessLinkResponse.getTechLinkResponse().getBody_after();
        if (StringUtils.isBlank(node)) {
            node = businessLinkResponse.getTechLinkResponse().getBody_before();
            if (StringUtils.isBlank(node)) {
                return linkDataResultList;
            }
        }

        Category category = JSON.parseObject(node, Category.class);
        if (category == null || StringUtils.isBlank(category.getApplicationName())) {
            return linkDataResultList;
        }
        buildLink(category.getApplicationName(), linkDataResultList, category.getChildren(), sTime, eTime);
        return linkDataResultList;
    }

    private void buildLink(String invokeName, List<LinkDataResult> parent, List<Category> subList,
                           long stime, long eTime) {
        if (CollectionUtils.isEmpty(subList)) {
            return;
        }

        for (Category tmp : subList) {
            LinkDataResult data = new LinkDataResult();
            data.setAppName(tmp.getApplicationName());
            data.setEvent(tmp.getEvent());
            data.setServiceName(tmp.getServiceDetail());
            // ????????????????????????metrics??????
            LinkDataResult tmpData = queryTimeMetrics(invokeName, tmp.getEvent(), tmp.getServiceType(),
                    tmp.getApplicationName(), stime, eTime);
            data.setEventType(tmp.getServiceType());
            data.setRt(tmpData.getRt());
            data.setTps(tmpData.getTps());
            data.setErrorCount(tmpData.getErrorCount());
            if (StringUtils.isNotBlank(tmpData.getAppName())) {
                data.setAppName(tmpData.getAppName());
            }
            parent.add(data);

            // ???????????????
            List<Category> sub = tmp.getChildren();
            if (CollectionUtils.isNotEmpty(sub)) {
                buildLink(data.getAppName(), data.getSubLink(), sub, stime, eTime);
            }
        }
    }

    /**
     * ?????????????????????metircs
     *
     * @return
     */
    private LinkDetailResult queryTimeMetricsDetail(String invokeApp, String event, String rpcType, String appName,
                                                    long sTime, long eTime) {
        TimeMetricsDetailParam param = new TimeMetricsDetailParam();
        param.setAppName(appName);
        param.setETime(eTime);
        param.setSTime(sTime);
        param.setInvokeApp(invokeApp);
        param.setEvent(event);
        param.setRpcType(rpcType);
        return baseServerDao.queryTimeMetricsDetail(param);
    }

    /**
     * ?????????????????????metircs
     *
     * @return
     */
    private LinkDataResult queryTimeMetrics(String invokeApp, String event, String rpcType, String appName, long sTime,
                                            long eTime) {
        TimeMetricsParam param = new TimeMetricsParam();
        param.setAppName(appName);
        param.setETime(eTime);
        param.setSTime(sTime);
        param.setInvokeApp(invokeApp);
        param.setEvent(event);
        param.setRpcType(rpcType);
        return baseServerDao.queryTimeMetrics(param);
    }

    /**
     * ??????????????????,???????????????????????????F
     * [1.1]?????????????????????????????????
     * [1.2]???????????????????????????TPS?????????????????????TPS???
     * [1.2.1]?????????????????????CPU/load/??????/??????IO/??????????????????????????????????????????80%???
     * [1.2.1.1]??????????????????????????????????????????
     * [1.2.1.2]???????????????????????????
     * [1.2.2]????????????????????????TPS???????????????5%?????????CPU/load/??????/??????IO/?????????????????????????????????80%???
     * [1.2.2.1]???????????????????????????
     * [1.2.2.2]??????????????????????????????????????????
     *
     * @return
     */
    private List<BaseRiskResult> processBaseRisk(List<String> appNames, long startTime, long endTime, Long
            reportId) {

        ProcessBaseRiskParam param = new ProcessBaseRiskParam();
        param.setAppNames(appNames);
        param.setEndTime(endTime);
        param.setReportId(reportId);
        param.setStartTime(startTime);
        return baseServerDao.queryProcessBaseRisk(param);

    }

    /**
     * ??????tps?????????
     *
     * @return
     */
    private List<BaseRiskResult> processOverRisk(List<String> appNames, Long sceneId, Long reportId, BigDecimal
            destTps) {
        List<BaseRiskResult> results = Lists.newArrayList();
        if (CollectionUtils.isEmpty(appNames)) {
            return results;
        }
        // 1??????????????????tps???cpu
        List<Metrices> metrices = reportDataCache.listAllMetricsData(reportId);
        if (CollectionUtils.isEmpty(metrices)) {
            logger.error("metrices is null,{}", reportId);
            return results;
        }
        // ??????metrices ?????????????????????????????????????????????????????????cpu??????
        List<Metrices> sortMetrics = metrices.stream().sorted(Comparator.comparing(Metrices::getTime)).collect(
                Collectors.toList());
        Metrices firstMetrices = sortMetrics.get(0);
        Metrices lastMetrices = sortMetrics.get(sortMetrics.size() - 1);
        // ???60??????????????????
        long firstTime = formatTimestamp(firstMetrices.getTime() - 60 * 1000);
        long lastTime = formatTimestamp(lastMetrices.getTime() + 60 * 1000);

        final List<Metrices> metricesList = metrices;

        appNames.forEach(appName -> {
            String ipSql = "select distinct(app_ip) as app_ip from app_base_data where app_name = '" + appName
                    + "' and time > " + firstTime + " and time <= " + lastTime +
                // ????????????
                " and tenant_app_key = '" + WebPluginUtils.traceTenantAppKey() + "'" +
                " and env_code = '" + WebPluginUtils.traceEnvCode() + "'";
            Collection<BaseServerResult> ipList = influxDatabaseManager.query(BaseServerResult.class, ipSql);
            if (CollectionUtils.isNotEmpty(ipList)) {
                // ???????????????Metrices
                List<BaseServerResult> calMetricesList = Lists.newArrayList();

                ipList.forEach(ip -> {
                    String tmpSql =
                            "select mean(cpu_rate) as cpu_rate,mean(cpu_load) as cpu_load,mean(mem_rate) as mem_rate,mean"
                                    + "(iowait) as iowait,mean(net_bandwidth_rate) as net_bandwidth_rate from app_base_data"
                                    + " where tag_app_name = '" + appName + "' and tag_app_ip = '" + ip.getAppIp()
                                    + "' and time > " + firstTime + " and time <= " + lastTime +
                                    // ????????????
                                    " and tenant_app_key = '" + WebPluginUtils.traceTenantAppKey() + "'" +
                                    " and env_code = '" + WebPluginUtils.traceEnvCode() + "'"
                                    + " group by time(5s) order by time";
                    Collection<BaseServerResult> voList = influxDatabaseManager.query(BaseServerResult.class, tmpSql);
                    if (CollectionUtils.isNotEmpty(voList)) {
                        // ????????????
                        Map<Long, List<BaseServerResult>> voMap = voList.stream().collect(
                                Collectors.groupingBy(BaseServerResult::getExtTime));
                        metricesList.forEach(metricesTmp -> {
                            BaseServerResult tmpVo = new BaseServerResult();
                            /**
                             * TODO influxDB??????????????????,?????????????????????????????????-????????????????????????TPS?????????????????????5????????????
                             *
                             * tps ?????? 0-5??????tps????????????5???
                             * infludb 0-5??????????????????0????????????
                             * ??????????????????????????????-5????????????
                             */
                            Long time = metricesTmp.getTime() - 5 * 1000;
                            List<BaseServerResult> tmpBaseVoList = voMap.get(time);
                            if (CollectionUtils.isNotEmpty(tmpBaseVoList)) {
                                tmpVo = tmpBaseVoList.stream().findFirst().get();
                                tmpVo.setTps(metricesTmp.getAvgTps());
                                tmpVo.setTime(Instant.ofEpochMilli(metricesTmp.getTime()));
                                tmpVo.setAppIp(ip.getAppIp());
                                tmpVo.setAppName(appName);

                                // ?????????
                                calMetricesList.add(tmpVo);
                            }
                        });

                        // ??????????????????
                        List<BaseRiskResult> tmpRiskList = matchRisk(calMetricesList, destTps, reportId);
                        if (CollectionUtils.isNotEmpty(tmpRiskList)) {
                            results.addAll(tmpRiskList);
                        }
                    }
                });
            }
        });
        return results;

    }

    /**
     * ??????????????????
     *
     * @return
     */
    private List<BaseRiskResult> matchRisk(Collection<BaseServerResult> voList, BigDecimal destTps, Long reportId) {
        List<BaseRiskResult> riskVoList = Lists.newArrayList();
        if (CollectionUtils.isEmpty(voList)) {
            return riskVoList;
        }
        /*
         * ?????????tps??????
         * 1???????????????tps???????????????,???????????????????????????tps???cpu??????
         * 2???????????????,??????tps ???5%??????????????????
         */
        BaseServerResult tmpVo = voList.stream().findFirst().orElse(new BaseServerResult());
        String appIp = tmpVo.getAppIp();
        String appName = tmpVo.getAppName();

        // ?????????
        Collection<BaseServerResult> sortedList = voList.stream()
                .filter(sort -> sort.getTps() != null)
                .sorted(Comparator.comparingDouble(BaseServerResult::getTps))
                .collect(Collectors.toList());
        // ??????????????????TPS?????????
        List<BaseServerResult> lessList = sortedList.stream()
                .filter(sort -> sort.getTps() != null)
                .filter(sort -> sort.getTps().compareTo(destTps.doubleValue()) < 0)
                .collect(Collectors.toList());

        // ?????? cpu ?????????
        double scale = Double.parseDouble(
            ConfigServerHelper.getValueByKey(ConfigServerKeyEnum.TAKIN_RISK_MAX_NORM_SCALE));

        // ?????? cpu load
        int maxLoad = Integer.parseInt(
            ConfigServerHelper.getValueByKey(ConfigServerKeyEnum.TAKIN_RISK_MAX_NORM_MAX_LOAD));

        if (CollectionUtils.isNotEmpty(lessList)) {
            BaseRiskResult risk = new BaseRiskResult();
            risk.setAppIp(appIp);
            risk.setAppName(appName);
            risk.setReportId(reportId);

            // ??????cpu?????????
            BaseServerResult cpuRate = lessList.stream()
                    .filter(less -> less.getCpuRate() != null)
                    .max(Comparator.comparing(BaseServerResult::getCpuRate))
                    .orElse(new BaseServerResult());

            StringBuilder stringBuilder = new StringBuilder();
            if (cpuRate.getCpuRate() != null && cpuRate.getCpuRate() >= scale) {
                stringBuilder.append("cpu???????????????")
                        .append(formatDouble(cpuRate.getCpuRate()))
                        .append("%;");
            }
            // ??????load
            BaseServerResult cpuLoad = lessList.stream()
                    .filter(less -> less.getCpuLoad() != null)
                    .max(Comparator.comparing(BaseServerResult::getCpuLoad))
                    .orElse(new BaseServerResult());
            if (cpuLoad.getCpuLoad() != null && cpuLoad.getCpuLoad() >= maxLoad) {
                stringBuilder.append("cpuLoad??????")
                        .append(formatDouble(cpuLoad.getCpuLoad()))
                        .append("; ");
            }
            // ?????????????????????
            BaseServerResult memRate = lessList.stream()
                    .filter(less -> less.getMemRate() != null)
                    .max(Comparator.comparing(BaseServerResult::getMemRate))
                    .orElse(new BaseServerResult());
            if (memRate.getMemRate() != null && memRate.getMemRate() >= scale) {
                stringBuilder.append("???????????????????????????")
                        .append(formatDouble(memRate.getMemRate()))
                        .append("%;");
            }
            // ??????Io?????????
            BaseServerResult ioWait = lessList.stream()
                    .filter(less -> less.getIoWait() != null)
                    .max(Comparator.comparing(BaseServerResult::getIoWait))
                    .orElse(new BaseServerResult());
            if (ioWait.getIoWait() != null && ioWait.getIoWait() >= scale) {
                stringBuilder.append("??????IoWait")
                        .append(formatDouble(ioWait.getIoWait()))
                        .append("%;");
            }
            // ???????????????????????????
            BaseServerResult netBandWidthRate = lessList.stream()
                    .filter(less -> less.getNetBandWidthRate() != null)
                    .max(Comparator.comparing(BaseServerResult::getNetBandWidthRate))
                    .orElse(new BaseServerResult());

            if (netBandWidthRate.getNetBandWidthRate() != null && netBandWidthRate.getNetBandWidthRate() >= scale) {
                stringBuilder.append("???????????????????????????")
                        .append(formatDouble(netBandWidthRate.getNetBandWidthRate()))
                        .append("%;");
            }

            /*
             * ????????????????????????
             */
            String content = stringBuilder.toString();
            if (StringUtils.isNotBlank(content)) {
                risk.setContent(content.substring(0, content.lastIndexOf(";")));
                riskVoList.add(risk);
            }
        }

        /*
         * ????????????tps?????????
         */
        List<BaseServerResult> moreList = sortedList.stream()
                .filter(sort -> sort.getTps() != null)
                .filter(sort -> sort.getTps().compareTo(destTps.doubleValue()) > 0)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(moreList)) {
            /*
             * ????????????5%???tps??????
             */
            BigDecimal mid = destTps.multiply(BigDecimal.valueOf(0.05));
            final BigDecimal max = formatDouble(mid.add(destTps).doubleValue());
            final BigDecimal min = formatDouble(destTps.subtract(mid).doubleValue());

            // ?????????tps?????????cpu???load??????
            List<BaseServerResult> midList = sortedList.stream().filter(sort -> sort.getTps() != null)
                    .filter(sort -> {
                        return BigDecimal.valueOf(sort.getTps()).compareTo(min) >= 0 &&
                                BigDecimal.valueOf(sort.getTps()).compareTo(max) <= 0;
                    }).collect(Collectors.toList());

            if (CollectionUtils.isNotEmpty(midList)) {
                BaseRiskResult risk = new BaseRiskResult();
                risk.setAppIp(appIp);
                risk.setAppName(appName);

                StringBuilder stringBuilder = new StringBuilder();
                // ??????cpu??????????????????
                double cpuRate = midList.stream().filter(Objects::nonNull).mapToDouble(BaseServerResult::getCpuRate)
                        .average().orElse(0D);
                double cpuLoad = midList.stream().filter(Objects::nonNull).mapToDouble(BaseServerResult::getCpuLoad)
                        .average().orElse(0D);
                double cpuMemRate = midList.stream().filter(Objects::nonNull).mapToDouble(BaseServerResult::getMemRate)
                        .average().orElse(0D);
                double ioWait = midList.stream().filter(Objects::nonNull).mapToDouble(BaseServerResult::getIoWait)
                        .average().orElse(0D);
                double netBandWidthRate = midList.stream().filter(Objects::nonNull).mapToDouble(
                                BaseServerResult::getNetBandWidthRate).average()
                        .orElse(0D);

                if (cpuRate >= scale) {
                    stringBuilder.append("cpu???????????????")
                            .append(formatDouble(cpuRate))
                            .append("%;");
                }
                // ??????load
                if (cpuLoad >= maxLoad) {
                    stringBuilder.append("cpuLoad??????")
                            .append(formatDouble(cpuLoad))
                            .append("; ");
                }
                // ?????????????????????
                if (cpuMemRate >= scale) {
                    stringBuilder.append("???????????????????????????")
                            .append(formatDouble(cpuMemRate))
                            .append("%;");
                }
                // ??????Io?????????
                if (ioWait >= scale) {
                    stringBuilder.append("??????IoWait")
                            .append(formatDouble(ioWait))
                            .append("%;");
                }
                // ???????????????????????????
                if (netBandWidthRate >= scale) {
                    stringBuilder.append("???????????????????????????")
                            .append(formatDouble(netBandWidthRate))
                            .append("%;");
                }

                /**
                 * ????????????????????????
                 */
                String content = stringBuilder.toString();
                if (StringUtils.isNotBlank(content)) {
                    risk.setContent(content.substring(0, content.lastIndexOf(";")));
                    riskVoList.add(risk);
                }
            }
        }
        return riskVoList;
    }

    private long formatTimestamp(long timestamp) {
        String temp = timestamp + "000000";
        return Long.parseLong(temp);
    }

    private BigDecimal formatDouble(Double data) {
        if (data == null) {
            return new BigDecimal("0");
        }
        BigDecimal b = BigDecimal.valueOf(data);
        return b.setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    private String fetchApp(BaseAppVo vo) {
        return vo.getReportId() + vo.getAppIp() + vo.getAppName();
    }

    private String fetchRisk(BaseRiskResult vo) {
        return vo.getReportId() + vo.getAppIp() + vo.getAppName();
    }

    private String fetchMachine(ReportMachineResult vo) {
        return vo.getReportId() + vo.getMachineIp() + vo.getApplicationName();
    }
}
