package io.shulie.takin.web.data.dao.pressureresource;

import io.shulie.takin.common.beans.page.PagingList;
import io.shulie.takin.web.data.model.mysql.pressureresource.PressureResourceRelateAppEntity;
import io.shulie.takin.web.data.param.pressureresource.PressureResourceAppQueryParam;

import java.util.List;

/**
 * @author xingchen
 * @description: TODO
 * @date 2022/8/30 2:54 PM
 */
public interface PressureResourceRelateAppDAO {
    /**
     * 分页
     *
     * @param param
     * @return
     */
    PagingList<PressureResourceRelateAppEntity> pageList(PressureResourceAppQueryParam param);

    /**
     * 内部查询
     *
     * @param param
     * @return
     */
    List<PressureResourceRelateAppEntity> queryList(PressureResourceAppQueryParam param);

    /**
     * 存在则更新
     */
    void saveOrUpdate(List<PressureResourceRelateAppEntity> list);

}
