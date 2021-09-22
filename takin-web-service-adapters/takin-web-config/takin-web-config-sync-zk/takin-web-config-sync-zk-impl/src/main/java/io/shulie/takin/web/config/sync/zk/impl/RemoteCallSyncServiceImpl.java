package io.shulie.takin.web.config.sync.zk.impl;

import cn.hutool.json.JSONUtil;
import io.shulie.takin.web.common.vo.agent.AgentRemoteCallVO;
import io.shulie.takin.web.config.sync.api.RemoteCallSyncService;
import io.shulie.takin.web.config.sync.zk.constants.ZkConfigPathConstants;
import io.shulie.takin.web.config.sync.zk.impl.client.ZkClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
