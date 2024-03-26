package io.shulie.takin.cloud.biz.service.report.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.pamirs.takin.cloud.entity.dao.report.TReportBusinessActivityDetailMapper;
import com.pamirs.takin.cloud.entity.dao.report.TReportMapper;
import com.pamirs.takin.cloud.entity.dao.scene.manage.TWarnDetailMapper;
import com.pamirs.takin.cloud.entity.domain.bo.scenemanage.WarnBO;
import com.pamirs.takin.cloud.entity.domain.dto.report.BusinessActivityDTO;
import com.pamirs.takin.cloud.entity.domain.dto.report.CloudReportDTO;
import com.pamirs.takin.cloud.entity.domain.dto.report.Metrices;
import com.pamirs.takin.cloud.entity.domain.dto.report.StatReportDTO;
import com.pamirs.takin.cloud.entity.domain.entity.report.Report;
import com.pamirs.takin.cloud.entity.domain.entity.report.ReportBusinessActivityDetail;
import com.pamirs.takin.cloud.entity.domain.entity.scene.manage.WarnDetail;
import com.pamirs.takin.entity.domain.dto.report.PressureTestTimeDTO;
import io.shulie.takin.adapter.api.model.ScriptNodeSummaryBean;
import io.shulie.takin.adapter.api.model.common.DataBean;
import io.shulie.takin.adapter.api.model.common.DistributeBean;
import io.shulie.takin.adapter.api.model.common.SlaBean;
import io.shulie.takin.adapter.api.model.common.StopReasonBean;
import io.shulie.takin.adapter.api.model.request.WarnQueryParam;
import io.shulie.takin.adapter.api.model.request.report.ReportQueryReq;
import io.shulie.takin.adapter.api.model.request.report.ReportTrendQueryReq;
import io.shulie.takin.adapter.api.model.request.report.ScriptNodeTreeQueryReq;
import io.shulie.takin.adapter.api.model.request.report.TrendRequest;
import io.shulie.takin.adapter.api.model.request.scenemanage.ReportActivityResp;
import io.shulie.takin.adapter.api.model.response.report.NodeTreeSummaryResp;
import io.shulie.takin.adapter.api.model.response.report.ReportTrendResp;
import io.shulie.takin.adapter.api.model.response.report.ScriptNodeTreeResp;
import io.shulie.takin.adapter.api.model.response.scenemanage.BusinessActivitySummaryBean;
import io.shulie.takin.cloud.biz.cloudserver.ReportConverter;
import io.shulie.takin.cloud.biz.collector.collector.AbstractIndicators;
import io.shulie.takin.cloud.biz.input.report.UpdateReportConclusionInput;
import io.shulie.takin.cloud.biz.input.report.UpdateReportSlaDataInput;
import io.shulie.takin.cloud.biz.input.report.WarnCreateInput;
import io.shulie.takin.cloud.biz.output.report.ReportDetailOutput;
import io.shulie.takin.cloud.biz.output.report.ReportOutput;
import io.shulie.takin.cloud.biz.output.scene.manage.SceneManageWrapperOutput;
import io.shulie.takin.cloud.biz.output.scene.manage.WarnDetailOutput;
import io.shulie.takin.cloud.biz.service.report.CloudReportService;
import io.shulie.takin.cloud.biz.service.scene.CloudSceneManageService;
import io.shulie.takin.cloud.biz.service.scene.ReportEventService;
import io.shulie.takin.cloud.biz.service.scene.SceneTaskEventService;
import io.shulie.takin.cloud.biz.utils.DataUtils;
import io.shulie.takin.cloud.common.bean.scenemanage.SceneManageQueryOptions;
import io.shulie.takin.cloud.common.bean.scenemanage.UpdateStatusBean;
import io.shulie.takin.cloud.common.bean.scenemanage.WarnBean;
import io.shulie.takin.cloud.common.bean.task.TaskResult;
import io.shulie.takin.cloud.common.constants.ReportConstants;
import io.shulie.takin.cloud.common.constants.SceneTaskRedisConstants;
import io.shulie.takin.cloud.common.constants.ScheduleConstants;
import io.shulie.takin.cloud.common.enums.PressureSceneEnum;
import io.shulie.takin.cloud.common.enums.scenemanage.SceneManageStatusEnum;
import io.shulie.takin.cloud.common.enums.scenemanage.SceneStopReasonEnum;
import io.shulie.takin.cloud.common.exception.TakinCloudException;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import io.shulie.takin.cloud.common.influxdb.InfluxUtil;
import io.shulie.takin.cloud.common.influxdb.InfluxWriter;
import io.shulie.takin.cloud.common.utils.*;
import io.shulie.takin.cloud.data.dao.report.ReportDao;
import io.shulie.takin.cloud.data.dao.scene.manage.SceneManageDAO;
import io.shulie.takin.cloud.data.model.mysql.ReportBusinessActivityDetailEntity;
import io.shulie.takin.cloud.data.model.mysql.ReportEntity;
import io.shulie.takin.cloud.data.model.mysql.SceneManageEntity;
import io.shulie.takin.cloud.data.param.report.ReportUpdateConclusionParam;
import io.shulie.takin.cloud.data.param.report.ReportUpdateParam;
import io.shulie.takin.cloud.data.result.report.ReportResult;
import io.shulie.takin.cloud.data.util.PressureStartCache;
import io.shulie.takin.cloud.ext.api.AssetExtApi;
import io.shulie.takin.cloud.ext.content.asset.AssetInvoiceExt;
import io.shulie.takin.cloud.ext.content.asset.RealAssectBillExt;
import io.shulie.takin.cloud.ext.content.enginecall.PtConfigExt;
import io.shulie.takin.cloud.ext.content.enginecall.ThreadGroupConfigExt;
import io.shulie.takin.cloud.ext.content.enums.AssetTypeEnum;
import io.shulie.takin.cloud.ext.content.enums.NodeTypeEnum;
import io.shulie.takin.cloud.ext.content.response.Response;
import io.shulie.takin.cloud.ext.content.script.ScriptNode;
import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import io.shulie.takin.eventcenter.Event;
import io.shulie.takin.eventcenter.annotation.IntrestFor;
import io.shulie.takin.plugin.framework.core.PluginManager;
import io.shulie.takin.utils.json.JsonHelper;
import io.shulie.takin.utils.linux.LinuxHelper;
import io.shulie.takin.web.biz.pojo.dto.scene.EnginePressureQuery;
import io.shulie.takin.web.biz.service.scenemanage.SceneManageService;
import io.shulie.takin.web.biz.utils.ParsePressureTimeByModeUtils;
import io.shulie.takin.web.biz.utils.ReportTimeUtils;
import io.shulie.takin.web.common.util.RedisClientUtil;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import jodd.util.Bits;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.influxdb.impl.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.takin.properties.AmdbClientProperties;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author 莫问
 * @date 2020-04-17
 */
@Service
@Slf4j
public class CloudReportServiceImpl extends AbstractIndicators implements CloudReportService {
    @Resource
    ReportDao reportDao;
    @Resource
    InfluxWriter influxWriter;
    @Resource
    TReportMapper tReportMapper;
    @Resource
    PluginManager pluginManager;
    @Resource
    SceneManageDAO sceneManageDao;
    @Resource
    TWarnDetailMapper tWarnDetailMapper;
    @Resource
    ReportEventService reportEventService;
    @Resource
    CloudSceneManageService cloudSceneManageService;
    @Resource
    StringRedisTemplate stringRedisTemplate;
    @Resource
    SceneTaskEventService sceneTaskEventService;
    @Resource
    TReportBusinessActivityDetailMapper tReportBusinessActivityDetailMapper;
    @Resource
    private RedisClientUtil redisClientUtil;

    @Value("${report.aggregation.interval}")
    private String reportAggregationInterval;
    /**
     * 压测场景强行关闭预留时间
     */
    @Value("${scene.pressure.forceCloseTime: 20}")
    private Integer forceCloseTime;
    @Value("${pressure.engine.jtl.path:/data/nfs_dir/ptl}")
    private String pressureEngineJtlPath;
    @Value("${pressure.engine.log.path:/data/nfs_dir/logs}")
    private String pressureEngineLogPath;

    public static final String COMPARE = "<=";

    @Autowired
    private AmdbClientProperties properties;

    private static final String AMDB_ENGINE_PRESSURE_QUERY_LIST_PATH = "/amdb/db/api/enginePressure/queryListMap";

    ExecutorService baseLineExecutorService  = Executors.newFixedThreadPool(2);

    ExecutorService baseLineProblemExecutorService  = Executors.newFixedThreadPool(2);


    @Resource
    private SceneManageService sceneManageService;

    @Override
    public PageInfo<CloudReportDTO> listReport(ReportQueryReq param) {

        PageHelper.startPage(param.getPageNumber(), param.getPageSize());
        //默认只查询普通场景的报告
        if (param.getType() == null) {
            param.setType(0);
        }
        // 补充用户过滤信息信息
        if (StrUtil.isNotBlank(CloudPluginUtils.getContext().getFilterSql())) {
            param.setFilterSql(CloudPluginUtils.getContext().getFilterSql());
            // 去除左右的括号
            if (param.getFilterSql().lastIndexOf("(") == 0
                    && param.getFilterSql().lastIndexOf(")") == param.getFilterSql().length() - 1) {
                param.setFilterSql(param.getFilterSql().substring(1, param.getFilterSql().length() - 1));
            }
        }
        param.setEnvCode(CloudPluginUtils.getContext().getEnvCode());
        param.setTenantId(CloudPluginUtils.getContext().getTenantId());
        param.setDeptId(WebPluginUtils.traceDeptId());
        List<Report> reportList = tReportMapper.listReport(param);
        if (CollectionUtils.isEmpty(reportList)) {
            return new PageInfo<>(new ArrayList<>(0));
        }
        PageInfo<Report> old = new PageInfo<>(reportList);
        Map<Long, String> errorMsgMap = new HashMap<>(0);
        for (Report report : reportList) {
            if (report.getConclusion() != null && report.getConclusion() == 0 && report.getFeatures() != null) {
                JSONObject jsonObject = JSON.parseObject(report.getFeatures());
                String key = "error_msg";
                if (Objects.nonNull(jsonObject) && jsonObject.containsKey(key)) {
                    errorMsgMap.put(report.getId(), jsonObject.getString(key));
                }
            }
        }
        List<CloudReportDTO> list = ReportConverter.INSTANCE.ofReport(reportList);
        for (CloudReportDTO dto : list) {
            if (errorMsgMap.containsKey(dto.getId())) {
                dto.setErrorMsg(errorMsgMap.get(dto.getId()));
            }
        }
        PageInfo<CloudReportDTO> data = new PageInfo<>(list);
        data.setTotal(old.getTotal());

        return data;
    }

    @Override
    public ReportDetailOutput getReportByReportId(Long reportId) {
        ReportResult report = reportDao.selectById(reportId);
        if (report == null) {
            log.warn("获取报告异常，报告数据不存在。报告ID：{}", reportId);
            return null;
        }
        ReportDetailOutput detail = ReportConverter.INSTANCE.ofReportDetail(report);

        //警告列表
        List<WarnBean> warnList = listWarn(reportId);
        detail.setTaskStatus(report.getStatus());
        if (CollectionUtils.isNotEmpty(warnList)) {
            detail.setWarn(warnList);
            detail.setTotalWarn(warnList.stream().mapToLong(WarnBean::getTotal).sum());
        }
        if (StringUtils.isNotEmpty(report.getFeatures())) {
            detail.setConclusionRemark(
                    JSON.parseObject(report.getFeatures()).getString(ReportConstants.FEATURES_ERROR_MSG));
        }
        detail.setTestTotalTime(TestTimeUtil.format(report.getStartTime(), report.getEndTime()));
        List<ScriptNodeSummaryBean> reportNodeDetail = getReportNodeDetail(report.getScriptNodeTree(), reportId);
        detail.setNodeDetail(reportNodeDetail);

        List<BusinessActivitySummaryBean> businessActivities = new ArrayList<>();
        buildFailActivitiesByNodeDetails(reportNodeDetail, businessActivities);
        detail.setBusinessActivity(businessActivities);
        //任务没有完成，提示用户正在生成中
        if (report.getStatus() != ReportConstants.FINISH_STATUS) {
            detail.setTaskStatus(ReportConstants.RUN_STATUS);
        }
        // sla转换对象
        if (StringUtils.isNotBlank(report.getFeatures())) {
            JSONObject jsonObject = JSON.parseObject(report.getFeatures());
            if (jsonObject.containsKey(ReportConstants.SLA_ERROR_MSG)) {
                detail.setSlaMsg(
                        JsonHelper.json2Bean(jsonObject.getString(ReportConstants.SLA_ERROR_MSG), SlaBean.class));
            }
            String ptlPath = jsonObject.getString(PressureStartCache.FEATURES_MACHINE_PTL_PATH);
            if (StringUtils.isNotBlank(ptlPath)) {
                detail.setPtlPath(Arrays.asList(ptlPath.split(",")));
            }
            detail.setMaxTps(jsonObject.getBigDecimal("maxTps"));
            detail.setMaxRt(jsonObject.getBigDecimal("maxRt"));
            detail.setMinTps(jsonObject.getBigDecimal("minTps"));
            detail.setMinRt(jsonObject.getBigDecimal("minRt"));
        }
        dealCalibrationStatus(detail);
        return detail;
    }

    private void buildFailActivitiesByNodeDetails(List<ScriptNodeSummaryBean> reportNodeDetail,
                                                  List<BusinessActivitySummaryBean> result) {
        if (CollectionUtils.isEmpty(reportNodeDetail)) {
            return;
        }
        for (ScriptNodeSummaryBean bean : reportNodeDetail) {
            if (Objects.nonNull(bean.getActivityId()) && bean.getActivityId() > 0) {
                BusinessActivitySummaryBean summaryBean = new BusinessActivitySummaryBean();
                summaryBean.setBusinessActivityId(bean.getActivityId());
                summaryBean.setBusinessActivityName(bean.getTestName());
                summaryBean.setBindRef(bean.getXpathMd5());
                summaryBean.setApplicationIds(bean.getApplicationIds());
                summaryBean.setAvgRT(bean.getAvgRt());
                summaryBean.setMinRt(bean.getMinRt());
                summaryBean.setMaxRt(bean.getMaxRt());
                summaryBean.setMaxTps(bean.getMaxTps());
                summaryBean.setPassFlag(bean.getPassFlag());
                summaryBean.setSa(bean.getSa());
                summaryBean.setSuccessRate(bean.getSuccessRate());
                summaryBean.setTotalRequest(bean.getTotalRequest());
                summaryBean.setTps(bean.getTps());
                summaryBean.setFeatures(bean.getFeatures());
                summaryBean.setRtDistribute(bean.getRtDistribute());
                summaryBean.setDistribute(bean.getDistribute());
                result.add(summaryBean);
            }
            if (CollectionUtils.isNotEmpty(bean.getChildren())) {
                buildFailActivitiesByNodeDetails(bean.getChildren(), result);
            }
        }
    }

    public List<ScriptNodeSummaryBean> getReportNodeDetail(String scriptNodeTree, Long reportId) {
        List<ReportBusinessActivityDetail> activities = tReportBusinessActivityDetailMapper
                .queryReportBusinessActivityDetailByReportId(reportId);
        return getScriptNodeSummaryBeans(reportId, scriptNodeTree, activities);
    }

    @Override
    public ReportTrendResp queryReportTrend(ReportTrendQueryReq reportTrendQuery) {
        return queryReportTrend(reportTrendQuery, false);
    }

    @Override
    public ReportDetailOutput tempReportDetail(Long sceneId) {
        long start = System.currentTimeMillis();
        ReportDetailOutput reportDetail = new ReportDetailOutput();

        ReportResult reportResult = reportDao.getTempReportBySceneId(sceneId);
        if (reportResult == null) {
            reportDetail.setTaskStatus(ReportConstants.FINISH_STATUS);
            return reportDetail;
        }
        SceneManageQueryOptions options = new SceneManageQueryOptions();
        options.setIncludeBusinessActivity(false);
        SceneManageWrapperOutput wrapper = cloudSceneManageService.getSceneManage(sceneId, options);
        reportDetail = ReportConverter.INSTANCE.ofReportDetail(reportResult);
        reportDetail.setUserId(wrapper.getUserId());

        // 补充停止原因
        ReportDetailOutput detailOutput = this.getReportByReportId(reportResult.getId());
        reportDetail.setStopReasons(getStopReasonBean(sceneId, detailOutput));
        // 查询sla熔断数据
        reportDetail.setSlaMsg(detailOutput.getSlaMsg());
        String testPlanXpathMd5 = getTestPlanXpathMd5V2(reportResult.getScriptNodeTree());
        String transaction = StringUtils.isBlank(testPlanXpathMd5) ? ReportConstants.ALL_BUSINESS_ACTIVITY
                : testPlanXpathMd5;
        Long jobId = reportResult.getJobId();
        StatReportDTO statReport = statTempReport(jobId, sceneId, reportResult.getId(), reportResult.getTenantId(),
                transaction);
        if (statReport == null) {
            log.warn("实况报表:[{}]，暂无数据", reportResult.getId());
        } else {
            reportDetail.setTotalRequest(statReport.getTotalRequest());
            reportDetail.setAvgRt(statReport.getAvgRt());
            reportDetail.setAvgTps(statReport.getTps());
            reportDetail.setSa(statReport.getSa());
            reportDetail.setSuccessRate(statReport.getSuccessRate());
            reportDetail.setAvgConcurrent(statReport.getAvgConcurrenceNum());
        }
        reportDetail.setSceneName(wrapper.getPressureTestSceneName());
        //最大并发
        Integer maxConcurrence = getMaxConcurrence(jobId, sceneId, reportResult.getId(), reportResult.getTenantId(),
                transaction);
        if (Objects.nonNull(statReport) && Objects.nonNull(statReport.getAvgConcurrenceNum())) {
            maxConcurrence = Math.max(maxConcurrence, statReport.getAvgConcurrenceNum().intValue());
        }
        reportDetail.setConcurrent(maxConcurrence);
        reportDetail.setTotalWarn(tWarnDetailMapper.countReportTotalWarn(reportResult.getId()));
        reportDetail.setTaskStatus(reportResult.getStatus());
        reportDetail.setTestTime(getTaskTime(reportResult.getStartTime(), new Date(), wrapper.getTotalTestTime()));
        reportDetail.setTestTotalTime(
                String.format("%d'%d\"", wrapper.getTotalTestTime() / 60, wrapper.getTotalTestTime() % 60));

        // 补充操作人
        List<ReportBusinessActivityDetail> reportBusinessActivityDetails = tReportBusinessActivityDetailMapper
                .queryReportBusinessActivityDetailByReportId(reportResult.getId());
        if (StringUtils.isNotBlank(reportResult.getScriptNodeTree())) {
            String nodeTree = reportResult.getScriptNodeTree();
            Map<String, Map<String, Object>> resultMap = new HashMap<>(reportBusinessActivityDetails.size());
            reportBusinessActivityDetails.stream()
                    .filter(Objects::nonNull)
                    .forEach(ref -> {
                        StatReportDTO data = statTempReport(jobId, sceneId, reportResult.getId(), reportResult.getTenantId(),
                                ref.getBindRef());
                        Map<String, Object> objectMap = fillTempMap(data, ref);
                        resultMap.put(ref.getBindRef(), objectMap);
                    });
            String resultTree = JsonPathUtil.putNodesToJson(nodeTree, resultMap);
            reportDetail.setNodeDetail(JsonUtil.parseArray(resultTree,
                    ScriptNodeSummaryBean.class));
        } else {
            List<ScriptNodeSummaryBean> nodeDetails = reportBusinessActivityDetails.stream()
                    .filter(Objects::nonNull)
                    .map(ref -> {
                        StatReportDTO data = statTempReport(jobId, sceneId, reportResult.getId(), reportResult.getTenantId(),
                                ref.getBindRef());
                        return new ScriptNodeSummaryBean() {{
                            setName(ref.getBusinessActivityName());
                            setTestName(ref.getBusinessActivityName());
                            setXpathMd5(ref.getBindRef());
                            if (Objects.nonNull(data)) {
                                setAvgRt(new DataBean(data.getAvgRt(), ref.getTargetRt()));
                                setSa(new DataBean(data.getSa(), ref.getTargetSa()));
                                setTps(new DataBean(data.getTps(), ref.getTargetTps()));
                                setSuccessRate(new DataBean(data.getSuccessRate(), ref.getTargetSuccessRate()));
                                setAvgConcurrenceNum(data.getAvgConcurrenceNum());
                                setTempRequestCount(data.getTempRequestCount());
                                setTotalRequest(data.getTotalRequest());
                            } else {
                                setAvgRt(new DataBean("0", ref.getTargetRt()));
                                setSa(new DataBean("0", ref.getTargetSa()));
                                setTps(new DataBean("0", ref.getTargetTps()));
                                setSuccessRate(new DataBean("0", ref.getTargetSuccessRate()));
                                setAvgConcurrenceNum(new BigDecimal(0));
                                setTempRequestCount(0L);
                                setTotalRequest(0L);
                            }
                        }};
                    }).collect(Collectors.toList());
            reportDetail.setNodeDetail(nodeDetails);

        }
        //检查任务是否超时
        // boolean taskIsTimeOut = checkSceneTaskIsTimeOut(reportResult, wrapper);
        boolean taskIsTimeOut = false;
        if (wrapper.getStatus().intValue() == SceneManageStatusEnum.PRESSURE_TESTING.getValue().intValue() && taskIsTimeOut) {
            log.info("报表[{}]超时，通知调度马上停止压测", reportResult.getId());
            //报告正在生成中
            reportDetail.setTaskStatus(ReportConstants.RUN_STATUS);
            //重置时间
            reportDetail.setTestTime(
                    String.format("%d'%d\"", wrapper.getTotalTestTime() / 60, wrapper.getTotalTestTime() % 60));

            // 主动通知暂停事件，注意有可能会被多次触发
            redisClientUtil.setString(PressureStartCache.getStopTaskMessageKey(sceneId), "报告超时", 2, TimeUnit.MINUTES);
            sceneTaskEventService.callStopEvent(reportResult);
        }
        log.info("实时监测metric数据：tempReportDetail-运行时间：{}", System.currentTimeMillis() - start);
        return reportDetail;
    }

    private String getTestPlanXpathMd5(String scriptNodeTree) {
        if (StringUtils.isBlank(scriptNodeTree)) {
            return null;
        }
        List<ScriptNode> currentNodeByType = JsonPathUtil.getCurrentNodeByType(scriptNodeTree,
                NodeTypeEnum.TEST_PLAN.name());
        if (CollectionUtils.isNotEmpty(currentNodeByType) && currentNodeByType.size() == 1) {
            return currentNodeByType.get(0).getXpathMd5();
        }
        return null;
    }

    private String getTestPlanXpathMd5V2(String scriptNodeTree) {
        if (StringUtils.isBlank(scriptNodeTree)) {
            return null;
        }
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            ScriptNode[] scriptNodes = objectMapper.readValue(scriptNodeTree, ScriptNode[].class);
            if (scriptNodes != null && scriptNodes.length > 0) {
                for (ScriptNode scriptNode : scriptNodes) {
                    if (NodeTypeEnum.TEST_PLAN.equals(scriptNode.getType())) {
                        return scriptNode.getXpathMd5();
                    }
                }
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        return null;
    }


    /**
     * 组装停止原因
     *
     * @param sceneId      场景主键
     * @param detailOutput 报告
     * @return -
     */
    private List<StopReasonBean> getStopReasonBean(Long sceneId, ReportDetailOutput detailOutput) {
        List<StopReasonBean> stopReasons = Lists.newArrayList();

        // 查询sla熔断数据
        if (detailOutput.getSlaMsg() != null) {
            StopReasonBean slaReasonBean = new StopReasonBean();
            slaReasonBean.setType(SceneStopReasonEnum.SLA.getType());
            slaReasonBean.setDescription(SceneStopReasonEnum.toSlaDesc(detailOutput.getSlaMsg()));
            stopReasons.add(slaReasonBean);
        }
        // 查询压测引擎是否有异常
        String key = String.format(SceneTaskRedisConstants.SCENE_TASK_RUN_KEY + "%s_%s", sceneId, detailOutput.getId());
        Object errorObj = stringRedisTemplate.opsForHash().get(key, SceneTaskRedisConstants.SCENE_RUN_TASK_ERROR);
        if (Objects.nonNull(errorObj)) {
            // 组装压测引擎异常显示数据
            stopReasons.add(new StopReasonBean() {{
                setType(SceneStopReasonEnum.ENGINE.getType());
                setDescription(SceneStopReasonEnum.toEngineDesc(errorObj.toString()));
            }});
        }
        return stopReasons;
    }

    @Override
    public ReportTrendResp queryTempReportTrend(ReportTrendQueryReq reportTrendQuery) {
        return queryReportTrend(reportTrendQuery, true);
    }

    @Override
    public PageInfo<WarnDetailOutput> listWarn(WarnQueryParam param) {
        PageHelper.startPage(param.getCurrentPage() + 1, param.getPageSize());
        List<WarnDetail> warnDetailList = tWarnDetailMapper.listWarn(param);
        if (CollectionUtils.isEmpty(warnDetailList)) {
            return new PageInfo<>();
        }
        PageInfo<WarnDetail> old = new PageInfo<>(warnDetailList);
        List<WarnDetailOutput> list = ReportConverter.INSTANCE.ofWarnDetail(warnDetailList);
        PageInfo<WarnDetailOutput> data = new PageInfo<>(list);
        data.setTotal(old.getTotal());
        return data;
    }

    @Override
    public List<BusinessActivityDTO> queryReportActivityByReportId(Long reportId) {
        List<ReportBusinessActivityDetail> reportBusinessActivityDetailList = tReportBusinessActivityDetailMapper
                .queryReportBusinessActivityDetailByReportId(reportId);
        return ReportConverter.INSTANCE.ofBusinessActivity(reportBusinessActivityDetailList);
    }

    @Override
    public List<BusinessActivityDTO> queryReportActivityBySceneId(Long sceneId) {
        ReportResult reportResult = reportDao.getReportBySceneId(sceneId);
        if (reportResult != null) {
            return queryReportActivityByReportId(reportResult.getId());
        }
        return Lists.newArrayList();
    }

    /**
     * SLA警告信息
     *
     * @return -
     */
    private List<WarnBean> listWarn(Long reportId) {
        List<WarnBO> warnBOList = tWarnDetailMapper.summaryWarnByReportId(reportId);
        return ReportConverter.INSTANCE.ofWarn(warnBOList);
    }

    /**
     * 业务活动概况
     *
     * @return -
     */
    @Override
    public NodeTreeSummaryResp getNodeSummaryList(Long reportId) {
        //查询业务活动的概况
        List<ReportBusinessActivityDetail> reportBusinessActivityDetailList = tReportBusinessActivityDetailMapper
                .queryReportBusinessActivityDetailByReportId(reportId);
        NodeTreeSummaryResp resp = new NodeTreeSummaryResp();
        if (CollectionUtils.isEmpty(reportBusinessActivityDetailList)) {
            return resp;
        }
        ReportResult reportResult = reportDao.selectById(reportId);
        if (Objects.isNull(reportResult)) {
            throw new TakinCloudException(TakinCloudExceptionEnum.REPORT_GET_ERROR, "未查询到报告" + reportId);
        }
        resp.setScriptNodeSummaryBeans(getScriptNodeSummaryBeans(reportId, reportResult.getScriptNodeTree(), reportBusinessActivityDetailList));
        return resp;
    }

    /**
     * 处理节点链路明细
     *
     * @param nodeTree 节点树
     * @param details  业务活动详情
     * @return -
     */
    private List<ScriptNodeSummaryBean> getScriptNodeSummaryBeans(Long reportId, String nodeTree, List<ReportBusinessActivityDetail> details) {
        Map<String, Map<String, Object>> resultMap = new HashMap<>(details.size());
        Map<String, List<BigDecimal>> threadNumStages = getSummaryConcurrentStageThreadNum(reportId);

        if (StringUtils.isNotBlank(nodeTree)) {
            details.stream().filter(Objects::nonNull).forEach(detail -> {
                Map<String, Object> objectMap = fillReportMap(detail, threadNumStages);
                if (Objects.nonNull(objectMap)) {
                    resultMap.put(detail.getBindRef(), objectMap);
                }
            });
            if (resultMap.size() > 0) {
                String nodesToJson = JsonPathUtil.putNodesToJson(nodeTree, resultMap);
                return JSONArray.parseArray(nodesToJson, ScriptNodeSummaryBean.class);
            }
        }
        return details.stream().filter(Objects::nonNull)
                .map(detail -> {
                    ScriptNodeSummaryBean bean = new ScriptNodeSummaryBean();
                    bean.setXpathMd5(detail.getBindRef());
                    bean.setTestName(detail.getBusinessActivityName());
                    bean.setTotalRequest(detail.getRequest());
                    bean.setAvgConcurrenceNum(detail.getAvgConcurrenceNum());
                    bean.setSuccessRate(new DataBean(detail.getSuccessRate(), detail.getTargetSuccessRate()));
                    bean.setTps(new DataBean(detail.getTps(), detail.getTargetTps()));
                    bean.setMaxTps(detail.getMaxTps());
                    bean.setAvgRt(new DataBean(detail.getRt(), detail.getTargetRt()));
                    bean.setMaxRt(detail.getMaxRt());
                    bean.setMinRt(detail.getMinRt());
                    bean.setPassFlag((Optional.ofNullable(detail.getPassFlag()).orElse(0)));
                    bean.setDistribute(getDistributes(detail.getRtDistribute()));
                    bean.setApplicationIds(detail.getApplicationIds());
                    bean.setActivityId(detail.getBusinessActivityId());
                    bean.setFeatures(detail.getFeatures());
                    bean.setRtDistribute(detail.getRtDistribute());
                    bean.setSa(new DataBean(detail.getSa(), detail.getTargetSa()));
                    return bean;
                }).collect(Collectors.toList());
    }

    /**
     * 获取阶梯递增的阶段线程数
     *
     * @param reportId
     * @return
     */
    private Map<String, List<BigDecimal>> getSummaryConcurrentStageThreadNum(Long reportId) {
        ReportOutput reportOutput = selectById(reportId);
        PtConfigExt ext = JSON.parseObject(reportOutput.getPtConfig(), PtConfigExt.class);
        Map<String, ThreadGroupConfigExt> configMap = ext.getThreadGroupConfigMap();
        if (configMap == null || configMap.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, List<BigDecimal>> stages = new HashMap<>();
        for (Map.Entry<String, ThreadGroupConfigExt> entry : configMap.entrySet()) {
            ThreadGroupConfigExt value = entry.getValue();
            if (value.getType() == null || value.getType() != 0 || value.getMode() == null || value.getMode() != 3) {
                continue;
            }
            // 阶梯递增模式
            Integer steps = value.getSteps();
            Integer threadNum = value.getThreadNum();
            List<BigDecimal> stepList = new ArrayList<>(steps);
            for (Integer i = 1; i <= steps; i++) {
                stepList.add(new BigDecimal(threadNum * i).divide(new BigDecimal(steps), 2, BigDecimal.ROUND_HALF_UP));
            }
            stages.put(entry.getKey(), stepList);
        }
        return stages;

    }

    @Override
    public Map<String, Object> getReportWarnCount(Long reportId) {
        Map<String, Object> dataMap = tReportBusinessActivityDetailMapper.selectCountByReportId(reportId);
        if (MapUtils.isEmpty(dataMap)) {
            dataMap = Maps.newHashMap();
        }
        dataMap.put("warnCount", tWarnDetailMapper.countReportTotalWarn(reportId));
        return dataMap;
    }

    @Override
    public Long queryRunningReport(ContextExt contextExt) {
        Report report = tReportMapper.selectOneRunningReport(contextExt);
        return report == null ? null : report.getId();
    }

    @Override
    public List<Long> queryListRunningReport() {
        List<Report> report = tReportMapper.selectListRunningReport(CloudPluginUtils.getContext());
        return CollectionUtils.isEmpty(report) ? new ArrayList<>(0) : report.stream().map(Report::getId).collect(Collectors.toList());
    }

    @Override
    public List<Long> queryListPressuringReport() {
        List<Report> report = tReportMapper.selectListPressuringReport();
        return CollectionUtils.isEmpty(report) ? null : report.stream().map(Report::getId).collect(Collectors.toList());
    }

    @Override
    public Boolean lockReport(Long reportId) {
        log.info("web -> cloud lock reportId【{}】,starting", reportId);
        ReportResult reportResult = reportDao.selectById(reportId);
        if (checkReportError(reportResult)) {
            return false;
        }
        if (ReportConstants.RUN_STATUS != reportResult.getStatus()) {
            return false;
        }
        long lastUpdate = 0;
        if (null != reportResult.getGmtUpdate()) {
            lastUpdate = reportResult.getGmtUpdate().getTime();
        }
        //+10分钟,如果锁了10分钟了，则认为锁失效,可以重新锁
        lastUpdate += 10 * 60 * 1000;
        if (ReportConstants.LOCK_STATUS == reportResult.getLock() && lastUpdate > System.currentTimeMillis()) {
            log.error("异常代码【{}】,异常内容：锁定报告异常 --> 报告{}状态锁定状态，不能再次锁定",
                    TakinCloudExceptionEnum.TASK_STOP_VERIFY_ERROR, reportId);
            return false;
        }
        reportDao.updateReportLock(reportId, ReportConstants.LOCK_STATUS);
        log.info("报告{}锁定成功", reportId);
        return true;
    }

    private boolean checkReportError(ReportResult reportResult) {
        // 锁定报告前提也是要有结束时间
        if (reportResult == null) {
            log.error("io.shulie.takin.cloud.biz.service.report.impl.ReportServiceImpl#checkReportError"
                    + "reportResult 是 null");
            return true;
        }
        if (reportResult.getEndTime() == null) {
            log.error("报告{} endTime 为null", reportResult.getId());
            return true;
        }
        return false;
    }

    @Override
    public Boolean unLockReport(Long reportId) {
        ReportResult reportResult = reportDao.selectById(reportId);
        if (!reportResult.getType().equals(PressureSceneEnum.DEFAULT.getCode()) && ReportConstants.LOCK_STATUS != reportResult.getLock()) {
            log.error("异常代码【{}】,异常内容：解锁报告异常 --> 报告{}非锁定状态，不能解锁",
                    TakinCloudExceptionEnum.TASK_STOP_VERIFY_ERROR, reportId);
            return false;
        }
        // 解锁
        reportDao.updateReportLock(reportId, ReportConstants.RUN_STATUS);
        log.info("报告{}解锁成功", reportId);
        return true;
    }

    @Override
    public Boolean finishReport(Long reportId) {
        log.info("web -> cloud finish reportId【{}】,starting", reportId);
        ReportResult reportResult = reportDao.selectById(reportId);
        //只有常规模式需要生成报告内容
        if (!reportResult.getPressureType().equals(PressureSceneEnum.DEFAULT.getCode())) {
            reportDao.finishReport(reportId);
            cloudSceneManageService.updateSceneLifeCycle(
                    UpdateStatusBean.build(reportResult.getSceneId(), reportResult.getId(), reportResult.getTenantId())
                            .checkEnum(SceneManageStatusEnum.ENGINE_RUNNING,
                                    SceneManageStatusEnum.STOP).updateEnum(SceneManageStatusEnum.WAIT).build());
            doneReport(reportResult);
            pressureEnd(reportResult);
            return true;
        }
        if (checkReportError(reportResult)) {
            return false;
        }
        //判断报告是否已经汇总，如果没有汇总，先汇总报告，然后在更新报告状态
        if (Objects.isNull(reportResult.getEndTime())) {
            TaskResult taskResult = new TaskResult();
            taskResult.setSceneId(reportResult.getSceneId());
            taskResult.setTaskId(reportResult.getId());
            taskResult.setTenantId(reportResult.getTenantId());
            modifyReport(taskResult);
            reportResult = reportDao.selectById(reportId);
        } else if (Objects.isNull(reportResult.getTotalRequest()) && Objects.isNull(reportResult.getAvgTps())) {
            TaskResult taskResult = new TaskResult();
            taskResult.setSceneId(reportResult.getSceneId());
            taskResult.setTaskId(reportResult.getId());
            taskResult.setTenantId(reportResult.getTenantId());
            modifyReport(taskResult);
            reportResult = reportDao.selectById(reportId);
        }
        if (ReportConstants.RUN_STATUS != reportResult.getStatus()) {
            log.info("报告状态不正确：reportId=" + reportId + ", status=" + reportResult.getStatus());
            return false;
        }

        // 两个地方关闭压测引擎，版本不同，关闭方式不同
        //更新场景 压测引擎停止 ---> 待启动
        SceneManageEntity sceneManage = sceneManageDao.getSceneById(reportResult.getSceneId());
        //如果是强制停止 不需要更新
        log.info("finish scene {}, state :{}", reportResult.getSceneId(), Optional.ofNullable(sceneManage)
                .map(SceneManageEntity::getStatus)
                .map(SceneManageStatusEnum::getSceneManageStatusEnum)
                .map(SceneManageStatusEnum::getDesc).orElse("未找到场景"));
        if (sceneManage != null && !sceneManage.getType().equals(SceneManageStatusEnum.FORCE_STOP.getValue())) {
            cloudSceneManageService.updateSceneLifeCycle(
                    UpdateStatusBean.build(reportResult.getSceneId(), reportResult.getId(), reportResult.getTenantId())
                            .checkEnum(SceneManageStatusEnum.ENGINE_RUNNING, SceneManageStatusEnum.WAIT,
                                    SceneManageStatusEnum.STOP).updateEnum(SceneManageStatusEnum.WAIT).build());
        }
        //报告结束应该放在场景之后
        reportDao.finishReport(reportId);
        doneReport(reportResult);
        pressureEnd(reportResult);
        log.info("报告{} finish done", reportId);

        //报告结束的时候讲当前报告的结果作为基线写入基线表
        baseLineExecutorService.submit(() -> {
            this.sceneManageService.getBaseLineByReportIdAndInsert(reportId);
        });

//        baseLineProblemExecutorService.submit(() -> {
//            this.sceneManageService.getBaseLineProblemAndInsert(reportId);
//        });
        return true;
    }

    @Override
    public void forceFinishReport(Long reportId) {
        // 更新场景
        ReportResult reportResult = reportDao.selectById(reportId);
        // 完成报告
        if (reportResult.getStatus() != ReportConstants.FINISH_STATUS) {
            log.info("{}报告触发强制停止", reportId);
            reportDao.finishReport(reportId);
            doneReport(reportResult);
        }

        cloudSceneManageService.updateSceneLifeCycle(UpdateStatusBean.build(reportResult.getSceneId(), reportResult.getId(),
                        reportResult.getTenantId())
                .checkEnum(SceneManageStatusEnum.getAll()).updateEnum(SceneManageStatusEnum.FORCE_STOP).build());
        pressureEnd(reportResult);
    }

    /**
     * 实况报表取值
     *
     * @return -
     */
    private StatReportDTO statTempReport(Long jobId, Long sceneId, Long reportId, Long tenantId, String transaction) {
        String measurement = InfluxUtil.getMeasurement(jobId, sceneId, reportId, tenantId);
        String influxDbSql = "select"
                + " count as tempRequestCount, fail_count as failRequest, avg_tps as tps , avg_rt as avgRt, sa_count as saCount, active_threads as avgConcurrenceNum"
                + " from "
                + measurement
                + " where transaction = '" + transaction + "'"
                + " order by time desc limit 1";
        StatReportDTO statReportDTO = influxWriter.querySingle(influxDbSql, StatReportDTO.class);
        if (Objects.nonNull(statReportDTO)) {
            String totalRequestSql = "select sum(count) as totalRequest from "
                    + measurement
                    + " where  transaction = '" + transaction + "'"
                    + " order by time desc limit 1";
            StatReportDTO totalRequestDto = influxWriter.querySingle(totalRequestSql, StatReportDTO.class);
            if (Objects.nonNull(totalRequestDto) && Objects.nonNull(totalRequestDto.getTotalRequest())) {
                statReportDTO.setTotalRequest(totalRequestDto.getTotalRequest());
            } else {
                statReportDTO.setTotalRequest(0L);
            }
        }

        return statReportDTO;
    }

    /**
     * 获取最大并发数
     *
     * @param sceneId     场景ID
     * @param reportId    报告ID
     * @param customerId  租户ID
     * @param transaction 节点
     * @return -
     */
    private Integer getMaxConcurrence(Long jobId, Long sceneId, Long reportId, Long customerId, String transaction) {
        String influxDbSql = "select max(active_threads) as maxConcurrenceNum from "
                + InfluxUtil.getMeasurement(jobId, sceneId, reportId, customerId)
                + " where transaction = '" + transaction + "'"
                + " order by time desc limit 1";
        StatReportDTO statReportDTO = influxWriter.querySingle(influxDbSql, StatReportDTO.class);
        if (Objects.nonNull(statReportDTO)) {
            return statReportDTO.getMaxConcurrenceNum();
        }
        return 0;
    }

    /**
     * 查看报表实况
     *
     * @param reportTrendQuery 报表查询对象
     * @param isTempReport     是否实况报表
     * @return -
     */
    private ReportTrendResp queryReportTrend(ReportTrendQueryReq reportTrendQuery, boolean isTempReport) {
        long start = System.currentTimeMillis();
        ReportTrendResp reportTrend = new ReportTrendResp();
        ReportResult reportResult;
        try {
            if (isTempReport) {
                reportResult = reportDao.getTempReportBySceneId(reportTrendQuery.getSceneId());
            } else {
                reportResult = reportDao.selectById(reportTrendQuery.getReportId());
            }
            if (reportResult == null) {
                return new ReportTrendResp();
            }
            String testPlanXpathMd5 = getTestPlanXpathMd5V2(reportResult.getScriptNodeTree());
            String transaction = StringUtils.isBlank(testPlanXpathMd5) ? ReportConstants.ALL_BUSINESS_ACTIVITY
                    : testPlanXpathMd5;
            if (StringUtils.isNotBlank(reportTrendQuery.getXpathMd5())) {
                transaction = reportTrendQuery.getXpathMd5();
            }
            StringBuilder influxDbSql = new StringBuilder();
            influxDbSql.append("select");
            influxDbSql.append(
                    " sum(count) as tempRequestCount, sum(fail_count) as failRequest, mean(avg_tps) as tps , sum(sum_rt)/sum"
                            + "(count) as "
                            + "avgRt, sum(sa_count) as saCount, count(avg_rt) as recordCount ,mean(active_threads) as "
                            + "avgConcurrenceNum ");
            influxDbSql.append(" from ");
            influxDbSql.append(
                    InfluxUtil.getMeasurement(reportResult.getJobId(), reportResult.getSceneId(),
                            reportResult.getId(), reportResult.getTenantId()));
            influxDbSql.append(" where ");
            influxDbSql.append(" transaction = ").append("'").append(transaction).append("'");

            //按配置中的时间间隔分组
            influxDbSql.append(" group by time(").append(reportAggregationInterval).append(")");

            List<StatReportDTO> list = Lists.newArrayList();

            if (StringUtils.isNotEmpty(transaction)) {
                list = influxWriter.query(influxDbSql.toString(), StatReportDTO.class);
            }

            if (CollectionUtils.isEmpty(list)) {
                log.info("查询SQL：{}", influxDbSql);
                log.info("实时监测链路趋势：queryReportTrend-运行时间：{}", System.currentTimeMillis() - start);
                return new ReportTrendResp();
            }

            //influxdb 空数据也会返回,需要过滤空数据
            //前端要求的格式
            List<String> time = Lists.newLinkedList();
            List<String> sa = Lists.newLinkedList();
            List<String> avgRt = Lists.newLinkedList();
            List<String> tps = Lists.newLinkedList();
            List<String> successRate = Lists.newLinkedList();
            List<String> concurrent = Lists.newLinkedList();

            list.stream()
                    .filter(Objects::nonNull)
                    .filter(data -> data.getTps() != null)
                    .filter(data -> StringUtils.isNotBlank(data.getTime()))
                    .forEach(data -> {
                        time.add(getTime(data.getTime()));
                        sa.add(NumberUtil.decimalToString(data.getSa()));
                        avgRt.add(NumberUtil.decimalToString(data.getAvgRt()));
                        tps.add(NumberUtil.decimalToString(data.getTps()));
                        successRate.add(NumberUtil.decimalToString(data.getSuccessRate()));
                        concurrent.add(NumberUtil.decimalToString(data.getAvgConcurrenceNum()));
                    });
            //链路趋势
            reportTrend.setTps(tps);
            reportTrend.setSa(sa);
            reportTrend.setSuccessRate(successRate);
            reportTrend.setRt(avgRt);
            reportTrend.setTime(time);
            reportTrend.setConcurrent(concurrent);

            return reportTrend;
        } catch (Exception e) {
            log.error("queryReportTrend error:", e);
        }
        return null;
    }

    /**
     * 日期格式化ck
     *
     * @return -
     */
    private String getTime(String time) {
        long date = TimeUtil.fromInfluxDBTimeFormat(time);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(date));
        calendar.set(Calendar.HOUR, calendar.get(Calendar.HOUR));
        return DateUtil.formatTime(calendar.getTime());
    }

    /**
     * 压测时间格式化
     *
     * @return -
     */
    private String getTaskTime(Date startTime, Date endTime, Long totalTestTime) {
        LocalDateTime start = startTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime end = endTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        long seconds = Duration.between(start, end).getSeconds();
        if (seconds > totalTestTime) {
            seconds = totalTestTime;
        }
        long minutes = seconds / 60;
        long second = seconds % 60;
        return String.format("%d'%d\"", minutes, second < 0 ? 0 : second);
    }

    /**
     * 检查场景状态是否超时
     */
    public boolean checkSceneTaskIsTimeOut(ReportResult reportResult, SceneManageWrapperOutput scene) {
        long totalTestTime = scene.getTotalTestTime();
        long runTime = DateUtil.between(reportResult.getStartTime(), new Date(), DateUnit.SECOND);
        if (runTime >= totalTestTime + forceCloseTime) {
            log.info("report = {}，runTime = {} , totalTestTime= {},Timeout check", reportResult.getId(), runTime,
                    totalTestTime);
            return true;
        }
        return false;
    }

    /**
     * 通过场景iD和报告ID。获取压测中jmeter上报的数据
     *
     * @return -
     */
    @Override
    public List<Metrices> metric(TrendRequest req) {
        Long reportId = req.getReportId();
        Long sceneId = req.getSceneId();
        Long jobId = req.getJobId();
        List<Metrices> metricList = Lists.newArrayList();
        if (StringUtils.isBlank(String.valueOf(reportId))) {
            return metricList;
        }
        try {
            String measurement = InfluxUtil.getMeasurement(jobId, sceneId, reportId, CloudPluginUtils.getContext().getTenantId());
            metricList = influxWriter.query(
                    "select time,avg_tps as avgTps from " + measurement + " where transaction='all'", Metrices.class);
        } catch (Throwable e) {
            log.error("异常代码【{}】,异常内容：获取压测中jmeter上报的数据异常 --> influxdb数据查询异常: {}",
                    TakinCloudExceptionEnum.REPORT_GET_ERROR, e);
        }
        return metricList;
    }

    private void getReportFeatures(ReportResult reportResult, String errKey, String errMsg) {
        Map<String, String> map = Maps.newHashMap();
        if (StringUtils.isNotBlank(reportResult.getFeatures())) {
            map = JsonHelper.string2Obj(reportResult.getFeatures(), new TypeReference<Map<String, String>>() {
            });
        }
        if (StringUtils.isNotBlank(errKey) && StringUtils.isNotBlank(errMsg)) {
            errMsg = StringUtils.trim(errMsg);
            if (!errMsg.startsWith("[") && !errMsg.startsWith("{") && errMsg.length() > 100) {
                errMsg = errMsg.substring(0, 100);
            }
            map.put(errKey, errMsg);
            reportResult.setFeatures(GsonUtil.gsonToString(map));
        }
    }

    @Override
    public void updateReportFeatures(Long reportId, Integer status, String errKey, String errMsg) {
        ReportResult reportResult = reportDao.selectById(reportId);
        // 完成状态
        reportResult.setStatus(status);
        getReportFeatures(reportResult, errKey, errMsg);
        ReportUpdateParam param = BeanUtil.copyProperties(reportResult, ReportUpdateParam.class);
        reportDao.updateReport(param);
    }

    @Override
    public void updateReportConclusion(UpdateReportConclusionInput input) {
        ReportResult reportResult = reportDao.selectById(input.getId());
        if (reportResult == null) {
            throw new TakinCloudException(TakinCloudExceptionEnum.REPORT_GET_ERROR, "报告" + input.getId() + "不存在");
        }
        if (StringUtils.isNotBlank(input.getErrorMessage())) {
            getReportFeatures(reportResult, ReportConstants.FEATURES_ERROR_MSG, input.getErrorMessage());
        }
        ReportUpdateConclusionParam param = BeanUtil.copyProperties(input, ReportUpdateConclusionParam.class);
        param.setFeatures(reportResult.getFeatures());
        reportDao.updateReportConclusion(param);
    }

    @Override
    public void updateReportSlaData(UpdateReportSlaDataInput input) {
        ReportResult reportResult = reportDao.selectById(input.getReportId());
        if (reportResult == null) {
            throw new TakinCloudException(TakinCloudExceptionEnum.REPORT_GET_ERROR, "报告" + input.getReportId() + "不存在");
        }
        // 适配
        getReportFeatures(reportResult, ReportConstants.SLA_ERROR_MSG, JsonHelper.bean2Json(input.getSlaBean()));
        ReportUpdateParam param = new ReportUpdateParam();
        param.setId(input.getReportId());
        param.setFeatures(reportResult.getFeatures());
        param.setGmtUpdate(new Date());
        reportDao.updateReport(param);
    }

    /**
     * 生成报告
     * 报告更新，完成压测后的报告更新
     * 原来在场景调度那边，现在把报告放在这里面
     */
    @IntrestFor(event = "finished")
    public void doReportEvent(Event event) {
        try {
            // 等待时间 influxDB完成
            Thread.sleep(2000);
            long start = System.currentTimeMillis();
            TaskResult taskResult = (TaskResult) event.getExt();
            log.info("通知报告模块，开始生成本次压测{}-{}-{}的报告", taskResult.getSceneId(), taskResult.getTaskId(),
                    taskResult.getTenantId());
            modifyReport(taskResult);
            log.info("本次压测{}-{}-{}的报告生成时间-{}", taskResult.getSceneId(), taskResult.getTaskId(),
                    taskResult.getTenantId(), System.currentTimeMillis() - start);
        } catch (Exception e) {
            log.error("异常代码【{}】,异常内容：生成报告异常 --> 【通知报告模块】处理finished事件异常: {}",
                    TakinCloudExceptionEnum.TASK_STOP_DEAL_REPORT_ERROR, e);
        }
    }

    @Override
    public ReportOutput selectById(Long id) {
        ReportResult reportResult = reportDao.selectById(id);
        if (reportResult == null) {
            return null;
        }
        return BeanUtil.copyProperties(reportResult, ReportOutput.class);
    }

    @Override
    public void updateReportOnSceneStartFailed(Long sceneId, Long reportId, String errMsg) {
        ReportResult report = reportDao.getById(reportId);
        getReportFeatures(report, ReportConstants.FEATURES_ERROR_MSG, errMsg);
        reportDao.updateReport(new ReportUpdateParam() {{
            setSceneId(sceneId);
            setId(reportId);
            setStatus(ReportConstants.FINISH_STATUS);
            setFeatures(report.getFeatures());
        }});
    }

    /**
     * 获取报告的基础数据(基础表数据)
     *
     * @param reportId 报告主键
     * @return 基础表数据
     */
    @Override
    public ReportResult getReportBaseInfo(Long reportId) {
        return reportDao.selectById(reportId);
    }

    /**
     * 压测完成/结束 更新报告
     * 更新报表数据
     * V4.2.2以前版本的客户端，由cloud更新报告、场景状态
     * V4.2.2及以后版本的客户端，由web调用cloud，来更新报告、场景状态
     */
    //@Transactional(rollbackFor = Exception.class)
    public void modifyReport(TaskResult taskResult) {
        //保存报表状态为生成中
        Long reportId = taskResult.getTaskId();
        ReportResult reportResult = reportDao.selectById(reportId);
        if (reportResult == null) {
            log.error("not find reportId= {}", reportId);
            return;
        }
        // 默认为true
        boolean updateVersion = System.currentTimeMillis() >= 0;
        log.info("ReportId={}, tenantId={}, CompareResult={}", reportId, reportResult.getTenantId(), updateVersion);

        String testPlanXpathMd5 = getTestPlanXpathMd5V2(reportResult.getScriptNodeTree());
        String transaction = StringUtils.isBlank(testPlanXpathMd5) ? ReportConstants.ALL_BUSINESS_ACTIVITY
                : testPlanXpathMd5;
        //汇总所有业务活动数据
        Long jobId = reportResult.getJobId();
        StatReportDTO statReport = statReport(jobId, taskResult.getSceneId(),
                reportId, taskResult.getTenantId(), transaction);
        if (statReport == null) {
            log.warn("没有找到报表数据，报表生成失败。报告ID：{}", reportId);
        }

        //更新报表业务活动 isConclusion 指标是否通过
        boolean isConclusion = updateReportBusinessActivity(jobId, taskResult.getSceneId(), taskResult.getTaskId(),
                taskResult.getTenantId());
        //保存报表结果
        saveReportResult(reportResult, statReport, isConclusion);

        //先保存报告内容，再更新报告状态，防止报告内容没有填充，就触发finishReport操作
        if (updateVersion) {
            UpdateStatusBean reportStatus = new UpdateStatusBean();
            reportStatus.setResultId(reportId);
            reportStatus.setPreStatus(ReportConstants.INIT_STATUS);
            reportStatus.setAfterStatus(ReportConstants.RUN_STATUS);
            int row = tReportMapper.updateReportStatus(reportStatus);
            // modify by 李鹏
            // 添加TotalRequest不为null 保证报告是有数据的  20210707
            if (row != 1 && reportResult.getTotalRequest() != null) {
                log.error("异常代码【{}】,异常内容：更新报告到生成中状态异常 --> 报告{}状态非0,状态为:{}",
                        TakinCloudExceptionEnum.TASK_STOP_VERIFY_ERROR, reportId, reportResult.getStatus());
                return;
            }
            generateReport(reportResult.getTaskId());
        }

        if (!updateVersion) {
            log.info("旧版本-结束报告:{}", reportId);
            UpdateStatusBean reportStatus = new UpdateStatusBean();
            reportStatus.setResultId(reportId);
            reportStatus.setPreStatus(ReportConstants.INIT_STATUS);
            reportStatus.setAfterStatus(ReportConstants.FINISH_STATUS);
            tReportMapper.updateReportStatus(reportStatus);
            doneReport(reportResult);
            //更新场景 压测引擎停止压测---> 待启动  版本不一样，关闭不一样
            cloudSceneManageService.updateSceneLifeCycle(
                    UpdateStatusBean.build(reportResult.getSceneId(), reportResult.getId(), reportResult.getTenantId())
                            .checkEnum(
                                    SceneManageStatusEnum.STOP).updateEnum(SceneManageStatusEnum.WAIT).build());
        }

    }

    private Date getFinalDateTime(Long jobId, Long sceneId, Long reportId, Long customerId) {
        String influxDbSql = "select max(create_time) as time from "
                + InfluxUtil.getMeasurement(jobId, sceneId, reportId, customerId);
        StatReportDTO statReportDTO = influxWriter.querySingle(influxDbSql, StatReportDTO.class);
        Date date;
        if (Objects.nonNull(statReportDTO) && StringUtils.isNotBlank(statReportDTO.getTime())) {
            try {
                date = new Date(NumberUtil.parseLong(statReportDTO.getTime()));
            } catch (Exception e) {
                log.error("时间转换异常");
                date = new Date(System.currentTimeMillis());
            }
        } else {
            date = new Date(System.currentTimeMillis());
        }
        return date;
    }

    /**
     * 报表数据统计
     *
     * @param sceneId     场景ID
     * @param reportId    报表ID
     * @param customerId  顾客ID
     * @param transaction 业务活动
     * @return -
     */
    @Override
    public StatReportDTO statReport(Long jobId, Long sceneId, Long reportId, Long customerId, String transaction) {
        String influxDbSql = "select "
                + "sum(count)                   as totalRequest,"
                + "sum(count)                   as tempRequestCount,"
                + "sum(fail_count)              as failRequest,"
                + "mean(avg_tps)                as tps ,"
                + "sum(sum_rt)/sum(count)       as avgRt,"
                + "sum(sa_count)                as saCount,"
                + "max(avg_tps)                 as maxTps,"
                + "min(min_rt)                  as minRt,"
                + "max(max_rt)                  as maxRt,"
                + "count(avg_rt)                as recordCount,"
                + "max(active_threads)          as maxConcurrenceNum,"
                + "round(mean(active_threads))  as avgConcurrenceNum"
                + " from "
                + InfluxUtil.getMeasurement(jobId, sceneId, reportId, customerId)
                + " where transaction = '" + transaction + "'";

        return influxWriter.querySingle(influxDbSql, StatReportDTO.class);
    }

    /**
     * 更新报表业务活动并且判断是否满足业务指标
     *
     * @return -
     */
    @Override
        public boolean updateReportBusinessActivity(Long jobId, Long sceneId, Long reportId, Long tenantId) {
        //报表活动
        List<ReportBusinessActivityDetail> reportBusinessActivityDetails = tReportBusinessActivityDetailMapper
                .queryReportBusinessActivityDetailByReportId(reportId);

        //业务活动是否匹配
        boolean totalPassFlag = true;
        boolean passFlag;
        String tableName = InfluxUtil.getMeasurement(jobId, sceneId, reportId, tenantId);
        for (ReportBusinessActivityDetail reportBusinessActivityDetail : reportBusinessActivityDetails) {
            if (StringUtils.isBlank(reportBusinessActivityDetail.getBindRef())) {
                continue;
            }
            //统计某个业务活动的数据
            StatReportDTO data = statReport(jobId, sceneId, reportId, tenantId,
                    reportBusinessActivityDetail.getBindRef());
            if (data == null) {
                if (areAllTargetsZero(reportBusinessActivityDetail)) {
                    reportBusinessActivityDetail.setPassFlag(1);
//                    reportBusinessActivityDetail.setAvgConcurrenceNum(new BigDecimal(0));
//                    reportBusinessActivityDetail.setMaxRt(new BigDecimal(0));
//                    reportBusinessActivityDetail.setMaxTps(new BigDecimal(0));
//                    reportBusinessActivityDetail.setMinRt(new BigDecimal(0));
//                    reportBusinessActivityDetail.setTps(new BigDecimal(0));
//                    reportBusinessActivityDetail.setRt(new BigDecimal(0));
//                    reportBusinessActivityDetail.setSa(new BigDecimal(0));
//                    reportBusinessActivityDetail.setRequest(0L);
//                    reportBusinessActivityDetail.setSuccessRate(new BigDecimal(0));
                    tReportBusinessActivityDetailMapper.updateByPrimaryKeySelective(reportBusinessActivityDetail);
                    continue;
                }
                //如果有一个业务活动没有找到对应的数据，则认为压测不通过
                totalPassFlag = false;
                log.warn("没有找到匹配的压测数据：场景ID[{}],报告ID:[{}],业务活动:[{}]", sceneId, reportId, reportBusinessActivityDetail.getBindRef());
                continue;
            }
            //统计RT分布
            // TODO：调用Cloud接口
            Map<String, String> rtMap = reportEventService.queryAndCalcRtDistribute(tableName,
                    reportBusinessActivityDetail.getBindRef());
            //匹配报告业务的活动
            reportBusinessActivityDetail.setAvgConcurrenceNum(data.getAvgConcurrenceNum());
            reportBusinessActivityDetail.setMaxRt(data.getMaxRt());
            reportBusinessActivityDetail.setMaxTps(data.getMaxTps());
            reportBusinessActivityDetail.setMinRt(data.getMinRt());
            reportBusinessActivityDetail.setTps(data.getTps());
            reportBusinessActivityDetail.setRt(data.getAvgRt());
            reportBusinessActivityDetail.setSa(data.getSa());
            reportBusinessActivityDetail.setRequest(data.getTotalRequest());
            reportBusinessActivityDetail.setSuccessRate(data.getSuccessRate());
            if (MapUtils.isNotEmpty(rtMap)) {
                reportBusinessActivityDetail.setRtDistribute(JSON.toJSONString(rtMap));
            }
            passFlag = isPass(reportBusinessActivityDetail);
//            if (!passFlag){
//                log.info("updateReportBusinessActivity passFlag false,{}", JSON.toJSONString(reportBusinessActivityDetail));
//            }
            reportBusinessActivityDetail.setPassFlag(passFlag ? 1 : 0);
            tReportBusinessActivityDetailMapper.updateByPrimaryKeySelective(reportBusinessActivityDetail);
            if (!passFlag) {
                totalPassFlag = false;
            }
        }

        //联通版新报告 根据压测时间段，统计性能指标明细、RT分位明细
        ReportResult dbReport = reportDao.selectById(reportId);
        if (dbReport.getStartTime() != null && dbReport.getEndTime() != null) {
            Map<String, List<PressureTestTimeDTO>> timeMap = ParsePressureTimeByModeUtils.parsePtConfig2Map(dbReport.getStartTime(), dbReport.getEndTime(), dbReport.getPtConfig());
            List<ScriptNodeSummaryBean> refList = JSON.parseArray(dbReport.getScriptNodeTree(), ScriptNodeSummaryBean.class);
            //key=业务活动, value=线程组
            Map<String, String> httpThreadGroupMap = new HashMap<>();
            calcHttpAndThreadGroupRef(refList, httpThreadGroupMap);
            for (ReportBusinessActivityDetail detail : reportBusinessActivityDetails) {
                if (StringUtils.isBlank(detail.getBindRef())) {
                    continue;
                }
                List<PressureTestTimeDTO> timeList = timeMap.get(httpThreadGroupMap.get(detail.getBindRef()));
                if (CollectionUtils.isEmpty(timeList)) {
                    continue;
                }
                List<Map<String, Object>> stepList = new ArrayList<>();
                for (int i = 0; i < timeList.size(); i++) {
                    //统计某个业务活动的数据
                    long startTime = timeList.get(i).getStartTime().getTime();
                    long calcStartTime = startTime;
                    //查询>= <=，所以这里要+1
                    if (i > 0) {
                        calcStartTime = startTime + 1L;
                    } else {
                        calcStartTime = ReportTimeUtils.beforeStartTime(startTime);
                    }
                    long endTime = timeList.get(i).getEndTime().getTime();
                    long calcEndime = endTime;
                    if (i == timeList.size() - 1) {
                        calcEndime = ReportTimeUtils.afterEndTime(endTime);
                    }
                    Map<String, Object> stepMap = new HashMap<>();
                    StatReportDTO reportData = statReportByTimes(calcStartTime, calcEndime, jobId, sceneId, reportId, tenantId, detail.getBindRef());
                    Map<String, Integer> rtMap = reportEventService.queryAndCalcRtDistributeByTime(calcStartTime, calcEndime, jobId, detail.getBindRef());
                    stepMap.put("startTime", DateUtil.formatDateTime(DateUtil.date(startTime)));
                    stepMap.put("endTime", DateUtil.formatDateTime(DateUtil.date(endTime)));
                    if (reportData != null) {
                        stepMap.put("totalRequest", reportData.getTotalRequest());
                        stepMap.put("concurrent", reportData.getAvgConcurrenceNum());
                        stepMap.put("avgTps", reportData.getTps());
                        stepMap.put("minTps", reportData.getMinTps());
                        stepMap.put("maxTps", reportData.getMaxTps());
                        stepMap.put("avgRt", reportData.getAvgRt());
                        stepMap.put("minRt", reportData.getMinRt());
                        stepMap.put("maxRt", reportData.getMaxRt());
                        stepMap.put("successRate", reportData.getSuccessRate());
                        stepMap.put("sa", reportData.getSa());
                    }
                    if (MapUtils.isNotEmpty(rtMap)) {
                        stepMap.putAll(rtMap);
                    }
                    stepList.add(stepMap);
                }
                ReportBusinessActivityDetail updateDetail = new ReportBusinessActivityDetail();
                updateDetail.setId(detail.getId());
                Map<String, Object> targetMap = JSON.parseObject(detail.getRtDistribute(), Map.class);
                if (MapUtils.isEmpty(targetMap)) {
                    targetMap = new HashMap<>();
                }
                targetMap.put("stepData", stepList);
                updateDetail.setRtDistribute(JSON.toJSONString(targetMap));
                tReportBusinessActivityDetailMapper.updateByPrimaryKeySelective(updateDetail);

            }
        }
        return totalPassFlag;
    }

    private void calcHttpAndThreadGroupRef(List<ScriptNodeSummaryBean> dataList, Map<String, String> dataMap) {
        if (CollectionUtils.isEmpty(dataList)) {
            return;
        }
        dataList.forEach(data -> {
            if (data.getType().equals(NodeTypeEnum.THREAD_GROUP.name())) {
                String key = data.getXpathMd5();
                List<String> sampleList = new ArrayList<>();
                calcHttp(data.getChildren(), sampleList);
                if (CollectionUtils.isNotEmpty(sampleList)) {
                    sampleList.stream().forEach(sampler -> dataMap.put(sampler, key));
                }
            }
            calcHttpAndThreadGroupRef(data.getChildren(), dataMap);
        });
    }

    private void calcHttp(List<ScriptNodeSummaryBean> dataList, List<String> sampleList) {
        if (CollectionUtils.isEmpty(dataList)) {
            return;
        }
        dataList.forEach(data -> {
            if (data.getType().equals(NodeTypeEnum.SAMPLER.name())) {
                sampleList.add(data.getXpathMd5());
            }
            calcHttp(data.getChildren(), sampleList);
        });
    }

    public StatReportDTO statReportByTimes(Long startTime, Long endTime, Long jobId, Long sceneId, Long reportId, Long customerId, String transaction) {
        EnginePressureQuery enginePressureQuery = new EnginePressureQuery();
        enginePressureQuery.setJobId(jobId);
        enginePressureQuery.setEndTime(endTime);
        enginePressureQuery.setStartTime(startTime);
        enginePressureQuery.setJobId(jobId);
        enginePressureQuery.setTransaction(transaction);
        List<StatReportDTO> statReportDTOList = this.listEnginePressure(enginePressureQuery, StatReportDTO.class);
        if (CollectionUtils.isNotEmpty(statReportDTOList)) {
            statReportDTOList.forEach(statReportDTO -> {
                statReportDTO.setTempRequestCount(statReportDTO.getTotalRequest());
            });
        }
        return CollectionUtils.isNotEmpty(statReportDTOList) ? statReportDTOList.get(0) : null;
    }

    public <T> List<T> listEnginePressure(EnginePressureQuery query, Class<T> tClass) {
//        Instant startTimestampInstant = Instant.ofEpochSecond(query.getStartTime());
//        LocalDateTime startUtcDateTime = LocalDateTime.ofInstant(startTimestampInstant, ZoneOffset.UTC);
//        long startTimeUseInInFluxDB = startUtcDateTime.toInstant(ZoneOffset.of("+0")).toEpochMilli();
//
//        Instant endTimestampInstant = Instant.ofEpochSecond(query.getEndTime());
//        LocalDateTime endUtcDateTime = LocalDateTime.ofInstant(endTimestampInstant, ZoneOffset.UTC);
//        long endTimeUseInInFluxDB = endUtcDateTime.toInstant(ZoneOffset.of("+0")).toEpochMilli();

        String measurement = InfluxUtil.getMeasurement(query.getJobId(), null, null, null);
        StringBuilder sql = new StringBuilder();
        sql.append(" select ");
        sql.append(" sum(count) as totalRequest, ");
        sql.append(" sum(fail_count) as failRequest, ");
        sql.append(" mean(avg_tps) as tps, ");
        sql.append(" sum(sum_rt) as sumRt, ");
        sql.append(" sum(sa_count) as saCount, ");
        sql.append(" min(avg_tps) as minTps, ");
        sql.append(" max(avg_tps) as maxTps, ");
        sql.append(" min(min_rt) as minRt, ");
        sql.append(" max(max_rt) as maxRt, ");
        sql.append(" count(avg_rt) as recordCount, ");
        sql.append(" max(active_threads) as maxConcurrenceNum, ");
        sql.append(" mean(active_threads) as avgConcurrenceNum ");
        sql.append(" from ").append(measurement);
        sql.append(" where transaction = '").append(query.getTransaction()).append("'");
//        sql.append(" and time >= ").append(query.getStartTime());
//        sql.append(" and time <= ").append(query.getEndTime());
        log.info("listEnginePressure查询压测数据SQL:{}", sql);
        return influxWriter.query(sql.toString(), tClass);
    }

    @Override
    public List<ReportActivityResp> getActivities(List<Long> sceneIds) {
        if (CollectionUtils.isEmpty(sceneIds)) {
            return null;
        }
        List<ReportEntity> reportEntities = reportDao.queryReportBySceneIds(sceneIds);
        if (CollectionUtils.isNotEmpty(reportEntities)) {
            List<Long> reportIds = CommonUtil.getList(reportEntities, ReportEntity::getId);
            List<ReportBusinessActivityDetailEntity> activities = reportDao.getActivityByReportIds(reportIds);
            if (CollectionUtils.isNotEmpty(activities)) {
                Map<Long, List<ReportBusinessActivityDetailEntity>> activityMap = activities.stream().filter(
                                Objects::nonNull)
                        .collect(Collectors.groupingBy(ReportBusinessActivityDetailEntity::getReportId));
                return reportEntities.stream().filter(Objects::nonNull)
                        .map(entity -> {
                            ReportActivityResp resp = new ReportActivityResp();
                            resp.setSceneId(entity.getSceneId());
                            resp.setReportId(entity.getId());
                            resp.setJobId(entity.getJobId());
                            resp.setSceneName(entity.getSceneName());
                            List<ReportBusinessActivityDetailEntity> details = activityMap.get(entity.getId());
                            if (CollectionUtils.isNotEmpty(details)) {
                                List<ReportActivityResp.BusinessActivity> activityList = details.stream().filter(Objects::nonNull)
                                        .filter(detail -> detail.getBusinessActivityId() > 0)
                                        .map(detail -> {
                                            ReportActivityResp.BusinessActivity activity = new ReportActivityResp.BusinessActivity();
                                            activity.setActivityName(detail.getBusinessActivityName());
                                            activity.setBindRef(detail.getBindRef());
                                            return activity;
                                        }).collect(Collectors.toList());
                                resp.setBusinessActivityList(activityList);
                            }
                            return resp;
                        }).collect(Collectors.toList());
            }
        }
        return null;
    }

//    /**

//     *
//     * @return -
//     */


    /**
     * 判断活动是否满足预设指标
     * 活动是否满足预设指标
     * 1.目标成功率 < 实际成功率
     * 2.目标SA > 实际SA
     * 3.目标RT > 实际RT
     * 4.目标TPS < 实际TPS
     * @return 满足返回true,否则返回false
     */
    private static boolean isPass(ReportBusinessActivityDetail detail) {

        // 1. 检查指标目标值是否全为0
        if (areAllTargetsZero(detail)) {
            return true;
        }

        // 2. 检查每个指标
        if (!isSuccessRatePass(detail)) {
            return false;
        }

        if (!isSAPass(detail)) {
            return false;
        }

        if (!isRTPass(detail)) {
            return false;
        }

        if (!isTPSPass(detail)) {
            return false;
        }

        // 所有检查通过,满足预设指标
        return true;

    }

    // 检查目标值是否全部为0
    private static boolean areAllTargetsZero(ReportBusinessActivityDetail detail) {
        return detail.getTargetSuccessRate().compareTo(BigDecimal.ZERO) <= 0
                && detail.getTargetSa().compareTo(BigDecimal.ZERO) <= 0
                && detail.getTargetRt().compareTo(BigDecimal.ZERO) <= 0
                && detail.getTargetTps().compareTo(BigDecimal.ZERO) <= 0;
    }

    // 检查成功率指标
    private static boolean isSuccessRatePass(ReportBusinessActivityDetail detail) {
        return detail.getTargetSuccessRate().compareTo(detail.getSuccessRate()) <= 0;
    }

    // 检查SA指标
    private static boolean isSAPass(ReportBusinessActivityDetail detail) {
        return detail.getTargetSa().compareTo(detail.getSa()) <= 0;
    }

    // 检查RT指标
    private static boolean isRTPass(ReportBusinessActivityDetail detail) {
        return detail.getTargetRt().compareTo(detail.getRt()) >= 0;
    }

    // 检查TPS指标
    private static boolean isTPSPass(ReportBusinessActivityDetail detail) {
        return detail.getTargetTps().compareTo(detail.getTps()) <= 0;
    }


    /**
     * 保存报表结果
     */
    public void saveReportResult(ReportResult reportResult, StatReportDTO statReport, boolean isConclusion) {
        //SLA规则优先

        if (isSla(reportResult)) {
            reportResult.setConclusion(ReportConstants.FAIL);
            getReportFeatures(reportResult, ReportConstants.FEATURES_ERROR_MSG, "触发SLA终止规则");
        } else if (!isConclusion) {
            reportResult.setConclusion(ReportConstants.FAIL);
            getReportFeatures(reportResult, ReportConstants.FEATURES_ERROR_MSG, "业务活动指标不达标");
        } else {
            reportResult.setConclusion(ReportConstants.PASS);
        }

        String engineName = ScheduleConstants.getEngineName(reportResult.getSceneId(), reportResult.getId(),
                reportResult.getTenantId());
        String cacheString = stringRedisTemplate.opsForValue().get(engineName + ScheduleConstants.LAST_SIGN);
        Long eTime = cacheString == null ? null : Long.valueOf(cacheString);
        Date curDate;
        if (eTime != null) {
            curDate = new Date(eTime);
        } else {
            curDate = new Date();
        }
        long testRunTime = DateUtil.between(reportResult.getStartTime(), curDate, DateUnit.SECOND);

        if (null != statReport) {
            //保存报表数据
            reportResult.setTotalRequest(statReport.getTotalRequest());
            // 保留
            reportResult.setAvgRt(statReport.getAvgRt());
            reportResult.setAvgTps(statReport.getTps());
            reportResult.setSuccessRate(statReport.getSuccessRate());
            reportResult.setSa(statReport.getSa());
            reportResult.setId(reportResult.getId());
            reportResult.setAvgConcurrent(statReport.getAvgConcurrenceNum());
            reportResult.setConcurrent(statReport.getMaxConcurrenceNum());
            reportResult.setAvgConcurrent(statReport.getAvgConcurrenceNum());
            reportResult.setConcurrent(statReport.getMaxConcurrenceNum());
            //保存最大 最小tps、最大、最小rt到features中
            if (statReport.getMaxTps() != null) {
                getReportFeatures(reportResult, "maxTps", statReport.getMaxTps().toString());
            }
            if (statReport.getMinTps() != null) {
                getReportFeatures(reportResult, "minTps", statReport.getMinTps().toString());
            }
            if (statReport.getMaxRt() != null) {
                getReportFeatures(reportResult, "maxRt", statReport.getMaxRt().toString());
            }
            if (statReport.getMinRt() != null) {
                getReportFeatures(reportResult, "minRt", statReport.getMinRt().toString());
            }
        }
        reportResult.setGmtUpdate(new Date());

        //流量结算
        AssetExtApi assetExtApi = pluginManager.getExtension(AssetExtApi.class);
        if (null != assetExtApi) {
            AssetInvoiceExt<RealAssectBillExt> invoice = new AssetInvoiceExt<>();
            invoice.setSceneId(reportResult.getSceneId());
            invoice.setTaskId(reportResult.getId());
            invoice.setCustomerId(reportResult.getTenantId());
            invoice.setResourceId(reportResult.getId());
            invoice.setResourceType(AssetTypeEnum.PRESS_REPORT.getCode());
            invoice.setResourceName(AssetTypeEnum.PRESS_REPORT.getName());
            invoice.setOperateId(reportResult.getOperateId());
            invoice.setOperateName(reportResult.getOperateName());

            RealAssectBillExt bill = new RealAssectBillExt();
            bill.setTime(testRunTime);

            BigDecimal avgThreadNum;
            //如果有平均rt和平均tps，则:threadNum=tps*rt/1000,否则取报告中的平均线程数
            if (null != reportResult.getAvgTps() && null != reportResult.getAvgRt()) {
                avgThreadNum = reportResult.getAvgTps().multiply(reportResult.getAvgRt())
                        .divide(new BigDecimal(1000), 10, RoundingMode.HALF_UP);
            } else {
                avgThreadNum = reportResult.getAvgConcurrent();
            }
            bill.setAvgThreadNum(avgThreadNum);
            invoice.setData(bill);
            Response<BigDecimal> paymentRes = assetExtApi.payment(invoice);
            if (null != paymentRes && paymentRes.isSuccess()) {
                reportResult.setAmount(paymentRes.getData());
            }
        }
        //填充开始结束时间
        if (Objects.isNull(reportResult.getStartTime())) {
            reportResult.setStartTime(reportResult.getGmtCreate());
        }
        if (Objects.isNull(reportResult.getEndTime())) {
            Date finalDateTime = getFinalDateTime(reportResult.getJobId(), reportResult.getSceneId(),
                    reportResult.getId(), reportResult.getTenantId());
            if (Objects.isNull(finalDateTime) || finalDateTime.getTime() < reportResult.getStartTime().getTime()) {
                finalDateTime = new Date();
            }
            reportResult.setEndTime(finalDateTime);
        }
        // 更新，此处不更报告状态
        reportResult.setStatus(null);
        ReportUpdateParam param = BeanUtil.copyProperties(reportResult, ReportUpdateParam.class);
        reportDao.updateReport(param);
    }

    @Override
    public void addWarn(WarnCreateInput input) {
        WarnDetail warnDetail = BeanUtil.copyProperties(input, WarnDetail.class);
        warnDetail.setWarnTime(DateUtil.parseDateTime(input.getWarnTime()));
        warnDetail.setCreateTime(new Date());
        tWarnDetailMapper.insertSelective(warnDetail);
    }

    private boolean isSla(ReportResult reportResult) {
        if (StringUtils.isBlank(reportResult.getFeatures())) {
            return false;
        }
        JSONObject jsonObject = JSON.parseObject(reportResult.getFeatures());
        // sla熔断数据
        return jsonObject.containsKey(ReportConstants.SLA_ERROR_MSG)
                && StringUtils.isNotEmpty(jsonObject.getString(ReportConstants.SLA_ERROR_MSG));
    }

    private Map<String, Object> fillReportMap(ReportBusinessActivityDetail detail, Map<String, List<BigDecimal>> threadNumStages) {
        if (Objects.nonNull(detail)) {
            Map<String, Object> resultMap = new HashMap<>(13);
            resultMap.put("avgRt", new DataBean(detail.getRt(), detail.getTargetRt()));
            resultMap.put("sa", new DataBean(detail.getSa(), detail.getTargetSa()));
            resultMap.put("tps", new DataBean(detail.getTps(), detail.getTargetTps()));
            resultMap.put("maxRt", detail.getMaxRt());
            resultMap.put("minRt", detail.getMinRt());
            resultMap.put("maxTps", detail.getMaxTps());
            resultMap.put("activityId", detail.getBusinessActivityId());
            //采样器和设置过目标的控制器视作业务活动，才会对是否达标进行判断，否则按照达标判断
            if (detail.getBusinessActivityId() > -1) {
                resultMap.put("passFlag", Optional.ofNullable(detail.getPassFlag()).orElse(0));
            } else {
                resultMap.put("passFlag", 1);
            }
            resultMap.put("rtDistribute", detail.getRtDistribute());
            resultMap.put("totalRequest", detail.getRequest());
            resultMap.put("successRate", new DataBean(detail.getSuccessRate(), detail.getTargetSuccessRate()));
            resultMap.put("avgConcurrenceNum", detail.getAvgConcurrenceNum());
            resultMap.put("distribute", getDistributes(detail.getRtDistribute()));
            if (detail.getBusinessActivityId() > -1 && StringUtils.isNotBlank(detail.getApplicationIds())) {
                resultMap.put("applicationIds", detail.getApplicationIds());
            }
            String xpathMd5 = detail.getBindRef();
            if (threadNumStages.containsKey(xpathMd5)) {
                resultMap.put("concurrentStageThreadNum", threadNumStages.get(xpathMd5));
            }
            resultMap.put("features", detail.getFeatures());
            return resultMap;
        }
        return null;

    }

    private List<DistributeBean> getDistributes(String distributes) {
        if (StringUtils.isBlank(distributes)) {
            return Lists.newArrayList();
        }
        List<DistributeBean> result = new ArrayList<>();
        Map<String, String> distributeMap = JSON.parseObject(distributes, Map.class);
        Iterator<String> iterator = distributeMap.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next();
            if (key.equals("stepData")) {
                continue;
            }
            String value = distributeMap.get(key);
            DistributeBean distribute = new DistributeBean();
            distribute.setLable(key);
            distribute.setValue(COMPARE + value);
            result.add(distribute);
        }
        result.sort(((o1, o2) -> -o1.getLable().compareTo(o2.getLable())));
        return result;
    }

    private Map<String, Object> fillTempMap(StatReportDTO statReport, ReportBusinessActivityDetail detail) {
        if (Objects.isNull(detail)) {
            return null;
        }
        Map<String, Object> resultMap = new HashMap<>(6);
        if (Objects.nonNull(statReport)) {
            resultMap.put("avgRt", new DataBean(statReport.getAvgRt(), detail.getTargetRt()));
            resultMap.put("sa", new DataBean(statReport.getSa(), detail.getTargetSa()));
            resultMap.put("tps", new DataBean(statReport.getTps(), detail.getTargetTps()));
            resultMap.put("successRate", new DataBean(statReport.getSuccessRate(), detail.getTargetSuccessRate()));
            resultMap.put("avgConcurrenceNum", Optional.ofNullable(statReport.getAvgConcurrenceNum()).orElse(new BigDecimal(0)).toString());
            resultMap.put("totalRequest", statReport.getTotalRequest());
            resultMap.put("tempRequestCount", statReport.getTempRequestCount());
        } else {
            resultMap.put("avgRt", new DataBean("0", detail.getTargetRt()));
            resultMap.put("sa", new DataBean("0", detail.getTargetSa()));
            resultMap.put("tps", new DataBean("0", detail.getTargetTps()));
            resultMap.put("successRate", new DataBean("0", detail.getTargetSuccessRate()));
            resultMap.put("avgConcurrenceNum", "0");
            resultMap.put("tempRequestCount", 0L);
            resultMap.put("totalRequest", 0L);
        }
        return resultMap;
    }

    @Override
    public List<ScriptNodeTreeResp> getNodeTree(ScriptNodeTreeQueryReq req) {
        ReportResult reportResult;
        if (Objects.nonNull(req.getReportId())) {
            reportResult = reportDao.selectById(req.getReportId());
        } else {
            reportResult = reportDao.getReportBySceneId(req.getSceneId());
        }

        if (Objects.isNull(reportResult)) {
            if (Objects.isNull(req.getReportId())) {
                log.warn("查询报告节点树--未查询到正在运行的报告，场景ID:{}", req.getSceneId());
            }
            return null;
        }
        List<ReportBusinessActivityDetail> reportBusinessActivityDetails = tReportBusinessActivityDetailMapper
                .queryReportBusinessActivityDetailByReportId(reportResult.getId());
        if (CollectionUtils.isEmpty(reportBusinessActivityDetails)) {
            return null;
        }
        if (StringUtils.isNotBlank(reportResult.getScriptNodeTree())) {
            //需要把activityId塞到节点树里
            DocumentContext context = JsonPath.parse(reportResult.getScriptNodeTree());
            reportBusinessActivityDetails.stream()
                    .filter(Objects::nonNull)
                    .forEach(detail -> {
                        Map<String, Object> tmpMap = new HashMap<>(1);
                        tmpMap.put("businessActivityId", detail.getBusinessActivityId());
                        Map<String, Map<String, Object>> resultMap = new HashMap<>(1);
                        resultMap.put(detail.getBindRef(), tmpMap);
                        JsonPathUtil.putNodesToJson(context, resultMap);
                    });
            List<ScriptNodeTreeResp> result = JSONArray.parseArray(context.jsonString(), ScriptNodeTreeResp.class);
            if (result.size() == 1) {
                String ptConfigCopy = reportResult.getPtConfig();
                if (StringUtils.isBlank(ptConfigCopy)) {
                    // 获取场景
                    SceneManageEntity scene = sceneManageDao.getSceneById(reportResult.getSceneId());
                    // 校验场景是否存在
                    if (scene == null) {
                        throw new TakinCloudException(TakinCloudExceptionEnum.SCENE_MANAGE_GET_ERROR, "压测不存在:" + reportResult.getSceneId());
                    }
                    ptConfigCopy = scene.getPtConfig();
                }
                // 获取施压配置
                PtConfigExt ptConfig = JSON.parseObject(ptConfigCopy, PtConfigExt.class);
                // 遍历填充压力模式
                result.get(0).getChildren().forEach(t -> {
                    // 根据MD5获取线程组配置
                    ThreadGroupConfigExt threadGroupConfig = ptConfig.getThreadGroupConfigMap().get(t.getXpathMd5());
                    // 填充压力模式
                    fullScriptNodeTreePressureType(t, threadGroupConfig == null ? null : threadGroupConfig.getType());
                });
            }
            return result;
        } else {
            ScriptNodeTreeResp all = new ScriptNodeTreeResp();
            all.setTestName("全局趋势");
            all.setName("all");
            all.setXpathMd5("all");
            List<ScriptNodeTreeResp> list = reportBusinessActivityDetails.stream()
                    .filter(Objects::nonNull)
                    .map(detail -> new ScriptNodeTreeResp() {{
                        setName(detail.getBindRef());
                        setBusinessActivityId(detail.getBusinessActivityId());
                        setTestName(detail.getBusinessActivityName());
                        //兼容老版本，将后续趋势查询条件设置为bindRef
                        setXpathMd5(detail.getBindRef());
                    }}).collect(Collectors.toList());
            list.add(0, all);
            return list;
        }
    }

    /**
     * 填充脚本节点树的压力模式
     *
     * @param scriptNodeTree 树节点
     * @param target         压力模式
     */
    private void fullScriptNodeTreePressureType(ScriptNodeTreeResp scriptNodeTree, Integer target) {
        scriptNodeTree.setPressureType(target);
        if (scriptNodeTree.getChildren() != null) {
            scriptNodeTree.getChildren().forEach(t -> fullScriptNodeTreePressureType(t, target));
        }
    }

    @Override
    public String getJtlDownLoadUrl(Long reportId, boolean needZip) {
        ReportResult reportResult = reportDao.selectById(reportId);
        if (reportResult == null) {
            throw new TakinCloudException(TakinCloudExceptionEnum.FILE_ZIP_ERROR, "未找到报告");
        }
        String features = reportResult.getFeatures();
        if (StringUtils.isNotBlank(features)
                && JSONObject.parseObject(features).containsKey(PressureStartCache.FEATURES_MACHINE_PTL_PATH)) {
            return "";
        }
        String realJtlPath = pressureEngineJtlPath;
        String realLogPath = pressureEngineLogPath;

        // 获取集群上报的挂载目录并替换,此处不考虑分别独立挂载nfs的情况
        if (StringUtils.isNotBlank(features)) {
            JSONObject engineInfos = JSONObject.parseObject(features);
            String nfsRoot = engineInfos.getString(PressureStartCache.FEATURES_NFS_ROOT);
            if (StringUtils.isNotBlank(nfsRoot)) {
                realJtlPath = DataUtils.mergeDirPath(nfsRoot, "ptl");
                realLogPath = DataUtils.mergeDirPath(nfsRoot, "logs");
            }
        }

        // 1.查看是否有jtl.zip /nfs_dir/jtl/127/1637/pressure.jtl
        String jtlPath = realJtlPath + "/" + reportResult.getSceneId() + "/" + reportId;
        String logPath = realLogPath + "/" + reportResult.getSceneId() + "/" + reportId;
        Long jobId = reportResult.getJobId();
        if (Objects.nonNull(jobId)) {
            String resourceId = reportResult.getResourceId();
            jtlPath = realJtlPath + "/" + resourceId + "/" + jobId;
            logPath = realLogPath + "/" + resourceId + "/" + jobId;
        }

        if (new File(jtlPath + "/" + "Jmeter.tar.gz").exists()) {
            // 2.存在直接返回
            return jtlPath + "/" + "Jmeter.tar.gz";
        } else if (needZip) {
            // 开始压缩
            String command = StrUtil.indexedFormat("tar -czf  {0}/Jmeter.tar.gz -C {0} . -C {1} .", jtlPath, logPath);
            log.info("压测日志打包成文件:{}", command);
            Boolean result = LinuxHelper.executeLinuxCmd(command);
            if (result) {
                // 返回成功
                return jtlPath + "/" + "Jmeter.tar.gz";
            }
            throw new TakinCloudException(TakinCloudExceptionEnum.FILE_ZIP_ERROR, "查看" + jtlPath);
        } else {
            return "";
        }
    }

    @Override
    public Integer getReportStatusById(Long reportId) {
        ReportResult report = reportDao.selectById(reportId);
        if (report == null) {
            log.warn("获取报告异常，报告数据不存在。报告ID：{}", reportId);
            return null;
        }
        return report.getStatus();
    }

    @Override
    public void updateReportById(ReportUpdateParam report) {
        reportDao.updateReport(report);
    }

    @Override
    public ReportDetailOutput getByResourceId(String resourceId) {
        ReportResult report = reportDao.selectByResourceId(resourceId);
        if (Objects.isNull(report)) {
            return null;
        }
        return ReportConverter.INSTANCE.ofReportDetail(report);
    }

    // 此处判断状态已cloud的，amdb的压测流量明细不关心
    private void dealCalibrationStatus(ReportDetailOutput detail) {
        Integer status = detail.getCalibrationStatus();
        int calibration = 0;
        // 从右往左
        boolean thirdSet = Bits.isSet(status, 2);
        boolean fourthSet = Bits.isSet(status, 1);
        if ((thirdSet && fourthSet)) {
            calibration = 3;
        } else if (thirdSet) {
            calibration = 2;
        } else if (fourthSet) {
            calibration = 1;
        }
        detail.setCalibration(calibration);
    }

    @Override
    public List<ReportDetailOutput> getReportListBySceneId(Long sceneId) {
        List<ReportResult> resultList = reportDao.selectBySceneId(sceneId);
        List<ReportDetailOutput> outputList = new ArrayList<>();
        if (CollectionUtils.isEmpty(resultList)) {
            return outputList;
        }
        resultList.stream().forEach(result -> {
            ReportDetailOutput output = new ReportDetailOutput();
            output.setId(result.getId());
            output.setStartTime(DateUtil.formatDateTime(result.getStartTime()));
            output.setConcurrent(result.getConcurrent());
            outputList.add(output);
        });
        return outputList;
    }
}
