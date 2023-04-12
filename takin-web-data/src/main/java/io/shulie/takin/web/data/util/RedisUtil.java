package io.shulie.takin.web.data.util;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RedisUtil {
    public static <T,V> void hmset(RedisTemplate redisTemplate, String key, Map<T, V> map) {
        if (MapUtils.isEmpty(map)){
            return;
        }
        redisTemplate.<T, V>opsForHash().putAll(key, map);
    }
    public static Map<Long, String> hmget(RedisTemplate redisTemplate, String key, List<Long> fields) {
        if (CollectionUtils.isEmpty(fields)){
            return new HashMap<>();
        }
        List<String> keys =  fields.stream().map(String::valueOf).collect(Collectors.toList());
        List<String> result = redisTemplate.<String, String>opsForHash().multiGet(key, keys);
        Map<Long, String> ans = new HashMap<>(fields.size());
        for (int i = 0; i < fields.size(); i++) {
            ans.put(fields.get(i), result.get(i));
        }
        return ans;
    }
}
