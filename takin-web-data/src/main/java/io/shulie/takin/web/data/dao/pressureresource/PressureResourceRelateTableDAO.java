package io.shulie.takin.web.data.dao.pressureresource;

import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceRelateTableEntityV2;
import io.shulie.takin.web.data.model.mysql.pressureresource.RelateTableEntity;
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
    void add_V2(List<PressureResourceRelateTableEntityV2> dsEntitys);

    /**
     * 分页
     *
     * @param param
     * @return
     */
    PagingList<RelateTableEntity> pageList_v2(PressureResourceTableQueryParam param);

    /**
     * @param param
     * @return
     */
    List<RelateTableEntity> queryList_v2(PressureResourceTableQueryParam param);

    /**
     * 批量保存
     *
     * @param tableEntitys
     */
    void saveOrUpdate(List<PressureResourceRelateTableEntityV2> tableEntitys);

    void deleteByParam(PressureResourceTableQueryParam queryParam);
}
