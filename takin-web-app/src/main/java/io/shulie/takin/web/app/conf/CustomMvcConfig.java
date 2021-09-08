package io.shulie.takin.web.app.conf;

import io.shulie.takin.web.app.conf.intercepter.AbstractInterceptor;
import io.shulie.takin.web.app.factory.TrimmedAnnotationFormatterFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author shulie
 * @date 2019/9/20 15:14
 */
@Configuration
public class CustomMvcConfig implements WebMvcConfigurer {
    @Autowired
    private AbstractInterceptor abstractInterceptor;

    @Autowired
    private TrimmedAnnotationFormatterFactory trimmedAnnotationFormatterFactory;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        WebMvcConfigurer.super.addResourceHandlers(registry);
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addFormatterForFieldAnnotation(trimmedAnnotationFormatterFactory);
        WebMvcConfigurer.super.addFormatters(registry);
    }

    /**
     * 跨域支持
     */
    private CorsConfiguration corsConfig() {
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        corsConfiguration.addAllowedOrigin("*");
        corsConfiguration.addAllowedHeader("*");
        corsConfiguration.addAllowedMethod("*");
        corsConfiguration.setAllowCredentials(true);
        corsConfiguration.setMaxAge(3600 * 24L);
        return corsConfiguration;
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig());
        return new CorsFilter(source);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(abstractInterceptor).addPathPatterns("/**");
    }

}
