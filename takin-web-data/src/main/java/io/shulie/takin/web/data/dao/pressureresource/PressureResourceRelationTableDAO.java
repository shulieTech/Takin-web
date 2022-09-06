package io.shulie.takin.web.data.dao.pressureresource;

import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceRelationTableEntity;
import io.shulie.takin.web.data.param.pressureresource.PressureResourceTableQueryParam;

import java.util.List;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/8/30 2:54 PM
 */
public interface PressureResourceRelationTableDAO {
    /**
     * 新增
     *
     * @param dsEntitys
     */
    void add(List<PressureResourceRelationTableEntity> dsEntitys);

    /**
     * 分页
     *
     * @param param
     * @return
     */
    PagingList<PressureResourceRelationTableEntity> pageList(PressureResourceTableQueryParam param);

    /**
     * @param param
     * @return
     */
    List<PressureResourceRelationTableEntity> queryList(PressureResourceTableQueryParam param);

    /**
     * 批量保存
     *
     * @param tableEntitys
     */
    void saveOrUpdate(List<PressureResourceRelationTableEntity> tableEntitys);
}
