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

    @Autowired
    private ZkClient zkClient;

    @Override
    public void syncRemoteCall(String namespace, String applicationName, AgentRemoteCallVO agentRemoteCallVO) {
        String path = "/" + namespace + ZkConfigPathConstants.REMOTE_CALL_PARENT_PATH + "/" + applicationName;
        // 空数组，我们认为是清空
        if (agentRemoteCallVO == null) {
            zkClient.syncNode(path, "");
            return;
        }
        // 如果新更新的和已有的一样，不更新，降低ZK压力
        String existsStr = zkClient.getNode(path);
        String newGuardStr = JSONUtil.toJsonStr(agentRemoteCallVO);;
        if(StringUtils.isNotEmpty(existsStr) && existsStr.equals(newGuardStr)) {
            return;
        }
        zkClient.syncNode(path, newGuardStr);
    }
}
