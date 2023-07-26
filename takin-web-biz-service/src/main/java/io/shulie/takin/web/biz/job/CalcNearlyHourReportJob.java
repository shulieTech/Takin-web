package io.shulie.takin.web.biz.job;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import io.shulie.takin.job.annotation.ElasticSchedulerJob;
import io.shulie.takin.web.biz.service.report.ReportTaskService;
import io.shulie.takin.web.biz.threadpool.ThreadPoolUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
@ElasticSchedulerJob(jobName = "calcNearlyHourReportJob",
        // 分片序列号和参数用等号分隔 不需要参数可以不加
        isSharding = true,
        //shardingItemParameters = "0=0,1=1,2=2",
        cron = "0 */1 * * * ?",
        description = "汇总应用 机器数 风险机器数")
public class CalcNearlyHourReportJob implements SimpleJob {

    @Autowired
    private ReportTaskService reportTaskService;

    private static Map<Long, AtomicInteger> runningTasks = new ConcurrentHashMap<>();
    private static AtomicInteger EMPTY = new AtomicInteger();

    @Override
    public void execute(ShardingContext shardingContext) {
        try {
            this.execute_ext(shardingContext);
        } catch (Throwable e) {
            // 捕捉全部异常,防止任务异常，导致esjob有问题
            log.error("io.shulie.takin.web.biz.job.calcNearlyHourReportJob#execute error" + ExceptionUtils.getStackTrace(e));
        }
    }

    //取过去一个小时的report，计算水位数据
    public void execute_ext(ShardingContext shardingContext) {
        long start = System.currentTimeMillis();
        List<Long> reportIds = reportTaskService.nearlyHourReportIds(20);
        if (CollectionUtils.isEmpty(reportIds)) {
            log.warn("calcNearlyHourReportJob current not running pressure task!!!");
            return;
        }
        for (Long reportId : reportIds) {
            // 开始数据层分片
            if (reportId % shardingContext.getShardingTotalCount() == shardingContext.getShardingItem()) {
                Object task = runningTasks.putIfAbsent(reportId, EMPTY);
                if (task == null) {
                    ThreadPoolUtil.getReportTpsThreadPool().execute(() -> {
                        try {
                            reportTaskService.calcMachineDate(reportId);
                        } catch (Throwable e) {
                            log.error("execute calcNearlyHourReportJob occured error. reportId={}", reportId, e);
                        } finally {
                            runningTasks.remove(reportId);
                        }
                    });
                }
            }
        }
        log.debug("calcNearlyHourReportJob 执行时间:{}", System.currentTimeMillis() - start);
    }

}
