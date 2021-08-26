package io.shulie.takin.web.biz.constant;

/**
 * @author shiyajian
 * create: 2020-08-19
 */
public class CacheKeyFactory {

    public static final String WHITE_LIST_PREFIX = "takin#WHITE_LIST";

    public static String getWhiteListKey(String userId) {
        return WHITE_LIST_PREFIX + "#" + userId;
    }
}
