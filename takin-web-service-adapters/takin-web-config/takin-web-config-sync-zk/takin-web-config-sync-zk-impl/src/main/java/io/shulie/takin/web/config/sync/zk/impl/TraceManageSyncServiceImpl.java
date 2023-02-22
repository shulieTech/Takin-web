package io.shulie.takin.web.config.sync.zk.impl;

import io.shulie.takin.web.config.sync.api.TraceManageSyncService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author zhaoyong
 */
@Slf4j
@Component
public class TraceManageSyncServiceImpl implements TraceManageSyncService {

//    @Autowired
//    private ZkClient zkClient;


    @Override
    public void createZkTraceManage(String agentId, String sampleId, String traceDeployObject) {
//        String path = ZkConfigPathConstants.TRACE_MANAGE_DEPLOY_PATH + "/" + agentId + "/" + sampleId;
//        String existsStr = zkClient.getNode(path);
//        //如果路径下有值，说明信息还没有同步，不再重新创建
//        if (StringUtils.isBlank(existsStr)){
//            log.info("创建trace写入zk节点，path={},traceDeployObject={}",path,traceDeployObject);
//            zkClient.syncNode(path,traceDeployObject);
//        }
    }
}
