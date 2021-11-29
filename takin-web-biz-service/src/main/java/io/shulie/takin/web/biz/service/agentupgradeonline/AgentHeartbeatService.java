package io.shulie.takin.web.biz.service.agentupgradeonline;

import java.util.List;

import io.shulie.takin.web.biz.pojo.bo.agentupgradeonline.AgentCommandResBO;
import io.shulie.takin.web.biz.pojo.request.agentupgradeonline.AgentHeartbeatRequest;

/**
 * @Description agent心跳数据处理
 * @Author ocean_wll
 * @Date 2021/11/17 5:04 下午
 */
public interface AgentHeartbeatService {

    /**
     * 处理心跳数据
     *
     * @param request 心跳请求
     * @return AgentCommandBO集合
     */
    List<AgentCommandResBO> process(AgentHeartbeatRequest request);
}
