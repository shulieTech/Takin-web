package io.shulie.takin.web.biz.service;

import java.util.List;

import io.shulie.takin.common.beans.component.SelectVO;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.agent.vo.ShadowConsumerVO;
import io.shulie.takin.web.biz.pojo.input.application.ShadowConsumerCreateInput;
import io.shulie.takin.web.biz.pojo.input.application.ShadowConsumerQueryInput;
import io.shulie.takin.web.biz.pojo.input.application.ShadowConsumerQueryInputV2;
import io.shulie.takin.web.biz.pojo.input.application.ShadowConsumerUpdateInput;
import io.shulie.takin.web.biz.pojo.input.application.ShadowConsumerUpdateUserInput;
import io.shulie.takin.web.biz.pojo.input.application.ShadowConsumersOperateInput;
import io.shulie.takin.web.biz.pojo.output.application.ShadowConsumerOutput;

/**
 * @author shiyajian
 * create: 2021-02-04
 */
public interface ShadowConsumerService {

    List<ShadowConsumerOutput> getShadowConsumersByApplicationId(long applicationId);

    ShadowConsumerOutput getMqConsumerById(Long id);

    /**
     * 是否已存在
     * @param request
     * @return
     */
    Boolean exist(ShadowConsumerQueryInput request);

    PagingList<ShadowConsumerOutput> pageMqConsumers(ShadowConsumerQueryInput request);

    void createMqConsumers(ShadowConsumerCreateInput request);

    void updateMqConsumers(ShadowConsumerUpdateInput request);

    /**
     * 导入更新mq
     * @param request
     */
    void importUpdateMqConsumers(ShadowConsumerUpdateInput request);

    void deleteMqConsumers(List<Long> id);

    void operateMqConsumers(ShadowConsumersOperateInput request);

    List<ShadowConsumerVO> agentSelect(String namespace);

    int allocationUser(ShadowConsumerUpdateUserInput request);

    List<SelectVO> queryMqSupportType();

    List<SelectVO> queryMqSupportProgramme(String engName);

    void updateMqConsumersV2(ShadowConsumerUpdateInput request);

    void createMqConsumersV2(ShadowConsumerCreateInput request,Boolean manualTag);

    PagingList<ShadowConsumerOutput> pageMqConsumersV2(ShadowConsumerQueryInputV2 request);
}
