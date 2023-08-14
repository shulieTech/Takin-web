package io.shulie.takin.web.biz.service.report.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.pamirs.takin.common.constant.VerifyResultStatusEnum;
import com.pamirs.takin.entity.domain.dto.report.LeakVerifyResult;
import com.pamirs.takin.entity.domain.dto.report.ReportDTO;
import com.pamirs.takin.entity.domain.vo.report.ReportQueryParam;
import io.shulie.takin.adapter.api.entrypoint.report.CloudReportApi;
import io.shulie.takin.adapter.api.model.request.report.*;
import io.shulie.takin.adapter.api.model.response.report.*;
import io.shulie.takin.adapter.api.model.response.scenemanage.WarnDetailResponse;
import io.shulie.takin.cloud.biz.service.report.CloudReportService;
import io.shulie.takin.cloud.data.dao.report.ReportDao;
import io.shulie.takin.cloud.data.mapper.mysql.ReportMapper;
import io.shulie.takin.cloud.data.model.mysql.ReportBusinessActivityDetailEntity;
import io.shulie.takin.cloud.data.model.mysql.ReportEntity;
import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.biz.pojo.output.report.ReportDetailOutput;
import io.shulie.takin.web.biz.pojo.output.report.ReportDetailTempOutput;
import io.shulie.takin.web.biz.pojo.output.report.ReportDownLoadOutput;
import io.shulie.takin.web.biz.pojo.output.report.ReportJtlDownloadOutput;
import io.shulie.takin.web.biz.pojo.output.scene.SceneReportListOutput;
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
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
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

    @Autowired
    private CloudReportApi cloudReportApi;
    @Resource
    private VerifyTaskReportService verifyTaskReportService;

    @Value("${file.upload.url:''}")
    private String fileUploadUrl;

    @Autowired
    private PDFUtil pdfUtil;

    @Autowired
    private DistributedLock distributedLock;
    @Resource
    private ReportDao reportDao;
    @Resource
    private ReportMapper reportMapper;

    @Autowired
    private CloudReportService cloudReportService;

    @Autowired
    private ActivityService activityService;

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

    /**
     * 获取最近一小时的报告ids
     *
     * @return
     */
    @Override
    public List<Long> nearlyHourReportIds(int minutes) {
        return reportDao.nearlyHourReportIds(minutes);
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

    @Override
    public List<SceneReportListOutput> getReportListBySceneId(Long sceneId) {
        ReportDetailBySceneIdReq req = new ReportDetailBySceneIdReq();
        req.setSceneId(sceneId);
        List<ReportDetailResp> respList = cloudReportApi.detailListBySceneId(req);
        List<SceneReportListOutput> outputList = new ArrayList<>();
        if (CollectionUtils.isEmpty(respList)) {
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
        return ResponseResult.success(activityResponse);
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
//        request.setFlowTypeEnum(FlowTypeEnum.PRESSURE_MEASUREMENT);
        request.setStartTime(reportLinkDiagramReq.getStartTime());
        request.setEndTime(reportLinkDiagramReq.getEndTime());
        request.setTempActivity(false);
        return activityService.getActivityWithMetricsById(request);
    }

    private static LocalDateTime transferUTC(LocalDateTime ldt) {
        if (ldt == null) {
            return null;
        }
        ZoneId originalZone = ZoneId.systemDefault();
        ZonedDateTime zdt = ldt.atZone(originalZone);
        ZoneId utcZone = ZoneId.of("UTC");
        ZonedDateTime utcZdt = zdt.withZoneSameInstant(utcZone);
        return utcZdt.toLocalDateTime();
    }

    @Override
    public void modifyLinkDiagram(ReportLinkDiagramReq reportLinkDiagramReq) {
        reportLinkDiagramReq.setEndTime(LocalDateTime.now());
        ReportBusinessActivityDetailEntity detail = reportDao.getReportBusinessActivityDetail(reportLinkDiagramReq.getSceneId(), reportLinkDiagramReq.getXpathMd5(), reportLinkDiagramReq.getReportId());
        if (detail == null) {
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
        if (CollectionUtils.isEmpty(detailList)) {
            throw new TakinWebException(TakinWebExceptionEnum.SCENE_REPORT_VALIDATE_ERROR, "重新生成拓扑图，没有获取到对应的数据!");
        }
        for (ReportBusinessActivityDetailEntity detailEntity : detailList) {
            io.shulie.takin.web.biz.pojo.response.activity.ActivityResponse activityResponse = queryLinkDiagram(detailEntity.getBusinessActivityId(), reportLinkDiagramReq);
            if (activityResponse != null) {
                reportDao.modifyReportLinkDiagram(reportLinkDiagramReq.getReportId(), detailEntity.getBindRef(), JSON.toJSONString(activityResponse));
            }
        }
    }

}