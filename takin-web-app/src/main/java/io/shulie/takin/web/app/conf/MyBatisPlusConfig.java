package io.shulie.takin.web.app.conf;

import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.baomidou.mybatisplus.extension.plugins.PaginationInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author shiyajian
 * create: 2020-09-24
 */

@Configuration
public class MyBatisPlusConfig {

    /**
     * 分页插件，自动识别数据库类型
     * 多租户，请参考官网【插件扩展】
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }

    @Bean
    public ConfigurationCustomizer mybatisConfigurationCustomizer() {
        return configuration -> configuration.addInterceptor(new com.github.pagehelper.PageInterceptor());
    }

    @Bean
    public MySqlInjector sqlInjector() {
        return new MySqlInjector();
    }
}

