package io.shulie.takin.web.app.conf;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.SentinelServersConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties.Sentinel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;

/**
 * 分布式锁 redisson config
 *
 * @author liuchuan
 * @date 2021/6/2 12:12 下午
 */
@Configuration
public class RedissonConfig {

    @Autowired
    private RedisProperties redisProperties;

    @Bean(destroyMethod = "shutdown")
    public RedissonClient redisson() {
        Config config = new Config();

        Sentinel sentinel = redisProperties.getSentinel();
        if (sentinel != null && !CollectionUtils.isEmpty(sentinel.getNodes())) {
            SentinelServersConfig sentinelServersConfig = config.useSentinelServers()
                .setTimeout(10000)
                .setDatabase(redisProperties.getDatabase())
                .setPassword(redisProperties.getPassword())
                .setMasterName(redisProperties.getSentinel().getMaster());
            for (String node : sentinel.getNodes()) {
                sentinelServersConfig.addSentinelAddress(String.format("redis://%s", node));
            }

            sentinelServersConfig.setCheckSentinelsList(false);
            return Redisson.create(config);
        }

        config.useSingleServer()
            .setAddress(String.format("redis://%s:%s", redisProperties.getHost(), redisProperties.getPort()))
            .setDatabase(redisProperties.getDatabase())
            .setPassword(redisProperties.getPassword());
        return Redisson.create(config);
    }

}
