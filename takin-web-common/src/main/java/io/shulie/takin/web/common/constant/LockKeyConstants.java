package io.shulie.takin.web.common.constant;

/**
 * 锁 常量
 *
 * @author liuchuan
 * @date 2021/6/2 2:02 下午
 */
public interface LockKeyConstants {

    /**
     * 锁, key 前缀
     */
    String LOCK_PREFIX = "takin-WEB:LOCK:%s";

    /**
     * 文件上传锁
     * 锁住 租户id, 文件名称, 大小
     */
    String LOCK_UPLOAD_FILE = String.format(LOCK_PREFIX, "UPLOAD_FILE:%d_%s_%d");

    /**
     * 创建探针锁
     * 锁住 租户id, 文件路径hashCode
     */
    String LOCK_CREATE_PROBE = String.format(LOCK_PREFIX, "CREATE_PROBE:%d_%d");

    /**
     * 节点探针操作锁
     * 锁住 应用id, agentId
     */
    String LOCK_OPERATE_PROBE = String.format(LOCK_PREFIX, "OPERATE_PROBE:%d_%s");

    /**
     * 脚本调试锁
     * 锁住脚本发布id
     */
    String LOCK_SCRIPT_DEBUG = String.format(LOCK_PREFIX, "SCRIPT_DEBUG:%d");

    /**
     * 应用中间件比对锁
     * 锁住应用id
     */
    String LOCK_COMPARE_APPLICATION_MIDDLEWARE = String.format(LOCK_PREFIX, "COMPARE_APPLICATION_MIDDLEWARE:%d");

    /**
     * agent 上报应用中间件
     * 锁住应用名称
     */
    String LOCK_PUSH_APPLICATION_MIDDLEWARE = String.format(LOCK_PREFIX, "PUSH_APPLICATION_MIDDLEWARE:%s");

    /**
     * agent 上报应用中间件, 异步处理锁
     * 锁住应用id
     */
    String LOCK_HANDLE_PUSH_APPLICATION_MIDDLEWARE = String.format(LOCK_PREFIX, "HANDLE_PUSH_APPLICATION_MIDDLEWARE:%d");

    /**
     * 脚本调试停止, 锁住脚本实例 id
     */
    String LOCK_SCRIPT_DEBUG_STOP = String.format(LOCK_PREFIX, "SCRIPT_DEBUG_STOP:%d");

}
