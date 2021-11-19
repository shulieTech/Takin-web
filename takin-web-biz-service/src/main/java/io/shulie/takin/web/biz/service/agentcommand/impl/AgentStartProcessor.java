package io.shulie.takin.web.biz.service.agentcommand.impl;

import com.alibaba.fastjson.JSONObject;

import io.shulie.takin.web.biz.pojo.bo.agentupgradeonline.AgentHeartbeatBO;
import io.shulie.takin.web.biz.service.agentcommand.AgentCommandSupport;
import io.shulie.takin.web.common.enums.agentupgradeonline.AgentCommandEnum;
import org.springframework.stereotype.Component;

/**
 * @Description agent启动处理器
 * @Author ocean_wll
 * @Date 2021/11/19 11:44 上午
 */
@Component
public class AgentStartProcessor extends AgentCommandSupport {

    @Override
    public void process0(AgentHeartbeatBO agentHeartbeatBO, JSONObject extras) {
        // do nothing
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
        return AgentCommandEnum.AGENT_START_GET_FILE;
    }
}
