package io.shulie.takin.web.config.sync.file.impl;

import io.shulie.takin.web.common.vo.agent.AgentRemoteCallVO;
import io.shulie.takin.web.config.sync.api.RemoteCallSyncService;
import org.springframework.stereotype.Component;

/**
 * @author 无涯
 * @date 2021/6/5 9:44 下午
 */
@Component
public class RemoteCallSyncServiceImpl implements RemoteCallSyncService {

    @Override
    public void syncRemoteCall(String namespace, String applicationName, AgentRemoteCallVO agentRemoteCallVO) {

    }
}
