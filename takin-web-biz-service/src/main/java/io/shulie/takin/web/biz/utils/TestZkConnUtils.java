package io.shulie.takin.web.biz.utils;

import org.apache.zookeeper.ZooKeeper;
import org.springframework.util.StringUtils;

/**
 * 测试zk连接的工具类
 *
 * @author ocean_wll
 * @date 2021/8/13 2:11 下午
 */
public class TestZkConnUtils {

    /**
     * zk测试路径
     */
    private final static String TEST_NODE_PATH = "/shulieAgentZkTest";

    /**
     * 测试连接zk
     *
     * @param zkPath zk地址
     * @return true可以正常连接，false连接不上
     */
    public static Boolean test(String zkPath) {
        boolean successConn = false;
        if (!StringUtils.hasLength(zkPath)) {
            return false;
        }
        ZooKeeper zk = null;
        try {
            zk = new ZooKeeper(zkPath, 1000, event -> {
                // do nothing
            });
            zk.exists(TEST_NODE_PATH, false);
            successConn = true;
        } catch (Exception e) {
            // ignore
        } finally {
            if (zk != null) {
                try {
                    zk.close();
                } catch (InterruptedException e) {
                    // ignore
                }
            }
        }
        return successConn;
    }

}

