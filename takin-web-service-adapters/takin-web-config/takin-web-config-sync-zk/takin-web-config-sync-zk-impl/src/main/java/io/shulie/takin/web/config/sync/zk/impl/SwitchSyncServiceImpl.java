package io.shulie.takin.web.config.sync.zk.impl;

import com.alibaba.fastjson.JSONObject;

import io.shulie.takin.web.common.util.CommonUtil;
import io.shulie.takin.web.config.sync.api.SwitchSyncService;
import io.shulie.takin.web.config.sync.zk.constants.ZkConfigPathConstants;
import io.shulie.takin.web.config.sync.zk.impl.client.ZkClient;
import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
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
    public void turnClusterTestSwitch(TenantCommonExt commonExt, boolean on) {
        String path = "/" + CommonUtil.getZkNameSpace(commonExt) + ZkConfigPathConstants.CLUSTER_TEST_SWITCH_PATH;
        zkClient.syncNode(path, JSONObject.toJSONString(on));
    }

    @Override
    public void turnAllowListSwitch(TenantCommonExt commonExt, boolean on) {
        String path = "/" + CommonUtil.getZkNameSpace(commonExt) + ZkConfigPathConstants.ALLOW_LIST_SWITCH_PATH;
        zkClient.syncNode(path, JSONObject.toJSONString(on));
    }
}
