package io.shulie.takin.web.biz.mq.producer;

import io.shulie.takin.web.biz.pojo.dto.mq.MessageDTO;

/**
 * 消息: 生产者
 *
 * 实现有 redis, ...
 *
 * @author liuchuan
 * @date 2021/7/2 5:18 下午
 */
public interface Producer {

    /**
     * 生产
     *
     * @param messageDTO 消息传输对象
     */
    void produce(MessageDTO messageDTO);

}
