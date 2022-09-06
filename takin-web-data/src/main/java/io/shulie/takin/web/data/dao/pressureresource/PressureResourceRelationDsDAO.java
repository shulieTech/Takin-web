package io.shulie.takin.web.data.dao.pressureresource;

import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceRelationDsEntity;
import io.shulie.takin.web.data.param.pressureresource.PressureResourceDsQueryParam;

import java.util.List;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/8/30 2:54 PM
 */
public interface PressureResourceRelationDsDAO {
    /**
     * 新增
     *
     * @param dsEntitys
     */
    void add(List<PressureResourceRelationDsEntity> dsEntitys);

    /**
     * 按条件查询列表页
     *
     * @param param
     * @return
     */
    List<PressureResourceRelationDsEntity> queryByParam(PressureResourceDsQueryParam param);
}
