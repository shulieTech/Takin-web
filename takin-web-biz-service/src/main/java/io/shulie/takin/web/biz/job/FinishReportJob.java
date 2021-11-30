package io.shulie.takin.web.biz.job;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import io.shulie.takin.job.annotation.ElasticSchedulerJob;
import io.shulie.takin.utils.json.JsonHelper;
import io.shulie.takin.web.biz.service.DistributedLock;
import io.shulie.takin.web.biz.service.report.ReportTaskService;
import io.shulie.takin.web.biz.utils.job.JobRedisUtils;
import io.shulie.takin.web.common.enums.ContextSourceEnum;
import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
import io.shulie.takin.web.ext.entity.tenant.TenantInfoExt;
import io.shulie.takin.web.ext.entity.tenant.TenantInfoExt.TenantEnv;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * @author 无涯
 * @date 2021/6/15 6:08 下午
 */
@Component
@ElasticSchedulerJob(jobName = "finishReportJob",
    // 分片序列号和参数用等号分隔 不需要参数可以不加
    //shardingItemParameters = "0=0,1=1,2=2",
    isSharding = true,
    cron = "*/10 * * * * ?",
    description = "压测报告状态，汇总报告")
@Slf4j
public class FinishReportJob implements SimpleJob {
    @Autowired
    private ReportTaskService reportTaskService;



    @Autowired
    @Qualifier("jobThreadPool")
    private ThreadPoolExecutor finishReportJobThreadPool;

    @Autowired
    @Qualifier("fastDebugThreadPool")
    private ThreadPoolExecutor fastDebugThreadPool;

    @Autowired
    private DistributedLock distributedLock;

    @Override
    public void execute(ShardingContext shardingContext) {

        long start = System.currentTimeMillis();
        if(WebPluginUtils.isOpenVersion()) {
            // 私有化 + 开源 根据 报告id进行分片
            List<Long> reportIds =  reportTaskService.getRunningReport();
            log.debug("获取正在压测中的报告:{}", JsonHelper.bean2Json(reportIds));
            for (Long reportId : reportIds) {
                // 开始数据层分片
                if (reportId % shardingContext.getShardingTotalCount() == shardingContext.getShardingItem()) {
                    fastDebugThreadPool.execute(() -> reportTaskService.finishReport(reportId,WebPluginUtils.setTraceTenantContext(WebPluginUtils.traceTenantId(),WebPluginUtils.traceTenantAppKey(),WebPluginUtils.traceEnvCode(),
                        WebPluginUtils.traceTenantCode(), ContextSourceEnum.JOB.getCode())));
                }
            }
        }else {
            List<TenantInfoExt> tenantInfoExts = WebPluginUtils.getTenantInfoList();
            // saas 根据租户进行分片
            for (TenantInfoExt ext : tenantInfoExts) {
                // 开始数据层分片
                if (ext.getTenantId() % shardingContext.getShardingTotalCount() == shardingContext.getShardingItem()) {
                    // 根据环境 分线程
                    //for (TenantEnv e : ext.getEnvs()) {
                    //    // 分布式锁
                    //    String lockKey = JobRedisUtils.getJobRedis(ext.getTenantId(),e.getEnvCode(),shardingContext.getJobName());
                    //    if (distributedLock.checkLock(lockKey)) {
                    //        continue;
                    //    }
                    //    final TenantCommonExt commonExt = WebPluginUtils.setTraceTenantContext(
                    //        ext.getTenantId(), ext.getTenantAppKey(), e.getEnvCode(), ext.getTenantCode(),
                    //        ContextSourceEnum.JOB.getCode());
                    //    finishReportJobThreadPool.execute(() -> {
                    //        boolean tryLock = distributedLock.tryLock(lockKey, 1L, 1L, TimeUnit.MINUTES);
                    //        if(!tryLock) {
                    //            return;
                    //        }
                    //        try {
                    //            this.finishReport(commonExt);
                    //        } finally {
                    //            distributedLock.unLockSafely(lockKey);
                    //        }
                    //    });
                    //}

                    for (TenantEnv e : ext.getEnvs()) {
                        final TenantCommonExt commonExt = WebPluginUtils.setTraceTenantContext(
                            ext.getTenantId(), ext.getTenantAppKey(), e.getEnvCode(), ext.getTenantCode(),
                            ContextSourceEnum.JOB.getCode());
                        this.finishReport(commonExt);
                    }

                }
            }
        }
        log.debug("finishReport 执行时间:{}", System.currentTimeMillis() - start);
    }

    private void finishReport(TenantCommonExt commonExt) {
        WebPluginUtils.setTraceTenantContext(commonExt);
        List<Long> reportIds = reportTaskService.getRunningReport();
        if (CollectionUtils.isEmpty(reportIds)){
            log.debug("暂无压测中的报告！");
            return;
        }
        log.debug("获取租户【{}】【{}】正在压测中的报告:{}", commonExt.getTenantId(),
            commonExt.getEnvCode(), JsonHelper.bean2Json(reportIds));
        for (Long reportId : reportIds) {
            // 分布式锁
            String lockKey = JobRedisUtils.getJobRedis(commonExt.getTenantId(),commonExt.getEnvCode(),"finishReport#"+reportId);
            if (distributedLock.checkLock(lockKey)) {
                continue;
            }

            boolean tryLock = distributedLock.tryLock(lockKey, 1L, 1L, TimeUnit.MINUTES);
            if(!tryLock) {
                return;
            }
            try {
                // 开始数据分片
                fastDebugThreadPool.execute(() -> {
                    WebPluginUtils.setTraceTenantContext(commonExt);
                    reportTaskService.finishReport(reportId,commonExt);
                });
            } finally {
                distributedLock.unLockSafely(lockKey);
            }
        }

    }
}
