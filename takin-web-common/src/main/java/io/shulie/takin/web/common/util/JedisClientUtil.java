package io.shulie.takin.web.common.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPool;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/10/13 10:36 PM
 */
@Component
public class JedisClientUtil {
    @Autowired
    private JedisPool jedisPool;

    // 字符串操作
    public void set(Object key, Object val) {
        jedisPool.getResource().set(Objects.toString(key), Objects.toString(val));
    }

    public void set(Object key, Object val, Integer maxIdle, TimeUnit timeUnit) {
        long time = timeUnit.toSeconds(maxIdle);
        jedisPool.getResource().setex(Objects.toString(key), Math.toIntExact(time), Objects.toString(val));
    }

    public Object get(Object key) {
        return jedisPool.getResource().get(Objects.toString(key));
    }

    public void delete(Object key) {
        jedisPool.getResource().del(Objects.toString(key));
    }

    public Long increment(Object key) {
        return jedisPool.getResource().incr(String.valueOf(key));
    }

    // hash
    public Boolean hasKey(Object key, Object field) {
        return jedisPool.getResource().hexists(Objects.toString(key), Objects.toString(field));
    }

    public Boolean hasKey(Object key) {
        return jedisPool.getResource().exists(Objects.toString(key));
    }

    public Object get(Object key, Object field) {
        return jedisPool.getResource().hget(Objects.toString(key), Objects.toString(field));
    }

    public void put(Object key, Object field, Object value) {
        jedisPool.getResource().hset(Objects.toString(key), Objects.toString(field), Objects.toString(value));
    }

    public void expire(Object key, long time, TimeUnit timeUnit) {
        jedisPool.getResource().pexpire(Objects.toString(key), timeUnit.toSeconds(time));
    }

    public Long getExpire(Object key, final TimeUnit timeUnit) {
        return jedisPool.getResource().ttl(Objects.toString(key));
    }
}
