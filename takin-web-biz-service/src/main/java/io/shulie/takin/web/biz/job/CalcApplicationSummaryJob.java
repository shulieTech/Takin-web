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
@ElasticSchedulerJob(jobName = "calcApplicationSummaryJob",
    // 分片序列号和参数用等号分隔 不需要参数可以不加
    isSharding = true,
    //shardingItemParameters = "0=0,1=1,2=2",
    cron = "*/10 * * * * ?",
    description = "汇总应用 机器数 风险机器数")
@Slf4j
public class CalcApplicationSummaryJob implements SimpleJob {

    @Autowired
    private ReportTaskService reportTaskService;

    @Autowired
    @Qualifier("reportSummaryThreadPool")
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
                    // 临时做的开关 现在已不用
                    //if (!ConfigServerHelper.getBooleanValueByKey(ConfigServerKeyEnum.TAKIN_REPORT_OPEN_TASK)) {
                    //    return;
                    //}
                    // 私有化 + 开源 根据 报告id进行分片
                    log.debug("获取正在压测中的报告:{}", reportId);
                    // 开始数据层分片
                    if (reportId % shardingContext.getShardingTotalCount() == shardingContext.getShardingItem()) {
                        reportThreadPool.execute(() -> {
                            WebPluginUtils.setTraceTenantContext(taskDto);
                            reportTaskService.calcApplicationSummary(reportId);
                        });
                    }
                }else {
                    if (taskDto.getTenantId() % shardingContext.getShardingTotalCount() == shardingContext.getShardingItem()) {
                        this.calcApplicationSummary(taskDto);
                    }
                }
            }
        }

        log.debug("calcApplicationSummaryJob 执行时间:{}", System.currentTimeMillis() - start);
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

    private void calcApplicationSummary(SceneTaskDto taskDto) {
        // 开始数据层分片
        reportThreadPool.execute(() -> {
            WebPluginUtils.setTraceTenantContext(taskDto);
            reportTaskService.calcApplicationSummary(taskDto.getReportId());
        });
    }

}