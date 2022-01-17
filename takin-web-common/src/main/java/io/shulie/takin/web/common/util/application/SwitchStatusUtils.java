package io.shulie.takin.web.common.util.application;

import io.shulie.takin.web.ext.util.WebPluginUtils;

/**
 * @author by: hezhongqi
 * @Package io.shulie.takin.web.common.util.application
 * @ClassName: SwitchStatusUtil
 * @Description: TODO
 * @Date: 2022/1/17 14:53
 */
public class SwitchStatusUtils {

    /**
     *  静默开关
     */
    private static final String PRADAR_SILENCE_SWITCH_STATUS_VO = "PRADAR_SILENCE_SWITCH_STATUS_";
    /**
     *  静默开关 vo
     */
    private static final String PRADAR_SILENCE_SWITCH_STATUS = "PRADAR_SILENCE_SWITCH_STATUS_VO_";

    /**
     * 压测开关
     */
    private static final String PRADAR_SWITCH_STATUS = "PRADAR_SWITCH_STATUS_";
    /**
     * 压测开关 vo
     */
    private static final String PRADAR_SWITCH_STATUS_VO = "PRADAR_SWITCH_STATUS_VO_";

    /**
     *
     * @return
     */
    public static String getSilenceSwitchVoRedisKey() {
        return PRADAR_SILENCE_SWITCH_STATUS_VO + WebPluginUtils.traceTenantId() + "_" + WebPluginUtils.traceEnvCode();
    }

    /**
     *
     * @return
     */
    public static String getSilenceSwitchRedisKey() {
        return PRADAR_SILENCE_SWITCH_STATUS + WebPluginUtils.traceTenantId() + "_" + WebPluginUtils.traceEnvCode();
    }
}
