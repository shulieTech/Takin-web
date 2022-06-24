package io.shulie.takin.web.biz.common;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;

import com.dangdang.ddframe.job.api.ShardingContext;
import io.shulie.takin.web.biz.constant.WebRedisKeyConstant;
import io.shulie.takin.web.common.enums.config.ConfigServerKeyEnum;
import io.shulie.takin.web.common.pojo.dto.SceneTaskDto;
import io.shulie.takin.web.data.util.ConfigServerHelper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author caijianying
 */
@Slf4j
public abstract class AbstractSceneTask {

    @Autowired
    @Qualifier("redisTemplate")
    private RedisTemplate redisTemplate;

    private Integer allowedTenantThreadMax;

    protected List<SceneTaskDto> getTaskFromRedis() {
        String reportKeyName = WebRedisKeyConstant.getTaskList();
        List<String> o = redisTemplate.opsForList().range(reportKeyName, 0, -1);
        List<SceneTaskDto> taskDtoList = null;
        try {
            if (CollectionUtils.isEmpty(o)) {
                return null;
            }
            taskDtoList = o.stream().map(t -> {
                final Object jsonData = redisTemplate.opsForValue().get(t);
                if (Objects.isNull(jsonData)) {
                    return null;
                }
                return JSON.parseObject(jsonData.toString(), SceneTaskDto.class);
            }).filter(Objects::nonNull).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("格式有误，序列化失败！{}", e);
        }
        if (CollectionUtils.isEmpty(taskDtoList)) {
            return null;
        }
        return taskDtoList;
    }

    protected int getAllowedTenantThreadMax() {
        if (allowedTenantThreadMax != null) {
            return allowedTenantThreadMax;
        }
        allowedTenantThreadMax = ConfigServerHelper.getIntegerValueByKey(
            ConfigServerKeyEnum.PER_TENANT_ALLOW_TASK_THREADS_MAX);
        return allowedTenantThreadMax;
    }

    protected void cleanUnAvailableTasks(List<SceneTaskDto> taskDtoList) {
        try {
            if (CollectionUtils.isNotEmpty(taskDtoList)) {
                final LocalDateTime now = LocalDateTime.now();
                taskDtoList.stream().filter(t -> t.getEndTime() != null && now.compareTo(t.getEndTime()) > 0).forEach(
                    t -> removeReportKey(t.getReportId()));
            }
        } catch (Exception e) {
            log.error("清理过期任务时发生错误！", e);
        }
    }

    private void removeReportKey(Long reportId) {
        final String reportKey = WebRedisKeyConstant.getReportKey(reportId);
        redisTemplate.opsForList().remove(WebRedisKeyConstant.getTaskList(), 0, reportKey);
        redisTemplate.opsForValue().getOperations().delete(reportKey);
    }

    protected abstract void runTaskInTenantIfNecessary(SceneTaskDto tenantTask, Long reportId);

    protected abstract Map<Long, AtomicInteger> getRunningTasks();

    protected synchronized List<SceneTaskDto> runTask(List<SceneTaskDto> taskDtoList, ShardingContext shardingContext) {
        //已经运行完的任务
        List<SceneTaskDto> taskAlreadyRun = new ArrayList<>();
        //每个租户可以使用的最大线程数
        int allowedThreadMax = this.getAllowedTenantThreadMax();
        //筛选出租户的任务
        final Map<Long, List<SceneTaskDto>> listMap = taskDtoList.stream().filter(t ->
            t.getReportId() % shardingContext.getShardingTotalCount() == shardingContext.getShardingItem()
        ).collect(Collectors.groupingBy(SceneTaskDto::getTenantId));
        if (org.springframework.util.CollectionUtils.isEmpty(listMap)) {
            return taskAlreadyRun;
        }
        for (Entry<Long, List<SceneTaskDto>> listEntry : listMap.entrySet()) {
            final List<SceneTaskDto> tenantTasks = listEntry.getValue();
            if (org.springframework.util.CollectionUtils.isEmpty(tenantTasks)) {
                continue;
            }
            long tenantId = listEntry.getKey();
            /**
             * 取最值。当前租户的任务数和允许的最大线程数
             */
            AtomicInteger allowRunningThreads = new AtomicInteger(
                Math.min(allowedThreadMax, tenantTasks.size()));

            /**
             * 已经运行的任务数
             */
            final Map<Long, AtomicInteger> runningTasks = this.getRunningTasks();
            if (runningTasks == null) {
                log.error("runningTasks cannot be null!");
                return taskAlreadyRun;
            }
            AtomicInteger oldRunningThreads = runningTasks.putIfAbsent(tenantId, allowRunningThreads);
            if (oldRunningThreads != null) {
                /**
                 * 剩下允许执行的任务数
                 * allow running threads calculated by capacity
                 */
                int permitsThreads = Math.min(allowedThreadMax - oldRunningThreads.get(),
                    allowRunningThreads.get());
                // add new threads to capacity
                oldRunningThreads.addAndGet(permitsThreads);
                // adjust allow current running threads
                allowRunningThreads.set(permitsThreads);
            }

            for (int i = 0; i < allowRunningThreads.get(); i++) {
                final SceneTaskDto task = tenantTasks.get(i);
                this.runTaskInTenantIfNecessary(task, task.getReportId());
                taskAlreadyRun.add(task);
            }
        }
        return taskAlreadyRun;
    }

}
