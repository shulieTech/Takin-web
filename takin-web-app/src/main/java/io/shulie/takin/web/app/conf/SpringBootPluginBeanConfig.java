package io.shulie.takin.web.app.conf;

import java.util.List;
import java.util.Map;
import java.util.Set;

import io.shulie.takin.plugin.framework.core.PluginManager;
import io.shulie.takin.plugin.framework.core.configuration.IConfiguration;
import io.shulie.takin.plugin.framework.core.configuration.impl.DefaultConfiguration;
import io.shulie.takin.plugin.framework.extension.spring.AutoPluginManager;
import io.shulie.takin.plugin.framework.extension.spring.SpringBaseExtension;
import io.shulie.takin.plugin.framework.extension.spring.SpringBasicBeanExtension;
import io.shulie.takin.plugin.framework.extension.spring.SpringInitExtension;
import io.shulie.takin.plugin.framework.extension.spring.SpringLogbackExtension;
import io.shulie.takin.plugin.framework.extension.spring.SpringMybatisPlusExtension;
import io.shulie.takin.plugin.framework.extension.springboot.SpringAutoConfigExtension;
import io.shulie.takin.plugin.framework.extension.springboot.SpringBootInitExtension;
import io.shulie.takin.plugin.framework.extension.springmvc.SpringMvcExtension;
import org.pf4j.RuntimeMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author xiaobin.zfb|xiaobin@shulie.io
 * @since 2021/7/22 7:29 下午
 */
@Component
@ConfigurationProperties(prefix = "takin.plugin")
public class SpringBootPluginBeanConfig {

    /**
     * running mode
     * dev env: development、dev
     * <p>
     * prod env: deployment、prod
     */
    @Value("${runMode:prod}")
    private String runMode;

    /**
     * plugin path
     */
    @Value("${pluginPath:plugins}")
    private String pluginPath;

    /**
     * plugin config file path
     */
    @Value("${pluginConfigFilePath:pluginConfigs}")
    private String pluginConfigFilePath;

    /**
     * plugin config file path
     */
    @Value("${pluginPrefixPath:/}")
    private String pluginPrefixPath;

    /**
     * plugin config file path
     */
    @Value("${enablePluginPrefixPathPluginId:false}")
    private boolean enablePluginPrefixPathPluginId;

    /**
     * enable plugins
     */
    @Value("${enablePluginIds:}")
    private Set<String> enablePluginIds;

    /**
     * disable plugins
     */
    @Value("${disablePluginIds:}")
    private Set<String> disablePluginIds;

    /**
     * sort plugins
     */
    @Value("${sortPluginIds:ee_web_plugin_user}")
    private List<String> sortPluginIds;

    /**
     * sort plugins
     */
    @Value("${version:0.0.0}")
    private String version;

    /**
     * sort plugins
     */
    @Value("${exactVersionAllowed:false}")
    private boolean exactVersionAllowed;

    private Map<String, String> arguments;

    @Bean
    public IConfiguration configuration() {
        return new DefaultConfiguration.Builder()
            .withEnvironment(RuntimeMode.byName(runMode))
            .withPluginPath(pluginPath)
            .withPluginConfigFilePath(pluginConfigFilePath)
            .withPluginPrefixPath(pluginPrefixPath)
            .withArguments(arguments)
            .withEnablePluginIds(enablePluginIds)
            .withDisablePluginIds(disablePluginIds)
            .withSortPluginIds(sortPluginIds)
            .withExactVersionAllowed(exactVersionAllowed)
            .withEnablePluginPrefixPathPluginId(enablePluginPrefixPathPluginId)
            .withVersion(version)
            .build();
    }


    @Bean
    @Order(Ordered.LOWEST_PRECEDENCE)
    public PluginManager pluginManager(@Autowired IConfiguration configuration) {
        PluginManager pluginManager = new AutoPluginManager(configuration);
        pluginManager.addApplicationExtension(new SpringInitExtension());
        pluginManager.addApplicationExtension(new SpringBootInitExtension());
        pluginManager.addApplicationExtension(new SpringLogbackExtension());
        pluginManager.addApplicationExtension(new SpringBaseExtension());
        pluginManager.addApplicationExtension(new SpringBasicBeanExtension());
        pluginManager.addApplicationExtension(new SpringAutoConfigExtension());
        pluginManager.addApplicationExtension(new SpringMybatisPlusExtension());
        pluginManager.addApplicationExtension(new SpringMvcExtension());
        //pluginManager.addApplicationExtension(new SwaggerExtension());
        return pluginManager;
    }


}