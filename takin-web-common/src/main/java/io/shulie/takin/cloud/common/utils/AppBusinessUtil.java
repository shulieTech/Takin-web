package io.shulie.takin.cloud.common.utils;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * 项目工具类 配合 springboot
 *
 * @author liuchuan
 * @date 2021/4/5 5:10 下午
 */
@Component
public class AppBusinessUtil implements ApplicationContextAware {

    @Value("${spring.profiles.active}")
    private String profiles;

    /**
     * springboot 上下文
     */
    public static ApplicationContext ac;

    /**
     * 启动环境
     */
    public static String env;

    /**
     * 是否是 local 环境
     *
     * @return 是否是
     */
    public static boolean isLocal() {
        return "local".equals(env);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ac = applicationContext;
        env = profiles;
    }

}
