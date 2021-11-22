package io.shulie.takin.web.biz.service.agentcommand.impl;

import java.util.concurrent.TimeUnit;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSONObject;

import com.pamirs.takin.common.util.MD5Util;
import io.shulie.takin.web.biz.pojo.bo.agentupgradeonline.AgentHeartbeatBO;
import io.shulie.takin.web.biz.service.DistributedLock;
import io.shulie.takin.web.biz.service.agentcommand.AgentCommandSupport;
import io.shulie.takin.web.biz.service.agentupgradeonline.ApplicationPluginUpgradeService;
import io.shulie.takin.web.common.enums.agentupgradeonline.AgentCommandEnum;
import io.shulie.takin.web.common.enums.agentupgradeonline.AgentUpgradeEnum;
import io.shulie.takin.web.common.enums.application.ApplicationAgentPathValidStatusEnum;
import io.shulie.takin.web.data.result.application.ApplicationPluginDownloadPathDetailResult;
import io.shulie.takin.web.data.result.application.ApplicationPluginUpgradeDetailResult;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @Description agent启动处理器
 * @Author ocean_wll
 * @Date 2021/11/19 11:44 上午
 */
@Component
public class AgentStartProcessor extends AgentCommandSupport {

    private static final String AGENT_START_TEMPLATE = "AGENT_START:%s-%s";

    @Resource
    private ApplicationPluginUpgradeService applicationPluginUpgradeService;

    @Resource
    private DistributedLock distributedLock;

    @Override
    public void process0(AgentHeartbeatBO agentHeartbeatBO, JSONObject extras) {
        // do nothing
    }

    @Override
    public boolean needDealHeartbeat(AgentHeartbeatBO agentHeartbeatBO) {
        // 只要agent上报的当前批次号是-1就需要处理心跳数据
        return DEFAULT_UPGRADE_BATH.equals(agentHeartbeatBO.getCurUpgradeBatch());
    }

    @Override
    public Object dealHeartbeat0(AgentHeartbeatBO agentHeartbeatBO) {
        // 查询当前应用最新的升级成功的升级单批次号
        ApplicationPluginUpgradeDetailResult upgradeDetailResult
            = applicationPluginUpgradeService.queryLatestUpgradeByAppIdAndStatus(agentHeartbeatBO.getApplicationId(),
            AgentUpgradeEnum.UPGRADE_SUCCESS.getVal());

        // 有升级记录，将升级单的批次号返回
        if (upgradeDetailResult != null) {
            ApplicationPluginDownloadPathDetailResult downloadResult = getPluginDownloadPath(
                ApplicationAgentPathValidStatusEnum.CHECK_PASSED);

            AgentStartResult agentStartResult = new AgentStartResult();
            BeanUtils.copyProperties(downloadResult, agentStartResult);
            agentStartResult.setUpgradeBath(upgradeDetailResult.getUpgradeBatch());
            return agentStartResult;
        }

        // 没有升级记录，将dependencyInfo入库，并且将pathType设置为 -1 要抢锁
        if (StringUtils.isEmpty(agentHeartbeatBO.getDependencyInfo())) {
            return null;
        }

        // 计算MD5值，把MD5值当成数据库批次号
        String dependencyMd5 = MD5Util.getMD5(agentHeartbeatBO.getDependencyInfo());

        // 查询依赖的MD5对应的升级单
        ApplicationPluginUpgradeDetailResult dependencyUpgradeDetailResult
            = applicationPluginUpgradeService.queryByAppIdAndUpgradeBatch(agentHeartbeatBO.getApplicationId(),
            dependencyMd5);

        AgentStartResult agentStartResult = new AgentStartResult();
        agentStartResult.setPathType(-1);

        if (dependencyUpgradeDetailResult == null) {
            // 抢锁将dependencyInfo入库
            String lockKey = String.format(AGENT_START_TEMPLATE, agentHeartbeatBO.getApplicationId(), dependencyMd5);
            if (distributedLock.tryLock(lockKey, 0L, 60L, TimeUnit.SECONDS)) {
                try {

                    // TODO 将dependencyInfo入库
                    agentStartResult.setUpgradeBath(dependencyMd5);
                } finally {
                    distributedLock.unLockSafely(lockKey);
                }
            }
        } else {
            agentStartResult.setUpgradeBath(dependencyMd5);
        }

        return agentStartResult;
    }

    @Override
    public AgentCommandEnum getCommand() {
        return AgentCommandEnum.AGENT_START_GET_FILE;
    }

    @Data
    static class AgentStartResult {

        /**
         * 类型 0:oss;1:ftp;2:nginx
         */
        private Integer pathType;

        /**
         * 配置内容
         */
        private String context;

        /**
         * 盐
         */
        private String salt;

        /**
         * 升级批次号
         */
        private String upgradeBath;
    }
}
