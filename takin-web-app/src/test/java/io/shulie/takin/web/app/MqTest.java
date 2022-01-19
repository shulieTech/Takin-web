package io.shulie.takin.web.app;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;

import com.pamirs.takin.common.util.Snowflake;
import io.shulie.takin.web.biz.constant.WebRedisKeyConstant;
import io.shulie.takin.web.biz.mq.producer.Producer;
import io.shulie.takin.web.biz.pojo.dto.mq.MessageDTO;
import io.shulie.takin.web.common.constant.MqConstants;
import io.shulie.takin.web.common.pojo.dto.SceneTaskDto;
import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author liuchuan
 * @date 2021/12/17 9:51 上午
 */
@Slf4j
@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = Application.class)
public class MqTest {

    @Autowired
    private Producer producer;

    @Autowired
    protected Snowflake snowflake;

    @Autowired
    @Qualifier("redisTemplate")
    private RedisTemplate redisTemplate;

    @Value("${resource.redis.host}")
    private String redis;

    @Test
    public void testProduce() {
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setMessage(1111);
        messageDTO.setTopic(MqConstants.MQ_REDIS_PUSH_APPLICATION_MIDDLEWARE);
        producer.produce(messageDTO);
    }

    @Test
    public void test() {
        for (int i = 0; i < 100; i++) {
            System.out.println(snowflake.next());
        }
    }

    @Test
    public void testRedis() {
        final List<SceneTaskDto> fromRedis = getTaskFromRedis();
        fromRedis.stream().filter(t -> Duration.between(fromRedis.get(0).getEndTime(),LocalDateTime.now()).toDays() > 2).forEach(
            t -> removeReportKey(t.getReportId(), null));

    }

    private void removeReportKey(Long reportId, TenantCommonExt commonExt) {
        final String reportKey = WebRedisKeyConstant.getReportKey(reportId);
        redisTemplate.opsForList().remove(WebRedisKeyConstant.SCENE_REPORTID_KEY, 0, reportKey);
        redisTemplate.opsForValue().getOperations().delete(reportKey);
    }

    protected List<SceneTaskDto> getTaskFromRedis() {
        List<String> o = redisTemplate.opsForList().range(WebRedisKeyConstant.SCENE_REPORTID_KEY, 0, -1);
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

}
