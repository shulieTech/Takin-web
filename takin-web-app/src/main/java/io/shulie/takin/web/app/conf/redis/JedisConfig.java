package io.shulie.takin.web.app.conf.redis;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/10/13 10:39 PM
 */
@Configuration
public class JedisConfig {
    @Autowired
    private RedisProperties redisProperties;

    @Bean
    public JedisPool jedisPool() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxTotal(redisProperties.getJedis().getPool().getMaxActive());
        jedisPoolConfig.setMinIdle(redisProperties.getJedis().getPool().getMinIdle());
        jedisPoolConfig.setMaxWaitMillis(redisProperties.getJedis().getPool().getMaxWait().toMillis());
        JedisPool jedisPool = new JedisPool(
                jedisPoolConfig,
                redisProperties.getHost(),
                redisProperties.getPort(),
                10000,
                redisProperties.getPassword());
        return jedisPool;
    }
}
