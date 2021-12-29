package io.shulie.takin.web.app.conf.redis;

import java.time.Duration;
import java.util.Arrays;
import java.util.Map;

import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.support.spring.FastJsonRedisSerializer;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONUtil;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.shulie.takin.web.common.constant.CacheConstants;
import io.shulie.takin.web.ext.util.WebPluginUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.PatternTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig implements CacheConstants {

    /**
     * redis 消息监听者
     * key 通道名称, value 监听者实现
     */
    @Autowired(required = false)
    private Map<String, MessageListener> messageListenerMap;

    @Bean(CACHE_KEY_GENERATOR_BY_TENANT_INFO)
    public KeyGenerator cacheKeyGeneratorByTenantInfo() {
        return (target, method, params) -> target.getClass().getName()
            + method.getName()
            + ":"
            + Arrays.toString(DigestUtil.md5(
            JSONUtil.toJsonStr(params) + WebPluginUtils.traceEnvCode() + WebPluginUtils.traceTenantId()));
    }

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        // 自定义 redisCache 管理，指定key，带有过期时间，10分钟
        // @Cacheable 使用
        return new RedisCacheManager(
            RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory),
            this.getRedisCacheConfigurationWithTtl(600),
            CACHE_KEY_AGENT_CONFIG, CACHE_KEY_AGENT_APPLICATION_NODE
        );
    }

    @Bean("userRedisTemplate")
    public RedisTemplate<String, Object> userRedisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(factory);
        // 为了解决不同工程，序列化问题
        FastJsonRedisSerializer valueSerializer = new FastJsonRedisSerializer<>(Object.class);
        // 建议使用这种方式，小范围指定白名单
        ParserConfig.getGlobalInstance().addAccept("io.shulie.");
        // 设置值（value）的序列化采用FastJsonRedisSerializer
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        // key采用String的序列化方式
        template.setKeySerializer(stringRedisSerializer);
        // hash的key也采用String的序列化方式
        template.setHashKeySerializer(stringRedisSerializer);
        // value序列化方式采用jackson
        template.setValueSerializer(valueSerializer);
        // hash的value序列化方式采用jackson
        template.setHashValueSerializer(valueSerializer);
        template.afterPropertiesSet();
        return template;
    }

    @Bean("redisTemplate")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(factory);
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer
            = getJackson2JsonRedisSerializer();
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        // key采用String的序列化方式
        template.setKeySerializer(stringRedisSerializer);
        // hash的key也采用String的序列化方式
        template.setHashKeySerializer(stringRedisSerializer);
        // value序列化方式采用jackson
        template.setValueSerializer(jackson2JsonRedisSerializer);
        // hash的value序列化方式采用jackson
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        template.afterPropertiesSet();
        return template;
    }

    /**
     * redis 通道监听
     *
     * @param factory redis 连接工厂
     * @return Redis 消息侦听器提供异步行为的容器
     */
    @Bean("redisMessageListener")
    public RedisMessageListenerContainer container(RedisConnectionFactory factory) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(factory);

        if (CollectionUtil.isEmpty(messageListenerMap)) {
            return container;
        }

        // sessionRepository -> {RedisOperationsSessionRepository@15032}
        // 多了一个, 这边没用到, 所以排除掉
        messageListenerMap.forEach((k, v) -> {
            if ("sessionRepository".equals(k)) {
                return;
            }
            container.addMessageListener(v, new PatternTopic(k));
        });

        return container;
    }

    /**
     * cacheManage 缓存时间
     *
     * @param seconds 秒
     * @return cacheConfig
     */
    private RedisCacheConfiguration getRedisCacheConfigurationWithTtl(Integer seconds) {
        RedisCacheConfiguration redisCacheConfiguration = RedisCacheConfiguration.defaultCacheConfig();
        redisCacheConfiguration = redisCacheConfiguration.serializeValuesWith(
            RedisSerializationContext
                .SerializationPair
                .fromSerializer(this.getJackson2JsonRedisSerializer())
        ).entryTtl(Duration.ofSeconds(seconds));
        return redisCacheConfiguration;
    }

    private Jackson2JsonRedisSerializer<Object> getJackson2JsonRedisSerializer() {
        Jackson2JsonRedisSerializer<Object> jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer<>(
            Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        om.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        return jackson2JsonRedisSerializer;
    }
}
