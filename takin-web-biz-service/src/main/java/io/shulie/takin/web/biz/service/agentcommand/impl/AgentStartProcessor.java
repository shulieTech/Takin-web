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
import io.shulie.takin.web.common.enums.agentupgradeonline.AgentUpgradeTypeEnum;
import io.shulie.takin.web.common.enums.application.ApplicationAgentPathValidStatusEnum;
import io.shulie.takin.web.common.enums.fastagentaccess.AgentReportStatusEnum;
import io.shulie.takin.web.data.param.agentupgradeonline.CreateApplicationPluginUpgradeParam;
import io.shulie.takin.web.data.result.application.ApplicationPluginDownloadPathDetailResult;
import io.shulie.takin.web.data.result.application.ApplicationPluginUpgradeDetailResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @Description agent启动处理器
 * @Author ocean_wll
 * @Date 2021/11/19 11:44 上午
 */
@Component
@Slf4j
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

            AgentStartResult agentStartResult = new AgentStartResult(downloadResult);
            agentStartResult.setUpgradeBath(upgradeDetailResult.getUpgradeBatch());

            if (AgentUpgradeTypeEnum.AGENT_REPORT.getVal().equals(upgradeDetailResult.getType())) {
                agentStartResult.setPathType(-1);
            }

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
                    // 将dependencyInfo入库
                    saveDependencyInfo(agentHeartbeatBO);
                    agentStartResult.setUpgradeBath(dependencyMd5);
                } catch (Exception e) {
                    log.error("[AGENT] heartbeat save dependency error, dependency:{}",
                        agentHeartbeatBO.getDependencyInfo(), e);
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
    public AgentReportStatusEnum workStatus() {
        return AgentReportStatusEnum.BEGIN;
    }

    @Override
    public AgentCommandEnum getCommand() {
        return AgentCommandEnum.AGENT_START_GET_FILE;
    }

    /**
     * 将agent上报上来的dependencyInfo入库
     *
     * dependencyInfo格式：module-id=instrument-simulator,module-version=null;module-id=module-aerospike,
     * module-version=null;
     *
     * @param agentHeartbeatBO 心跳数据
     */
    private void saveDependencyInfo(AgentHeartbeatBO agentHeartbeatBO) {
        CreateApplicationPluginUpgradeParam upgradeParam = new CreateApplicationPluginUpgradeParam();
        upgradeParam.setApplicationId(agentHeartbeatBO.getApplicationId());
        upgradeParam.setApplicationName(agentHeartbeatBO.getProjectName());
        upgradeParam.setUpgradeBatch(MD5Util.getMD5(agentHeartbeatBO.getDependencyInfo()));
        upgradeParam.setUpgradeContext(agentHeartbeatBO.getDependencyInfo());
        upgradeParam.setUpgradeAgentId(agentHeartbeatBO.getAgentId());
        upgradeParam.setPluginUpgradeStatus(AgentUpgradeEnum.UPGRADE_SUCCESS.getVal());
        upgradeParam.setType(AgentUpgradeTypeEnum.AGENT_REPORT.getVal());
        upgradeParam.setRemark("agent上报上来的依赖数据");
        applicationPluginUpgradeService.createUpgradeOrder(upgradeParam);
    }

    static class AgentStartResult extends PluginDownloadPathResult {

        /**
         * 升级批次号
         */
        private String upgradeBath;

        public AgentStartResult() {
            super(null);
        }

        public AgentStartResult(ApplicationPluginDownloadPathDetailResult detailResult) {
            super(detailResult);
        }

        public String getUpgradeBath() {
            return upgradeBath;
        }

        public void setUpgradeBath(String upgradeBath) {
            this.upgradeBath = upgradeBath;
        }
    }
}
