package io.shulie.takin.web.biz.job;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import io.shulie.takin.job.annotation.ElasticSchedulerJob;
import io.shulie.takin.web.biz.common.AbstractSceneTask;
import io.shulie.takin.web.biz.service.report.ReportTaskService;
import io.shulie.takin.web.common.enums.config.ConfigServerKeyEnum;
import io.shulie.takin.web.common.pojo.dto.SceneTaskDto;
import io.shulie.takin.web.data.util.ConfigServerHelper;
import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
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
public class FinishReportJob extends AbstractSceneTask implements SimpleJob {
    @Autowired
    private ReportTaskService reportTaskService;

    @Autowired
    @Qualifier("reportFinishThreadPool")
    private ThreadPoolExecutor reportThreadPool;

    private static Map<Long, AtomicInteger> runningTasks = new ConcurrentHashMap<>();
    private static AtomicInteger EMPTY = new AtomicInteger();

    @Override
    public void execute(ShardingContext shardingContext) {
        long start = System.currentTimeMillis();
        final Boolean openVersion = WebPluginUtils.isOpenVersion();
        //任务开始
        while (true){
            List<SceneTaskDto> taskDtoList = getTaskFromRedis();
            if (taskDtoList == null) { break; }
            if(openVersion) {
                for (SceneTaskDto taskDto : taskDtoList) {
                    Long reportId = taskDto.getReportId();
                    // 私有化 + 开源 根据 报告id进行分片
                    // 开始数据层分片
                    if (reportId % shardingContext.getShardingTotalCount() == shardingContext.getShardingItem()) {
                        Object task = runningTasks.putIfAbsent(reportId, EMPTY);
                        if (task == null) {
                            reportThreadPool.execute(() -> {
                                try {
                                    boolean ifFinish = reportTaskService.finishReport(reportId,taskDto);
                                    if (!ifFinish){
                                        removeTaskIfNecessary(taskDto);
                                    }
                                } catch (Throwable e) {
                                    log.error("execute FinishReportJob occured error. reportId={}", reportId, e);
                                } finally {
                                    runningTasks.remove(reportId);
                                }
                            });
                        }
                    }
                }
            }else {
                //每个租户可以使用的最大线程数
                int allowedTenantThreadMax = this.getAllowedTenantThreadMax();
                //筛选出租户的任务
                final Map<Long, List<SceneTaskDto>> listMap = taskDtoList.stream().collect(
                    Collectors.groupingBy(SceneTaskDto::getTenantId));
                for (SceneTaskDto taskDto : taskDtoList) {
                    Long reportId = taskDto.getReportId();
                    final Long tenantId = taskDto.getTenantId();
                    // saas 根据租户进行分片
                    if (tenantId % shardingContext.getShardingTotalCount() == shardingContext.getShardingItem()) {
                        AtomicInteger runningThreads = new AtomicInteger(0);
                        final List<SceneTaskDto> tenantTasks = listMap.get(tenantId);
                        for (SceneTaskDto tenantTask : tenantTasks) {
                            runTaskInTenantIfNecessary(allowedTenantThreadMax, tenantTask, reportId, runningThreads);
                        }
                    }
                }
            }
        }
        log.debug("finishReport 执行时间:{}", System.currentTimeMillis() - start);
    }

    @Override
    protected void runTaskInTenantIfNecessary(int allowedTenantThreadMax, SceneTaskDto tenantTask, Long reportId,
        AtomicInteger runningThreads) {
        AtomicInteger oldRunningThreads = runningTasks.putIfAbsent(tenantTask.getTenantId(), runningThreads);
        if (oldRunningThreads != null) {
            runningThreads = oldRunningThreads;
        }
        final int currentThreads = runningThreads.get();
        if (currentThreads + 1 <= allowedTenantThreadMax) {
            if (runningThreads.compareAndSet(currentThreads, currentThreads + 1)) {
                //将任务放入线程池
                reportThreadPool.execute(() -> {
                    try {
                        WebPluginUtils.setTraceTenantContext(tenantTask);
                        reportTaskService.finishReport(reportId, tenantTask);
                        //boolean ifFinish = reportTaskService.finishReport(reportId, tenantTask);
                        //if (!ifFinish){
                        //    removeTaskIfNecessary(tenantTask);
                        //}
                    } catch (Throwable e) {
                        log.error("execute FinishReportJob occured error. reportId={}", reportId, e);
                    } finally {
                        AtomicInteger currentRunningThreads = runningTasks.get(tenantTask.getTenantId());
                        if (currentRunningThreads.get() - 1 <= 0) {
                            // 移除对应的租户
                            runningTasks.remove(tenantTask.getTenantId());
                        } else {
                            currentRunningThreads.decrementAndGet();
                        }
                    }
                });
            }
        }
    }

    public void removeTaskIfNecessary(SceneTaskDto tenantTask){
        if (tenantTask.getEndTime()!=null && LocalDateTime.now().compareTo(tenantTask.getEndTime()) > 0){
            this.removeReportKey(tenantTask.getReportId(),tenantTask);
        }
    }
}
