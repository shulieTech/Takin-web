package io.shulie.takin.web.data.dao.pressureresource;

import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceRelateTableEntity;
import io.shulie.takin.web.data.param.pressureresource.PressureResourceTableQueryParam;

import java.util.List;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/8/30 2:54 PM
 */
public interface PressureResourceRelateTableDAO {
    /**
     * 新增
     *
     * @param dsEntitys
     */
    void add(List<PressureResourceRelateTableEntity> dsEntitys);

    /**
     * 分页
     *
     * @param param
     * @return
     */
    PagingList<PressureResourceRelateTableEntity> pageList(PressureResourceTableQueryParam param);

    /**
     * @param param
     * @return
     */
    List<PressureResourceRelateTableEntity> queryList(PressureResourceTableQueryParam param);

    /**
     * 批量保存
     *
     * @param tableEntitys
     */
    void saveOrUpdate(List<PressureResourceRelateTableEntity> tableEntitys);
}
