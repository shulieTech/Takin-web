package io.shulie.takin.web.biz.service.agentcommand.impl;

import io.shulie.takin.web.biz.pojo.request.agentupgradeonline.AgentCommandRequest;
import io.shulie.takin.web.biz.service.agentcommand.AgentCommandSupport;
import io.shulie.takin.web.common.enums.agentupgradeonline.AgentCommandEnum;
import org.springframework.stereotype.Service;

/**
 * @Description agent心跳处理器
 * @Author ocean_wll
 * @Date 2021/11/11 2:53 下午
 */
@Service
public class AgentHeartBeatProcessor extends AgentCommandSupport {

    @Override
    public Object process0(AgentCommandRequest commandRequest) {
        return null;
    }

    @Override
    public AgentCommandEnum getCommand() {
        return AgentCommandEnum.HEARTBEAT;
    }
}
