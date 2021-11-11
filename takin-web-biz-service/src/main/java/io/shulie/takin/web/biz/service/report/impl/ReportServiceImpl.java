package io.shulie.takin.web.biz.service.report.impl;

import com.google.common.collect.Lists;
import com.pamirs.takin.common.constant.VerifyResultStatusEnum;
import com.pamirs.takin.common.exception.ApiException;
import com.pamirs.takin.entity.domain.dto.report.LeakVerifyResult;
import com.pamirs.takin.entity.domain.vo.report.ReportIdVO;
import com.pamirs.takin.entity.domain.vo.report.ReportQueryParam;
import com.pamirs.takin.entity.domain.vo.report.ReportTrendQueryParam;
import com.pamirs.takin.entity.domain.vo.report.SceneIdVO;
import com.pamirs.takin.entity.domain.vo.sla.WarnQueryParam;
import io.shulie.takin.cloud.common.bean.scenemanage.BusinessActivitySummaryBean;
import io.shulie.takin.cloud.open.req.report.ReportDetailByIdReq;
import io.shulie.takin.cloud.open.req.report.ReportDetailBySceneIdReq;
import io.shulie.takin.cloud.open.req.report.ReportTrendQueryReq;
import io.shulie.takin.cloud.open.req.report.ScriptNodeTreeQueryReq;
import io.shulie.takin.cloud.open.resp.report.NodeTreeSummaryResp;
import io.shulie.takin.cloud.open.resp.report.ReportDetailResp;
import io.shulie.takin.cloud.open.resp.report.ReportTrendResp;
import io.shulie.takin.cloud.open.resp.report.ScriptNodeTreeResp;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.biz.pojo.output.report.ReportDetailOutput;
import io.shulie.takin.web.biz.pojo.output.report.ReportDetailTempOutput;
import io.shulie.takin.web.biz.pojo.request.leakverify.LeakVerifyTaskReportQueryRequest;
import io.shulie.takin.web.biz.pojo.request.report.ReportQueryRequest;
import io.shulie.takin.web.biz.pojo.response.activity.ActivityResponse;
import io.shulie.takin.web.biz.pojo.response.leakverify.LeakVerifyTaskResultResponse;
import io.shulie.takin.web.biz.service.ActivityService;
import io.shulie.takin.web.biz.service.VerifyTaskReportService;
import io.shulie.takin.web.biz.service.report.ReportRealTimeService;
import io.shulie.takin.web.biz.service.report.ReportService;
import io.shulie.takin.web.common.constant.RemoteConstant;
import io.shulie.takin.web.common.domain.ErrorInfo;
import io.shulie.takin.web.common.domain.WebRequest;
import io.shulie.takin.web.common.domain.WebResponse;
import io.shulie.takin.web.common.enums.activity.BusinessTypeEnum;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import io.shulie.takin.web.common.http.HttpAssert;
import io.shulie.takin.web.common.http.HttpWebClient;
import io.shulie.takin.web.data.common.InfluxDatabaseManager;
import io.shulie.takin.web.data.dao.activity.ActivityDAO;
import io.shulie.takin.web.data.param.activity.ActivityQueryParam;
import io.shulie.takin.web.data.result.activity.ActivityListResult;
import io.shulie.takin.web.data.result.activity.ActivityResult;
import io.shulie.takin.web.data.result.baseserver.TraceMetricsResult;
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

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author qianshui
 * @date 2020/5/12 下午3:33
 */
@Service
@Slf4j
public class ReportServiceImpl implements ReportService {

    @Autowired
    private HttpWebClient httpWebClient;
    @Autowired
    private ReportApi reportApi;
    @Value("${file.upload.url:''}")
    private String fileUploadUrl;

    @Autowired
    private VerifyTaskReportService verifyTaskReportService;

    @Value("${file.upload.user.data.dir:/data/tmp}")
    private String fileDir;

    @Autowired
    private ActivityDAO activityDAO;

    @Autowired
    private ActivityService activityService;

    @Autowired
    private InfluxDatabaseManager influxDBManager;

    private static String pradarDatabase = "pradar";

    @Autowired
    private ReportRealTimeService reportRealTimeService;

    @Override
    public WebResponse listReport(ReportQueryParam param) {
        param.setRequestUrl(RemoteConstant.REPORT_LIST);
        param.setHttpMethod(HttpMethod.GET);
        // 前端查询条件 传用户
        if (StringUtils.isNotBlank(param.getUserName())) {
            List<UserExt> userList = WebPluginUtils.selectByName(param.getUserName());
            if (CollectionUtils.isNotEmpty(userList)) {
                List<Long> userIds = userList.stream().map(UserExt::getId).collect(Collectors.toList());
                if (CollectionUtils.isEmpty(userIds)) {
                    param.setUserIdStr(null);
                } else {
                    param.setUserIdStr(StringUtils.join(userIds, ","));
                }
            } else {
                return WebResponse.success(Lists.newArrayList());
            }
        }
        WebResponse webResponse = httpWebClient.request(param);

        if (!webResponse.getSuccess()) {
            ErrorInfo error = webResponse.getError();
            String errorMsg = Objects.isNull(error) ? "" : error.getMsg();
            throw new TakinWebException(TakinWebExceptionEnum.SCENE_REPORT_THIRD_PARTY_ERROR,
                "takin-cloud查询报告出错！原因：" + errorMsg);
        }
        List<Map> webRespData = (List<Map>)webResponse.getData();
        if (webRespData == null) {
            throw new TakinWebException(TakinWebExceptionEnum.SCENE_REPORT_THIRD_PARTY_ERROR, "takin-cloud查询报告返回为空！");
        }
        List<Long> userIds = webRespData.stream().filter(data -> null != data.get("userId"))
            .map(data -> Long.valueOf(data.get("userId").toString()))
            .collect(Collectors.toList());
        //用户信息Map key:userId  value:user对象
        Map<Long, UserExt> userMap = WebPluginUtils.getUserMapByIds(userIds);
        webRespData.forEach(data -> {
            Long userId = data.get("userId") == null ? null : Long.valueOf(data.get("userId").toString());
            //负责人名称
            String userName = Optional.ofNullable(userMap.get(userId))
                .map(UserExt::getName)
                .orElse("");
            data.put("userName", userName);
            data.put("userId", userId);
        });
        return webResponse;
    }

    @Override
    public ResponseResult<ReportDetailResp> getReportByReportId(Long reportId) {
        ReportDetailByIdReq req = new ReportDetailByIdReq();
        req.setReportId(reportId);
        ResponseResult<ReportDetailResp> result = reportApi.getReportByReportId(req);
        if (result == null) {
            throw new TakinWebException(TakinWebExceptionEnum.SCENE_REPORT_THIRD_PARTY_ERROR, "takin-cloud查询实况报告不存在!");
        }

        if (result.getSuccess()) {
            ReportDetailOutput output = new ReportDetailOutput();
            BeanUtils.copyProperties(result.getData(), output);
            assembleVerifyResult(output);
            // 虚拟业务活动处理
            //dealVirtualBusiness(output);
            //补充报告执行人
            fillExecuteMan(output);
            return result;
        }

        throw new TakinWebException(TakinWebExceptionEnum.SCENE_REPORT_THIRD_PARTY_ERROR,
            "takin-cloud查询实况报告错误，原因为：" + result.getError().getMsg());
    }

    private void fillExecuteMan(ReportDetailOutput output) {
        if (output == null || output.getUserId() == null) {
            return;
        }
        WebPluginUtils.fillUserData(output);
    }

    /**
     * todo 不做需要修改
     */
    private void dealVirtualBusiness(ReportDetailOutput output) {
        List<Long> ids = output.getBusinessActivity().stream().map(BusinessActivitySummaryBean::getBusinessActivityId)
            .collect(Collectors.toList());
        ActivityQueryParam param = new ActivityQueryParam();
        param.setActivityIds(ids);
        List<ActivityListResult> result = activityDAO.getActivityList(param);
        Map<Long, List<ActivityListResult>> map = result.stream().collect(
            Collectors.groupingBy(ActivityListResult::getActivityId));
        List<BusinessActivitySummaryBean> beans = output.getBusinessActivity();
        for (BusinessActivitySummaryBean bean : output.getBusinessActivity()) {
            List<ActivityListResult> activityResults = map.get(bean.getBusinessActivityId());
            if (activityResults != null && activityResults.size() > 0) {
                ActivityListResult listResult = activityResults.get(0);
                if (listResult.getBusinessType().equals(BusinessTypeEnum.VIRTUAL_BUSINESS.getType())) {
                    ActivityResult activityResult = activityDAO.getActivityById(listResult.getBindBusinessId());
                    BusinessActivitySummaryBean activitySummaryBean = new BusinessActivitySummaryBean();
                    activitySummaryBean.setBusinessActivityId(activityResult.getActivityId());
                    activitySummaryBean.setBusinessActivityName(activityResult.getActivityName());
                    beans.add(activitySummaryBean);
                }
            }
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
    public ResponseResult<ReportTrendResp> queryReportTrend(ReportTrendQueryParam param) {
        ReportTrendQueryReq reportTrendQueryReq = new ReportTrendQueryReq();
        reportTrendQueryReq.setSceneId(param.getSceneId());
        reportTrendQueryReq.setReportId(param.getReportId());
        return reportApi.reportTrend(reportTrendQueryReq);
    }

    //@Override
    //public WebResponse queryReportTrendWithTopology(ReportTrendQueryParam reportTrendQuery) {
    //    // 获得 报告链路趋势
    //    ResponseResult<ReportTrendResp>  webResponse= queryReportTrend(reportTrendQuery);
    //    Long businessActivityId = reportTrendQuery.getBusinessActivityId();
    //    if (businessActivityId.equals(0L)) { // 如果 活动ID 为0，则表示 全局趋势
    //        return webResponse;
    //    }
    //
    //    // 获取 `压测报告`的`请求流量明细`. 取最晚一条 traceId
    //    PageInfo<ReportTraceDTO> reportLinkListByReportId = reportRealTimeService.getReportLinkListByReportId(
    //            reportTrendQuery.getReportId(), null, 0, 1);
    //    List<ReportTraceDTO> reportTraceDTOS = reportLinkListByReportId.getList();
    //    if (CollectionUtils.isEmpty(reportTraceDTOS)) {
    //        return webResponse; // todo
    //    }
    //    String latestTraceIdStr = reportTraceDTOS.get(0).getTraceId();
    //
    //    // 取最早一条 traceId
    //    int total = (int) reportLinkListByReportId.getTotal();
    //    log.info("压测报告 report id = " + reportTrendQuery.getReportId() + " 总条数:" + total);
    //    reportLinkListByReportId = reportRealTimeService.getReportLinkListByReportId(
    //            reportTrendQuery.getReportId(), null, total, 1);
    //    reportTraceDTOS = reportLinkListByReportId.getList();
    //    if (CollectionUtils.isEmpty(reportTraceDTOS)) {
    //        return webResponse; // todo
    //    }
    //    String firstTraceId = reportTraceDTOS.get(0).getTraceId();
    //
    //    LinkedHashMap<String, Object> data = webResponse.getData();
    //    return getWebResponse(webResponse, firstTraceId, latestTraceIdStr, businessActivityId, data);
    //}

    private LocalDateTime queryDataTimeByTraceId(String traceId) {
        String queryByTraceId =
            "SELECT" +
                " time, totalTps" +
                " FROM trace_metrics" +
                " where" +
                " traceId = '" + traceId + "'";

        Collection<TraceMetricsResult> results = influxDBManager.query(TraceMetricsResult.class, queryByTraceId,
            pradarDatabase);
        ArrayList<TraceMetricsResult> queryByTraceIdList = new ArrayList<>(results);

        LocalDateTime localDateTime = null;
        if (queryByTraceIdList.size() != 0) {
            TraceMetricsResult traceMetricsResult = queryByTraceIdList.get(0);
            localDateTime = LocalDateTime.ofInstant(traceMetricsResult.getTime(), ZoneId.systemDefault());
        }
        return localDateTime;
    }

    @Override
    public ResponseResult<ReportDetailResp> tempReportDetail(Long sceneId) {
        ReportDetailBySceneIdReq req = new ReportDetailBySceneIdReq();
        req.setSceneId(sceneId);
        ResponseResult<ReportDetailResp> result = reportApi.tempReportDetail(req);
        if (!result.getSuccess()) {
            return ResponseResult.fail(result.getError().getCode(), result.getError().getMsg());
        }
        ReportDetailResp resp = result.getData();
        ReportDetailTempOutput output = new ReportDetailTempOutput();
        BeanUtils.copyProperties(resp, output);

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
        return ResponseResult.success(resp);

    }

    @Override
    public ResponseResult<ReportTrendResp> queryTempReportTrend(ReportTrendQueryParam param) {
        ReportTrendQueryReq reportTrendQueryReq = new ReportTrendQueryReq();
        reportTrendQueryReq.setSceneId(param.getSceneId());
        reportTrendQueryReq.setReportId(param.getReportId());
        return reportApi.tmpReportTrend(reportTrendQueryReq);
    }

    //@Override
    //public WebResponse queryTempReportTrendWithTopology(ReportTrendQueryParam param, ReportTraceQueryDTO queryDTO) {
    //    // 获得 实况报告链路趋势
    //    ResponseResult<ReportTrendResp> reportTrendRespResponseResult = queryTempReportTrend(param);
    //    Long businessActivityId = param.getBusinessActivityId();
    //    if (businessActivityId.equals(0L)) { // 如果 活动ID 为0，则表示 全局趋势
    //        return webResponse;
    //    }
    //
    //    // 获取 `压测实况`的`请求流量明细`. 取最晚一条 traceId
    //    PageInfo<ReportTraceDTO> reportLinkListByReportId =
    //            reportRealTimeService.getReportLinkList(queryDTO.getReportId(), queryDTO.getSceneId(), queryDTO
    //           .getStartTime(), queryDTO.getType(), 0, 1);
    //    List<ReportTraceDTO> reportTraceDTOS = reportLinkListByReportId.getList();
    //    if (CollectionUtils.isEmpty(reportTraceDTOS)) {
    //        return webResponse; // todo
    //    }
    //    String latestTraceIdStr = reportTraceDTOS.get(0).getTraceId();
    //
    //    // 取最早一条 traceId
    //    int total = (int) reportLinkListByReportId.getTotal();
    //    reportLinkListByReportId = reportRealTimeService.getReportLinkListByReportId(
    //            queryDTO.getReportId(), null, total, 1);
    //    reportTraceDTOS = reportLinkListByReportId.getList();
    //    if (CollectionUtils.isEmpty(reportTraceDTOS)) {
    //        return webResponse; // todo
    //    }
    //    String firstTraceId = reportTraceDTOS.get(0).getTraceId();
    //
    //    LinkedHashMap<String, Object> data = webResponse.getData();
    //    return getWebResponse(webResponse, firstTraceId, latestTraceIdStr, businessActivityId, data);
    //}

    private WebResponse getWebResponse(WebResponse<LinkedHashMap<String, Object>> webResponse, String firstTraceId,
        String lastTraceId, Long businessActivityId, LinkedHashMap<String, Object> data) {
        LocalDateTime now = LocalDateTime.now();

        LocalDateTime startDateTime = queryDataTimeByTraceId(firstTraceId);
        LocalDateTime endDateTime = queryDataTimeByTraceId(lastTraceId);

        boolean isCompletion = false; // 报告是否完整入库
        if (null != startDateTime && null != endDateTime) {
            isCompletion = true;
        }
        startDateTime = startDateTime == null ? now : startDateTime;
        endDateTime = endDateTime == null ? now : endDateTime;

        // 查询 链路拓扑图
        ActivityResponse activity = activityService
            .getActivityWithMetricsByIdForReport(businessActivityId, startDateTime, endDateTime);
        // 响应中 加入 链路拓扑图
        data.put("activity", activity);
        data.put("isCompletion", isCompletion);
        webResponse.setData(data);
        return webResponse;
    }

    @Override
    public WebResponse listWarn(WarnQueryParam param) {
        param.setRequestUrl(RemoteConstant.WARN_LIST);
        param.setHttpMethod(HttpMethod.GET);
        WebResponse webResponse = httpWebClient.request(param);
        HttpAssert.isOk(webResponse, param, "takin-cloud查询警告列表");
        return webResponse;

    }

    @Override
    public WebResponse queryReportActivityByReportId(Long reportId) {
        ReportIdVO vo = new ReportIdVO();
        vo.setReportId(reportId);
        vo.setRequestUrl(RemoteConstant.REPORT_BUSINESSACTIVITY_LIST);
        vo.setHttpMethod(HttpMethod.GET);
        WebResponse webResponse = httpWebClient.request(vo);
        HttpAssert.isOk(webResponse, vo, "takin-cloud通过报告ID查询报告的业务活动");
        return webResponse;
    }

    @Override
    public WebResponse queryReportActivityBySceneId(Long sceneId) {
        SceneIdVO vo = new SceneIdVO();
        vo.setSceneId(sceneId);
        vo.setRequestUrl(RemoteConstant.SCENE_BUSINESSACTIVITY_LIST);
        vo.setHttpMethod(HttpMethod.GET);
        WebResponse webResponse = httpWebClient.request(vo);
        HttpAssert.isOk(webResponse, vo, "takin-cloud通过场景ID查询报告的业务活动");
        return webResponse;
    }

    @Override
    public ResponseResult<NodeTreeSummaryResp> querySummaryList(Long reportId) {
        return reportApi.getBusinessActivitySummaryList(reportId);
    }

    @Override
    public WebResponse queryMetrices(Long reportId, Long sceneId, Long customerId) {
        ReportIdVO vo = new ReportIdVO();
        vo.setReportId(reportId);
        vo.setSceneId(sceneId);
        vo.setCustomerId(customerId);
        vo.setRequestUrl(RemoteConstant.REPORT_METRICES);
        vo.setHttpMethod(HttpMethod.GET);
        return httpWebClient.request(vo);
    }

    @Override
    public List<Map<String, Object>> listMetrics(Long reportId, Long sceneId, Long customerId) {
        ReportIdVO vo = new ReportIdVO();
        vo.setReportId(reportId);
        vo.setSceneId(sceneId);
        vo.setCustomerId(customerId);
        vo.setRequestUrl(RemoteConstant.REPORT_METRICES);
        vo.setHttpMethod(HttpMethod.GET);
        WebResponse response = httpWebClient.request(vo);
        if (response == null) {
            throw ApiException.create(500, String.format("请求 cloud 指标错误! url: %s", RemoteConstant.REPORT_METRICES));
        }

        Object metricsObject = response.getData();
        if (metricsObject == null) {
            return Collections.emptyList();
        }

        return (List<Map<String, Object>>)metricsObject;
    }

    @Override
    public WebResponse queryReportCount(Long reportId) {
        ReportIdVO vo = new ReportIdVO();
        vo.setReportId(reportId);
        vo.setRequestUrl(RemoteConstant.REPORT_COUNT);
        vo.setHttpMethod(HttpMethod.GET);
        return httpWebClient.request(vo);
    }

    @Override
    public WebResponse queryRunningReport() {
        WebRequest request = new WebRequest();
        request.setRequestUrl(RemoteConstant.REPORT_RUNNINNG);
        request.setHttpMethod(HttpMethod.GET);
        return httpWebClient.request(request);
    }

    @Override
    public WebResponse queryListRunningReport() {
        WebRequest request = new WebRequest();
        request.setRequestUrl(RemoteConstant.REPORT_RUNNINNG_LIST);
        request.setHttpMethod(HttpMethod.GET);
        return httpWebClient.request(request);
    }

    @Override
    public WebResponse lockReport(Long reportId) {
        ReportIdVO vo = new ReportIdVO();
        vo.setReportId(reportId);
        vo.setRequestUrl(RemoteConstant.REPORT_LOCK);
        vo.setHttpMethod(HttpMethod.PUT);
        return httpWebClient.request(vo);
    }

    @Override
    public WebResponse unLockReport(Long reportId) {
        ReportIdVO vo = new ReportIdVO();
        vo.setReportId(reportId);
        vo.setRequestUrl(RemoteConstant.REPORT_UNLOCK);
        vo.setHttpMethod(HttpMethod.PUT);
        return httpWebClient.request(vo);
    }

    @Override
    public WebResponse finishReport(Long reportId) {
        ReportIdVO vo = new ReportIdVO();
        vo.setReportId(reportId);
        vo.setRequestUrl(RemoteConstant.REPORT_FINISH);
        vo.setHttpMethod(HttpMethod.PUT);
        return httpWebClient.request(vo);
    }

    @Override
    public ResponseResult<List<ScriptNodeTreeResp>> queryNodeTree(ReportQueryRequest request) {
        ResponseResult<List<ScriptNodeTreeResp>> listResponseResult = reportApi.scriptNodeTree(
            new ScriptNodeTreeQueryReq() {{
                setSceneId(request.getSceneId());
                setReportId(request.getReportId());
            }});
        return listResponseResult;
    }
}
