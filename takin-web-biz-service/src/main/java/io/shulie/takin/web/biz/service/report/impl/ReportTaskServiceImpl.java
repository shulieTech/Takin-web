package io.shulie.takin.web.biz.service.report.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.concurrent.ThreadPoolExecutor;

import com.alibaba.fastjson.JSON;

import cn.hutool.core.bean.BeanUtil;
import com.pamirs.takin.entity.domain.dto.report.ReportDetailDTO;
import io.shulie.takin.cloud.common.redis.RedisClientUtils;
import io.shulie.takin.cloud.sdk.model.request.report.UpdateReportConclusionReq;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.biz.constant.WebRedisKeyConstant;
import io.shulie.takin.web.biz.pojo.output.report.ReportDetailOutput;
import io.shulie.takin.web.biz.service.report.ReportService;
import io.shulie.takin.web.biz.service.report.ReportTaskService;
import io.shulie.takin.web.biz.service.risk.ProblemAnalysisService;
import io.shulie.takin.web.common.common.Separator;
import io.shulie.takin.web.common.pojo.dto.SceneTaskDto;
import io.shulie.takin.web.common.util.CommonUtil;
import io.shulie.takin.web.common.util.SceneTaskUtils;
import io.shulie.takin.web.data.dao.leakverify.LeakVerifyResultDAO;
import io.shulie.takin.web.diff.api.scenetask.SceneTaskApi;
import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
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
    @Qualifier("collectDataThreadPool")
    private ThreadPoolExecutor collectDataThreadPool;

    @Autowired
    @Qualifier("redisTemplate")
    private RedisTemplate redisTemplate;

    @Override
    public Boolean finishReport(Long reportId, TenantCommonExt commonExt) {
        try {
            try {
                //Ready 数据准备
                reportDataCache.readyCloudReportData(reportId);
            } catch (Exception e) {
                log.error("finish report data preparation：{},errorMsg= {}", reportId, e.getMessage());
            }
            // 查询报告状态
            final ReportDetailOutput report = reportService.getReportById(reportId);
            if (report == null) {
                return false;
            }
            // 压测结束才锁报告
            Integer status = report.getTaskStatus();
            if (status == null || status != 1) {
                return false;
            }
            ReportDetailDTO reportDetailDTO = reportDataCache.getReportDetailDTO(reportId);
            if (reportDetailDTO == null) {
                log.error("未查到报告明细！reportId={}",reportId);
                return false;
            }
            Date endTime = reportDetailDTO.getEndTime();
            //更新任务的结束时间
            if (!this.updateTaskEndTime(reportId, commonExt, endTime)) { return false; }

            // 解除 场景锁
            redisClientUtils.delete(SceneTaskUtils.getSceneTaskKey(reportDetailDTO.getSceneId()));
            try {
                // 前置删除
                //删除redis数据
                redisClientUtils.del(WebRedisKeyConstant.REPORT_WARN_PREFIX + reportId);
                // 删除key
                String redisKey = CommonUtil.generateRedisKeyWithSeparator(Separator.Separator3,
                    WebPluginUtils.traceTenantAppKey(), WebPluginUtils.traceEnvCode(),
                    String.format(WebRedisKeyConstant.PTING_APPLICATION_KEY, reportId));
                redisClientUtils.del(redisKey);
                long startTime = System.currentTimeMillis();
                Boolean lockResponse = reportService.lockReport(reportId);
                if (!lockResponse) {
                    log.error("锁定运行报告数据失败, reportId={}", reportId);
                }
                log.info("finish report，total data  Running Report :{}", reportId);

                // 收集数据 单独线程收集
                collectDataThreadPool.execute(collectData(reportId,commonExt));

                // 停止报告
                Boolean webResponse = reportService.finishReport(reportId);
                if (!webResponse) {
                    log.info("压测结束失败 Report :{}，cloud更新失败", reportId);
                }

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
                // log.error("客户端生成报告id={}数据异常:{}", reportId, e.getMessage(), e);
                //生成报告异常，清空本轮生成表数据
                reportClearService.clearReportData(reportId);
                //压测结束，生成压测报告异常，解锁报告
                Boolean unLockReportResult = reportService.unLockReport(reportId);
                log.error("Unlock Report Success, reportId={} ,unLockReportResult= {}...", reportId, unLockReportResult,
                    e);
            } finally {
                removeReportKey(reportId, commonExt);
            }

        } catch (Exception e) {
            log.error("QueryRunningReport Error :{}", e.getMessage());
        }
        return true;
    }

    private boolean updateTaskEndTime(Long reportId, TenantCommonExt commonExt, Date endTime) {
        LocalDateTime endTimeLocal = null;
        try {
            endTimeLocal = endTime.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        } catch (Exception e) {
            log.error("endTime日期转化有误，endTime={}", JSON.toJSONString(endTime), e);
            return false;
        }
        SceneTaskDto task = new SceneTaskDto(commonExt, reportId);
        task.setEndTime(endTimeLocal);
        String reportKey = null;
        try {
            reportKey = WebRedisKeyConstant.getReportKey(reportId);
            redisTemplate.opsForValue().set(reportKey, JSON.toJSONString(task));
        } catch (Exception e) {
            log.error("更新redis任务有误，key={}", reportKey, e);
            return false;
        }
        return true;
    }

    private void removeReportKey(Long reportId, TenantCommonExt commonExt) {
        final String reportKey = WebRedisKeyConstant.getReportKey(reportId);
        redisTemplate.opsForList().remove(WebRedisKeyConstant.SCENE_REPORTID_KEY, 0, reportKey);
        redisTemplate.opsForValue().getOperations().delete(reportKey);
    }

    /**
     * 收集报告的数据
     *
     * @param reportId 报告 id
     * @return 可运行
     */
    private synchronized Runnable collectData(Long reportId, TenantCommonExt commonExt) {
        return () -> {
            WebPluginUtils.setTraceTenantContext(commonExt);
            try {
                // 检查风险机器
                problemAnalysisService.checkRisk(reportId);
            } catch (Exception e) {
                log.error("reportId = {}: Check the risk machine,errorMsg= {} ", reportId, e.getMessage());
            }
            try {
                // 瓶颈处理
                problemAnalysisService.processBottleneck(reportId);
            } catch (Exception e) {
                log.error("reportId = {}: Bottleneck handling,errorMsg= {} ", reportId, e.getMessage());
            }
            try {
                //then 报告汇总接口
                summaryService.calcReportSummay(reportId);
            } catch (Exception e) {
                log.error("reportId = {}: total report ,errorMsg= {}", reportId, e.getMessage());
            }
        };
    }

    @Override
    public void syncMachineData(Long reportId) {
        //Ready 数据准备
        reportDataCache.readyCloudReportData(reportId);
        //first 同步应用基础信息
        long startTime = System.currentTimeMillis();
        problemAnalysisService.syncMachineData(reportId);
        log.debug("reportId={} syncMachineData success，cost time={}s", reportId,
            (System.currentTimeMillis() - startTime) / 1000);
    }

    @Override
    public void calcTpsTarget(Long reportId) {
        long startTime = System.currentTimeMillis();
        //Ready 数据准备
        reportDataCache.readyCloudReportData(reportId);
        //then tps指标图
        summaryService.calcTpsTarget(reportId);
        log.debug("reportId={} calcTpsTarget success，cost time={}s", reportId,
            (System.currentTimeMillis() - startTime) / 1000);
    }

    @Override
    public void calcApplicationSummary(Long reportId) {
        long startTime = System.currentTimeMillis();
        //Ready 数据准备
        reportDataCache.readyCloudReportData(reportId);
        //汇总应用 机器数 风险机器数
        summaryService.calcApplicationSummary(reportId);
        log.debug("reportId={} calcApplicationSummary success，cost time={}s", reportId,
            (System.currentTimeMillis() - startTime) / 1000);
    }
}
