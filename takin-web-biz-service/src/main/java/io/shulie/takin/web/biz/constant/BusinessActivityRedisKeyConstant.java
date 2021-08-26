package io.shulie.takin.web.biz.constant;

public class BusinessActivityRedisKeyConstant {

    /**
     * 业务活动流量验证key
     */
    public static final String ACTIVITY_VERIFY_KEY = "activity_verify:";

    /**
     * 业务活动流量验证缓存超时时长
     */
    public static final Integer ACTIVITY_VERIFY_KEY_EXPIRE = 24 * 60 * 60;

    public static final String ACTIVITY_VERIFY_SUFFIX = "-FlowVerify";

    public static final Integer ACTIVITY_VERIFY_DEFAULT_CONFIG_TYPE = 1;

    public static final Integer ACTIVITY_VERIFY_DEFAULT_IP_NUM = 1;

    public static final Integer ACTIVITY_VERIFY_DEFAULT_TARGET_RT = 1;

    public static final Integer ACTIVITY_VERIFY_DEFAULT_TARGET_TPS = 1;

    public static final Integer ACTIVITY_VERIFY_UNVERIFIED = 0;

    public static final Integer ACTIVITY_VERIFY_VERIFYING = 1;

    public static final Integer ACTIVITY_VERIFY_VERIFIED = 2;



}
