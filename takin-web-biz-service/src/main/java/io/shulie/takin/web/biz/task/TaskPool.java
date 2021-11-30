package io.shulie.takin.web.biz.task;

import com.alibaba.fastjson.JSON;

import io.shulie.takin.web.common.job.TakinTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author by: hezhongqi
 * @Package io.shulie.takin.web.biz.task
 * @ClassName: JobPool
 * @Description: job 任务池，为普通的 K/V 结构，提供基础的操作
 * @Date: 2021/11/29 22:38
 */
@Component
@Slf4j
public class TaskPool {
    @Autowired
    private RedisTemplate redisTemplate;

    private String NAME = "task.pool";

    /**
     * 获取任务池
     * @return
     */
    private BoundHashOperations getPool () {
        BoundHashOperations ops = redisTemplate.boundHashOps(NAME);
        return ops;
    }

    /**
     * 添加任务
     * @param task
     */
    public void addTask(TakinTask task) {
        log.debug("任务池添加任务：{}", JSON.toJSONString(task));
        this.getPool().put(task.getId(),task);
    }

    /**
     * 获得任务
     * @param jobId
     * @return
     */
    public TakinTask getJob(Long jobId) {
        Object o = getPool().get(jobId);
        if (o instanceof TakinTask) {
            return (TakinTask) o;
        }
        return null;
    }
    /**
     * 移除任务
     * @param jobId
     */
    public void removeDelayJob (Long jobId) {
        log.info("任务池移除任务：{}",jobId);
        // 移除任务
        getPool().delete(jobId);
    }
}
