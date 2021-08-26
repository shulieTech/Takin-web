package io.shulie.takin.web.common.constant;

/**
 * mq 常量池
 *
 * @author liuchuan
 * @date 2021/4/27 10:44 上午
 */
public interface MqConstants {

    /**
     * 应用中间件上报 mq topic
     */
    String MQ_REDIS_PUSH_APPLICATION_MIDDLEWARE = "mq-redis-agentPushMiddlewareAndCompareConsumer";

}
