package io.shulie.takin.web.biz.job;

import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import io.shulie.takin.web.biz.nacos.NacosConfigManager;
import io.shulie.takin.web.biz.service.DistributedLock;
import io.shulie.takin.web.biz.service.pressureresource.PressureResourceCommandService;
import io.shulie.takin.web.biz.service.pressureresource.common.ModuleEnum;
import io.shulie.takin.web.biz.service.pressureresource.vo.CommandTaskVo;
import io.shulie.takin.web.biz.utils.job.JobRedisUtils;
import io.shulie.takin.web.data.dao.pressureresource.PressureResourceDAO;
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
@Slf4j
public class PressureResourceCommandJob {

    @Resource
    private PressureResourceDAO pressureResourceDAO;

    @Autowired
    private PressureResourceCommandService pressureResourceCommandService;

    @Resource
    @Qualifier("pressureResourceThreadPool")
    private ThreadPoolExecutor pressureResourceThreadPool;

    @Resource
    private DistributedLock distributedLock;

    @Resource
    private NacosConfigManager nacosConfigManager;

    @XxlJob("pressureResourceCommandJobExecute")
    public void execute() {
        // 如果使用nacos做配置中心，则只在配置修改时才发送命令
        if (nacosConfigManager.useNacosForConfigCenter()) {
            return;
        }
        // 查询所有压测资源准备配置
        List<PressureResourceEntity> resourceList = pressureResourceDAO.getAll();
        if (CollectionUtils.isEmpty(resourceList)) {
            log.warn("当前压测资源准备配置为空,暂不处理!!!");
            return;
        }
        // 按配置Id分片
        List<PressureResourceEntity> filterList = resourceList.stream().filter(resouce ->
                        resouce.getId() % XxlJobHelper.getShardTotal() == XxlJobHelper.getShardIndex())
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(filterList)) {
            return;
        }
        filterList.forEach(resource -> {
            String lockKey = JobRedisUtils.getRedisJobResource(1L, "command", resource.getId());
            if (distributedLock.checkLock(lockKey)) {
                return;
            }
            pressureResourceThreadPool.execute(() -> {
                boolean tryLock = distributedLock.tryLock(lockKey, 0L, 60L, TimeUnit.SECONDS);
                if (!tryLock) {
                    return;
                }
                try {
                    ResourceContextUtil.setTenantContext(resource);
                    CommandTaskVo commandTaskVo = new CommandTaskVo();
                    commandTaskVo.setResourceId(resource.getId());
                    commandTaskVo.setModule(ModuleEnum.ALL.getCode());
                    pressureResourceCommandService.pushCommand(commandTaskVo);
                } finally {
                    distributedLock.unLockSafely(lockKey);
                }

            });
        });
    }

}
