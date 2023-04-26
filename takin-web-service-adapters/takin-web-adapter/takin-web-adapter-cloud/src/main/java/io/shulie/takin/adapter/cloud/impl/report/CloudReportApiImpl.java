package io.shulie.takin.adapter.cloud.impl.report;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSON;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.TypeReference;
import com.github.pagehelper.PageInfo;
import com.pamirs.takin.cloud.entity.domain.dto.report.BusinessActivityDTO;
import com.pamirs.takin.cloud.entity.domain.dto.report.CloudReportDTO;
import com.pamirs.takin.cloud.entity.domain.dto.report.Metrices;
import io.shulie.takin.adapter.api.constant.EntrypointUrl;
import io.shulie.takin.adapter.api.model.request.scenemanage.ReportActivityResp;
import io.shulie.takin.adapter.api.model.request.scenemanage.ReportDetailByIdsReq;
import io.shulie.takin.cloud.biz.input.report.UpdateReportConclusionInput;
import io.shulie.takin.cloud.biz.input.report.WarnCreateInput;
import io.shulie.takin.cloud.biz.output.report.ReportDetailOutput;
import io.shulie.takin.cloud.biz.output.scene.manage.WarnDetailOutput;
import io.shulie.takin.cloud.biz.service.report.CloudReportService;
import io.shulie.takin.cloud.common.exception.TakinCloudException;
import io.shulie.takin.cloud.common.exception.TakinCloudExceptionEnum;
import io.shulie.takin.web.common.util.RedisClientUtil;
import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.adapter.cloud.convert.WarnDetailRespConvertor;
import io.shulie.takin.adapter.api.entrypoint.report.CloudReportApi;
import io.shulie.takin.adapter.api.model.request.WarnQueryParam;
import io.shulie.takin.adapter.api.model.request.common.CloudCommonInfoWrapperReq;
import io.shulie.takin.adapter.api.model.request.report.JtlDownloadReq;
import io.shulie.takin.adapter.api.model.request.report.ReportDetailByIdReq;
import io.shulie.takin.adapter.api.model.request.report.ReportDetailBySceneIdReq;
import io.shulie.takin.adapter.api.model.request.report.ReportQueryReq;
import io.shulie.takin.adapter.api.model.request.report.ReportTrendQueryReq;
import io.shulie.takin.adapter.api.model.request.report.ScriptNodeTreeQueryReq;
import io.shulie.takin.adapter.api.model.request.report.TrendRequest;
import io.shulie.takin.adapter.api.model.request.report.UpdateReportConclusionReq;
import io.shulie.takin.adapter.api.model.request.report.WarnCreateReq;
import io.shulie.takin.adapter.api.model.request.report.WarnQueryReq;
import io.shulie.takin.adapter.api.model.response.report.ActivityResponse;
import io.shulie.takin.adapter.api.model.response.report.MetricesResponse;
import io.shulie.takin.adapter.api.model.response.report.NodeTreeSummaryResp;
import io.shulie.takin.adapter.api.model.response.report.ReportDetailResp;
import io.shulie.takin.adapter.api.model.response.report.ReportResp;
import io.shulie.takin.adapter.api.model.response.report.ReportTrendResp;
import io.shulie.takin.adapter.api.model.response.report.ScriptNodeTreeResp;
import io.shulie.takin.adapter.api.model.response.scenemanage.WarnDetailResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

/**
 * @author 无涯
 * @author 张天赐
 * @date 2020/12/17 1:29 下午
 */
@Service
@Slf4j
public class CloudReportApiImpl implements CloudReportApi {

    @Resource
    private CloudReportService cloudReportService;

    @Resource
    private RedisClientUtil redisClientUtil;

    @Override
    public ResponseResult<List<ReportResp>> listReport(ReportQueryReq req) {
        PageInfo<CloudReportDTO> reportList = cloudReportService.listReport(req);
        List<ReportResp> respList = reportList.getList().stream()
            .map(report -> BeanUtil.copyProperties(report, ReportResp.class))
            .collect(Collectors.toList());
        return ResponseResult.success(respList, reportList.getTotal());
    }

    @Override
    public ReportDetailResp detail(ReportDetailByIdReq req) {
        Long reportId = req.getReportId();
        ReportDetailOutput detailOutput = cloudReportService.getReportByReportId(reportId);
        if (detailOutput == null) {
            throw new TakinCloudException(TakinCloudExceptionEnum.REPORT_GET_ERROR, "报告不存在Id:" + reportId);
        }
        ReportDetailResp result = BeanUtil.copyProperties(detailOutput, ReportDetailResp.class);
        try {
            String jtlDownLoadUrl = cloudReportService.getJtlDownLoadUrl(result.getId(), false);
            log.debug("获取报告详情时获取JTL下载路径:{}.", jtlDownLoadUrl);
            result.setHasJtl(true);
        } catch (Throwable e) {
            result.setHasJtl(false);
        }
        return result;
    }

    @Override
    public List<ReportDetailResp> detailListBySceneId(ReportDetailBySceneIdReq req) {
        List<ReportDetailOutput> outputList = cloudReportService.getReportListBySceneId(req.getSceneId());
        List<ReportDetailResp> respList = new ArrayList<>();
        if(CollectionUtils.isEmpty(outputList)) {
            return respList;
        }
        outputList.stream().forEach(output -> {
            ReportDetailResp resp = new ReportDetailResp();
            resp.setId(output.getId());
            resp.setStartTime(output.getStartTime());
            resp.setConcurrent(output.getConcurrent());
            respList.add(resp);
        });
        return respList;
    }

    @Override
    public ReportTrendResp trend(ReportTrendQueryReq req) {
        try {
            String key = JSON.toJSONString(req);
            ReportTrendResp data;
            if (redisClientUtil.hasKey(key)) {
                data = JSON.parseObject(redisClientUtil.getString(key), ReportTrendResp.class);
                if (Objects.isNull(data)
                    || CollectionUtils.isEmpty(data.getConcurrent())
                    || CollectionUtils.isEmpty(data.getSa())
                    || CollectionUtils.isEmpty(data.getRt())
                    || CollectionUtils.isEmpty(data.getTps())
                    || CollectionUtils.isEmpty(data.getSuccessRate())) {
                    data = cloudReportService.queryReportTrend(req);
                    redisClientUtil.setString(key, JSON.toJSONString(data));
                }
            } else {
                data = cloudReportService.queryReportTrend(req);
                redisClientUtil.setString(key, JSON.toJSONString(data));
            }
            return data;
        } catch (Exception e) {
            return new ReportTrendResp();
        }
    }

    @Override
    public ReportTrendResp tempTrend(ReportTrendQueryReq req) {
        return cloudReportService.queryTempReportTrend(req);
    }

    @Override
    public String addWarn(WarnCreateReq req) {
        WarnCreateInput input = BeanUtil.copyProperties(req, WarnCreateInput.class);
        cloudReportService.addWarn(input);
        return "创建告警成功";
    }

    @Override
    public ResponseResult<List<WarnDetailResponse>> listWarn(WarnQueryReq req) {
        WarnQueryParam param = BeanUtil.copyProperties(req, WarnQueryParam.class);
        PageInfo<WarnDetailOutput> list = cloudReportService.listWarn(param);
        List<WarnDetailResponse> responses = WarnDetailRespConvertor.INSTANCE.ofList(list.getList());
        return ResponseResult.success(responses, list.getTotal());
    }

    /**
     * 根据报告主键获取业务活动
     *
     * @param req 报告主键
     * @return 业务活动
     */
    @Override
    public List<ActivityResponse> activityByReportId(ReportDetailByIdReq req) {
        List<BusinessActivityDTO> dtoList = cloudReportService.queryReportActivityByReportId(req.getReportId());
        if (CollectionUtils.isEmpty(dtoList)) {
            return new ArrayList<>(0);
        }
        return dtoList.stream().map(dto -> BeanUtil.copyProperties(dto, ActivityResponse.class)).collect(Collectors.toList());
    }

    /**
     * 根据场景主键获取业务活动
     *
     * @param req 场景主键
     * @return 业务活动
     */
    @Override
    public List<ActivityResponse> activityBySceneId(ReportDetailBySceneIdReq req) {
        List<BusinessActivityDTO> dtoList = cloudReportService.queryReportActivityBySceneId(req.getSceneId());
        if (CollectionUtils.isEmpty(dtoList)) {
            return new ArrayList<>(0);
        }
        return dtoList.stream().map(dto -> BeanUtil.copyProperties(dto, ActivityResponse.class)).collect(Collectors.toList());
    }

    @Override
    public String updateReportConclusion(UpdateReportConclusionReq req) {
        UpdateReportConclusionInput input = BeanUtil.copyProperties(req, UpdateReportConclusionInput.class);
        cloudReportService.updateReportConclusion(input);
        return "更新成功";
    }

    @Override
    public ReportDetailResp getReportByReportId(ReportDetailByIdReq req) {
        Long reportId = req.getReportId();
        ReportDetailOutput detailOutput = cloudReportService.getReportByReportId(reportId);
        if (detailOutput == null) {
            throw new TakinCloudException(TakinCloudExceptionEnum.REPORT_GET_ERROR, "报告不存在Id:" + reportId);
        }
        ReportDetailResp result = BeanUtil.copyProperties(detailOutput, ReportDetailResp.class);
        try {
            String jtlDownLoadUrl = cloudReportService.getJtlDownLoadUrl(result.getId(), false);
            log.debug("获取报告详情时获取JTL下载路径:{}.", jtlDownLoadUrl);
            result.setHasJtl(true);
        } catch (Throwable e) {
            result.setHasJtl(false);
        }
        return result;
    }

    @Override
    public ReportDetailResp tempReportDetail(ReportDetailBySceneIdReq req) {
        ReportDetailOutput detailOutput = cloudReportService.tempReportDetail(req.getSceneId());
        if (detailOutput == null) {
            throw new TakinCloudException(TakinCloudExceptionEnum.REPORT_GET_ERROR, "报告不存在");
        }
        ReportDetailResp resp = BeanUtil.copyProperties(detailOutput, ReportDetailResp.class);
        if (CollectionUtils.isNotEmpty(detailOutput.getStopReasons())) {
            resp.setStopReasons(detailOutput.getStopReasons());
        }
        return resp;
    }

    @Override
    public List<Long> queryListRunningReport(CloudCommonInfoWrapperReq req) {
        return cloudReportService.queryListRunningReport();
    }

    /**
     * 压测明细
     *
     * @param req 请求参数
     * @return 压测明细结果-节点树摘要
     */
    @Override
    public NodeTreeSummaryResp summary(ReportDetailByIdReq req) {
        return cloudReportService.getNodeSummaryList(req.getReportId());
    }

    /**
     * 获取报告告警总数
     *
     * @param req 报告主键
     * @return 告警汇总信息
     */
    @Override
    public Map<String, Object> warnCount(ReportDetailByIdReq req) {
        return cloudReportService.getReportWarnCount(req.getReportId());
    }

    /**
     * 获取正在运行中的报告
     *
     * @param req 报告主键
     * @return 告警汇总信息
     */
    @Override
    public Long listRunning(ContextExt req) {
        return cloudReportService.queryRunningReport(req);
    }

    /**
     * 锁定
     *
     * @param req 报告主键
     * @return 操作结果
     */
    @Override
    public Boolean lock(ReportDetailByIdReq req) {
        return cloudReportService.lockReport(req.getReportId());
    }

    /**
     * 解锁
     *
     * @param req 报告主键
     * @return 操作结果
     */
    @Override
    public Boolean unlock(ReportDetailByIdReq req) {
        return cloudReportService.unLockReport(req.getReportId());
    }

    /**
     * 完成报告
     *
     * @param req 报告主键
     * @return 操作结果
     */
    @Override
    public Boolean finish(ReportDetailByIdReq req) {
        return cloudReportService.finishReport(req.getReportId());
    }

    /**
     * 当前压测的所有数据
     *
     * @param req 请求
     * @return 响应
     */
    @Override
    public List<MetricesResponse> metrics(TrendRequest req) {
        List<Metrices> metrics = cloudReportService.metric(req);
        if (CollectionUtils.isEmpty(metrics)) {
            return new ArrayList<>(0);
        }
        return metrics.stream().map(metric -> BeanUtil.copyProperties(metric,MetricesResponse.class)).collect(Collectors.toList());
    }

    /**
     * 查询脚本节点树
     *
     * @param req 请求参数
     * @return 脚本节点数结果
     */
    @Override
    public List<ScriptNodeTreeResp> queryNodeTree(ScriptNodeTreeQueryReq req) {
        return cloudReportService.getNodeTree(req);
    }

    /**
     * 获取下载jtl下载路径
     *
     * @param req 请求参数
     *            <p>传入reportId即可</p>
     * @return 下载路径
     */
    @Override
    public String getJtlDownLoadUrl(JtlDownloadReq req) {
        return cloudReportService.getJtlDownLoadUrl(req.getReportId(), true);
    }

    @Override
    public Integer getReportStatusById(ReportDetailByIdReq req) {
        return cloudReportService.getReportStatusById(req.getReportId());
    }

	@Override
    public ReportDetailResp getReportByResourceId(String resourceId) {
        ReportDetailOutput output = cloudReportService.getByResourceId(resourceId);
        if (Objects.isNull(output)) {
            return null;
        }
        return BeanUtil.copyProperties(output, ReportDetailResp.class);
    }

    @Override
    public ResponseResult<List<ReportActivityResp>> getActivities(ReportDetailByIdsReq req) {
        List<ReportActivityResp> result = cloudReportService.getActivities(req.getSceneIds());
        return ResponseResult.success(result);
    }
}
