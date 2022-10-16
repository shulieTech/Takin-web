package io.shulie.takin.web.common.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.TimeoutUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/10/13 10:36 PM
 */
//@Component
public class JedisClientUtil {
    @Autowired
    private JedisPool jedisPool;

    // 字符串操作
    public void set(String key, String val) {
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.set(Objects.toString(key), val);
        } finally {
            returnResource(jedis);
        }
    }

    public void set(String key, String val, Integer maxIdle, TimeUnit timeUnit) {
        Jedis jedis = jedisPool.getResource();
        try {
            long time = timeUnit.toSeconds(maxIdle);
            jedis.setex(key, Math.toIntExact(time), val);
        } finally {
            returnResource(jedis);
        }
    }

    public Object get(Object key) {
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.get(Objects.toString(key));
        } finally {
            returnResource(jedis);
        }
    }

    public void delete(Object key) {
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.del(Objects.toString(key));
        } finally {
            returnResource(jedis);
        }
    }

    public Long increment(String key) {
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.incr(key);
        } finally {
            returnResource(jedis);
        }
    }

    // hash
    public Boolean hasKey(String key, String field) {
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.hexists(key, field);
        } finally {
            returnResource(jedis);
        }
    }

    public Boolean hasKey(String key) {
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.exists(key);
        } finally {
            returnResource(jedis);
        }
    }

    public Object get(String key, String field) {
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.hget(key, field);
        } finally {
            returnResource(jedis);
        }
    }

    public void put(String key, String field, String value) {
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.hset(key, field, value);
        } finally {
            returnResource(jedis);
        }
    }

    public void expire(String key, long timeout, TimeUnit unit) {
        Jedis jedis = jedisPool.getResource();
        try {
            long rawTimeout = TimeoutUtils.toMillis(timeout, unit);
            jedis.pexpire(Objects.toString(key), rawTimeout);
        } finally {
            returnResource(jedis);
        }
    }

    public Long getExpire(String key, final TimeUnit timeUnit) {
        Jedis jedis = jedisPool.getResource();
        try {
            Long ttl = jedis.pttl(key);
            return TimeoutUtils.toSeconds(ttl, timeUnit);
        } finally {
            returnResource(jedis);
        }
    }

    public static void returnResource(Jedis jedis) {
        if (jedis != null) {
            jedis.close();
        }
    }
}
