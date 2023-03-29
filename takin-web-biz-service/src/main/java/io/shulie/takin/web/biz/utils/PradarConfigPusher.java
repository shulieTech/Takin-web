package io.shulie.takin.web.biz.utils;

import com.alibaba.fastjson.JSON;
import io.shulie.takin.web.biz.nacos.NacosConfigManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.zookeeper.data.Stat;
import org.ehcache.impl.internal.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.Map;

/**
 * @author liuchuan
 * @date 2021/11/11 8:59 下午
 */
@Slf4j
@Service
public class PradarConfigPusher {


    @Resource
    private NacosConfigManager nacosConfigManager;

    private ConcurrentHashMap<String, String> currentConfig = new ConcurrentHashMap<>();

    private static final String DATA_ID = "pradarConfig";
    private static final String GROUP = "PRADAR_CONFIG";

    /**
     * 是否使用nacos做配置中心
     *
     * @return
     */
    public boolean useNaocsForConfigCenter() {
        return nacosConfigManager.useNacosForConfigCenter();
    }

    /**
     * 节点值获得
     *
     * @param path 路径
     * @return 值
     */
    public String getNodeValueWithStat(Stat stat, String path) {
        if (!this.isNodeExists(path)) {
            return null;
        }

        try {
//            return new String(client.getData().storingStatIn(stat).forPath(path));
        } catch (Exception e) {
            log.error("读取zk数据节点失败;path={}, 错误信息: {}", path, e.getMessage(), e);
        }

        return null;
    }

    /**
     * 节点值获得
     *
     * @param path 路径
     * @return 值
     */
    public String getNodeValue(String path) {
        if (!this.isNodeExists(path)) {
            return null;
        }

        try {
//            return new String(client.getData().forPath(path));
        } catch (Exception e) {
            log.error("读取zk数据节点失败;path={}, 错误信息: {}", path, e.getMessage(), e);
        }

        return null;
    }

    /**
     * 校验节点是否存在
     *
     * @param path 路径
     * @return true存在，false不存在
     */
    public boolean isNodeExists(String path) {
        try {
//            return client.checkExists().forPath(path) != null;
        } catch (Exception e) {
            log.error("判断数据节点是否存在失败;path={}, 错误信息: {}", path, e.getMessage(), e);
        }
        return false;
    }

    /**
     * 创建永久节点与值
     *
     * @param path  路径
     * @param value 值
     */
    public void addPersistentNode(String path, String value) {
        try {
//            client.create().creatingParentContainersIfNeeded()
//                    .withMode(CreateMode.PERSISTENT)
//                    .forPath(path, value.getBytes());
        } catch (Exception e) {
            log.error("创建zk数据节点失败;path={},data={}", path, value, e);
            throw new RuntimeException(String.format("创建永久节点失败, 错误信息: %s", e.getMessage()));
        }
    }

    /**
     * 更新节点的值
     *
     * @param path  路径
     * @param value 值
     */
    public void updateNode(String path, String value) {
        try {
//            client.setData().forPath(path, value.getBytes());
        } catch (Exception e) {
            log.error("更新zk数据节点失败;path={},data={}", path, value, e);
            throw new RuntimeException(String.format("更新节点失败, 错误信息: %s", e.getMessage()));
        }
    }

    /**
     * 删除节点的值
     *
     * @param path 路径
     */
    public void deleteNode(String path) {
        try {
//            client.delete().guaranteed().deletingChildrenIfNeeded().forPath(path);
        } catch (Exception e) {
            log.error("删除zk数据节点失败;path={}", path, e);
            throw new RuntimeException(String.format("删除节点失败, 错误信息: %s", e.getMessage()));
        }
    }

    public void pushConfigToNacos(Map<String, String> config) {
        currentConfig.putAll(config);
        nacosConfigManager.pushNacosConfigs(DATA_ID, GROUP, JSON.toJSONString(currentConfig));
    }
}
