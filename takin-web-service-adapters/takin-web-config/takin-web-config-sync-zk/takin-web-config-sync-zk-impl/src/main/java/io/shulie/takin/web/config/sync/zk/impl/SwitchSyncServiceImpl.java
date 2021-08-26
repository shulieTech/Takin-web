package io.shulie.takin.web.config.sync.zk.impl;

import com.alibaba.fastjson.JSONObject;

import io.shulie.takin.web.config.sync.api.SwitchSyncService;
import io.shulie.takin.web.config.sync.zk.constants.ZkConfigPathConstants;
import io.shulie.takin.web.config.sync.zk.impl.client.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author shiyajian
 * create: 2020-09-17
 */
@Component
public class SwitchSyncServiceImpl implements SwitchSyncService {

    @Autowired
    private ZkClient zkClient;

    @Override
    public void turnClusterTestSwitch(String namespace, boolean on) {
        String path = "/" + namespace + ZkConfigPathConstants.CLUSTER_TEST_SWITCH_PATH;
        zkClient.syncNode(path, JSONObject.toJSONString(on));
    }

    @Override
    public void turnAllowListSwitch(String namespace, boolean on) {
        String path = "/" + namespace + ZkConfigPathConstants.ALLOW_LIST_SWITCH_PATH;
        zkClient.syncNode(path, JSONObject.toJSONString(on));
    }
}
