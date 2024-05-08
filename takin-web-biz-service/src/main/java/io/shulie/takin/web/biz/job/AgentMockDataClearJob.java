package io.shulie.takin.web.biz.job;

import cn.hutool.core.date.DateUtil;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import io.shulie.takin.job.annotation.ElasticSchedulerJob;
import io.shulie.takin.web.biz.service.agent.AgentMockDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @Description agent心跳数据清理任务
 * @Author ocean_wll
 * @Date 2021/11/18 2:30 下午
 */
@Component
@ElasticSchedulerJob(jobName = "agentMockDataClearJob", cron = "0 0 5 * * ? *", description = "清理3天前mock数据")
@Slf4j
public class AgentMockDataClearJob implements SimpleJob {
    @Resource
    private AgentMockDataService agentMockDataService;

    @Override
    public void execute(ShardingContext shardingContext) {
        Date beforeDate = DateUtil.offsetDay(DateUtil.date(), -3);
        long startTime = System.currentTimeMillis();
        agentMockDataService.clearExpireData(beforeDate);
        log.info("成功清理agent mock数据, 耗时: {}ms", System.currentTimeMillis() - startTime);
    }
}
