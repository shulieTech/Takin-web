package io.shulie.takin.web.biz.job;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import io.shulie.takin.job.annotation.ElasticSchedulerJob;
import io.shulie.takin.web.biz.service.DistributedLock;
import io.shulie.takin.web.biz.service.agentupgradeonline.AgentReportService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * @Description agent心跳数据清理任务
 * @Author ocean_wll
 * @Date 2021/11/18 2:30 下午
 */
@Component
@ElasticSchedulerJob(jobName = "agentHeartbeatClearJob", cron = "0 0/1 * * * ? *", description = "清理过期的agent心跳数据")
@Slf4j
public class AgentHeartbeatClearJob implements SimpleJob {
    @Resource
    private AgentReportService agentReportService;
    @Autowired
    private DistributedLock distributedLock;
    String key = "agent_heartbeat_clear_job_key";

    @Override
    public void execute(ShardingContext shardingContext) {
        try {
            if (distributedLock.checkLock(key)) {
                return;
            }
            boolean isLock = distributedLock.tryLock(key, 0L, 5L, TimeUnit.MINUTES);
            if (isLock){
                agentReportService.clearExpiredData();
            }
        }finally {
            distributedLock.unLockSafely(key);
        }
    }
}
