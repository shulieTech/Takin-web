package io.shulie.takin.web.app.conf;

import io.shulie.takin.channel.ServerChannel;
import io.shulie.takin.channel.protocal.JsonChannelProtocol;
import io.shulie.takin.channel.router.zk.DefaultServerChannel;
import io.shulie.takin.channel.router.zk.ZkClientConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author mubai
 * @date 2021-01-04 17:38
 */

@Configuration
@Slf4j
public class CommandChannelConfig {

    @Value("${takin.config.zk.addr:}")
    private String zkPath;

    @Bean
    public ServerChannel registerChannel() {
        ServerChannel channel = null;
        try {
            ZkClientConfig config = new ZkClientConfig();
            config.setZkServers(zkPath);
            channel = new DefaultServerChannel()
                .build(config)
                .setChannelProtocol(new JsonChannelProtocol());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return channel;
    }

}
