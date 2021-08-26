package com.pamirs.takin.common.redis;

/**
 * 一个辅助类，key是缓存的Key，timeout是缓存的失效时间。
 *
 * @author Administrator
 */
public class RedisKey {

    private String key;

    private long timeout;

    public RedisKey(String key, long timeout) {
        super();
        this.key = key;
        this.timeout = timeout;
    }

    public String getKey() {
        return key;
    }

    public long getTimeout() {
        return timeout;
    }

}
