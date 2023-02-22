package io.shulie.takin.web.biz.job;

import com.pamirs.takin.common.constant.AppSwitchEnum;
import com.pamirs.takin.entity.domain.dto.ApplicationSwitchStatusDTO;
import com.xxl.job.core.handler.annotation.XxlJob;
import io.shulie.takin.web.biz.cache.AgentConfigCacheManager;
import io.shulie.takin.web.biz.service.DistributedLock;
import io.shulie.takin.web.biz.service.pressureresource.PressureResourceCommandService;
import io.shulie.takin.web.biz.service.pressureresource.PressureResourceCommonService;
import io.shulie.takin.web.biz.service.pressureresource.vo.CommandTaskVo;
import io.shulie.takin.web.biz.utils.job.JobRedisUtils;
import io.shulie.takin.web.data.mapper.mysql.PressureResourceMapper;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 压测资源关联应用
 */
@Component
@Slf4j
public class PressureResourceChangeJob {

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

    @Resource
    private AgentConfigCacheManager agentConfigCacheManager;

    @PostConstruct
    public void init(){
        execute();
    }

    @XxlJob("pressureResourceChangeJobExecute")
    public void execute() {
        // 如果压测开关关闭或者静默开关打开，则不发送命令
        if (!shouldSendCommand()) {
            return;
        }
        // 查询所有压测资源准备配置
        List<CommandTaskVo> commandTaskVos = pressureResourceCommonService.getTaskFormRedis();
        if (CollectionUtils.isEmpty(commandTaskVos)) {
            return;
        }
        commandTaskVos.forEach(taskVo -> {
            String lockKey = JobRedisUtils.getRedisJobResource(1L, "change", taskVo.getResourceId());
            if (distributedLock.checkLock(lockKey)) {
                return;
            }
            pressureResourceThreadPool.execute(() -> {
                boolean tryLock = distributedLock.tryLock(lockKey, 0L, 60L, TimeUnit.SECONDS);
                if (!tryLock) {
                    return;
                }
                try {
                    PressureResourceEntity resource = pressureResourceMapper.queryByIdNoTenant(taskVo.getResourceId());
                    if (resource == null) {
                        log.warn("当前资源准备{}状态调整未查询到数据", taskVo.getResourceId());
                        return;
                    }
                    ResourceContextUtil.setTenantContext(resource);
                    pressureResourceCommandService.pushCommand(taskVo);
                } finally {
                    // 移除Redis数据
                    pressureResourceCommonService.deleteCommandTask(taskVo);
                    distributedLock.unLockSafely(lockKey);
                }
            });
        });
    }

    /**
     * 如果压测开关关闭或者静默开关打开，则不发送命令
     *
     * @return
     */
    private boolean shouldSendCommand() {
        ApplicationSwitchStatusDTO pressureSwitch = agentConfigCacheManager.getPressureSwitch();
        return AppSwitchEnum.OPENED.getCode().equals(pressureSwitch.getSwitchStatus()) && AppSwitchEnum.CLOSED.getCode().equals(pressureSwitch.getSilenceSwitchOn());
    }
}
