package com.pamirs.takin.common.redis;

/**
 * 放白名单 黑名单的 redis key
 *
 * @author Administrator
 */
public class WhiteBlackListRedisKey {

    /**
     * 失效时间 - 七天
     */
    public final static long TIMEOUT = 60 * 60 * 24 * 7;

    /**
     * 白名单key
     */
    public final static String TAKIN_WHITE_LIST_KEY = "takin_white_list_";

    /**
     * 白名单metric key
     */
    public final static String TAKIN_WHITE_LIST_KEY_METRIC = "takin_white_list_metric_";

    /**
     * 黑名单key
     */
    public final static String TAKIN_BLACK_LIST_KEY = "takin_black_list";

}
