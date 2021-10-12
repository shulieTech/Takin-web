package io.shulie.takin.web.config.sync.zk.impl;

import java.util.Comparator;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import com.google.common.collect.Lists;
import io.shulie.takin.web.common.util.CommonUtil;
import io.shulie.takin.web.config.entity.AllowList;
import io.shulie.takin.web.config.sync.api.AllowListSyncService;
import io.shulie.takin.web.config.sync.zk.constants.ZkConfigPathConstants;
import io.shulie.takin.web.config.sync.zk.impl.client.ZkClient;
import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

/**
 * @author shiyajian
 * create: 2020-09-17
 */
@Service
public class AllowListSyncServiceImpl implements AllowListSyncService {

    @Autowired
    private ZkClient zkClient;

    @Override
    public void syncAllowList(TenantCommonExt commonExt, String applicationName, List<AllowList> newAllowLists) {
        if (newAllowLists == null) {
            throw new RuntimeException("传入的数据为空");
        }
        String path = "/" + CommonUtil.getZkNameSpace(commonExt) + ZkConfigPathConstants.ALLOW_LIST_PARENT_PATH + "/" + applicationName;
        // 空数组，我们认为是清空
        if (CollectionUtils.isEmpty(newAllowLists)) {
            zkClient.syncNode(path, JSONObject.toJSONString(Lists.newArrayList()));
            return;
        }
        // 如果新更新的和已有的一样，不更新，降低ZK压力
        String existsStr = zkClient.getNode(path);
        newAllowLists.sort(Comparator.comparing(AllowList::getId));
        String newAllowListStr = JSON.toJSONString(newAllowLists);
        if (newAllowListStr.equals(existsStr)) {
            return;
        }
        zkClient.syncNode(path, newAllowListStr);
    }
}
