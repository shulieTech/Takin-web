package io.shulie.takin.web.biz.job;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import io.shulie.takin.job.annotation.ElasticSchedulerJob;
import io.shulie.takin.web.biz.common.AbstractSceneTask;
import io.shulie.takin.web.biz.service.report.ReportTaskService;
import io.shulie.takin.web.biz.threadpool.ThreadPoolUtil;
import io.shulie.takin.web.common.pojo.dto.SceneTaskDto;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author 无涯
 * @date 2021/7/13 23:10
 */
@Component
@ElasticSchedulerJob(jobName = "calcReportMockJob", cron = "*/30 * * * * ?", description = "计算压测报告mock数据")
@Slf4j
public class CalcReportMockJob extends AbstractSceneTask implements SimpleJob {

    @Autowired
    private ReportTaskService reportTaskService;

    @Autowired
    private ThreadPoolUtil threadPoolUtil;

    private static Map<Long, AtomicInteger> runningTasks = new ConcurrentHashMap<>();
    private static AtomicInteger EMPTY = new AtomicInteger();

    @Override
    public void execute(ShardingContext shardingContext) {
        try {
            this.execute_ext(shardingContext);
        } catch (Throwable e) {
            // 捕捉全部异常,防止任务异常，导致esjob有问题
            log.error("io.shulie.takin.web.biz.job.CalcReportMockJob#execute error" + ExceptionUtils.getStackTrace(e));
        }
    }

    public void execute_ext(ShardingContext shardingContext) {
        long start = System.currentTimeMillis();
        final Boolean openVersion = WebPluginUtils.isOpenVersion();
        List<SceneTaskDto> taskDtoList = getTaskFromRedis();
        if (taskDtoList == null) {
            return;
        }
        if (openVersion) {
            for (SceneTaskDto taskDto : taskDtoList) {
                Long reportId = taskDto.getReportId();
                // 开始数据层分片
                if (reportId % shardingContext.getShardingTotalCount() == shardingContext.getShardingItem()) {
                    Object task = runningTasks.putIfAbsent(reportId, EMPTY);
                    if (task == null) {
                        ThreadPoolUtil.getReportMockThreadPool().execute(() -> {
                            try {
                                reportTaskService.calcMockSummary(reportId, taskDto.getStartTime(), taskDto.getTenantId(), taskDto.getEnvCode());
                            } catch (Throwable e) {
                                log.error("execute CalcReportMockJob occured error. reportId={}", reportId, e);
                            } finally {
                                runningTasks.remove(reportId);
                            }
                        });
                    }
                }
            }
        } else {
            this.runTask_ext(taskDtoList, shardingContext);
        }
        log.debug("CalcReportMockJob 执行时间:{}", System.currentTimeMillis() - start);
    }

    @Override
    protected void runTaskInTenantIfNecessary(SceneTaskDto tenantTask, Long reportId) {
        //将任务放入线程池
        threadPoolUtil.getReportMockThreadPool().execute(() -> {
            try {
                WebPluginUtils.setTraceTenantContext(tenantTask);
                reportTaskService.calcMockSummary(tenantTask.getReportId(), tenantTask.getStartTime(), tenantTask.getTenantId(), tenantTask.getEnvCode());
            } catch (Throwable e) {
                log.error("execute CalcReportMockJob occured error. reportId={}", reportId, e);
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
