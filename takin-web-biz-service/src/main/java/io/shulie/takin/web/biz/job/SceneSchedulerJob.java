package io.shulie.takin.web.biz.job;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import io.shulie.takin.job.annotation.ElasticSchedulerJob;
import io.shulie.takin.web.biz.service.DistributedLock;
import io.shulie.takin.web.biz.service.scenemanage.SceneSchedulerTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @author 无涯
 * @date 2021/6/15 5:36 下午
 */
@Component
@ElasticSchedulerJob(jobName = "sceneSchedulerJob", cron = "0 */1 * * * ?", description = "压测场景定时执行，一分钟检查一次，待压测场景执行")
@Slf4j
public class SceneSchedulerJob implements SimpleJob {
    @Resource
    private SceneSchedulerTaskService sceneSchedulerTaskService;

    @Autowired
    private DistributedLock distributedLock;

    private String key ="takin_scene_scheduler_job_key";
    @Override
    public void execute(ShardingContext shardingContext) {
        // 查询所有
        if (distributedLock.checkLock(key)) {
            return;
        }
        try {
            boolean tryLock = distributedLock.tryLock(key, 0L, 30L, TimeUnit.SECONDS);
            if (!tryLock) {
                return;
            }
            // 私有化 + 开源
            sceneSchedulerTaskService.executeSchedulerPressureTask();
        } finally {
            distributedLock.unLockSafely(key);
        }
    }
}
