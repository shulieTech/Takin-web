package io.shulie.takin.web.biz.job;

import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import com.alibaba.fastjson.JSON;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import io.shulie.takin.job.annotation.ElasticSchedulerJob;
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
public class CalcTpsTargetJob implements SimpleJob {

    @Autowired
    private ReportTaskService reportTaskService;

    @Autowired
    @Qualifier("reportTpsThreadPool")
    private ThreadPoolExecutor reportThreadPool;

    @Autowired
    @Qualifier("redisTemplate")
    private RedisTemplate redisTemplate;

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
                    // 开始数据层分片
                    if (reportId % shardingContext.getShardingTotalCount() == shardingContext.getShardingItem()) {
                        reportThreadPool.execute(() -> reportTaskService.calcTpsTarget(reportId));
                    }
                }else {
                    if (taskDto.getTenantId() % shardingContext.getShardingTotalCount() == shardingContext.getShardingItem()) {
                        this.calcTpsTarget(taskDto);
                    }
                }
            }
        }
        log.debug("calcTpsTargetJob 执行时间:{}", System.currentTimeMillis() - start);
    }

    private List<SceneTaskDto> getTaskFromRedis() {
        Object o = redisTemplate.opsForList().range(WebRedisKeyConstant.SCENE_REPORTID_KEY,0,-1);
        List<SceneTaskDto> taskDtoList = null;
        try {
            taskDtoList = JSON.parseArray(o.toString(),SceneTaskDto.class);
        }catch (Exception e){
            log.error("格式有误，序列化失败！{}",o);
        }
        if (CollectionUtils.isEmpty(taskDtoList)){
            return null;
        }
        return taskDtoList;
    }

    private void calcTpsTarget(SceneTaskDto taskDto) {
        reportThreadPool.execute(() -> {
            WebPluginUtils.setTraceTenantContext(taskDto);
            reportTaskService.calcTpsTarget(taskDto.getReportId());
        });
    }
}
