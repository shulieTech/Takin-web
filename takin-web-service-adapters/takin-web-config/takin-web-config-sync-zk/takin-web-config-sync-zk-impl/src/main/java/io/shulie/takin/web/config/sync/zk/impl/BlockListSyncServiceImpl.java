package io.shulie.takin.web.config.sync.zk.impl;

import java.util.List;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import com.google.common.collect.Lists;
import io.shulie.takin.web.common.util.CommonUtil;
import io.shulie.takin.web.config.enums.BlockListType;
import io.shulie.takin.web.config.sync.api.BlockListSyncService;
import io.shulie.takin.web.config.sync.zk.constants.ZkConfigPathConstants;
import io.shulie.takin.web.config.sync.zk.impl.client.ZkClient;
import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * @author fanxx
 * @date 2020/9/28 5:16 下午
 */
@Component
public class BlockListSyncServiceImpl implements BlockListSyncService {

    @Autowired
    private ZkClient zkClient;

    @Override
    public void syncBlockList(TenantCommonExt commonExt, BlockListType type, List<String> blockLists) {
        if (blockLists == null) {
            throw new RuntimeException("传入的数据为空");
        }
        String path = "/" + CommonUtil.getZkNameSpace(commonExt) + ZkConfigPathConstants.BLOCK_LIST_PARENT_PATH + "/" + type.name().toLowerCase();
        // 空数组，我们认为是清空
        if (CollectionUtils.isEmpty(blockLists)) {
            zkClient.syncNode(path, JSONObject.toJSONString(Lists.newArrayList()));
            return;
        }
        // 如果新更新的和已有的一样，不更新，降低ZK压力
        String existsStr = zkClient.getNode(path);
        blockLists.sort(String.CASE_INSENSITIVE_ORDER);
        String newBlockListStr = JSON.toJSONString(blockLists);
        if (newBlockListStr.equals(existsStr)) {
            return;
        }
        zkClient.syncNode(path, newBlockListStr);
    }
}
