package io.shulie.takin.web.data.dao.pressureresource;

import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceDetailEntity;
import io.shulie.takin.web.data.param.pressureresource.PressureResourceDetailQueryParam;

import java.util.List;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/8/30 6:54 PM
 */
public interface PressureResourceDetailDAO {
    /**
     * 按条件查询
     *
     * @param params
     * @return
     */
    List<PressureResourceDetailEntity> getList(PressureResourceDetailQueryParam params);

    /**
     * 新增
     *
     * @param insertList
     */
    void batchInsert(List<PressureResourceDetailEntity> insertList);

    void updateEntranceName(PressureResourceDetailEntity detailEntity);
}
