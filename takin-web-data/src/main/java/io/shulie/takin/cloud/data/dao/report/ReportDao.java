package io.shulie.takin.cloud.data.dao.report;

import java.util.Date;
import java.util.List;

import io.shulie.takin.cloud.data.model.mysql.ReportBusinessActivityDetailEntity;
import io.shulie.takin.cloud.data.model.mysql.ReportEntity;
import io.shulie.takin.cloud.data.param.report.ReportInsertParam;
import io.shulie.takin.cloud.data.param.report.ReportQueryParam;
import io.shulie.takin.cloud.data.param.report.ReportUpdateConclusionParam;
import io.shulie.takin.cloud.data.param.report.ReportUpdateParam;
import io.shulie.takin.cloud.data.result.report.ReportResult;
import io.shulie.takin.cloud.ext.content.enums.NodeTypeEnum;

/**
 * @author 无涯
 * @date 2020/12/17 3:30 下午
 */
public interface ReportDao {

    int insert(ReportInsertParam param);

    /**
     * 获取列表
     *
     * @param param -
     * @return -
     */
    List<ReportResult> queryCalcPushWindowsReportList(ReportQueryParam param);

    /**
     * 获取报告
     *
     * @param id 报告主键
     * @return -
     */
    ReportResult selectById(Long id);

    List<ReportResult> selectBySceneId(Long sceneId);

    /**
     * 获取当前场景最新一条报告
     *
     * @param sceneId 场景主键
     * @return -
     */
    ReportResult getRecentlyReport(Long sceneId);

    /**
     * 更新通过是否通过
     *
     * @param param 入参
     */
    void updateReportConclusion(ReportUpdateConclusionParam param);

    /**
     * 更新报告
     *
     * @param param 参数
     */
    void updateReport(ReportUpdateParam param);

    /**
     * 完成报告
     *
     * @param reportId 报告主键
     */
    void finishReport(Long reportId);

    /**
     * 锁报告
     *
     * @param resultId -
     * @param lock     -
     */
    void updateReportLock(Long resultId, Integer lock);

    /**
     * 根据场景ID获取（临时）压测中的报告ID
     *
     * @param sceneId 场景主键
     * @return -
     */
    ReportResult getTempReportBySceneId(Long sceneId);

    /**
     * 根据场景ID获取压测中的报告ID
     *
     * @param sceneId 场景主键
     * @return -
     */
    ReportResult getReportBySceneId(Long sceneId);

    /**
     * 根据场景主键设置压测报告状态
     *
     * @param sceneId 场景主键
     * @param status  状态值
     * @return 操作影响行数
     */
    int updateStatus(Long sceneId, Integer status);

    /**
     * 更新报告开始时间
     * @param reportId 报告主键
     * @param startTime 开始时间
     */
    void updateReportStartTime(Long reportId,Date startTime);

    /**
     * 更新报告结束时间
     *
     * @param resultId 报告主键
     * @param endTime  结束时间
     */
    void updateReportEndTime(Long resultId, Date endTime);

    void updateReportStopTime(Long jobId, Date stopTime);

    /**
     * 根据id查询报告
     *
     * @param resultId 报告主键
     * @return 报告详情
     */
    ReportResult getById(Long resultId);

    ReportResult getByResourceId(Long resourceId);

    void deleteReport(Long reportId);

    /**
     * 查询报告关联的节点信息
     *
     * @param reportId 报告ID
     * @param nodeType 节点类型
     * @return 节点信息集合
     */
    List<ReportBusinessActivityDetailEntity> getReportBusinessActivityDetailsByReportId(Long reportId, NodeTypeEnum nodeType);

    /**
     * 获取报告
     *
     * @param jobId 任务Id
     * @return -
     */
    ReportResult selectByJobId(Long jobId);

    ReportResult selectByResourceId(String resourceId);

    List<ReportEntity> queryReportBySceneIds(List<Long> sceneIds);

    List<ReportBusinessActivityDetailEntity> getActivityByReportIds(List<Long> reportIds) ;

    /**
     * 根据场景Id,xpathMd5获取报告业务活动详情
     * @param sceneId 场景id
     * @param xpathMd5 脚本md5路径
     * @param reportId 报告id
     * @return ReportBusinessActivityDetailEntity
     */
    ReportBusinessActivityDetailEntity getReportBusinessActivityDetail(Long sceneId,String xpathMd5,Long reportId);
}
