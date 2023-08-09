package io.shulie.takin.web.common.util;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;

import com.google.common.collect.Lists;
import io.shulie.takin.web.common.exception.TakinWebExceptionEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * @author 慕白
 * @date 2019-04-17 9:24
 * redis 工具类
 */
@Component
@Slf4j
public class RedisClientUtil {
    /**
     * lock 超时时间
     */
    private static final int EXPIREMSECS = 30;

    private static final String REENTRY_LOCK_SCRIPT =
            " if redis.call('GET', KEYS[1]) == ARGV[1] then return '1' end;"
                    + " if redis.call('SETNX', KEYS[1], ARGV[1]) == 1 then return '1' else return '0' end";

    private static final String UNLOCK_SCRIPT = "if redis.call('get',KEYS[1]) == ARGV[1] then "
            + " redis.call('del',KEYS[1]) return '1' else return '0' end";

    private static final String REM_RETURN_COUNT =
            " redis.call('SREM', KEYS[1], ARGV[1]); return redis.call('SCARD', KEYS[1])";

    private static final String ADD_RETURN_COUNT =
            " redis.call('SADD', KEYS[1], ARGV[1]); return redis.call('SCARD', KEYS[1])";

    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private RedisTemplate redisTemplate;
    private DefaultRedisScript<Integer> unlockRedisScript;
    private DefaultRedisScript<Integer> reentryLockRedisScript;
    private DefaultRedisScript<Integer> remAndCountScript;
    private DefaultRedisScript<Integer> addAndCountScript;

    @PostConstruct
    public void init() {
        unlockRedisScript = new DefaultRedisScript<>(UNLOCK_SCRIPT, Integer.class);
        reentryLockRedisScript = new DefaultRedisScript<>(REENTRY_LOCK_SCRIPT, Integer.class);
        remAndCountScript = new DefaultRedisScript<>(REM_RETURN_COUNT, Integer.class);
        addAndCountScript = new DefaultRedisScript<>(ADD_RETURN_COUNT, Integer.class);
    }

    @Autowired
    public void setStringRedisTemplate(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public void setString(final String key, final String value,
                          final int expire, TimeUnit timeUnit) {
        stringRedisTemplate.opsForValue().set(key, value, expire, timeUnit);
    }

    public void setString(final String key, final String value) {
        stringRedisTemplate.opsForValue().set(key, value);
    }

    public String getString(final String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    public void leftPushAll(final String key, final List<String> numList) {
        stringRedisTemplate.opsForList().leftPushAll(key, numList);
    }

    public Object getObject(final String key) {
        return stringRedisTemplate.opsForValue().get(key);
    }

    public void delete(final String key) {
        stringRedisTemplate.delete(key);
    }

    public boolean lock(String key, String value) {
        return lockExpire(key, value, EXPIREMSECS, TimeUnit.SECONDS);
    }

    public boolean reentryLockNoExpire(String key, String value) {
        return "1".equals(String.valueOf(redisTemplate.execute(reentryLockRedisScript,
                Lists.newArrayList(getLockPrefix(key)), value)));
    }

    public boolean lockNoExpire(String key, String value) {
        return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(getLockPrefix(key), value));
    }

    public boolean lockExpire(String key, String value, long time, TimeUnit unit) {
        return Boolean.TRUE.equals(redisTemplate.opsForValue().setIfAbsent(getLockPrefix(key), value, time, unit));
    }

    private static String getLockPrefix(String key) {
        return String.format("LOCK:%s", key);
    }

    public boolean unlock(String key, String value) {
        return "1".equals(String.valueOf(redisTemplate.execute(unlockRedisScript,
                Lists.newArrayList(getLockPrefix(key)), value)));
    }

    public Long increment(final String key, final long l) {
        stringRedisTemplate.expire(key, 24, TimeUnit.HOURS);
        return stringRedisTemplate.opsForValue().increment(key, l);
    }

    /**
     * 获取并自增l个，key不过期
     *
     * @param key -
     * @param l   -
     * @return -
     */
    public Long incrementAndNotExpire(final String key, final long l) {
        return stringRedisTemplate.opsForValue().increment(key, l);
    }

    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间(秒)
     * @return -
     */
    public boolean expire(String key, long time) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, TimeUnit.SECONDS);
            }
            return true;
        } catch (Exception e) {
            log.error("异常代码【{}】,异常内容：redis命令执行失败 --> expire方法执行异常，异常信息: {}",
                    TakinWebExceptionEnum.REDIS_CMD_EXECUTE_ERROR, e);
            return false;
        }
    }

    public boolean expire(String key, long time, TimeUnit timeUnit) {
        try {
            if (time > 0) {
                redisTemplate.expire(key, time, timeUnit);
            }
            return true;
        } catch (Exception e) {
            log.error("异常代码【{}】,异常内容：redis命令执行失败 --> expire方法执行异常，异常信息: {}",
                    TakinWebExceptionEnum.REDIS_CMD_EXECUTE_ERROR, e);
            return false;
        }
    }

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    public Object get(String key, int indexdb) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        return key == null ? null : redisTemplate.opsForValue().get(key);
    }

    /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     * @return true成功 false失败
     */
    public boolean set(String key, Object value, int indexdb) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            log.error("异常代码【{}】,异常内容：redis命令执行失败 --> set方法执行异常，异常信息: {}",
                    TakinWebExceptionEnum.REDIS_CMD_EXECUTE_ERROR, e);
            return false;
        }

    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    public boolean set(String key, Object value, long time) {
        try {
            if (time > 0) {
                redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                redisTemplate.opsForValue().set(key, value);
            }
            return true;
        } catch (Exception e) {
            log.error("异常代码【{}】,异常内容：redis命令执行失败 --> set with time方法执行异常，异常信息: {}",
                    TakinWebExceptionEnum.REDIS_CMD_EXECUTE_ERROR, e);
            return false;
        }
    }

    /**
     * HashSet
     *
     * @param key 键
     * @param map 对应多个键值
     * @return true 成功 false 失败
     */
    public boolean hmset(String key, Map<String, Object> map) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            return true;
        } catch (Exception e) {
            log.error("异常代码【{}】,异常内容：redis命令执行失败 --> hmset 方法执行异常，异常信息: {}",
                    TakinWebExceptionEnum.REDIS_CMD_EXECUTE_ERROR, e);
            return false;
        }
    }

    public boolean hmset(String key, String field, Object value) {
        try {
            redisTemplate.opsForHash().put(key, field, value);
            return true;
        } catch (Exception e) {
            log.error("异常代码【{}】,异常内容：redis命令执行失败 --> hmset with time方法执行异常，异常信息: {}",
                    TakinWebExceptionEnum.REDIS_CMD_EXECUTE_ERROR, e);
            return false;
        }
    }

    /**
     * HashSet 并设置时间
     *
     * @param key  键
     * @param map  对应多个键值
     * @param time 时间(秒)
     * @return true成功 false失败
     */
    public boolean hmset(String key, Map<String, Object> map, long time) {
        try {
            redisTemplate.opsForHash().putAll(key, map);
            if (time > 0) {
                expire(key, time);
            }
            return true;
        } catch (Exception e) {
            log.error("异常代码【{}】,异常内容：redis命令执行失败 --> hmset map with time方法执行异常，异常信息: {}",
                    TakinWebExceptionEnum.REDIS_CMD_EXECUTE_ERROR, e);
            return false;
        }
    }

    public boolean hExists(String key, String field) {
        return Boolean.TRUE.equals(redisTemplate.opsForHash().hasKey(key, field));
    }

    /**
     * 删除缓存
     *
     * @param key 可以传一个值 或多个
     */
    @SuppressWarnings("unchecked")
    public void del(String... key) {
        if (key != null && key.length > 0) {
            if (key.length == 1) {
                redisTemplate.delete(key[0]);
            } else {
                redisTemplate.delete(CollectionUtils.arrayToList(key));
            }
        }
    }

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    public boolean hasKey(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            log.error("异常代码【{}】,异常内容：redis命令执行失败 --> hasKey方法执行异常，异常信息: {}",
                    TakinWebExceptionEnum.REDIS_CMD_EXECUTE_ERROR, e);
            return false;
        }
    }

    public boolean hasLockKey(String key) {
        try {
            return redisTemplate.hasKey(getLockPrefix(key));
        } catch (Exception e) {
            log.error("异常代码【{}】,异常内容：redis命令执行失败 --> hasKey方法执行异常，异常信息: {}",
                    TakinWebExceptionEnum.REDIS_CMD_EXECUTE_ERROR, e);
            return false;
        }
    }

    public static String getLockKey(String key) {
        return getLockPrefix(key);
    }

    /**
     * 获取hashKey对应的所有键值
     *
     * @param key 键
     * @return 对应的多个键值
     */
    public Map<Object, Object> hmget(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    public Object hmget(String key, String field) {
        return redisTemplate.opsForHash().get(key, field);
    }

    public List<String> hmget(String key, List<String> fieldList) {
        return redisTemplate.opsForHash().multiGet(key, fieldList);
    }

    public void hmdelete(String key, String field) {
        redisTemplate.opsForHash().delete(key, field);
    }

    /**------------------zSet相关操作--------------------------------*/

    /**
     * 添加元素,有序集合是按照元素的score值由小到大排列
     *
     * @param key
     * @param value
     * @param score
     * @return -
     */
    public Boolean zAdd(String key, String value, double score) {
        return redisTemplate.opsForZSet().add(key, value, score);
    }

    /**
     * @param key
     * @param values
     * @return -
     */
    public Long zAdd(String key, Set<ZSetOperations.TypedTuple<String>> values) {
        return redisTemplate.opsForZSet().add(key, values);
    }

    /**
     * @param key
     * @param values
     * @return -
     */
    public Long zRemove(String key, Object... values) {
        return redisTemplate.opsForZSet().remove(key, values);
    }

    /**
     * 增加元素的score值，并返回增加后的值
     *
     * @param key
     * @param value
     * @param delta
     * @return -
     */
    public Double zIncrementScore(String key, String value, double delta) {
        return redisTemplate.opsForZSet().incrementScore(key, value, delta);
    }

    //    /**
    //     * 获取集合的元素, 从小到大排序
    //     *
    //     * @param key
    //     * @param start 开始位置
    //     * @param end   结束位置, -1查询所有
    //     * @return -
    //     */
    //    public Set<String> zRange(String key, long start, long end) {
    //        return redisTemplate.opsForZSet().range(key, start, end);
    //    }
    //
    //    /**
    //     * 获取集合元素, 从小到大排序, 并且把score值也获取
    //     *
    //     * @param key
    //     * @param start
    //     * @param end
    //     * @return -
    //     */
    //    public Set<ZSetOperations.TypedTuple<String>> zRangeWithScores(String key, long start,
    //                                                                   long end) {
    //        return redisTemplate.opsForZSet().rangeWithScores(key, start, end);
    //    }

    /**
     * 获取集合的元素, 从大到小排序
     *
     * @param key
     * @param start
     * @param end   ["num1","num2","num3"]
     * @return -
     */
    public Set<String> zReverseRange(String key, long start, long end) {
        return redisTemplate.opsForZSet().reverseRange(key, start, end);
    }

    /**
     * 获取集合的元素, 从大到小排序, 并返回score值
     *
     * @param key
     * @param start
     * @param end   [{"score":100,"value":"num1"},{"score":99,"value":"num2"},{"score":98,"value":"num3"}]
     * @return -
     */
    public Set<ZSetOperations.TypedTuple<String>> zReverseRangeWithScores(String key,
                                                                          long start, long end) {
        return redisTemplate.opsForZSet().reverseRangeWithScores(key, start,
                end);
    }

    /**
     * list 右边插入
     *
     * @param key
     * @param value
     */
    public void rightPush(String key, String value) {
        redisTemplate.opsForList().rightPush(key, value);
    }

    public List<String> rangeValue(String key, long begin, long end) {
        return redisTemplate.opsForList().range(key, begin, end);
    }

    public Long setSetValue(String key, String value) {
        return redisTemplate.opsForSet().add(key, value);
    }

    public Long addSetValue(String key, String... value) {
        return redisTemplate.opsForSet().add(key, value);
    }

    public boolean setContainsValue(String key, String value) {
        return Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, value));
    }

    public Long remSetValueAndReturnCount(String key, String value) {
        Object count = redisTemplate.execute(remAndCountScript, Lists.newArrayList(key), value);
        if (count instanceof List) {
            return Long.valueOf(String.valueOf(((List<Object>) count).get(0)));
        }
        return Long.valueOf(String.valueOf(count));
    }

    public Long addSetValueAndReturnCount(String key, String value) {
        Object count = redisTemplate.execute(addAndCountScript, Lists.newArrayList(key), value);
        if (count instanceof List) {
            return Long.valueOf(String.valueOf(((List<Object>) count).get(0)));
        }
        return Long.valueOf(String.valueOf(count));
    }

    public Long getSetSize(String key) {
        return redisTemplate.opsForSet().size(key);
    }

    public void trimList(String key, long begin, long end) {
        redisTemplate.opsForList().trim(key, begin, end);
    }

    public Long zsetAdd(String key, String value) {
        return redisTemplate.opsForSet().add(key, value);
    }

    public void setBit(String key, long offset, boolean value) {
        redisTemplate.opsForValue().setBit(key, offset, value);
    }

    public boolean isSet(String key, long offset) {
        return Boolean.TRUE.equals(redisTemplate.opsForValue().getBit(key, offset));
    }

    public List<Long> getBit(String key, BitFieldSubCommands subCommands) {
        return redisTemplate.opsForValue().bitField(key, subCommands);
    }

    public boolean lockStopFlagExpire(String stopFlagKey, String value) {
        return lockExpire(stopFlagKey, value, 10, TimeUnit.MINUTES);
    }
}
