package io.shulie.takin.web.biz.service.pressureresource;

import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.pojo.request.pressureresource.PressureResourceRelationDsInput;
import io.shulie.takin.web.biz.pojo.request.pressureresource.PressureResourceRelationDsRequest;
import io.shulie.takin.web.biz.service.pressureresource.vo.PressureResourceRelationDsVO;

/**
 * 压测资源配置-数据源
 *
 * @author xingchen
 * @description: TODO
 * @date 2022/8/30 2:53 PM
 */
public interface PressureResourceDsService {
    /**
     * 数据源新增
     */
    void add(PressureResourceRelationDsInput input);

    /**
     * 数据源视图页面
     */
    PagingList<PressureResourceRelationDsVO> listByDs(PressureResourceRelationDsRequest request);

    /**
     * 应用视图页面
     */
    PagingList<PressureResourceRelationDsVO> listByApp(PressureResourceRelationDsRequest request);
}
