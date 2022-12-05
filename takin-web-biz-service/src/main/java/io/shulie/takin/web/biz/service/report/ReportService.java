package io.shulie.takin.web.biz.service.report;

import java.util.List;
import java.util.Map;

import com.pamirs.takin.entity.domain.dto.report.ReportDTO;
import com.pamirs.takin.entity.domain.vo.report.ReportQueryParam;
import io.shulie.takin.adapter.api.model.ScriptNodeSummaryBean;
import io.shulie.takin.adapter.api.model.request.report.ReportTrendQueryReq;
import io.shulie.takin.adapter.api.model.request.report.TrendRequest;
import io.shulie.takin.adapter.api.model.request.report.WarnQueryReq;
import io.shulie.takin.adapter.api.model.response.report.ActivityResponse;
import io.shulie.takin.adapter.api.model.response.report.MetricesResponse;
import io.shulie.takin.adapter.api.model.response.report.NodeTreeSummaryResp;
import io.shulie.takin.adapter.api.model.response.report.ReportTrendResp;
import io.shulie.takin.adapter.api.model.response.report.ScriptNodeTreeResp;
import io.shulie.takin.adapter.api.model.response.scenemanage.WarnDetailResponse;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.biz.pojo.output.report.ReportDetailOutput;
import io.shulie.takin.web.biz.pojo.output.report.ReportDetailTempOutput;
import io.shulie.takin.web.biz.pojo.output.report.ReportJtlDownloadOutput;
import io.shulie.takin.web.biz.pojo.request.report.ReportQueryRequest;

/**
 * @author qianshui
 * @date 2020/5/12 下午3:33
 */
public interface ReportService {
    /**
     * 查询报告列表
     *
     * @param param 入参
     * @return 报告列表
     */
    ResponseResult<List<ReportDTO>> listReport(ReportQueryParam param);

    /**
     * 根据报告主键查询报告详情
     *
     * @param reportId 报告主键
     * @return 报告详情
     */
    ReportDetailOutput getReportByReportId(Long reportId);

    /**
     * 查询报告趋势
     *
     * @param param 入参
     * @return 报告趋势
     */
    ReportTrendResp queryReportTrend(ReportTrendQueryReq param);

    /**
     * 报告实况详情
     *
     * @param sceneId 场景主键
     * @return 报告实况详情
     */
    ReportDetailTempOutput tempReportDetail(Long sceneId);

    /**
     * 报告实况趋势
     *
     * @param param 入参
     * @return 报告实况趋势
     */
    ReportTrendResp queryTempReportTrend(ReportTrendQueryReq param);

    /**
     * 列出警告
     *
     * @param req 入参
     * @return 警告列表
     */
    ResponseResult<List<WarnDetailResponse>> listWarn(WarnQueryReq req);

    /**
     * 根据报告主键查询业务活动
     *
     * @param reportId 报告主键
     * @return 业务活动列表
     */
    List<ActivityResponse> queryReportActivityByReportId(Long reportId);

    /**
     * 根据场景主键查询业务活动
     *
     * @param sceneId 场景主键
     * @return 业务活动列表
     */
    List<ActivityResponse> queryReportActivityBySceneId(Long sceneId);

    /**
     * 查询报告详情
     *
     * @param reportId 报告主键
     * @return 报告详情
     */
    NodeTreeSummaryResp querySummaryList(Long reportId);

    /**
     * 获取指标列表
     * time
     * avgTps
     * 两个 key
     *
     * @param req 查询条件
     * @return 指标列表
     */
    List<MetricesResponse> queryMetrics(TrendRequest req);

    /**
     * 查询报告的警告数量
     *
     * @param reportId 报告主键
     * @return 警告信息
     */
    Map<String, Object> queryReportCount(Long reportId);

    /**
     * 锁定报告
     *
     * @param reportId 报告主键
     * @return 操作结果
     */
    Boolean lockReport(Long reportId);

    /**
     * 解锁报告
     *
     * @param reportId 报告主键
     * @return 操作结果
     */
    Boolean unLockReport(Long reportId);

    /**
     * 结束报告
     *
     * @param reportId 报告主键
     * @return 操作结果
     */
    Boolean finishReport(Long reportId);

    /**
     * 查询脚本节点树
     *
     * @param request 查询参数
     * @return 脚本节点数
     */
    ResponseResult<List<ScriptNodeTreeResp>> queryNodeTree(ReportQueryRequest request);

    /**
     * 下载jtl路径
     *
     * @param reportId 报告主键
     * @return JTL文件下载路径
     */
    ReportJtlDownloadOutput getJtlDownLoadUrl(Long reportId);


    /**
     * 用于finishjob判断报告的状态
     * 根据报告主键查询报告详情
     * @param id 报告主键
     * @return 报告详情
     */
    ReportDetailOutput getReportById(Long id);

    String downloadPDFPath(Long reportId);

    /**
     * 查询阶梯递增模式下指定线程数的明细
     *
     * @param reportId
     * @param xpathMd5
     * @param threadNum
     * @return
     */
    ScriptNodeSummaryBean queryNode(Long reportId, String xpathMd5, Double threadNum);

}