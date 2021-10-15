package io.shulie.takin.web.common.enums.config;

import io.shulie.takin.web.common.constant.AppConstants;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author liuchuan
 * @date 2021/10/14 2:40 下午
 */
@Getter
@AllArgsConstructor
public enum ConfigServerKeyEnum implements AppConstants {

    /**
     * zk 配置
     */
    TAKIN_CONFIG_ZOOKEEPER_ADDRESS("takin.config.zk.addr", "takin.config.zk.addr", NO),

    /**
     * takin-web url 配置
     */
    AGENT_TAKIN_WEB_URL("agent.interactive.takin.web.url", "agent.interactive.takin.web.url", NO),

    /**
     * ops 脚本路径 配置
     */
    TAKIN_FILE_OPS_SCRIPT_PATH("file.ops_script.path", "takin.file.ops_script.path", NO),

    /**
     * 指定linux执行脚本用户
     */
    TAKIN_FILE_OPS_SCRIPT_DEPLOY_USER("file.ops_script.deploy_user", "takin.file.ops_script.deploy_user", NO),

    /**
     * 是否执行添加linux用户的操作
     */
    TAKIN_FILE_OPS_SCRIPT_DEPLOY_USER_ENABLE("file.ops_script.deploy_user_enable", "takin.file.ops_script.deploy_user_enable", NO),

    /**
     * 白名单长度校验
     */
    TAKIN_WHITE_LIST_NUMBER_LIMIT("whitelist.number.limit", "takin.white_list.number.limit", NO),

    /**
     * 是否开启白名单重名校验
     */
    TAKIN_WHITE_LIST_DUPLICATE_NAME_CHECK("whitelist.duplicate.name.check", "takin.white_list.duplicate.name.check", YES),
    /**
     * 白名单文件
     */
    TAKIN_WHITE_LIST_CONFIG_PATH("spring.config.whiteListPath", "takin.white_list.config.path", NO),
    TAKIN_REMOTE_CALL_SYNC("tro-web.remote-call.sync", "takin.remote-call.sync", YES),
    ;

    /**
     * 原先的 key
     */
    private final String old;

    /**
     * 改造后的 key
     */
    private final String now;

    /**
     * 是否用于租户, 1 是, 0 否
     */
    private final Integer isTenant;

}
