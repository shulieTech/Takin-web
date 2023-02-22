package io.shulie.takin.web.app.conf;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

/**
 * @author mubai
 * @date 2021-01-04 17:38
 */
@Configuration
@Slf4j
public class CommandChannelConfig {

//    @Value("${takin.config.zk.addr}")
//    private String zkPath;

//    @Bean
//    public ServerChannel registerChannel() {
//        ServerChannel channel = null;
//        try {
//            ZkClientConfig config = new ZkClientConfig();
//            config.setZkServers(zkPath);
//            channel = new DefaultServerChannel()
//                .build(config)
//                .setChannelProtocol(new JsonChannelProtocol());
//        } catch (Exception e) {
//            log.error(e.getMessage());
//        }
//        return channel;
//    }

}
