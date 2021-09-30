package io.shulie.takin.web.config.sync.zk.impl.client;

import javax.annotation.PostConstruct;

import io.shulie.takin.web.common.util.CommonUtil;
import io.shulie.takin.web.config.sync.zk.constants.ZkConfigPathConstants;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author shiyajian
 * create: 2020-09-17
 */
@Slf4j
@Component
public class ZkClient {

    @Value("${takin.config.zk.addr}")
    private String zkAddr;

    @Value("${takin.config.zk.timeout: 3000}")
    private Integer timeout;

    private CuratorFramework client;

    @PostConstruct
    public void init() {
        client = CuratorFrameworkFactory
            .builder()
            .connectString(zkAddr)
            .sessionTimeoutMs(timeout)
            .namespace(ZkConfigPathConstants.NAME_SPACE.substring(1))
            .retryPolicy(new ExponentialBackoffRetry(1000, 3))
            .build();
        client.start();
    }

    public CuratorFramework getClient() {
        return client;
    }

    /**
     * 递归创建所需父节点
     * 创建类型为持久节点
     * 目录及内容
     *
     * @param path 节点路径
     * @param data 值
     */
    public void addNode(String path, String data) {
        try {
            client.create().creatingParentContainersIfNeeded()
                .withMode(CreateMode.PERSISTENT)
                .forPath(CommonUtil.getZkTenantAndEnvPath(path), data.getBytes());
        } catch (Exception e) {
            log.error("创建zk数据节点失败;path={},data={}", CommonUtil.getZkTenantAndEnvPath(path), data, e);
        }
    }

    public String getNode(String path) {
        if (!checkNodeExists(path)) {
            return null;
        }
        byte[] bytes = new byte[0];
        try {
            bytes = client.getData().forPath(CommonUtil.getZkTenantAndEnvPath(path));
        } catch (Exception e) {
            log.error("读取zk数据节点失败;path={}", CommonUtil.getZkTenantAndEnvPath(path), e);
        }
        return new String(bytes);
    }

    public boolean checkNodeExists(String path) {
        try {
            return client.checkExists().forPath(CommonUtil.getZkTenantAndEnvPath(path)) != null;
        } catch (Exception e) {
            log.error("判断数据节点是否存在失败;path={}", CommonUtil.getZkTenantAndEnvPath(path), e);
        }
        return false;
    }

    public void syncNode(String path, String data) {
        if (data.getBytes().length > 1024 * 1024) {
            throw new RuntimeException("ZK单个节点的数据大小不能超过1M，请修改ZK配置");
        }
        if (checkNodeExists(path)) {
            updateNode(path, data);
        } else {
            addNode(path, data);
        }
    }

    public void updateNode(String path, String data) {
        try {
            client.setData().forPath(CommonUtil.getZkTenantAndEnvPath(path), data.getBytes());
        } catch (Exception e) {
            log.error("创建zk数据节点失败;path={},data={}", CommonUtil.getZkTenantAndEnvPath(path), data, e);
        }
    }

    /**
     * // 强制保证删除
     * // 递归删除子节点
     *
     * @param path
     */
    public void deleteNode(String path) {
        try {
            client.delete().guaranteed().deletingChildrenIfNeeded().forPath(CommonUtil.getZkTenantAndEnvPath(path));
        } catch (Exception e) {
            throw new RuntimeException(String.format("删除zk数据节点失败;path=[%s]", CommonUtil.getZkTenantAndEnvPath(path)));

        }
    }
}
