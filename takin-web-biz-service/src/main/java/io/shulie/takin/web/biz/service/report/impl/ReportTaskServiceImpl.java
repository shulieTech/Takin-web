package io.shulie.takin.web.biz.service.report.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.collect.Lists;
import com.pamirs.takin.cloud.entity.dao.scene.manage.TSceneBusinessActivityRefMapper;
import com.pamirs.takin.common.util.DateUtils;
import com.pamirs.takin.entity.domain.dto.report.ReportDetailDTO;
import com.pamirs.takin.entity.domain.entity.report.TpsTarget;
import com.pamirs.takin.entity.domain.entity.report.TpsTargetArray;
import com.pamirs.takin.entity.domain.risk.BaseAppVo;
import com.pamirs.takin.entity.domain.risk.Metrices;
import io.shulie.takin.adapter.api.model.request.report.UpdateReportConclusionReq;
import io.shulie.takin.cloud.data.model.mysql.ReportEntity;
import io.shulie.takin.cloud.data.model.mysql.SceneBusinessActivityRefEntity;
import io.shulie.takin.web.biz.pojo.request.activity.ActivityInfoQueryRequest;
import io.shulie.takin.web.biz.pojo.request.report.ReportLinkDiagramReq;
import io.shulie.takin.web.biz.service.report.ReportLocalService;
import io.shulie.takin.web.biz.service.risk.util.DateUtil;
import io.shulie.takin.web.biz.threadpool.ThreadPoolUtil;
import io.shulie.takin.web.biz.utils.VolumnUtil;
import io.shulie.takin.web.common.common.Response;
import io.shulie.takin.web.common.enums.activity.info.FlowTypeEnum;
import io.shulie.takin.web.common.enums.config.ConfigServerKeyEnum;
import io.shulie.takin.web.common.util.RedisClientUtil;
import io.shulie.takin.common.beans.response.ResponseResult;
import io.shulie.takin.web.biz.constant.WebRedisKeyConstant;
import io.shulie.takin.web.biz.pojo.output.report.ReportDetailOutput;
import io.shulie.takin.web.biz.service.DistributedLock;
import io.shulie.takin.web.biz.service.report.ReportService;
import io.shulie.takin.web.biz.service.report.ReportTaskService;
import io.shulie.takin.web.biz.service.risk.ProblemAnalysisService;
import io.shulie.takin.web.biz.utils.job.JobRedisUtils;
import io.shulie.takin.web.common.common.Separator;
import io.shulie.takin.web.common.pojo.dto.SceneTaskDto;
import io.shulie.takin.web.common.util.CommonUtil;
import io.shulie.takin.web.common.util.SceneTaskUtils;
import io.shulie.takin.web.data.dao.application.ApplicationNodeDAO;
import io.shulie.takin.web.data.dao.baseserver.BaseServerDao;
import io.shulie.takin.web.data.dao.leakverify.LeakVerifyResultDAO;
import io.shulie.takin.web.data.dao.report.ReportMachineDAO;
import io.shulie.takin.web.data.mapper.mysql.ApplicationMntMapper;
import io.shulie.takin.web.data.model.mysql.ApplicationMntEntity;
import io.shulie.takin.web.data.param.application.ApplicationNodeQueryParam;
import io.shulie.takin.web.data.param.baseserver.AppBaseDataQuery;
import io.shulie.takin.web.data.param.baseserver.BaseServerParam;
import io.shulie.takin.web.data.param.report.ReportMachineUpdateParam;
import io.shulie.takin.web.data.result.baseserver.BaseServerResult;
import io.shulie.takin.web.data.util.ConfigServerHelper;
import io.shulie.takin.web.diff.api.scenetask.SceneTaskApi;
import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 1、查询生成中状态的报告（只取一条）
 * 2、更新报告状态为锁定
 * 3、客户端生成报告内容
 * - 瓶颈接口
 * - 风险机器
 * - 容量水位
 * 4、更新报告状态为已完成
 * <p>
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
    private RedisClientUtil redisClientUtil;

    @Autowired
    private LeakVerifyResultDAO leakVerifyResultDAO;

    @Autowired
    private SceneTaskApi sceneTaskApi;

    @Autowired
    @Qualifier("redisTemplate")
    private RedisTemplate redisTemplate;

    @Autowired
    private DistributedLock distributedLock;

    @Resource
    private TSceneBusinessActivityRefMapper tSceneBusinessActivityRefMapper;
    @Resource
    private ApplicationMntMapper applicationMntMapper;

    @Resource
    private ApplicationNodeDAO applicationNodeDAO;
    @Resource
    private BaseServerDao baseServerDao;
    @Resource
    private ReportMachineDAO reportMachineDAO;

    @Autowired
    private ReportLocalService reportLocalService;


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
                log.warn("未获取到报告信息,reportId=" + reportId);
                return false;
            }
            // 加锁
            // 分布式锁
            String lockKey = JobRedisUtils.getRedisJobReport(WebPluginUtils.traceTenantId(), WebPluginUtils.traceEnvCode(), reportId);
            if (!distributedLock.checkLock(lockKey)) {
                try {
                    // 收集数据 单独线程收集
                    ThreadPoolUtil.getCollectDataThreadPool().execute(() -> collectData(reportId, commonExt, lockKey));
                } catch (Throwable e) {
                    // TODO 如果线程池满了，继续走下面的逻辑,否则任务有问题
                    log.error("提交线程池任务异常," + ExceptionUtils.getStackTrace(e));
                }
            }
            // 压测结束才锁报告
            Integer status = report.getTaskStatus();
            if (status == null || status != 1) {
                return false;
            }
            ReportDetailDTO reportDetailDTO = reportDataCache.getReportDetailDTO(reportId);
            if (reportDetailDTO == null) {
                log.error("未查到报告明细！reportId={}", reportId);
                return false;
            }
            Date endTime = reportDetailDTO.getEndTime();
            //更新任务的结束时间
            if (!this.updateTaskEndTime(reportId, commonExt, endTime)) {
                return false;
            }

            // 解除 场景锁
            redisClientUtil.delete(SceneTaskUtils.getSceneTaskKey(reportDetailDTO.getSceneId()));
            try {
                // 前置删除
                //删除redis数据
                redisClientUtil.del(WebRedisKeyConstant.REPORT_WARN_PREFIX + reportId);
                // 删除key
                String redisKey = CommonUtil.generateRedisKeyWithSeparator(Separator.Separator3,
                        WebPluginUtils.traceTenantAppKey(), WebPluginUtils.traceEnvCode(),
                        String.format(WebRedisKeyConstant.PTING_APPLICATION_KEY, reportId));
                redisClientUtil.del(redisKey);
                long startTime = System.currentTimeMillis();
                Boolean lockResponse = reportService.lockReport(reportId);
                if (!lockResponse) {
                    log.error("锁定运行报告数据失败, reportId={}", reportId);
                }
                log.info("finish report，total data  Running Report :{}", reportId);

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
                try {
                    //缓存新版压测报告对比数据
                    reportLocalService.cacheLTReportData2Redis(reportId);
                } catch (Throwable e) {
                    log.error("生成压测报告{}时，计算报告对比数据异常={}", reportId, e);
                }
                reportDataCache.clearDataCache(reportId);
                log.info("报告id={}汇总成功，花费时间={}", reportId, (System.currentTimeMillis() - startTime));
            } catch (Throwable e) {
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
        if (endTime == null) {
            return false;
        }
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
    private void collectData(Long reportId, TenantCommonExt commonExt, String lockKey) {
        // 有锁,证明任务在处理,不需要等太久,后续会有任务继续处理
        boolean tryLock = distributedLock.tryLock(lockKey, 5L, 60L, TimeUnit.SECONDS);
        try {
            if (!tryLock) {
                return;
            }
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
        } catch (Throwable e) {
            log.error("collectData is fail " + ExceptionUtils.getStackTrace(e));
        } finally {
            distributedLock.unLockSafely(lockKey);
        }

    }

    /**
     * 汇总实况数据，包括应用基础信息、tps指标图、应用机器数和风险机器
     *
     * @param reportId
     */
    @Override
    public void calcTmpReportData(Long reportId) {
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        //Ready 数据准备
        reportDataCache.readyCloudReportData(reportId);
        //first 同步应用基础信息
        executorService.execute(() -> {
            problemAnalysisService.syncMachineData(reportId, null);
        });
        //then tps指标图
        executorService.execute(() -> {
            summaryService.calcTpsTarget(reportId, null);
        });
        //end汇总应用 机器数 风险机器数
        executorService.execute(() -> {
            summaryService.calcApplicationSummary(reportId);
        });
    }

    @Override
    public List<Long> nearlyHourReportIds(int minutes) {
        return reportService.nearlyHourReportIds(minutes);
    }

    @Override
    public void calcMachineDate(Long reportId) {
        List<ReportEntity> reportEntities = reportService.getReportListByReportIds(Lists.newArrayList(reportId));
        if (CollectionUtils.isEmpty(reportEntities)) {
            return;
        }
        ReportEntity reportEntity = reportEntities.get(0);

        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Long endTime = reportEntity.getEndTime().getTime();
        //first 同步应用基础信息
        executorService.execute(() -> {
            problemAnalysisService.syncMachineData(reportId, endTime);
        });
        //then tps指标图
        executorService.execute(() -> {
            summaryService.calcTpsTarget(reportId, endTime);
        });

        //重建链路图信息
        ReportLinkDiagramReq reportLinkDiagramReq = new ReportLinkDiagramReq();
        reportLinkDiagramReq.setReportId(reportId);
        ZoneId zoneId = ZoneId.systemDefault();
        reportLinkDiagramReq.setStartTime(LocalDateTime.ofInstant(reportEntity.getStartTime().toInstant(), zoneId));
        reportLinkDiagramReq.setEndTime(LocalDateTime.ofInstant(reportEntity.getEndTime().toInstant(), zoneId));
        reportLinkDiagramReq.setSceneId(reportEntity.getSceneId());

        List<SceneBusinessActivityRefEntity> sceneBusinessActivityRefEntities = tSceneBusinessActivityRefMapper.selectList(new LambdaQueryWrapper<SceneBusinessActivityRefEntity>()
                .select(SceneBusinessActivityRefEntity::getBindRef)
                .eq(SceneBusinessActivityRefEntity::getSceneId, reportEntity.getSceneId()));
        if (CollectionUtils.isEmpty(sceneBusinessActivityRefEntities)) {
            return;
        }
        List<String> bindRefList = sceneBusinessActivityRefEntities.stream().filter(a -> StringUtils.isNotBlank(a.getBindRef())).map(SceneBusinessActivityRefEntity::getBindRef).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(bindRefList)) {
            return;
        }
        executorService.execute(() -> {
            reportService.modifyLinkDiagrams(reportLinkDiagramReq, bindRefList);
        });

    }
}
