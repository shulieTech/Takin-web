package io.shulie.takin.web.biz.service;

import io.shulie.takin.common.beans.component.SelectVO;
import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.agent.vo.ShadowConsumerVO;
import io.shulie.takin.web.biz.pojo.input.application.ShadowConsumerCreateInput;
import io.shulie.takin.web.biz.pojo.input.application.ShadowConsumerQueryInput;
import io.shulie.takin.web.biz.pojo.input.application.ShadowConsumerUpdateInput;
import io.shulie.takin.web.biz.pojo.input.application.ShadowConsumerUpdateUserInput;
import io.shulie.takin.web.biz.pojo.input.application.ShadowConsumersOperateInput;
import io.shulie.takin.web.biz.pojo.output.application.ShadowConsumerOutput;

import java.util.List;

/**
 * @author shiyajian
 * create: 2021-02-04
 */
public interface ShadowConsumerService {

    List<ShadowConsumerOutput> getShadowConsumersByApplicationId(long applicationId);

    ShadowConsumerOutput getMqConsumerById(Long id);

    PagingList<ShadowConsumerOutput> pageMqConsumers(ShadowConsumerQueryInput request);

    void createMqConsumers(ShadowConsumerCreateInput request);

    void updateMqConsumers(ShadowConsumerUpdateInput request);

    void deleteMqConsumers(List<Long> id);

    void operateMqConsumers(ShadowConsumersOperateInput request);

    List<ShadowConsumerVO> agentSelect(String namespace);

    int allocationUser(ShadowConsumerUpdateUserInput request);

    List<SelectVO> queryMqSupportType();

    List<SelectVO> queryMqSupportProgramme(String engName);

}
