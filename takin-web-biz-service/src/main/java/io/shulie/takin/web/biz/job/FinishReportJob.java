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
 * @date 2021/6/15 6:08 下午
 */
@Component
@ElasticSchedulerJob(jobName = "finishReportJob",
    // 分片序列号和参数用等号分隔 不需要参数可以不加
    //shardingItemParameters = "0=0,1=1,2=2",
    isSharding = true,
    cron = "*/10 * * * * ?",
    description = "压测报告状态，汇总报告")
@Slf4j
public class FinishReportJob implements SimpleJob {
    @Autowired
    private ReportTaskService reportTaskService;

    @Autowired
    @Qualifier("reportFinishThreadPool")
    private ThreadPoolExecutor reportThreadPool;

    @Autowired
    @Qualifier("redisTemplate")
    private RedisTemplate redisTemplate;

    @Override
    public void execute(ShardingContext shardingContext) {
        long start = System.currentTimeMillis();
        final Boolean openVersion = WebPluginUtils.isOpenVersion();
        //任务开始
        while (true){
            List<SceneTaskDto> taskDtoList = getTaskFromRedis();
            if (taskDtoList == null) { break; }
            for (SceneTaskDto taskDto : taskDtoList) {
                Long reportId = taskDto.getReportId();
                if(openVersion) {
                    // 私有化 + 开源 根据 报告id进行分片
                    // 开始数据层分片
                    if (reportId % shardingContext.getShardingTotalCount() == shardingContext.getShardingItem()) {
                        reportThreadPool.execute(() -> reportTaskService.finishReport(reportId,taskDto));
                    }
                }else {
                    // saas 根据租户进行分片
                    // 开始数据层分片
                    if (taskDto.getTenantId() % shardingContext.getShardingTotalCount() == shardingContext.getShardingItem()) {
                        this.finishReport(taskDto);
                    }
                }
            }
        }
        log.debug("finishReport 执行时间:{}", System.currentTimeMillis() - start);
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

    private void finishReport(SceneTaskDto taskDto) {
        log.debug("获取租户【{}】【{}】正在压测中的报告:{}", taskDto.getTenantId(),
            taskDto.getEnvCode(), taskDto.getReportId());
        reportThreadPool.execute(() -> {
            WebPluginUtils.setTraceTenantContext(taskDto);
            reportTaskService.finishReport(taskDto.getReportId(),taskDto);
        });

    }
}
