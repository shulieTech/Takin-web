package io.shulie.takin.web.entrypoint.controller.health;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

//自定义Mvc配置
@Component
public class SampleWebMvcConfigurer implements WebMvcConfigurer {
    @Autowired
    private SampleMvcInterceptor sampleMvcInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(sampleMvcInterceptor);
    }
}
