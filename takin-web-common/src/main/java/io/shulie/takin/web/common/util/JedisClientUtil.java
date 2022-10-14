package io.shulie.takin.web.common.util;

import com.alibaba.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
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

    public Long increment(Object key) {
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.incr(String.valueOf(key));
        } finally {
            returnResource(jedis);
        }
    }

    // hash
    public Boolean hasKey(Object key, Object field) {
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.hexists(Objects.toString(key), Objects.toString(field));
        } finally {
            returnResource(jedis);
        }
    }

    public Boolean hasKey(Object key) {
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.exists(Objects.toString(key));
        } finally {
            returnResource(jedis);
        }
    }

    public Object get(Object key, Object field) {
        Jedis jedis = jedisPool.getResource();
        try {
            return jedis.hget(Objects.toString(key), Objects.toString(field));
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

    public void expire(Object key, long time, TimeUnit timeUnit) {
        Jedis jedis = jedisPool.getResource();
        try {
            jedis.pexpire(Objects.toString(key), timeUnit.toSeconds(time));
        } finally {
            returnResource(jedis);
        }
    }

    public Long getExpire(Object key, final TimeUnit timeUnit) {
        Jedis jedis = jedisPool.getResource();
        try {
            Long ttl = jedis.ttl(Objects.toString(key));
            return timeUnit.toSeconds(ttl);
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
