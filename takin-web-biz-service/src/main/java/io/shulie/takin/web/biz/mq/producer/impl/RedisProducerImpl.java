package io.shulie.takin.web.biz.mq.producer.impl;

import io.shulie.takin.web.biz.mq.producer.Producer;
import io.shulie.takin.web.biz.pojo.dto.mq.MessageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * redis 生产者实现
 *
 * @author liuchuan
 * @date 2021/7/2 5:21 下午
 */
@Service("redisProducer")
public class RedisProducerImpl implements Producer {

    @Autowired
    @Qualifier("redisTemplate")
    private RedisTemplate redis;

    @Async
    @Override
    public void produce(MessageDTO messageDTO) {
        // 向某个通道 参数1 推送一条消息 参数2
        redis.convertAndSend(messageDTO.getTopic(), messageDTO.getMessage());
    }

}
