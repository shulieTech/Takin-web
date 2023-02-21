package io.shulie.takin.web.biz.common;

import java.time.LocalDateTime;
import java.util.*;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;

import com.xxl.job.core.context.XxlJobHelper;
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
    
    /**
     * @param taskDtoList
     */
    protected void runTask_ext(List<SceneTaskDto> taskDtoList) {
        //筛选出租户的任务
        final Map<Long, List<SceneTaskDto>> listMap =
                taskDtoList.stream().filter(t -> t.getReportId() % XxlJobHelper.getShardTotal() == XxlJobHelper.getShardIndex()
                ).collect(Collectors.groupingBy(SceneTaskDto::getTenantId));
        if (listMap.isEmpty()) {
            return;
        }
        for (Entry<Long, List<SceneTaskDto>> listEntry : listMap.entrySet()) {
            final List<SceneTaskDto> tenantTasks = listEntry.getValue();
            if (CollectionUtils.isEmpty(tenantTasks)) {
                continue;
            }
            for (int i = 0; i < tenantTasks.size(); i++) {
                final SceneTaskDto task = tenantTasks.get(i);
                this.runTaskInTenantIfNecessary(task, task.getReportId());
            }
        }
    }

}
