package io.shulie.takin.web.biz.service.risk.impl;

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
import io.shulie.takin.web.data.param.baseserver.*;
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

import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author xingchen
 * @ClassName: BaseRiskServiceImpl
 * @Package: io.shulie.takin.report.service.impl
 * @date 2020/7/2717:26
 */
@Service(value = "problemAnalysisService")
public class ProblemAnalysisServiceImpl implements ProblemAnalysisService {
    public static final String UNKNOW_HTTP = "未知(HTTP)";
    public static final String UNKNOW_RPC = "未知(RPC)";
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
     * 同步机器基础信息到表
     */
    @Override
    public void syncMachineData(Long reportId,Long endTime) {
        long startTime = System.currentTimeMillis();
        ReportDetailDTO dto = reportDataCache.getReportDetailDTO(reportId);
        if (dto == null) {
            return;
        }
        // 统计当前时间 前5分钟数据
        if (StringUtils.isNotBlank(dto.getStartTime())) {
            startTime = DateUtil.parseSecondFormatter(dto.getStartTime()).getTime();
        }
        // 统计当前时间 前5分钟数据
        if (endTime == null) {
            if (dto.getEndTime() != null) {
                endTime = dto.getEndTime().getTime();
            } else {
                endTime = System.currentTimeMillis();
            }
        }
        // 统计当前时间 前5分钟数据
        int riskTime = ConfigServerHelper.getIntegerValueByKey(ConfigServerKeyEnum.TAKIN_RISK_COLLECT_TIME);
        if (endTime - startTime >= riskTime) {
            startTime = endTime - riskTime;
        }

        final long sTime = formatTimestamp(startTime);
        final long eTime = formatTimestamp(endTime);

        List<BaseAppVo> baseAppVoList = Lists.newArrayList();

        /**
         * 获取压测中所有的应用信息
         */
        List<String> appNameList = reportDataCache.getApplications(reportId);

        //获取在线应用的agentId --> 目前查询amdb不隔离
        ApplicationNodeQueryParam param = new ApplicationNodeQueryParam();
        param.setApplicationNames(appNameList);
        List<String> onlineAgentIds = applicationNodeDAO.getOnlineAgentIds(param);
        appNameList.forEach(appName -> {
            Collection<BaseServerResult> baseList = baseServerDao.queryBaseServer(new BaseServerParam(sTime, eTime, appName));
            if (CollectionUtils.isNotEmpty(baseList)) {
                logger.debug("报告{}对应的应用{},查询时间段为：{}-{},在influx中对应的数据长度为:{}", dto.getId(), appName, sTime, eTime, baseList.size());
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
                    vo.setGcCount(BigDecimal.valueOf(Optional.ofNullable(base.getFullGcCount()).orElse(0D))
                            .add(BigDecimal.valueOf(Optional.ofNullable(base.getYoungGcCount()).orElse(0D))));
                    vo.setGcTime(BigDecimal.valueOf(Optional.ofNullable(base.getFullGcCost()).orElse(0D))
                            .add(BigDecimal.valueOf(Optional.ofNullable(base.getYoungGcCost()).orElse(0D))));
                    return vo;
                }).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(tmpList)) {
                    baseAppVoList.addAll(tmpList);
                }
            } else {
                logger.debug("报告{}对应的应用{},查询时间段为：{}-{},在influx中对应的数据长度为空", dto.getId(), appName, sTime, eTime);
            }
        });

        // 处理基础信息
        if (CollectionUtils.isNotEmpty(baseAppVoList)) {

            Map<String, List<BaseAppVo>> appMap = baseAppVoList.stream().collect(Collectors.groupingBy(this::fetchApp));

            List<ReportMachineUpdateParam> insertList = Lists.newArrayList();
            appMap.values().forEach(value -> {

                if(CollectionUtils.isEmpty(value)) {
                    return;
                }
                BaseAppVo baseAppVo = value.stream().filter(base -> {
                    // agentId 是否在线 只有不等于空
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
                     * 单位换算 磁盘 内存
                     */
                    baseAppVo.setDisk(VolumnUtil.convertByte2Gb(baseAppVo.getDisk()));
                    baseAppVo.setMemory(VolumnUtil.convertByte2Gb(baseAppVo.getMemory()));

                    //只保存基础信息
                    baseAppVo.setAppIp(null);
                    baseAppVo.setAppName(null);
                    baseAppVo.setReportId(null);
                    baseAppVo.setAgentIp(null);
                    tmp.setMachineBaseConfig(JSON.toJSONString(baseAppVo));
                    // 增加租户
                    WebPluginUtils.transferTenantParam(WebPluginUtils.traceTenantCommonExt(),tmp);
                    insertList.add(tmp);
                }
            });
            if (CollectionUtils.isNotEmpty(insertList)) {
                // machineTpsTargetConfig 指标信息不更新
                insertList.forEach(reportMachineDAO::insertOrUpdate);
            }
        }
    }

    /**
     * 检查风险机器
     */
    @Override
    public void checkRisk(Long reportId) {
        List<BaseRiskResult> riskVoList = this.processRisk(reportId);
        if (CollectionUtils.isEmpty(riskVoList)) {
            return;
        }

        //更新机器风险信息
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
     * 根据压测场景ID获取风险列表
     *
     * @return
     */
    @Override
    public List<BaseRiskResult> processRisk(Long reportId) {
        /**
         * 1、获取报告ID报告详情（此处报告不完整，为了获取一些基础信息数据），获取压测开始时间和结束时间，
         * 2、获取压测报告中的业务活动，获取目标tps和实际tps
         *  2-1、通过业务活动去获取关联的链路信息
         *  2-2、从链路信息中获取链路关联的应用信息
         *  2-3、根据应用信息去获取机器信息
         *  2-4、判断当前tps是否达到目标tps
         *      2-4-1、所有指标到达tps
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
        // 业务活动链路概览
        if (CollectionUtils.isEmpty(dto.getBusinessActivity())) {
            return results;
        }

        final long sTime = formatTimestamp(startTime);
        final long eTime = formatTimestamp(endTime);
        dto.getBusinessActivity().forEach(ba -> {
            Long businessActivityId = ba.getBusinessActivityId();
            BigDecimal maxTps = ba.getMaxTps() != null ? ba.getMaxTps() : new BigDecimal("0");
            // 目标TPS
            BigDecimal destTps = BigDecimal.valueOf(
                    Double.parseDouble(ObjectUtils.toString(ba.getTps().getValue(), "0")));
            // 根据业务活动。获取关联的应用名称
            List<String> appNameList = applicationBusinessActivityService.processAppNameByBusinessActiveId(
                    businessActivityId);

            /**
             * 是否超过目标TPS
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
     * 处理瓶颈,返回业务活动，接口列表对应的tps和rt
     */
    @Override
    public void processBottleneck(Long reportId) {
        long startTime = System.currentTimeMillis();
        long endTime = System.currentTimeMillis();
        ReportDetailDTO dto = reportDataCache.getReportDetailDTO(reportId);
        // 业务活动链路概览
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
                //计算瓶颈权重
                LinkCount linkCount = new LinkCount();
                linkDataCalcUtil.calcLinkWeight(linkCount, null, linkDataResultList);
                //返回瓶颈列表
                BigDecimal avg = linkDataCalcUtil.calcAvgRate(linkCount.getCount());
                linkDataCalcUtil.getBottleneckInterfaces(linkDataResultList, linkCount.getCount(), avg, bottleneckList);
            }
        });
        //根据权重倒序排
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
        //批量入库
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
     * 根据业务活动ID、时间获取压测链路明细信息
     *
     * @param startTime 毫秒数
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
         * 获取此时间段产生的traceID
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
     * 移除位置节点
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
            // 查询实时计算库的metrics信息
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

                // TODO 暂时处理下平均rt,假如比第一层级大，则取最小的
                if (tmpDetail.getAvgRt() > firstRt.get()) {
                    data.setAvgRt(firstRt.get());
                }
            }
            data.setTraceId(traceId);
            if (StringUtils.isNotBlank(tmpDetail.getAppName())) {
                data.setAppName(tmpDetail.getAppName());
            }
            parent.add(data);

            // 获取子节点
            List<Category> sub = tmp.getChildren();
            if (CollectionUtils.isNotEmpty(sub)) {
                data.setChildren(Lists.newArrayList());
                buildLinkDetail(false, data.getAppName(), data.getChildren(), sub, stime, eTime, traceId, aggMap);
            }
        }
    }

    /**
     * 根据业务活动Id找到链路数据
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
            // 查询实时计算库的metrics信息
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

            // 获取子节点
            List<Category> sub = tmp.getChildren();
            if (CollectionUtils.isNotEmpty(sub)) {
                buildLink(data.getAppName(), data.getSubLink(), sub, stime, eTime);
            }
        }
    }

    /**
     * 查询实时数据的metircs
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
     * 查询实时数据的metircs
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
     * 根据应用信息,返回所有的风险列表F
     * [1.1]有的话报宕机风险风险；
     * [1.2]没有的话看实际压测TPS有没有达到目标TPS：
     * [1.2.1]没有的话，检查CPU/load/内存/磁盘IO/网络带宽使用率最高值是否超过80%：
     * [1.2.1.1]超过则报对应的机器指标风险；
     * [1.2.1.2]未超过则不报风险；
     * [1.2.2]有的话，检查目标TPS值上下区间5%的平均CPU/load/内存/磁盘IO/网络带宽使用率是否超过80%：
     * [1.2.2.1]没超过则不报风险；
     * [1.2.2.2]超过则报对应的机器指标风险。
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
     * 超过tps的时候
     *
     * @return
     */
    private List<BaseRiskResult> processOverRisk(List<String> appNames, Long sceneId, Long reportId, BigDecimal
            destTps) {
        List<BaseRiskResult> results = Lists.newArrayList();
        if (CollectionUtils.isEmpty(appNames)) {
            return results;
        }
        // 1、调取云上的tps和cpu
        List<Metrices> metrices = reportDataCache.listAllMetricsData(reportId);
        if (CollectionUtils.isEmpty(metrices)) {
            logger.error("metrices is null,{}", reportId);
            return results;
        }
        // 获取metrices 的最大时间和最小时间，获取此时间段内的cpu信息
        List<Metrices> sortMetrics = metrices.stream().sorted(Comparator.comparing(Metrices::getTime)).collect(
                Collectors.toList());
        Metrices firstMetrices = sortMetrics.get(0);
        Metrices lastMetrices = sortMetrics.get(sortMetrics.size() - 1);
        // 取60秒前后的数据
        long firstTime = formatTimestamp(firstMetrices.getTime() - 60 * 1000);
        long lastTime = formatTimestamp(lastMetrices.getTime() + 60 * 1000);

        final List<Metrices> metricesList = metrices;

        appNames.forEach(appName -> {
            String ipSql = "select distinct(app_ip) as app_ip from app_base_data where app_name = '" + appName
                    + "' and time > " + firstTime + " and time <= " + lastTime +
                // 增加租户
                " and tenant_app_key = '" + WebPluginUtils.traceTenantAppKey() + "'" +
                " and env_code = '" + WebPluginUtils.traceEnvCode() + "'";
            Collection<BaseServerResult> ipList = influxDatabaseManager.query(BaseServerResult.class, ipSql);
            if (CollectionUtils.isNotEmpty(ipList)) {
                // 需要统计的Metrices
                List<BaseServerResult> calMetricesList = Lists.newArrayList();

                ipList.forEach(ip -> {
                    String tmpSql =
                            "select mean(cpu_rate) as cpu_rate,mean(cpu_load) as cpu_load,mean(mem_rate) as mem_rate,mean"
                                    + "(iowait) as iowait,mean(net_bandwidth_rate) as net_bandwidth_rate from app_base_data"
                                    + " where tag_app_name = '" + appName + "' and tag_app_ip = '" + ip.getAppIp()
                                    + "' and time > " + firstTime + " and time <= " + lastTime +
                                    // 增加租户
                                    " and tenant_app_key = '" + WebPluginUtils.traceTenantAppKey() + "'" +
                                    " and env_code = '" + WebPluginUtils.traceEnvCode() + "'"
                                    + " group by time(5s) order by time";
                    Collection<BaseServerResult> voList = influxDatabaseManager.query(BaseServerResult.class, tmpSql);
                    if (CollectionUtils.isNotEmpty(voList)) {
                        // 按秒分组
                        Map<Long, List<BaseServerResult>> voMap = voList.stream().collect(
                                Collectors.groupingBy(BaseServerResult::getExtTime));
                        metricesList.forEach(metricesTmp -> {
                            BaseServerResult tmpVo = new BaseServerResult();
                            /**
                             * TODO influxDB中聚合的时候,计算的时间的为开始时间-结束时间，相比于TPS的计算。错开了5秒的时间
                             *
                             * tps 计算 0-5秒，tps统计到第5秒
                             * infludb 0-5秒，计算的是0秒的数据
                             * 匹配的时候，直接获取-5秒的数据
                             */
                            Long time = metricesTmp.getTime() - 5 * 1000;
                            List<BaseServerResult> tmpBaseVoList = voMap.get(time);
                            if (CollectionUtils.isNotEmpty(tmpBaseVoList)) {
                                tmpVo = tmpBaseVoList.stream().findFirst().get();
                                tmpVo.setTps(metricesTmp.getAvgTps());
                                tmpVo.setTime(Instant.ofEpochMilli(metricesTmp.getTime()));
                                tmpVo.setAppIp(ip.getAppIp());
                                tmpVo.setAppName(appName);

                                // 设置值
                                calMetricesList.add(tmpVo);
                            }
                        });

                        // 判断匹配告警
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
     * 匹配风险规则
     *
     * @return
     */
    private List<BaseRiskResult> matchRisk(Collection<BaseServerResult> voList, BigDecimal destTps, Long reportId) {
        List<BaseRiskResult> riskVoList = Lists.newArrayList();
        if (CollectionUtils.isEmpty(voList)) {
            return riskVoList;
        }
        /*
         * 按目标tps排序
         * 1、判断目标tps所在的位置,小于的部分按照最大tps，cpu告警
         * 2、大于部分,按照tps 的5%上下区间告警
         */
        BaseServerResult tmpVo = voList.stream().findFirst().orElse(new BaseServerResult());
        String appIp = tmpVo.getAppIp();
        String appName = tmpVo.getAppName();

        // 升序排
        Collection<BaseServerResult> sortedList = voList.stream()
                .filter(sort -> sort.getTps() != null)
                .sorted(Comparator.comparingDouble(BaseServerResult::getTps))
                .collect(Collectors.toList());
        // 获取小于目标TPS的数据
        List<BaseServerResult> lessList = sortedList.stream()
                .filter(sort -> sort.getTps() != null)
                .filter(sort -> sort.getTps().compareTo(destTps.doubleValue()) < 0)
                .collect(Collectors.toList());

        // 最大 cpu 使用率
        double scale = Double.parseDouble(
            ConfigServerHelper.getValueByKey(ConfigServerKeyEnum.TAKIN_RISK_MAX_NORM_SCALE));

        // 最大 cpu load
        int maxLoad = Integer.parseInt(
            ConfigServerHelper.getValueByKey(ConfigServerKeyEnum.TAKIN_RISK_MAX_NORM_MAX_LOAD));

        if (CollectionUtils.isNotEmpty(lessList)) {
            BaseRiskResult risk = new BaseRiskResult();
            risk.setAppIp(appIp);
            risk.setAppName(appName);
            risk.setReportId(reportId);

            // 最大cpu使用率
            BaseServerResult cpuRate = lessList.stream()
                    .filter(less -> less.getCpuRate() != null)
                    .max(Comparator.comparing(BaseServerResult::getCpuRate))
                    .orElse(new BaseServerResult());

            StringBuilder stringBuilder = new StringBuilder();
            if (cpuRate.getCpuRate() != null && cpuRate.getCpuRate() >= scale) {
                stringBuilder.append("cpu使用率超过")
                        .append(formatDouble(cpuRate.getCpuRate()))
                        .append("%;");
            }
            // 最大load
            BaseServerResult cpuLoad = lessList.stream()
                    .filter(less -> less.getCpuLoad() != null)
                    .max(Comparator.comparing(BaseServerResult::getCpuLoad))
                    .orElse(new BaseServerResult());
            if (cpuLoad.getCpuLoad() != null && cpuLoad.getCpuLoad() >= maxLoad) {
                stringBuilder.append("cpuLoad超过")
                        .append(formatDouble(cpuLoad.getCpuLoad()))
                        .append("; ");
            }
            // 最大内存使用率
            BaseServerResult memRate = lessList.stream()
                    .filter(less -> less.getMemRate() != null)
                    .max(Comparator.comparing(BaseServerResult::getMemRate))
                    .orElse(new BaseServerResult());
            if (memRate.getMemRate() != null && memRate.getMemRate() >= scale) {
                stringBuilder.append("最大内存使用率超过")
                        .append(formatDouble(memRate.getMemRate()))
                        .append("%;");
            }
            // 最大Io等待率
            BaseServerResult ioWait = lessList.stream()
                    .filter(less -> less.getIoWait() != null)
                    .max(Comparator.comparing(BaseServerResult::getIoWait))
                    .orElse(new BaseServerResult());
            if (ioWait.getIoWait() != null && ioWait.getIoWait() >= scale) {
                stringBuilder.append("最大IoWait")
                        .append(formatDouble(ioWait.getIoWait()))
                        .append("%;");
            }
            // 最大网络带宽使用率
            BaseServerResult netBandWidthRate = lessList.stream()
                    .filter(less -> less.getNetBandWidthRate() != null)
                    .max(Comparator.comparing(BaseServerResult::getNetBandWidthRate))
                    .orElse(new BaseServerResult());

            if (netBandWidthRate.getNetBandWidthRate() != null && netBandWidthRate.getNetBandWidthRate() >= scale) {
                stringBuilder.append("网络带宽使用率超过")
                        .append(formatDouble(netBandWidthRate.getNetBandWidthRate()))
                        .append("%;");
            }

            /*
             * 判断是否符合规则
             */
            String content = stringBuilder.toString();
            if (StringUtils.isNotBlank(content)) {
                risk.setContent(content.substring(0, content.lastIndexOf(";")));
                riskVoList.add(risk);
            }
        }

        /*
         * 大于目标tps的部分
         */
        List<BaseServerResult> moreList = sortedList.stream()
                .filter(sort -> sort.getTps() != null)
                .filter(sort -> sort.getTps().compareTo(destTps.doubleValue()) > 0)
                .collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(moreList)) {
            /*
             * 获取上下5%的tps区间
             */
            BigDecimal mid = destTps.multiply(BigDecimal.valueOf(0.05));
            final BigDecimal max = formatDouble(mid.add(destTps).doubleValue());
            final BigDecimal min = formatDouble(destTps.subtract(mid).doubleValue());

            // 获取此tps段内的cpu、load集合
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
                // 计算cpu使用率平均值
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
                    stringBuilder.append("cpu使用率超过")
                            .append(formatDouble(cpuRate))
                            .append("%;");
                }
                // 最大load
                if (cpuLoad >= maxLoad) {
                    stringBuilder.append("cpuLoad超过")
                            .append(formatDouble(cpuLoad))
                            .append("; ");
                }
                // 最大内存使用率
                if (cpuMemRate >= scale) {
                    stringBuilder.append("最大内存使用率超过")
                            .append(formatDouble(cpuMemRate))
                            .append("%;");
                }
                // 最大Io等待率
                if (ioWait >= scale) {
                    stringBuilder.append("最大IoWait")
                            .append(formatDouble(ioWait))
                            .append("%;");
                }
                // 最大网络带宽使用率
                if (netBandWidthRate >= scale) {
                    stringBuilder.append("网络带宽使用率超过")
                            .append(formatDouble(netBandWidthRate))
                            .append("%;");
                }

                /**
                 * 判断是否符合规则
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
