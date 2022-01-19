package io.shulie.takin.web.biz.common;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;

import io.shulie.takin.web.biz.constant.WebRedisKeyConstant;
import io.shulie.takin.web.common.common.Separator;
import io.shulie.takin.web.common.enums.config.ConfigServerKeyEnum;
import io.shulie.takin.web.common.pojo.dto.SceneTaskDto;
import io.shulie.takin.web.data.util.ConfigServerHelper;
import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * @author caijianying
 */
@Slf4j
public abstract class AbstractSceneTask {

    @Autowired
    @Qualifier("redisTemplate")
    private RedisTemplate redisTemplate;

    /**
     * 是否是预发环境
     */
    @Value("${takin.inner.pre:0}")
    private int isInnerPre;

    protected List<SceneTaskDto> getTaskFromRedis() {
        String reportKeyName = isInnerPre == 1 ? WebRedisKeyConstant.SCENE_REPORTID_KEY_FOR_INNER_PRE
            : WebRedisKeyConstant.SCENE_REPORTID_KEY;
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
        return ConfigServerHelper.getIntegerValueByKey(ConfigServerKeyEnum.PER_TENANT_ALLOW_TASK_THREADS_MAX);
    }

    protected void cleanUnAvailableTasks(List<SceneTaskDto> taskDtoList) {
        try {
            if(CollectionUtils.isNotEmpty(taskDtoList)){
                taskDtoList.stream().filter(t -> t.getEndTime()!=null && Duration.between(t.getEndTime(), LocalDateTime.now()).toHours() > 2).forEach(
                    t -> removeReportKey(t.getReportId()));
            }
        }catch (Exception e){
            log.error("清理过期任务时发生错误！",e);
        }
    }

    private void removeReportKey(Long reportId) {
        final String reportKey = WebRedisKeyConstant.getReportKey(reportId);
        redisTemplate.opsForList().remove(WebRedisKeyConstant.SCENE_REPORTID_KEY, 0, reportKey);
        redisTemplate.opsForValue().getOperations().delete(reportKey);
    }

    protected abstract void runTaskInTenantIfNecessary(int allowedTenantThreadMax, SceneTaskDto tenantTask,
        Long reportId,
        AtomicInteger runningThreads);

}
