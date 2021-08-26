package io.shulie.takin.web.biz.service.report.impl;

import java.util.Date;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicBoolean;

import com.alibaba.fastjson.JSON;

import com.pamirs.takin.entity.domain.dto.report.ReportDetailDTO;
import io.shulie.takin.cloud.common.redis.RedisClientUtils;
import io.shulie.takin.cloud.open.req.report.UpdateReportConclusionReq;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.biz.constant.WebRedisKeyConstant;
import io.shulie.takin.web.biz.service.report.ReportService;
import io.shulie.takin.web.biz.service.report.ReportTaskService;
import io.shulie.takin.web.biz.service.risk.ProblemAnalysisService;
import io.shulie.takin.web.common.domain.WebResponse;
import io.shulie.takin.web.common.util.SceneTaskUtils;
import io.shulie.takin.web.data.dao.leakverify.LeakVerifyResultDAO;
import io.shulie.takin.web.diff.api.scenetask.SceneTaskApi;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * 1、查询生成中状态的报告（只取一条）
 * 2、更新报告状态为锁定
 * 3、客户端生成报告内容
 * - 瓶颈接口
 * - 风险机器
 * - 容量水位
 * 4、更新报告状态为已完成
 *
 * 配合压测实况：容量水位
 * 1、压测中生成报告，执行 机器列表 tps汇总图 机器统计
 * 2、压测中时，别忘记解锁
 *
 * @author qianshui
 * @date 2020/7/28 上午10:59
 */
@Service
@Slf4j
public class ReportTaskServiceImpl implements ReportTaskService {


    private static AtomicBoolean RUNNINT = new AtomicBoolean(false);

    @Autowired
    private ReportDataCache reportDataCache;

    @Autowired
    private ReportService reportService;

    @Autowired
    private ProblemAnalysisService problemAnalysisService;

    @Autowired
    private SummaryService summaryService;

    @Autowired
    private ReportClearService reportClearService;

    @Autowired
    private RedisClientUtils redisClientUtils;

    @Autowired
    private LeakVerifyResultDAO leakVerifyResultDAO;

    @Autowired
    private SceneTaskApi sceneTaskApi;

    @Autowired
    @Qualifier("reportThreadPool")
    private ThreadPoolExecutor reportThreadPool;


    @Override
    public void finishReport(Long reportId) {
        try {
            if (RUNNINT.get()) {
                return;
            }
            if (!RUNNINT.compareAndSet(false, true)) {
                return;
            }
            try {
                //Ready 数据准备
                reportDataCache.readyCloudReportData(reportId);
            } catch (Exception e) {
                log.error("finish report data preparation：{}", reportId);
            }
            ReportDetailDTO reportDetailDTO = reportDataCache.getReportDetailDTO(reportId);
            if (reportDetailDTO == null) {
                return;
            }
            // 压测结束才锁报告
            Date endTime = reportDetailDTO.getEndTime();
            if (endTime == null) {
                return;
            }
            // 解除 场景锁
            redisClientUtils.delete(SceneTaskUtils.getSceneTaskKey(reportDetailDTO.getSceneId()));
            try {
                Long startTime = System.currentTimeMillis();
                WebResponse lockResponse = reportService.lockReport(reportId);
                if (!lockResponse.getSuccess() || lockResponse.getData() == null || !((Boolean)lockResponse.getData())) {
                    log.error("Lock Running Report Data Failure, reportId={}...", reportId);
                }
                log.info("finish report，total data  Running Report :{}", reportId);

                // 收集数据 单独线程收集
                reportThreadPool.execute(() -> {
                    try {
                        // 检查风险机器
                        problemAnalysisService.checkRisk(reportId);
                    } catch (Exception e) {
                        log.error("reportId = {}: Check the risk machine ", reportId);
                    }
                    try {
                        // 瓶颈处理
                        problemAnalysisService.processBottleneck(reportId);
                    } catch (Exception e) {
                        log.error("reportId = {}: Bottleneck handling ", reportId);
                    }
                    try {
                        //then 报告汇总接口
                        summaryService.calcReportSummay(reportId);
                    } catch (Exception e) {
                        log.error("reportId = {}: total report ", reportId);
                    }
                });

                // 停止报告
                WebResponse webResponse = reportService.finishReport(reportId);
                if (!webResponse.getSuccess() || !(Boolean)webResponse.getData()) {
                    log.info("压测结束失败 Report :{}，cloud更新失败", reportId);
                }
                //删除redis数据
                redisClientUtils.del(WebRedisKeyConstant.REPORT_WARN_PREFIX + reportId);
                // 删除key
                redisClientUtils.hmdelete(WebRedisKeyConstant.PTING_APPLICATION_KEY, String.valueOf(reportId));

                Boolean isLeaked = leakVerifyResultDAO.querySceneIsLeaked(reportId);
                if (isLeaked) {
                    //存在漏数，压测失败，修改压测报告状态 1：通过 0：不通过
                    log.info("存在漏数，压测失败，修改压测报告状态:[{}]", reportId);
                    UpdateReportConclusionReq conclusionReq = new UpdateReportConclusionReq();
                    conclusionReq.setId(reportId);
                    conclusionReq.setConclusion(0);
                    conclusionReq.setErrorMessage("存在漏数");
                    ResponseResult<String> responseResult = sceneTaskApi.updateReportStatus(conclusionReq);
                    log.info("修改压测报告的结果:[{}]", JSON.toJSONString(responseResult));
                }

                reportDataCache.clearDataCache(reportId);
                log.info("报告id={}汇总成功，花费时间={}", reportId, (System.currentTimeMillis() - startTime));
            } catch (Exception e) {
                log.error("客户端生成报告id={}数据异常:{}", reportId, e.getMessage(), e);
                //生成报告异常，清空本轮生成表数据
                reportClearService.clearReportData(reportId);
                //压测结束，生成压测报告异常，解锁报告
                reportService.unLockReport(reportId);
                log.error("Unlock Report Success, reportId={}...", reportId);
            }
        } catch (Exception e) {
            log.error("QueryRunningReport Error :{}", e.getMessage());
        } finally {
            RUNNINT.compareAndSet(true, false);
        }
    }

    @Override
    public void syncMachineData(Long reportId) {
        try {
            //Ready 数据准备
            reportDataCache.readyCloudReportData(reportId);
            //first 同步应用基础信息
            long startTime = System.currentTimeMillis();
            problemAnalysisService.syncMachineData(reportId);
            log.info("reportId={} syncMachineData success，cost time={}s", reportId, (System.currentTimeMillis() - startTime) / 1000);
        } catch (Exception e) {
            log.error("reportId={} syncMachineData false", reportId);
        }
    }

    @Override
    public void calcTpsTarget(Long reportId) {
        try {
            long startTime = System.currentTimeMillis();
            //Ready 数据准备
            reportDataCache.readyCloudReportData(reportId);
            //then tps指标图
            summaryService.calcTpsTarget(reportId);
            log.info("reportId={} calcTpsTarget success，cost time={}s", reportId, (System.currentTimeMillis() - startTime) / 1000);
        } catch (Exception e) {
            log.error("reportId={} calcTpsTarget false", reportId);
        }
    }

    @Override
    public void calcApplicationSummary(Long reportId) {
        try {
            long startTime = System.currentTimeMillis();
            //Ready 数据准备
            reportDataCache.readyCloudReportData(reportId);
            //汇总应用 机器数 风险机器数
            summaryService.calcApplicationSummary(reportId);
            log.info("reportId={} calcApplicationSummary success，cost time={}s", reportId, (System.currentTimeMillis() - startTime) / 1000);
        } catch (Exception e) {
            log.error("reportId={} calcApplicationSummary false", reportId);
        }
    }
}
