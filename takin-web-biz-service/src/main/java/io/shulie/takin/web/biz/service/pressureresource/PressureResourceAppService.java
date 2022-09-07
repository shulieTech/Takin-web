package io.shulie.takin.web.biz.service.pressureresource;

import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.pojo.request.pressureresource.PressureResourceAppInput;
import io.shulie.takin.web.biz.pojo.request.pressureresource.PressureResourceAppRequest;
import io.shulie.takin.web.biz.service.pressureresource.vo.PressureResourceRelateAppVO;

/**
 * 压测资源配置
 *
 * @author xingchen
 * @description: TODO
 * @date 2022/8/30 2:53 PM
 */
public interface PressureResourceAppService {
    /**
     * 应用检查列表查询
     *
     * @param request
     * @return
     */
    PagingList<PressureResourceRelateAppVO> appCheckList(PressureResourceAppRequest request);

    /**
     * 修改
     */
    void update(PressureResourceAppInput input);
}
