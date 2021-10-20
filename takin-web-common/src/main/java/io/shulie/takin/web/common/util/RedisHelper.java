package io.shulie.takin.web.common.util;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Component;

/**
 * redis 工具类
 *
 * @author liuchuan
 * @date 2021/8/13 3:58 下午
 */
@Component
public class RedisHelper {

    private static RedisTemplate redisTemplate;

    @Autowired
    @Qualifier("redisTemplate")
    private RedisTemplate redisT;

    @PostConstruct
    public void init() {
        redisTemplate = redisT;
    }

    /**
     * 根据 redis key, hash key, 获取 value
     *
     * @param redisKey redis key
     * @return value
     */
    public static Object hashGet(String redisKey, String hashKey) {
        return redisTemplate.opsForHash().get(redisKey, hashKey);
    }

    /**
     * 根据 redis key, 获得所有 hash key
     * @param redisKey redisKey
     * @return hashKeys
     */
    public static Set<Object> hashGetAllKeys(String redisKey) {
        return redisTemplate.opsForHash().keys(redisKey);
    }

    /**
     * 根据 redis key, 获取整个 hash
     *
     * @param redisKey redis key
     * @return hash 数据
     */
    public static Map<Object, Object> hashGetAll(String redisKey) {
        return redisTemplate.opsForHash().entries(redisKey);
    }

    /**
     * 根据 redis key, 删除某个 hashKey 及值
     * 或者全部
     *
     * @param redisKey redis key
     * @param hashKey hash key, 不传则删除整个 hash
     */
    public static void hashDelete(String redisKey, Object... hashKey) {
        redisTemplate.opsForHash().delete(redisKey, hashKey);
    }

    /**
     * hash 结构, put
     * @param redisKey redis key
     * @param hashKey hash key
     * @param value value
     */
    public static void hashPut(String redisKey, Object hashKey, Object value) {
        redisTemplate.opsForHash().put(redisKey, hashKey, value);
    }

    /**
     * hash 结构, putAll
     * @param redisKey redis key
     * @param m map
     */
    public static void hashPutAll(String redisKey, Map<?, ?> m) {
        redisTemplate.opsForHash().putAll(redisKey, m);
    }

    /**
     * 是否存在 key
     *
     * @param redisKey key
     * @return 是否
     */
    public static boolean hasKey(String redisKey) {
        return redisTemplate.hasKey(redisKey);
    }

    /**
     * 删除 key
     * @param redisKey key
     * @return 是否成功
     */
    public static Boolean delete(String redisKey) {
        return redisTemplate.delete(redisKey);
    }

    /**
     * 根据key 获取 value
     * @param redisKey
     * @return
     */
    public static Object getValueByKey(String redisKey) {
        return redisTemplate.opsForValue().get(redisKey);
    }

    /**
     * 模糊查询
     * @param pattern
     * @param consumer
     */
    private static void scan(String pattern, Consumer<byte[]> consumer) {
        redisTemplate.execute((RedisConnection connection) -> {
            try (Cursor<byte[]> cursor = connection.scan(ScanOptions.scanOptions().count(Long.MAX_VALUE).match(pattern)
                .build())) {
                cursor.forEachRemaining(consumer);
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * 获取符合条件的key
     *
     * @param pattern 表达式
     * @return
     */
    public static Set<String> keys(String pattern) {
        Set<String> keys = new HashSet<>();
        scan(pattern, item -> {
            //符合条件的key
            String key = new String(item, StandardCharsets.UTF_8);
            keys.add(key);
        });
        return keys;
    }
}
