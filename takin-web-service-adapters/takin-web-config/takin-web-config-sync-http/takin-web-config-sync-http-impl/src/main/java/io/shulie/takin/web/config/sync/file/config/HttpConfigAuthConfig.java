package io.shulie.takin.web.config.sync.file.config;

import io.shulie.takin.web.config.sync.http.constants.HttpConfigConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 *
 */
@Configuration
public class HttpConfigAuthConfig implements WebMvcConfigurer {

    @Autowired
    private HttpConfigAuthInterceptor httpConfigAuthInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(httpConfigAuthInterceptor)
            .addPathPatterns(HttpConfigConstants.HTTP_BASE_PATH + "/**");
    }
}
