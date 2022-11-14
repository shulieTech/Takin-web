package io.shulie.takin.web.biz.service.pressureresource;

import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.pojo.request.pressureresource.PressureResourceMqConsumerCreateInput;
import io.shulie.takin.web.biz.pojo.request.pressureresource.PressureResourceMqConsumerQueryRequest;
import io.shulie.takin.web.biz.service.pressureresource.vo.PressureResourceMqComsumerVO;

/**
 * 压测资源配置
 *
 * @author xingchen
 * @description: TODO
 * @date 2022/8/30 2:53 PM
 */
public interface PressureResourceMqConsumerService {
    /**
     * 分页
     *
     * @param request
     * @return
     */
    PagingList<PressureResourceMqComsumerVO> list(PressureResourceMqConsumerQueryRequest request);

    /**
     * 创建影子消费者
     *
     * @param input
     */
    void create(PressureResourceMqConsumerCreateInput input);

    /**
     * 创建影子消费者
     *
     * @param input
     */
    void update(PressureResourceMqConsumerCreateInput input);

    /**
     * 创建影子消费者
     *
     * @param input
     */
    void processConsumerTag(PressureResourceMqConsumerCreateInput input);

    /**
     * 删除
     *
     * @param id
     */
    void delete(Long id);
}
