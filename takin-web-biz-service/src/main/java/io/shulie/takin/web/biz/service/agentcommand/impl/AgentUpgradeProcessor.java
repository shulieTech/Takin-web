package io.shulie.takin.web.biz.service.agentcommand.impl;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSONObject;

import io.shulie.takin.web.biz.pojo.bo.agentupgradeonline.AgentHeartbeatBO;
import io.shulie.takin.web.biz.service.agentcommand.AgentCommandSupport;
import io.shulie.takin.web.biz.service.agentupgradeonline.ApplicationPluginUpgradeService;
import io.shulie.takin.web.common.enums.agentupgradeonline.AgentCommandEnum;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * @Description 处理应用升级状态
 * @Author ocean_wll
 * @Date 2021/11/18 3:25 下午
 */
@Component
public class AgentUpgradeProcessor extends AgentCommandSupport {

    private static final String STRING_FINISH = "finish";

    private static final String STRING_UPGRADE_BATCH = "upgradeBatch";

    @Resource
    private ApplicationPluginUpgradeService applicationPluginUpgradeService;

    @Override
    public void process0(AgentHeartbeatBO agentHeartbeatBO, JSONObject commandParam) {
        Boolean finish = commandParam.getBoolean(STRING_FINISH);
        String upgradeBatch = commandParam.getString(STRING_UPGRADE_BATCH);
        if (finish == null || !finish || StringUtils.isEmpty(upgradeBatch)) {
            return;
        }
        applicationPluginUpgradeService.finishUpgrade(agentHeartbeatBO.getApplicationId(), upgradeBatch);
    }

    @Override
    public boolean needDealHeartbeat(AgentHeartbeatBO agentHeartbeatBO) {
        // TODO ocean_wll
        return false;
    }

    @Override
    public Object dealHeartbeat0(AgentHeartbeatBO agentHeartbeatBO) {
        // TODO ocean_wll
        return null;
    }

    @Override
    public AgentCommandEnum getCommand() {
        return AgentCommandEnum.REPORT_UPGRADE_RESULT;
    }
}
