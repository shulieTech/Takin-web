package io.shulie.takin.web.biz.job;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import io.shulie.takin.job.annotation.ElasticSchedulerJob;
import io.shulie.takin.utils.json.JsonHelper;
import io.shulie.takin.web.biz.service.report.ReportTaskService;
import io.shulie.takin.web.common.enums.config.ConfigServerKeyEnum;
import io.shulie.takin.web.data.util.ConfigServerHelper;
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
@ElasticSchedulerJob(jobName = "syncMachineDataJob",
    // 分片序列号和参数用等号分隔 不需要参数可以不加
    isSharding = true,
    //shardingItemParameters = "0=0,1=1,2=2",
    cron = "*/10 * * * * ?",
    description = "同步应用基础信息")
@Slf4j
public class SyncMachineDataJob implements SimpleJob {

    @Autowired
    private ReportTaskService reportTaskService;

    @Autowired
    @Qualifier("jobThreadPool")
    private ThreadPoolExecutor jobThreadPool;

    @Autowired
    @Qualifier("fastDebugThreadPool")
    private ThreadPoolExecutor fastDebugThreadPool;

    @Override
    public void execute(ShardingContext shardingContext) {
        long start = System.currentTimeMillis();
        List<TenantInfoExt> tenantInfoExts = WebPluginUtils.getTenantInfoList();
        if (CollectionUtils.isEmpty(tenantInfoExts)) {
            if (!ConfigServerHelper.getBooleanValueByKey(ConfigServerKeyEnum.TAKIN_REPORT_OPEN_TASK)) {
                return;
            }

            // 私有化 + 开源 根据 报告id进行分片
            List<Long> reportIds = reportTaskService.getRunningReport();
            log.info("获取正在压测中的报告:{}", JsonHelper.bean2Json(reportIds));
            for (Long reportId : reportIds) {
                // 开始数据层分片
                if (reportId % shardingContext.getShardingTotalCount() == shardingContext.getShardingItem()) {
                    fastDebugThreadPool.execute(() -> reportTaskService.syncMachineData(reportId));
                }
            }
        } else {
            // saas 根据租户进行分片
            for (TenantInfoExt ext : tenantInfoExts) {
                // 开始数据层分片
                if (ext.getTenantId() % shardingContext.getShardingTotalCount() == shardingContext.getShardingItem()) {
                    // 根据环境 分线程
                    for (TenantEnv e : ext.getEnvs()) {
                        WebPluginUtils.setTraceTenantContext(ext.getTenantId(), ext.getTenantAppKey(), e.getEnvCode(),ext.getTenantCode());
                        if (!ConfigServerHelper.getBooleanValueByKey(ConfigServerKeyEnum.TAKIN_REPORT_OPEN_TASK)) {
                            continue;
                        }

                        jobThreadPool.execute(this::syncMachineData);
                    }
                }
            }
        }

        log.info("syncMachineData 执行时间:{}", System.currentTimeMillis() - start);
    }

    private void syncMachineData() {
        List<Long> reportIds = reportTaskService.getRunningReport();
        log.info("获取租户【{}】【{}】正在压测中的报告:{}", WebPluginUtils.traceTenantId(), WebPluginUtils.traceEnvCode(),
            JsonHelper.bean2Json(reportIds));
        for (Long reportId : reportIds) {
            fastDebugThreadPool.execute(() -> reportTaskService.syncMachineData(reportId));
        }

    }
}
