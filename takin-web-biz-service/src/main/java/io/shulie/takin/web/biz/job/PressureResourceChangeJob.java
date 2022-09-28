package io.shulie.takin.web.biz.job;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import io.shulie.takin.job.annotation.ElasticSchedulerJob;
import io.shulie.takin.web.biz.service.DistributedLock;
import io.shulie.takin.web.biz.service.pressureresource.PressureResourceCommandService;
import io.shulie.takin.web.biz.service.pressureresource.PressureResourceCommonService;
import io.shulie.takin.web.biz.utils.job.JobRedisUtils;
import io.shulie.takin.web.data.mapper.mysql.PressureResourceMapper;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 压测资源关联应用
 */
@Component
@ElasticSchedulerJob(jobName = "PressureResourceChangeJob",
        isSharding = true,
        cron = "0/10 * * * * ? *",
        description = "配置资源修改立即触发")
@Slf4j
public class PressureResourceChangeJob implements SimpleJob {

    @Resource
    private PressureResourceCommonService pressureResourceCommonService;

    @Autowired
    private PressureResourceCommandService pressureResourceCommandService;

    @Resource
    @Qualifier("pressureResourceThreadPool")
    private ThreadPoolExecutor pressureResourceThreadPool;

    @Resource
    private DistributedLock distributedLock;

    @Resource
    private PressureResourceMapper pressureResourceMapper;

    @Override
    public void execute(ShardingContext shardingContext) {
        // 查询所有压测资源准备配置
        List<Long> resourceIds = pressureResourceCommonService.getResourceIdsFormRedis();
        if (CollectionUtils.isEmpty(resourceIds)) {
            return;
        }
        // 按配置Id分片
        List<Long> filterList = resourceIds.stream().filter(resourceId ->
                        resourceId % shardingContext.getShardingTotalCount() == shardingContext.getShardingItem())
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(filterList)) {
            return;
        }
        filterList.forEach(resourceId -> {
            String lockKey = JobRedisUtils.getRedisJobResource(1L, "change", resourceId);
            if (distributedLock.checkLock(lockKey)) {
                return;
            }
            pressureResourceThreadPool.execute(() -> {
                boolean tryLock = distributedLock.tryLock(lockKey, 0L, 60L, TimeUnit.SECONDS);
                if (!tryLock) {
                    return;
                }
                try {
                    PressureResourceEntity resource = pressureResourceMapper.selectById(resourceId);
                    if (resource == null) {
                        log.warn("当前资源准备{}状态调整未查询到数据", resourceId);
                        return;
                    }
                    ResourceContextUtil.setTenantContext(resource);
                    pressureResourceCommandService.pushCommand(resourceId);
                } finally {
                    distributedLock.unLockSafely(lockKey);
                }

            });
        });
    }
}
