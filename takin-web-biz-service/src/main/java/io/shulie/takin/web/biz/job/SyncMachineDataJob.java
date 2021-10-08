package io.shulie.takin.web.biz.job;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import io.shulie.takin.job.annotation.ElasticSchedulerJob;
import io.shulie.takin.web.biz.service.report.ReportService;
import io.shulie.takin.web.biz.service.report.ReportTaskService;
import io.shulie.takin.web.ext.entity.tenant.TenantInfoExt;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
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
    private ReportService reportService;

    @Value("${open.report.task: true}")
    private boolean openReportTask;

    @Autowired
    @Qualifier("jobThreadPool")
    private ThreadPoolExecutor jobThreadPool;

    @Autowired
    @Qualifier("fastDebugThreadPool")
    private ThreadPoolExecutor fastDebugThreadPool;



    @Override
    public void execute(ShardingContext shardingContext) {
        if (!openReportTask) {
            return;
        }
        List<TenantInfoExt> tenantInfoExts = WebPluginUtils.getTenantInfoList();
        if(CollectionUtils.isEmpty(tenantInfoExts)) {
            // 私有化 + 开源 根据 报告id 分片
            //syncMachineData(null);

        }else {
            // saas 分配
            //tenantInfoExts.forEach(t -> {
            //    // 根据环境 分线程
            //    t.getEnvs().forEach(e ->
            //        jobThreadPool.execute(() ->  collectData(new TenantCommonExt(t.getTenantId(),t.getUserAppKey(),e.getEnvCode()))));
            //});
        }

    }

    //// 获取 报告id
    //private List<Long> getReportList() {
    //    return null;
    //}

    //private void syncMachineData(TenantCommonExt tenantCommonExt) {
    //    long start = System.currentTimeMillis();
    //
    //    for (Object obj : reportIds) {
    //        // 开始数据层分片
    //        long reportId = Long.parseLong(String.valueOf(obj));
    //        if (reportId % shardingContext.getShardingTotalCount() == shardingContext.getShardingItem()) {
    //            reportTaskService.syncMachineData(reportId);
    //        }
    //    }
    //    log.info("syncMachineData 执行时间:{}", System.currentTimeMillis() - start);
    //}
}
