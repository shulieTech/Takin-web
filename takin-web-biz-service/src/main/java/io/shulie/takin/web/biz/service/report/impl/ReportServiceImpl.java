package io.shulie.takin.web.biz.service.report.impl;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.pamirs.takin.common.constant.VerifyResultStatusEnum;
import com.pamirs.takin.entity.domain.dto.report.LeakVerifyResult;
import com.pamirs.takin.entity.domain.vo.report.ReportIdVO;
import com.pamirs.takin.entity.domain.vo.report.ReportQueryParam;
import com.pamirs.takin.entity.domain.vo.report.ReportTrendQueryParam;
import com.pamirs.takin.entity.domain.vo.report.SceneIdVO;
import com.pamirs.takin.entity.domain.vo.sla.WarnQueryParam;
import io.shulie.takin.cloud.common.bean.scenemanage.BusinessActivitySummaryBean;
import io.shulie.takin.cloud.open.req.report.ReportDetailByIdReq;
import io.shulie.takin.cloud.open.req.report.ReportDetailBySceneIdReq;
import io.shulie.takin.cloud.open.resp.report.ReportDetailResp;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.biz.pojo.output.report.ReportDetailOutput;
import io.shulie.takin.web.biz.pojo.output.report.ReportDetailTempOutput;
import io.shulie.takin.web.biz.pojo.request.leakverify.LeakVerifyTaskReportQueryRequest;
import io.shulie.takin.web.biz.pojo.response.leakverify.LeakVerifyTaskResultResponse;
import io.shulie.takin.web.biz.service.VerifyTaskReportService;
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
import io.shulie.takin.web.data.dao.activity.ActivityDAO;
import io.shulie.takin.web.data.param.activity.ActivityQueryParam;
import io.shulie.takin.web.data.result.activity.ActivityListResult;
import io.shulie.takin.web.data.result.activity.ActivityResult;
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
            throw new TakinWebException(TakinWebExceptionEnum.SCENE_REPORT_THIRD_PARTY_ERROR, "takin-cloud查询报告出错！原因：" + errorMsg);
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
    public WebResponse getReportByReportId(Long reportId) {
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
            return WebResponse.success(output);
        }

        throw new TakinWebException(TakinWebExceptionEnum.SCENE_REPORT_THIRD_PARTY_ERROR, "takin-cloud查询实况报告错误，原因为：" + result.getError().getMsg());
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
        List<Long> ids = output.getBusinessActivity().stream().map(BusinessActivitySummaryBean::getBusinessActivityId).collect(Collectors.toList());
        ActivityQueryParam param = new ActivityQueryParam();
        param.setActivityIds(ids);
        List<ActivityListResult> result = activityDAO.getActivityList(param);
        Map<Long, List<ActivityListResult>> map = result.stream().collect(Collectors.groupingBy(ActivityListResult::getActivityId));
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
    public WebResponse queryReportTrend(ReportTrendQueryParam param) {
        param.setRequestUrl(RemoteConstant.REPORT_TREND);
        param.setHttpMethod(HttpMethod.GET);
        WebResponse webResponse = httpWebClient.request(param);
        HttpAssert.isOk(webResponse, param, "takin-cloud查询报告链路趋势");
        return webResponse;
    }

    @Override
    public WebResponse tempReportDetail(Long sceneId) {
        ReportDetailBySceneIdReq req = new ReportDetailBySceneIdReq();
        req.setSceneId(sceneId);
        ResponseResult<ReportDetailResp> result = reportApi.tempReportDetail(req);
        if (!result.getSuccess()) {
            return WebResponse.fail(result.getError().getCode(), result.getError().getMsg());
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
        return WebResponse.success(output);

    }

    @Override
    public WebResponse queryTempReportTrend(ReportTrendQueryParam param) {
        param.setRequestUrl(RemoteConstant.REPORT_REALTIME_TREND);
        param.setHttpMethod(HttpMethod.GET);
        WebResponse webResponse = httpWebClient.request(param);
        HttpAssert.isOk(webResponse, param, "takin-cloud查询实况报告链路趋势");
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
    public WebResponse querySummaryList(Long reportId) {
        ReportIdVO vo = new ReportIdVO();
        vo.setReportId(reportId);
        vo.setRequestUrl(RemoteConstant.REPORT_SUMMARY_LIST);
        vo.setHttpMethod(HttpMethod.GET);
        WebResponse webResponse = httpWebClient.request(vo);
        HttpAssert.isOk(webResponse, vo, "takin-cloud查询压测明细");
        return webResponse;
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

}
