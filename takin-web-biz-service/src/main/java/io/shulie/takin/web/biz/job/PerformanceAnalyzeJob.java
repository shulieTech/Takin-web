package io.shulie.takin.web.biz.job;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import io.shulie.takin.job.annotation.ElasticSchedulerJob;
import io.shulie.takin.web.biz.service.perfomanceanaly.PerformanceBaseDataService;
import io.shulie.takin.web.biz.service.perfomanceanaly.ThreadAnalyService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author 无涯
 * @date 2021/6/15 5:50 下午
 */
@Component
@ElasticSchedulerJob(jobName = "performanceAnalyzeJob", cron = "0 0 5 * * ?", description = "性能分析-每天早晨5点执行一次，mysql 清理 5 天, 之前的统计数据")
@Slf4j
public class PerformanceAnalyzeJob implements SimpleJob {

    @Autowired
    private PerformanceBaseDataService performanceBaseDataService;
    @Autowired
    private ThreadAnalyService threadAnalyService;

    @Value("${performance.clear.second:172800}")
    private String second;

    @Override
    public void execute(ShardingContext shardingContext) {
        performanceBaseDataService.clearData(Integer.valueOf(second));
        threadAnalyService.clearData(Integer.valueOf(second));
    }
}
