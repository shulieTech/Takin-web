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
     * 配置 key
     */
    TAKIN_CONFIG_ZOOKEEPER_ADDRESS("takin.config.zk.addr", "takin.config.zk.addr", NO),
    AGENT_TAKIN_WEB_URL("agent.interactive.takin.web.url", "agent.interactive.takin.web.url", NO),

    TAKIN_REMOTE_CALL_SYNC("tro-web.remote-call.sync", "takin.remote-call.sync", YES),
    ;

    private String old;
    private String now;

    /**
     * 是否用于租户, 1 是, 0 否
     */
    private Integer isTenant;

}
