package io.shulie.takin.web.app.conf.filter;

import javax.servlet.Filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 添加put请求过滤器
 * @author  vernon
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
