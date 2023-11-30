package io.shulie.takin.web.biz.service.report.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.gson.reflect.TypeToken;
import com.pamirs.takin.cloud.entity.dao.report.TReportBusinessActivityDetailMapper;
import com.pamirs.takin.cloud.entity.domain.entity.report.ReportBusinessActivityDetail;
import io.shulie.takin.adapter.api.model.ScriptNodeSummaryBean;
import io.shulie.takin.adapter.api.model.common.DataBean;
import io.shulie.takin.adapter.api.model.response.report.*;
import io.shulie.takin.cloud.biz.service.report.CloudReportService;
import io.shulie.takin.cloud.common.utils.JsonPathUtil;
import io.shulie.takin.cloud.data.mapper.mysql.ReportMapper;
import io.shulie.takin.cloud.data.mapper.mysql.SceneManageMapper;
import io.shulie.takin.cloud.data.model.mysql.ReportBusinessActivityDetailEntity;
import io.shulie.takin.cloud.data.model.mysql.ReportEntity;
import io.shulie.takin.cloud.data.model.mysql.SceneManageEntity;
import io.shulie.takin.cloud.data.result.report.ReportResult;
import io.shulie.takin.cloud.ext.content.enginecall.PtConfigExt;
import io.shulie.takin.cloud.ext.content.enginecall.ThreadGroupConfigExt;
import io.shulie.takin.cloud.ext.content.enums.NodeTypeEnum;
import io.shulie.takin.cloud.ext.content.script.ScriptNode;
import io.shulie.takin.web.biz.pojo.dto.scene.EngineMetricsDTO;
import io.shulie.takin.web.biz.pojo.dto.scene.EnginePressureQuery;
import io.shulie.takin.web.biz.pojo.output.report.*;
import io.shulie.takin.web.biz.pojo.request.report.ReportLinkDiagramReq2;
import io.shulie.takin.web.biz.pojo.request.report.RiskListQueryRequest;
import io.shulie.takin.web.biz.pojo.response.application.ApplicationEntranceTopologyResponse;
import io.shulie.takin.web.biz.pojo.response.report.RiskItemExtractionVO;
import io.shulie.takin.web.biz.utils.SreHelper;
import io.shulie.takin.web.common.SrePageData;
import io.shulie.takin.web.common.SreResponse;
import io.shulie.takin.web.common.enums.activity.info.FlowTypeEnum;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.pamirs.takin.common.constant.VerifyResultStatusEnum;
import com.pamirs.takin.entity.domain.dto.report.LeakVerifyResult;
import com.pamirs.takin.entity.domain.dto.report.ReportDTO;
import com.pamirs.takin.entity.domain.vo.report.ReportQueryParam;
import io.shulie.takin.cloud.data.dao.report.ReportDao;
import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import io.shulie.takin.adapter.api.entrypoint.report.CloudReportApi;
import io.shulie.takin.adapter.api.model.request.report.ReportDetailByIdReq;
import io.shulie.takin.adapter.api.model.request.report.ReportDetailBySceneIdReq;
import io.shulie.takin.adapter.api.model.request.report.ReportQueryReq;
import io.shulie.takin.adapter.api.model.request.report.ReportTrendQueryReq;
import io.shulie.takin.adapter.api.model.request.report.ScriptNodeTreeQueryReq;
import io.shulie.takin.adapter.api.model.request.report.TrendRequest;
import io.shulie.takin.adapter.api.model.request.report.WarnQueryReq;
import io.shulie.takin.adapter.api.model.response.scenemanage.WarnDetailResponse;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.biz.pojo.request.activity.ActivityInfoQueryRequest;
import io.shulie.takin.web.biz.pojo.request.leakverify.LeakVerifyTaskReportQueryRequest;
import io.shulie.takin.web.biz.pojo.request.report.ReportLinkDiagramReq;
import io.shulie.takin.web.biz.pojo.request.report.ReportQueryRequest;
import io.shulie.takin.web.biz.pojo.response.leakverify.LeakVerifyTaskResultResponse;
import io.shulie.takin.web.biz.service.ActivityService;
import io.shulie.takin.web.biz.service.DistributedLock;
import io.shulie.takin.web.biz.service.VerifyTaskReportService;
import io.shulie.takin.web.biz.service.report.ReportService;
import io.shulie.takin.web.biz.utils.PDFUtil;
import io.shulie.takin.web.common.constant.LockKeyConstants;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.common.util.RedisClientUtil;
import io.shulie.takin.web.data.dao.activity.ActivityDAO;
import io.shulie.takin.web.diff.api.report.ReportApi;
import io.shulie.takin.web.ext.entity.UserExt;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;

/**
 * @author qianshui
 * @date 2020/5/12 下午3:33
 */
@Service
@Slf4j
public class ReportServiceImpl implements ReportService {
    @Resource
    private ReportApi reportApi;
    @Resource
    private ActivityDAO activityDAO;

    @Resource
    private ActivityService activityService;

    @Resource
    private ReportDao reportDao;

    @Resource
    SceneManageMapper sceneManageMapper;
    @Resource
    private CloudReportApi cloudReportApi;
    @Resource
    private VerifyTaskReportService verifyTaskReportService;
    @Resource
    TReportBusinessActivityDetailMapper tReportBusinessActivityDetailMapper;

    @Value("${file.upload.url:''}")
    private String fileUploadUrl;

    @Autowired
    private PDFUtil pdfUtil;

    @Autowired
    private DistributedLock distributedLock;

    @Resource
    private RedisClientUtil redisClientUtil;
    @Resource
    private CloudReportService cloudReportService;

    @Resource
    private ReportMapper reportMapper;

    @Value("${takin.sre.path:192.168.54.103:8501}")
    private String sreUrl;

    @Override
    public ResponseResult<List<ReportDTO>> listReport(ReportQueryParam param) {
        // 前端查询条件 传用户
        final List<String> userIdList = new ArrayList<>(0);
        if (StringUtils.isNotBlank(param.getManagerName())) {
            List<UserExt> userList = WebPluginUtils.selectByName(param.getManagerName());
            if (CollectionUtils.isNotEmpty(userList)) {
                userList.forEach(t -> userIdList.add(StrUtil.format("'{}'", t.getId())));
            } else {
                return ResponseResult.success(new ArrayList<>(0), 0L);
            }
        }
        ResponseResult<List<ReportResp>> reportResponseList = cloudReportApi.listReport(new ReportQueryReq() {{
            setSceneId(param.getSceneId());
            setReportId(param.getReportId());
            setDeptId(param.getDeptId());
            setSceneName(param.getSceneName());
            setStartTime(param.getStartTime());
            setEndTime(param.getEndTime());
            setUserIds(WebPluginUtils.queryAllowUserIdList());
            setDeptIds(WebPluginUtils.queryAllowDeptIdList());
            setPageSize(param.getPageSize());
            setPageNumber(param.getCurrentPage() + 1);
            setFilterSql(String.join(",", userIdList));
        }});
        List<Long> userIds = reportResponseList.getData().stream().map(ContextExt::getUserId)
                .filter(Objects::nonNull).collect(Collectors.toList());
        //用户信息Map key:userId  value:user对象
        Map<Long, UserExt> userMap = WebPluginUtils.getUserMapByIds(userIds);
        List<ReportDTO> dtoList = reportResponseList.getData().stream().map(t -> {
            Long userId = t.getUserId() == null ? null : Long.valueOf(t.getUserId().toString());
            //负责人名称
            String userName = Optional.ofNullable(userMap.get(userId))
                    .map(UserExt::getName)
                    .orElse("");
            ReportDTO result = BeanUtil.copyProperties(t, ReportDTO.class);
            result.setUserName(userName);
            result.setUserId(userId);
            WebPluginUtils.fillQueryResponse(result);
            return result;
        }).collect(Collectors.toList());
        return ResponseResult.success(dtoList, reportResponseList.getTotalNum());
    }

    @Override
    public ReportDetailOutput getReportByReportId(Long reportId) {
        ReportDetailByIdReq req = new ReportDetailByIdReq();
        req.setReportId(reportId);
        final ReportDetailByIdReq idReq = new ReportDetailByIdReq() {{
            setReportId(reportId);
        }};
        ReportDetailResp detailResponse = cloudReportApi.detail(idReq);
        // sa超过100 显示100
        if (detailResponse.getSa() != null
                && detailResponse.getSa().compareTo(BigDecimal.valueOf(100)) > 0) {
            detailResponse.setSa(BigDecimal.valueOf(100));
        }
        ReportDetailOutput output = new ReportDetailOutput();
        BeanUtils.copyProperties(detailResponse, output);
        assembleVerifyResult(output);
        // 虚拟业务活动处理
        //dealVirtualBusiness(output);
        //补充报告执行人
        fillExecuteMan(output);
        return output;

    }

    @Override
    public List<SceneReportListOutput> getReportListBySceneId(Long sceneId) {
        ReportDetailBySceneIdReq req = new ReportDetailBySceneIdReq();
        req.setSceneId(sceneId);
        List<ReportDetailResp> respList = cloudReportApi.detailListBySceneId(req);
        List<SceneReportListOutput> outputList = new ArrayList<>();
        if(CollectionUtils.isEmpty(respList)) {
            return outputList;
        }
        respList.stream().forEach(resp -> {
            SceneReportListOutput out = new SceneReportListOutput();
            out.setReportId(resp.getId());
            out.setStartTime(resp.getStartTime());
            out.setMaxConcurrent(resp.getConcurrent());
            outputList.add(out);
        });
        return outputList;
    }

    private void fillExecuteMan(ReportDetailOutput output) {
        if (output == null) {
            return;
        }
        // 获取用户信息
        Map<Long, UserExt> userInfo = WebPluginUtils.getUserMapByIds(
                new ArrayList<Long>(1) {{
                    add(output.getUserId());
                }});
        // 填充用户信息
        if (userInfo.containsKey(output.getUserId())) {
            output.setOperateId(output.getUserId().toString());
            output.setUserName(userInfo.get(output.getUserId()).getName());
            output.setOperateName(userInfo.get(output.getUserId()).getName());
        }
    }

    private void assembleVerifyResult(ReportDetailOutput output) {
        //组装验证任务结果
        LeakVerifyTaskReportQueryRequest queryRequest = new LeakVerifyTaskReportQueryRequest();
        queryRequest.setReportId(output.getId());
        LeakVerifyTaskResultResponse verifyTaskResultResponse = verifyTaskReportService.getVerifyTaskReport(
                queryRequest);
        if (Objects.isNull(verifyTaskResultResponse)) {
            return;
        }
        LeakVerifyResult leakVerifyResult = new LeakVerifyResult();
        leakVerifyResult.setCode(verifyTaskResultResponse.getStatus());
        leakVerifyResult.setLabel(VerifyResultStatusEnum.getLabelByCode(verifyTaskResultResponse.getStatus()));
        output.setLeakVerifyResult(leakVerifyResult);
    }

    @Override
    public ReportTrendResp queryReportTrend(ReportTrendQueryReq param) {
        return cloudReportApi.trend(param);
    }

    @Override
    public ReportDetailTempOutput tempReportDetail(Long sceneId) {
        ReportDetailBySceneIdReq req = new ReportDetailBySceneIdReq();
        req.setSceneId(sceneId);
        ReportDetailResp result = reportApi.tempReportDetail(req);
        ReportDetailTempOutput output = new ReportDetailTempOutput();
        BeanUtils.copyProperties(result, output);

        fillExecuteMan(output);
        WebPluginUtils.fillQueryResponse(output);
        return output;

    }

    @Override
    public ReportTrendResp queryTempReportTrend(ReportTrendQueryReq param) {
        return cloudReportApi.tempTrend(param);
    }

    @Override
    public ResponseResult<List<WarnDetailResponse>> listWarn(WarnQueryReq req) {
        return cloudReportApi.listWarn(req);

    }

    @Override
    public List<ActivityResponse> queryReportActivityByReportId(Long reportId) {
        return cloudReportApi.activityByReportId(new ReportDetailByIdReq() {{
            setReportId(reportId);
        }});
    }

    @Override
    public List<ActivityResponse> queryReportActivityBySceneId(Long sceneId) {
        return cloudReportApi.activityBySceneId(new ReportDetailBySceneIdReq() {{
            setSceneId(sceneId);
        }});
    }

    @Override
    public NodeTreeSummaryResp querySummaryList(Long reportId) {
        return reportApi.getSummaryList(reportId);
    }

    @Override
    public List<MetricesResponse> queryMetrics(TrendRequest req) {
        return cloudReportApi.metrics(req);
    }

    @Override
    public Map<String, Object> queryReportCount(Long reportId) {
        return cloudReportApi.warnCount(new ReportDetailByIdReq() {{
            setReportId(reportId);
        }});
    }

    @Override
    public Boolean lockReport(Long reportId) {
        return cloudReportApi.lock(new ReportDetailByIdReq() {{
            setReportId(reportId);
        }});
    }

    @Override
    public Boolean unLockReport(Long reportId) {
        return cloudReportApi.unlock(new ReportDetailByIdReq() {{
            setReportId(reportId);
        }});
    }

    @Override
    public Boolean finishReport(Long reportId) {
        return cloudReportApi.finish(new ReportDetailByIdReq() {{
            setReportId(reportId);
        }});
    }

    @Override
    public ResponseResult<List<ScriptNodeTreeResp>> queryNodeTree(ReportQueryRequest request) {
        List<ScriptNodeTreeResp> listResponseResult = reportApi.scriptNodeTree(
                new ScriptNodeTreeQueryReq() {{
                    setSceneId(request.getSceneId());
                    setReportId(request.getReportId());
                }});
        return ResponseResult.success(listResponseResult);
    }

    @Override
    public ReportJtlDownloadOutput getJtlDownLoadUrl(Long reportId) {
        String result = reportApi.getJtlDownLoadUrl(reportId);
        return new ReportJtlDownloadOutput(result, true);
    }

    @Override
    public ReportDetailOutput getReportById(Long id) {
        ReportDetailByIdReq req = new ReportDetailByIdReq();
        req.setReportId(id);
        final ReportDetailByIdReq idReq = new ReportDetailByIdReq() {{
            setReportId(id);
        }};
        Integer status = cloudReportApi.getReportStatusById(idReq);
        final ReportDetailOutput output = new ReportDetailOutput();
        output.setTaskStatus(status);
        return output;
    }

    @Override
    public String downloadPDFPath(Long reportId) {
        String lockKey = String.format(LockKeyConstants.LOCK_REPORT_EXPORT, reportId);
        if (!distributedLock.tryLockSecondsTimeUnit(lockKey, 0L, 30L)) {
            throw new TakinWebException(TakinWebExceptionEnum.SCENE_REPORT_EXPORT_ERROR, "操作太频繁!");
        }
        //获取需要导出的数据
        ReportDetailOutput detailOutput = this.getReportByReportId(reportId);
        NodeTreeSummaryResp nodeTreeSummaryResp = this.querySummaryList(reportId);
        ReportDownLoadOutput downLoadOutput = new ReportDownLoadOutput(detailOutput, nodeTreeSummaryResp);

        Map<String, Object> dataModel = new HashMap<>();
        dataModel.put("data", downLoadOutput);
        String content = pdfUtil.parseFreemarker("report/report.html", dataModel);
        String pdf = "report_" + reportId + "_" + ".pdf";
        try {
            String path = pdfUtil.exportPDF(content, pdf);
            while (!(FileUtil.exist(path))) {
                //一直等待文件生成成功
            }
            return path;
        } catch (IOException e) {
            throw new TakinWebException(TakinWebExceptionEnum.SCENE_REPORT_EXPORT_ERROR, e.getMessage(), e);
        } finally {
            distributedLock.unLock(lockKey);
        }
    }

    @Override
    public void modifyRemarks(Long reportId, String remarks) {
        // 由于场景已经是合到web不在经由cloudApi-cloudService-dao兜一圈
        reportDao.updateRemarks(reportId, remarks);
    }

    @Override
    public ResponseResult<io.shulie.takin.web.biz.pojo.response.activity.ActivityResponse> getLinkDiagram(ReportLinkDiagramReq reportLinkDiagramReq) {
        // 首先通过xpathMdt获取到业务活动id
        ReportBusinessActivityDetailEntity detail = reportDao.getReportBusinessActivityDetail(reportLinkDiagramReq.getSceneId(), reportLinkDiagramReq.getXpathMd5(), reportLinkDiagramReq.getReportId());
        if (detail == null) {
            return ResponseResult.fail("400", "场景下不存在业务活动", "请检查后重试或联系管理员处理!");
        }
        io.shulie.takin.web.biz.pojo.response.activity.ActivityResponse activityResponse = null;
        if (reportLinkDiagramReq.getReportId() == null) {
            // 实况查询
            activityResponse = queryLinkDiagram(detail.getBusinessActivityId(), reportLinkDiagramReq);

        } else {
            String reportJson = detail.getReportJson();
            if (reportJson != null && StringUtils.isNotBlank(reportJson.trim())) {
                activityResponse = JSON.parseObject(reportJson, io.shulie.takin.web.biz.pojo.response.activity.ActivityResponse.class);
            }
        }

        if (activityResponse != null && activityResponse.getTopology() != null && activityResponse.getTopology().getNodes() != null) {
            List<ReportRiskItemOutput> reportRiskItemOutputList = getRiskItemListByDiagnosisId(reportLinkDiagramReq);
            if (CollectionUtils.isEmpty(reportRiskItemOutputList)){
                return ResponseResult.success(activityResponse);
            }
            Map<String, ReportRiskItemOutput> riskItemMap = reportRiskItemOutputList.stream().collect(Collectors.toMap(ReportRiskItemOutput::getAppName, Function.identity(), (existing, replacement) -> existing));
            List<ApplicationEntranceTopologyResponse.AbstractTopologyNodeResponse> nodes = activityResponse.getTopology().getNodes();
            for (ApplicationEntranceTopologyResponse.AbstractTopologyNodeResponse node : nodes) {
                ReportRiskItemOutput riskItemOutput = riskItemMap.get(node.getLabel());
                node.setRiskRank(riskItemOutput.getRanking());
            }
        }
        return ResponseResult.success(activityResponse);
    }
    private static List<ReportRiskItemOutput> getRiskItemListByDiagnosisId(ReportLinkDiagramReq reportLinkDiagramReq) {
        Map<String, Object> map = new HashMap<>();
        map.put("taskIdList", Arrays.asList(reportLinkDiagramReq.getDiagnosisId()));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        map.put("startTime", formatter.format(reportLinkDiagramReq.getStartTime()));
        map.put("endTime", formatter.format(reportLinkDiagramReq.getEndTime()));
        TypeToken<SreResponse<List<ReportRiskItemOutput>>> typeToken = new TypeToken<SreResponse<List<ReportRiskItemOutput>>>() {
        };
//        String url = sreUrl + "/takin-sre/api/risk/pressure/diagnosis/order";
        String url = "http://192.168.63.37:8501" + "/takin-sre/api/risk/pressure/diagnosis/order";
        SreResponse<List<ReportRiskItemOutput>> response = SreHelper.builder().url(url).httpMethod(HttpMethod.POST).param(map).queryList(typeToken);
        if (null != response && response.isSuccess()) {
            return response.getData();
        }
        return null;
    }

    @Override
    public io.shulie.takin.web.biz.pojo.response.activity.ActivityResponse getLinkDiagram2(ReportLinkDiagramReq2 reportLinkDiagramReq) {
        // 直接调用查询业务活动的拓扑图方法即可
        ActivityInfoQueryRequest request = new ActivityInfoQueryRequest();
        request.setActivityId(reportLinkDiagramReq.getActivityId());
        request.setFlowTypeEnum(FlowTypeEnum.BLEND);
        request.setTempActivity(false);
        return activityService.getActivityWithMetricsById(request);
    }

    /**
     * @param activityId           业务活动Id
     * @param reportLinkDiagramReq 查询条件
     */
    @Override
    public io.shulie.takin.web.biz.pojo.response.activity.ActivityResponse queryLinkDiagram(Long activityId, ReportLinkDiagramReq reportLinkDiagramReq) {
        // 直接调用查询业务活动的拓扑图方法即可
        ActivityInfoQueryRequest request = new ActivityInfoQueryRequest();
        request.setActivityId(activityId);
        request.setFlowTypeEnum(FlowTypeEnum.PRESSURE_MEASUREMENT);
        request.setStartTime(reportLinkDiagramReq.getStartTime());
        request.setEndTime(reportLinkDiagramReq.getEndTime());
        request.setTempActivity(false);
        return activityService.getActivityWithMetricsById(request);
    }


    @Override
    public ScriptNodeSummaryBean queryNode(Long reportId, String xpathMd5, Double threadNum) {
        ReportResult report = reportDao.selectById(reportId);
        SceneManageEntity sceneManageEntity = sceneManageMapper.selectById(report.getSceneId());
        List<ScriptNode> scriptAnalysis = JSON.parseArray(sceneManageEntity.getScriptAnalysisResult(), ScriptNode.class);
        List<ScriptNode> scriptNodes = scriptAnalysis.get(0).getChildren();
        // 获取thread_group节点
        ScriptNode scriptNode = scriptNodes.stream().filter(scriptNode1 -> xpathMd5.equals(scriptNode1.getXpathMd5())).findFirst().get();
        // 获取子节点
        List<String> xpathMd5List = new ArrayList<>();
        xpathMd5List.add(scriptNode.getXpathMd5());
        populateInternalXpathMd5(xpathMd5List, scriptNode.getChildren());

        int threadNumInt = new BigDecimal(threadNum).intValue();

        Map<String, Object> whereFilter = new HashMap<>();
        if (threadNum != threadNumInt) {
            List<String> threadNumIntList = new ArrayList<>();
            threadNumIntList.add(threadNumInt + "");
            threadNumIntList.add((threadNumInt + 1) + "");
            whereFilter.put("active_threads", threadNumIntList);
        } else {
            whereFilter.put("active_threads", threadNumInt + "");
        }

        whereFilter.put("transaction", xpathMd5List);

        EnginePressureQuery enginePressureQuery = new EnginePressureQuery();
        Map<String, String> fieldAndAlias = new HashMap<>();
        fieldAndAlias.put("count", "count");
        fieldAndAlias.put("time", "time");
        fieldAndAlias.put("fail_count", "failCount");
        fieldAndAlias.put("avg_tps", "avgTps");
        fieldAndAlias.put("max_rt", "maxRt");
        fieldAndAlias.put("min_rt", "minRt");
        fieldAndAlias.put("sum_rt", "sumRt");
        fieldAndAlias.put("sa_count", "saCount");
        fieldAndAlias.put("active_threads", "activeThreads");
        fieldAndAlias.put("transaction", "transaction");
        enginePressureQuery.setFieldAndAlias(fieldAndAlias);
        enginePressureQuery.setWhereFilter(whereFilter);
        enginePressureQuery.setJobId(report.getJobId());

        List<EngineMetricsDTO> engineMetricsDTOS = cloudReportService.listEnginePressure(enginePressureQuery, EngineMetricsDTO.class);
        Map<String, Map<String, Object>> groupMergedResult = new HashMap<>();
        if (CollectionUtils.isNotEmpty(engineMetricsDTOS)) {
            // 按xpathMd5分组
            Map<String, List<EngineMetricsDTO>> stringListMap = engineMetricsDTOS.stream().collect(Collectors.groupingBy(EngineMetricsDTO::getTransaction));

            // 分组后合并的结果
            for (Map.Entry<String, List<EngineMetricsDTO>> entry : stringListMap.entrySet()) {
                groupMergedResult.put(entry.getKey(), mergeResult(entry.getValue()));
            }

            Map<String, Object> rootGroupResult = mergeResult(engineMetricsDTOS);

            double maxTps = groupMergedResult.entrySet().stream().map(entry -> (BigDecimal) entry.getValue().get("maxTps")).mapToDouble(value -> value.doubleValue()).sum();
            rootGroupResult.put("maxTps", new BigDecimal(maxTps).setScale(1, BigDecimal.ROUND_HALF_UP));

            double tps = groupMergedResult.entrySet().stream().map(entry -> (BigDecimal) entry.getValue().get("tps")).mapToDouble(value -> value.doubleValue()).sum();
            rootGroupResult.put("tps", new BigDecimal(tps).setScale(1, BigDecimal.ROUND_HALF_UP));

            groupMergedResult.put(xpathMd5, rootGroupResult);
        }

        // 补充目标信息等
        List<ReportBusinessActivityDetail> activityDetails = tReportBusinessActivityDetailMapper.queryReportBusinessActivityDetailByReportId(reportId);
        Map<String, ReportBusinessActivityDetail> detailMap = activityDetails.stream().collect(Collectors.toMap(ReportBusinessActivityDetail::getBindRef, ReportBusinessActivityDetail -> ReportBusinessActivityDetail));

        ScriptNodeSummaryBean summaryBean = buildNodeSummaryBean(threadNum, scriptNode, groupMergedResult, detailMap);
        summaryBean.setConcurrentStageThreadNum(getSummaryConcurrentStageThreadNum(xpathMd5, sceneManageEntity));

        return summaryBean;
    }

    /**
     * 获取阶梯递增的阶段线程数
     *
     * @return
     */
    private List<BigDecimal> getSummaryConcurrentStageThreadNum(String xpathMd5, SceneManageEntity manageEntity) {

        PtConfigExt ext = JSON.parseObject(manageEntity.getPtConfig(), PtConfigExt.class);
        Map<String, ThreadGroupConfigExt> configMap = ext.getThreadGroupConfigMap();
        if (configMap == null || configMap.isEmpty()) {
            return Collections.emptyList();
        }
        ThreadGroupConfigExt value = configMap.get(xpathMd5);
        if (value.getType() == null || value.getType() != 0 || value.getMode() == null || value.getMode() != 3) {
            return Collections.emptyList();
        }
        // 阶梯递增模式
        Integer steps = value.getSteps();
        Integer threadNum = value.getThreadNum();
        List<BigDecimal> stepList = new ArrayList<>(steps);
        for (Integer i = 1; i <= steps; i++) {
            stepList.add(new BigDecimal(threadNum * i).divide(new BigDecimal(steps), 2, BigDecimal.ROUND_HALF_UP));
        }
        return stepList;

    }

    private ScriptNodeSummaryBean buildNodeSummaryBean(Double threadNum, ScriptNode node, Map<String, Map<String, Object>> groupMergedResult, Map<String, ReportBusinessActivityDetail> detailMap) {
        ScriptNodeSummaryBean summaryBean = new ScriptNodeSummaryBean();
        summaryBean.setName(node.getName());
        summaryBean.setTestName(node.getTestName());
        summaryBean.setMd5(node.getMd5());
        summaryBean.setType(node.getType().name());
        summaryBean.setXpath(node.getXpath());
        String xpathMd5 = node.getXpathMd5();
        summaryBean.setXpathMd5(xpathMd5);

        Map<String, Object> objectMap = groupMergedResult.get(xpathMd5);
        objectMap = objectMap == null ? new HashMap<>() : objectMap;

        if (objectMap != null) {
            ReportBusinessActivityDetail activityDetail = detailMap.get(xpathMd5);
            summaryBean.setTps(new DataBean(objectMap.get("tps"), activityDetail.getTargetTps()));
            summaryBean.setAvgRt(new DataBean(objectMap.get("avgRt"), activityDetail.getTargetRt()));
            summaryBean.setSuccessRate(new DataBean(objectMap.get("successRate"), activityDetail.getTargetSuccessRate()));
            summaryBean.setSa(new DataBean(objectMap.get("sa"), activityDetail.getTargetSa()));
            summaryBean.setMaxTps((BigDecimal) objectMap.get("maxTps"));
            summaryBean.setMaxRt((BigDecimal) objectMap.get("maxRt"));
            summaryBean.setMinRt((BigDecimal) objectMap.get("minRt"));
            Object totalRequest = objectMap.get("totalRequest");
            if (totalRequest != null) {
                summaryBean.setTotalRequest(new Double((double) totalRequest).longValue());
            }
            summaryBean.setAvgConcurrenceNum(new BigDecimal(threadNum));
            summaryBean.setPassFlag(activityDetail.getPassFlag());
        }
        List<ScriptNode> children = node.getChildren();
        if (CollectionUtils.isNotEmpty(children)) {
            List<ScriptNodeSummaryBean> nodeChildren = new ArrayList<>(children.size());
            for (ScriptNode scriptNode : children) {
                nodeChildren.add(buildNodeSummaryBean(threadNum, scriptNode, groupMergedResult, detailMap));
            }
            summaryBean.setChildren(nodeChildren);
        }
        return summaryBean;
    }

    private Map<String, Object> mergeResult(List<EngineMetricsDTO> values) {
        Map<String, Object> v = new HashMap<>();
        //请求数
        double totalResultNum = values.stream().mapToDouble(value -> transform(value.getCount())).sum();
        v.put("totalRequest", totalResultNum);
        //TPS
        double maxTps = values.stream().map(map -> (new BigDecimal(map.getCount())).divide(new BigDecimal(5), 1, BigDecimal.ROUND_HALF_UP)).mapToDouble(value -> value.doubleValue()).max().getAsDouble();
        v.put("maxTps", new BigDecimal(maxTps).setScale(1, BigDecimal.ROUND_HALF_UP));

        double avgTps = values.stream().map(map -> (new BigDecimal(map.getCount())).divide(new BigDecimal(5), 1, BigDecimal.ROUND_HALF_UP)).mapToDouble(value -> value.doubleValue()).average().getAsDouble();
        v.put("tps", new BigDecimal(avgTps).setScale(2, BigDecimal.ROUND_HALF_UP));

        //rt
        double maxRt = values.stream().mapToDouble(value -> transform(value.getMaxRt())).max().getAsDouble();
        v.put("maxRt", new BigDecimal(maxRt).setScale(1, BigDecimal.ROUND_HALF_UP));
        double minRt = values.stream().mapToDouble(value -> transform(value.getMinRt())).min().getAsDouble();
        v.put("minRt", new BigDecimal(minRt).setScale(1, BigDecimal.ROUND_HALF_UP));

        double avgRt = values.stream().map(map -> ((BigDecimal) map.getSumRt()).divide(new BigDecimal(map.getCount()), 2, BigDecimal.ROUND_HALF_UP)).mapToDouble(value -> value.doubleValue()).average().getAsDouble();
        v.put("avgRt", new BigDecimal(avgRt).setScale(2, BigDecimal.ROUND_HALF_UP));

        //请求成功率
        double totalFailedRequestNum = values.stream().mapToDouble(value -> transform(value.getFailCount())).sum();
        double successRate = (totalResultNum - totalFailedRequestNum) * 100 / totalResultNum;
        v.put("successRate", new BigDecimal(successRate).setScale(0, BigDecimal.ROUND_HALF_UP));

        //sa
        double sa = values.stream().mapToDouble(value -> transform(value.getSaCount())).average().getAsDouble();
        v.put("sa", new BigDecimal(sa).setScale(0, BigDecimal.ROUND_HALF_UP));

        return v;
    }

    private double transform(Object decimal) {
        return Double.parseDouble(decimal.toString());
    }

    private void populateInternalXpathMd5(List<String> xpathMd5List, List<ScriptNode> scriptNodes) {
        if (scriptNodes == null || scriptNodes.isEmpty()) {
            return;
        }
        while (true) {
            for (ScriptNode scriptNode : scriptNodes) {
                xpathMd5List.add(scriptNode.getXpathMd5());
                populateInternalXpathMd5(xpathMd5List, scriptNode.getChildren());
            }
            break;
        }
    }

    @Override
    public ThreadReportTrendResp queryReportTrendByThread(ReportTrendQueryReq reportTrendQuery) {

        String key = "reportTrendByThread:" + JSON.toJSONString(reportTrendQuery);
        if (redisClientUtil.hasKey(key)) {
            return JSON.parseObject(redisClientUtil.getString(key), ThreadReportTrendResp.class);
        }

        ThreadReportTrendResp reportTrend = new ThreadReportTrendResp();
        ReportResult reportResult = reportDao.selectById(reportTrendQuery.getReportId());
        if (reportResult == null) {
            return reportTrend;
        }

        String ptConfig = reportResult.getPtConfig();
        PtConfigExt ext = JSON.parseObject(ptConfig, PtConfigExt.class);
        Map<String, ThreadGroupConfigExt> configMap = ext.getThreadGroupConfigMap();
        if (configMap == null || configMap.isEmpty()) {
            return null;
        }

        //根据xpathMd5找到上级对应线程组的md5
        Map<String, String> threadGroupChildMap = new HashMap<>();
        List<ScriptNode> allThreadGroup = JsonPathUtil.getNodeListByType(reportResult.getScriptNodeTree(), NodeTypeEnum.THREAD_GROUP);
        allThreadGroup.forEach(scriptNode -> {
            threadGroupChildMap.put(scriptNode.getXpathMd5(), scriptNode.getXpathMd5());
            List<ScriptNode> childControllers = JsonPathUtil.getChildControllers(reportResult.getScriptNodeTree(), scriptNode.getXpathMd5());
            List<ScriptNode> childSamplers = JsonPathUtil.getChildSamplers(reportResult.getScriptNodeTree(), scriptNode.getXpathMd5());
            childControllers.forEach(o -> threadGroupChildMap.put(o.getXpathMd5(), scriptNode.getXpathMd5()));
            childSamplers.forEach(o -> threadGroupChildMap.put(o.getXpathMd5(), scriptNode.getXpathMd5()));
        });
        if (!threadGroupChildMap.containsKey(reportTrendQuery.getXpathMd5())) {
            return null;
        }
        ThreadGroupConfigExt value = configMap.get(threadGroupChildMap.get(reportTrendQuery.getXpathMd5()));
        if (value == null || value.getType() == null || value.getType() != 0 || value.getMode() == null || value.getMode() != 3 || value.getSteps() == null) {
            return null;
        }
        // 阶梯递增模式
        Integer steps = value.getSteps();
        Integer threadNum = value.getThreadNum();

        List<String> rt = new ArrayList<>();
        List<String> tps = new ArrayList<>();
        List<String> concurrent = new ArrayList<>(steps);
        for (Integer i = 1; i <= steps; i++) {
            concurrent.add(new BigDecimal(threadNum * i).divide(new BigDecimal(steps), 0, BigDecimal.ROUND_HALF_UP).toString());
        }
        if (CollectionUtils.isEmpty(concurrent)) {
            return null;
        }
        reportTrend.setConcurrent(concurrent);
        EnginePressureQuery enginePressureQuery = new EnginePressureQuery();
        Map<String, String> fieldAndAlias = new HashMap<>();
        fieldAndAlias.put("active_threads", "activeThreads");
        fieldAndAlias.put("avg(avg_tps)", "avgTps");
        fieldAndAlias.put("avg(avg_rt)", "avgRt");
        enginePressureQuery.setFieldAndAlias(fieldAndAlias);
        enginePressureQuery.setJobId(reportResult.getJobId());
        enginePressureQuery.setTransaction(threadGroupChildMap.get(reportTrendQuery.getXpathMd5()));
        List<String> groupByFields = new ArrayList<>();
        groupByFields.add("active_threads");
        enginePressureQuery.setGroupByFields(groupByFields);
        List<EngineMetricsDTO> engineMetricsDTOS = cloudReportService.listEnginePressure(enginePressureQuery, EngineMetricsDTO.class);
        if (CollectionUtils.isNotEmpty(engineMetricsDTOS)){
            Map<Integer, List<EngineMetricsDTO>> listMap = engineMetricsDTOS.stream().collect(Collectors.groupingBy(EngineMetricsDTO::getActiveThreads));
            for (String num : concurrent) {
                String rtString = "0";
                String tpsString = "0";
                double parseDouble = Double.parseDouble(num);
                int threadNumInt = new BigDecimal(num).intValue();
                List<EngineMetricsDTO> all = new ArrayList<>();
                List<EngineMetricsDTO> metricsDTOS = listMap.get(threadNumInt);
                if (CollectionUtils.isNotEmpty(metricsDTOS)){
                    all.addAll(metricsDTOS);
                }
                if (parseDouble != threadNumInt) {
                    List<EngineMetricsDTO> otherMetricsDTOS = listMap.get(threadNumInt + 1);
                    if (CollectionUtils.isNotEmpty(otherMetricsDTOS)){
                        all.addAll(otherMetricsDTOS);
                    }
                }
                if (CollectionUtils.isNotEmpty(all)){
                    double avgRt = all.stream().mapToDouble(o -> o.getAvgRt().doubleValue()).average().getAsDouble();
                    double avgTps = all.stream().mapToDouble(o -> o.getAvgTps().doubleValue()).average().getAsDouble();
                    rtString = avgRt + "";
                    tpsString = avgTps + "";
                }
                rt.add(rtString);
                tps.add(tpsString);
            }
        }

        reportTrend.setRt(rt);
        reportTrend.setTps(tps);
        if (CollectionUtils.isNotEmpty(reportTrend.getConcurrent()) && CollectionUtils.isNotEmpty(reportTrend.getRt()) && !"0".equals(reportTrend.getRt().get(0))) {
            redisClientUtil.setString(key, JSON.toJSONString(reportTrend));
            redisClientUtil.expire(key, 1000 * 60 * 120);
        }
        return reportTrend;

    }

    @Override
    public void modifyLinkDiagram(ReportLinkDiagramReq reportLinkDiagramReq) {
        reportLinkDiagramReq.setEndTime(LocalDateTime.now());
        ReportBusinessActivityDetailEntity detail = reportDao.getReportBusinessActivityDetail(reportLinkDiagramReq.getSceneId(), reportLinkDiagramReq.getXpathMd5(), reportLinkDiagramReq.getReportId());
        if (detail == null){
            throw new TakinWebException(TakinWebExceptionEnum.SCENE_REPORT_VALIDATE_ERROR, "重新生成拓扑图，没有获取到对应的数据!");
        }
        io.shulie.takin.web.biz.pojo.response.activity.ActivityResponse activityResponse = queryLinkDiagram(detail.getBusinessActivityId(), reportLinkDiagramReq);
        // 将链路拓扑信息更新到表中
        reportDao.modifyReportLinkDiagram(reportLinkDiagramReq.getReportId(), reportLinkDiagramReq.getXpathMd5(), JSON.toJSONString(activityResponse));
    }

    @Override
    public void modifyLinkDiagrams(ReportLinkDiagramReq reportLinkDiagramReq, List<String> pathMd5List) {
        reportLinkDiagramReq.setEndTime(LocalDateTime.now());

        List<ReportBusinessActivityDetailEntity> detailList = reportDao.getReportBusinessActivityDetails(reportLinkDiagramReq.getSceneId(), pathMd5List, reportLinkDiagramReq.getReportId());
        if (CollectionUtils.isEmpty(detailList)){
            throw new TakinWebException(TakinWebExceptionEnum.SCENE_REPORT_VALIDATE_ERROR, "重新生成拓扑图，没有获取到对应的数据!");
        }
        for (ReportBusinessActivityDetailEntity detailEntity : detailList) {
            io.shulie.takin.web.biz.pojo.response.activity.ActivityResponse activityResponse = queryLinkDiagram(detailEntity.getBusinessActivityId(), reportLinkDiagramReq);
            if (activityResponse != null){
                reportDao.modifyReportLinkDiagram(reportLinkDiagramReq.getReportId(), detailEntity.getBindRef(), JSON.toJSONString(activityResponse));
            }
        }
    }

    /**
     * 根据报告ids查询报告详情
     *
     * @param reportIds
     * @return
     */
    @Override
    public List<ReportEntity> getReportListByReportIds(List<Long> reportIds) {
        if (CollectionUtils.isEmpty(reportIds)) {
            return new ArrayList<>();
        }
        LambdaQueryWrapper<ReportEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(ReportEntity::getId, ReportEntity::getSceneName, ReportEntity::getSceneId, ReportEntity::getEndTime, ReportEntity::getStartTime);
        queryWrapper.in(ReportEntity::getId, reportIds);
        return reportMapper.selectList(queryWrapper);
    }

    @Override
    public void buildReportTestData(Long jobId, Long sceneId, Long reportId, Long tenantId) {
        cloudReportService.updateReportBusinessActivity(jobId, sceneId, reportId, tenantId);
    }

    /**
     * 获取最近一小时的报告ids
     *
     * @return
     */
    @Override
    public List<Long> nearlyHourReportIds(int minutes) {
        return reportDao.nearlyHourReportIds(minutes);
    }

    @Override
    public SreResponse<SrePageData<RiskItemExtractionVO>> getReportRiskItemPages(RiskListQueryRequest request) {
        Map<String, Object> param = new HashMap<>();
        param.put("startTime", request.getStartTime());
        param.put("endTime", request.getEndTime());
        param.put("taskIdList", request.getTaskIds());
        param.put("tenantCode", request.getTenantCode());
        param.put("page", request.getPage());
        param.put("size", request.getSize());
        TypeToken<SreResponse<SrePageData<RiskItemExtractionVO>>> typeToken = new TypeToken<SreResponse<SrePageData<RiskItemExtractionVO>>>() {
        };
//        String url = sreUrl + "/takin-sre/api/risk/pressure/diagnosis/query";
        String url = "http://192.168.63.37:8501" + "/takin-sre/api/risk/pressure/diagnosis/query";
        SreResponse<SrePageData<RiskItemExtractionVO>> response = SreHelper.builder().param(param).url(url).httpMethod(HttpMethod.POST).queryList(typeToken);
        return response;
    }

    public static void main(String[] args) throws ParseException {
        ReportServiceImpl reportService = new ReportServiceImpl();
//        RiskListQueryRequest request = new RiskListQueryRequest();
//        request.setStartTime("2023-11-28 00:00:00");
//        request.setEndTime("2023-11-28 23:00:00");
//        request.setTaskIds(Arrays.asList(-1L));
//        request.setTenantCode("wstest");
//        request.setPage(1);
//        request.setSize(10);
//        System.out.println(JSON.toJSONString(reportService.getReportRiskItemPages(request)));
        ReportLinkDiagramReq reportLinkDiagramReq = new ReportLinkDiagramReq();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        reportLinkDiagramReq.setEndTime(LocalDateTime.parse("2023-11-28 23:00:00", formatter));
        reportLinkDiagramReq.setStartTime(LocalDateTime.parse("2023-11-28 00:00:00", formatter));
        reportLinkDiagramReq.setDiagnosisId(-1L);
        System.out.println(JSON.toJSONString(getRiskItemListByDiagnosisId(reportLinkDiagramReq)));
    }

    private ScriptNodeSummaryBean getCurrentValue(ScriptNodeSummaryBean scriptNodeSummaryBean, String xpathMd5) {
        if (scriptNodeSummaryBean.getXpathMd5().equals(xpathMd5)) {
            return scriptNodeSummaryBean;
        }
        if (CollectionUtils.isNotEmpty(scriptNodeSummaryBean.getChildren())) {
            for (ScriptNodeSummaryBean child : scriptNodeSummaryBean.getChildren()) {
                return getCurrentValue(child, xpathMd5);
            }
        }
        return new ScriptNodeSummaryBean();
    }
}