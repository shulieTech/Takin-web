package io.shulie.takin.web.app;

import io.shulie.takin.web.biz.mq.producer.Producer;
import io.shulie.takin.web.biz.pojo.dto.mq.MessageDTO;
import io.shulie.takin.web.common.constant.MqConstants;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author liuchuan
 * @date 2021/12/17 9:51 上午
 */
@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = Application.class)
public class MqTest {

    @Autowired
    private Producer producer;

    @Test
    public void testProduce() {
        MessageDTO messageDTO = new MessageDTO();
        messageDTO.setMessage(1111);
        messageDTO.setTopic(MqConstants.MQ_REDIS_PUSH_APPLICATION_MIDDLEWARE);
        producer.produce(messageDTO);
    }


}
