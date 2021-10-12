package io.shulie.takin.web.biz.constant;

import io.shulie.takin.web.ext.util.WebPluginUtils;

/**
 * 正常每个客户部署完客户端之后，整个平台使用一个开关。
 * 后期如果做SAAS，一个客户端，要支持多个租户，那么就需要根据租户ID进行区分开关了
 *
 * @author shiyajian
 * create: 2020-09-18
 */
public class SwitchKeyFactory {

    public static final String CLUSTER_TEST_SWITCH_REDIS_KEY = "TAKIN_CLUSTER_TEST_SWITCH#@customerKey@envCode@";

    public static final String ALLOW_LIST_SWITCH_REDIS_KEY = "TAKIN_ALLOW_LIST_SWITCH#@customerKey@envCode@";

    public static final String KEY_PLACEHOLDER = "@customerKey@";

    public static final String ENV_CODE_PLACEHOLDER="envCode@";

    /**
     * 开关先按客户端分，再按租户分。
     */
    public static String getClusterTestSwitchRedisKey(String customerKey) {
        String key = CLUSTER_TEST_SWITCH_REDIS_KEY.replace(KEY_PLACEHOLDER, customerKey);
        return key.replace(ENV_CODE_PLACEHOLDER, WebPluginUtils.traceEnvCode());


    }

    /**
     * 开关按客户级别存储,一个控制台应该是唯一的,SAAS时候才区分
     *
     * @param customerKey
     */
    public static String getAllowListSwitchRedisKey(String customerKey) {
        String key = ALLOW_LIST_SWITCH_REDIS_KEY.replace(KEY_PLACEHOLDER, customerKey);
        return key.replace(ENV_CODE_PLACEHOLDER, WebPluginUtils.traceEnvCode());
    }
}
