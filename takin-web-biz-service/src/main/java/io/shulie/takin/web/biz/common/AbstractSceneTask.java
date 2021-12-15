package io.shulie.takin.web.biz.common;

import java.util.List;

import com.alibaba.fastjson.JSON;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import io.shulie.takin.web.common.pojo.dto.SceneTaskDto;
import io.shulie.takin.web.biz.constant.WebRedisKeyConstant;

/**
 * @author caijianying
 */
@Slf4j
public abstract class AbstractSceneTask {

    @Autowired
    @Qualifier("redisTemplate")
    private RedisTemplate redisTemplate;

    public List<SceneTaskDto> getTaskFromRedis() {
        Object o = redisTemplate.opsForList().range(WebRedisKeyConstant.SCENE_REPORTID_KEY, 0, -1);
        List<SceneTaskDto> taskDtoList = null;
        try {
            taskDtoList = JSON.parseArray(o.toString(), SceneTaskDto.class);
        } catch (Exception e) {
            log.error("格式有误，序列化失败！{}", o);
        }
        if (CollectionUtils.isEmpty(taskDtoList)) {
            return null;
        }
        return taskDtoList;
    }

}
