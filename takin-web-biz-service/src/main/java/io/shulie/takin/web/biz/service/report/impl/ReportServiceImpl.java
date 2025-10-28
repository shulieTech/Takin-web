package io.shulie.takin.web.biz.service.report.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.collect.Lists;
import com.pamirs.takin.common.constant.VerifyResultStatusEnum;
import com.pamirs.takin.entity.domain.dto.report.LeakVerifyResult;
import com.pamirs.takin.entity.domain.dto.report.ReportDTO;
import com.pamirs.takin.entity.domain.vo.report.ReportQueryParam;
import io.shulie.takin.cloud.common.influxdb.InfluxWriter;
import io.shulie.takin.cloud.entrypoint.report.CloudReportApi;
import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import io.shulie.takin.cloud.sdk.model.common.BusinessActivitySummaryBean;
import io.shulie.takin.cloud.sdk.model.request.common.CloudCommonInfoWrapperReq;
import io.shulie.takin.cloud.sdk.model.request.report.*;
import io.shulie.takin.cloud.sdk.model.response.report.*;
import io.shulie.takin.cloud.sdk.model.response.scenemanage.WarnDetailResponse;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.utils.json.JsonHelper;
import io.shulie.takin.web.biz.pojo.output.report.ReportDetailOutput;
import io.shulie.takin.web.biz.pojo.output.report.ReportDetailTempOutput;
import io.shulie.takin.web.biz.pojo.request.leakverify.LeakVerifyTaskReportQueryRequest;
import io.shulie.takin.web.biz.pojo.request.report.ReportTrendQueryReq;
import io.shulie.takin.web.biz.pojo.request.report.ReportTrendResp;
import io.shulie.takin.web.biz.pojo.response.leakverify.LeakVerifyTaskResultResponse;
import io.shulie.takin.web.biz.service.VerifyTaskReportService;
import io.shulie.takin.web.biz.service.report.ReportService;
import io.shulie.takin.web.common.enums.activity.BusinessTypeEnum;
import io.shulie.takin.web.common.exception.TakinWebException;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
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
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
    @Autowired
    private ReportApi reportApi;
    @Autowired
    private CloudReportApi cloudReportApi;

    @Autowired
    private VerifyTaskReportService verifyTaskReportService;

    @Autowired
    private ActivityDAO activityDAO;


    @Resource
    private InfluxWriter influxWriter;

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
        if(detailResponse != null && detailResponse.getSa() != null
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
        if (output == null) {return;}
        // 获取用户信息
        Map<Long, UserExt> userInfo = WebPluginUtils.getUserMapByIds(
            new ArrayList<Long>(1) {{add(output.getUserId());}});
        // 填充用户信息
        if (userInfo.containsKey(output.getUserId())) {
            output.setOperateId(output.getUserId().toString());
            output.setUserName(userInfo.get(output.getUserId()).getName());
            output.setOperateName(userInfo.get(output.getUserId()).getName());
        }
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
    public TrendResponse queryReportTrend(TrendRequest param) {
        return cloudReportApi.trend(param);
    }

    @Override
    public ReportDetailTempOutput tempReportDetail(Long sceneId) {
        ReportDetailBySceneIdReq req = new ReportDetailBySceneIdReq();
        req.setSceneId(sceneId);
        ResponseResult<ReportDetailResp> result = reportApi.tempReportDetail(req);
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
        return output;

    }

    @Override
    public TrendResponse queryTempReportTrend(TrendRequest param) {
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
    public List<BusinessActivitySummaryBean> querySummaryList(Long reportId) {
        return cloudReportApi.summary(new ReportDetailByIdReq() {{
            setReportId(reportId);
        }});
    }

    @Override
    public List<MetricesResponse> queryMetrics(Long reportId, Long sceneId) {
        return cloudReportApi.metrics(new TrendRequest() {{
            setReportId(reportId);
            setSceneId(sceneId);
        }});
    }

    @Override
    public Map<String, Object> queryReportCount(Long reportId) {
        return cloudReportApi.warnCount(new ReportDetailByIdReq() {{
            setReportId(reportId);
        }});
    }

    @Override
    public Long queryRunningReport() {
        return cloudReportApi.listRunning(new ContextExt());
    }

    @Override
    public List<Long> queryListRunningReport() {
        CloudCommonInfoWrapperReq req = new CloudCommonInfoWrapperReq();
        WebPluginUtils.fillCloudUserData(req);
        ResponseResult<List<Long>> result = reportApi.queryListRunningReport(req);
        if (result == null || !result.getSuccess()) {
            throw new TakinWebException(TakinWebExceptionEnum.SCENE_REPORT_THIRD_PARTY_ERROR,
                Optional.ofNullable(result).map(ResponseResult::getError).map(JsonHelper::bean2Json).orElse("cloud查询异常"));
        }
        return result.getData();
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
    public ReportTrendResp queryReportTrend(ReportTrendQueryReq param) {
        return trend(param);
    }

    private ReportTrendResp trend(ReportTrendQueryReq req) {
        String key = "report_trend:" + req.getReportId() + ":" + req.getXpathMd5();
        try {
            ReportTrendResp data;

            data = queryReportTrend(req, false);

            return data;
        } catch (Exception e) {
            log.error("获取报告趋势数据异常", e);
            return new ReportTrendResp();
        }
    }

    private ReportTrendResp queryReportTrend(ReportTrendQueryReq reportTrendQuery, boolean isTempReport) {
        long start = System.currentTimeMillis();
        ReportTrendResp reportTrend = new ReportTrendResp();
        try {



            StringBuilder influxDbSql = new StringBuilder();
            influxDbSql.append("select");
            influxDbSql.append(
                    " sum(count) as tempRequestCount, sum(fail_count) as failRequest, mean(avg_tps) as tps , sum(sum_rt)/sum"
                            + "(count) as "
                            + "avgRt, sum(sa_count) as saCount, count(avg_rt) as recordCount ,mean(active_threads) as "
                            + "avgConcurrenceNum ");
            influxDbSql.append(" from ");
            influxDbSql.append(" pressure ");
            influxDbSql.append(" where ");
            influxDbSql.append(" transaction = ").append("'").append("transaction").append("'");

            if (reportTrendQuery.getStartTime() != null) {
                influxDbSql.append(" and time >= ").append(reportTrendQuery.getStartTime().getTime() * 1_000_000L);
            }
            if (reportTrendQuery.getEndTime() != null){
                influxDbSql.append(" and time <= ").append(DateUtils.addMinutes(reportTrendQuery.getEndTime(), 5).getTime() * 1_000_000L);
            }

            //按配置中的时间间隔分组
            influxDbSql.append(" group by time(").append(60).append(")");

            List<Object> list = Lists.newArrayList();

            list = influxWriter.query(influxDbSql.toString(), Object.class);


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

//            list.stream()
//                    .filter(Objects::nonNull)
//                    .filter(data -> data.getTps() != null)
//                    .filter(data -> StringUtils.isNotBlank(data.getTime()))
//                    .forEach(data -> {
//                        time.add(getTime(data.getTime()));
//                        sa.add(NumberUtil.decimalToString(data.getSa()));
//                        avgRt.add(NumberUtil.decimalToString(data.getAvgRt()));
//                        tps.add(NumberUtil.decimalToString(data.getTps()));
//                        successRate.add(NumberUtil.decimalToString(data.getSuccessRate()));
//                        concurrent.add(NumberUtil.decimalToString(data.getAvgConcurrenceNum()));
//                    });
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

}