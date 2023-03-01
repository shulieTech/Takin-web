package io.shulie.takin.cloud.biz.service.report;

import java.util.List;
import java.util.Map;

import com.github.pagehelper.PageInfo;
import com.pamirs.takin.cloud.entity.domain.dto.report.BusinessActivityDTO;
import com.pamirs.takin.cloud.entity.domain.dto.report.CloudReportDTO;
import com.pamirs.takin.cloud.entity.domain.dto.report.Metrices;
import com.pamirs.takin.cloud.entity.domain.dto.report.StatReportDTO;
import io.shulie.takin.adapter.api.model.request.report.TrendRequest;
import io.shulie.takin.adapter.api.model.request.scenemanage.ReportActivityResp;
import io.shulie.takin.cloud.biz.input.report.UpdateReportConclusionInput;
import io.shulie.takin.cloud.biz.input.report.UpdateReportSlaDataInput;
import io.shulie.takin.cloud.biz.input.report.WarnCreateInput;
import io.shulie.takin.cloud.biz.output.report.ReportDetailOutput;
import io.shulie.takin.cloud.biz.output.report.ReportOutput;
import io.shulie.takin.cloud.biz.output.scene.manage.WarnDetailOutput;
import io.shulie.takin.cloud.data.model.mysql.ReportEntity;
import io.shulie.takin.cloud.data.param.report.ReportUpdateParam;
import io.shulie.takin.cloud.data.result.report.ReportResult;
import io.shulie.takin.cloud.ext.content.trace.ContextExt;
import io.shulie.takin.adapter.api.model.request.WarnQueryParam;
import io.shulie.takin.adapter.api.model.request.report.ReportQueryReq;
import io.shulie.takin.adapter.api.model.request.report.ReportTrendQueryReq;
import io.shulie.takin.adapter.api.model.request.report.ScriptNodeTreeQueryReq;
import io.shulie.takin.adapter.api.model.response.report.NodeTreeSummaryResp;
import io.shulie.takin.adapter.api.model.response.report.ReportTrendResp;
import io.shulie.takin.adapter.api.model.response.report.ScriptNodeTreeResp;
import io.shulie.takin.web.biz.pojo.dto.scene.EnginePressureQuery;

/**
 * @author 数列科技
 */
public interface CloudReportService {

    /**
     * 报告列表
     *
     * @param param -
     * @return -
     */
    PageInfo<CloudReportDTO> listReport(ReportQueryReq param);

    /**
     * 报告详情
     *
     * @param reportId 报告主键
     * @return -
     */
    ReportDetailOutput getReportByReportId(Long reportId);

    /**
     * 报告链路趋势
     *
     * @param reportTrendQuery -
     * @return -
     */
    ReportTrendResp queryReportTrend(ReportTrendQueryReq reportTrendQuery);

    /**
     * 实况报表
     *
     * @param sceneId 场景主键
     * @return -
     */
    ReportDetailOutput tempReportDetail(Long sceneId);

    /**
     * 实况链路趋势
     *
     * @param reportTrendQuery -
     * @return -
     */
    ReportTrendResp queryTempReportTrend(ReportTrendQueryReq reportTrendQuery);

    /**
     * 警告列表
     *
     * @param param -
     * @return -
     */
    PageInfo<WarnDetailOutput> listWarn(WarnQueryParam param);

    /**
     * 查询报告中的业务活动
     *
     * @param reportId 报告主键
     * @return -
     */
    List<BusinessActivityDTO> queryReportActivityByReportId(Long reportId);

    /**
     * 查询报告中的业务活动
     *
     * @param sceneId 场景主键
     * @return -
     */
    List<BusinessActivityDTO> queryReportActivityBySceneId(Long sceneId);

    /**
     * 获取业务活动摘要列表
     *
     * @param reportId 报告主键
     * @return -
     */
    NodeTreeSummaryResp getNodeSummaryList(Long reportId);

    /**
     * 获取报告的业务活动数量和压测通过数量
     *
     * @param reportId 报告主键
     * @return -
     */
    Map<String, Object> getReportWarnCount(Long reportId);

    /**
     * 查询正在生成的报告
     *
     * @param contextExt 溯源数据
     * @return -
     */
    Long queryRunningReport(ContextExt contextExt);

    /**
     * 根据租户 获取运行中的报告列表
     *
     * @return 报告主键列表
     */
    List<Long> queryListRunningReport();

    /**
     * 获取压测中的报告主键列表
     *
     * @return -
     */
    List<Long> queryListPressuringReport();

    /**
     * 锁定报告
     *
     * @param reportId 报告主键
     * @return -
     */
    Boolean lockReport(Long reportId);

    /**
     * 解锁报告
     *
     * @param reportId 报告主键
     * @return -
     */
    Boolean unLockReport(Long reportId);

    /**
     * 客户端调，报告完成
     *
     * @param reportId 报告主键
     * @return -
     */
    Boolean finishReport(Long reportId);

    /**
     * 强制关闭报告
     *
     * @param reportId 报告主键
     */
    void forceFinishReport(Long reportId);

    /**
     * 新增 tenantId
     *
     * @param req 查询条件
     * @return -
     */
    List<Metrices> metric(TrendRequest req);

    /**
     * 更新扩展字段
     *
     * @param reportId 报告主键
     * @param status   状态
     * @param errKey   错误key
     * @param errMsg   错误message
     */
    void updateReportFeatures(Long reportId, Integer status, String errKey, String errMsg);

    /**
     * 创建告警
     *
     * @param input -
     */
    void addWarn(WarnCreateInput input);

    /**
     * 更新报告是否通过
     *
     * @param input -
     */
    void updateReportConclusion(UpdateReportConclusionInput input);

    /**
     * 更新sla数据
     *
     * @param input -
     */
    void updateReportSlaData(UpdateReportSlaDataInput input);

    /**
     * 获取报告
     *
     * @param id 报告主键
     * @return -
     */
    ReportOutput selectById(Long id);

    /**
     * 更新场景启动失败的报告的状态
     *
     * @param sceneId  场景ID
     * @param reportId 报告ID
     * @param errorMsg 异常信息
     */
    void updateReportOnSceneStartFailed(Long sceneId, Long reportId, String errorMsg);

    /**
     * 查询报告对应的脚本树结构
     *
     * @param req 查询参数
     * @return 树结构json字符串
     */
    List<ScriptNodeTreeResp> getNodeTree(ScriptNodeTreeQueryReq req);

    /**
     * 获取报告的基础数据(基础表数据)
     *
     * @param reportId 报告主键
     * @return 基础表数据
     */
    ReportResult getReportBaseInfo(Long reportId);

    /**
     * 下载jtl路径
     *
     * @param reportId 报告主键
     * @param needZip  是否需要打包
     *                 <p>false代表只进行逻辑校验而不执行打包操作</p>
     * @return jtl文件的下载路径
     */
    String getJtlDownLoadUrl(Long reportId, boolean needZip);

    /**
     * 根据报告id获取报告详情，之前的报告给页面使用，所以状态不是真实状态
     *
     * @param reportId -
     * @return -
     */
    Integer getReportStatusById(Long reportId);

    void updateReportById(ReportUpdateParam report);

    StatReportDTO statReport(Long jobId, Long sceneId, Long reportId, Long customerId, String transaction);

    boolean updateReportBusinessActivity(Long jobId, Long sceneId, Long reportId, Long tenantId);

    List<ReportActivityResp> getActivities(List<Long> sceneIds);

	ReportDetailOutput getByResourceId(String resourceId);

    <T> List<T> listEnginePressure(EnginePressureQuery query, Class<T> tClass);
}
