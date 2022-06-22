package io.shulie.takin.cloud.common.constants;

/**
 * @author zhaoyong
 */
public class PressureInstanceRedisKey {

    /**
     * 一级redis key
     */
    private static final String PRESSURE_ENGINE_INSTANCE_REDIS_KEY = "PRESSURE:ENGINE:INSTANCE:%s:%s:%s";

    /**
     * 二级redis key
     */
    public static class SecondRedisKey {

        public static final String CONFIG_ID = "CONFIG_MAP_ID";

        public static final String CONFIG_NAME = "CONFIG_MAP_NAME";

        public static final String APP_ID = "APP_ID";

        public static final String REDIS_TPS_LIMIT = "REDIS_TPS_LIMIT";

        public static final String REDIS_TPS_ALL_LIMIT = "REDIS_TPS_ALL_LIMIT";

        public static final String REDIS_TPS_POD_NUM = "REDIS_TPS_POD_NUM";

        public static final String ACTIVITY_REFS = "ACTIVITY_REFS";

        public static final String ACTIVITY_REF_MAP = "ACTIVITY_REF_MAP";

    }

    public static String getEngineInstanceRedisKey(Long sceneId, Long taskId, Long tenantId) {
        return String.format(PressureInstanceRedisKey.PRESSURE_ENGINE_INSTANCE_REDIS_KEY, sceneId, taskId, tenantId);
    }
}
