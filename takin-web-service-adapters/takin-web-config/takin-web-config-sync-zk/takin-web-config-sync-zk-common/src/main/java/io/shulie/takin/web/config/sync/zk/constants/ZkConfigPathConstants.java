package io.shulie.takin.web.config.sync.zk.constants;

/**
 * @author shiyajian
 * create: 2020-09-15
 */
public final class ZkConfigPathConstants {

    public static final String NAME_SPACE = "/takin/config";

    /**
     * 白名单，按应用名保存
     */
    public static final String ALLOW_LIST_PARENT_PATH = "/allow_list";
    /**
     * 黑名单
     */
    public static final String BLOCK_LIST_PARENT_PATH = "/block_list";
    /**
     * 影子库表，按应用名保存
     */
    public static final String SHADOW_DB_PARENT_PATH = "/shadow_db";
    /**
     * 影子job，按应用名保存
     */
    public static final String SHADOW_JOB_PARENT_PATH = "/shadow_job";
    /**
     * 影子消费者，按应用名保存
     */
    public static final String SHADOW_CONSUMER_PARENT_PATH = "/shadow_job";
    /**
     * 挡板信息，按应用名保存
     */
    public static final String LINK_GUARD_PARENT_PATH = "/guard";

    /**
     * 远程调用路径，按应用名保存
     */
    public static final String REMOTE_CALL_PARENT_PATH = "/remote_call";

    /**
     * 全局压测开关
     */
    public static final String CLUSTER_TEST_SWITCH_PATH = "/switch/cluster_test";
    /**
     * 全局白名单开关
     */
    public static final String ALLOW_LIST_SWITCH_PATH = "/switch/allow_list";

    /**
     * dump 内存文件
     */
    public static final String DUMP_MEMORY_FILE_PATH = "/dumpMemory/file/path";

    /**
     * 方法追踪交互zk节点
     */
    public static final String TRACE_MANAGE_DEPLOY_PATH = "/trace/sample";

    private ZkConfigPathConstants() { /* no instance */ }

}
