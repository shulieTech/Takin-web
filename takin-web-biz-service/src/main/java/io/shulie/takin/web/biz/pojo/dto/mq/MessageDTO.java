package io.shulie.takin.web.biz.pojo.dto.mq;

import lombok.Getter;
import lombok.Setter;

/**
 * 消息传输对象
 *
 * @author liuchuan
 * @date 2021/7/2 5:19 下午
 */
@Getter
@Setter
public class MessageDTO {

    /**
     * 主题
     */
    private String topic;

    /**
     * 消息内容
     */
    private Object message;

}
