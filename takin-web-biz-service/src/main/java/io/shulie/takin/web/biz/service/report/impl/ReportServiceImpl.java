package io.shulie.takin.web.biz.service.report.impl;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.pamirs.takin.common.constant.VerifyResultStatusEnum;
import com.pamirs.takin.entity.domain.dto.report.LeakVerifyResult;
import com.pamirs.takin.entity.domain.dto.report.ReportDTO;
import com.pamirs.takin.entity.domain.vo.report.ReportQueryParam;
import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import io.shulie.takin.adapter.api.entrypoint.report.CloudReportApi;
import io.shulie.takin.adapter.api.model.request.report.ReportDetailByIdReq;
import io.shulie.takin.adapter.api.model.request.report.ReportDetailBySceneIdReq;
import io.shulie.takin.adapter.api.model.request.report.ReportQueryReq;
import io.shulie.takin.adapter.api.model.request.report.ReportTrendQueryReq;
import io.shulie.takin.adapter.api.model.request.report.ScriptNodeTreeQueryReq;
import io.shulie.takin.adapter.api.model.request.report.TrendRequest;
import io.shulie.takin.adapter.api.model.request.report.WarnQueryReq;
import io.shulie.takin.adapter.api.model.response.report.ActivityResponse;
import io.shulie.takin.adapter.api.model.response.report.MetricesResponse;
import io.shulie.takin.adapter.api.model.response.report.NodeTreeSummaryResp;
import io.shulie.takin.adapter.api.model.response.report.ReportDetailResp;
import io.shulie.takin.adapter.api.model.response.report.ReportResp;
import io.shulie.takin.adapter.api.model.response.report.ReportTrendResp;
import io.shulie.takin.adapter.api.model.response.report.ScriptNodeTreeResp;
import io.shulie.takin.adapter.api.model.response.scenemanage.WarnDetailResponse;
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

/**
 * @author qianshui
 * @date 2020/5/12 ??????3:33
 */
@Service
@Slf4j
public class ReportServiceImpl implements ReportService {
    @Resource
    private ReportApi reportApi;
    @Resource
    private ActivityDAO activityDAO;
    @Resource
    private CloudReportApi cloudReportApi;
    @Resource
    private VerifyTaskReportService verifyTaskReportService;

    @Value("${file.upload.url:''}")
    private String fileUploadUrl;

    @Autowired
    private PDFUtil pdfUtil;

    @Autowired
    private DistributedLock distributedLock;

    @Override
    public ResponseResult<List<ReportDTO>> listReport(ReportQueryParam param) {
        // ?????????????????? ?????????
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
        //????????????Map key:userId  value:user??????
        Map<Long, UserExt> userMap = WebPluginUtils.getUserMapByIds(userIds);
        List<ReportDTO> dtoList = reportResponseList.getData().stream().map(t -> {
            Long userId = t.getUserId() == null ? null : Long.valueOf(t.getUserId().toString());
            //???????????????
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
        // sa??????100 ??????100
        if (detailResponse.getSa() != null
            && detailResponse.getSa().compareTo(BigDecimal.valueOf(100)) > 0) {
            detailResponse.setSa(BigDecimal.valueOf(100));
        }
        ReportDetailOutput output = new ReportDetailOutput();
        BeanUtils.copyProperties(detailResponse, output);
        assembleVerifyResult(output);
        // ????????????????????????
        //dealVirtualBusiness(output);
        //?????????????????????
        fillExecuteMan(output);
        return output;

    }

    private void fillExecuteMan(ReportDetailOutput output) {
        if (output == null) {return;}
        // ??????????????????
        Map<Long, UserExt> userInfo = WebPluginUtils.getUserMapByIds(
            new ArrayList<Long>(1) {{add(output.getUserId());}});
        // ??????????????????
        if (userInfo.containsKey(output.getUserId())) {
            output.setOperateId(output.getUserId().toString());
            output.setUserName(userInfo.get(output.getUserId()).getName());
            output.setOperateName(userInfo.get(output.getUserId()).getName());
        }
    }

    private void assembleVerifyResult(ReportDetailOutput output) {
        //????????????????????????
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
    public String downloadPDFPath(Long reportId){
        String lockKey = String.format(LockKeyConstants.LOCK_REPORT_EXPORT,reportId);
        if(!distributedLock.tryLockSecondsTimeUnit(lockKey, 0L, 30L)){
            throw  new TakinWebException(TakinWebExceptionEnum.SCENE_REPORT_EXPORT_ERROR, "???????????????!");
        }
        //???????????????????????????
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
                //??????????????????????????????
            }
            return path;
        }catch (IOException e){
            throw  new TakinWebException(TakinWebExceptionEnum.SCENE_REPORT_EXPORT_ERROR, e.getMessage(), e);
        }finally {
            distributedLock.unLock(lockKey);
        }
    }

}