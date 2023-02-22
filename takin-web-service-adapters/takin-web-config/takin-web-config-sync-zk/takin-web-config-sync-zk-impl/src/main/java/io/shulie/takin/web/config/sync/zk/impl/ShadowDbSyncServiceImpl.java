package io.shulie.takin.web.config.sync.zk.impl;

import io.shulie.takin.web.config.entity.ShadowDB;
import io.shulie.takin.web.config.sync.api.ShadowDbSyncService;
import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author shiyajian
 * create: 2020-09-17
 */
@Component
public class ShadowDbSyncServiceImpl implements ShadowDbSyncService {

//    @Autowired
//    private ZkClient zkClient;

    @Override
    public void syncShadowDataBase(TenantCommonExt commonExt, String applicationName, List<ShadowDB> newShadowDataBases) {
//        if (newShadowDataBases == null) {
//            throw new RuntimeException("传入的数据为空");
//        }
//        String path = "/" + CommonUtil.getZkNameSpace(commonExt) + ZkConfigPathConstants.SHADOW_DB_PARENT_PATH + "/" + applicationName;
//        // 空数组，我们认为是清空
//        if (CollectionUtils.isEmpty(newShadowDataBases)) {
//            zkClient.syncNode(path, JSONObject.toJSONString(Lists.newArrayList()));
//            return;
//        }
//        // 如果新更新的和已有的一样，不更新，降低ZK压力
//        String existsStr = zkClient.getNode(path);
//        newShadowDataBases.sort(Comparator.comparing(ShadowDB::getId));
//        String newShadowDbStr = JSON.toJSONString(newShadowDataBases);
//        if (newShadowDbStr.equals(existsStr)) {
//            return;
//        }
//        zkClient.syncNode(path, newShadowDbStr);
    }
}
