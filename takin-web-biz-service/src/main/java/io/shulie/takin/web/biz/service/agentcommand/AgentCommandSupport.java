package io.shulie.takin.web.biz.service.agentcommand;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import io.shulie.takin.web.biz.pojo.bo.agentupgradeonline.AgentCommandBO;
import io.shulie.takin.web.biz.pojo.bo.agentupgradeonline.AgentHeartbeatBO;
import org.apache.commons.lang3.StringUtils;

/**
 * @Description agent指令
 * @Author ocean_wll
 * @Date 2021/11/11 2:45 下午
 */
public abstract class AgentCommandSupport implements IAgentCommandProcessor {

    /**
     * 处理业务数据
     *
     * @param agentHeartbeatBO agent心跳数据
     * @param extras           指令参数
     */
    public abstract void process0(AgentHeartbeatBO agentHeartbeatBO, JSONObject extras);

    /**
     * 判断是否需要处理心跳数据
     *
     * @param agentHeartbeatBO agent心跳数据
     * @return true:需要处理，false:不需要处理
     */
    public abstract boolean needDealHeartbeat(AgentHeartbeatBO agentHeartbeatBO);

    /**
     * 处理心跳数据
     *
     * @param agentHeartbeatBO agent心跳数据
     * @return 不同指令的数据
     */
    public abstract Object dealHeartbeat0(AgentHeartbeatBO agentHeartbeatBO);

    @Override
    public void process(AgentHeartbeatBO agentHeartbeatBO, AgentCommandBO commandParam) {
        // 因为这个方法是在线程池中异步调用，所以不允许抛出异常
        try {
            if (StringUtils.isBlank(commandParam.getExtras())) {
                return;
            }
            process0(agentHeartbeatBO, JSON.parseObject(commandParam.getExtras()));
        } catch (Exception e) {
            // ignore
        }
    }

    @Override
    public AgentCommandBO dealHeartbeat(AgentHeartbeatBO agentHeartbeatBO) {
        if (!needDealHeartbeat(agentHeartbeatBO)) {
            return null;
        }

        AgentCommandBO commandBO;
        Object dealResult = dealHeartbeat0(agentHeartbeatBO);
        if (dealResult == null) {
            commandBO = new AgentCommandBO(getCommand().getCommand(), "");
        } else {
            commandBO = new AgentCommandBO(getCommand().getCommand(), JSON.toJSONString(dealResult));
        }

        return commandBO;
    }
}
