package io.shulie.takin.web.biz.service.report.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.pamirs.takin.cloud.entity.dao.report.TReportBusinessActivityDetailMapper;
import com.pamirs.takin.cloud.entity.domain.dto.report.StatReportDTO;
import com.pamirs.takin.cloud.entity.domain.entity.report.ReportBusinessActivityDetail;
import com.pamirs.takin.common.constant.VerifyResultStatusEnum;
import com.pamirs.takin.entity.domain.dto.report.LeakVerifyResult;
import com.pamirs.takin.entity.domain.dto.report.ReportDTO;
import com.pamirs.takin.entity.domain.vo.report.ReportQueryParam;
import io.shulie.takin.adapter.api.entrypoint.report.CloudReportApi;
import io.shulie.takin.adapter.api.model.ScriptNodeSummaryBean;
import io.shulie.takin.adapter.api.model.common.DataBean;
import io.shulie.takin.adapter.api.model.request.report.*;
import io.shulie.takin.adapter.api.model.response.report.*;
import io.shulie.takin.adapter.api.model.response.scenemanage.WarnDetailResponse;
import io.shulie.takin.cloud.common.influxdb.InfluxUtil;
import io.shulie.takin.cloud.common.influxdb.InfluxWriter;
import io.shulie.takin.cloud.common.utils.JsonPathUtil;
import io.shulie.takin.cloud.data.dao.report.ReportDao;
import io.shulie.takin.cloud.data.mapper.mysql.SceneManageMapper;
import io.shulie.takin.cloud.data.model.mysql.SceneManageEntity;
import io.shulie.takin.cloud.data.result.report.ReportResult;
import io.shulie.takin.cloud.ext.content.enginecall.PtConfigExt;
import io.shulie.takin.cloud.ext.content.enginecall.ThreadGroupConfigExt;
import io.shulie.takin.cloud.ext.content.enums.NodeTypeEnum;
import io.shulie.takin.cloud.ext.content.script.ScriptNode;
import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.biz.pojo.output.report.ReportDetailOutput;
import io.shulie.takin.web.biz.pojo.output.report.ReportDetailTempOutput;
import io.shulie.takin.web.biz.pojo.output.report.ReportDownLoadOutput;
import io.shulie.takin.web.biz.pojo.output.report.ReportJtlDownloadOutput;
import io.shulie.takin.web.biz.pojo.request.leakverify.LeakVerifyTaskReportQueryRequest;
import io.shulie.takin.web.biz.pojo.request.report.ReportQueryRequest;
import io.shulie.takin.web.biz.pojo.response.leakverify.LeakVerifyTaskResultResponse;
import io.shulie.takin.web.biz.service.DistributedLock;
import io.shulie.takin.web.biz.service.VerifyTaskReportService;
import io.shulie.takin.web.biz.service.report.ReportService;
import io.shulie.takin.web.biz.utils.PDFUtil;
import io.shulie.takin.web.common.constant.LockKeyConstants;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
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
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

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
    ReportDao reportDao;
    @Resource
    InfluxWriter influxWriter;
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
            setSceneName(param.getSceneName());
            setStartTime(param.getStartTime());
            setEndTime(param.getEndTime());
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

        List<Long> allowStartStopUserIdList = WebPluginUtils.getStartStopAllowUserIdList();
        if (CollectionUtils.isNotEmpty(allowStartStopUserIdList)) {
            Long userId = output.getUserId();
            if (userId == null || !allowStartStopUserIdList.contains(userId)) {
                output.setCanStartStop(Boolean.FALSE);
            } else {
                output.setCanStartStop(Boolean.TRUE);
            }
        } else {
            output.setCanStartStop(Boolean.TRUE);
        }
        fillExecuteMan(output);
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

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < xpathMd5List.size(); i++) {
            sb.append("transaction='").append(xpathMd5List.get(i)).append("'");
            if (i < xpathMd5List.size() - 1) {
                sb.append(" or ");
            }
        }
        String measurement = InfluxUtil.getMetricsMeasurement(report.getJobId(), report.getSceneId(), report.getId(), report.getTenantId());

        int threadNumInt = new BigDecimal(threadNum).intValue();
        String threadNumSql = "active_threads = " + threadNumInt;
        if (threadNum != threadNumInt) {
            threadNumSql = threadNumSql + " or active_threads = " + (threadNumInt + 1);
        }

        String sql = String.format("select count, fail_count, avg_tps , max_rt, min_rt, sum_rt, sa_count, active_threads, transaction " +
                "from %s where (%s) and (%s) ", measurement, threadNumSql, sb);

        List<Map> queryResult = influxWriter.query(sql, Map.class);

        Map<String, Map<String, Object>> groupMergedResult = new HashMap<>();
        if (queryResult != null && !queryResult.isEmpty()) {
            // 按xpathMd5分组
            Map<String, List<Map>> groupedResult = queryResult.stream().collect(Collectors.groupingBy(map -> (String) map.get("transaction")));

            // 分组后合并的结果
            for (Map.Entry<String, List<Map>> entry : groupedResult.entrySet()) {
                groupMergedResult.put(entry.getKey(), mergeResult(entry.getValue()));
            }

            Map<String, Object> rootGroupResult = mergeResult(queryResult);

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

    private Map<String, Object> mergeResult(List<Map> values) {
        Map<String, Object> v = new HashMap<>();
        //请求数
        double totalResultNum = values.stream().mapToDouble(value -> transform(value.get("count"))).sum();
        v.put("totalRequest", totalResultNum);
        //TPS
        double maxTps = values.stream().map(map -> ((BigDecimal) map.get("count")).divide(new BigDecimal(5), 1, BigDecimal.ROUND_HALF_UP)).mapToDouble(value -> value.doubleValue()).max().getAsDouble();
        v.put("maxTps", new BigDecimal(maxTps).setScale(1, BigDecimal.ROUND_HALF_UP));

        double avgTps = values.stream().map(map -> ((BigDecimal) map.get("count")).divide(new BigDecimal(5), 1, BigDecimal.ROUND_HALF_UP)).mapToDouble(value -> value.doubleValue()).average().getAsDouble();
        v.put("tps", new BigDecimal(avgTps).setScale(2, BigDecimal.ROUND_HALF_UP));

        //rt
        double maxRt = values.stream().mapToDouble(value -> transform(value.get("max_rt"))).max().getAsDouble();
        v.put("maxRt", new BigDecimal(maxRt).setScale(1, BigDecimal.ROUND_HALF_UP));
        double minRt = values.stream().mapToDouble(value -> transform(value.get("min_rt"))).min().getAsDouble();
        v.put("minRt", new BigDecimal(minRt).setScale(1, BigDecimal.ROUND_HALF_UP));

        double avgRt = values.stream().map(map -> ((BigDecimal) map.get("sum_rt")).divide((BigDecimal) map.get("count"), 2, BigDecimal.ROUND_HALF_UP)).mapToDouble(value -> value.doubleValue()).average().getAsDouble();
        v.put("avgRt", new BigDecimal(avgRt).setScale(2, BigDecimal.ROUND_HALF_UP));

        //请求成功率
        double totalFailedRequestNum = values.stream().mapToDouble(value -> transform(value.get("fail_count"))).sum();
        double successRate = (totalResultNum - totalFailedRequestNum) * 100 / totalResultNum;
        v.put("successRate", new BigDecimal(successRate).setScale(0, BigDecimal.ROUND_HALF_UP));

        //sa
        double sa = values.stream().mapToDouble(value -> transform(value.get("sa_count"))).average().getAsDouble();
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
        ThreadReportTrendResp reportTrend = new ThreadReportTrendResp();
        ReportResult reportResult = reportDao.selectById(reportTrendQuery.getReportId());
        if (reportResult == null) {
            return reportTrend;
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
                " sum(count) as tempRequestCount,  mean(avg_tps) as tps , sum(sum_rt)/sum"
                        + "(count) as "
                        + "avgRt, sum(sa_count) as saCount, count(avg_rt) as recordCount ,mean(active_threads) as "
                        + "avgConcurrenceNum ");
        influxDbSql.append(" from ");
        influxDbSql.append(
                InfluxUtil.getMeasurement(reportResult.getJobId(), reportResult.getSceneId(),
                        reportResult.getId(), reportResult.getTenantId()));
        influxDbSql.append(" where ");
        influxDbSql.append(" transaction = ").append("'").append(transaction).append("'");

        List<StatReportDTO> list = new ArrayList<>();
        if (StringUtils.isNotEmpty(transaction)) {
            list = influxWriter.query(influxDbSql.toString(), StatReportDTO.class);
        }
        if (CollectionUtils.isEmpty(list)){
            return null;
        }
        String ptConfig = reportResult.getPtConfig();
        PtConfigExt ext = JSON.parseObject(ptConfig, PtConfigExt.class);
        Map<String, ThreadGroupConfigExt> configMap = ext.getThreadGroupConfigMap();
        if (configMap == null || configMap.isEmpty()) {
            return null;
        }
        ThreadGroupConfigExt value = configMap.get(transaction);
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
            long startTime = reportResult.getStartTime().getTime();
            int finalI = i;
            List<BigDecimal> avgRtList = list.stream().filter(o -> o.getAvgRt() != null && TimeUtil.fromInfluxDBTimeFormat(o.getTime()) >= startTime * finalI
                            && TimeUtil.fromInfluxDBTimeFormat(o.getTime()) < startTime * (finalI + 1)).map(StatReportDTO::getAvgRt)
                    .collect(Collectors.toList());
            List<BigDecimal> tpsList = list.stream().filter(o -> o.getTps() != null && TimeUtil.fromInfluxDBTimeFormat(o.getTime()) >= startTime * finalI
                            && TimeUtil.fromInfluxDBTimeFormat(o.getTime()) < startTime * (finalI + 1)).map(StatReportDTO::getTps)
                    .collect(Collectors.toList());
            rt.add(getAvg(avgRtList));
            tps.add(getAvg(tpsList));
            concurrent.add(new BigDecimal(threadNum * i).divide(new BigDecimal(steps), 0, BigDecimal.ROUND_HALF_UP).toString());
        }
        if (CollectionUtils.isEmpty(concurrent)){
            return null;
        }

        reportTrend.setConcurrent(concurrent);
        reportTrend.setRt(rt);
        reportTrend.setTps(tps);
        return reportTrend;

    }

    private String getAvg(List<BigDecimal> list){
        BigDecimal sum = new BigDecimal(0);
        if (CollectionUtils.isEmpty(list)){
            return "0";
        }
        for (BigDecimal b : list){
            sum = sum.add(b);
        }
        return sum.divide(new BigDecimal(list.size()),0, BigDecimal.ROUND_HALF_UP).toString();
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
}