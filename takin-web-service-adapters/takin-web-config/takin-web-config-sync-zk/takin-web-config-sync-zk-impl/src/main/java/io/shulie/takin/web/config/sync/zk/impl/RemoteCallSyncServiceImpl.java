package io.shulie.takin.web.config.sync.zk.impl;

import io.shulie.takin.web.common.vo.agent.AgentRemoteCallVO;
import io.shulie.takin.web.config.sync.api.RemoteCallSyncService;
import org.springframework.stereotype.Component;

/**
 * @author shiyajian
 * create: 2020-09-17
 */
@Component
public class RemoteCallSyncServiceImpl implements RemoteCallSyncService {

    @Override
    public void syncRemoteCall(String namespace, String applicationName, AgentRemoteCallVO agentRemoteCallVO) {
    }

}
