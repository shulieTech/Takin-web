package io.shulie.takin.web.biz.mq.consumer;

import io.shulie.takin.web.biz.pojo.dto.mq.MessageDTO;

/**
 * 消息: 消费者
 *
 * 实现: redis...
 *
 * @author liuchuan
 * @date 2021/7/2 5:27 下午
 */
public interface Consumer {

    /**
     * 消费
     *
     * @param messageDTO 消息传输对象
     */
    void consume(MessageDTO messageDTO);

}
