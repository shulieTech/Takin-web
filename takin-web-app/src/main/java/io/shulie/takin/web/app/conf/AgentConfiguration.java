package io.shulie.takin.web.app.conf;

import java.util.HashMap;
import java.util.Map;

import io.shulie.takin.web.common.agent.IAgentZipResolver;
import io.shulie.takin.web.common.agent.impl.AgentZipResolver;
import io.shulie.takin.web.common.agent.impl.MiddlewareZipResolver;
import io.shulie.takin.web.common.agent.impl.SimulatorZipResolver;
import io.shulie.takin.web.common.enums.agentupgradeonline.PluginTypeEnum;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Description agent配置类
 * @Author ocean_wll
 * @Date 2021/11/10 8:53 下午
 */
@Configuration
public class AgentConfiguration {

    @Bean
    public Map<PluginTypeEnum, IAgentZipResolver> agentZipResolverMap() {
        Map<PluginTypeEnum, IAgentZipResolver> agentZipResolverMap = new HashMap<>();
        agentZipResolverMap.put(PluginTypeEnum.AGENT, new AgentZipResolver());
        agentZipResolverMap.put(PluginTypeEnum.SIMULATOR, new SimulatorZipResolver());
        agentZipResolverMap.put(PluginTypeEnum.MIDDLEWARE, new MiddlewareZipResolver());
        return agentZipResolverMap;
    }
}
