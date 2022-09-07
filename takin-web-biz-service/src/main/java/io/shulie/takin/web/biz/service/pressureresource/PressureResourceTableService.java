package io.shulie.takin.web.biz.service.pressureresource;

import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.biz.pojo.request.pressureresource.PressureResourceRelateTableInput;
import io.shulie.takin.web.biz.pojo.request.pressureresource.PressureResourceRelateTableRequest;
import io.shulie.takin.web.biz.service.pressureresource.vo.PressureResourceRelationTableVO;

/**
 * 压测资源配置-数据源
 *
 * @author xingchen
 * @description: TODO
 * @date 2022/8/30 2:53 PM
 */
public interface PressureResourceTableService {
    /**
     * 数据源新增
     */
    void save(PressureResourceRelateTableInput input);

    /**
     * 更新
     *
     * @param updateInput
     */
    void update(PressureResourceRelateTableInput updateInput);

    /**
     * 删除
     *
     * @param id
     */
    void delete(Long id);

    /**
     * 分页
     *
     * @param request
     * @return
     */
    PagingList<PressureResourceRelationTableVO> pageList(PressureResourceRelateTableRequest request);
}
