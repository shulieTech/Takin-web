package io.shulie.takin.web.biz.job;

import java.util.concurrent.ThreadPoolExecutor;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import io.shulie.takin.job.annotation.ElasticSchedulerJob;
import io.shulie.takin.web.biz.service.DistributedLock;
import io.shulie.takin.web.biz.service.VerifyTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

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


    @Override
    public void execute(ShardingContext shardingContext) {
        verifyTaskService.showdownVerifyTask();
    }
}
