package io.shulie.takin.web.biz.service.pressureresource;

import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.pojo.request.pressureresource.PressureResourceMockInput;
import io.shulie.takin.web.biz.pojo.request.pressureresource.PressureResourceRelateRemoteCallRequest;
import io.shulie.takin.web.biz.service.pressureresource.vo.PressureResourceRelateRemoteCallVO;

/**
 * 压测资源配置-远程调用
 *
 * @author xingchen
 * @description: TODO
 * @date 2022/8/30 2:53 PM
 */
public interface PressureResourceRemoteCallService {
    /**
     * 分页
     *
     * @param request
     * @return
     */
    PagingList<PressureResourceRelateRemoteCallVO> pageList(PressureResourceRelateRemoteCallRequest request);

    /**
     * 修改
     *
     * @param mockInput
     */
    void update(PressureResourceMockInput mockInput);

    /**
     * 获取服务响应时间
     *
     * @param id
     * @return
     */
    Integer getServiceAvgRt(Long id);
}
