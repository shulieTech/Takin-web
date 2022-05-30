package io.shulie.takin.cloud.common.constants;

/**
 * zk 节点路径
 *
 * @author moriarty
 */
public interface ZkNodePathConstants {

    /**
     * 本地挂载的场景ID
     */
    String LOCAL_MOUNT_SCENE_IDS_PATH = "/config/engine/local/mount/sceneIds";

    /**
     * 日志采样率
     */
    String LOG_SAMPLING_PATH = "/config/log/trace/simpling";
}
