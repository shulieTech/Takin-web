package io.shulie.takin.web.app.conf.filter;

import javax.servlet.Filter;
import javax.servlet.MultipartConfigElement;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

/**
 * 添加put请求过滤器
 *
 * @author vernon
 * @date 2019/12/2 15:34
 */
@Configuration
public class FilterConfiguration {

    @Bean
    public FilterRegistrationBean<Filter> pufFilter() {
        FilterRegistrationBean<Filter> registration = new FilterRegistrationBean<>();
        registration.addUrlPatterns("/*");
        registration.setFilter(new org.springframework.web.filter.HttpPutFormContentFilter());
        registration.setName("httpPutFormContentFilter");
        return registration;
    }

    @Bean
    public FilterRegistrationBean<LogTraceIdFilter> logTraceIdFilter() {
        FilterRegistrationBean<LogTraceIdFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new LogTraceIdFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(0);
        registrationBean.setName("logTraceIdFilter");
        return registrationBean;
    }
}
