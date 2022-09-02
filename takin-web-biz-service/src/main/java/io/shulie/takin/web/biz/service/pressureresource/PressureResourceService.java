package io.shulie.takin.web.biz.service.pressureresource;

import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.pojo.request.pressureresource.PressureResourceInput;
import io.shulie.takin.web.biz.pojo.request.pressureresource.PressureResourceIsolateInput;
import io.shulie.takin.web.biz.pojo.request.pressureresource.PressureResourceQueryRequest;
import io.shulie.takin.web.biz.service.pressureresource.vo.PressureResourceDetailVO;
import io.shulie.takin.web.biz.service.pressureresource.vo.PressureResourceInfoVO;
import io.shulie.takin.web.biz.service.pressureresource.vo.PressureResourceVO;

import java.util.List;

/**
 * 压测资源配置
 *
 * @author xingchen
 * @description: TODO
 * @date 2022/8/30 2:53 PM
 */
public interface PressureResourceService {
    /**
     * 新增
     *
     * @param input
     */
    void add(PressureResourceInput input);

    /**
     * 修改
     *
     * @param input
     */
    void update(PressureResourceInput input);

    /**
     * 分页
     *
     * @param request
     * @return
     */
    PagingList<PressureResourceVO> list(PressureResourceQueryRequest request);

    /**
     * 详情查询
     *
     * @param request
     * @return
     */
    PressureResourceInfoVO detail(PressureResourceQueryRequest request);

    /**
     * 修改数据隔离方式
     *
     * @param isolateInput
     */
    void updateIsolate(PressureResourceIsolateInput isolateInput);
}
