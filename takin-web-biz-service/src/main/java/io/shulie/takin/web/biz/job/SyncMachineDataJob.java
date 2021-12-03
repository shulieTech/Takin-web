package io.shulie.takin.web.biz.job;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

import com.alibaba.fastjson.JSON;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import io.shulie.takin.job.annotation.ElasticSchedulerJob;
import io.shulie.takin.web.biz.common.AbstractSceneTask;
import io.shulie.takin.web.biz.constant.WebRedisKeyConstant;
import io.shulie.takin.web.biz.service.report.ReportTaskService;
import io.shulie.takin.web.common.enums.config.ConfigServerKeyEnum;
import io.shulie.takin.web.common.pojo.dto.SceneTaskDto;
import io.shulie.takin.web.data.util.ConfigServerHelper;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
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
public class SyncMachineDataJob extends AbstractSceneTask implements SimpleJob {

    @Autowired
    private ReportTaskService reportTaskService;

    @Autowired
    @Qualifier("reportMachineThreadPool")
    private ThreadPoolExecutor reportThreadPool;


    private static Map<Long, AtomicInteger> runningTasks = new ConcurrentHashMap<>();
    private static AtomicInteger EMPTY = new AtomicInteger();

    @Override
    public void execute(ShardingContext shardingContext) {
        long start = System.currentTimeMillis();
        final Boolean openVersion = WebPluginUtils.isOpenVersion();
        while (true) {
            List<SceneTaskDto> taskDtoList = getTaskFromRedis();
            if (taskDtoList == null) { break; }
            for (SceneTaskDto taskDto : taskDtoList) {
                Long reportId = taskDto.getReportId();
                if (openVersion){
                    if (reportId % shardingContext.getShardingTotalCount() == shardingContext.getShardingItem()) {
                        Object task = runningTasks.putIfAbsent(reportId, EMPTY);
                        if (task == null) {
                            reportThreadPool.execute(() -> {
                                try {
                                    reportTaskService.syncMachineData(reportId);
                                } catch (Throwable e) {
                                    log.error("execute SyncMachineDataJob occured error. reportId= {}", reportId, e);
                                } finally {
                                    runningTasks.remove(reportId);
                                }
                            });
                        }
                    }
                }else {
                    if (taskDto.getTenantId() % shardingContext.getShardingTotalCount() == shardingContext.getShardingItem()) {
                        AtomicInteger runningThreads = new AtomicInteger(0);
                        AtomicInteger oldRunningThreads = runningTasks.putIfAbsent(taskDto.getTenantId(), runningThreads);
                        if (oldRunningThreads != null) {
                            runningThreads = oldRunningThreads;
                        }
                        //当前租户可以使用的最大线程数
                        int totalThreads = ConfigServerHelper.getIntegerValueByKey(
                            ConfigServerKeyEnum.PER_TENANT_ALLOW_TASK_THREADS_MAX);
                        final int currentThreads = runningThreads.get();
                        if (currentThreads + 1 <= totalThreads) {
                            if (runningThreads.compareAndSet(currentThreads, currentThreads + 1)) {
                                //将任务放入线程池
                                reportThreadPool.execute(() -> {
                                    try {
                                        WebPluginUtils.setTraceTenantContext(taskDto);
                                        reportTaskService.syncMachineData(taskDto.getReportId());
                                    } catch (Throwable e) {
                                        log.error("execute SyncMachineDataJob occured error. reportId={}", reportId, e);
                                    } finally {
                                        AtomicInteger currentRunningThreads = runningTasks.get(taskDto.getTenantId());
                                        if (currentRunningThreads.get() - 1 <= 0) {
                                            // 移除对应的租户
                                            runningTasks.remove(taskDto.getTenantId());
                                        } else {
                                            currentRunningThreads.decrementAndGet();
                                        }
                                    }
                                });
                            }
                        }
                    }
                }
            }
        }

        log.debug("syncMachineData 执行时间:{}", System.currentTimeMillis() - start);
    }
}
