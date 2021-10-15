package io.shulie.takin.web.app.conf;

import io.shulie.takin.channel.ServerChannel;
import io.shulie.takin.channel.protocal.JsonChannelProtocol;
import io.shulie.takin.channel.router.zk.DefaultServerChannel;
import io.shulie.takin.channel.router.zk.ZkClientConfig;
import io.shulie.takin.web.common.util.ConfigServerHelper;
import io.shulie.takin.web.common.enums.config.ConfigServerKeyEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author mubai
 * @date 2021-01-04 17:38
 */

@Configuration
@Slf4j
public class CommandChannelConfig {


    @Bean
    public ServerChannel registerChannel() {
        ServerChannel channel = null;
        try {
            ZkClientConfig config = new ZkClientConfig();
            config.setZkServers(ConfigServerHelper.getValueByKey(ConfigServerKeyEnum.TAKIN_CONFIG_ZOOKEEPER_ADDRESS));
            channel = new DefaultServerChannel()
                .build(config)
                .setChannelProtocol(new JsonChannelProtocol());
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return channel;
    }

}
