package io.shulie.takin.web.biz.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import com.alibaba.excel.util.CollectionUtils;
import com.alibaba.fastjson.JSON;

import io.shulie.takin.web.common.job.TakinDelayTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.BoundZSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Component;

/**
 * @author by: hezhongqi
 * @Package io.shulie.takin.web.biz.task
 * @ClassName: DelayBucket
 * @Description: TODO
 * @Date: 2021/11/29 22:40
 */
@Slf4j
@Component
public class DelayBucket {
    @Autowired
    private RedisTemplate redisTemplate;

    private static AtomicInteger index = new AtomicInteger(0);

    @Value("${thread.size}")
    private int bucketsSize;

    private List<String> bucketNames = new ArrayList<>();

    @Bean
    public List <String> createBuckets() {
        for (int i = 0; i < bucketsSize; i++) {
            bucketNames.add("bucket" + i);
        }
        return bucketNames;
    }

    /**
     * 获得桶的名称
     * @return
     */
    private String getThisBucketName() {
        int thisIndex = index.addAndGet(1);
        int i1 = thisIndex % bucketsSize;
        return bucketNames.get(i1);
    }

    /**
     * 获得桶集合
     * @param bucketName
     * @return
     */
    private BoundZSetOperations getBucket(String bucketName) {
        return redisTemplate.boundZSetOps(bucketName);
    }

    /**
     * 放入延时任务
     * @param job
     */
    public void addDelayJob(TakinDelayTask job) {
        log.info("添加延迟任务:{}", JSON.toJSONString(job));
        String thisBucketName = getThisBucketName();
        BoundZSetOperations bucket = getBucket(thisBucketName);
        bucket.add(job,job.getDelayDate());
    }

    /**
     * 获得最新的延期任务
     * @return
     */
    public TakinDelayTask getFirstDelayTime(Integer index) {
        String name = bucketNames.get(index);
        BoundZSetOperations bucket = getBucket(name);
        Set<TypedTuple> set = bucket.rangeWithScores(0, 1);
        if (CollectionUtils.isEmpty(set)) {
            return null;
        }
        ZSetOperations.TypedTuple typedTuple = (ZSetOperations.TypedTuple) set.toArray()[0];
        Object value = typedTuple.getValue();
        if (value instanceof TakinDelayTask) {
            return (TakinDelayTask) value;
        }
        return null;
    }
    /**
     * 移除延时任务
     * @param index
     * @param delayJob
     */
    public void removeDelayTime(Integer index, TakinDelayTask delayJob) {
        String name = bucketNames.get(index);
        BoundZSetOperations bucket = getBucket(name);
        bucket.remove(delayJob);
    }
}
