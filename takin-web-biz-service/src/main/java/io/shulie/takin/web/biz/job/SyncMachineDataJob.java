package io.shulie.takin.web.biz.job;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import com.alibaba.fastjson.JSON;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import io.shulie.takin.job.annotation.ElasticSchedulerJob;
import io.shulie.takin.web.biz.common.AbstractSceneTask;
import io.shulie.takin.web.biz.constant.WebRedisKeyConstant;
import io.shulie.takin.web.biz.service.report.ReportTaskService;
import io.shulie.takin.web.common.pojo.dto.SceneTaskDto;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author 无涯
 * @date 2021/7/13 23:10
 */
@Component
@ElasticSchedulerJob(jobName = "syncMachineDataJob",
    // 分片序列号和参数用等号分隔 不需要参数可以不加
    isSharding = true,
    //shardingItemParameters = "0=0,1=1,2=2",
    cron = "*/10 * * * * ?",
    description = "同步应用基础信息")
@Slf4j
public class SyncMachineDataJob extends AbstractSceneTask implements SimpleJob {

    @Autowired
    private ReportTaskService reportTaskService;

    @Autowired
    @Qualifier("reportMachineThreadPool")
    private ThreadPoolExecutor reportThreadPool;

    @Override
    public void execute(ShardingContext shardingContext) {
        long start = System.currentTimeMillis();
        final Boolean openVersion = WebPluginUtils.isOpenVersion();
        while (true) {
            List<SceneTaskDto> taskDtoList = getTaskFromRedis();
            if (taskDtoList == null) { break; }
            for (SceneTaskDto taskDto : taskDtoList) {
                Long reportId = taskDto.getReportId();
                if (openVersion){
                    if (reportId % shardingContext.getShardingTotalCount() == shardingContext.getShardingItem()) {
                        reportThreadPool.execute(() -> {
                            WebPluginUtils.setTraceTenantContext(taskDto);
                            reportTaskService.syncMachineData(reportId);
                        });
                    }
                }else {
                    if (taskDto.getTenantId() % shardingContext.getShardingTotalCount() == shardingContext.getShardingItem()) {
                        this.syncMachineData(taskDto);
                    }
                }
            }
        }


        log.debug("syncMachineData 执行时间:{}", System.currentTimeMillis() - start);
    }

    private void syncMachineData(SceneTaskDto taskDto) {
        reportThreadPool.execute(() -> {
            WebPluginUtils.setTraceTenantContext(taskDto);
            reportTaskService.syncMachineData(taskDto.getReportId());
        });
    }
}
