package io.shulie.takin.web.config.sync.api;

/**
 * @author zhaoyong
 * 方法追踪和agent交互服务
 */
public interface TraceManageSyncService {
    /**
     * 创建zk节点，写入内容
     * @param agentId
     * @param sampleId
     * @param traceDeployObject
     */
    void createZkTraceManage(String agentId, String sampleId, String traceDeployObject);
}
