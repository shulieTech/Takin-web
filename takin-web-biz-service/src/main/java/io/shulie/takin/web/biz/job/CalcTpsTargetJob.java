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
import io.shulie.takin.web.common.enums.config.ConfigServerKeyEnum;
import io.shulie.takin.web.data.util.ConfigServerHelper;
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
 * @date 2021/7/13 23:10
 */
@Component
@ElasticSchedulerJob(jobName = "calcTpsTargetJob",
    // 分片序列号和参数用等号分隔 不需要参数可以不加
    //shardingItemParameters = "0=0,1=1,2=2",
    isSharding = true,
    cron = "*/10 * * * * ?",
    description = "获取tps指标图")
@Slf4j
public class CalcTpsTargetJob implements SimpleJob {

    @Autowired
    private ReportTaskService reportTaskService;

    @Autowired
    @Qualifier("reportTpsThreadPool")
    private ThreadPoolExecutor reportThreadPool;

    @Autowired
    private DistributedLock distributedLock;

    @Override
    public void execute(ShardingContext shardingContext) {
        long start = System.currentTimeMillis();
        if (WebPluginUtils.isOpenVersion()) {
            if (!ConfigServerHelper.getBooleanValueByKey(ConfigServerKeyEnum.TAKIN_REPORT_OPEN_TASK)) {
                return;
            }

            // 私有化 + 开源 根据 报告id进行分片
            List<Long> reportIds = reportTaskService.getRunningReport();
            log.debug("获取正在压测中的报告:{}", JsonHelper.bean2Json(reportIds));
            for (Long reportId : reportIds) {
                // 开始数据层分片
                if (reportId % shardingContext.getShardingTotalCount() == shardingContext.getShardingItem()) {
                    reportThreadPool.execute(() -> reportTaskService.calcTpsTarget(reportId));
                }
            }
        } else {
            List<TenantInfoExt> tenantInfoExts = WebPluginUtils.getTenantInfoList();
            // saas 根据租户进行分片
            for (TenantInfoExt ext : tenantInfoExts) {
                // 开始数据层分片
                if (ext.getTenantId() % shardingContext.getShardingTotalCount() == shardingContext.getShardingItem()) {
                    for (TenantEnv e : ext.getEnvs()) {
                        TenantCommonExt tenantCommonExt = WebPluginUtils.setTraceTenantContext(ext.getTenantId(), ext.getTenantAppKey(),
                            e.getEnvCode(), ext.getTenantCode(),
                            ContextSourceEnum.JOB.getCode());
                        this.calcTpsTarget(tenantCommonExt);
                    }
                }
            }
        }
        log.debug("calcTpsTargetJob 执行时间:{}", System.currentTimeMillis() - start);
    }

    private void calcTpsTarget(TenantCommonExt commonExt) {
        WebPluginUtils.setTraceTenantContext(commonExt);
        List<Long> reportIds = reportTaskService.getRunningReport();
        if (CollectionUtils.isEmpty(reportIds)){
            log.debug("暂无压测中的报告！");
            return;
        }
        log.debug("获取租户【{}】【{}】正在压测中的报告:{}", WebPluginUtils.traceTenantId(), WebPluginUtils.traceEnvCode(),
            JsonHelper.bean2Json(reportIds));
        for (Long reportId : reportIds) {
            // 分布式锁
            String lockKey = JobRedisUtils.getJobRedis(commonExt.getTenantId(),commonExt.getEnvCode(),"calcTpsTargetJob#"+reportId);
            if (distributedLock.checkLock(lockKey)) {
                continue;
            }
            // 开始数据层分片
            reportThreadPool.execute(() -> {
                boolean tryLock = distributedLock.tryLock(lockKey, 1L, 1L, TimeUnit.MINUTES);
                if(!tryLock) {
                    return;
                }
                try {
                    WebPluginUtils.setTraceTenantContext(commonExt);
                    reportTaskService.calcTpsTarget(reportId);
                }finally {
                    distributedLock.unLockSafely(lockKey);
                }
            });
        }
    }
}
