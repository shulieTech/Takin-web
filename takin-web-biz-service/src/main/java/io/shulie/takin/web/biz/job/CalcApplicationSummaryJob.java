package io.shulie.takin.web.biz.job;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import io.shulie.takin.job.annotation.ElasticSchedulerJob;
import io.shulie.takin.web.biz.common.AbstractSceneTask;
import io.shulie.takin.web.biz.service.report.ReportTaskService;
import io.shulie.takin.web.common.pojo.dto.SceneTaskDto;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * @author 无涯
 * @date 2021/7/13 23:10
 */
@Component
@ElasticSchedulerJob(jobName = "calcApplicationSummaryJob",
        // 分片序列号和参数用等号分隔 不需要参数可以不加
        isSharding = true,
        //shardingItemParameters = "0=0,1=1,2=2",
        cron = "*/10 * * * * ?",
        description = "汇总应用 机器数 风险机器数")
@Slf4j
public class CalcApplicationSummaryJob extends AbstractSceneTask implements SimpleJob {

    @Autowired
    private ReportTaskService reportTaskService;

    @Autowired
    @Qualifier("reportSummaryThreadPool")
    private ThreadPoolExecutor reportThreadPool;

    private static Map<Long, AtomicInteger> runningTasks = new ConcurrentHashMap<>();
    private static AtomicInteger EMPTY = new AtomicInteger();

    @Override
    public void execute(ShardingContext shardingContext) {
        try {
            this.execute_ext(shardingContext);
        } catch (Throwable e) {
            // 捕捉全部异常,防止任务异常，导致esjob有问题
            log.error("io.shulie.takin.web.biz.job.CalcApplicationSummaryJob#execute error" + ExceptionUtils.getStackTrace(e));
        }
    }

    public void execute_ext(ShardingContext shardingContext) {
        long start = System.currentTimeMillis();
        final Boolean openVersion = WebPluginUtils.isOpenVersion();
        while (true) {
            List<SceneTaskDto> taskDtoList = getTaskFromRedis();
            if (taskDtoList == null) {
                break;
            }
            if (openVersion) {
                for (SceneTaskDto taskDto : taskDtoList) {
                    Long reportId = taskDto.getReportId();
                    // 开始数据层分片
                    if (reportId % shardingContext.getShardingTotalCount() == shardingContext.getShardingItem()) {
                        Object task = runningTasks.putIfAbsent(reportId, EMPTY);
                        if (task == null) {
                            reportThreadPool.execute(() -> {
                                try {
                                    reportTaskService.calcApplicationSummary(reportId);
                                } catch (Throwable e) {
                                    log.error(
                                            "execute CalcApplicationSummaryJob occured error. reportId= {},errorMsg={}",
                                            reportId, e.getMessage(), e);
                                } finally {
                                    runningTasks.remove(reportId);
                                }
                            });
                        }
                    }
                }
            } else {
                this.runTask(taskDtoList, shardingContext);
            }
        }

        log.debug("calcApplicationSummaryJob 执行时间:{}", System.currentTimeMillis() - start);
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void runTaskInTenantIfNecessary(SceneTaskDto tenantTask, Long reportId) {
        //将任务放入线程池
        reportThreadPool.execute(() -> {
            try {
                WebPluginUtils.setTraceTenantContext(tenantTask);
                reportTaskService.calcApplicationSummary(tenantTask.getReportId());
            } catch (Throwable e) {
                log.error("execute CalcApplicationSummaryJob occured error. reportId={}", reportId, e);
            } finally {
                AtomicInteger currentRunningThreads = runningTasks.get(tenantTask.getTenantId());
                if (currentRunningThreads != null) {
                    currentRunningThreads.decrementAndGet();
                }

            }
        });
    }

    @Override
    protected Map<Long, AtomicInteger> getRunningTasks() {
        return runningTasks;
    }

}