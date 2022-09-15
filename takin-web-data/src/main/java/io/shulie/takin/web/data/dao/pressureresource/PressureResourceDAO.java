package io.shulie.takin.web.data.dao.pressureresource;

import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceEntity;
import io.shulie.takin.web.data.param.pressureresource.PressureResourceQueryParam;

import java.util.List;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/8/30 2:54 PM
 */
public interface PressureResourceDAO {
    /**
     * 新增
     *
     * @param insertEntity
     * @return
     */
    Long add(PressureResourceEntity insertEntity);

    /**
     * 列表分页查询
     *
     * @param param
     * @return
     */
    PagingList<PressureResourceEntity> pageList(PressureResourceQueryParam param);

    /**
     * 按名称查询
     *
     * @param name
     * @return
     */
    PressureResourceEntity queryByName(String name);

    /**
     * 获取所有
     *
     * @return
     */
    List<PressureResourceEntity> getAll();


    void delete(Long sourceId);
}
