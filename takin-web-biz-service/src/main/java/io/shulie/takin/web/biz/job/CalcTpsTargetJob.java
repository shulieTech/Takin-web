package io.shulie.takin.web.biz.job;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * @author 无涯
 * @date 2021/7/13 23:10
 */
@Component
@ElasticSchedulerJob(jobName = "calcTpsTargetJob",
    // 分片序列号和参数用等号分隔 不需要参数可以不加
    //shardingItemParameters = "0=0,1=1,2=2",
    isSharding = true,
    cron = "*/10 * * * * ?",
    description = "获取tps指标图")
@Slf4j
public class CalcTpsTargetJob extends AbstractSceneTask implements SimpleJob {

    @Autowired
    private ReportTaskService reportTaskService;

    @Autowired
    @Qualifier("reportTpsThreadPool")
    private ThreadPoolExecutor reportThreadPool;

    private static Map<Long, Object> runningTasks = new ConcurrentHashMap<>();
    private static Object EMPTY = new Object();

    @Override
    public void execute(ShardingContext shardingContext) {
        long start = System.currentTimeMillis();
        final Boolean openVersion = WebPluginUtils.isOpenVersion();
        while (true) {
            List<SceneTaskDto> taskDtoList = getTaskFromRedis();
            if (taskDtoList == null) { break; }
            for (SceneTaskDto taskDto : taskDtoList) {
                Long reportId = taskDto.getReportId();
                if (openVersion) {
                    // 开始数据层分片
                    if (reportId % shardingContext.getShardingTotalCount() == shardingContext.getShardingItem()) {
                        Object task = runningTasks.putIfAbsent(reportId, EMPTY);
                        if (task == null) {
                            reportThreadPool.execute(() -> {
                                try {
                                    reportTaskService.calcTpsTarget(reportId);
                                } catch (Throwable e) {
                                    log.error("execute CalcTpsTargetJob occured error. reportId={}", reportId, e);
                                } finally {
                                    runningTasks.remove(reportId);
                                }

                            });
                        }

                    }
                } else {
                    if (taskDto.getTenantId() % shardingContext.getShardingTotalCount() == shardingContext
                        .getShardingItem()) {
                        Object task = runningTasks.putIfAbsent(taskDto.getTenantId(), EMPTY);
                        if (task == null) {
                            reportThreadPool.execute(() -> {
                                try {
                                    WebPluginUtils.setTraceTenantContext(taskDto);
                                    reportTaskService.calcTpsTarget(taskDto.getReportId());
                                } catch (Throwable e) {
                                    log.error("execute CalcTpsTargetJob occured error. reportId={},tenantId={}",reportId,taskDto.getTenantId(), e);
                                } finally {
                                    runningTasks.remove(taskDto.getTenantId());
                                }
                            });
                        }
                    }
                }
            }
        }
        log.debug("calcTpsTargetJob 执行时间:{}", System.currentTimeMillis() - start);
    }
}
