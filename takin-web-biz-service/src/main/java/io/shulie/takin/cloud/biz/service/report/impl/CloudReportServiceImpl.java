package io.shulie.takin.cloud.biz.service.report.impl;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.core.type.TypeReference;
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
import io.shulie.takin.cloud.data.model.mysql.ReportBusinessActivityDetailEntity;
import io.shulie.takin.cloud.data.model.mysql.ReportEntity;
import io.shulie.takin.web.common.util.RedisClientUtil;
import io.shulie.takin.cloud.common.utils.CloudPluginUtils;
import io.shulie.takin.cloud.common.utils.GsonUtil;
import io.shulie.takin.cloud.common.utils.JsonPathUtil;
import io.shulie.takin.cloud.common.utils.JsonUtil;
import io.shulie.takin.cloud.common.utils.NumberUtil;
import io.shulie.takin.cloud.common.utils.TestTimeUtil;
import io.shulie.takin.cloud.data.dao.report.ReportDao;
import io.shulie.takin.cloud.data.dao.scene.manage.SceneManageDAO;
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
import jodd.util.Bits;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.influxdb.impl.TimeUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

/**
 * @author ??????
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
     * ????????????????????????????????????
     */
    @Value("${scene.pressure.forceCloseTime: 20}")
    private Integer forceCloseTime;
    @Value("${pressure.engine.jtl.path:/data/nfs_dir/ptl}")
    private String pressureEngineJtlPath;
    @Value("${pressure.engine.log.path:/data/nfs_dir/logs}")
    private String pressureEngineLogPath;

    public static final String COMPARE = "<=";

    @Override
    public PageInfo<CloudReportDTO> listReport(ReportQueryReq param) {

        PageHelper.startPage(param.getPageNumber(), param.getPageSize());
        //????????????????????????????????????
        if (param.getType() == null) {
            param.setType(0);
        }
        // ??????????????????????????????
        if (StrUtil.isNotBlank(CloudPluginUtils.getContext().getFilterSql())) {
            param.setFilterSql(CloudPluginUtils.getContext().getFilterSql());
            // ?????????????????????
            if (param.getFilterSql().lastIndexOf("(") == 0
                    && param.getFilterSql().lastIndexOf(")") == param.getFilterSql().length() - 1) {
                param.setFilterSql(param.getFilterSql().substring(1, param.getFilterSql().length() - 1));
            }
        }
        param.setEnvCode(CloudPluginUtils.getContext().getEnvCode());
        param.setTenantId(CloudPluginUtils.getContext().getTenantId());
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
            log.warn("???????????????????????????????????????????????????ID???{}", reportId);
            return null;
        }
        ReportDetailOutput detail = ReportConverter.INSTANCE.ofReportDetail(report);

        //????????????
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
        //????????????????????????????????????????????????
        if (report.getStatus() != ReportConstants.FINISH_STATUS) {
            detail.setTaskStatus(ReportConstants.RUN_STATUS);
        }
        // sla????????????
        if (StringUtils.isNotBlank(report.getFeatures())) {
            JSONObject jsonObject = JSON.parseObject(report.getFeatures());
            if (jsonObject.containsKey(ReportConstants.SLA_ERROR_MSG)) {
                detail.setSlaMsg(
                    JsonHelper.json2Bean(jsonObject.getString(ReportConstants.SLA_ERROR_MSG), SlaBean.class));
            }
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
            if (bean.getActivityId() > 0) {
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
                result.add(summaryBean);
            }
            if (CollectionUtils.isNotEmpty(bean.getChildren())) {
                buildFailActivitiesByNodeDetails(bean.getChildren(), result);
            }
        }
    }

    private List<ScriptNodeSummaryBean> getReportNodeDetail(String scriptNodeTree, Long reportId) {
        List<ReportBusinessActivityDetail> activities = tReportBusinessActivityDetailMapper
            .queryReportBusinessActivityDetailByReportId(reportId);
        return getScriptNodeSummaryBeans(scriptNodeTree, activities);
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

        // ??????????????????
        ReportDetailOutput detailOutput = this.getReportByReportId(reportResult.getId());
        reportDetail.setStopReasons(getStopReasonBean(sceneId, detailOutput));
        // ??????sla????????????
        reportDetail.setSlaMsg(detailOutput.getSlaMsg());
        String testPlanXpathMd5 = getTestPlanXpathMd5(reportResult.getScriptNodeTree());
        String transaction = StringUtils.isBlank(testPlanXpathMd5) ? ReportConstants.ALL_BUSINESS_ACTIVITY
                : testPlanXpathMd5;
        Long jobId = reportResult.getJobId();
        StatReportDTO statReport = statTempReport(jobId, sceneId, reportResult.getId(), reportResult.getTenantId(),
                transaction);
        if (statReport == null) {
            log.warn("????????????:[{}]???????????????", reportResult.getId());
        } else {
            reportDetail.setTotalRequest(statReport.getTotalRequest());
            reportDetail.setAvgRt(statReport.getAvgRt());
            reportDetail.setAvgTps(statReport.getTps());
            reportDetail.setSa(statReport.getSa());
            reportDetail.setSuccessRate(statReport.getSuccessRate());
            reportDetail.setAvgConcurrent(statReport.getAvgConcurrenceNum());
        }
        reportDetail.setSceneName(wrapper.getPressureTestSceneName());
        //????????????
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

        // ???????????????
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
        //????????????????????????
        // boolean taskIsTimeOut = checkSceneTaskIsTimeOut(reportResult, wrapper);
        boolean taskIsTimeOut = false;
        if (wrapper.getStatus().intValue() == SceneManageStatusEnum.PRESSURE_TESTING.getValue().intValue() && taskIsTimeOut) {
            log.info("??????[{}]???????????????????????????????????????", reportResult.getId());
            //?????????????????????
            reportDetail.setTaskStatus(ReportConstants.RUN_STATUS);
            //????????????
            reportDetail.setTestTime(
                    String.format("%d'%d\"", wrapper.getTotalTestTime() / 60, wrapper.getTotalTestTime() % 60));

            // ????????????????????????????????????????????????????????????
            redisClientUtil.setString(PressureStartCache.getStopTaskMessageKey(sceneId), "????????????", 2, TimeUnit.MINUTES);
            sceneTaskEventService.callStopEvent(reportResult);
        }
        log.info("????????????metric?????????tempReportDetail-???????????????{}", System.currentTimeMillis() - start);
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

    /**
     * ??????????????????
     *
     * @param sceneId      ????????????
     * @param detailOutput ??????
     * @return -
     */
    private List<StopReasonBean> getStopReasonBean(Long sceneId, ReportDetailOutput detailOutput) {
        List<StopReasonBean> stopReasons = Lists.newArrayList();

        // ??????sla????????????
        if (detailOutput.getSlaMsg() != null) {
            StopReasonBean slaReasonBean = new StopReasonBean();
            slaReasonBean.setType(SceneStopReasonEnum.SLA.getType());
            slaReasonBean.setDescription(SceneStopReasonEnum.toSlaDesc(detailOutput.getSlaMsg()));
            stopReasons.add(slaReasonBean);
        }
        // ?????????????????????????????????
        String key = String.format(SceneTaskRedisConstants.SCENE_TASK_RUN_KEY + "%s_%s", sceneId, detailOutput.getId());
        Object errorObj = stringRedisTemplate.opsForHash().get(key, SceneTaskRedisConstants.SCENE_RUN_TASK_ERROR);
        if (Objects.nonNull(errorObj)) {
            // ????????????????????????????????????
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
     * SLA????????????
     *
     * @return -
     */
    private List<WarnBean> listWarn(Long reportId) {
        List<WarnBO> warnBOList = tWarnDetailMapper.summaryWarnByReportId(reportId);
        return ReportConverter.INSTANCE.ofWarn(warnBOList);
    }

    /**
     * ??????????????????
     *
     * @return -
     */
    @Override
    public NodeTreeSummaryResp getNodeSummaryList(Long reportId) {
        //???????????????????????????
        List<ReportBusinessActivityDetail> reportBusinessActivityDetailList = tReportBusinessActivityDetailMapper
                .queryReportBusinessActivityDetailByReportId(reportId);
        NodeTreeSummaryResp resp = new NodeTreeSummaryResp();
        if (CollectionUtils.isEmpty(reportBusinessActivityDetailList)) {
            return resp;
        }
        ReportResult reportResult = reportDao.selectById(reportId);
        if (Objects.isNull(reportResult)) {
            throw new TakinCloudException(TakinCloudExceptionEnum.REPORT_GET_ERROR, "??????????????????" + reportId);
        }
        resp.setScriptNodeSummaryBeans(getScriptNodeSummaryBeans(reportResult.getScriptNodeTree(),
                reportBusinessActivityDetailList));
        return resp;
    }

    /**
     * ????????????????????????
     *
     * @param nodeTree ?????????
     * @param details  ??????????????????
     * @return -
     */
    private List<ScriptNodeSummaryBean> getScriptNodeSummaryBeans(String nodeTree,
                                                                  List<ReportBusinessActivityDetail> details) {
        Map<String, Map<String, Object>> resultMap = new HashMap<>(details.size());
        if (StringUtils.isNotBlank(nodeTree)) {
            details.stream().filter(Objects::nonNull)
                    .forEach(detail -> {
                        Map<String, Object> objectMap = fillReportMap(detail);
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
                    bean.setSa(new DataBean(detail.getSa(), detail.getTargetSa()));
                    return bean;
                }).collect(Collectors.toList());
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
        log.info("web -> cloud lock reportId???{}???,starting", reportId);
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
        //+10??????,????????????10??????????????????????????????,???????????????
        lastUpdate += 10 * 60 * 1000;
        if (ReportConstants.LOCK_STATUS == reportResult.getLock() && lastUpdate > System.currentTimeMillis()) {
            log.error("???????????????{}???,????????????????????????????????? --> ??????{}???????????????????????????????????????",
                    TakinCloudExceptionEnum.TASK_STOP_VERIFY_ERROR, reportId);
            return false;
        }
        reportDao.updateReportLock(reportId, ReportConstants.LOCK_STATUS);
        log.info("??????{}????????????", reportId);
        return true;
    }

    private boolean checkReportError(ReportResult reportResult) {
        // ??????????????????????????????????????????
        if (reportResult == null) {
            log.error("io.shulie.takin.cloud.biz.service.report.impl.ReportServiceImpl#checkReportError"
                    + "reportResult ??? null");
            return true;
        }
        if (reportResult.getEndTime() == null) {
            log.error("??????{} endTime ???null", reportResult.getId());
            return true;
        }
        return false;
    }

    @Override
    public Boolean unLockReport(Long reportId) {
        ReportResult reportResult = reportDao.selectById(reportId);
        if (!reportResult.getType().equals(PressureSceneEnum.DEFAULT.getCode()) && ReportConstants.LOCK_STATUS != reportResult.getLock()) {
            log.error("???????????????{}???,????????????????????????????????? --> ??????{}??????????????????????????????",
                    TakinCloudExceptionEnum.TASK_STOP_VERIFY_ERROR, reportId);
            return false;
        }
        // ??????
        reportDao.updateReportLock(reportId, ReportConstants.RUN_STATUS);
        log.info("??????{}????????????", reportId);
        return true;
    }

    @Override
    public Boolean finishReport(Long reportId) {
        log.info("web -> cloud finish reportId???{}???,starting", reportId);
        ReportResult reportResult = reportDao.selectById(reportId);
        //??????????????????????????????????????????
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
        //???????????????????????????????????????????????????????????????????????????????????????????????????
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
            log.info("????????????????????????reportId=" + reportId + ", status=" + reportResult.getStatus());
            return false;
        }

        // ??????????????????????????????????????????????????????????????????
        //???????????? ?????????????????? ---> ?????????
        SceneManageEntity sceneManage = sceneManageDao.getSceneById(reportResult.getSceneId());
        //????????????????????? ???????????????
        log.info("finish scene {}, state :{}", reportResult.getSceneId(), Optional.ofNullable(sceneManage)
            .map(SceneManageEntity::getStatus)
            .map(SceneManageStatusEnum::getSceneManageStatusEnum)
            .map(SceneManageStatusEnum::getDesc).orElse("???????????????"));
        if (sceneManage != null && !sceneManage.getType().equals(SceneManageStatusEnum.FORCE_STOP.getValue())) {
            cloudSceneManageService.updateSceneLifeCycle(
                UpdateStatusBean.build(reportResult.getSceneId(), reportResult.getId(), reportResult.getTenantId())
                    .checkEnum(SceneManageStatusEnum.ENGINE_RUNNING, SceneManageStatusEnum.WAIT,
                        SceneManageStatusEnum.STOP).updateEnum(SceneManageStatusEnum.WAIT).build());
        }
        //????????????????????????????????????
        reportDao.finishReport(reportId);
        doneReport(reportResult);
        pressureEnd(reportResult);
        log.info("??????{} finish done", reportId);

        return true;
    }

    @Override
    public void forceFinishReport(Long reportId) {
        // ????????????
        ReportResult reportResult = reportDao.selectById(reportId);
        // ????????????
        if (reportResult.getStatus() != ReportConstants.FINISH_STATUS) {
            log.info("{}????????????????????????", reportId);
            reportDao.finishReport(reportId);
            doneReport(reportResult);
        }

        cloudSceneManageService.updateSceneLifeCycle(UpdateStatusBean.build(reportResult.getSceneId(), reportResult.getId(),
                reportResult.getTenantId())
            .checkEnum(SceneManageStatusEnum.getAll()).updateEnum(SceneManageStatusEnum.FORCE_STOP).build());
        pressureEnd(reportResult);
    }

    /**
     * ??????????????????
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
     * ?????????????????????
     *
     * @param sceneId     ??????ID
     * @param reportId    ??????ID
     * @param customerId  ??????ID
     * @param transaction ??????
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
     * ??????????????????
     *
     * @param reportTrendQuery ??????????????????
     * @param isTempReport     ??????????????????
     * @return -
     */
    private ReportTrendResp queryReportTrend(ReportTrendQueryReq reportTrendQuery, boolean isTempReport) {
        long start = System.currentTimeMillis();
        ReportTrendResp reportTrend = new ReportTrendResp();
        ReportResult reportResult;
        if (isTempReport) {
            reportResult = reportDao.getTempReportBySceneId(reportTrendQuery.getSceneId());
        } else {
            reportResult = reportDao.selectById(reportTrendQuery.getReportId());
        }
        if (reportResult == null) {
            return new ReportTrendResp();
        }
        String testPlanXpathMd5 = getTestPlanXpathMd5(reportResult.getScriptNodeTree());
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

        //?????????????????????????????????
        influxDbSql.append(" group by time(").append(reportAggregationInterval).append(")");

        List<StatReportDTO> list = Lists.newArrayList();

        if (StringUtils.isNotEmpty(transaction)) {
            list = influxWriter.query(influxDbSql.toString(), StatReportDTO.class);
        }

        //influxdb ?????????????????????,?????????????????????
        //?????????????????????
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
        //????????????
        reportTrend.setTps(tps);
        reportTrend.setSa(sa);
        reportTrend.setSuccessRate(successRate);
        reportTrend.setRt(avgRt);
        reportTrend.setTime(time);
        reportTrend.setConcurrent(concurrent);
        log.info("???????????????????????????queryReportTrend-???????????????{}", System.currentTimeMillis() - start);

        return reportTrend;
    }

    /**
     * ???????????????ck
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
     * ?????????????????????
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
     * ??????????????????????????????
     */
    public boolean checkSceneTaskIsTimeOut(ReportResult reportResult, SceneManageWrapperOutput scene) {
        long totalTestTime = scene.getTotalTestTime();
        long runTime = DateUtil.between(reportResult.getStartTime(), new Date(), DateUnit.SECOND);
        if (runTime >= totalTestTime + forceCloseTime) {
            log.info("report = {}???runTime = {} , totalTestTime= {},Timeout check", reportResult.getId(), runTime,
                    totalTestTime);
            return true;
        }
        return false;
    }

    /**
     * ????????????iD?????????ID??????????????????jmeter???????????????
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
            log.error("???????????????{}???,??????????????????????????????jmeter????????????????????? --> influxdb??????????????????: {}",
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
        // ????????????
        reportResult.setStatus(status);
        getReportFeatures(reportResult, errKey, errMsg);
        ReportUpdateParam param = BeanUtil.copyProperties(reportResult, ReportUpdateParam.class);
        reportDao.updateReport(param);
    }

    @Override
    public void updateReportConclusion(UpdateReportConclusionInput input) {
        ReportResult reportResult = reportDao.selectById(input.getId());
        if (reportResult == null) {
            throw new TakinCloudException(TakinCloudExceptionEnum.REPORT_GET_ERROR, "??????" + input.getId() + "?????????");
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
            throw new TakinCloudException(TakinCloudExceptionEnum.REPORT_GET_ERROR, "??????" + input.getReportId() + "?????????");
        }
        // ??????
        getReportFeatures(reportResult, ReportConstants.SLA_ERROR_MSG, JsonHelper.bean2Json(input.getSlaBean()));
        ReportUpdateParam param = new ReportUpdateParam();
        param.setId(input.getReportId());
        param.setFeatures(reportResult.getFeatures());
        param.setGmtUpdate(new Date());
        reportDao.updateReport(param);
    }

    /**
     * ????????????
     * ?????????????????????????????????????????????
     * ????????????????????????????????????????????????????????????
     */
    @IntrestFor(event = "finished")
    public void doReportEvent(Event event) {
        try {
            // ???????????? influxDB??????
            Thread.sleep(2000);
            long start = System.currentTimeMillis();
            TaskResult taskResult = (TaskResult)event.getExt();
            log.info("?????????????????????????????????????????????{}-{}-{}?????????", taskResult.getSceneId(), taskResult.getTaskId(),
                taskResult.getTenantId());
            modifyReport(taskResult);
            log.info("????????????{}-{}-{}?????????????????????-{}", taskResult.getSceneId(), taskResult.getTaskId(),
                taskResult.getTenantId(), System.currentTimeMillis() - start);
        } catch (Exception e) {
            log.error("???????????????{}???,????????????????????????????????? --> ??????????????????????????????finished????????????: {}",
                TakinCloudExceptionEnum.TASK_STOP_DEAL_REPORT_ERROR, e);
        }
    }

    @Override
    public ReportOutput selectById(Long id) {
        ReportResult reportResult = reportDao.selectById(id);
        if (reportResult == null) {return null;}
        return BeanUtil.copyProperties(reportResult, ReportOutput.class);
    }

    @Override
    public void updateReportOnSceneStartFailed(Long sceneId, Long reportId, String errMsg) {
        reportDao.updateReport(new ReportUpdateParam() {{
            setSceneId(sceneId);
            setId(reportId);
            setStatus(ReportConstants.FINISH_STATUS);
            JSONObject errorMsg = new JSONObject();
            errorMsg.put(ReportConstants.FEATURES_ERROR_MSG, errMsg);
            setFeatures(errorMsg.toJSONString());
        }});
    }

    /**
     * ???????????????????????????(???????????????)
     *
     * @param reportId ????????????
     * @return ???????????????
     */
    @Override
    public ReportResult getReportBaseInfo(Long reportId) {
        return reportDao.selectById(reportId);
    }

    /**
     * ????????????/?????? ????????????
     * ??????????????????
     * V4.2.2??????????????????????????????cloud???????????????????????????
     * V4.2.2?????????????????????????????????web??????cloud?????????????????????????????????
     */
    //@Transactional(rollbackFor = Exception.class)
    public void modifyReport(TaskResult taskResult) {
        //??????????????????????????????
        Long reportId = taskResult.getTaskId();
        ReportResult reportResult = reportDao.selectById(reportId);
        if (reportResult == null) {
            log.error("not find reportId= {}", reportId);
            return;
        }
        // ?????????true
        boolean updateVersion = System.currentTimeMillis() >= 0;
        log.info("ReportId={}, tenantId={}, CompareResult={}", reportId, reportResult.getTenantId(), updateVersion);

        String testPlanXpathMd5 = getTestPlanXpathMd5(reportResult.getScriptNodeTree());
        String transaction = StringUtils.isBlank(testPlanXpathMd5) ? ReportConstants.ALL_BUSINESS_ACTIVITY
                : testPlanXpathMd5;
        //??????????????????????????????
        Long jobId = reportResult.getJobId();
        StatReportDTO statReport = statReport(jobId, taskResult.getSceneId(),
                reportId, taskResult.getTenantId(), transaction);
        if (statReport == null) {
            log.warn("??????????????????????????????????????????????????????ID???{}", reportId);
        }

        //???????????????????????? isConclusion ??????????????????
        boolean isConclusion = updateReportBusinessActivity(jobId, taskResult.getSceneId(), taskResult.getTaskId(),
                taskResult.getTenantId());
        //??????????????????
        saveReportResult(reportResult, statReport, isConclusion);

        //??????????????????????????????????????????????????????????????????????????????????????????finishReport??????
        if (updateVersion) {
            UpdateStatusBean reportStatus = new UpdateStatusBean();
            reportStatus.setResultId(reportId);
            reportStatus.setPreStatus(ReportConstants.INIT_STATUS);
            reportStatus.setAfterStatus(ReportConstants.RUN_STATUS);
            int row = tReportMapper.updateReportStatus(reportStatus);
            // modify by ??????
            // ??????TotalRequest??????null ???????????????????????????  20210707
            if (row != 1 && reportResult.getTotalRequest() != null) {
                log.error("???????????????{}???,??????????????????????????????????????????????????? --> ??????{}?????????0,?????????:{}",
                        TakinCloudExceptionEnum.TASK_STOP_VERIFY_ERROR, reportId, reportResult.getStatus());
                return;
            }
            generateReport(reportResult.getTaskId());
        }

        if (!updateVersion) {
            log.info("?????????-????????????:{}", reportId);
            UpdateStatusBean reportStatus = new UpdateStatusBean();
            reportStatus.setResultId(reportId);
            reportStatus.setPreStatus(ReportConstants.INIT_STATUS);
            reportStatus.setAfterStatus(ReportConstants.FINISH_STATUS);
            tReportMapper.updateReportStatus(reportStatus);
            doneReport(reportResult);
            //???????????? ????????????????????????---> ?????????  ?????????????????????????????????
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
                log.error("??????????????????");
                date = new Date(System.currentTimeMillis());
            }
        } else {
            date = new Date(System.currentTimeMillis());
        }
        return date;
    }

    /**
     * ??????????????????
     *
     * @param sceneId     ??????ID
     * @param reportId    ??????ID
     * @param customerId  ??????ID
     * @param transaction ????????????
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
     * ????????????????????????????????????????????????????????????
     *
     * @return -
     */
    @Override
    public boolean updateReportBusinessActivity(Long jobId, Long sceneId, Long reportId, Long tenantId) {
        //????????????
        List<ReportBusinessActivityDetail> reportBusinessActivityDetails = tReportBusinessActivityDetailMapper
                .queryReportBusinessActivityDetailByReportId(reportId);

        //????????????????????????
        boolean totalPassFlag = true;
        boolean passFlag;
        String tableName = InfluxUtil.getMeasurement(jobId, sceneId, reportId, tenantId);
        for (ReportBusinessActivityDetail reportBusinessActivityDetail : reportBusinessActivityDetails) {
            if (StringUtils.isBlank(reportBusinessActivityDetail.getBindRef())) {
                continue;
            }
            //?????????????????????????????????
            StatReportDTO data = statReport(jobId, sceneId, reportId, tenantId,
                    reportBusinessActivityDetail.getBindRef());
            if (data == null) {
                //?????????????????????????????????????????????????????????????????????????????????
                totalPassFlag = false;
                log.warn("??????????????????????????????????????????ID[{}],??????ID:[{}],????????????:[{}]", sceneId, reportId,
                        reportBusinessActivityDetail.getBindRef());
                continue;
            }
            //??????RT??????
            // TODO?????????Cloud??????
            Map<String, String> rtMap = reportEventService.queryAndCalcRtDistribute(tableName,
                    reportBusinessActivityDetail.getBindRef());
            //???????????????????????????
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
            reportBusinessActivityDetail.setPassFlag(passFlag ? 1 : 0);
            tReportBusinessActivityDetailMapper.updateByPrimaryKeySelective(reportBusinessActivityDetail);
            if (!passFlag) {
                totalPassFlag = false;
            }
        }
        return totalPassFlag;
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

    /**
     * ??????????????????????????????
     * 1.??????????????? < ???????????????
     * 2.??????SA > ??????SA
     * 3.??????RT > ??????RT
     * 4.??????TPS < ??????TPS
     *
     * @return -
     */
    private boolean isPass(ReportBusinessActivityDetail detail) {
        if (isTargetBiggerThanZero(detail.getTargetSuccessRate()) && detail.getTargetSuccessRate().compareTo(
            detail.getSuccessRate()) > 0) {
            return false;
        } else if (isTargetBiggerThanZero(detail.getTargetSa()) && detail.getTargetSa().compareTo(detail.getSa()) > 0) {
            return false;
        } else if (isTargetBiggerThanZero(detail.getTargetRt()) && detail.getTargetRt().compareTo(detail.getRt()) < 0) {
            return false;
        } else {
            return !isTargetBiggerThanZero(detail.getTargetTps()) || detail.getTargetTps().compareTo(detail.getTps()) <= 0;
        }
    }

    private boolean isTargetBiggerThanZero(BigDecimal target) {
        if (Objects.nonNull(target)) {
            return target.compareTo(new BigDecimal(0)) > 0;
        }
        return false;
    }

    /**
     * ??????????????????
     */
    public void saveReportResult(ReportResult reportResult, StatReportDTO statReport, boolean isConclusion) {
        //SLA????????????

        if (isSla(reportResult)) {
            reportResult.setConclusion(ReportConstants.FAIL);
            getReportFeatures(reportResult, ReportConstants.FEATURES_ERROR_MSG, "??????SLA????????????");
        } else if (!isConclusion) {
            reportResult.setConclusion(ReportConstants.FAIL);
            getReportFeatures(reportResult, ReportConstants.FEATURES_ERROR_MSG, "???????????????????????????");
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
            //??????????????????
            reportResult.setTotalRequest(statReport.getTotalRequest());
            // ??????
            reportResult.setAvgRt(statReport.getAvgRt());
            reportResult.setAvgTps(statReport.getTps());
            reportResult.setSuccessRate(statReport.getSuccessRate());
            reportResult.setSa(statReport.getSa());
            reportResult.setId(reportResult.getId());
            reportResult.setAvgConcurrent(statReport.getAvgConcurrenceNum());
            reportResult.setConcurrent(statReport.getMaxConcurrenceNum());
        }
        reportResult.setGmtUpdate(new Date());

        //????????????
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
            //???????????????rt?????????tps??????:threadNum=tps*rt/1000,????????????????????????????????????
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
        //????????????????????????
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
        // ?????????????????????????????????
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
        // sla????????????
        return jsonObject.containsKey(ReportConstants.SLA_ERROR_MSG)
                && StringUtils.isNotEmpty(jsonObject.getString(ReportConstants.SLA_ERROR_MSG));
    }

    private Map<String, Object> fillReportMap(ReportBusinessActivityDetail detail) {
        if (Objects.nonNull(detail)) {
            Map<String, Object> resultMap = new HashMap<>(13);
            resultMap.put("avgRt", new DataBean(detail.getRt(), detail.getTargetRt()));
            resultMap.put("sa", new DataBean(detail.getSa(), detail.getTargetSa()));
            resultMap.put("tps", new DataBean(detail.getTps(), detail.getTargetTps()));
            resultMap.put("maxRt", detail.getMaxRt());
            resultMap.put("minRt", detail.getMinRt());
            resultMap.put("maxTps", detail.getMaxTps());
            resultMap.put("activityId", detail.getBusinessActivityId());
            //????????????????????????????????????????????????????????????????????????????????????????????????????????????????????????
            if (detail.getBusinessActivityId() > -1) {
                resultMap.put("passFlag", Optional.ofNullable(detail.getPassFlag()).orElse(0));
            } else {
                resultMap.put("passFlag", 1);
            }
            resultMap.put("totalRequest", detail.getRequest());
            resultMap.put("successRate", new DataBean(detail.getSuccessRate(), detail.getTargetSuccessRate()));
            resultMap.put("avgConcurrenceNum", detail.getAvgConcurrenceNum());
            resultMap.put("distribute", getDistributes(detail.getRtDistribute()));
            if (detail.getBusinessActivityId() > -1 && StringUtils.isNotBlank(detail.getApplicationIds())) {
                resultMap.put("applicationIds", detail.getApplicationIds());
            }
            return resultMap;
        }
        return null;

    }

    private List<DistributeBean> getDistributes(String distributes) {
        List<DistributeBean> result;
        if (StringUtils.isNoneBlank(distributes)) {
            Map<String, String> distributeMap = JsonHelper.string2Obj(distributes,
                new TypeReference<Map<String, String>>() {});
            List<DistributeBean> distributeBeans = Lists.newArrayList();
            distributeMap.forEach((key, value) -> {
                DistributeBean distribute = new DistributeBean();
                distribute.setLable(key);
                distribute.setValue(COMPARE + value);
                distributeBeans.add(distribute);
            });
            distributeBeans.sort(((o1, o2) -> -o1.getLable().compareTo(o2.getLable())));
            result = distributeBeans;
        } else {
            result = Lists.newArrayList();
        }
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
            resultMap.put("avgConcurrenceNum", statReport.getAvgConcurrenceNum().toString());
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
                log.warn("?????????????????????--??????????????????????????????????????????ID:{}", req.getSceneId());
            }
            return null;
        }
        List<ReportBusinessActivityDetail> reportBusinessActivityDetails = tReportBusinessActivityDetailMapper
                .queryReportBusinessActivityDetailByReportId(reportResult.getId());
        if (CollectionUtils.isEmpty(reportBusinessActivityDetails)) {
            return null;
        }
        if (StringUtils.isNotBlank(reportResult.getScriptNodeTree())) {
            //?????????activityId??????????????????
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
                    // ????????????
                    SceneManageEntity scene = sceneManageDao.getSceneById(reportResult.getSceneId());
                    // ????????????????????????
                    if (scene == null) {
                        throw new TakinCloudException(TakinCloudExceptionEnum.SCENE_MANAGE_GET_ERROR, "???????????????:" + reportResult.getSceneId());
                    }
                    ptConfigCopy = scene.getPtConfig();
                }
                // ??????????????????
                PtConfigExt ptConfig = JSON.parseObject(ptConfigCopy, PtConfigExt.class);
                // ????????????????????????
                result.get(0).getChildren().forEach(t -> {
                    // ??????MD5?????????????????????
                    ThreadGroupConfigExt threadGroupConfig = ptConfig.getThreadGroupConfigMap().get(t.getXpathMd5());
                    // ??????????????????
                    fullScriptNodeTreePressureType(t, threadGroupConfig == null ? null : threadGroupConfig.getType());
                });
            }
            return result;
        } else {
            ScriptNodeTreeResp all = new ScriptNodeTreeResp();
            all.setTestName("????????????");
            all.setName("all");
            all.setXpathMd5("all");
            List<ScriptNodeTreeResp> list = reportBusinessActivityDetails.stream()
                    .filter(Objects::nonNull)
                    .map(detail -> new ScriptNodeTreeResp() {{
                        setName(detail.getBindRef());
                        setBusinessActivityId(detail.getBusinessActivityId());
                        setTestName(detail.getBusinessActivityName());
                        //??????????????????????????????????????????????????????bindRef
                        setXpathMd5(detail.getBindRef());
                    }}).collect(Collectors.toList());
            list.add(0, all);
            return list;
        }
    }

    /**
     * ????????????????????????????????????
     *
     * @param scriptNodeTree ?????????
     * @param target         ????????????
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
            throw new TakinCloudException(TakinCloudExceptionEnum.FILE_ZIP_ERROR, "???????????????");
        }
        // 1.???????????????jtl.zip /nfs_dir/jtl/127/1637/pressure.jtl
        String jtlPath = pressureEngineJtlPath + "/" + reportResult.getSceneId() + "/" + reportId;
        String logPath = pressureEngineLogPath + "/" + reportResult.getSceneId() + "/" + reportId;
        Long jobId = reportResult.getJobId();
        if (Objects.nonNull(jobId)) {
            String resourceId = reportResult.getResourceId();
            jtlPath = pressureEngineJtlPath + "/" + resourceId + "/" + jobId;
            logPath = pressureEngineLogPath + "/" + resourceId + "/" + jobId;
        }
        if (new File(jtlPath + "/" + "Jmeter.zip").exists()) {
            // 2.??????????????????
            return jtlPath + "/" + "Jmeter.zip";
        } else if (needZip) {
            // ????????????
            String command = StrUtil.indexedFormat("sudo zip -r -j {0}/Jmeter.zip {0} {1}", jtlPath, logPath);
            log.info("???????????????????????????:{}", command);
            Boolean result = LinuxHelper.executeLinuxCmd(command);
            if (result) {
                // ????????????
                return jtlPath + "/" + "Jmeter.zip";
            }
            throw new TakinCloudException(TakinCloudExceptionEnum.FILE_ZIP_ERROR, "??????" + jtlPath);
        } else {return "";}
    }

    @Override
    public Integer getReportStatusById(Long reportId) {
        ReportResult report = reportDao.selectById(reportId);
        if (report == null) {
            log.warn("???????????????????????????????????????????????????ID???{}", reportId);
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

    // ?????????????????????cloud??????amdb??????????????????????????????
    private void dealCalibrationStatus(ReportDetailOutput detail) {
        Integer status = detail.getCalibrationStatus();
        int calibration = 0;
        // ????????????
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
}
