package io.shulie.takin.web.biz.job;

import com.alibaba.fastjson.JSON;
import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import io.shulie.takin.web.biz.constant.WebRedisKeyConstant;
import io.shulie.takin.web.biz.service.report.ReportTaskService;
import io.shulie.takin.web.biz.threadpool.ThreadPoolUtil;
import io.shulie.takin.web.common.pojo.dto.SceneTaskDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * @author zhangz
 * Created on 2024/3/4 19:28
 * Email: zz052831@163.com
 */

@Component
//@ElasticSchedulerJob(jobName = "reportBusinessActivitySummaryJob", isSharding = true, cron = "*/10 * * * * ?"
//        , description = "压测报告-计算业务活动排名、业务活动trace节点排名、trace快照入库")
@Slf4j
public class ReportBusinessActivitySummaryJob implements SimpleJob {

    @Value("${report.business.summary.limit.cost:100}")
    private int limitSummaryCost;

    @Autowired
    private ReportTaskService reportTaskService;

    @Autowired
    private RedisTemplate redisTemplate;

    private static Map<Long, AtomicInteger> runningTasks = new ConcurrentHashMap<>();
    private static AtomicInteger EMPTY = new AtomicInteger();

    /**
     * 执行作业.
     *
     * @param shardingContext 分片上下文
     */
    @Override
    public void execute(ShardingContext shardingContext) {
        long start = System.currentTimeMillis();
        List<SceneTaskDto> taskDtoList = getTaskFromRedis();
        if (taskDtoList == null) {
            log.warn("current not running pressure task!!!");
            return;
        }
        for (SceneTaskDto taskDto : taskDtoList) {
            Long reportId = taskDto.getReportId();
            // 开始数据层分片
            if (reportId % shardingContext.getShardingTotalCount() == shardingContext.getShardingItem()) {
                Object task = runningTasks.putIfAbsent(reportId, EMPTY);
                if (task == null) {
                    ThreadPoolUtil.getReportTpsThreadPool().execute(() -> {
                        try {
                            reportTaskService.reportBusinessActivitySummary(reportId, limitSummaryCost);
                        } catch (Throwable e) {
                            log.error("execute CalcTmpReportDataJob occured error. reportId={}", reportId, e);
                        } finally {
                            runningTasks.remove(reportId);
                        }
                    });
                }
            }
        } log.debug("CalcTmpReportDataJob 执行时间:{}", System.currentTimeMillis() - start);
    }

    protected List<SceneTaskDto> getTaskFromRedis() {
        String reportKeyName = WebRedisKeyConstant.getTaskList();
        List<String> o = redisTemplate.opsForList().range(reportKeyName, 0, -1);
        List<SceneTaskDto> taskDtoList = null;
        try {
            if (CollectionUtils.isEmpty(o)) {
                return null;
            }
            taskDtoList = o.stream().map(t -> {
                final Object jsonData = redisTemplate.opsForValue().get(t);
                if (Objects.isNull(jsonData)) {
                    return null;
                }
                return JSON.parseObject(jsonData.toString(), SceneTaskDto.class);
            }).filter(Objects::nonNull).collect(Collectors.toList());
        } catch (Exception e) {
            log.error("格式有误，序列化失败！{}", e);
        }
        if (CollectionUtils.isEmpty(taskDtoList)) {
            return null;
        }
        return taskDtoList;
    }
}
