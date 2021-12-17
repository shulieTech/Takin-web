package io.shulie.takin.web.biz.common;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.alibaba.fastjson.JSON;

import io.shulie.takin.web.biz.constant.WebRedisKeyConstant;
import io.shulie.takin.web.common.enums.config.ConfigServerKeyEnum;
import io.shulie.takin.web.common.pojo.dto.SceneTaskDto;
import io.shulie.takin.web.data.util.ConfigServerHelper;
import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
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

    protected List<SceneTaskDto> getTaskFromRedis() {
        Object o = redisTemplate.opsForList().range(WebRedisKeyConstant.SCENE_REPORTID_KEY,0,-1);
        List<SceneTaskDto> taskDtoList = null;
        try {
            taskDtoList = JSON.parseArray(o.toString(),SceneTaskDto.class);
        }catch (Exception e){
            log.error("格式有误，序列化失败！{}",o);
        }
        if (CollectionUtils.isEmpty(taskDtoList)){
            return null;
        }
        return taskDtoList;
    }

    protected int getAllowedTenantThreadMax(){
        return ConfigServerHelper.getIntegerValueByKey(ConfigServerKeyEnum.PER_TENANT_ALLOW_TASK_THREADS_MAX);
    }

    protected void removeReportKey(Long reportId, TenantCommonExt commonExt) {
        redisTemplate.opsForList().remove(WebRedisKeyConstant.SCENE_REPORTID_KEY,0,JSON.toJSONString(new SceneTaskDto(
            commonExt, reportId)));
    }


    protected abstract void runTaskInTenantIfNecessary(int allowedTenantThreadMax, SceneTaskDto tenantTask, Long reportId,
        AtomicInteger runningThreads);

}
