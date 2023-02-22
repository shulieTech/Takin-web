package io.shulie.takin.web.config.sync.zk.impl;

import io.shulie.takin.web.config.entity.ShadowJob;
import io.shulie.takin.web.config.sync.api.ShadowJobSyncService;
import io.shulie.takin.web.ext.entity.tenant.TenantCommonExt;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author shiyajian
 * create: 2020-09-17
 */
@Component
public class ShadowJobSyncServiceImpl implements ShadowJobSyncService {

//    @Autowired
//    private ZkClient zkClient;

    @Override
    public void syncShadowJob(TenantCommonExt commonExt, String applicationName, List<ShadowJob> newShadowJobs) {
//        if (newShadowJobs == null) {
//            throw new RuntimeException("传入的数据为空");
//        }
//        String path = "/" + CommonUtil.getZkNameSpace(commonExt) + ZkConfigPathConstants.SHADOW_JOB_PARENT_PATH + "/" + applicationName;
//        // 空数组，我们认为是清空
//        if (CollectionUtils.isEmpty(newShadowJobs)) {
//            zkClient.syncNode(path, JSONObject.toJSONString(Lists.newArrayList()));
//            return;
//        }
//        // 如果新更新的和已有的一样，不更新，降低ZK压力
//        String existsStr = zkClient.getNode(path);
//        newShadowJobs.sort(Comparator.comparing(ShadowJob::getId));
//        String newShadowJobsStr = JSON.toJSONString(newShadowJobs);
//        if (newShadowJobsStr.equals(existsStr)) {
//            return;
//        }
//        zkClient.syncNode(path, newShadowJobsStr);
    }
}
