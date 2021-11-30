package io.shulie.takin.web.biz.task;

import com.alibaba.fastjson.JSON;

import io.shulie.takin.web.common.job.TakinDelayTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author by: hezhongqi
 * @Package io.shulie.takin.web.biz.task
 * @ClassName: ReadyQueue
 * @Description: TODO
 * @Date: 2021/11/29 22:41
 */
@Component
@Slf4j
public class ReadyQueue {
    @Autowired
    private RedisTemplate redisTemplate;

    private String NAME = "process.queue";

    private String getKey(String topic) {
        return NAME + topic;
    }

    /**
     * 获得队列
     * @param topic
     * @return
     */
    private BoundListOperations getQueue (String topic) {
        BoundListOperations ops = redisTemplate.boundListOps(getKey(topic));
        return ops;
    }

    /**
     * 设置任务
     * @param delayJob
     */
    public void pushJob(TakinDelayTask delayJob) {
        log.info("执行队列添加任务:{}",delayJob);
        BoundListOperations listOperations = getQueue(delayJob.getTopic());
        listOperations.leftPush(delayJob);
    }
    /**
     * 移除并获得任务
     * @param topic
     * @return
     */
    public TakinDelayTask popJob(String topic) {
        BoundListOperations listOperations = getQueue(topic);
        Object o = listOperations.leftPop();
        if (o instanceof TakinDelayTask) {
            log.info("执行队列取出任务:{}", JSON.toJSONString((TakinDelayTask) o));
            return (TakinDelayTask) o;
        }
        return null;
    }

}
