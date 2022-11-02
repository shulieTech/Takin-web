package io.shulie.takin.web.app.conf.redis;

import org.apache.commons.lang3.StringUtils;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.config.SentinelServersConfig;
import org.redisson.config.SingleServerConfig;
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

        //哨兵
        Sentinel sentinel = redisProperties.getSentinel();
        if (sentinel != null && !CollectionUtils.isEmpty(sentinel.getNodes())) {
            SentinelServersConfig sentinelServersConfig = config.useSentinelServers()
                    .setTimeout(10000)
                    .setDatabase(redisProperties.getDatabase())
                    .setMasterName(redisProperties.getSentinel().getMaster());
            if (StringUtils.isNotBlank(redisProperties.getPassword())){
                sentinelServersConfig.setPassword(redisProperties.getPassword());
            }
            for (String node : sentinel.getNodes()) {
                sentinelServersConfig.addSentinelAddress(String.format("redis://%s", node));
            }

            sentinelServersConfig.setCheckSentinelsList(false);
            return Redisson.create(config);
        }

        //集群
        RedisProperties.Cluster cluster = redisProperties.getCluster();
        if (cluster != null && !CollectionUtils.isEmpty(cluster.getNodes())) {
            ClusterServersConfig clusterServersConfig = config.useClusterServers();
            for (String node : cluster.getNodes()) {
                clusterServersConfig.addNodeAddress(String.format("redis://%s", node));
            }
            if (StringUtils.isNotBlank(redisProperties.getPassword())) {
                clusterServersConfig.setPassword(redisProperties.getPassword());
            }
            return Redisson.create(config);
        }

        //单机
        SingleServerConfig singleServerConfig = config.useSingleServer()
                .setAddress(String.format("redis://%s:%s", redisProperties.getHost(), redisProperties.getPort()))
                .setDatabase(redisProperties.getDatabase());
        if (StringUtils.isNotBlank(redisProperties.getPassword())){
            singleServerConfig.setPassword(redisProperties.getPassword());
        }
        return Redisson.create(config);
    }
}
