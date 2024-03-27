package io.shulie.takin.web.biz.job;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import io.shulie.takin.job.annotation.ElasticSchedulerJob;
import io.shulie.takin.web.biz.service.DistributedLock;
import io.shulie.takin.web.biz.service.VerifyTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * @author 无涯
 * @date 2021/6/15 5:40 下午
 */
@Component
@ElasticSchedulerJob(jobName = "showdownVerifyJob", cron = "0/10 * *  * * ?", description = "漏数验证")
@Slf4j
public class ShowdownVerifyJob implements SimpleJob {

    @Autowired
    private VerifyTaskService verifyTaskService;

    @Autowired
    private DistributedLock distributedLock;

    private String key ="takin_showdown_verify_job_key";
    @Override
    public void execute(ShardingContext shardingContext) {
        if (distributedLock.checkLock(key)) {
            return;
        }
        try {
            boolean tryLock = distributedLock.tryLock(key, 0L, 3L, TimeUnit.SECONDS);
            if (!tryLock) {
                return;
            }
            // 私有化 + 开源
            verifyTaskService.showdownVerifyTask();
        } finally {
            distributedLock.unLockSafely(key);
        }
    }
}
