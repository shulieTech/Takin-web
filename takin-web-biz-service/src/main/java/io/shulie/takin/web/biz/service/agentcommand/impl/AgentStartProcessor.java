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
        // 只要agent上报的当前批次号是-1就需要处理心跳数据
        return DEFAULT_UPGRADE_BATH.equals(agentHeartbeatBO.getCurUpgradeBatch());
    }

    @Override
    public Object dealHeartbeat0(AgentHeartbeatBO agentHeartbeatBO) {
        // TODO ocean_wll

        // 查询当前应用最新的升级成功的升级单批次号

        // 没有升级记录，将dependencyInfo入库，并且将pathType设置为 -1 要抢锁

        // 有升级记录，将升级单的批次号返回

        return null;
    }

    @Override
    public AgentCommandEnum getCommand() {
        return AgentCommandEnum.AGENT_START_GET_FILE;
    }
}
