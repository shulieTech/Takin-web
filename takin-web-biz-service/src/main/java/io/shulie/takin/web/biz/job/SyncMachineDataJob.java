package io.shulie.takin.web.biz.job;

import java.util.List;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.google.common.collect.Lists;
import io.shulie.takin.job.annotation.ElasticSchedulerJob;
import io.shulie.takin.utils.json.JsonHelper;
import io.shulie.takin.web.biz.service.report.ReportService;
import io.shulie.takin.web.biz.service.report.ReportTaskService;
import io.shulie.takin.web.common.domain.WebResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Override
    public void execute(ShardingContext shardingContext) {
        if (!openReportTask) {
            return;
        }
        long start = System.currentTimeMillis();
        List<Object> reportIds = Lists.newArrayList();
        WebResponse runningResponse = reportService.queryListRunningReport();
        if (runningResponse.getSuccess() == true && runningResponse.getData() != null) {
            reportIds.addAll((List)runningResponse.getData());
        }
        log.info("获取正在压测中的报告:{}", JsonHelper.bean2Json(reportIds));
        for (Object obj : reportIds) {
            // 开始数据层分片
            long reportId = Long.parseLong(String.valueOf(obj));
            if (reportId % shardingContext.getShardingTotalCount() == shardingContext.getShardingItem()) {
                reportTaskService.syncMachineData(reportId);
            }
        }
        log.info("syncMachineData 执行时间:{}", System.currentTimeMillis() - start);
    }
}
