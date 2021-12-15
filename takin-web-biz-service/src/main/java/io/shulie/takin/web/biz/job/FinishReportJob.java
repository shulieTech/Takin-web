package io.shulie.takin.web.biz.job;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import io.shulie.takin.job.annotation.ElasticSchedulerJob;
import io.shulie.takin.utils.json.JsonHelper;
import io.shulie.takin.web.biz.service.report.ReportService;
import io.shulie.takin.web.biz.service.report.ReportTaskService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
    private ReportService reportService;
    @Autowired
    private ThreadPoolExecutor commThreadPool;

    @Override
    public void execute(ShardingContext shardingContext) {

        List<Long> reportIds = reportService.queryListRunningReport();
        log.info("FinishReportJob 获取正在压测中的报告:{}", JsonHelper.bean2Json(reportIds));
        reportIds.stream().filter(Objects::nonNull)
                .map(String::valueOf)
                .map(NumberUtils::toLong)
                .filter(id -> id > 0)
                .filter(id -> id % shardingContext.getShardingTotalCount() == shardingContext.getShardingItem())
                .peek(id -> log.info("------Thread ID: {}, {},任务总片数: {}, 当前分片项: {},当前参数:{}, 当前任务名称: {},当前任务参数 {},reportId :{}",
                        Thread.currentThread().getId(),
                        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()),
                        shardingContext.getShardingTotalCount(),
                        shardingContext.getShardingItem(),
                        shardingContext.getShardingParameter(),
                        shardingContext.getJobName(),
                        shardingContext.getJobParameter(),
                        id)
                )
                .map(id -> (Runnable) () -> reportTaskService.finishReport(id))
                .forEach(commThreadPool::submit);
    }
}
