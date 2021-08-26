package io.shulie.takin.web.app.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.ConfigureRedisAction;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * @author 无涯
 * @date 2021/6/17 11:51 下午
 */

@Configuration
//设定为60秒session过期
@EnableRedisHttpSession(maxInactiveIntervalInSeconds=60)
public class RedisSessionConfig {
    /**
     *  解决redis集群环境没有开启Keyspace notifications导致的
     *
     *  Error creating bean with name 'enableRedisKeyspaceNotificationsInitializer' defined in class path resource
     *
     * */
    @Bean
    public static ConfigureRedisAction configureRedisAction() {
        return ConfigureRedisAction.NO_OP;
    }
}
