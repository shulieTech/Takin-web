package io.shulie.takin.web.config.sync.api;

import io.shulie.takin.web.common.vo.agent.AgentRemoteCallVO;

/**
 * @author 无涯
 * @date 2021/6/4 6:01 下午
 */
public interface RemoteCallSyncService {

    /**
     * 同步远程调用信息
     *
     * @param namespace
     * @param applicationName
     * @param agentRemoteCallVO
     */
    void syncRemoteCall(String namespace, String applicationName, AgentRemoteCallVO agentRemoteCallVO);

}
